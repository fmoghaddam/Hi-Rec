package model;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import it.unimi.dsi.fastutil.ints.Int2FloatLinkedOpenHashMap;

/**
 * User class
 * 
 * @author FBM
 *
 */
public final class User {

    /**
     * User id
     */
    private final int id;
    /**
     * all the items which this user rated and the related rating value
     */
    private Int2FloatLinkedOpenHashMap itemRating;
    /**
     * Mean of rating for this user
     */
    private float meanOfRatings;

    public User(
            final int id)
    {
        this.id = id;
        itemRating = new Int2FloatLinkedOpenHashMap();
    }

    public User(
            final User user)
    {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }
        this.id = user.id;
        this.itemRating = new Int2FloatLinkedOpenHashMap(user.itemRating);
        this.meanOfRatings = user.meanOfRatings;
    }

    public
            float getMeanOfRatings() {
        calculateMeanOfRatings();
        return meanOfRatings;
    }

    public
            void calculateMeanOfRatings() {
        final DescriptiveStatistics values = new DescriptiveStatistics();
        for (Float rating: itemRating.values()) {
            values.addValue(rating);
        }
        this.meanOfRatings = (float)values.getMean();
    }

    public
            int getId() {
        return id;
    }

    public
        Int2FloatLinkedOpenHashMap getItemRating() {
        return itemRating;
    }

    public
            void addItemRating(
                    final int itemId, final float rating)
    {
        if (itemId <= 0 || rating > Globals.MAX_RATING
                || rating < Globals.MIN_RATING)
        {
            throw new IllegalArgumentException(
                    "Itemid is not OK or rating value is not OK");
        }

        this.itemRating.put(itemId, rating);
    }

    public
            double[] getRatingsInFullSizeArray() {
        final double[] ratings = new double[(int)Globals.MAX_ID_OF_ITEMS];
        for (int i: itemRating.keySet()) {
            ratings[(int)(i - 1)] = itemRating.get(i);
        }
        return ratings;
    }

    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public
            String toString() {
        return "User [id=" + id + ", itemRating=" + itemRating
                + ", meanOfRatings=" + meanOfRatings + "]";
    }

}
