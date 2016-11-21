package metrics;

import static org.junit.Assert.*;

import org.junit.Test;

import model.User;

/**
 * Test class for {@link Recall}
 * 
 * @author FBM
 *
 */
public final class RecallTest {

    @Test
    public
            void recallShouldReturnCurrectValue() {
        final User user = TestDataGenerator.getUser1();
        final Recall recall = new Recall();
        recall.addRecommendations(user, TestDataGenerator.generateList1());
        assertEquals(0.75, recall.getEvaluationResult(), 0.01);

        recall.addRecommendations(user, TestDataGenerator.generateList2());
        assertEquals(0.875, recall.getEvaluationResult(), 0.01);
    }

}
