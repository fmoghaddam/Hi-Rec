package metrics;

import static org.junit.Assert.*;

import org.junit.Test;

import model.Rating;

/**
 * Test class for {@link MAE}
 * 
 * @author FBM
 *
 */
public final class MAETest {

    @Test
    public
            void test() {
        Rating rating = new Rating(1, 1, 5);
        final MAE mae = new MAE();
        mae.addTestPrediction(rating, 5);
        assertEquals(0, mae.getPredictionAccuracy(), 0.001);

        mae.addTestPrediction(rating, 0);
        assertEquals(2.5, mae.getPredictionAccuracy(), 0.001);

        mae.addTestPrediction(rating, 2);
        assertEquals(2.666, mae.getPredictionAccuracy(), 0.001);

    }

}
