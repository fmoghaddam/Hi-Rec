/**
 * 
 */
package metrics;

import static org.junit.Assert.*;

import org.junit.Test;

import model.User;

/**
 * Test class for {@link MAP}
 * 
 * @author FBM
 *
 */
public final class MAPTest {

    @Test
    public
            void mapShouldReturnACorrectValue() {
        final User user = TestDataGenerator.getUser1();
        final MAP map = new MAP();

        map.addRecommendations(user, TestDataGenerator.generateList1());
        assertEquals(0.698, map.getEvaluationResult(), 0.001);

        map.addRecommendations(user, TestDataGenerator.generateList2());
        assertEquals(0.73, map.getEvaluationResult(), 0.01);
    }
}
