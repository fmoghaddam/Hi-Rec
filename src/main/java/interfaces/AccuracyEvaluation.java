package interfaces;

import model.Rating;

/**
 * All the accuracy evaluation metrics (such as RMSE,MAE,...) should implement
 * this interface
 * 
 * @author FBM
 *
 */
public interface AccuracyEvaluation extends Metric {
    /**
     * Adds prediction for internal calculation.
     * 
     * @param rating
     *            Real {@link Rating} which normally comes from test data
     * @param prediction
     *            Predicted rating
     */
    void addTestPrediction(Rating rating, float prediction);

    /**
     * Returns the calculated result
     * 
     * @return The calculated result
     */
    float getPredictionAccuracy();
}
