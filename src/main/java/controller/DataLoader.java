package controller;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import model.*;
import org.apache.log4j.Logger;
import util.TopPopularItemsUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for reading files and converting them to
 * {@link DataModel} All the functions in this class ignore the lines which
 * starts with "#"
 *
 * @author FBM
 */
public final class DataLoader {

    /**
     * Logger for this class
     */
    private Logger LOG = Logger.getLogger(DataLoader.class.getCanonicalName());
    private final DataModel dataModel = new DataModel();
    private final List<Integer> doNotAddList = new ArrayList<>();
    private boolean alreadyReduced = false;

    /**
     * Reads rating files.
     *
     * @throws DataLoaderException
     */
    private void readRatingFile() throws DataLoaderException {
        try (final BufferedReader reader = new BufferedReader(
                new FileReader(Globals.RATING_FILE_PATH));) {
            LOG.info("Reading rating file " + Globals.RATING_FILE_PATH + "....");
            String line;
            line = reader.readLine();
            while (line.contains("user")) {
                line = reader.readLine();
            }
            String[] tokens;
            int maxUserId = -1;
            int maxItemId = -1;
            float maxRating = -1;
            float minRating = 10000000;
            while (line != null) {
                if (line.trim().startsWith("//")) {
                    line = reader.readLine();
                    continue;
                }
                tokens = line.split(Globals.RATING_FILE_SEPERATOR);
                final float rating = Float.parseFloat(tokens[2]);
                final int userId = Integer.parseInt(tokens[0]);
                if (userId > maxUserId) {
                    maxUserId = userId;
                }
                final int itemId = Integer.parseInt(tokens[1]);
                if (doNotAddList.contains(itemId)) {
                    line = reader.readLine();
                    continue;
                }
                if (itemId > maxItemId) {
                    maxItemId = itemId;
                }

                if (rating > maxRating) {
                    maxRating = rating;
                    Globals.setMaxRating(maxRating);
                }
                if (rating < minRating) {
                    minRating = rating;
                    Globals.setMinRating(minRating);
                }
                if (this.dataModel.getUser(userId) != null) {
                    this.dataModel.getUser(userId).addItemRating(itemId,
                            rating);
                } else {
                    final User user = new User(userId);
                    user.addItemRating(itemId, rating);
                    this.dataModel.addUser(user);
                }
                if (this.dataModel.getItem(itemId) != null) {
                    this.dataModel.getItem(itemId).addUserRating(userId,
                            rating);
                } else {
                    final Item item = new Item(itemId);
                    item.addUserRating(userId, rating);
                    this.dataModel.addItem(item);
                }
                this.dataModel.addRating(new Rating(userId, itemId, rating));
                line = reader.readLine();
            }
            Globals.setMaxNumberOfUsers(maxUserId);
            Globals.setMaxNumberOfItems(maxItemId);
            reader.close();
            if (!alreadyReduced) {
                LOG.info("Rating file loaded from " + Globals.RATING_FILE_PATH);
            }
        } catch (final Exception exception) {
            throw new DataLoaderException("Can not load rating file: " + Globals.RATING_FILE_PATH, exception);
        }
    }

    /**
     * Reads low level file and parse it. The format of low level file should be
     * like this: ItemId,feature1,feature2,.... Features should be float
     *
     * @throws DataLoaderException
     */
    private void readLowLevelFile() throws DataLoaderException {
        LOG.info("Reading visual features file " + Globals.LOW_LEVEL_FILE_PATH + "....");
        try (final BufferedReader reader = new BufferedReader(
                new FileReader(Globals.LOW_LEVEL_FILE_PATH));) {
            String line;
            line = reader.readLine();
            while (line.contains("#")) {
                line = reader.readLine();
            }
            String[] tokens;
            while (line != null) {
                if (line.trim().startsWith("//")) {
                    line = reader.readLine();
                    continue;
                }
                tokens = line.split(Globals.LOW_LEVEL_FILE_SEPERATOR);
                final FloatArrayList features = new FloatArrayList();
                final int itemId = Integer.parseInt(tokens[0]);
                if (doNotAddList.contains(itemId)) {
                    line = reader.readLine();
                    continue;
                }
                for (int i = 1; i < tokens.length; i++) {
                    try {
                        features.add(Float.parseFloat(tokens[i]));
                    } catch (final NumberFormatException exception) {
                        LOG.error(
                                "Can not convert visual feature to numbers."
                                        + exception);
                        throw exception;
                    }
                }
                final Item item = new Item(itemId);
                item.setLowLevelFeature(features);
                this.dataModel.addItem(item);
                line = reader.readLine();
            }
            reader.close();
            if (!alreadyReduced) {
                LOG.info("Visual features file loaded from " + Globals.LOW_LEVEL_FILE_PATH);
            }
        } catch (final Exception exception) {
            throw new DataLoaderException("Can not load low level feature file: " + Globals.LOW_LEVEL_FILE_PATH, exception);
        }
    }

