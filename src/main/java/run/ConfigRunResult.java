package run;

import interfaces.Metric;
import model.Globals;

import java.util.List;
import java.util.Map;

public class ConfigRunResult {
    private Configuration configuration;
    private long averageTrainTime;
    private long totalTrainTime;
    private long averageTestTime;
    private long totalTestTime;
    private Map<Metric, List<Float>> metricResultMap;

    public ConfigRunResult(Configuration configuration, long averageTrainTime, long totalTrainTime,
                           long averageTestTime, long totalTestTime, Map<Metric, List<Float>> metricResultMap) {
        this.configuration = configuration;
        this.averageTrainTime = averageTrainTime;
        this.totalTrainTime = totalTrainTime;
        this.averageTestTime = averageTestTime;
        this.totalTestTime = totalTestTime;
        this.metricResultMap = metricResultMap;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public long getAverageTrainTime() {
        return averageTrainTime;
    }

    public long getTotalTrainTime() {
        return totalTrainTime;
    }

    public long getAverageTestTime() {
        return averageTestTime;
    }

    public long getTotalTestTime() {
        return totalTestTime;
    }

    public String[][] asTable() {
        String[][] resultTable = new String[metricResultMap.keySet().size() + 1][(Globals.NUMBER_OF_FOLDS + 2)];
        resultTable[0][0] = "Fold number";
        resultTable[0][Globals.NUMBER_OF_FOLDS + 1] = "Average";
        for (int nFold = 1; nFold <= Globals.NUMBER_OF_FOLDS; nFold++) {
            resultTable[0][nFold] = String.valueOf(nFold);
        }

        int i = 1;
        int nFold = 1;
        for (final Metric evalType : metricResultMap.keySet()) {
            resultTable[i][0] = evalType.getClass().getName();
            resultTable[i][Globals.NUMBER_OF_FOLDS + 1] = String.valueOf(mean(metricResultMap.get(evalType)));
            for (float accuracy : metricResultMap.get(evalType)) {
                resultTable[i][nFold++] = String.valueOf(accuracy);
            }
            i++;
            nFold = 1;
        }
        return resultTable;
    }

    private float mean(final List<Float> list) {
        float sum = 0;
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i);
        }
        return sum / list.size();
    }

    public String toLatexTable() {
        String[][] table = asTable();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\\begin{tabular}{");
        for (int i = 0; i < table[0].length; i++) {
            stringBuilder.append(" l");
        }
        stringBuilder.append("}").append("\n");
        for (int i = 1; i < table.length; i++) {
            stringBuilder.append(String.join(" & ", table[i]));
            stringBuilder.append("\\\\").append("\n");
        }


        stringBuilder.append("\\end{tabular}");
        return stringBuilder.toString();
    }

    public String toTabSeperatedTable() {
        String[][] table = asTable();

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < table.length; i++) {
            stringBuilder.append(String.join("\t", table[i]));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
