package metrics;

import static org.junit.Assert.*;

import org.apache.log4j.NDC;
import org.junit.Test;

import model.User;

/**
 * Test class for {@link NDC}
 * @author FBM
 *
 */
public final class NDCGTest {

    @Test
    public
            void ndcgShoudlReturnCorrectValue() {
        final User user = TestDataGenerator.createUser();
        final NDCG ndcg = new NDCG();
        ndcg.addRecommendations(user, TestDataGenerator.generateList1());
        assertEquals(0.715, ndcg.getEvaluationResult(), 0.001);

        ndcg.addRecommendations(user, TestDataGenerator.generateList2());
        assertEquals(0.81, ndcg.getEvaluationResult(), 0.01);
    }

}
