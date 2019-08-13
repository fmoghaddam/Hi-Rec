package metrics;

import org.apache.log4j.Logger;
import interfaces.AccuracyEvaluation;
import model.Rating;

/**
 * Mean Absolute Error. 
 * The code based is from Recommneder101:
 * http://ls13-www.cs.tu-dortmund.de/homepage/recommender101/index.shtml
 */
public final class MAE
        implements AccuracyEvaluation
{

    /**
     * Logger used for this class
     */
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(MAE.class.getCanonicalName());
    /**
     * Accumulated the errors
     */
    private float errorAccumulator;
    /**
     * The number of predictions
     */
    private int predictionCount;

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
                float error = Math.abs(rating.getRating() - prediction);
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
        return errorAccumulator / (float)predictionCount;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public
            String toString() {
        return "MAE";
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public
            int hashCode() {
        return 4;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public
            boolean equals(
                    Object obj)
    {
        if(obj==null){
            throw new IllegalArgumentException("Obj is null");
        }
        if (this.toString().equals(obj.toString())) {
            return true;
        } else {
            return false;
        }
    }

}
