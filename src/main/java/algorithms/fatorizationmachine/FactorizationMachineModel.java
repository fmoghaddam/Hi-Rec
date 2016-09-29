package algorithms.fatorizationmachine;

import model.DataModel;
import model.Globals;
import model.Item;
import model.Rating;
import run.Configuration;
import util.ArrayUtil;

/**
 * This is Factorization Machine model which is responsible
 * for all Factorization machine calculations 
 * 
 * @author FBM
 */
public final class FactorizationMachineModel {
    /**
     * Number of features
     */
    public final int k = Globals.NUMBER_OF_FEATURES_FOR_FM;

    public float w0;
    public float[] w;
    public float[][] v;
    public int numberOfItems = (int) Globals.MAX_ID_OF_ITEMS;
    public int numberOfUsers = (int) Globals.MAX_ID_OF_UESRS;
    private int numberOfFeatures = 0;

    private DataModel trainDataModel;
    private Configuration configuration;

    public FactorizationMachineModel(final DataModel dataModel, final Configuration configuration) {
	if (dataModel == null) {
	    throw new IllegalArgumentException("DataModel is null");
	}
	if (configuration == null) {
	    throw new IllegalArgumentException("Configuration is null");
	}
	this.trainDataModel = dataModel;
	this.configuration = configuration;
	this.initialize();
    }

    private void initialize() {
	if (configuration.isUseLowLevel() && configuration.isUseGenre()) {
	    numberOfFeatures = this.trainDataModel.getItems().entrySet().iterator().next().getValue()
		    .getLowLevelFeature().size()
		    + this.trainDataModel.getItems().entrySet().iterator().next().getValue().getGenres().size();
	} else if (configuration.isUseLowLevel()) {
	    numberOfFeatures = this.trainDataModel.getItems().entrySet().iterator().next().getValue()
		    .getLowLevelFeature().size();
	} else if (configuration.isUseGenre()) {
	    numberOfFeatures = this.trainDataModel.getItems().entrySet().iterator().next().getValue().getGenres()
		    .size();
	} else if (configuration.isUseRating()) {
	    numberOfFeatures = 0;
	} else if (configuration.isUseTag()) {
	    throw new UnsupportedOperationException("Factorization machine for tag still not implemented");
	}
	this.w0 = 0;
	this.w = generateWVector();
	this.v = generateVMatrix();
    }

    private float[][] generateVMatrix() {
	float[][] v = new float[(int) (this.numberOfUsers + this.numberOfItems + this.numberOfFeatures)][k];
	for (int i = 0; i < v.length; i++) {
	    for (int j = 0; j < k; j++) {
		v[i][j] = (float) util.StaticFunctions.ran_gaussian(0, 0.1);
	    }
	}
	return v;
    }

    private float[] generateWVector() {
	float[] w = new float[(int) (this.numberOfUsers + this.numberOfItems + this.numberOfFeatures)];
	for (int i = 0; i < w.length; i++) {
	    w[i] = (float) util.StaticFunctions.ran_gaussian(0, 0.1);
	}

	return w;
    }

    public float calculate(final Rating rating) {
	if(rating == null){
	    throw new IllegalArgumentException("Rating is null");
	}
	final float firstPart = w0;
	final float secondPrat = calculateSecondPart(rating);
	final float thirdPrat = calculateThirdPart(rating);
	final float result = firstPart + secondPrat + thirdPrat;
	// + regularization(rating);
	if (result > Globals.MAX_RATING) {
	    return Globals.MAX_RATING;
	} else if (result < Globals.MIN_RATING) {
	    return Globals.MIN_RATING;
	} else {
	    return result;
	}
    }

    /**
     * @param rating
     * @return
     */
    @SuppressWarnings("unused")
    // TODO: Should be implemented
    private float regularization(Rating rating) {
	if(rating == null){
	    throw new IllegalArgumentException("Rating is null");
	}
	final int userId = rating.getUserId() - 1;
	final int itemId = rating.getItemId() - 1;
	double sum1 = 0;
	double sum2 = 0;
	double sum3 = w[userId] * w[userId] + w[itemId] * w[itemId];
	for (int i = 0; i < k; i++) {
	    sum1 += v[userId][i] * v[userId][i];
	    sum2 += v[itemId][i] * v[itemId][i];
	}
	final double lambda = 2;
	return (float) (lambda * (sum1 + sum2 + sum3));
    }

