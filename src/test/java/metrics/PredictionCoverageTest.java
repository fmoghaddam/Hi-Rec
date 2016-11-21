package metrics;

import static org.junit.Assert.*;

import org.junit.Test;

import model.Rating;

/**
 * Test class for {@link PredictionCoverage}
 * 
 * @author FBM
 *
 */
public final class PredictionCoverageTest {

    @Test
    public
            void test() {
        final PredictionCoverage predictionCoverage = new PredictionCoverage();

        final Rating rating = new Rating(1, 1, 5);

        predictionCoverage.addTestPrediction(rating, 4);
        assertEquals(1, predictionCoverage.getPredictionAccuracy(), 0.001);

        predictionCoverage.addTestPrediction(rating, 4);
        assertEquals(1, predictionCoverage.getPredictionAccuracy(), 0.001);

        predictionCoverage.addTestPrediction(rating, Float.NaN);
        assertEquals(0.666, predictionCoverage.getPredictionAccuracy(), 0.001);

        predictionCoverage.addTestPrediction(rating, Float.NaN);
        assertEquals(0.5, predictionCoverage.getPredictionAccuracy(), 0.001);
    }
}
