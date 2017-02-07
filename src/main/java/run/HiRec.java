package run;

import java.awt.Toolkit;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

import controller.DataLoader;
import interfaces.DataLoaderInterface;
import model.DataModel;
import model.Globals;

/**
 * This class is responsible for running main code
 * 
 * @author FBM
 *
 */
public final class HiRec {

    /**
     * Logger for this class
     */
    private static final Logger LOG = Logger
            .getLogger(HiRec.class.getCanonicalName());

    /**
     * Main function
     * 
     * @param args
     */
    public static
    void main(
            String[] args)
    {
        try{
            CleanLogFile();
            final DataLoaderInterface loader = new DataLoader();
            final DataModel dataModel = loader.readData();
            dataModel.printStatistic();
            final ParallelEvaluator evaluator = new ParallelEvaluator(dataModel);
            evaluator.evaluate();
            Toolkit.getDefaultToolkit().beep();
            waitTillKeyPress();
        }catch(final Exception exception){
            LOG.error(exception.getMessage());
        }
    }

    /**
     * 
     */
    private static void CleanLogFile() {
        try {
            Files.deleteIfExists(Paths.get("log/Recommender.log"));
        } catch (final IOException exception) {
        }
    }

    /**
     * Start the application in integrated mode
     */
    public static void execute(){    	
        final Thread thread = new Thread(() -> {
            try{
                CleanLogFile();
                Globals.readData();
                final DataLoaderInterface loader = new DataLoader();
                final DataModel dataModel = loader.readData();
                dataModel.printStatistic();
                final ParallelEvaluator evaluator = new ParallelEvaluator(dataModel);
                evaluator.evaluate();
            }catch(final Exception exception){
                LOG.error(exception.getMessage());
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Wait till user press any key
     */
    private static
    void waitTillKeyPress() {
        LOG.info("Press enter to exit....");
        try {
            System.in.read();
        } catch (final IOException exception) {
            LOG.error(exception.getMessage());
        }
    }
}
