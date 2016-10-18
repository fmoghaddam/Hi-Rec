package model;

import java.util.Map.Entry;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.Int2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

/**
 * Item class
 * 
 * @author FBM
 *
 */
public class Item {

    /**
     * Item id
     */
    private final int id;
    /**
     * All the used who rated this item with the rating value
     */
    private Int2FloatLinkedOpenHashMap userRated;
    /**
     * List of low level features
     */
    private FloatArrayList lowLevelFeature;
    /**
     * List of Genre
     */
    private FloatArrayList genres;
    /**
     * List of tags
     */
    private ObjectSet<String> tags;
    /**
     * Global mean for this item
     */
    private Float mean = null;

    public Item(
            final int id)
    {
        this.id = id;
        this.userRated = new Int2FloatLinkedOpenHashMap();
        this.lowLevelFeature = new FloatArrayList();
        this.genres = new FloatArrayList();
        this.tags = new ObjectOpenHashSet<>();
    }

    public Item(
            final Item item)
    {
        this.id = item.id;
        this.userRated = new Int2FloatLinkedOpenHashMap(item.userRated);
        this.lowLevelFeature = new FloatArrayList(item.lowLevelFeature);
        this.genres = new FloatArrayList(item.genres);
        this.tags = new ObjectOpenHashSet<>(item.tags);
    }

    public
            int getId() {
        return id;
    }

    public
        Int2FloatLinkedOpenHashMap getUserRated() {
        return userRated;
    }

    public
            double[] getUserRatedAsArray() {
        final double[] array = new double[(int)Globals.MAX_ID_OF_UESRS];
        for (Entry<Integer, Float> entry: userRated.entrySet()) {
            array[entry.getKey() - 1] = entry.getValue();
        }
        return array;
    }

    public
            void addUserRating(
                    final int userId, final float rating)
    {
        if (userId <= 0 || rating < Globals.MIN_RATING
                || rating > Globals.MAX_RATING)
        {
            throw new IllegalArgumentException(
                    "User id is not OK or rating value is not OK");
        }
        userRated.put(userId, rating);
    }

    /**
     * Convert low level feature list to array
     * 
     * @return
     */
    public
            double[] getLowLevelFeatureAsArray() {
        final double[] array = new double[lowLevelFeature.size()];
        for (int i = 0; i < lowLevelFeature.size(); i++) {
            array[i] = (double)lowLevelFeature.getFloat(i);
        }
        return array;
    }

    /**
     * Returns global mean
     * 
     * @return
     */
    public
            float getMean() {
        if (mean != null) {
            return mean;
        } else {
            float sum = 0;
            for (float rating: userRated.values()) {
                sum += rating;
            }
            mean = sum / userRated.size();
        }
        return mean;

    }

    /**
     * @return the tags
     */
    public final
            ObjectSet<String> getTags() {
        return tags;
    }

    /**
     * @param tags
     *            the tags to set
     */
    public final
            void setTags(
                    ObjectSet<String> tags)
    {
        this.tags = new ObjectOpenHashSet<>(tags);
    }

    /**
     * @return the genres
     */
    public final
        FloatArrayList getGenres() {
        return genres;
    }

    /**
     * @return the lowLevelFeature
     */
    public final
        FloatArrayList getLowLevelFeature() {
        return lowLevelFeature;
    }

    /**
     * @param lowLevelFeature
     *            the lowLevelFeature to set
     */
    public final
            void setLowLevelFeature(
                    FloatArrayList lowLevelFeature)
    {
        this.lowLevelFeature = new FloatArrayList(lowLevelFeature);
    }

    /**
     * @param userRated
     *            the userRated to set
     */
    public final
            void setUserRated(
                    final Int2FloatLinkedOpenHashMap userRated)
    {
        this.userRated = userRated;
    }

    /**
     * @param genres
     *            the genres to set
     */
    public final
            void setGenres(
                    final FloatArrayList genres)
    {
        this.genres = new FloatArrayList(genres);
    }

    /**
     * Convert genre list to array
     * 
     * @return
     */
    public
            double[] getGenresAsArray() {
        final double[] array = new double[genres.size()];
        for (int i = 0; i < genres.size(); i++) {
            array[i] = genres.getFloat(i);
        }
        return array;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public
            String toString() {
        return "Item [id=" + id + ", userRated=" + userRated
                + ", lowLevelFeature=" + lowLevelFeature + ", genres="
                + genres + ", tags=" + tags + ", mean=" + mean + "]";
    }

}
