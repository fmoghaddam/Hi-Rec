package util;

import java.util.concurrent.TimeUnit;

/**
 * This class try to store algorithm train and test time
 *
 * @author FBM
 */
public final class TimeMeasurement {
    private int foldId;
    private long trainStart = 0;
    private long trainEnd = 0;
    private long testStart = 0;
    private long testEnd = 0;

    /**
     *
     */
    public TimeMeasurement(final int foldId) {
        this.foldId = foldId;
    }

    public long getTrainTime() {
        return TimeUnit.MILLISECONDS.toSeconds(trainEnd - trainStart);
    }

    public long getTrainStart() {
        return trainStart;
    }

    public long getTrainEnd() {
        return trainEnd;
    }

    public long getTestStart() {
        return testStart;
    }

    public long getTestEnd() {
        return testEnd;
    }

    public long getTestTime() {
        return TimeUnit.MILLISECONDS.toSeconds(testEnd - testStart);
    }

    public void setTrainStart() {
        this.trainStart = System.currentTimeMillis();
    }

    public void setTrainEnd() {
        this.trainEnd = System.currentTimeMillis();
    }

    public void setTestStart() {
        this.testStart = System.currentTimeMillis();
    }

    public void setTestEnd() {
        this.testEnd = System.currentTimeMillis();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + foldId;
        result = prime * result + (int) (testEnd ^ (testEnd >>> 32));
        result = prime * result + (int) (testStart ^ (testStart >>> 32));
        result = prime * result + (int) (trainEnd ^ (trainEnd >>> 32));
        result = prime * result + (int) (trainStart ^ (trainStart >>> 32));
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TimeMeasurement other = (TimeMeasurement) obj;
        if (foldId != other.foldId) {
            return false;
        }
        if (testEnd != other.testEnd) {
            return false;
        }
        if (testStart != other.testStart) {
            return false;
        }
        if (trainEnd != other.trainEnd) {
            return false;
        }
        if (trainStart != other.trainStart) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TimeMeasurement [foldId=" + foldId + ", trainStart=" + trainStart + ", trainEnd=" + trainEnd
                + ", testStart=" + testStart + ", testEnd=" + testEnd + "]";
    }

}
