package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

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
    private LinkedHashMap<Integer, Float> userRated;
    /**
     * List of low level features
     */
    private List<Float> lowLevelFeature;
    /**
     * List of Genre
     */
    private List<Float> genres;
    /**
     * List of tags
     */
    private Set<String> tags;
    /**
     * Global mean for this item
     */
    private Float mean = null;

    public Item(
            final int id)
    {
        this.id = id;
        this.userRated = new LinkedHashMap<>();
        this.lowLevelFeature = new ArrayList<>();
        this.genres = new ArrayList<>();
        this.tags = new HashSet<>();
    }

    public Item(
            final Item item)
    {
        this.id = item.id;
        this.userRated = new LinkedHashMap<>(item.userRated);
        this.lowLevelFeature = new ArrayList<>(item.lowLevelFeature);
        this.genres = new ArrayList<>(item.genres);
        this.tags = new HashSet<>(item.tags);
    }

    public
            int getId() {
        return id;
    }

    public
            LinkedHashMap<Integer, Float> getUserRated() {
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
            array[i] = (double)lowLevelFeature.get(i);
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
            Set<String> getTags() {
        return tags;
    }

    /**
     * @param tags
     *            the tags to set
     */
    public final
            void setTags(
                    Set<String> tags)
    {
        this.tags = new HashSet<>(tags);
    }

    /**
     * @return the genres
     */
    public final
            List<Float> getGenres() {
        return genres;
    }

    /**
     * @return the lowLevelFeature
     */
    public final
            List<Float> getLowLevelFeature() {
        return lowLevelFeature;
    }

    /**
     * @param lowLevelFeature
     *            the lowLevelFeature to set
     */
    public final
            void setLowLevelFeature(
                    List<Float> lowLevelFeature)
    {
        this.lowLevelFeature = new ArrayList<>(lowLevelFeature);
    }

    /**
     * @param userRated
     *            the userRated to set
     */
    public final
            void setUserRated(
                    LinkedHashMap<Integer, Float> userRated)
    {
        this.userRated = userRated;
    }

    /**
     * @param genres
     *            the genres to set
     */
    public final
            void setGenres(
                    List<Float> genres)
    {
        this.genres = new ArrayList<>(genres);
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
            array[i] = genres.get(i);
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
