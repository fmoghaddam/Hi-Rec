package metrics;

import interfaces.AccuracyEvaluation;
import model.Rating;

/**
 * Coverage.
 * The code based is from Recommneder101:
 * http://ls13-www.cs.tu-dortmund.de/homepage/recommender101/index.shtml
 */
public final class PredictionCoverage
        implements AccuracyEvaluation {

    /**
     * A counter for the total number of ratings
     */
    private int totalRatings = 0;
    /**
     * A counter for the predicted one
     */
    private int predictedRatings = 0;

    /*
     * @see interfaces.AccuracyEvaluation#addTestPrediction(model.Rating, float)
     */
    @Override
    public void addTestPrediction(
            final Rating rating, final float prediction) {
        if (!Float.isNaN(prediction)) {
            predictedRatings++;
        }
        totalRatings++;
    }

    /*
     * @see interfaces.AccuracyEvaluation#getPredictionAccuracy()
     */
    @Override
    public float getPredictionAccuracy() {
        return predictedRatings / (float) totalRatings;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Coverage";
    }

    /*
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 12;
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(
            Object obj) {
        if (this.toString().equals(obj.toString())) {
            return true;
        } else {
            return false;
        }
    }
}
