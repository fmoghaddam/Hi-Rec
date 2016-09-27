package algorithms;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import interfaces.Recommender;
import interfaces.SimilarityInterface;
import model.DataModel;
import model.Globals;
import model.Item;
import model.User;
import util.MapUtil;

/**
 * This is Itembased K nearest neighbour algorithm based on:
 * "ItemBased Collaborative Filtering Recommendation Algorithms", Badrul Sarwar, George Karypis, Joseph Konstan, and John Riedl
 * @author FBM
 *
 */
public final class ItemBasedNN implements Recommender {

    /**
     * Logger for this class
     */
    private static final Logger LOG = Logger.getLogger(ItemBasedNN.class.getCanonicalName());
    /**
     * Number of neighbors
     */
    private final int numberOfNeighors;
    /**
     * Repository used for calculating similarities
     */
    private SimilarityInterface similarityRepository;
    /**
     * Train data set
     */
    private DataModel trainDataModel;

    /**
     * Constructor
     */
    public ItemBasedNN() {
	this.numberOfNeighors = Globals.NUMBER_OF_NEAREST_NEIGHBOUR;
    }

    /**
     * Returns similarity repository
     * @return
     */
    public SimilarityInterface getSimilarityRepository() {
	return similarityRepository;
    }

    /*
     * (non-Javadoc)
     * @see interfaces.Recommender#setSimilarityRepository(interfaces.SimilarityInterface)
     */
    @Override
    public void setSimilarityRepository(final SimilarityInterface similarityRepository) {
	if (similarityRepository == null) {
	    throw new IllegalArgumentException("SimilarityRepository is null");
	}
	this.similarityRepository = similarityRepository;
    }

    /*
     * (non-Javadoc)
     * @see interfaces.Recommender#predictRating(model.User, model.Item)
     */
    @Override
    public Float predictRating(final User testUser, final Item testItem) {
	if (testItem == null) {
	    throw new IllegalArgumentException("Item is null");
	}
	if (testUser == null) {
	    throw new IllegalArgumentException("User is null");
	}

	Map<Integer, Float> similarities = new LinkedHashMap<Integer, Float>();

	final User user = trainDataModel.getUser(testUser.getId());
	if (user == null) {
	    return Float.NaN;
	}
	for (final Integer itemId : user.getItemRating().keySet()) {
	    final Float itemSimilairty = this.similarityRepository.getItemSimilairty(itemId, testItem.getId());
	    if (itemSimilairty != null && !Float.isNaN(itemSimilairty)) {
		similarities.put(itemId, itemSimilairty);
	    }
	}

	if (similarities.size() < numberOfNeighors) {
	    return Float.NaN;
	}

	similarities = MapUtil.sortByValueAscending(similarities);

	double totalBias = 0;
	double totalSimilarity = 0;

	for (final Map.Entry<Integer, Float> mapData : similarities.entrySet()) {
	    final Float similarity = mapData.getValue();
	    final Float rating = user.getItemRating().get(mapData.getKey());
	    final Item item = trainDataModel.getItem(mapData.getKey());
	    if (item == null) {
		// test item does not exist in training data
		return Float.NaN;
	    }
	    final float mean = item.getMean();

	    if (!Float.isNaN(rating)) {
		double neighborBias = rating - mean;
		neighborBias = neighborBias * similarity;
		totalBias += neighborBias;
		totalSimilarity += Math.abs(similarity);
	    }
	}
	final float rating = (float) (testItem.getMean() + (totalBias / totalSimilarity));
	if (rating > Globals.MAX_RATING) {
	    return Globals.MAX_RATING;
	} else if (rating < Globals.MIN_RATING) {
	    return Globals.MIN_RATING;
	} else {
	    return rating;
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
	for (final Item item : trainDataModel.getItems().values()) {
	    final int itemId = item.getId();
	    final float predictRating = predictRating(user, item);
	    if (!Float.isNaN(predictRating)) {
		predictions.put(itemId, predictRating);
	    }
	}
	final LinkedHashMap<Integer, Float> sortByComparator = MapUtil.sortByValueDescending(predictions);
	return sortByComparator;
    }

    /*
     * (non-Javadoc)
     * @see interfaces.Recommender#train(model.DataModel)
     */
    @Override
    public void train(final DataModel trainData) {
	if (trainData == null) {
	    throw new IllegalArgumentException("Training data is null");
	}
	final long now = new Date().getTime();
	this.trainDataModel = trainData;
	LOG.debug("Train time: " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - now));
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "ItemBasedNN";
    }

    /*
     * (non-Javadoc)
     * @see interfaces.Recommender#isSimilairtyNeeded()
     */
    @Override
    public boolean isSimilairtyNeeded() {
	return true;
    }

}
