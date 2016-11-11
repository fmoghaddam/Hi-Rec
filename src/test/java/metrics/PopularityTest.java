/**
 * 
 */
package metrics;

import static org.junit.Assert.*;

import org.junit.Test;

import model.DataModel;
import model.User;

/**
 * Test class for {@link Popularity}
 * @author FBM
 *
 */
public class PopularityTest {

    @Test
    public
            void test() {
        final User user = TestDataGenerator.createUser();
        final DataModel trainData = TestDataGenerator.getTrainData();
        final Popularity popularity = new Popularity();
        popularity.setTrainData(trainData);
        popularity.addRecommendations(user, TestDataGenerator.generateList1());
        assertEquals(0.333333, popularity.getEvaluationResult(), 0.001);
        
    }

}
