package metrics;

import static org.junit.Assert.*;

import org.junit.Test;
import model.DataModel;
import model.User;

/**
 * Test class for {@link Novelty}
 * 
 * @author FBM
 *
 */
public final class NoveltyTest {

    @Test
    public
            void test() {
        final User user = TestDataGenerator.getUser1();
        final DataModel trainData = TestDataGenerator.getTrainData();
        final Novelty novelty = new Novelty();
        novelty.setTrainData(trainData);

        novelty.addRecommendations(user, TestDataGenerator.generateList1());
        assertEquals(1.584, novelty.getEvaluationResult(), 0.001);

        novelty.addRecommendations(user, TestDataGenerator.generateList2());
        assertEquals(1.584, novelty.getEvaluationResult(), 0.001);
    }

}