    private float calculateThirdPart(final Rating rating) {
	if(rating == null){
	    throw new IllegalArgumentException("Rating is null");
	}
	/**
	 * If item does not exist in train dataset then return NaN
	 */
	final Item item = this.trainDataModel.getItem(rating.getItemId());
	if (item == null) {
	    return Float.NaN;
	}

	float sum = 0;
	for (int f = 0; f < k; f++) {
	    float firstSum = 0;
	    int userValue = rating.getUserId() - 1;
	    int itemValue = rating.getItemId() - 1;

	    firstSum += v[userValue][f];
	    firstSum += v[userValue+itemValue+1][f];

	    final double[] featureAsArray = getFatureArray(item);
	    for (int i = (this.numberOfUsers + this.numberOfItems); i < v.length; i++) {
		final int j = (int) (i - (this.numberOfUsers + this.numberOfItems));
		firstSum += v[i][f] * featureAsArray[j];
	    }

	    firstSum = firstSum * firstSum;

	    float secondSum = 0;
	    secondSum += v[userValue][f] * v[userValue][f];
	    secondSum += v[userValue+itemValue+1][f] * v[userValue+itemValue+1][f];

	    for (int i = (this.numberOfUsers + this.numberOfItems); i < v.length; i++) {
		final int j = (int) (i - (this.numberOfUsers + this.numberOfItems));
		final double featureValue = featureAsArray[j];
		secondSum += (v[i][f] * v[i][f]) * (featureValue * featureValue);
	    }

	    sum += (firstSum - secondSum);
	}
	return sum / 2;
    }

    private float calculateSecondPart(final Rating rating) {
	if(rating == null){
	    throw new IllegalArgumentException("Rating is null");
	}
	/**
	 * If item does not exist in train set then return NaN
	 */
	final Item item = this.trainDataModel.getItem(rating.getItemId());
	if (item == null) {
	    return Float.NaN;
	}

	final float userValue = w[rating.getUserId() - 1];
	final float itemValue = w[this.numberOfUsers + rating.getItemId() - 1];

	float sum = userValue + itemValue;
	
	final double[] featureAsArray = getFatureArray(item);
	for (int i = (this.numberOfUsers + this.numberOfItems); i < w.length; i++) {
	    final int j = (int) (i - (this.numberOfUsers + this.numberOfItems));
	    sum += w[i] * featureAsArray[j];
	}
	return sum;
    }

    @Deprecated
    public double convertToVecor(final Rating rating, final int i) {
	if(rating == null){
	    throw new IllegalArgumentException("Rating is null");
	}
	if (rating.getUserId() - 1 == i || (this.numberOfUsers + rating.getItemId()) - 1 == i) {
	    return 1;
	} else if (i > (this.numberOfUsers + this.numberOfItems) - 1) {
	    final int j = (int) (i - (this.numberOfUsers + this.numberOfItems));
	    return this.trainDataModel.getItem(rating.getItemId()).getLowLevelFeatureAsArray()[j];
	} else {
	    return 0;
	}
    }

    /**
     * Returns training data model
     * @return
     */
    public DataModel getDataModel() {
	return this.trainDataModel;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public
            String toString() {
        return "FactorizationMachineModel [numberOfFeatures=" + numberOfFeatures
                + ", Learning rate=" + Globals.LEARNING_RATE_FOR_FM
                + "]";
    }
    
    public double[] getFatureArray(final Item item){
        final double[] featureAsArray;
        if (configuration.isUseLowLevel() && configuration.isUseGenre()) {
            featureAsArray = ArrayUtil.concatAll(item.getLowLevelFeatureAsArray(), item.getGenresAsArray());
        } else if (configuration.isUseLowLevel()) {
            featureAsArray = item.getLowLevelFeatureAsArray();
        } else if (configuration.isUseGenre()) {
            featureAsArray = item.getGenresAsArray();
        }else{
            featureAsArray = new double[0];
        }
        return featureAsArray; 
    }
    
}
