package gui.model;

import java.util.LinkedHashMap;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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

	/**
	 * @return
	 */
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