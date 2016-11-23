package algorithms;

import java.util.Map;

import org.apache.log4j.Logger;

import interfaces.AbstractRecommender;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import model.DataModel;
import model.Item;
import model.User;
import util.MapUtil;

/**
 * This is non-personalized average popularity algorithm which return item
 * global mean for all the queries
 * 
 * @author FBM
 *
 */
public final class AveragePopularity extends AbstractRecommender {

	/**
	 * Unique id used for serialization
	 */
	private static final long serialVersionUID = 1019876085196345903L;
	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(AveragePopularity.class.getCanonicalName());
	/**
	 * Train data
	 */
	private DataModel trainData;

	/*
	 * (non-Javadoc)
	 * 
	 * @see interfaces.Recommender#predictRating(model.User, model.Item)
	 */
	@Override
	public Float predictRating(final User user, final Item item) {
		if (item == null) {
			throw new IllegalArgumentException("Item is null");
		}
		if (user == null) {
			throw new IllegalArgumentException("User is null");
		}
		final Item result = this.trainData.getItem(item.getId());
		if (result == null) {
			return Float.NaN;
		} else {
			return result.getMean();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see interfaces.Recommender#recommendItems(model.User)
	 */
	@Override
	public Map<Integer, Float> recommendItems(final User user) {
		if (user == null) {
			throw new IllegalArgumentException("User is null");
		}
		final Int2FloatOpenHashMap predictions = new Int2FloatOpenHashMap();
		for (final Item item : trainData.getItems().values()) {
			final int itemId = item.getId();
			final float predictRating = predictRating(user, item);
			if (!Float.isNaN(predictRating)) {
				predictions.put(itemId, predictRating);
			}
		}
		final Map<Integer, Float> sortByComparator = MapUtil.sortByValueDescending(predictions);
		return sortByComparator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see interfaces.Recommender#train(model.DataModel)
	 */
	@Override
	public void train(final DataModel trainData) {
		if (trainData == null) {
			throw new IllegalArgumentException("Train data is null");
		}
		this.trainData = trainData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AveragePopularity";
	}
}
