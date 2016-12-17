package util;

import org.apache.log4j.Logger;

import controller.DataLoader;
import controller.DataLoaderException;
import model.DataModel;

/**
 * This class samples data based on ratings or users.
 * 
 * @author FBM
 */
public class Sampler {

    /**
     * Logger for this class
     */
    private static final Logger LOG = Logger.getLogger(Sampler.class.getCanonicalName());
    
    /**
     * @param args
     * @throws DataLoaderException 
     */
    public static
            void main(
                    String[] args) throws DataLoaderException
    {
        LOG.info("Do not forget to set rating file path to full data");
        //sampleByRating(20, "20%RatingsSampledByRating.csv");
        sampleByUser(20, "20%RatingsSampledByUser.csv");
    }

    /**
     * Sample data based on rating to the amount of percentage
     * @param percentage
     * @throws DataLoaderException 
     */
    private static void sampleByRating(final int percentage,final String fileName) throws DataLoaderException{        
        final DataLoader loader = new DataLoader();
        final DataModel dataModel = loader.readData();
        dataModel.printStatistic();

        final DataModel sampledDataModel = dataModel.sampleRatings(percentage);
        sampledDataModel.printStatistic();
        sampledDataModel.writeRatingsToFile(fileName);
    }
    
    /**
     * Sample data based on user to the amount of percentage
     * @param percentage
     * @throws DataLoaderException 
     */
    private static void sampleByUser(final int percentage,final String fileName) throws DataLoaderException{
        final DataLoader loader = new DataLoader();
        final DataModel dataModel = loader.readData();
        dataModel.printStatistic();

        final DataModel sampledDataModel = dataModel.sampleUsers(percentage);
        sampledDataModel.printStatistic();
        sampledDataModel.writeRatingsToFile(fileName);
    }
}
