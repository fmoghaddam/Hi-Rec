package metrics;

import interfaces.AccuracyEvaluation;
import model.Rating;

/**
 * Root Mean Square Error.
 * The code based is from Recommneder101:
 * http://ls13-www.cs.tu-dortmund.de/homepage/recommender101/index.shtml
 */
public final class RMSE
        implements AccuracyEvaluation
{

    /**
     * Accumulated error
     */
    float errorAccumulator;
    /**
     * The number of predictions
     */
    int predictionCount;

    /*
     * @see interfaces.AccuracyEvaluation#addTestPrediction(model.Rating, float)
     */
    @Override
    public
            void addTestPrediction(
                    final Rating rating, final float prediction)
    {
        if (!Float.isNaN(prediction)) {
            if (rating != null) {
                float error = (float)Math
                        .abs(Math.pow(rating.getRating() - prediction, 2));
                errorAccumulator += error;
                predictionCount++;
            }
        }
    }

    /*
     * @see interfaces.AccuracyEvaluation#getPredictionAccuracy()
     */
    @Override
    public
            float getPredictionAccuracy() {
        return (float)Math.sqrt(errorAccumulator / (float)predictionCount);
    }

    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public
            String toString() {
        return "RMSE";
    }

    /*
     * @see java.lang.Object#hashCode()
     */
    @Override
    public
            int hashCode() {
        return 9;
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
