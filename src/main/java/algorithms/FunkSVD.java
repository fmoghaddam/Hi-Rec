package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import algorithms.funksvd.GradientDescentSVD;
import interfaces.Recommender;
import interfaces.SimilarityInterface;
import model.DataModel;
import model.Globals;
import model.Item;
import model.Rating;
import model.User;
import util.MapUtil;
import util.StaticFunctions;
/**
 * This is FunkSVD algorithm. http://sifter.org/~simon/journal/20061211.html
 * The code based is from Recommneder101: http://ls13-www.cs.tu-dortmund.de/homepage/recommender101/index.shtml
 */
public final class FunkSVD implements Recommender {

    /**
     * Logger for this class
     */
    private static final Logger LOG = Logger.getLogger(AveragePopularity.class.getCanonicalName());
    /**
     * Number of features
     */
    int numFeatures = Globals.NUMBER_OF_FEATURES_FOR_FUNKSVD;
    /**
     * Number of iteration 
     */
    int initialSteps = Globals.NUMBER_OF_ITERATION_FOR_FUNKSVD;

    private Map<Integer, Integer> userMap = new LinkedHashMap<>();
    private Map<Integer, Integer> itemMap = new LinkedHashMap<>();
    private GradientDescentSVD gdSvd = null;
    private List<Rating> cachedPreferences = null;

    private DataModel dataModel;
    
    /**
     * Calculate the user averages
     */
    Map<Integer, Float> perUserAverage = new LinkedHashMap<Integer, Float>();

    /**
     * 
     */
    public FunkSVD() {
	//Empty constructor
    }

    /*
     * (non-Javadoc)
     * @see interfaces.Recommender#predictRating(model.User, model.Item)
     */
    @Override
    public synchronized Float predictRating(final User user,final Item item) {
	if (item == null) {
	    throw new IllegalArgumentException("Item is null");
	}
	if (user == null) {
	    throw new IllegalArgumentException("User is null");
	}
	final Integer userid = userMap.get(user.getId());
	final Integer itemid = itemMap.get(item.getId());
	if (userid != null && itemid != null) {
	    return (float) gdSvd.getDotProduct(userid, itemid);
	} else {
	    return Float.NaN;
	}
    }

    /*
     * (non-Javadoc)
     * @see interfaces.Recommender#recommendItems(model.User)
     */
    @Override
    public Map<Integer, Float> recommendItems(final User user) {
	if (user == null) {
	    throw new IllegalArgumentException("User is null");
	}
	final Map<Integer, Float> predictions = new LinkedHashMap<Integer, Float>();

	for (final Item item : dataModel.getItems().values()) {
	    final int itemId = item.getId();
	    final float predictRating = predictRating(user, item);
	    if (!Float.isNaN(predictRating)) {
		predictions.put(itemId, predictRating);
	    }
	}
	final Map<Integer, Float> sortByComparator = MapUtil.sortByValueDescending(predictions);
	return sortByComparator;
    }

    public void train(final int steps) {
	for (int i = 0; i < steps; i++) {
	    nextTrainStep();
	}
    }

    private void nextTrainStep() {
	Collections.shuffle(cachedPreferences, StaticFunctions.random);
	int userid;
	int itemid;
	for (int i = 0; i < numFeatures; i++) {
	    for (final Rating rating : cachedPreferences) {
		userid = rating.getUserId();
		itemid = rating.getItemId();
		final Integer integer = this.userMap.get(userid);
		final Integer integer2 = this.itemMap.get(itemid);
		int useridx = integer;
		int itemidx = integer2;
		gdSvd.train(useridx, itemidx, i, rating.getRating());
	    }
	}
    }

    private void cachePreferences() {
	cachedPreferences.clear();
	for (final User user : dataModel.getUsers().values()) {
	    if (userMap.get(user.getId()) == null) {
		LOG.error("User: " + user.getId());
		System.exit(1);
	    }
	    for (final Entry<Integer, Float> rating : user.getItemRating().entrySet()) {
		if (itemMap.get(rating.getKey()) == null) {
		    LOG.error("Item: " + rating.getKey());
		    System.exit(1);
		}
		cachedPreferences.add(new Rating(user.getId(), rating.getKey(), rating.getValue()));
	    }
	}
    }

    /*
     * (non-Javadoc)
     * @see interfaces.Recommender#train(model.DataModel)
     */
    @Override
    public void train(final DataModel trainData) {
	if (trainData == null) {
	    throw new IllegalArgumentException("Train data is null");
	}
	final long now = new Date().getTime();
	this.dataModel = trainData;
	int numUsers = this.dataModel.getUsers().size();
	int index = 0;
	for (final Integer user : dataModel.getUsers().keySet()) {
	    userMap.put(user, index++);
	}

	final int numItems = dataModel.getItems().size();
	index = 0;
	for (final Integer item : dataModel.getItems().keySet()) {
	    itemMap.put(item, index++);
	}

	final double average = 2.5;
	final double defaultValue = Math.sqrt((average - 1.0) / numFeatures);

	gdSvd = new GradientDescentSVD(numUsers, numItems, numFeatures, defaultValue);
	cachedPreferences = new ArrayList<Rating>();
	cachePreferences();

	train(initialSteps);

	this.perUserAverage.clear();
	for (final Entry<Integer, User> user : dataModel.getUsers().entrySet()) {
	    this.perUserAverage.put(user.getValue().getId(), user.getValue().getMeanOfRatings());
	}
	LOG.debug("Train time: " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - now));
    }

    /*
     * (non-Javadoc)
     * @see interfaces.Recommender#setSimilarityRepository(controller.
     * SimilarityRepository)
     */
    @Override
    public void setSimilarityRepository(final SimilarityInterface similarityRepository) {
	// Empty Function
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "FunkSVD";
    }

    /*
     * (non-Javadoc)
     * 
     * @see interfaces.Recommender#isSimilairtyNeeded()
     */
    @Override
    public boolean isSimilairtyNeeded() {
	return false;
    }
}
