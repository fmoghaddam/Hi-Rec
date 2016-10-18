package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collections;

import org.apache.commons.math3.stat.Frequency;
import org.apache.log4j.Logger;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * Main data model which contains all the information about items,users,ratings,
 * genres,tags,lowlevel features,...
 * 
 * @author FBM
 *
 */
public final class DataModel {

    /**
     * Logger useed for this class
     */
    private static final Logger LOG = Logger
            .getLogger(DataModel.class.getName());

    /**
     * Map between itemId and Item
     */
    private Int2ObjectLinkedOpenHashMap<Item> items;
    /**
     * Map between userId and User
     */
    private Int2ObjectLinkedOpenHashMap<User> users;
    /**
     * List of ratings
     */
    private ObjectArrayList<Rating> ratings;

    private int numberOfRatings = 0;
    private int numberOfUsers = 0;
    private int numberOfItems = 0;

    private Frequency freq = new Frequency();

    public DataModel() {
        items = new Int2ObjectLinkedOpenHashMap<Item>();
        users = new Int2ObjectLinkedOpenHashMap<User>();
        ratings = new ObjectArrayList<>();
    }

    public
        Int2ObjectLinkedOpenHashMap<Item> getItems() {
        return items;
    }

    public
        Int2ObjectLinkedOpenHashMap<User> getUsers() {
        return users;
    }

    public
        ObjectArrayList<Rating> getRatings() {
        return ratings;
    }

