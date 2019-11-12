package algorithms;

import controller.similarity.LowLevelSimilarityRepository;
import controller.similarity.TagSimilarityRepository;
import interfaces.AbstractRecommender;
import it.unimi.dsi.fastutil.ints.Int2FloatLinkedOpenHashMap;
import model.DataModel;
import model.Item;
import model.User;
import org.apache.log4j.Logger;
import util.MapUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This algorithms is a combination of 2 ItemBased KNN which one of them uses
 * Tags and another one uses LowLevel features. For prediction result this
 * algorithm takes an average over two algorithm and for list result this
 * algorithm take 50% from each algorithm
 *
 * @author FBM
 */
public final class HybridTagLowLevel extends AbstractRecommender {

    /**
     * Unique id used for serialization
     */
    private static final long serialVersionUID = 8599538816641404122L;
    /**
     * Logger for this class
     */
    @SuppressWarnings("unused")
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
     * Number of neighbors
     */
    private int numberOfNeighbours;

    /**
     *
     */
    public HybridTagLowLevel() {
        final HashMap<String, String> h1 = new HashMap<>();
        h1.put("NUMBER_OF_NEAREST_NEIGHBOUR", "Number of nearest neighbor");
        this.configurableParametersMap.put("numberOfNeighbours", h1);
    }

    /**
     * @return the numberOfNeighbours
     */
    public final int getNumberOfNeighbours() {
        return numberOfNeighbours;
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
     *
     * @see interfaces.Recommender#recommendItems(model.User)
     */
    @Override
    public Map<Integer, Float> recommendItems(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }
        final Int2FloatLinkedOpenHashMap tagRecommend = tagAlgo.recommendItems(user);
        final Int2FloatLinkedOpenHashMap lowLevelRecommend = lowLevelAlgo.recommendItems(user);
        final Int2FloatLinkedOpenHashMap result = new Int2FloatLinkedOpenHashMap();
        final Iterator<Entry<Integer, Float>> lowLevelIterator = lowLevelRecommend.entrySet().iterator();
        final Iterator<Entry<Integer, Float>> tagIterator = tagRecommend.entrySet().iterator();
        if (tagRecommend.size() >= lowLevelRecommend.size()) {
            while (lowLevelIterator.hasNext()) {
                final Entry<Integer, Float> tagEntry = tagIterator.next();
                final Entry<Integer, Float> lowLevelEntry = lowLevelIterator.next();
                if (!result.containsKey(tagEntry.getKey())) {
                    result.put((int) tagEntry.getKey(), (float) tagEntry.getValue());
                }
                if (!result.containsKey(lowLevelEntry.getKey())) {
                    result.put((int) lowLevelEntry.getKey(), (float) lowLevelEntry.getValue());
                }
            }
            while (tagIterator.hasNext()) {
                final Entry<Integer, Float> tagEntry = tagIterator.next();
                if (!result.containsKey(tagEntry.getKey())) {
                    result.put((int) tagEntry.getKey(), (float) tagEntry.getValue());
                }
            }
        } else {
            while (tagIterator.hasNext()) {
                final Entry<Integer, Float> tagEntry = tagIterator.next();
                final Entry<Integer, Float> lowLevelEntry = lowLevelIterator.next();
                if (!result.containsKey(tagEntry.getKey())) {
                    result.put((int) tagEntry.getKey(), (float) tagEntry.getValue());
                }
                if (!result.containsKey(lowLevelEntry.getKey())) {
                    result.put((int) lowLevelEntry.getKey(), (float) lowLevelEntry.getValue());
                }
            }
            while (lowLevelIterator.hasNext()) {
                final Entry<Integer, Float> lowLevelEntry = lowLevelIterator.next();
                if (!result.containsKey(lowLevelEntry.getKey())) {
                    result.put((int) lowLevelEntry.getKey(), (float) lowLevelEntry.getValue());
                }
            }
        }

        final LinkedHashMap<Integer, Float> sortByComparator = (LinkedHashMap<Integer, Float>) MapUtil
                .sortByValueDescending(result);
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

        tagAlgo = new ItemBasedNN();
        lowLevelAlgo = new ItemBasedNN();

        tagAlgo.setNumberOfNeighbours(numberOfNeighbours);
        lowLevelAlgo.setNumberOfNeighbours(numberOfNeighbours);

        tagAlgo.train(trainData);
        lowLevelAlgo.train(trainData);

        tagAlgo.setSimilarityRepository(new TagSimilarityRepository(trainData));
        lowLevelAlgo.setSimilarityRepository(new LowLevelSimilarityRepository(trainData));
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "HybridTagLowLevel";
    }


}
