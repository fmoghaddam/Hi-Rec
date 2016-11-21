/**
 * 
 */
package controller.similarity;

import static org.junit.Assert.*;

import org.junit.Test;

import metrics.TestDataGenerator;
import model.DataModel;

/**
 * Test class for {@link LowLevelSimilarityRepository}
 * 
 * @author FBM
 *
 */
public class LowLevelSimilarityRepositoryTest {

    @Test
    public
            void calculateCosineSimilairtyBasedOnLowLevelShouldWorkProperly() {
        final DataModel trainData = TestDataGenerator.getTrainData();
        final LowLevelSimilarityRepository lowLevelSimilarityRepository = new LowLevelSimilarityRepository(
                trainData);
        assertEquals(1, lowLevelSimilarityRepository.getItemSimilairty(1, 2),
                0.00001);
        assertEquals(1, lowLevelSimilarityRepository.getItemSimilairty(3, 11),
                0.00001);
        assertEquals(0.8944271802902222,
                lowLevelSimilarityRepository.getItemSimilairty(1, 3), 0.00001);
        assertEquals(Float.NaN,
                lowLevelSimilarityRepository.getItemSimilairty(1, 20), 0.00001);
    }

}
