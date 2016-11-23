package interfaces;

import java.util.Map;

import model.DataModel;
import model.Item;
import model.User;

/**
 * All the algorithms should implement this interface
 * 
 * @author FBM
 *
 */
public interface Recommender {

    /**
     * Predicts a rating for the user
     * 
     * @param user
     * @param item
     * @return the rating value
     */
    Float predictRating(User user, Item item);

    /**
     * Generates a ranked list of recommendations
     * 
     * @param user
     * @return the ranked list of items (in descending order)
     */
    Map<Integer, Float> recommendItems(User user);

    /**
     * Trains the algorithm. Keep is mind for some algorithms which there is no
     * learning phase this function can be empty
     * 
     * @param trainData
     */
    void train(DataModel trainData);

    /**
     * Set similarity function for the algorithm. 
     * 
     * @param similarityRepository
     *            Given {@link SimilarityInterface}
     */
    void setSimilarityRepository(final SimilarityInterface similarityRepository);

    /**
     * If algorithms needs to calculate similarity returns {@code true}, O.W.
     * {@code false}
     * 
     * @return If algorithms needs to calculate similarity returns {@code true},
     *         O.W. {@code false}
     */
    boolean isSimilairtyNeeded();
    
    /**
     * Returns all the configurable parameters. These parameters should be set in 
     * config.properties file.
     * @return
     */
    Map<String,String> getConfigurabaleParameters();

	/**
	 * Get similairty function
	 * @return
	 */
	SimilarityInterface getSimilarityRepository();

}
