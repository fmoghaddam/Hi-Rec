package metrics;

import static org.junit.Assert.*;

import org.junit.Test;

import model.Rating;

/**
 * Test class for {@link PredictionCoverageOnlyPositive}
 * 
 * @author FBM
 *
 */
public final class PredictionCoverageOnlyPositiveTest {

    @Test
    public
            void test() {
        final PredictionCoverageOnlyPositive predictionCoverage = new PredictionCoverageOnlyPositive();

        final Rating negativeRating = new Rating(1, 1, 1);
        final Rating positiveRating = new Rating(1, 1, 5);

        predictionCoverage.addTestPrediction(positiveRating, 4);
        assertEquals(1, predictionCoverage.getPredictionAccuracy(), 0.001);

        predictionCoverage.addTestPrediction(positiveRating, 4);
        assertEquals(1, predictionCoverage.getPredictionAccuracy(), 0.001);

        predictionCoverage.addTestPrediction(positiveRating, Float.NaN);
        assertEquals(0.666, predictionCoverage.getPredictionAccuracy(), 0.001);

        predictionCoverage.addTestPrediction(positiveRating, Float.NaN);
        assertEquals(0.5, predictionCoverage.getPredictionAccuracy(), 0.001);

        predictionCoverage.addTestPrediction(negativeRating, Float.NaN);
        assertEquals(0.5, predictionCoverage.getPredictionAccuracy(), 0.001);

    }
}
