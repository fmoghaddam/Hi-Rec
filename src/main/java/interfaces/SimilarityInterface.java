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
}
