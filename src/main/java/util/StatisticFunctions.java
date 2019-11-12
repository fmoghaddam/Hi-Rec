package util;

import interfaces.Metric;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.log4j.Logger;
import run.Configuration;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 * A class that contains static methods.
 * The code based is from Recommneder101:
 * http://ls13-www.cs.tu-dortmund.de/homepage/recommender101/index.shtml
 */
public class StatisticFunctions {
    private static final Logger LOG = Logger
            .getLogger(StatisticFunctions.class.getCanonicalName());

    public static final Random random = new Random();
    private static final Randomness randomness = new Randomness();

    public static double ran_gaussian(double mean, double stdev) {
        if ((stdev == 0.0) || (Double.isNaN(stdev))) {
            return mean;
        } else {
            return mean + stdev * ran_gaussian();
        }
    }

    private static double ran_gaussian() {
        return randomness.nextGaussian();
    }

    public static class Randomness {
        private final Random rand = new Random();

        double nextGaussian() {
            return rand.nextGaussian();
        }
    }

    /**
     *
     */
    public
    static synchronized void runTTestAndPrettyPrint(final Map<Configuration, Map<Metric, List<Float>>> tTestValues) {
        for (Entry<Configuration, Map<Metric, List<Float>>> entry1 : tTestValues.entrySet()) {
            final Configuration configuration1 = entry1.getKey();
            final Map<Metric, List<Float>> configuration1Value = entry1.getValue();
            final int idWhichConsidered = configuration1.getId();
            for (Entry<Configuration, Map<Metric, List<Float>>> entry2 : tTestValues.entrySet()) {
                final Configuration configuration2 = entry2.getKey();
                if (configuration2.getId() <= idWhichConsidered) {
                    continue;
                }
                final Map<Metric, List<Float>> configuration2Value = entry2.getValue();
                if (configuration2.equals(configuration1)) {
                    continue;
                }
//                if(!configuration1.getAlgorithm().toString().equals(configuration2.getAlgorithm().toString())){
//                    continue;
//                }

                final String[][] resultTable = new String[2][configuration1Value.keySet().size() + 1];
                int c = 1;
                for (final Metric m : configuration1Value.keySet()) {
                    resultTable[0][c] = m.toString();
                    c++;
                }
                resultTable[0][0] = "Metric";
                resultTable[1][0] = "P-Value";

                int iIndex = 1;
                int jIndex = 1;
                for (final Metric metric : configuration1Value.keySet()) {
                    final List<Float> list1 = configuration1Value.get(metric);
                    final List<Float> list2 = configuration2Value.get(metric);

                    double[] list1Double = new double[list1.size()];
                    double[] list2Double = new double[list2.size()];
                    for (int i = 0; i < list1.size(); i++) {
                        list1Double[i] = (double) list1.get(i);
                    }
                    for (int i = 0; i < list2.size(); i++) {
                        list2Double[i] = (double) list2.get(i);
                    }

                    final TTest tTest = new TTest();
                    final double p_value = tTest.tTest(list1Double, list2Double);
                    resultTable[iIndex][jIndex++] = String.valueOf(p_value);
                }
                LOG.info(configuration1.toString());
                LOG.info(configuration2.toString());
                new PrettyPrinter().print(resultTable);
            }
        }

    }
}
