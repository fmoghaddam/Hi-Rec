package gui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class ConfigData {

	public StringProperty LOW_LEVEL_FILE_PATH = new SimpleStringProperty();
	public StringProperty LOW_LEVEL_FILE_SEPARATOR = new SimpleStringProperty();

	public StringProperty GENRE_FILE_PATH = new SimpleStringProperty();
	public StringProperty GENRE_FILE_SEPARATOR = new SimpleStringProperty();

	public StringProperty TAG_FILE_PATH = new SimpleStringProperty();
	public StringProperty TAG_FILE_SEPARATOR = new SimpleStringProperty();

	public StringProperty RATING_FILE_PATH = new SimpleStringProperty();
	public StringProperty RATING_FILE_SEPARATOR = new SimpleStringProperty();

	public StringProperty NUMBER_OF_FOLDS = new SimpleStringProperty();
	public StringProperty RANDOMIZATION_SEED = new SimpleStringProperty();

	public StringProperty SIMILARITY_FUNCTION = new SimpleStringProperty();
	public StringProperty TOP_N = new SimpleStringProperty();
	public StringProperty MINIMUM_THRESHOLD_FOR_POSITIVE_RATING = new SimpleStringProperty();
	public StringProperty AT_N = new SimpleStringProperty();
	public StringProperty DROP_POPULAR_ITEM = new SimpleStringProperty();
	public StringProperty DROP_POPULAR_ITEM_NUMBER = new SimpleStringProperty();
	public StringProperty METRICS = new SimpleStringProperty();
	public StringProperty CALCULATE_TTEST = new SimpleStringProperty();

	public StringProperty NUMBER_OF_CONFIGURATION = new SimpleStringProperty();

	public Map<String, StringProperty> ALGORITHM_PARAMETERS = new LinkedHashMap<>();

	public StringProperty RUN_ALGORITHMS_PARALLEL = new SimpleStringProperty();
	public StringProperty RUN_ALGORITHMS_NUMBER_OF_THREAD = new SimpleStringProperty();
	
	public StringProperty RUN_FOLDS_PARALLEL = new SimpleStringProperty();
	public StringProperty RUN_FOLDS_NUMBER_OF_THREAD = new SimpleStringProperty();

	public static ConfigData instance = new ConfigData();

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return firstPagePrint();
	}

	public void removeAllAlgoParameterWithId(int id) {
		List<String> collect = ConfigData.instance.ALGORITHM_PARAMETERS.keySet().stream()
				.filter(s -> s.contains("ALGORITHM_" + id))
				.collect(Collectors.toList());
		for (String entry : collect) {
			if (entry.contains("ALGORITHM_" + id)) {
				ConfigData.instance.ALGORITHM_PARAMETERS.remove(entry);
			}

		}

	}

	public String allConfigToString() {
		final StringBuilder result = new StringBuilder();
		try {
			final Field[] allFields = ConfigData.instance.getClass().getDeclaredFields();
			for (final Field field : allFields) {
				if (field.getName().contains("instance")) {
					continue;
				}
				if (field.get(ConfigData.instance) instanceof StringProperty) {
					if (((StringProperty) field.get(ConfigData.instance)).get() != null) {
						result.append(field.getName()).append("=")
								.append(((StringProperty) field.get(ConfigData.instance)).get()).append("\n");
					} else {
						result.append(field.getName()).append("=").append("\n");
					}
				} else if (field.get(ConfigData.instance) instanceof Map) {
					@SuppressWarnings("unchecked") final Map<String, StringProperty> map = (Map<String, StringProperty>) field
							.get(ConfigData.instance);
					for (final Map.Entry<String, StringProperty> entry : map.entrySet()) {
						if (entry.getValue().get() != null) {
							result.append(entry.getKey()).append("=").append(entry.getValue().get()).append("\n");
						} else {
							result.append(entry.getKey()).append("=").append("\n");
						}
					}
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return result.toString();
	}

	public void loadPropertiesFromFile(String path) {
		try (InputStream input = new FileInputStream(path)) {

			Properties prop = new Properties();

			prop.load(input);

			ALGORITHM_PARAMETERS.clear();

			try {
				final Field[] allFields = ConfigData.instance.getClass().getDeclaredFields();
				for (final Field field : allFields) {
					if (field.getName().contains("instance")) {
						continue;
					}
					if (field.get(ConfigData.instance) instanceof StringProperty) {
						((StringProperty) field.get(ConfigData.instance))
								.setValue(prop.getProperty(field.getName(), ""));
					} else if (field.get(ConfigData.instance) instanceof Map) {
						for (Map.Entry<Object, Object> entry : prop.entrySet()) {
							String key = ((String) entry.getKey());
							String value = ((String) entry.getValue());

							if (key.startsWith("ALGORITHM")) {
								ALGORITHM_PARAMETERS.put(key, new SimpleStringProperty(value));
							}
						}
					}
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private String firstPagePrint() {
		StringBuilder result = new StringBuilder();
		result.append("LOW_LEVEL_FILE_PATH=").append(LOW_LEVEL_FILE_PATH.get()).append("\n")
				.append("LOW_LEVEL_FILE_SEPARATOR=").append(LOW_LEVEL_FILE_SEPARATOR.get()).append("\n")
				.append("GENRE_FILE_PATH=").append(GENRE_FILE_PATH.get()).append("\n").append("GENRE_FILE_SEPARATOR=")
				.append(GENRE_FILE_SEPARATOR.get()).append("\n").append("TAG_FILE_PATH=").append(TAG_FILE_PATH.get())
				.append("\n").append("TAG_FILE_SEPARATOR=").append(TAG_FILE_SEPARATOR.get()).append("\n")
				.append("RATING_FILE_PATH=").append(RATING_FILE_PATH.get()).append("\n")
				.append("RATING_FILE_SEPARATOR=").append(RATING_FILE_SEPARATOR.get()).append("\n");
		return result.toString();
	}

}