package util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * This class is responsible for reading attributes fom config file
 * @author FBM
 *
 */
public final class Config {
    
    private static final String BUNDLE_NAME = "config";
    private static final Logger LOG = Logger.getLogger(Config.class.getSimpleName());
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(Config.BUNDLE_NAME);

    /**
     * Returns a boolean from the config.properties file.
     * 
     * @param key
     * @param defBoolean
     *            default boolean being returned, if no value is found
     * @return
     */
    public static boolean getBoolean(final String key, final boolean defBoolean) {
	try {
	    return Boolean.parseBoolean(Config.RESOURCE_BUNDLE.getString(key));
	} catch (final Exception e) {
	    Config.LOG.warn(e.getMessage(), e);
	    return defBoolean;
	}
    }

    /**
     * Returns an integer from the config.properties file.
     * 
     * @param key
     * @param defInt
     *            default integer being returned, if no value is found
     * @return
     */
    public static int getInt(final String key, final int defInt) {
	try {
	    return Integer.parseInt(Config.RESOURCE_BUNDLE.getString(key));
	} catch (final Exception e) {
	    Config.LOG.warn(e.getMessage());
	    return defInt;
	}
    }

    /**
     * Returns a long integer from the config.properties file.
     * 
     * @param key
     * @param defLong
     *            default long integer being returned, if no value is found
     * @return
     */
    public static long getLong(final String key, final long defLong) {
	try {
	    return Long.parseLong(Config.RESOURCE_BUNDLE.getString(key));
	} catch (final Exception e) {
	    Config.LOG.warn(e.getMessage());
	    return defLong;
	}
    }

    /**
     * Returns a string from the config.properties file.
     * 
     * @param key
     * @param defString
     *            default string being returned, if no value is found
     * @return
     */
    public static String getString(final String key, final String defString) {
	try {
	    return Config.RESOURCE_BUNDLE.getString(key);
	} catch (final MissingResourceException e) {
	    Config.LOG.warn(e.getMessage());
	    return defString;
	}
    }

    /**
     * Private constructor to prevent creation with new.
     */
    private Config() {
    }

    public static double getDouble(final String key, final double defLong) {
	try {
	    return Double.parseDouble(Config.RESOURCE_BUNDLE.getString(key));
	} catch (final Exception e) {
	    Config.LOG.warn(e.getMessage());
	    return defLong;
	}
    }

}
