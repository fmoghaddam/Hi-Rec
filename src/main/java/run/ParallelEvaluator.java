package run;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;

import controller.DataSplitter;
import controller.similarity.SimilarityRepository;
import gui.pages.AlgorithmLevelUpdateMessage;
import gui.pages.FoldLevelUpdateMessage;
import gui.pages.FoldStatus;
import interfaces.AbstractRecommender;
import interfaces.AccuracyEvaluation;
import interfaces.ListEvaluation;
import interfaces.Metric;
import interfaces.Recommender;
import model.DataModel;
import model.Globals;
import model.Item;
import model.Rating;
import model.User;
import util.ClassInstantiator;
import util.Config;
import util.MessageBus;
import util.PrettyPrinter;
import util.StatisticFunctions;

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

	public ParallelEvaluator(final DataModel data) {
		this.dataModel = data;
		this.dataSpliter = new DataSplitter(this.dataModel.getCopy());
		this.dataSpliter.shuffle();
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
			throw new IllegalArgumentException("Number of configuarion in config file is " + numberOfConfiguration);
		}
		AbstractRecommender algorithm = null;
		boolean useTag;
		boolean useRating;
		boolean useLowLevel;
		boolean useGenre;
		for (int i = 1; i <= numberOfConfiguration; i++) {
			final String algorithmName = Config.getString("ALGORITHM_" + i + "_NAME", "");
			try {
				algorithm = (AbstractRecommender)ClassInstantiator.instantiateClass("algorithms." + algorithmName);				
				ClassInstantiator.setParametersDynamically(algorithm, i);
			} catch (final Exception e) {
				LOG.error("Can not load algorithm " + algorithmName, e);
				System.exit(1);
			}
			useLowLevel = Config.getBoolean("ALGORITHM_" + i + "_USE_LOW_LEVEL", false);
			useRating = Config.getBoolean("ALGORITHM_" + i + "_USE_RATING", false);
			useTag = Config.getBoolean("ALGORITHM_" + i + "_USE_TAG", false);
			useGenre = Config.getBoolean("ALGORITHM_" + i + "_USE_GENRE", false);
			configurations.add(new Configuration(i, algorithm, useLowLevel, useGenre, useTag, useRating));
			MessageBus.getInstance().getBus().post(new AlgorithmLevelUpdateMessage(i, algorithmName, Globals.NUMBER_OF_FOLDS));
		}
		return configurations;
	}

	/**
	 * Iterate over all the {@link Configuration}s and run them one by one
	 */
	public void evaluate() {
		final List<Configuration> configurations = readConfigurations();
		final ExecutorService executor = Executors
				.newFixedThreadPool(Runtime.getRuntime().availableProcessors() > configurations.size()
						? configurations.size() : Runtime.getRuntime().availableProcessors());
		final List<Runnable> tasks = new ArrayList<>();
		try {
			for (Configuration configuration : configurations) {
				final Runnable task = () -> {
					LOG.info("This process may take long time. Still running please wait....");
					LOG.info(configuration + "...");
					execute(configuration);
				};
				tasks.add(task);
			}
			for (final Runnable task : tasks) {
				executor.execute(task);
			}
			executor.shutdown();
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (final Exception exception) {
			LOG.error(exception.getMessage());
		}
		if (Globals.CALCULATE_TTEST) {
			StatisticFunctions.runTTestAndPrettyPrint(tTestValues);
		}
	}

	/**
	 * Run the experiment for a given {@link Configuration}
	 * 
	 * @param configuration
	 *            Given {@link Configuration}
	 */
	private void execute(final Configuration configuration) {
		final Map<Metric, List<Float>> printResult = new ConcurrentHashMap<>();
		final ExecutorService executor = Executors
				.newFixedThreadPool(Runtime.getRuntime().availableProcessors() > Globals.NUMBER_OF_FOLDS
						? (int) Globals.NUMBER_OF_FOLDS : Runtime.getRuntime().availableProcessors());
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
					exception.printStackTrace();
				}
			};
			tasks.add(task);
		}
		for (final Runnable task : tasks) {
			executor.execute(task);
		}		
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (final InterruptedException exception) {
			LOG.error(exception.getMessage(),exception);
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
			result.append(String.valueOf(mean(printResult.get(evalType)))).append("\",\",\")\n");
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

		String[][] resultTable = new String[printResult.keySet().size() + 1][(int) (Globals.NUMBER_OF_FOLDS + 2)];
		resultTable[0][0] = "Fold number";
		resultTable[0][(int) (Globals.NUMBER_OF_FOLDS + 1)] = "Average";
		for (int nFold = 1; nFold <= Globals.NUMBER_OF_FOLDS; nFold++) {
			resultTable[0][nFold] = String.valueOf(nFold);
		}

		int i = 1;
		int nFold = 1;
		for (final Metric evalType : printResult.keySet()) {
			resultTable[i][0] = evalType.getClass().getName();
			resultTable[i][(int) (Globals.NUMBER_OF_FOLDS + 1)] = String.valueOf(mean(printResult.get(evalType)));
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
				Object algo = ClassInstantiator.instantiateClass("algorithms." + algoName);
				algoList.addAll((Collection<? extends Recommender>) algo);
			} catch (final Exception exception) {
				LOG.error("Can not load algorithm " + algoName);
				exception.printStackTrace();
			}
		}
		return algoList;
	}

}
