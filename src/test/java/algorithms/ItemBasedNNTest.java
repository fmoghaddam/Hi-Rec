package algorithms;

import static org.junit.Assert.*;


import org.junit.Test;

import controller.similarity.LowLevelSimilarityRepository;
import it.unimi.dsi.fastutil.ints.Int2FloatLinkedOpenHashMap;
import metrics.TestDataGenerator;
import model.DataModel;

/**
 * Test class for {@link ItemBasedNN}
 * @author FBM
 *
 */
public class ItemBasedNNTest {

    @Test
    public
            void test() {
        final DataModel trainData = TestDataGenerator.getTrainData();
        final ItemBasedNN algorithm = new ItemBasedNN();
        algorithm.setNumberOfNeighbours(10);
        final LowLevelSimilarityRepository similarityRepository = new LowLevelSimilarityRepository(trainData);
        algorithm.setSimilarityRepository(similarityRepository);
        algorithm.train(trainData);
        final Int2FloatLinkedOpenHashMap recommendItems = algorithm.recommendItems(TestDataGenerator.getUser1());
        assertEquals(4, recommendItems.size());
    }

}
