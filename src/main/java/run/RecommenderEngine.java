package run;

import java.awt.Toolkit;
import java.io.IOException;

import org.apache.log4j.Logger;

import controller.DataLoader;
import model.DataModel;

/**
 * This class is responsible for running main code
 * 
 * @author FBM
 *
 */
public final class RecommenderEngine {

    /**
     * Logger for this class
     */
    private static final Logger LOG = Logger
            .getLogger(RecommenderEngine.class.getCanonicalName());

    /**
     * Main function
     * 
     * @param args
     */
    public static
            void main(
                    String[] args)
    {
        final DataLoader loader = new DataLoader();
        final DataModel dataModel = loader.readData();
        dataModel.printStatistic();
        final ParallelEvaluator evaluator = new ParallelEvaluator(dataModel);
        evaluator.evaluate();
        Toolkit.getDefaultToolkit().beep();
        waitTillKeyPress();
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