    /**
     * Reads genre file and parse it. Genres are presented as boolean vector The
     * format of genre file should be like this: ItemId,0,1,0,.... which 1 means
     * the movie is in the corresponding genre nad 0 means not
     *
     * @throws DataLoaderException
     */
    private void readGenreFile() throws DataLoaderException {
        try (final BufferedReader reader = new BufferedReader(
                new FileReader(Globals.GENRE_FILE_PATH));) {
            String line;
            line = reader.readLine();
            while (line.contains("#")) {
                line = reader.readLine();
            }
            String[] tokens;
            while (line != null) {
                if (line.trim().startsWith("//")) {
                    line = reader.readLine();
                    continue;
                }
                tokens = line.split(Globals.GENRE_FILE_SEPERATOR);
                final FloatArrayList features = new FloatArrayList();
                final int itemId = Integer.parseInt(tokens[0]);
                if (doNotAddList.contains(itemId)) {
                    line = reader.readLine();
                    continue;
                }
                for (int i = 1; i < tokens.length; i++) {
                    try {
                        features.add(Float.parseFloat(tokens[i]));
                    } catch (final NumberFormatException exception) {
                        LOG.error("Can not convert genre to numbers."
                                + exception);
                        throw exception;
                    }
                }

                if (this.dataModel.getItem(itemId) == null) {
                    final Item item = new Item(itemId);
                    item.setGenres(features);
                    this.dataModel.addItem(item);
                } else {
                    this.dataModel.getItem(itemId).setGenres(features);
                }

                line = reader.readLine();
            }
            reader.close();
            if (!alreadyReduced) {
                LOG.info("Genre file loaded from " + Globals.GENRE_FILE_PATH);
            }
        } catch (final Exception exception) {
            throw new DataLoaderException("Can not load genre file: " + Globals.GENRE_FILE_PATH, exception);
        }
    }

    /**
     * Reads tag file and parse it. The format of tag file should be like this:
     * ItemId,tag1,tag2,tag3,.... Tags are String. This function just read tags
     * and does not handle stemming, stopping word removal,...
     *
     * @throws DataLoaderException
     */
    private void readTagFile() throws DataLoaderException {
        try (final BufferedReader reader = new BufferedReader(
                new FileReader(Globals.TAG_FILE_PATH));) {
            String line;
            line = reader.readLine();
            while (line.contains("#")) {
                line = reader.readLine();
            }
            String[] tokens;
            while (line != null) {
                if (line.trim().startsWith("//")) {
                    line = reader.readLine();
                    continue;
                }
                tokens = line.split(Globals.TAG_FILE_SEPERATOR);
                final ObjectSet<String> features = new ObjectOpenHashSet<>();
                final int itemId = Integer.parseInt(tokens[0]);
                if (doNotAddList.contains(itemId)) {
                    line = reader.readLine();
                    continue;
                }
                for (int i = 1; i < tokens.length; i++) {
                    features.add(tokens[i]);
                }

                if (this.dataModel.getItem(itemId) == null) {
                    final Item item = new Item(itemId);
                    item.setTags(features);
                    this.dataModel.addItem(item);
                } else {
                    this.dataModel.getItem(itemId).setTags(features);
                }

                line = reader.readLine();
            }
            reader.close();
            if (!alreadyReduced) {
                LOG.info("Tag file loaded from " + Globals.TAG_FILE_PATH);
            }
        } catch (final Exception exception) {
            throw new DataLoaderException("Can not load tag file: " + Globals.TAG_FILE_PATH, exception);
        }
    }

