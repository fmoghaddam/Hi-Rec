package metrics;

import static org.junit.Assert.*;

import org.junit.Test;

import model.Rating;

/**
 * Test class for {@link RMSE}
 * 
 * @author FBM
 *
 */
public final class RMSETest {

    @Test
    public
            void test() {
        Rating rating = new Rating(1, 1, 5);
        final RMSE rmse = new RMSE();
        rmse.addTestPrediction(rating, 5);
        assertEquals(0, rmse.getPredictionAccuracy(), 0.001);

        rmse.addTestPrediction(rating, 0);
        assertEquals(3.535, rmse.getPredictionAccuracy(), 0.001);

        rmse.addTestPrediction(rating, 2);
        assertEquals(3.366, rmse.getPredictionAccuracy(), 0.001);

    }

}
