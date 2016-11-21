/**
 * 
 */
package controller.similarity;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import metrics.TestDataGenerator;
import model.DataModel;

/**
 * Test class for {@link TagSimilarityRepository}
 * @author FBM
 *
 */
public class TagSimilarityRepositoryTest {

    @Test
    public
            void calculateCosineSimilairtyBasedOnTagShouldWorkProperly() {
        final DataModel trainData = TestDataGenerator.getTrainData();
        trainData.getItem(1).setTags(new ObjectOpenHashSet<String>(Arrays.asList("tag1", "tag2","tag10", "tag21")));
        trainData.getItem(2).setTags(new ObjectOpenHashSet<String>(Arrays.asList("tag1", "tag2")));
        trainData.getItem(3).setTags(new ObjectOpenHashSet<String>(Arrays.asList("tag1")));
        trainData.getItem(11).setTags(new ObjectOpenHashSet<String>(Arrays.asList("tag4", "tag5")));
        
        final TagSimilarityRepository tagSimilarityRepository = new TagSimilarityRepository(
                trainData);
        assertEquals(1, tagSimilarityRepository.getItemSimilairty(1, 1),0.00001);
        assertEquals(0.5, tagSimilarityRepository.getItemSimilairty(1, 3),0.00001);
        assertEquals(Float.NaN, tagSimilarityRepository.getItemSimilairty(1, 11),0.00001);
    }

}
