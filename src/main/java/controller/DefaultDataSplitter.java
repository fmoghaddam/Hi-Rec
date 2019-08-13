package controller;

import java.util.Collections;
import java.util.Random;

import org.apache.log4j.Logger;

import interfaces.DataSplitterInterface;
import model.DataModel;
import model.Globals;

/**
 * This class is responsible for splitting data into train and test set
 * 
 * @author FBM
 *
 */
public final class DefaultDataSplitter implements DataSplitterInterface{

    /**
     * Logger for this class
     */
    private Logger LOG = Logger
            .getLogger(DefaultDataSplitter.class.getCanonicalName());

    private final DataModel dataModel;

    public DefaultDataSplitter(
            final DataModel dataModel)
    {
        this.dataModel = dataModel;
    }

    public
            DataModel getDataModel() {
        return this.dataModel;
    }

    /**
     * Generates new test {@link DataModel} for specific fold number
     * 
     * @param foldNumber
     *            Given fold number
     * @return New test {@link DataModel} for specific fold number
     */
    public
            DataModel getTestData(
                    final int foldNumber)
    {
        if (foldNumber > Globals.NUMBER_OF_FOLDS) {
            throw new IllegalArgumentException(
                    "Fold number is greater than max fold numner. Max fold number="
                            + Globals.NUMBER_OF_FOLDS);
        }
        final int chunk = (int)(this.dataModel.getNumberOfRatings()
                / Globals.NUMBER_OF_FOLDS);
        final int startIndex = (foldNumber - 1) * chunk;
        final int endIndex = startIndex + chunk;
        return this.dataModel.getTestData(startIndex, endIndex);
    }

    /**
     * Generates new train {@link DataModel} for specific fold number
     * 
     * @param foldNumber
     *            Given fold number
     * @return New train {@link DataModel} for specific fold number
     */
    public
            DataModel getTrainData(
                    int foldNumber)
    {
        if (foldNumber > Globals.NUMBER_OF_FOLDS) {
            throw new IllegalArgumentException(
                    "Fold number is greater than max fold numner. Max fold number="
                            + Globals.NUMBER_OF_FOLDS);
        }
        final int chunk = (int)(dataModel.getNumberOfRatings()
                / Globals.NUMBER_OF_FOLDS);
        final int startIndex = (foldNumber - 1) * chunk;
        final int endIndex = startIndex + chunk;
        return this.dataModel.getTrainData(startIndex, endIndex);
    }

    /**
     * Shuffle Ratings in DataModel
     */
    public
            void shuffle() {
        LOG.debug("Shuffling rating data randomly.");
        if(Globals.RANDOMIZATION_SEED==null){
            Collections.shuffle(this.dataModel.getRatings());
        }else{
            Collections.shuffle(this.dataModel.getRatings(),new Random(Globals.RANDOMIZATION_SEED));
        }
    }

}
