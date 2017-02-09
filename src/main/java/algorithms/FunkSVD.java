package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import algorithms.funksvd.GradientDescentSVDLearner;
import algorithms.implicitdatamatrixfactorization.AlternatingLeastSquareLearner;
import gui.model.LearningMethod;
import interfaces.AbstractRecommender;
import interfaces.Learner;
import model.DataModel;
import model.Item;
import model.Rating;
import model.User;
import util.MapUtil;
import util.StatisticFunctions;

/**
 * This is FunkSVD algorithm. http://sifter.org/~simon/journal/20061211.html The
 * code based is from Recommneder101:
 * http://ls13-www.cs.tu-dortmund.de/homepage/recommender101/index.shtml
 */
public final class FunkSVD
        extends AbstractRecommender
{

    /**
     * Unique id used for serialization
     */
    private static final long serialVersionUID = 7178521880514362760L;
    /**
     * Logger for this class
     */
    private static final Logger LOG = Logger
            .getLogger(AveragePopularity.class.getCanonicalName());
    /**
     * Number of features
     */
    private int numberOfFeatures;
    /**
     * Number of iteration
     */
    private int numberOfIteration;
    /**
     * learning rate used in SGD
     */
    private double learningRate;
    private Map<Integer, Integer> userMap = new LinkedHashMap<>();
    private Map<Integer, Integer> itemMap = new LinkedHashMap<>();
    private transient Learner learner = null;
    private LearningMethod learningMethod = LearningMethod.SGD;
    private transient List<Rating> cachedPreferences = null;

    /**
     * Calculate the user averages
     */
    private final Map<Integer, Float> perUserAverage = new LinkedHashMap<Integer, Float>();

    /**
     * 
     */
    public FunkSVD() {
        final HashMap<String, String> h1 = new HashMap<>();
        h1.put("NUMBER_OF_FEATURES_FOR_FUNKSVD", "Number of latent factor");
        this.configurableParametersMap.put("numberOfFeatures", h1);

        final HashMap<String, String> h2 = new HashMap<>();
        h2.put("NUMBER_OF_ITERATION_FOR_FUNKSVD", "Number of iteration");
        this.configurableParametersMap.put("numberOfIteration", h2);

        final HashMap<String, String> h3 = new HashMap<>();
        h3.put("LEARNING_RATE_FOR_FUNKSVD", "Learning rate");
        this.configurableParametersMap.put("learningRate", h3);               
    }

    /**
     * @return the numberOfFeatures
     */
    public final
            int getNumberOfFeatures() {
        return numberOfFeatures;
    }

    /**
     * @param numberOfFeatures the numberOfFeatures to set
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
     * @param numberOfIteration the numberOfIteration to set
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
        return learningRate;
    }

    /**
     * @param learningRate
     *            the learningRate to set
     */
    public final
            void setLearningRate(
                    double learningRate)
    {
        this.learningRate = learningRate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see interfaces.Recommender#predictRating(model.User, model.Item)
     */
    @Override
    public synchronized
            Float predictRating(
                    final User user, final Item item)
    {
        if (item == null) {
            throw new IllegalArgumentException("Item is null");
        }
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }
        final Integer userid = userMap.get(user.getId());
        final Integer itemid = itemMap.get(item.getId());
        if (userid != null && itemid != null) {
            return (float)learner.getResult(userid, itemid);
        } else {
            return Float.NaN;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see interfaces.Recommender#recommendItems(model.User)
     */
    @Override
    public
            Map<Integer, Float> recommendItems(
                    final User user)
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

    private
            void runTrain(
                    final int steps)
    {
        for (int i = 0; i < steps; i++) {
            if(nextTrainStep()){
                return;
            }
        }
    }

    private
            boolean nextTrainStep() {
        Collections.shuffle(cachedPreferences, StatisticFunctions.random);
        int userid;
        int itemid;
        for (int iteration = 0; iteration < numberOfFeatures; iteration++) {
            for (final Rating rating: cachedPreferences) {
                if (Thread.interrupted()) {
                    return true;
                }
                userid = rating.getUserId();
                itemid = rating.getItemId();
                final int useridx = this.userMap.get(userid);
                final int itemidx = this.itemMap.get(itemid);
                learner.train(useridx, itemidx, iteration, rating.getRating());
            }
        }
        return false;
    }

    private
            void cachePreferences() throws Exception {
        cachedPreferences.clear();
        for (final User user: trainDataModel.getUsers().values()) {
            if (userMap.get(user.getId()) == null) {
                LOG.error("User: " + user.getId());
                throw new Exception("Could not find user "+user.getId());
            }
            for (final Entry<Integer, Float> rating: user.getItemRating()
                    .entrySet())
            {
                if (itemMap.get(rating.getKey()) == null) {
                    LOG.error("Item: " + rating.getKey());
                    throw new Exception("Could not find item "+rating.getKey());
                }
                cachedPreferences.add(new Rating(user.getId(), rating.getKey(),
                        rating.getValue()));
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see interfaces.Recommender#train(model.trainDataModel)
     */
    @Override
    public
            void train(
                    final DataModel trainData)
    {
        if (trainData == null) {
            throw new IllegalArgumentException("Train data is null");
        }
        this.trainDataModel = trainData;
        int numUsers = this.trainDataModel.getUsers().size();
        int index = 0;
        for (final Integer user: trainDataModel.getUsers().keySet()) {
            userMap.put(user, index++);
        }

        final int numItems = trainDataModel.getItems().size();
        index = 0;
        for (final Integer item: trainDataModel.getItems().keySet()) {
            itemMap.put(item, index++);
        }

        final double average = 2.5;
        final double defaultValue = Math
                .sqrt((average - 1.0) / numberOfFeatures);

        switch (learningMethod) {
        case SGD:
            learner = new GradientDescentSVDLearner(numUsers, numItems, numberOfFeatures,
                    defaultValue, learningRate);
            break;
        case ALS:
            learner = new AlternatingLeastSquareLearner(trainData, learningRate, numberOfFeatures, numberOfIteration);
            break;
        default:
            break;
        }
        
        cachedPreferences = new ArrayList<Rating>();
        try {
            cachePreferences();
        } catch (final Exception e) {
            LOG.error(e.getMessage());
            return;
        }

        this.runTrain(numberOfIteration);

        this.perUserAverage.clear();
        for (final Entry<Integer, User> user: trainDataModel.getUsers()
                .entrySet())
        {
            this.perUserAverage.put(user.getValue().getId(),
                    user.getValue().getMeanOfRatings());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public
            String toString() {
        return "FunkSVD";
    }

}
