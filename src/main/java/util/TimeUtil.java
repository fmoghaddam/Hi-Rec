package util;

import model.Globals;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * This class measures all the algorithm train and test time
 *
 * @author FBM
 */
public class TimeUtil {

    private final ConcurrentHashMap<Integer, TimeMeasurement> timeMeasurements = new ConcurrentHashMap<>();

    public synchronized long getAverageTrainTime() {
        long totalTime = 0;
        for (TimeMeasurement timeMeasurement : timeMeasurements.values()) {
            totalTime += timeMeasurement.getTrainTime();
        }
        return (long) (totalTime / Globals.NUMBER_OF_FOLDS);
    }

    public synchronized long getAverageTestTime() {
        long totalTime = 0;
        for (TimeMeasurement timeMeasurement : timeMeasurements.values()) {
            totalTime += timeMeasurement.getTestTime();
        }
        return (long) (totalTime / Globals.NUMBER_OF_FOLDS);
    }


    public synchronized long getTotalTrainTime() {
        Long minStartTime = null;
        Long maxEndTime = null;
        for (TimeMeasurement timeMeasurement : timeMeasurements.values()) {
            if (minStartTime == null || timeMeasurement.getTrainStart() < minStartTime) {
                minStartTime = timeMeasurement.getTrainStart();
            }
            if (maxEndTime == null || timeMeasurement.getTrainEnd() > maxEndTime) {
                maxEndTime = timeMeasurement.getTrainEnd();
            }
        }
        if (minStartTime == null || maxEndTime == null) {
            return 0L;
        }
        return TimeUnit.MILLISECONDS.toSeconds(maxEndTime - minStartTime);
    }

    public synchronized long getTotalTestTime() {
        Long minStartTime = null;
        Long maxEndTime = null;
        for (TimeMeasurement timeMeasurement : timeMeasurements.values()) {
            if (minStartTime == null || timeMeasurement.getTestStart() < minStartTime) {
                minStartTime = timeMeasurement.getTestStart();
            }
            if (maxEndTime == null || timeMeasurement.getTestEnd() > maxEndTime) {
                maxEndTime = timeMeasurement.getTestEnd();
            }
        }

        if (minStartTime == null || maxEndTime == null) {
            return 0L;
        }
        return TimeUnit.MILLISECONDS.toSeconds(maxEndTime - minStartTime);
    }

    public synchronized void setTrainTimeStart(final int foldId) {
        if (timeMeasurements.containsKey(foldId)) {
            throw new IllegalStateException("Train start time already set for fold " + foldId);
        } else {
            final TimeMeasurement trainTime = new TimeMeasurement(foldId);
            trainTime.setTrainStart();
            timeMeasurements.put(foldId, trainTime);
        }
    }

    public synchronized void setTrainTimeEnd(final int foldId) {
        if (timeMeasurements.containsKey(foldId)) {
            timeMeasurements.get(foldId).setTrainEnd();
        } else {
            throw new IllegalStateException("Train start time has not been set for fold " + foldId);
        }
    }

    public synchronized void setTestTimeStart(final int foldId) {
        if (timeMeasurements.containsKey(foldId)) {
            timeMeasurements.get(foldId).setTestStart();
        } else {
            throw new IllegalStateException("Train start time has not been set for fold " + foldId);
        }
    }

    public synchronized void setTestTimeEnd(final int foldId) {
        if (timeMeasurements.containsKey(foldId)) {
            timeMeasurements.get(foldId).setTestEnd();
        } else {
            throw new IllegalStateException("Test start time time has not been set for fold " + foldId);
        }
    }

    public synchronized void clean() {
        timeMeasurements.clear();
    }

    @Override
    public String toString() {
        return "TimeUtil [timeMeasurements=" + timeMeasurements + "]";
    }

}
