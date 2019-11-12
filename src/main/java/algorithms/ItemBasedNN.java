package algorithms;

import interfaces.AbstractRecommender;
import it.unimi.dsi.fastutil.ints.Int2FloatLinkedOpenHashMap;
import model.DataModel;
import model.Globals;
import model.Item;
import model.User;
import org.apache.log4j.Logger;
import util.MapUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This is Itembased K nearest neighbor algorithm based on:
 * "ItemBased Collaborative Filtering Recommendation Algorithms", Badrul Sarwar,
 * George Karypis, Joseph Konstan, and John Riedl
 *
 * @author FBM
 */
public final class ItemBasedNN extends AbstractRecommender {

    /**
     * Unique id used for serialization
     */
    private static final long serialVersionUID = -3518067099137475848L;
    /**
     * Logger for this class
     */
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(ItemBasedNN.class.getCanonicalName());
    /**
     * Number of neighbors
     */
    private int numberOfNeighbours;

    /**
     *
     */
    public ItemBasedNN() {
        final Map<String, String> value = new HashMap<>();
        value.put("NUMBER_OF_NEAREST_NEIGHBOUR", "Number of nearest neighbor");
        configurableParametersMap.put("numberOfNeighbours", value);
    }

    /*
     * (non-Javadoc)
     *
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

        Int2FloatLinkedOpenHashMap similarities = new Int2FloatLinkedOpenHashMap();

        final User user = trainDataModel.getUser(testUser.getId());
        if (user == null) {
            return Float.NaN;
        }
        for (final Integer itemId : user.getItemRating().keySet()) {
            final Float itemSimilairty = this.similarityRepository.getItemSimilairty(itemId, testItem.getId());
            if (itemSimilairty != null && !Float.isNaN(itemSimilairty)) {
                similarities.put((int) itemId, (float) itemSimilairty);
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
        final float rating = (float) (nominator / denominator);
        if (rating > Globals.MAX_RATING) {
            return Globals.MAX_RATING;
        } else if (rating < Globals.MIN_RATING) {
            return Globals.MIN_RATING;
        } else {
            return rating;
        }

    }

    /**
     * @param numberOfNeighbours the numberOfNeighbours to set
     */
    public final void setNumberOfNeighbours(int numberOfNeighbours) {
        this.numberOfNeighbours = numberOfNeighbours;
    }

    /*
     * (non-Javadoc)
     *
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
     *
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
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ItemBasedNN";
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
    public Map<String, Map<String, String>> getConfigurabaleParameters() {
        return configurableParametersMap;
    }

    public int getNumberOfNeighbours() {
        return numberOfNeighbours;
    }
}