    /**
     * Main function which reads the files and returns {@link DataModel}
     *
     * @return {@link DataModel}
     * @throws DataLoaderException
     */
    public DataModel readData() throws DataLoaderException {
        boolean hasContent = false;
        if (Globals.LOW_LEVEL_FILE_PATH != null
                && !Globals.LOW_LEVEL_FILE_PATH.isEmpty()) {
            hasContent = true;
            this.readLowLevelFile();
        }
        if (Globals.GENRE_FILE_PATH != null
                && !Globals.GENRE_FILE_PATH.isEmpty()) {
            hasContent = true;
            this.readGenreFile();
        }
        if (Globals.TAG_FILE_PATH != null && !Globals.TAG_FILE_PATH.isEmpty()) {
            hasContent = true;
            this.readTagFile();
        }
        if (hasContent) {
            this.readRatingFileBaseOnExisintItems();
        } else {
            this.readRatingFile();
        }

        if (Globals.DROP_POPULAR_ITEM && !alreadyReduced) {
            alreadyReduced = true;
            return removeTopPopular(Globals.DROP_POPULAR_ITEM_NUMBER);
        }

        return dataModel;
    }

    /**
     * Reads rating file and parse it. Rating file format should be like this:
     * userId,ItemId,Rating This function ignore all the ratings which does not
     * have any feature (lowlevel,genre,tag)
     *
     * @throws DataLoaderException
     */
    private void readRatingFileBaseOnExisintItems() throws DataLoaderException {
        try (final BufferedReader reader = new BufferedReader(
                new FileReader(Globals.RATING_FILE_PATH));) {
            String line;
            line = reader.readLine();
            while (line.contains("#")) {
                line = reader.readLine();
            }
            String[] tokens;
            int maxUserId = -1;
            int maxItemId = -1;
            float maxRating = -1;
            float minRating = 10000000;
            while (line != null) {
                if (line.trim().startsWith("//")) {
                    line = reader.readLine();
                    continue;
                }
                tokens = line.split(Globals.RATING_FILE_SEPERATOR);
                final float rating = Float.parseFloat(tokens[2]);
                final Integer userId = Integer.parseInt(tokens[0]);
                final Integer itemId = Integer.parseInt(tokens[1]);
                if (doNotAddList.contains(itemId)) {
                    line = reader.readLine();
                    continue;
                }
                if (this.dataModel.getItem(itemId) != null) {

                    if (userId > maxUserId) {
                        maxUserId = userId;
                    }

                    if (itemId > maxItemId) {
                        maxItemId = itemId;
                    }

                    if (maxRating < rating) {
                        maxRating = rating;
                        Globals.setMaxRating(maxRating);
                    }
                    if (minRating > rating) {
                        minRating = rating;
                        Globals.setMinRating(minRating);
                    }

                    if (this.dataModel.getUser(userId) != null) {
                        this.dataModel.getUser(userId).addItemRating(itemId,
                                rating);
                    } else {
                        final User user = new User(userId);
                        user.addItemRating(itemId, rating);
                        this.dataModel.addUser(user);
                    }
                    this.dataModel.getItem(itemId).addUserRating(userId,
                            rating);
                    this.dataModel
                            .addRating(new Rating(userId, itemId, rating));
                }
                line = reader.readLine();
            }
            Globals.setMaxNumberOfItems(maxItemId);
            Globals.setMaxNumberOfUsers(maxUserId);
            reader.close();
            if (!alreadyReduced) {
                LOG.info("Related Rating Data loaded from " + Globals.RATING_FILE_PATH);
            }
        } catch (final Exception exception) {
            throw new DataLoaderException("Can not load rating file : " + Globals.RATING_FILE_PATH, exception);
        }
    }

    /**
     * Removes #numberOfItems top popular items from this dataset
     *
     * @param numberOfItems
     * @return
     * @throws DataLoaderException
     */
    public DataModel removeTopPopular(final int numberOfItems) throws DataLoaderException {
        LOG.info("Removing " + numberOfItems + " top popular items ...");
        final IntArrayList topPopularItems = TopPopularItemsUtil.getTopPopularItems(numberOfItems, dataModel);
        this.doNotAddList.addAll(topPopularItems);
        this.dataModel.clearAll();
        return readData();
    }
}
