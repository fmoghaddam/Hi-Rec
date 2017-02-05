package algorithms;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import algorithms.implicitdatamatrixfactorization.AlternatingLeastSquareLearner;
import gui.model.LearningMethod;
import interfaces.AbstractRecommender;
import interfaces.Learner;
import model.DataModel;
import model.Item;
import model.User;
import util.MapUtil;

/**
 * @author Farshad Moghaddam
 *
 */
public class ImplicitDataMatrixFactorization
        extends AbstractRecommender
{

	private static final Logger LOG = Logger.getLogger(ImplicitDataMatrixFactorization.class.getCanonicalName()); 
    private static final long serialVersionUID = 348789281670228689L;
    private transient Learner learner = null;
    private LearningMethod learningMethod = LearningMethod.ALS;
    /**
     * Number of features
     */
    private int numberOfFeatures;
    /**
     * Number of iteration
     */
    private int numberOfIteration;
    /**
     * learning rate
     */
    private double regularizationCoefficient;

    public ImplicitDataMatrixFactorization() {
        final HashMap<String, String> h1 = new HashMap<>();
        h1.put("NUMBER_OF_FEATURES", "Number of latent factor");
        this.configurableParametersMap.put("numberOfFeatures", h1);

        final HashMap<String, String> h2 = new HashMap<>();
        h2.put("NUMBER_OF_ITERATION", "Number of iteration");
        this.configurableParametersMap.put("numberOfIteration", h2);

        final HashMap<String, String> h3 = new HashMap<>();
        h3.put("REGULARIZATION_COEFFICIENT", "Regularization Coefficient");
        this.configurableParametersMap.put("regularizationCoefficient", h3);
    }

    /*
     * (non-Javadoc)
     * 
     * @see interfaces.AbstractRecommender#predictRating(model.User, model.Item)
     */
    @Override
    public
            Float predictRating(
                    User user, Item item)
    {
        return learner.getResult(user.getId(), item.getId());        
    }

    /*
     * (non-Javadoc)
     * 
     * @see interfaces.AbstractRecommender#recommendItems(model.User)
     */
    @Override
    public
            Map<Integer, Float> recommendItems(
                    User user)
    {
    	if (user == null) {
            throw new IllegalArgumentException("User is null");
        }
        final Map<Integer, Float> predictions = new LinkedHashMap<Integer, Float>();

        for (final Item item: trainDataModel.getItems().values()) {
            final int itemId = item.getId();
            final float predictRating = predictRating(user, item);
            if (!Float.isNaN(predictRating)) {
                predictions.put(itemId, predictRating);
            }
        }
        final Map<Integer, Float> sortByComparator = MapUtil
                .sortByValueDescending(predictions);
        return sortByComparator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see interfaces.AbstractRecommender#train(model.DataModel)
     */
    @Override
    public
            void train(
                    DataModel trainData)
    {
        if (trainData == null) {
            throw new IllegalArgumentException("Train data is null");
        }
        this.trainDataModel = trainData;
        switch (learningMethod) {
        case SGD:
        	LOG.error("SGD is not supported yet");
            break;
        case ALS:
            learner = new AlternatingLeastSquareLearner(trainData, regularizationCoefficient,
                    numberOfFeatures, numberOfIteration);
            ((AlternatingLeastSquareLearner)learner).learn();
            break;
        default:
            break;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see interfaces.AbstractRecommender#isSimilairtyNeeded()
     */
    @Override
    public
            boolean isSimilairtyNeeded() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public
            String toString() {
        return "ImplicitDataMatrixFactorization";
    }

    /**
     * @return the numberOfFeatures
     */
    public final
            int getNumberOfFeatures() {
        return numberOfFeatures;
    }

    /**
     * @param numberOfFeatures
     *            the numberOfFeatures to set
     */
    public final
            void setNumberOfFeatures(
                    int numberOfFeatures)
    {
        this.numberOfFeatures = numberOfFeatures;
    }

    /**
     * @return the numberOfIteration
     */
    public final
            int getNumberOfIteration() {
        return numberOfIteration;
    }

    /**
     * @param numberOfIteration
     *            the numberOfIteration to set
     */
    public final
            void setNumberOfIteration(
                    int numberOfIteration)
    {
        this.numberOfIteration = numberOfIteration;
    }

    /**
     * @return the learningRate
     */
    public final
            double getLearningRate() {
        return regularizationCoefficient;
    }

    /**
     * @param learningRate
     *            the learningRate to set
     */
    public final
            void setLearningRate(
                    double learningRate)
    {
        this.regularizationCoefficient = learningRate;
    }
}
