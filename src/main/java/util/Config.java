package util;

import java.io.File;

import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.log4j.Logger;

/**
 * @author FBM
 *
 */
public class Config {
	private static final Logger LOG = Logger.getLogger(Config.class.getSimpleName());
	private static FileBasedConfigurationBuilder<FileBasedConfiguration> builder ;
	static {
		final Parameters params = new Parameters();
		builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
				.configure(params.fileBased().setFile(new File("config.properties")));
		builder.setAutoSave(true);
	}

	public static String getString(final String key) {
		try {
			return builder.getConfiguration().getString(key);
		} catch (ConfigurationException e) {
			LOG.error(e.getMessage(),e);
			System.exit(1);
			return null;
		}
	}
	
	public static String getString(final String key,final String def) {
		try {
			return builder.getConfiguration().getString(key,def);
		} catch (ConfigurationException e) {
			LOG.error(e.getMessage(),e);
			System.exit(1);
			return null;
		}
	}
	
	public static boolean getBoolean(final String key) {
		try {
			return builder.getConfiguration().getBoolean(key);
		} catch (ConfigurationException e) {
			LOG.error(e.getMessage(),e);
			System.exit(1);
			return false;
		}
	}
	
	public static double getDouble(final String key) {
		try {
			return builder.getConfiguration().getDouble(key);
		} catch (ConfigurationException e) {
			LOG.error(e.getMessage(),e);
			System.exit(1);
			return 0.0;
		}
	}

	public static int getInt(final String key,final int def) {
		try {
			return builder.getConfiguration().getInt(key,def);
		} catch (ConfigurationException e) {
			LOG.error(e.getMessage(),e);
			System.exit(1);
			return 0;
		}
	}
	
	public static int getInt(final String key) {
		try {
			return builder.getConfiguration().getInt(key);
		} catch (ConfigurationException e) {
			LOG.error(e.getMessage(),e);
			System.exit(1);
			return 0;
		}
	}
	
	public static long getLong(final String key) {
		try {
			return builder.getConfiguration().getLong(key);
		} catch (ConfigurationException e) {
			LOG.error(e.getMessage(),e);
			System.exit(1);
			return 0;
		}
	}
	
	public static Long getLong(final String key,final Long def) {
		try {
			return builder.getConfiguration().getLong(key,def);
		} catch (ConfigurationException e) {
			LOG.error(e.getMessage(),e);
			System.exit(1);
			return null;
		}
	}

	public static void add(){
		try {
			FileHandler handler = new FileHandler(builder.getConfiguration());
			builder.getConfiguration().addProperty("XXXXXXX", "XXXXXXXX");
			handler.save();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param string
	 * @param b
	 * @return
	 */
	public static boolean getBoolean(final String key,final boolean def) {
		try {
			return builder.getConfiguration().getBoolean(key,def);
		} catch (ConfigurationException e) {
			LOG.error(e.getMessage(),e);
			System.exit(1);
			return false;
		}
	}
}
