package util;

import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.ConfigurationBuilderEvent;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.log4j.Logger;

/**
 * @author FBM
 */
public class Config {
    private static final Logger LOG = Logger.getLogger(Config.class.getSimpleName());
    private static ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration> builder;


    static {
        final Parameters params = new Parameters();
        builder = new ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                .configure(params.fileBased().setFileName("config.properties"));
        builder.setAutoSave(true);
        builder.addEventListener(ConfigurationBuilderEvent.CONFIGURATION_REQUEST,
                event -> builder.getReloadingController().checkForReloading(null));
    }

    public static String getString(final String key) {
        try {
            return builder.getConfiguration().getString(key);
        } catch (final Exception exception) {
            LOG.error("Key '" + key + "' does not exist in the config file!");
            return null;
        }
    }

    public static String getString(final String key, final String def) {
        try {
            return builder.getConfiguration().getString(key, def);
        } catch (final Exception exception) {
            LOG.error("Key '" + key + "' does not exist in the config file!");
            return null;
        }
    }

    public static boolean getBoolean(final String key) {
        try {
            final String value = builder.getConfiguration().getString(key);
            return Boolean.parseBoolean(value);
        } catch (final Exception exception) {
            LOG.error(key + "-" + exception.getMessage() + ". False will be returned");
            return false;
        }
    }

    public static double getDouble(final String key) {
        try {
            final String value = builder.getConfiguration().getString(key);
            return Double.parseDouble(value);
        } catch (final NumberFormatException exception) {
            return 0.0;
        } catch (final Exception exception) {
            LOG.error(key + "-" + exception.getMessage() + ". 0.0 will be returned");
            return 0.0;
        }
    }

    public static int getInt(final String key, final int def) {
        try {
            final String value = builder.getConfiguration().getString(key);
            return Integer.parseInt(value);
        } catch (final NumberFormatException exception) {
            return def;
        } catch (final Exception exception) {
            LOG.error(key + "-" + exception.getMessage() + ". " + def + " will be returned");
            return def;
        }
    }


    public static int getInt(final String key) {
        try {
            final String value = builder.getConfiguration().getString(key);
            return Integer.parseInt(value);
        } catch (final NumberFormatException exception) {
            return 0;
        } catch (final Exception exception) {
            LOG.error(key + "-" + exception.getMessage() + ". 0 will be returned");
            return 0;
        }
    }

    public static long getLong(final String key) {
        try {
            final String value = builder.getConfiguration().getString(key);
            return Long.parseLong(value);
        } catch (final NumberFormatException exception) {
            return 0;
        } catch (final Exception exception) {
            LOG.error(key + "-" + exception.getMessage() + ". 0 will be returned");
            return 0;
        }
    }

    public static Long getLong(final String key, final Long def) {
        try {
            final String value = builder.getConfiguration().getString(key);
            return Long.parseLong(value);
        } catch (final NumberFormatException exception) {
            return def;
        } catch (final Exception exception) {
            LOG.error(key + "-" + exception.getMessage() + ". " + def + " will be returned");
            return def;
        }
    }


    private static void add() {
        try {
            FileHandler handler = new FileHandler(builder.getConfiguration());
            builder.getConfiguration().addProperty("XXXXXXX", "XXXXXXXX");
            handler.save();
        } catch (ConfigurationException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public static boolean getBoolean(final String key, final boolean def) {
        try {
            final String value = builder.getConfiguration().getString(key);
            return Boolean.parseBoolean(value);
        } catch (final NumberFormatException exception) {
            return def;
        } catch (final Exception exception) {
            LOG.error(key + "-" + exception.getMessage() + ". " + def + " will be returned");
            return def;
        }
    }
}
