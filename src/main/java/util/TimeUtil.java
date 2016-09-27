package util;

import java.util.concurrent.ConcurrentHashMap;

/**
 * This class measure all the algorithm train and test time
 * @author FBM
 *
 */
public final class TimeUtil {

    private static final ConcurrentHashMap<Integer, TimeMeasurement> timeMeasurements = new ConcurrentHashMap<>();

    public static synchronized long getTrainTime() {
	long totalTime = 0;
	for (TimeMeasurement timeMeasurement : timeMeasurements.values()) {
	    totalTime += timeMeasurement.getTrainTime();
	}
	return totalTime;
    }

    public static synchronized long getTestTime() {
	long totalTime = 0;
	for (TimeMeasurement timeMeasurement : timeMeasurements.values()) {
	    totalTime += timeMeasurement.getTestTime();
	}
	return totalTime;
    }

    public static synchronized void setTrainTimeStart(final int foldId) {
	if (timeMeasurements.containsKey(foldId)) {
	    throw new IllegalStateException("Train start time already set for fold " + foldId);
	} else {
	    final TimeMeasurement trainTime = new TimeMeasurement(foldId);
	    trainTime.setTrainStart();
	    timeMeasurements.put(foldId, trainTime);
	}
    }

    public static synchronized void setTrainTimeEnd(final int foldId) {
	if (timeMeasurements.containsKey(foldId)) {
	    timeMeasurements.get(foldId).setTrainEnd();
	} else {
	    throw new IllegalStateException("Train start time already set for fold " + foldId);
	}
    }

    public static synchronized void setTestTimeStart(final int foldId) {
	if (timeMeasurements.containsKey(foldId)) {
	    timeMeasurements.get(foldId).setTestStart();
	} else {
	    throw new IllegalStateException("Test time already set for fold " + foldId);
	}
    }

    public static synchronized void setTestTimeEnd(final int foldId) {
	if (timeMeasurements.containsKey(foldId)) {
	    timeMeasurements.get(foldId).setTestEnd();
	} else {
	    throw new IllegalStateException("Test time already set for fold " + foldId);
	}
    }

    public static synchronized void clean() {
	timeMeasurements.clear();
    }
}
