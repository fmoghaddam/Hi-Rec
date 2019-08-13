package run;

import com.google.common.eventbus.Subscribe;
import controller.DataSplitter;
import controller.similarity.SimilarityRepository;
import gui.messages.*;
import gui.model.FoldStatus;
import interfaces.*;
import model.*;
import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import util.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * This is the main executor of all the algorithms. This class read the config
 * file and then run configuration one by one on all the available CPUs
 * 
 * @author FBM
 *
 */
public final class ParallelEvaluator {

	private static final Logger LOG = Logger.getLogger(ParallelEvaluator.class.getCanonicalName());
	private final DataModel dataModel;
	private final DataSplitter dataSpliter;
	private final Map<Configuration, Map<Metric, List<Float>>> tTestValues = new LinkedHashMap<>();
	private Object LOCK = new Object();

	private ExecutorService algorithmExecutor;
	private List<ExecutorService> foldExecutors = new ArrayList<>();
	
	public ParallelEvaluator(final DataModel data) {
		this.dataModel = data;
		this.dataSpliter = new DataSplitter(this.dataModel.getCopy());
		this.dataSpliter.shuffle();
		MessageBus.getInstance().register(this);
	}
	
	@Subscribe
	private void stopRequestReceived(final StopAllRequestMessage message){
		foldExecutors.forEach(p->p.shutdownNow());
		if (algorithmExecutor != null) {
			algorithmExecutor.shutdownNow();
		}
        MessageBus.getInstance().getBus().post(new ShutdownFinishedMessage());
	}

	/**
	 * Reads all the {@link Configuration} form config file
	 * 
	 * @return List of all {@link Configuration}
	 */
	private List<Configuration> readConfigurations() {
		final int numberOfConfiguration = Config.getInt("NUMBER_OF_CONFIGURATION", 0);
		final List<Configuration> configurations = new ArrayList<>();
		if (numberOfConfiguration <= 0) {
			throw new IllegalArgumentException("Number of configuration in config file is " + numberOfConfiguration);
		}
		LOG.info(numberOfConfiguration + " configurations detected.");
		AbstractRecommender algorithm = null;
		boolean useTag;
		boolean useRating;
		boolean useLowLevel;
		boolean useGenre;
		for (int i = 1; i <= numberOfConfiguration; i++) {
			final String algorithmName = Config.getString("ALGORITHM_" + i + "_NAME", "");
			try {
				algorithm = (AbstractRecommender) ClassInstantiator.instantiateClass(algorithmName);
				ClassInstantiator.setParametersDynamically(algorithm, i);
				LOG.info("Algorithm in package " + algorithmName + " created.");
			} catch (final Exception e) {
				LOG.error("Can not load algorithm " + algorithmName, e);
				System.exit(1);
			}
			useLowLevel = Config.getBoolean("ALGORITHM_" + i + "_USE_LOW_LEVEL", false);
			useRating = Config.getBoolean("ALGORITHM_" + i + "_USE_RATING", false);
			useTag = Config.getBoolean("ALGORITHM_" + i + "_USE_TAG", false);
			useGenre = Config.getBoolean("ALGORITHM_" + i + "_USE_GENRE", false);
			configurations.add(new Configuration(i, algorithm, useLowLevel, useGenre, useTag, useRating));
			MessageBus.getInstance().getBus().post(
					new AlgorithmLevelUpdateMessage(i, algorithm.getClass().getSimpleName(), Globals.NUMBER_OF_FOLDS));
		}
		return configurations;
	}

