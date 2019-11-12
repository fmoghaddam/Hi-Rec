package controller;

import model.DataModel;
import model.Globals;
import org.apache.log4j.Logger;
import util.Tuple;

import java.util.Collections;
import java.util.Random;

/**
 * This class is responsible for splitting data into train and test set
 *
 * @author FBM
 */
public final class DataSplitter {

    /**
     * Logger for this class
     */
    private Logger LOG = Logger
            .getLogger(DataSplitter.class.getCanonicalName());

    private final DataModel dataModel;

    public DataSplitter(
            final DataModel dataModel) {
        this.dataModel = dataModel;
    }

    public DataModel getDataModel() {
        return this.dataModel;
    }

    /**
     * Generates new test {@link DataModel} for specific fold number
     *
     * @param foldNumber Given fold number
     * @return New test {@link DataModel} for specific fold number
     */
    public DataModel getTestData(
            final int foldNumber) {
        if (foldNumber > Globals.NUMBER_OF_FOLDS) {
            throw new IllegalArgumentException(
                    "Fold number is greater than max fold numner. Max fold number="
                            + Globals.NUMBER_OF_FOLDS);
        }
        final int chunk = (int) (this.dataModel.getNumberOfRatings()
                / Globals.NUMBER_OF_FOLDS);
        final int startIndex = (foldNumber - 1) * chunk;
        final int endIndex = startIndex + chunk;
        LOG.debug("Test Data Fold " + foldNumber + " Start Index: " + startIndex
                + "\tEnd Index: " + endIndex);
        return this.dataModel.getTestData(startIndex, endIndex);
    }

    /**
     * Generates new train {@link DataModel} for specific fold number
     *
     * @param foldNumber Given fold number
     * @return New train {@link DataModel} for specific fold number
     */
    public DataModel getTrainData(
            int foldNumber) {
        if (foldNumber > Globals.NUMBER_OF_FOLDS) {
            throw new IllegalArgumentException(
                    "Fold number is greater than max fold numner. Max fold number="
                            + Globals.NUMBER_OF_FOLDS);
        }
        final int chunk = (int) (dataModel.getNumberOfRatings()
                / Globals.NUMBER_OF_FOLDS);
        final int startIndex = (foldNumber - 1) * chunk;
        final int endIndex = startIndex + chunk;
        LOG.debug("Test Data Fold " + foldNumber + " Start Index: " + startIndex
                + "\tEnd Index: " + endIndex);
        return this.dataModel.getTrainData(startIndex, endIndex);
    }

    /**
     * Shuffle Ratings in DataModel
     */
    public void shuffle() {
        LOG.debug("Shuffling rating data randomly.");
        if (Globals.RANDOMIZATION_SEED == null) {
            Collections.shuffle(this.dataModel.getRatings());
        } else {
            Collections.shuffle(this.dataModel.getRatings(), new Random(Globals.RANDOMIZATION_SEED));
        }
    }

    /**
     * Returns both train and test data in a {@link Tuple}
     *
     * @param foldNumber Given fold number
     * @return both train and test data in a {@link Tuple}
     */
    public Tuple<DataModel, DataModel> getTrainAndTestData(
            int foldNumber) {
        if (foldNumber > Globals.NUMBER_OF_FOLDS) {
            throw new IllegalArgumentException(
                    "Fold number is greater than max fold numner. Max fold number="
                            + Globals.NUMBER_OF_FOLDS);
        }
        final int chunk = (int) (dataModel.getNumberOfRatings()
                / Globals.NUMBER_OF_FOLDS);
        final int startIndex = (foldNumber - 1) * chunk;
        final int endIndex = startIndex + chunk;
        LOG.debug("Train Data Fold " + foldNumber + " Start Index: "
                + startIndex + "\tEnd Index: " + endIndex);
        final Tuple<DataModel, DataModel> trainAndTestData = new Tuple<DataModel, DataModel>(
                this.dataModel.getTrainData(startIndex, endIndex),
                this.dataModel.getTestData(startIndex, endIndex));
        return trainAndTestData;
    }

}