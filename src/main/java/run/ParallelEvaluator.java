package run;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import controller.DataSplitter;
import controller.similarity.SimilarityRepository;
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
import util.PrettyPrinter;
import util.TimeUtil;

/**
 * This is the main executor of all the algorithms. This class read the config
 * file and then run configuration one by one on all the available CPUs
 * 
 * @author FBM
 *
 */
public final class ParallelEvaluator {

    private static final Logger LOG = Logger
            .getLogger(ParallelEvaluator.class.getCanonicalName());
    private final DataModel dataModel;

    public ParallelEvaluator(
            final DataModel data)
    {
        this.dataModel = data;
    }

    /**
     * Reads all the {@link Configuration} form config file
     * 
     * @return List of all {@link Configuration}
     */
    private
            List<Configuration> readConfigurations() {
        final int numberOfConfiguration = Config
                .getInt("NUMBER_OF_CONFIGURATION", 0);
        final List<Configuration> configurations = new ArrayList<>();
        if (numberOfConfiguration <= 0) {
            throw new IllegalArgumentException(
                    "Number of configuarion in config file is "
                            + numberOfConfiguration);
        }
        Recommender algorithm = null;
        boolean useTag;
        boolean useRating;
        boolean useLowLevel;
        boolean useGenre;
        for (int i = 1; i <= numberOfConfiguration; i++) {
            final String algorithmName = Config
                    .getString("ALGORITHM_" + i + "_NAME", "");
            try {
                Object algo = ClassInstantiator
                        .instantiateClass("algorithms." + algorithmName);
                algorithm = (Recommender)algo;
            } catch (final Exception e) {
                LOG.error("Can not load algorithm " + algorithmName, e);
                System.exit(1);
            }
            useLowLevel = Config.getBoolean("ALGORITHM_" + i + "_USE_LOW_LEVEL",
                    false);
            useRating = Config.getBoolean("ALGORITHM_" + i + "_USE_RATING",
                    false);
            useTag = Config.getBoolean("ALGORITHM_" + i + "_USE_TAG", false);
            useGenre = Config.getBoolean("ALGORITHM_" + i + "_USE_GENRE",
                    false);
            configurations.add(new Configuration(i, algorithm, useLowLevel,
                    useGenre, useTag, useRating));
        }
        return configurations;
    }

    /**
     * Iterate over all the {@link Configuration}s and run them one by one
     */
    public
            void evaluate() {
        try {
            final List<Configuration> configurations = readConfigurations();
            for (Configuration configuration: configurations) {
                TimeUtil.clean();
                LOG.info(configuration);
                LOG.info(
                        "This process may take long time. Still running please wait....");
                execute(configuration);
            }
        } catch (final Exception exception) {
            LOG.error(exception.getMessage());
        }
    }

