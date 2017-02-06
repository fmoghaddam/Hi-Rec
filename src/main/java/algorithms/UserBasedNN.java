package algorithms;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import interfaces.AbstractRecommender;
import it.unimi.dsi.fastutil.ints.Int2FloatLinkedOpenHashMap;
import model.DataModel;
import model.Globals;
import model.Item;
import model.User;
import util.MapUtil;

public final class UserBasedNN extends AbstractRecommender {

	private static final long serialVersionUID = -6468579094795977986L;

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(UserBasedNN.class.getCanonicalName());

	/**
	 * Number of neighbors
	 */
	private int numberOfNeighbours;

	public UserBasedNN() {
		final Map<String,String> value = new HashMap<>();
		value.put("NUMBER_OF_NEAREST_NEIGHBOUR", "Number of nearest neighbor");
		configurableParametersMap.put("numberOfNeighbours",value);
	}

	public int getNumberOfNeighbours() {
		return numberOfNeighbours;
	}


	public void setNumberOfNeighbours(int numberOfNeighbours) {
		this.numberOfNeighbours = numberOfNeighbours;
	}


	/*
	 * (non-Javadoc)
	 * @see interfaces.Recommender#predictRating(model.User, model.Item)
	 */
	@Override
	public Float predictRating(final User testUser,final Item testItem) {
		if (testItem == null) {
			throw new IllegalArgumentException("Item is null");
		}
		if (testUser == null) {
			throw new IllegalArgumentException("User is null");
		}

		Int2FloatLinkedOpenHashMap similarities = new Int2FloatLinkedOpenHashMap();

		final User user = trainDataModel.getUser(testUser.getId());
		if (user == null) {
			return Float.NaN;
		}
		
		for(final User neighbourUser: this.trainDataModel.getUsers().values()){
			if(neighbourUser.getItemRating().containsKey(testItem.getId())){
				final Float userSimilairty = this.similarityRepository.getUserSimilarity(testUser.getId(), neighbourUser.getId());
				if (userSimilairty != null && !Float.isNaN(userSimilairty)) {
					similarities.put((int) neighbourUser.getId(), (float) userSimilairty);
				}
			}
		}
				
		if (similarities.isEmpty()) {
			return Float.NaN;
		}

		similarities = MapUtil.sortByValueDescendingNew(similarities);

		double nominator = 0;
		double denominator = 0;
		int numberOfSelectedItem = 0;
		for (final Entry<Integer, Float> mapData : similarities.entrySet()) {
			if (numberOfSelectedItem >= this.numberOfNeighbours) {
				break;
			}
			numberOfSelectedItem++;
			final Float similarity = mapData.getValue();
			final Float rating = user.getItemRating().get((int) mapData.getKey());

			if (!Float.isNaN(rating)) {
				nominator += similarity * rating;
				denominator += similarity;
			}
		}
		if(denominator==0){
			return Float.NaN;
		}
		final float rating = (float) (nominator / denominator);
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
	public Int2FloatLinkedOpenHashMap recommendItems(final User user) {
		if (user == null) {
			throw new IllegalArgumentException("User is null");
		}
		final Int2FloatLinkedOpenHashMap predictions = new Int2FloatLinkedOpenHashMap();
		for (final Item item : trainDataModel.getItems().values()) {
			final int itemId = item.getId();
			final float predictRating = predictRating(user, item);
			if (!Float.isNaN(predictRating)) {
				predictions.put(itemId, predictRating);
			}
		}
		final Int2FloatLinkedOpenHashMap sortedMap = MapUtil.sortByValueDescendingNew(predictions);
		return sortedMap;
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
		this.trainDataModel = trainData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see interfaces.Recommender#isSimilairtyNeeded()
	 */
	@Override
	public boolean isSimilairtyNeeded() {
		return true;
	}

	/* (non-Javadoc)
	 * @see interfaces.AbstractRecommender#getConfigurabaleParameters()
	 */
	@Override
	public Map<String,Map<String,String>> getConfigurabaleParameters() {
		return configurableParametersMap;
	}

	@Override
	public String toString() {
		return "UserBasedNN";
	}
}
