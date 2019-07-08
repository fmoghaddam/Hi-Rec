package gui.controller;

import gui.WizardControllerInterface;
import gui.model.ConfigData;
import gui.model.ErrorMessage;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class AlgorithmConfigurationWizardController implements Initializable, WizardControllerInterface {


    @FXML
    private Label algorithmName;

    @FXML
    private ToggleGroup similarityToggleGroup;

    @FXML
    private VBox similarityConfigurationSectionVBox;

    @FXML
    private GridPane parametersGridPane;

    private Map<String, TextField> parametersMap;
    private int id;


    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public ErrorMessage getErrorMessage() {
        return null;
    }


    /**
     *
     */
    public void fillWithSampleData() {
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
        parametersMap = new HashMap<>();
    }
}