    /**
     * run the experiment for given {@link Configuration}
     * 
     * @param configuration
     *            Given {@link Configuration}
     */
    private
            void execute(
                    final Configuration configuration)
    {
        final Map<Metric, List<Float>> printResult = new ConcurrentHashMap<>();
        final ExecutorService executor = Executors
                .newFixedThreadPool(Runtime.getRuntime()
                        .availableProcessors() > Globals.NUMBER_OF_FOLDS
                                ? (int)Globals.NUMBER_OF_FOLDS
                                : Runtime.getRuntime().availableProcessors());
        final DataSplitter dataSpliter = new DataSplitter(
                this.dataModel.getCopy());
        dataSpliter.shuffle();
        final Runnable[] tasks = new Runnable[(int)Globals.NUMBER_OF_FOLDS];
        for (int i = 1; i <= Globals.NUMBER_OF_FOLDS; i++) {
            final DataModel trainData = dataSpliter.getTrainData(i);
            final DataModel testData = dataSpliter.getTestData(i);
            final int foldNumber = i;
            final Runnable task = () -> {
                try {
                    LOG.debug("Fold " + foldNumber + " Started...");
                    final List<Metric> evalTypes = loadMetics();

                    final String algorithmName = Config.getString(
                            "ALGORITHM_" + configuration.getId() + "_NAME", "");
                    Recommender algorithm = null;
                    try {
                        final Object algo = ClassInstantiator.instantiateClass(
                                "algorithms." + algorithmName);
                        algorithm = (Recommender)algo;
                    } catch (final Exception e) {
                        LOG.error("Can not load algorithm " + algorithmName, e);
                        System.exit(1);
                    }

                    algorithm.setSimilarityRepository(
                            new SimilarityRepository(trainData, configuration));

                    TimeUtil.setTrainTimeStart(foldNumber);
                    LOG.debug("Fold " + foldNumber + " Train started...");
                    algorithm.train(trainData);
                    LOG.debug("Fold " + foldNumber + " Train is done");
                    TimeUtil.setTrainTimeEnd(foldNumber);
                    TimeUtil.setTestTimeStart(foldNumber);
                    final Metric hasRatingEvaluator = evalTypes.stream()
                            .filter(p1 -> p1 instanceof AccuracyEvaluation)
                            .findAny().orElse(null);
                    if (hasRatingEvaluator != null) {
                        for (final Rating rating: testData.getRatings()) {
                            final User testUser = testData
                                    .getUser(rating.getUserId());
                            final long count1 = testUser.getItemRating()
                                    .values().stream().filter(p2 -> p2 >= 4)
                                    .count();
                            if (count1 < Globals.TOP_N) {
                                continue;
                            }
                            final Item testItem = testData
                                    .getItem(rating.getItemId());
                            final Float predictRating = algorithm
                                    .predictRating(testUser, testItem);
                            for (final Metric metric1: evalTypes) {
                                if (metric1 instanceof AccuracyEvaluation) {
                                    ((AccuracyEvaluation)metric1)
                                            .addTestPrediction(rating,
                                                    predictRating);
                                }
                            }
                        }
                    }
                    final Metric hasListEvaluator = evalTypes.stream()
                            .filter(p3 -> p3 instanceof ListEvaluation)
                            .findAny().orElse(null);
                    if (hasListEvaluator != null) {
                        for (final Integer userId: testData.getUsers()
                                .keySet())
                        {
                            final User user = testData.getUser(userId);
                            final long count2 = user.getItemRating().values()
                                    .stream().filter(p4 -> p4 >= 4).count();// >Globals.TOP_N;
                            if (count2 < Globals.TOP_N) {
                                continue;
                            }
                            final Map<Integer, Float> recommendItems = algorithm
                                    .recommendItems(user);

                            for (Metric metric2: evalTypes) {
                                if (metric2 instanceof ListEvaluation) {
                                    ((ListEvaluation)metric2)
                                            .addRecommendations(user,
                                                    recommendItems);
                                }
                            }
                        }
                    }
                    TimeUtil.setTestTimeEnd(foldNumber);
                    handleMetric(evalTypes, printResult);
                    LOG.debug("Fold " + foldNumber + " is done.");
                } catch (final Exception exception) {
                    LOG.error("Fold " + foldNumber
                            + " is done with error. Error is "
                            + exception.getMessage());
                    exception.printStackTrace();
                }
            };
            tasks[i - 1] = task;
        }

        for (int i = 0; i < tasks.length; i++) {
            try {
                executor.execute(tasks[i]);
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (final InterruptedException exception) {
            exception.printStackTrace();
        }
        LOG.info(configuration.getAlgorithm().toString() + " result is:");
        LOG.info("Train Time: " + TimeUtil.getTrainTime() + " seconds");
        LOG.info("Test Time: " + TimeUtil.getTestTime() + " seconds");
        pretyPrintResult(printResult);
        // googleDocPrintResult(printResult);
    }

    /**
     * This function generate result in google doc format. someone can easily
     * copy paste it to google doc cell
     * 
     * @param printResult
     * 
     */
    @SuppressWarnings("unused")
    private
            void googleDocPrintResult(
                    Map<Metric, List<Float>> printResult)
    {
        final StringBuilder result = new StringBuilder();
        for (Metric evalType: printResult.keySet()) {
            result.append(evalType).append(",").append("=SPLIT(\"");
            for (float accuracy: printResult.get(evalType)) {
                result.append(accuracy).append(",");
            }
            result.append(String.valueOf(mean(printResult.get(evalType))))
                    .append("\",\",\")\n");
        }
        LOG.info(result.toString());
    }

    /**
     * 
     * @param evalTypes
     * @param printResult
     */
    private synchronized
            void handleMetric(
                    final List<Metric> evalTypes,
                    Map<Metric, List<Float>> printResult)
    {
        for (final Metric metric: evalTypes) {
            if (metric instanceof ListEvaluation) {
                if (printResult.get(metric) == null) {
                    printResult.put(metric, Arrays.asList(
                            ((ListEvaluation)metric).getEvaluationResult()));
                } else {
                    final List<Float> list = printResult.get(metric);
                    final List<Float> newList = new ArrayList<>(list);
                    newList.add(((ListEvaluation)metric).getEvaluationResult());
                    printResult.put(metric, newList);
                }
            } else if (metric instanceof AccuracyEvaluation) {
                if (printResult.get(metric) == null) {
                    printResult.put(metric,
                            Arrays.asList(((AccuracyEvaluation)metric)
                                    .getPredictionAccuracy()));
                } else {
                    final List<Float> list = printResult.get(metric);
                    final List<Float> newList = new ArrayList<>(list);
                    newList.add(((AccuracyEvaluation)metric)
                            .getPredictionAccuracy());
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
    private
            void pretyPrintResult(
                    Map<Metric, List<Float>> printResult)
    {

        String[][] resultTable = new String[printResult.keySet().size()
                + 1][(int)(Globals.NUMBER_OF_FOLDS + 2)];
        resultTable[0][0] = "Fold number";
        resultTable[0][(int)(Globals.NUMBER_OF_FOLDS + 1)] = "Avearge";
        for (int nFold = 1; nFold <= Globals.NUMBER_OF_FOLDS; nFold++) {
            resultTable[0][nFold] = String.valueOf(nFold);
        }

        int i = 1;
        int nFold = 1;
        for (Metric evalType: printResult.keySet()) {
            resultTable[i][0] = evalType.getClass().getName();
            resultTable[i][(int)(Globals.NUMBER_OF_FOLDS + 1)] = String
                    .valueOf(mean(printResult.get(evalType)));
            for (float accuracy: printResult.get(evalType)) {
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
    private
            float mean(
                    final List<Float> list)
    {
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
    public
            List<Metric> loadMetics() {
        final String metirics = Config.getString("METRICS", "");
        final String[] tokens = metirics.split(",");
        final List<Metric> metricList = new ArrayList<>();

        for (final String metricName: tokens) {
            try {
                final Object instantiateClass = ClassInstantiator
                        .instantiateClass("metrics." + metricName);
                if (instantiateClass instanceof Metric) {
                    metricList.add((Metric)instantiateClass);
                } else {
                    throw new IllegalArgumentException(
                            "Metric " + metricName + " is not implemented yet");
                }
            } catch (final Exception exception) {
                LOG.error("Can not load metric " + metricName);
                exception.printStackTrace();
            }
        }
        Collections.sort(metricList, new Comparator<Metric>() {
            @Override
            public
                    int compare(
                            Metric o1, Metric o2)
            {
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
    public
            List<Recommender> loadAlgorithms() {
        final String algorithms = Config.getString("ALGORITHMS", "");
        final String[] tokens = algorithms.split(",");
        final List<Recommender> algoList = new ArrayList<>();

        for (final String algoName: tokens) {
            try {
                Object algo = ClassInstantiator.instantiateClass(
                        "algorithms." + algoName);
                algoList.addAll((Collection<? extends Recommender>)algo);
            } catch (final Exception exception) {
                LOG.error("Can not load algorithm " + algoName);
                exception.printStackTrace();
            }
        }
        return algoList;
    }

}