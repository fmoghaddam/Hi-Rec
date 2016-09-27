package controller.similarity;

import java.util.Set;

import org.apache.log4j.Logger;

import interfaces.SimilarityInterface;
import model.DataModel;
import model.Globals;

/**
 * Calculate tag similarity between items on demand
 * 
 * @author FBM
 *
 */
public final class TagSimilarityRepository
        implements SimilarityInterface
{

    /**
     * Logger for this class
     */
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(TagSimilarityRepository.class.getCanonicalName());
    /**
     * Train data
     */
    private final DataModel dataModel;

    /**
     * Constructor
     * 
     * @param dataModel
     */
    public TagSimilarityRepository(
            final DataModel dataModel)
    {
        this.dataModel = dataModel;
    }

    /*
     * @see interfaces.SimilarityInterface#getItemSimilairty(int, int)
     */
    @Override
    public
            Float getItemSimilairty(
                    final int itemId1, final int itemId2)
    {
        switch (Globals.SIMILAIRTY_FUNCTION) {
        case COSINE:
            return calculateItemCosineSimilarity(itemId1, itemId2);
        case PEARSON:
            throw new UnsupportedOperationException(
                    "Pearson correlation not impleneted for tags. As it needs more effors so far not implemented.");
        default:
            return calculateItemCosineSimilarity(itemId1, itemId2);
        }
    }

    /**
     * Calculate Cosine similarity between two items
     * 
     * @param itemId1
     * @param itemId2
     * @return Cosine similarity of two items if they exist in train dataset,
     *         O.W. NaN
     */
    private
            Float calculateItemCosineSimilarity(
                    final int itemId1, final int itemId2)
    {
        if (this.dataModel.getItem(itemId1) != null
                && this.dataModel.getItem(itemId2) != null)
        {

            final Set<String> item1List = this.dataModel.getItem(itemId1)
                    .getTags();
            final Set<String> item2List = this.dataModel.getItem(itemId2)
                    .getTags();

            float dotProduct = 0;

            if (item1List.size() < item2List.size()) {
                for (String tag: item1List) {
                    if (item2List.contains(tag)) {
                        dotProduct += 1;
                    }
                }
            } else {
                for (String tag: item2List) {
                    if (item1List.contains(tag)) {
                        dotProduct += 1;
                    }
                }
            }
            if (dotProduct == 0) {
                return Float.NaN;
            }
            return (float)((dotProduct / (Math.sqrt(item1List.size())
                    * Math.sqrt(item2List.size()))));
        } else {
            return Float.NaN;
        }
    }

}
