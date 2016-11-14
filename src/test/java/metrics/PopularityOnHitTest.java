/**
 * 
 */
package metrics;

import static org.junit.Assert.*;

import org.junit.Test;

import model.DataModel;
import model.User;

/**
 * Test class for {@link PopularityOnHit}
 * @author FBM
 *
 */
final public class PopularityOnHitTest {

    @Test
    public
            void test() {
        final User user = TestDataGenerator.getUser1();
        final DataModel trainData = TestDataGenerator.getTrainData();
        final PopularityOnHit popularity = new PopularityOnHit();
        popularity.setTrainData(trainData);
        popularity.addRecommendations(user, TestDataGenerator.generateList1());
        assertEquals(0.333333, popularity.getEvaluationResult(), 0.001);
        
    }

}