	/**
	 * Iterate over all the {@link Configuration}s and run them one by one
	 */
	public List<Future<ConfigRunResult>> evaluate() {
		List<Future<ConfigRunResult>> runFutureList = new ArrayList<>();
		final List<Configuration> configurations = readConfigurations();
		if(Globals.RUN_ALGORITHMS_PARALLEL){
            if (Globals.RUN_ALGORITHMS_NUMBER_OF_THREAD == -1) {
			algorithmExecutor= Executors
					.newFixedThreadPool(Runtime.getRuntime().availableProcessors() > configurations.size()
							? configurations.size() : Runtime.getRuntime().availableProcessors());
			}else{
				algorithmExecutor = Executors
						.newFixedThreadPool(Globals.RUN_ALGORITHMS_NUMBER_OF_THREAD);
			}
		}else{
			algorithmExecutor= Executors
					.newFixedThreadPool(1);
		}
		final List<Callable<ConfigRunResult>> tasks = new ArrayList<>();
		try {
			for (Configuration configuration : configurations) {

				final Callable<ConfigRunResult> task = () -> {
					LOG.info("This process may take long time. Still running please wait....");
					LOG.info(configuration + "...");
					return execute(configuration);
				};
				tasks.add(task);
			}
			for (final Callable<ConfigRunResult> task : tasks) {
				Future<ConfigRunResult> submit = algorithmExecutor.submit(task);
				runFutureList.add(submit);
			}
			algorithmExecutor.shutdown();
			algorithmExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (final Exception exception) {
			LOG.error("Execution interrupted");
		}
		if (Globals.CALCULATE_TTEST) {
			StatisticFunctions.runTTestAndPrettyPrint(tTestValues);
		}
		return runFutureList;
	}

	/**
	 * Run the experiment for a given {@link Configuration}
	 * 
	 * @param configuration
	 *            Given {@link Configuration}
	 */
	private ConfigRunResult execute(final Configuration configuration) {
		final Map<Metric, List<Float>> printResult = new ConcurrentHashMap<>();
		final ExecutorService executor;
		if(Globals.RUN_FOLDS_PARALLEL){
            if (Globals.RUN_FOLDS_NUMBER_OF_THREAD == -1) {
				executor = Executors
						.newFixedThreadPool(Runtime.getRuntime().availableProcessors() > Globals.NUMBER_OF_FOLDS
								? Globals.NUMBER_OF_FOLDS : Runtime.getRuntime().availableProcessors());
			}else{
				executor = Executors
						.newFixedThreadPool(Globals.RUN_FOLDS_NUMBER_OF_THREAD);
			}
		}else{
			executor = Executors
					.newFixedThreadPool(1);
		}
		foldExecutors.add(executor);
		final List<Runnable> tasks = new ArrayList<>();
		for (int i = 1; i <= Globals.NUMBER_OF_FOLDS; i++) {
			final DataModel trainData = dataSpliter.getTrainData(i);
			final DataModel testData = dataSpliter.getTestData(i);
			final int foldNumber = i;
			final Runnable task = () -> {
				try {
					MessageBus.getInstance().getBus().post(new FoldLevelUpdateMessage(configuration.getId(),foldNumber,FoldStatus.STARTED));
					LOG.debug("Fold " + foldNumber + " Started...");
					final List<Metric> evalTypes = loadMetics();

					final AbstractRecommender algorithm = (AbstractRecommender) SerializationUtils.clone(configuration.getAlgorithm());
					final SimilarityRepository similarityRepository = new SimilarityRepository(trainData,configuration);
					algorithm.setSimilarityRepository(similarityRepository);

					MessageBus.getInstance().getBus().post(new FoldLevelUpdateMessage(configuration.getId(),foldNumber,FoldStatus.TRAINING));
					configuration.getTimeUtil().setTrainTimeStart(foldNumber);
					LOG.debug("Fold " + foldNumber + " Train started...");
					algorithm.train(trainData);
					LOG.debug("Fold " + foldNumber + " Train is done");
					configuration.getTimeUtil().setTrainTimeEnd(foldNumber);					

					MessageBus.getInstance().getBus().post(new FoldLevelUpdateMessage(configuration.getId(),foldNumber,FoldStatus.TESTING));
					configuration.getTimeUtil().setTestTimeStart(foldNumber);
					handleRatingEvaluation(testData, evalTypes, algorithm);
					handleListEvaluation(trainData, testData, evalTypes, algorithm);
					configuration.getTimeUtil().setTestTimeEnd(foldNumber);

					handleMetric(evalTypes, printResult);
					LOG.debug("Fold " + foldNumber + " is done.");
					MessageBus.getInstance().getBus().post(new FoldLevelUpdateMessage(configuration.getId(),foldNumber,FoldStatus.FINISHED));
				} catch (final Exception exception) {
					LOG.error("Fold " + foldNumber + " is done with error. Error is " + exception.getMessage());					
				}
			};
			tasks.add(task);
		}
		for (final Runnable task : tasks) {
			executor.execute(task);
		}
		executor.shutdown();
		try {
            MessageBus.getInstance().getBus().post(new ShutdownFinishedMessage());
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (final InterruptedException exception) {
			LOG.error("Execution interupted.");
		}
		synchronized (LOCK) {

			LOG.info(configuration + " result is:");
			LOG.info("Average Train Time: " + configuration.getTimeUtil().getAverageTrainTime() + " seconds");
			LOG.info("Total Train Time: " + configuration.getTimeUtil().getTotalTrainTime() + " seconds");
			LOG.info("Average Test Time: " + configuration.getTimeUtil().getAverageTestTime() + " seconds");
			LOG.info("Total Test Time: " + configuration.getTimeUtil().getTotalTestTime() + " seconds");
			this.addAverageAndPretyPrintResult(printResult);
			this.googleDocPrintResult(printResult);

			this.tTestValues.put(configuration, printResult);
		}
		MessageBus.getInstance().getBus().post(new CalculationDoneMessage());
		ConfigRunResult configRunResult = new ConfigRunResult(configuration,
				configuration.getTimeUtil().getAverageTrainTime(),
				configuration.getTimeUtil().getTotalTrainTime(),
				configuration.getTimeUtil().getAverageTestTime(),
				configuration.getTimeUtil().getTotalTestTime(),
				printResult);
		return configRunResult;
	}

	/**
	 * Handles the {@link Metric}s which are instance of {@link AccuracyEvaluation}
	 * @param trainData
	 * @param testData
	 * @param evalTypes
	 * @param algorithm
	 */
	private void handleListEvaluation(final DataModel trainData, final DataModel testData, final List<Metric> evalTypes,
			Recommender algorithm) {
		final Metric hasListEvaluator = evalTypes.stream().filter(p3 -> p3 instanceof ListEvaluation).findAny()
				.orElse(null);
		if (hasListEvaluator != null) {
			for (final Integer userId : testData.getUsers().keySet()) {
				if(Thread.interrupted()){
					return;
				}
				final User user = testData.getUser(userId);
				if (Globals.USE_ONLY_POSITIVE_RATING_IN_TEST) {
					final long numberOfPositiveItems = user.getItemRating().values().stream()
							.filter(p4 -> p4 >= Globals.MINIMUM_THRESHOLD_FOR_POSITIVE_RATING).count();
					if (numberOfPositiveItems < Globals.TOP_N) {
						continue;
					}
				} else {
					final long numberOfPositiveItems = user.getItemRating().values().stream()
							.filter(p4 -> p4 >= Globals.MINIMUM_THRESHOLD_FOR_POSITIVE_RATING).count();
					if (numberOfPositiveItems == 0) {
						continue;
					}
				}
				final Map<Integer, Float> recommendItems = algorithm.recommendItems(user);

				for (Metric metric2 : evalTypes) {
					if (metric2 instanceof ListEvaluation) {
						((ListEvaluation) metric2).setTrainData(trainData);
						((ListEvaluation) metric2).addRecommendations(user, recommendItems);
					}
				}
			}
		}
	}

	/**
	 * Handles the {@link Metric}s which are instance of {@link ListEvaluation}
	 * @param testData
	 * @param evalTypes
	 * @param algorithm
	 */
	private void handleRatingEvaluation(final DataModel testData, final List<Metric> evalTypes, Recommender algorithm) {
		final Metric hasRatingEvaluator = evalTypes.stream().filter(p1 -> p1 instanceof AccuracyEvaluation).findAny()
				.orElse(null);
		if (hasRatingEvaluator != null) {
			for (final Rating rating : testData.getRatings()) {
				if(Thread.interrupted()){
					return;
				}
				final User testUser = testData.getUser(rating.getUserId());
				final long numberOfPositiveItems = testUser.getItemRating().values().stream()
						.filter(p2 -> p2 >= Globals.MINIMUM_THRESHOLD_FOR_POSITIVE_RATING).count();
				if (Globals.USE_ONLY_POSITIVE_RATING_IN_TEST) {
					if (numberOfPositiveItems < Globals.TOP_N) {
						continue;
					}
				} else {
					if (numberOfPositiveItems == 0) {
						continue;
					}
				}

				final Item testItem = testData.getItem(rating.getItemId());
				final Float predictRating = algorithm.predictRating(testUser, testItem);
				for (final Metric metric1 : evalTypes) {
					if (metric1 instanceof AccuracyEvaluation) {
						((AccuracyEvaluation) metric1).addTestPrediction(rating, predictRating);
					}
				}
			}
		}
	}

	/**
	 * This function generate result in google doc format. someone can easily
	 * copy paste it to google doc cell
	 * 
	 * @param printResult
	 * 
	 */
	private synchronized void googleDocPrintResult(Map<Metric, List<Float>> printResult) {
		final StringBuilder result = new StringBuilder();
		for (final Metric evalType : printResult.keySet()) {
			result.append("=SPLIT(\"").append(evalType).append(",");
			for (float accuracy : printResult.get(evalType)) {
				result.append(accuracy).append(",");
			}
			result.append(mean(printResult.get(evalType))).append("\",\",\")\n");
		}
		LOG.info(result.toString());
	}

	/**
	 * 
	 * @param evalTypes
	 * @param printResult
	 */
	private synchronized void handleMetric(final List<Metric> evalTypes, Map<Metric, List<Float>> printResult) {
		for (final Metric metric : evalTypes) {
			if (metric instanceof ListEvaluation) {
				if (printResult.get(metric) == null) {
					printResult.put(metric, Arrays.asList(((ListEvaluation) metric).getEvaluationResult()));
				} else {
					final List<Float> list = printResult.get(metric);
					final List<Float> newList = new ArrayList<>(list);
					newList.add(((ListEvaluation) metric).getEvaluationResult());
					printResult.put(metric, newList);
				}
			} else if (metric instanceof AccuracyEvaluation) {
				if (printResult.get(metric) == null) {
					printResult.put(metric, Arrays.asList(((AccuracyEvaluation) metric).getPredictionAccuracy()));
				} else {
					final List<Float> list = printResult.get(metric);
					final List<Float> newList = new ArrayList<>(list);
					newList.add(((AccuracyEvaluation) metric).getPredictionAccuracy());
					printResult.put(metric, newList);
				}
			}
		}

	}

	/**
	 * Print the result in a pretty way
	 * 
	 * @param printResult
	 */
	private synchronized void addAverageAndPretyPrintResult(Map<Metric, List<Float>> printResult) {

		String[][] resultTable = new String[printResult.keySet().size() + 1][(Globals.NUMBER_OF_FOLDS + 2)];
		resultTable[0][0] = "Fold number";
		resultTable[0][Globals.NUMBER_OF_FOLDS + 1] = "Average";
		for (int nFold = 1; nFold <= Globals.NUMBER_OF_FOLDS; nFold++) {
			resultTable[0][nFold] = String.valueOf(nFold);
		}

		int i = 1;
		int nFold = 1;
		for (final Metric evalType : printResult.keySet()) {
			resultTable[i][0] = evalType.getClass().getName();
			resultTable[i][Globals.NUMBER_OF_FOLDS + 1] = String.valueOf(mean(printResult.get(evalType)));
			for (float accuracy : printResult.get(evalType)) {
				resultTable[i][nFold++] = String.valueOf(accuracy);
			}
			i++;
			nFold = 1;
		}
		new PrettyPrinter().print(resultTable);
	}

	/**
	 * Calculate mean of the given {@link List>
	 * 
	 * @param list
	 * @return
	 */
	private float mean(final List<Float> list) {
		float sum = 0;
		for (int i = 0; i < list.size(); i++) {
			sum += list.get(i);
		}
		return sum / list.size();
	}

	/**
	 * Read the configuration file and load the specified metrics
	 * 
	 * @return List of {@link Metric}s
	 */
	public List<Metric> loadMetics() {
		final String metirics = Config.getString("METRICS", "");
		final String[] tokens = metirics.split(",");
		final List<Metric> metricList = new ArrayList<>();

		for (final String metricName : tokens) {
			try {
				final Object instantiateClass = ClassInstantiator.instantiateClass("metrics." + metricName);
				if (instantiateClass instanceof Metric) {
					metricList.add((Metric) instantiateClass);
				} else {
					throw new IllegalArgumentException("Metric " + metricName + " is not implemented yet");
				}
			} catch (final Exception exception) {
				LOG.error("Can not load metric " + metricName);
				exception.printStackTrace();
			}
		}
		Collections.sort(metricList, new Comparator<Metric>() {
			@Override
			public int compare(Metric o1, Metric o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});
		LOG.debug("Metrics loaded from config file: " + metricList);
		return metricList;
	}

	/**
	 * Read the configuration file and load the specified algorithms
	 * 
	 * @return List of {@link Recommender} algorithms
	 */
	@SuppressWarnings("unchecked")
	public List<Recommender> loadAlgorithms() {
		final String algorithms = Config.getString("ALGORITHMS", "");
		final String[] tokens = algorithms.split(",");
		final List<Recommender> algoList = new ArrayList<>();

		for (final String algoName : tokens) {
			try {
				Object algo = ClassInstantiator.instantiateClass(algoName);
				algoList.addAll((Collection<? extends Recommender>) algo);
			} catch (final Exception exception) {
				LOG.error("Can not load algorithm " + algoName);
				exception.printStackTrace();
			}
		}
		return algoList;
	}

}