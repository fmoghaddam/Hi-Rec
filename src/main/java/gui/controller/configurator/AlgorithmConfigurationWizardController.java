package gui.controller.configurator;

import com.google.common.eventbus.Subscribe;
import gui.WizardControllerInterface;
import gui.messages.DataSourceChanged;
import gui.model.ConfigData;
import gui.model.ErrorMessage;
import interfaces.AbstractRecommender;
import interfaces.Recommender;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import util.ClassInstantiator;
import util.MessageBus;

import java.net.URL;
import java.util.*;

public class AlgorithmConfigurationWizardController implements Initializable, WizardControllerInterface {

    private ErrorMessage errorMessage;
    @FXML
    private Label algorithmName;

    @FXML
    private VBox similarityConfigurationSectionVBox;

    @FXML
    private RadioButton useVisualFeaturesRadioButton;

    @FXML
    private ToggleGroup similarityToggleGroup;

    @FXML
    private RadioButton useGenreRadioButton;

    @FXML
    private RadioButton useTagRadioButton;

    @FXML
    private RadioButton useRatingsRadioButton;

    @FXML
    private GridPane parametersGridPane;

    private Map<String, TextField> parametersMap;
    private int id;

    private void cleanMap(String key1) {
        final Map<String, StringProperty> newMap = new LinkedHashMap<>();
        for (Map.Entry<String, StringProperty> entry : ConfigData.instance.ALGORITHM_PARAMETERS.entrySet()) {
            if (!entry.getKey().contains(key1)) {
                newMap.put(entry.getKey(), entry.getValue());
            }
        }
        ConfigData.instance.ALGORITHM_PARAMETERS.clear();
        ConfigData.instance.ALGORITHM_PARAMETERS.putAll(newMap);
    }

    public void createConfigurationGUI(Recommender algorithm, int id) {
        this.id = id;
        addParametersToDataModel(algorithm);
        this.algorithmName.setText(algorithm.getClass().getSimpleName());
    }

    private void addParametersToDataModel(Recommender algorithm) {
        String alogirhtmName = algorithm.getClass().getSimpleName();
        final String removeKey = "ALGORITHM_" + id;
        cleanMap(removeKey);

        final String key1 = "ALGORITHM_" + id + "_NAME";
        ConfigData.instance.ALGORITHM_PARAMETERS.put(key1, new SimpleStringProperty(alogirhtmName));

        final String key2 = "ALGORITHM_" + id + "_USE_LOW_LEVEL";
        ConfigData.instance.ALGORITHM_PARAMETERS.put(key2, new SimpleStringProperty(""));
        ConfigData.instance.ALGORITHM_PARAMETERS.get(key2)
                .bind(useVisualFeaturesRadioButton.selectedProperty().asString());

        final String key3 = "ALGORITHM_" + id + "_USE_GENRE";
        ConfigData.instance.ALGORITHM_PARAMETERS.put(key3, new SimpleStringProperty(""));
        ConfigData.instance.ALGORITHM_PARAMETERS.get(key3)
                .bind(useGenreRadioButton.selectedProperty().asString());

        final String key4 = "ALGORITHM_" + id + "_USE_TAG";
        ConfigData.instance.ALGORITHM_PARAMETERS.put(key4, new SimpleStringProperty(""));
        ConfigData.instance.ALGORITHM_PARAMETERS.get(key4)
                .bind(useTagRadioButton.selectedProperty().asString());

        final String key5 = "ALGORITHM_" + id + "_USE_RATING";
        ConfigData.instance.ALGORITHM_PARAMETERS.put(key5, new SimpleStringProperty(""));
        ConfigData.instance.ALGORITHM_PARAMETERS.get(key5)
                .bind(useRatingsRadioButton.selectedProperty().asString());


        final Map<String, Map<String, String>> configurabaleParameters = algorithm.getConfigurabaleParameters();

        setParameters(configurabaleParameters);
    }

    @Override
    public boolean isValid() {
        final StringBuilder totalError = new StringBuilder();
        boolean isValid = true;
        for (Map.Entry<String, TextField> entry : this.parametersMap.entrySet()) {
            if (entry.getValue().getText().isEmpty()) {
                totalError.append("The value of \"" + entry.getKey() + "\" is Empty").append("\n");
                isValid = false;
            }
        }
        if (similarityToggleGroup.getSelectedToggle() == null) {
            isValid = false;
            totalError.append("Please specify the similarity type").append("\n");
        }
        errorMessage.setText(totalError.toString());
        return isValid;
    }

