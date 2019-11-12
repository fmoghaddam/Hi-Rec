package run;

import controller.DataLoader;
import model.DataModel;
import model.Globals;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

/**
 * This class is responsible for running main code
 *
 * @author FBM
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
    public static void main(
            String[] args) {
        try {
            CleanLogFile();
            final DataLoader loader = new DataLoader();
            final DataModel dataModel = loader.readData();
            dataModel.printStatistic();
            final ParallelEvaluator evaluator = new ParallelEvaluator(dataModel);
            evaluator.evaluate();
            Toolkit.getDefaultToolkit().beep();
            waitTillKeyPress();
        } catch (final Exception exception) {
            LOG.error(exception.getMessage());
        }
    }

    /**
     *
     */
    private static void CleanLogFile() {
        try {
            Files.deleteIfExists(Paths.get("log/Recommender.log"));
        } catch (NoSuchFileException x) {
        } catch (DirectoryNotEmptyException x) {
        } catch (IOException x) {
        }
    }

    /**
     * Start the application in integrated mode
     */
    public static void execute() {
        final Thread thread = new Thread(() -> {
            try {
                CleanLogFile();
                Globals.readData();
                final DataLoader loader = new DataLoader();
                final DataModel dataModel = loader.readData();
                dataModel.printStatistic();
                final ParallelEvaluator evaluator = new ParallelEvaluator(dataModel);
                evaluator.evaluate();
            } catch (final Exception exception) {
                LOG.error(exception.getMessage());
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Wait till user press any key
     */
    private static void waitTillKeyPress() {
        LOG.info("Press enter to exit....");
        try {
            System.in.read();
        } catch (final IOException exception) {
            LOG.error(exception.getMessage());
        }
    }
}
