package algorithms;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import controller.similarity.LowLevelSimilarityRepository;
import controller.similarity.TagSimilarityRepository;
import interfaces.Recommender;
import interfaces.SimilarityInterface;
import model.DataModel;
import model.Item;
import model.User;
import util.MapUtil;
/**
 * This algorithms is a combination of 2 ItemBased KNN which
 * one of them uses Tags and another one uses LowLevel features.
 * For prediction result this algorithm takes an average over two algorithm and for
 * list result this algorithm take 50% from each algorithm
 * 
 * @author FBM
 *
 */
public final class HybridTagLowLevel implements Recommender {

    /**
     * Logger for this class
     */
    private static final Logger LOG = Logger.getLogger(HybridTagLowLevel.class.getCanonicalName());
    /**
     * {@link ItemBasedNN} which uses tags 
     */
    private ItemBasedNN tagAlgo;
    /**
     * {@link ItemBasedNN} which uses low level features 
     */
    private ItemBasedNN lowLevelAlgo;
    /**
     * Repository used for calculating similarities for tag based algorithm
     */
    private TagSimilarityRepository tagSimilarity;
    /**
     * Repository used for calculating similarities for low level feature based algorithm
     */
    private LowLevelSimilarityRepository lowLevelSimilarity;

    /*
     * (non-Javadoc)
     * @see interfaces.Recommender#predictRating(model.User, model.Item)
     */
    @Override
    public Float predictRating(final User user,final Item item) {
	if (item == null) {
	    throw new IllegalArgumentException("Item is null");
	}
	if (user == null) {
	    throw new IllegalArgumentException("User is null");
	}
	final Float tagPrediction = tagAlgo.predictRating(user, item);
	final Float llPrediction = lowLevelAlgo.predictRating(user, item);

	if (!Float.isNaN(tagPrediction) && !Float.isNaN(llPrediction)) {
	    return (tagPrediction + llPrediction) / 2;
	}
	if (!Float.isNaN(tagPrediction) && Float.isNaN(llPrediction)) {
	    return tagPrediction;
	}
	if (Float.isNaN(tagPrediction) && !Float.isNaN(llPrediction)) {
	    return llPrediction;
	}
	return Float.NaN;
    }

    /*
     * (non-Javadoc)
     * @see interfaces.Recommender#recommendItems(model.User)
     */
    @Override
    public Map<Integer, Float> recommendItems(User user) {
	if (user == null) {
	    throw new IllegalArgumentException("User is null");
	}
	final Map<Integer, Float> tagRecommend = tagAlgo.recommendItems(user);
	final Map<Integer, Float> lowLevelRecommend = lowLevelAlgo.recommendItems(user);
	final Map<Integer, Float> result = new HashMap<>();
	final Iterator<Entry<Integer, Float>> lowLevelIterator = lowLevelRecommend.entrySet().iterator();
	final Iterator<Entry<Integer, Float>> tagIterator = tagRecommend.entrySet().iterator();
	if (tagRecommend.size() >= lowLevelRecommend.size()) {
	    while (lowLevelIterator.hasNext()) {
		final Entry<Integer, Float> tagEntry = tagIterator.next();
		final Entry<Integer, Float> lowLevelEntry = lowLevelIterator.next();
		if (!result.containsKey(tagEntry.getKey())) {
		    result.put(tagEntry.getKey(), tagEntry.getValue());
		}
		if (!result.containsKey(lowLevelEntry.getKey())) {
		    result.put(lowLevelEntry.getKey(), lowLevelEntry.getValue());
		}

	    }
	    while (tagIterator.hasNext()) {
		final Entry<Integer, Float> tagEntry = tagIterator.next();
		if (!result.containsKey(tagEntry.getKey())) {
		    result.put(tagEntry.getKey(), tagEntry.getValue());
		}
	    }
	} else {
	    while (tagIterator.hasNext()) {
		final Entry<Integer, Float> tagEntry = tagIterator.next();
		final Entry<Integer, Float> lowLevelEntry = lowLevelIterator.next();
		if (!result.containsKey(tagEntry.getKey())) {
		    result.put(tagEntry.getKey(), tagEntry.getValue());
		}
		if (!result.containsKey(lowLevelEntry.getKey())) {
		    result.put(lowLevelEntry.getKey(), lowLevelEntry.getValue());
		}
	    }
	    while (lowLevelIterator.hasNext()) {
		final Entry<Integer, Float> lowLevelEntry = lowLevelIterator.next();
		if (!result.containsKey(lowLevelEntry.getKey())) {
		    result.put(lowLevelEntry.getKey(), lowLevelEntry.getValue());
		}
	    }
	}

	final LinkedHashMap<Integer, Float> sortByComparator = MapUtil.sortByValueDescending(result);
	return sortByComparator;
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
	tagAlgo = new ItemBasedNN();
	lowLevelAlgo = new ItemBasedNN();

	tagAlgo.train(trainData);
	lowLevelAlgo.train(trainData);

	tagSimilarity = new TagSimilarityRepository(trainData);
	lowLevelSimilarity = new LowLevelSimilarityRepository(trainData);
	LOG.debug("Train time: " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - now));
    }

    /*
     * (non-Javadoc)
     * @see interfaces.Recommender#setSimilarityRepository(controller.
     * SimilarityRepository)
     */
    @Override
    public void setSimilarityRepository(final SimilarityInterface similarityRepository) {
	if (similarityRepository == null) {
	    throw new IllegalArgumentException("SimilarityRepository is null");
	}
	tagAlgo.setSimilarityRepository(tagSimilarity);
	lowLevelAlgo.setSimilarityRepository(lowLevelSimilarity);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "HybridTagLowLevel";
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
