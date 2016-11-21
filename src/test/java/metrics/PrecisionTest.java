package metrics;

import static org.junit.Assert.*;

import org.junit.Test;

import model.User;

/**
 * Test class for {@link Precision}
 * 
 * @author FBM
 *
 */
public final class PrecisionTest {

    @Test
    public
            void precisionShouldReturnCorrectValue() {
        final User user = TestDataGenerator.getUser1();
        final Precision precision = new Precision();
        precision.addRecommendations(user, TestDataGenerator.generateList1());
        assertEquals(0.3, precision.getEvaluationResult(), 0.01);

        precision.addRecommendations(user, TestDataGenerator.generateList2());
        assertEquals(0.35, precision.getEvaluationResult(), 0.01);
    }

}
