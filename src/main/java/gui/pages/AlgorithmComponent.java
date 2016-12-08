package gui.pages;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import gui.model.Algorithms;
import gui.model.ConfigData;
import interfaces.AbstractRecommender;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import util.ClassInstantiator;

/**
 * @author FBM
 *
 */
public class AlgorithmComponent {
	private int id;
	private Label algorithmName;
	private ComboBox<Algorithms> algorithmCombobox;
	private RadioButton useLowLevel = new RadioButton("Use LowLevel features");
	private RadioButton useTag = new RadioButton("Use tag");
	private RadioButton useGenre = new RadioButton("Use genre");
	private RadioButton useRating = new RadioButton("Use ratings");
	private ToggleGroup options = new ToggleGroup();
	private AlgorithmParametersComponent algorithmParameters;

	/**
	 * @param id
	 * 
	 */
	public AlgorithmComponent(int id) {
		this.id = id;
		algorithmParameters = new AlgorithmParametersComponent(id);
		algorithmName = new Label("Algorithm");
		algorithmCombobox = new ComboBox<>(FXCollections.observableArrayList(Algorithms.values()));
		useLowLevel.setToggleGroup(options);
		useTag.setToggleGroup(options);
		useGenre.setToggleGroup(options);
		useRating.setToggleGroup(options);
		useLowLevel.setSelected(true);
		initListeners();
	}

	/**
	 * 
	 */
	private void initListeners() {
		algorithmCombobox.setOnAction(event -> {
			addParametersToDataModel();
		});
	}

	private void addParametersToDataModel() {
		final String removeKey = "ALGORITHM_" + id;
		cleanMap(removeKey);

		final String key1 = "ALGORITHM_" + id + "_NAME";
		ConfigData.instance.ALGORITHM_PARAMETERS.put(key1, new SimpleStringProperty(""));
		ConfigData.instance.ALGORITHM_PARAMETERS.get(key1)
				.bind(new SimpleStringProperty(algorithmCombobox.getValue().getText().get()));

		final String key2 = "ALGORITHM_" + id + "_USE_LOW_LEVEL";
		ConfigData.instance.ALGORITHM_PARAMETERS.put(key2, new SimpleStringProperty(""));
		ConfigData.instance.ALGORITHM_PARAMETERS.get(key2)
				.bind(new SimpleStringProperty(String.valueOf(useLowLevel.selectedProperty().get())));

		final String key3 = "ALGORITHM_" + id + "_USE_GENRE";
		ConfigData.instance.ALGORITHM_PARAMETERS.put(key3, new SimpleStringProperty(""));
		ConfigData.instance.ALGORITHM_PARAMETERS.get(key3)
				.bind(new SimpleStringProperty(String.valueOf(useGenre.selectedProperty().get())));

		final String key4 = "ALGORITHM_" + id + "_USE_TAG";
		ConfigData.instance.ALGORITHM_PARAMETERS.put(key4, new SimpleStringProperty(""));
		ConfigData.instance.ALGORITHM_PARAMETERS.get(key4)
				.bind(new SimpleStringProperty(String.valueOf(useTag.selectedProperty().get())));

		final String key5 = "ALGORITHM_" + id + "_USE_RATING";
		ConfigData.instance.ALGORITHM_PARAMETERS.put(key5, new SimpleStringProperty(""));
		ConfigData.instance.ALGORITHM_PARAMETERS.get(key5)
				.bind(new SimpleStringProperty(String.valueOf(useRating.selectedProperty().get())));

		final String selectedAlgorithmName = "algorithms." + algorithmCombobox.getValue().getText().get();
		final AbstractRecommender instantiateClass = (AbstractRecommender) ClassInstantiator
				.instantiateClass(selectedAlgorithmName);
		final Map<String, Map<String, String>> configurabaleParameters = instantiateClass.getConfigurabaleParameters();

		algorithmParameters.setParameters(configurabaleParameters);
	}

	/**
	 * @param key1
	 */
	private void cleanMap(String key1) {
		final Map<String, StringProperty> newMap = new LinkedHashMap<>();
		for (Entry<String, StringProperty> entry : ConfigData.instance.ALGORITHM_PARAMETERS.entrySet()) {
			if (!entry.getKey().contains(key1)) {
				newMap.put(entry.getKey(), entry.getValue());
			}
		}
		ConfigData.instance.ALGORITHM_PARAMETERS.clear();
		ConfigData.instance.ALGORITHM_PARAMETERS.putAll(newMap);
	}

	public Parent getLayout() {
		algorithmParameters = new AlgorithmParametersComponent(id);
		final VBox radioGroup = new VBox(2, useLowLevel, useGenre, useTag, useRating);
		final HBox hBox = new HBox(5.0, algorithmName, algorithmCombobox, radioGroup);

		final VBox main = new VBox(5.0, hBox, algorithmParameters.getLayout());
		main.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
				+ "-fx-border-insets: 5;" + "-fx-border-radius: 5;" + "-fx-border-color: gray;");
		return main;
	}
}
