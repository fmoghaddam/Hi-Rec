package metrics;

import interfaces.AccuracyEvaluation;
import model.Globals;
import model.Rating;

/**
 * Coverage.
 * The code based is from Recommneder101:
 * http://ls13-www.cs.tu-dortmund.de/homepage/recommender101/index.shtml
 */
public final class PredictionCoverageOnlyPositive
        implements AccuracyEvaluation
{

    /**
     * A counter for the total number of ratings
     */
    int totalRatings = 0;
    /**
     * A counter for the predicted one
     */
    int predictedRatings = 0;

    /*
     * @see interfaces.AccuracyEvaluation#addTestPrediction(model.Rating, float)
     */
    @Override
    public
            void addTestPrediction(
                    final Rating rating, final float prediction)
    {
        if(rating.getRating()>=Globals.MINIMUM_THRESHOLD_FOR_POSITIVE_RATING){
            if (!Float.isNaN(prediction)) {
                predictedRatings++;
            }
            totalRatings++;
        }
    }

    /*
     * @see interfaces.AccuracyEvaluation#getPredictionAccuracy()
     */
    @Override
    public
            float getPredictionAccuracy() {
        return predictedRatings / (float)totalRatings;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public
            String toString() {
        return "CoverageOnlyPositive";
    }

    /*
     * @see java.lang.Object#hashCode()
     */
    @Override
    public
            int hashCode() {
        return 13;
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public
            boolean equals(
                    Object obj)
    {
        if (this.toString().equals(obj.toString())) {
            return true;
        } else {
            return false;
        }
    }
}
