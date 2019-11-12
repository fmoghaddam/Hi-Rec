package interfaces;

import model.DataModel;
import model.User;

import java.util.Map;

/**
 * All the rank evaluation metrics (such as NDCG,Precision,...) should implement
 * this interface
 *
 * @author FBM
 */
public interface ListEvaluation extends Metric {
    /**
     * Adds generated list for a specific user for internal calculation
     *
     * @param user A target {@link User} which normally comes from test data
     * @param list Generate item list with related predicted rating for a given
     *             {@link User}
     */
    void addRecommendations(User user, Map<Integer, Float> list);

    /**
     * Returns the calculated result
     *
     * @return The calculated result
     */
    float getEvaluationResult();

    /**
     * Set train data. Train data neede for some metrics such as popularity
     *
     * @param trainData
     */
    void setTrainData(DataModel trainData);
}
