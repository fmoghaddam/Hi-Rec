/**
 * 
 */
package controller.similarity;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.log4j.Logger;

import interfaces.SimilarityInterface;
import model.DataModel;
import model.Globals;

/**
 * Calculate LowLevel feature + Genre similarity between items on demand This is
 * implemented for special use cases
 * 
 * @author FBM
 *
 */
public final class LowLevelGenreSimilarityRepository
        implements SimilarityInterface
{

    /**
     * Logger used for this class
     */
    @SuppressWarnings("unused")
    private Logger LOG = Logger.getLogger(
            LowLevelGenreSimilarityRepository.class.getCanonicalName());
    /**
     * Train data
     */
    private final DataModel dataModel;

    /**
     * Constructor
     * 
     * @param dataModel
     */
    public LowLevelGenreSimilarityRepository(
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
                    int itemId1, int itemId2)
    {
        switch (Globals.SIMILAIRTY_FUNCTION) {
        case COSINE:
            return calculateItemCosineSimilarity(itemId1, itemId2);
        case PEARSON:
            return calculateItemPearsonSimilarity(itemId1, itemId2);
        default:
            return calculateItemCosineSimilarity(itemId1, itemId2);
        }
    }

    /**
     * Calculate Pearson correlation between two items
     * 
     * @param itemId1
     * @param itemId2
     * @return Pearson correlation of two items if they exist in train dataset,
     *         O.W. NaN
     */
    private
            Float calculateItemPearsonSimilarity(
                    int itemId1, int itemId2)
    {
        if (this.dataModel.getItem(itemId1) != null
                && this.dataModel.getItem(itemId2) != null)
        {

            final double[] lowLevelFeatureAsArray1 = dataModel.getItem(itemId1)
                    .getLowLevelFeatureAsArray();
            final double[] genresAsArray1 = dataModel.getItem(itemId1)
                    .getGenresAsArray();
            final double item1Array[] = new double[lowLevelFeatureAsArray1.length
                    + genresAsArray1.length];
            System.arraycopy(lowLevelFeatureAsArray1, 0, item1Array, 0,
                    lowLevelFeatureAsArray1.length);
            System.arraycopy(genresAsArray1, 0, item1Array,
                    lowLevelFeatureAsArray1.length, genresAsArray1.length);

            final double[] lowLevelFeatureAsArray2 = dataModel.getItem(itemId2)
                    .getLowLevelFeatureAsArray();
            final double[] genresAsArray2 = dataModel.getItem(itemId2)
                    .getGenresAsArray();
            final double item2Array[] = new double[lowLevelFeatureAsArray2.length
                    + genresAsArray2.length];
            System.arraycopy(lowLevelFeatureAsArray2, 0, item2Array, 0,
                    lowLevelFeatureAsArray2.length);
            System.arraycopy(genresAsArray2, 0, item2Array,
                    lowLevelFeatureAsArray2.length, genresAsArray2.length);

            return (float)new PearsonsCorrelation().correlation(item1Array,
                    item2Array);
        } else {
            return Float.NaN;
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

            final double[] lowLevelFeatureAsArray1 = dataModel.getItem(itemId1)
                    .getLowLevelFeatureAsArray();
            final double[] genresAsArray1 = dataModel.getItem(itemId1)
                    .getGenresAsArray();
            final double item1Array[] = new double[lowLevelFeatureAsArray1.length
                    + genresAsArray1.length];
            System.arraycopy(lowLevelFeatureAsArray1, 0, item1Array, 0,
                    lowLevelFeatureAsArray1.length);
            System.arraycopy(genresAsArray1, 0, item1Array,
                    lowLevelFeatureAsArray1.length, genresAsArray1.length);

            final double[] lowLevelFeatureAsArray2 = dataModel.getItem(itemId2)
                    .getLowLevelFeatureAsArray();
            final double[] genresAsArray2 = dataModel.getItem(itemId2)
                    .getGenresAsArray();
            final double item2Array[] = new double[lowLevelFeatureAsArray2.length
                    + genresAsArray2.length];
            System.arraycopy(lowLevelFeatureAsArray2, 0, item2Array, 0,
                    lowLevelFeatureAsArray2.length);
            System.arraycopy(genresAsArray2, 0, item2Array,
                    lowLevelFeatureAsArray2.length, genresAsArray2.length);

            float dotProduct = 0;
            float normA = 0;
            float normB = 0;
            for (int i = 0; i < item1Array.length; i++) {
                dotProduct += item1Array[i] * item2Array[i];
                normA += item1Array[i] * item1Array[i];
                normB += item2Array[i] * item2Array[i];
            }
            if (dotProduct == 0) {
                return Float.NaN;
            }
            return (float)(dotProduct / (Math.sqrt(normA) * Math.sqrt(normB)));

        } else {
            return Float.NaN;
        }
    }

}
