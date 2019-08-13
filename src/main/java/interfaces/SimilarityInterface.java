package interfaces;

/**
 * All the similarity classes should implement this interface
 * 
 * @author FBM
 *
 */
public interface SimilarityInterface {

    /**
     * Returns Similarity between two items. All the checks about which
     * similarity function should be used, should handle internally
     * 
     * @param itemId1
     *            Given item id
     * @param itemId2
     *            Given item id
     * @return Similarity between two items
     */
    Float getItemSimilairty(int itemId1, int itemId2);
    
    /**
     * Returns Similarity between two users. All the checks about which
     * similarity function should be used, should handle internally
     * @param userId1 Given user id
     * @param userId2 Given user id
     * @return Similarity between two users
     */
    Float getUserSimilarity(int userId1, int userId2);
}
