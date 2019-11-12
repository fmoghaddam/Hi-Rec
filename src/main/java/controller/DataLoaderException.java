package controller;

/**
 * @author FBM
 */
public class DataLoaderException extends Exception {

    private static final long serialVersionUID = 866963551019665411L;

    public DataLoaderException(final String text, final Exception exception) {
        super(text, exception);
    }
}
