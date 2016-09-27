package algorithms.fatorizationmachine;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import model.DataModel;
import model.Globals;
import model.Rating;

/**
 * This class is responsible for running StochasticGradient Descent
 * 
 * @author FBM
 *
 */
public final class SGDLearner {

    /**
     * Logger for this class
     */
    private static final Logger LOG = Logger.getLogger(SGDLearner.class.getCanonicalName());
    /**
     * Number of iteration for stopping learning
     */
    private final int numberOfIteration = Globals.NUMBER_OF_ITERATION_FOR_FM;
    /**
     * Learning rate
     */
    private final float learnRates = Globals.LEARNING_RATE_FOR_FM;
    /**
     * Train data
     */
    private DataModel trainDataModel;

    public SGDLearner() {
    }

    /**
     * run the SGD learning
     * @param FMModel model which is used for learning
     */
    public void learn(FactorizationMachineModel FMModel) {
	if(FMModel == null){
	    throw new IllegalArgumentException("FactorizationMachineModel is null");
	}
	trainDataModel = FMModel.getDataModel();
	// final List<Float> errors = new ArrayList<>();
	for (int iterate = 0; iterate < numberOfIteration; iterate++) {
	    final int dataSize = this.trainDataModel.getRatings().size();
	    for (int dataIndex = 0; dataIndex < dataSize; dataIndex++) {
		final Rating rating = this.trainDataModel.getRatings().get(dataIndex);
		final float prediction = FMModel.calculate(rating);
		final float error = rating.getRating() - prediction;
		final float firstPart = learnRates * error;

		FMModel.w0 += firstPart;
		final int userValue = rating.getUserId() - 1;
		final int itemValue = rating.getItemId() - 1;
		FMModel.w[userValue] = FMModel.w[userValue] + firstPart;
		FMModel.w[itemValue] = FMModel.w[itemValue] + firstPart;
		for (int i = (FMModel.numberOfUsers + FMModel.numberOfItems); i < FMModel.w.length; i++) {
		    FMModel.w[i] += (float) (firstPart * this.trainDataModel.getItem(rating.getItemId())
			    .getLowLevelFeatureAsArray()[(int) (i - (FMModel.numberOfUsers + FMModel.numberOfItems))]);
		}

		Float fixedPart;

		for (int f = 0; f < FMModel.k; f++) {
		    fixedPart = fixPart(FMModel, f, rating);
		    FMModel.v[userValue][f] += firstPart * (fixedPart - FMModel.v[userValue][f]);
		    FMModel.v[itemValue][f] += firstPart * (fixedPart - FMModel.v[itemValue][f]);
		    for (int i = (FMModel.numberOfUsers + FMModel.numberOfItems); i < FMModel.v.length; i++) {
			final float xValue = (float) this.trainDataModel.getItem(rating.getItemId())
				.getLowLevelFeatureAsArray()[(int) (i
					- (FMModel.numberOfUsers + FMModel.numberOfItems))];
			FMModel.v[i][f] += firstPart * (xValue * fixedPart - FMModel.v[i][f] * (xValue * xValue));
		    }
		}
	    }
	    // Calculate Error
	    // float errorSum=0;
	    // //for (int dataIndex = 0; dataIndex < dataSize; dataIndex++) {
	    // for (int dataIndex = 0; dataIndex < 10000; dataIndex++) {
	    // final Rating rating =
	    // this.trainDataModel.getRatings().get(dataIndex);
	    // final float prediction = FMModel.calculate(rating);
	    // final float error = rating.getRating() - prediction;
	    // errorSum+=Math.abs(error);
	    // }

	    // LOG.info(errorSum);
	    // errors.add(errorSum/dataSize);
	}

	// analyseError(errors);
	// (new ErrorChart("test", errors)).draw();
    }

    /**
     * @param errors
     */
    @SuppressWarnings("unused")
    private void analyseError(List<Float> errors) {
	if(errors == null){
	    throw new IllegalArgumentException("Error list is null");
	}
	final Float min = Collections.min(errors);
	LOG.info("\n" + min);
	int counter = 0;
	for (float f : errors) {
	    if (f == min) {
		LOG.info("INDEX: " + counter);
	    }
	    counter++;
	}
    }

    /**
     * Calculate fix part of Factorization machine algorithm.
     * for more information please check the original paper, page 3, first line
     * after Eq 4.
     * @param FMModel
     * @param f
     * @param rating
     * @return
     */
    private float fixPart(final FactorizationMachineModel FMModel,final int f,final Rating rating) {
	if(FMModel == null){
	    throw new IllegalArgumentException("FactorizationMachineModel list is null");
	}
	if(rating == null){
	    throw new IllegalArgumentException("rating list is null");
	}
	float sum = 0;

	int userValue = rating.getUserId() - 1;
	int itemValue = rating.getItemId() - 1;
	sum += FMModel.v[userValue][f];
	sum += FMModel.v[itemValue][f];

	for (int j = (FMModel.numberOfUsers + FMModel.numberOfItems); j < FMModel.v.length; j++) {
	    sum += FMModel.v[j][f] * this.trainDataModel.getItem(rating.getItemId())
		    .getLowLevelFeatureAsArray()[(int) (j - (FMModel.numberOfUsers + FMModel.numberOfItems))];
	}
	return sum;
    }
}