    public
            void addUser(
                    final User user)
    {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }
        users.put(user.getId(), user);
        this.numberOfUsers++;
    }

    public
            void addItem(
                    final Item item)
    {
        if (item == null) {
            throw new IllegalArgumentException("Item is null");
        }
        items.put(item.getId(), item);
        this.numberOfItems++;
    }

    public
            Item getItem(
                    final int Id)
    {
        if (items.containsKey(Id)) {
            return items.get(Id);
        } else {
            return null;
        }
    }

    public
            User getUser(
                    final int Id)
    {
        if (users.containsKey(Id)) {
            return users.get(Id);
        } else {
            return null;
        }
    }

    public
            void printStatistic() {
        final DecimalFormat decimalFormat = new DecimalFormat("#.####");
        LOG.info("-----------------------------");
        LOG.info("#Users: \t\t" + this.numberOfUsers);
        LOG.info("#Items: \t\t" + this.numberOfItems);
        LOG.info("#Ratings: \t\t" + this.numberOfRatings);
        LOG.info("Density: \t\t"
                + decimalFormat.format((double)this.numberOfRatings * 1.0
                        / ((double)(this.numberOfItems * 1.0
                                * this.numberOfUsers * 1.0))));
        LOG.info("Avg. Ratings/user: \t" + decimalFormat
                .format((double)this.numberOfRatings / this.numberOfUsers));
        LOG.info("Avg. Ratings/item: \t" + decimalFormat
                .format((double)this.numberOfRatings / this.numberOfItems));
        LOG.info("Max rating: \t\t" + Globals.MAX_RATING);
        LOG.info("Min rating: \t\t" + Globals.MIN_RATING);
        LOG.info("-----------------------------");
        LOG.info(this.freq);
        LOG.info("-----------------------------");
    }

    /**
     * Prints all the ratings
     */
    public
            void printAllRatings() {
        LOG.info("-----------------------------");
        ratings.forEach(p -> LOG.info(p));
        LOG.info("-----------------------------");
    }

    /**
     * Generate a copy of this data model
     * 
     * @return Generated copy of this data model
     */
    public
            DataModel getCopy() {
        final DataModel newDataModel = new DataModel();
        newDataModel.items = new Int2ObjectLinkedOpenHashMap<Item>(this.items);
        newDataModel.users = new Int2ObjectLinkedOpenHashMap<User>(this.users);
        newDataModel.ratings = new ObjectArrayList<>(this.ratings);
        newDataModel.numberOfItems = items.size();
        newDataModel.numberOfUsers = users.size();
        newDataModel.numberOfRatings = ratings.size();
        newDataModel.freq = this.freq;
        return newDataModel;
    }

    public
            int getNumberOfRatings() {
        return numberOfRatings;
    }

    public
            int getNumberOfUsers() {
        return numberOfUsers;
    }

    public
            int getNumberOfItems() {
        return numberOfItems;
    }

    public
            void addRating(
                    final Rating rating)
    {
        if (rating == null) {
            throw new IllegalArgumentException("Rating is null");
        }
        if (!users.containsKey(rating.getUserId())) {
            throw new IllegalArgumentException(
                    "User " + rating.getUserId() + " does not exist yet");
        }
        if (!items.containsKey(rating.getItemId())) {
            throw new IllegalArgumentException(
                    "Item " + rating.getItemId() + " does not exist yet");
        }
        this.ratings.add(rating);
        this.numberOfRatings++;
        this.freq.addValue(rating.getRating());
    }

    /**
     * Write all the ratings to a file
     */
    public
            void writeRatingsToFile() {
        BufferedWriter bw = null;
        try {
            final File file = new File("ratings.csv");

            if (!file.exists()) {
                file.createNewFile();
            }

            final StringBuffer content = new StringBuffer();
            for (Rating rating: ratings) {
                String line = String.valueOf(rating.getUserId()) + ","
                        + String.valueOf(rating.getItemId()) + ","
                        + String.valueOf(rating.getRating()) + "\n";
                content.append(line);
            }
            final FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Filter all the ratings between startIndex and endIndex and create new
     * data model for test data based on them
     * 
     * @param startIndex
     * @param endIndex
     * @return
     */
    public
            DataModel getTestData(
                    int startIndex, int endIndex)
    {
        final DataModel testData = new DataModel();
        for (int i = startIndex; i < endIndex; i++) {
            final Rating rating = this.ratings.get(i);
            if (testData.getUser(rating.getUserId()) != null) {
                testData.getUser(rating.getUserId())
                        .addItemRating(rating.getItemId(), rating.getRating());
            } else {
                final User user = new User(rating.getUserId());
                user.addItemRating(rating.getItemId(), rating.getRating());
                testData.addUser(user);
            }
            if (testData.getItem(rating.getItemId()) != null) {
                testData.getItem(rating.getItemId())
                        .addUserRating(rating.getUserId(), rating.getRating());
            } else {
                final Item item = new Item(this.getItem(rating.getItemId()));
                item.addUserRating(rating.getUserId(), rating.getRating());
                testData.addItem(item);
            }
            testData.addRating(rating);
        }
        return testData;
    }

    /**
     * Filter all the ratings which are not between startIndex and endIndex and
     * create new data model for train data based on them
     * 
     * @param startIndex
     * @param endIndex
     * @return
     */
    public
            DataModel getTrainData(
                    int startIndex, int endIndex)
    {
        final DataModel trainData = new DataModel();
        for (int i = 0; i < numberOfRatings; i++) {
            if (i >= startIndex && i < endIndex) {
                continue;
            }
            final Rating rating = this.ratings.get(i);
            if (trainData.getUser(rating.getUserId()) != null) {
                trainData.getUser(rating.getUserId())
                        .addItemRating(rating.getItemId(), rating.getRating());
            } else {
                final User user = new User(rating.getUserId());
                user.addItemRating(rating.getItemId(), rating.getRating());
                trainData.addUser(user);
            }
            if (trainData.getItem(rating.getItemId()) != null) {
                trainData.getItem(rating.getItemId())
                        .addUserRating(rating.getUserId(), rating.getRating());
            } else {
                final Item item = new Item(this.getItem(rating.getItemId()));
                item.addUserRating(rating.getUserId(), rating.getRating());
                trainData.addItem(item);
            }
            trainData.addRating(rating);
        }
        return trainData;
    }

    /**
     * Samples data and only keeps {@code percentage} of it.
     * 
     * @param percentage
     */
    public
            DataModel sampleRating(
                    int percentage)
    {
        Collections.shuffle(ratings);
        final DataModel sampleData = new DataModel();
        for (int i = 0; i < numberOfRatings * (percentage / 100.0); i++) {
            final Rating rating = this.ratings.get(i);
            if (sampleData.getUser(rating.getUserId()) != null) {
                sampleData.getUser(rating.getUserId())
                        .addItemRating(rating.getItemId(), rating.getRating());
            } else {
                final User user = new User(rating.getUserId());
                user.addItemRating(rating.getItemId(), rating.getRating());
                sampleData.addUser(user);
            }
            if (sampleData.getItem(rating.getItemId()) != null) {
                sampleData.getItem(rating.getItemId())
                        .addUserRating(rating.getUserId(), rating.getRating());
            } else {
                final Item item = new Item(this.getItem(rating.getItemId()));
                item.addUserRating(rating.getUserId(), rating.getRating());
                sampleData.addItem(item);
            }
            sampleData.addRating(rating);
        }
        return sampleData;
    }
}