    @Subscribe
    private void dataSourceChanged(final DataSourceChanged message) {
        enableRadioButtons();
    }

    public void enableRadioButtons() {
        if (ConfigData.instance.LOW_LEVEL_FILE_PATH == null || ConfigData.instance.LOW_LEVEL_FILE_PATH.isEmpty().get()) {
            useVisualFeaturesRadioButton.setDisable(true);
            useVisualFeaturesRadioButton.setSelected(false);
        } else {
            useVisualFeaturesRadioButton.setDisable(false);
        }
        if (ConfigData.instance.GENRE_FILE_PATH == null || ConfigData.instance.GENRE_FILE_PATH.isEmpty().get()) {
            useGenreRadioButton.setDisable(true);
            useGenreRadioButton.setSelected(false);
        } else {
            useGenreRadioButton.setDisable(false);
        }
        if (ConfigData.instance.TAG_FILE_PATH == null || ConfigData.instance.TAG_FILE_PATH.isEmpty().get()) {
            useTagRadioButton.setDisable(true);
            useTagRadioButton.setSelected(false);
        } else {
            useTagRadioButton.setDisable(false);
        }
        if (ConfigData.instance.RATING_FILE_PATH == null || ConfigData.instance.RATING_FILE_PATH.isEmpty().get()) {
            useRatingsRadioButton.setDisable(true);
            useRatingsRadioButton.setSelected(false);
        } else {
            useRatingsRadioButton.setDisable(false);
        }
    }

    @Override
    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }


    /**
     *
     */
    public void fillWithSampleData() {
        useRatingsRadioButton.setSelected(true);
        for (Map.Entry<String, TextField> entry : parametersMap.entrySet()) {
            entry.getValue().setText("10");
        }
    }

    @Override
    public void reset() {

    }

    /**
     * @param map
     */
    public void setParameters(final Map<String, Map<String, String>> map) {
        int i = 0;
        this.parametersGridPane.getChildren().clear();
        this.parametersMap.clear();
        for (Map<String, String> subMap : map.values()) {
            for (Map.Entry<String, String> entity : subMap.entrySet()) {
                final String value = entity.getValue();
                final Label parameterName = new Label(value);
                final TextField parameterValue = new TextField();
                parametersGridPane.add(parameterName, 0, i);
                parametersGridPane.add(parameterValue, 1, i);
                i++;
                final String key = "ALGORITHM_" + id + "_" + entity.getKey();
                ConfigData.instance.ALGORITHM_PARAMETERS.put(key, new SimpleStringProperty(""));
                ConfigData.instance.ALGORITHM_PARAMETERS.get(key).bind(parameterValue.textProperty());
                this.parametersMap.put(value, parameterValue);
            }
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        enableRadioButtons();
        errorMessage = new ErrorMessage();
        parametersMap = new HashMap<>();
        MessageBus.getInstance().register(this);
    }

    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    @Override
    public void fillWithPropertyFile(Properties properties) {
        String key1 = "ALGORITHM_" + id + "_NAME";
        String alogirhtmName = properties.getProperty(key1);
        String key2 = "ALGORITHM_" + id + "_USE_LOW_LEVEL";
        useVisualFeaturesRadioButton.setSelected(Boolean.parseBoolean(properties.getProperty(key2)));
        String key3 = "ALGORITHM_" + id + "_USE_GENRE";
        useGenreRadioButton.setSelected(Boolean.parseBoolean(properties.getProperty(key3)));
        String key4 = "ALGORITHM_" + id + "_USE_TAG";
        useTagRadioButton.setSelected(Boolean.parseBoolean(properties.getProperty(key4)));
        String key5 = "ALGORITHM_" + id + "_USE_RATING";
        useRatingsRadioButton.setSelected(Boolean.parseBoolean(properties.getProperty(key5)));


        String selectedAlgorithmName = "algorithms." + alogirhtmName;
        AbstractRecommender instantiateClass = (AbstractRecommender) ClassInstantiator
                .instantiateClass(selectedAlgorithmName);
        Map<String, Map<String, String>> configurabaleParameters = instantiateClass.getConfigurabaleParameters();
        int i = 0;
        for (Map<String, String> subMap : configurabaleParameters.values()) {
            for (Map.Entry<String, String> entity : subMap.entrySet()) {
                String value = entity.getValue();
                TextField textField = (TextField) getNodeFromGridPane(parametersGridPane, 1, i);
                i++;
                String key = "ALGORITHM_" + id + "_" + entity.getKey();
                textField.setText(properties.getProperty(key));
            }
        }
    }
}
