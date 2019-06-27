package gui.views;

import gui.WizardControllerInterface;
import gui.model.ConfigData;
import gui.model.ErrorMessage;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DatasetWizardController implements Initializable, WizardControllerInterface {
    private ErrorMessage errorMessage;

    @FXML
    private VBox datasetsVbox;

    @FXML
    private TextField randomizationSeedTextField;

    @FXML
    private TextField runAlgorithmNumberOfThreadTextField;

    @FXML
    private TextField runFoldsNumberOfThreadTextField;

    @FXML
    private Slider numberOfFoldsSlider;

    @FXML
    private CheckBox runFoldsParallelCheckBox;

    @FXML
    private CheckBox runAlgorithmParallelCheckBox;

    @FXML
    private CheckBox randomizationSeedCheckBox;

    @FXML
    private Label numberOfFoldsLabel;

    private List<Pair<Parent, DataSourceWizardController>> dataSources;


    private void initFoldNumberAttributes() {
        numberOfFoldsLabel.setText(String.valueOf((int) numberOfFoldsSlider.getValue()));
        ConfigData.instance.NUMBER_OF_FOLDS.bind(new SimpleStringProperty(String.valueOf((int) numberOfFoldsSlider.getValue())));
        numberOfFoldsSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            numberOfFoldsLabel.setText(String.valueOf((int) numberOfFoldsSlider.getValue()));
            ConfigData.instance.NUMBER_OF_FOLDS.bind(new SimpleStringProperty(String.valueOf((int) numberOfFoldsSlider.getValue())));
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        errorMessage = new ErrorMessage();
        dataSources = new ArrayList<>();
        final String home = System.getProperty("user.dir");

        String[] mandatoryFiles = {"Rating", "LowLevel Features", "Genre", "Tag"};
        String[] defaultPaths = {home + "\\data\\5%RatingsSampledByRating.csv",
                home + "\\data\\LLVisualFeatures13K_QuantileLog.csv",
                home + "\\data\\Genre.csv", home + "\\data\\Tag.csv"};
        StringProperty[] correspondFilePathConfigProperty = {
                ConfigData.instance.RATING_FILE_PATH, ConfigData.instance.LOW_LEVEL_FILE_PATH,
                ConfigData.instance.GENRE_FILE_PATH, ConfigData.instance.TAG_FILE_PATH};
        StringProperty[] correspondSeperatorConfigProperty = {
                ConfigData.instance.RATING_FILE_SEPARATOR, ConfigData.instance.LOW_LEVEL_FILE_SEPARATOR,
                ConfigData.instance.GENRE_FILE_SEPARATOR, ConfigData.instance.TAG_FILE_SEPARATOR};
        for (int i = 0; i < mandatoryFiles.length; i++) {
            createNewDataSource(mandatoryFiles[i], defaultPaths[i],
                    correspondFilePathConfigProperty[i],
                    correspondSeperatorConfigProperty[i]);
        }

        initFoldNumberAttributes();
        initFoldParallelAttributes();
        initAlgorithmParallelAttributes();
        initRandimizationSeedAttributes();
    }


    private void initFoldParallelAttributes() {
        runFoldsNumberOfThreadTextField.setDisable(true);
        ConfigData.instance.RUN_FOLDS_PARALLEL.bind(new SimpleStringProperty(String.valueOf(false)));
        runFoldsParallelCheckBox.selectedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    runFoldsNumberOfThreadTextField.setDisable(!newValue);
                    ConfigData.instance.RUN_FOLDS_PARALLEL.bind(new SimpleStringProperty(String.valueOf(newValue)));
                    if (!newValue) {
                        runFoldsNumberOfThreadTextField.setText("");
                        errorMessage.setText("");
                    }
                    final StringProperty textProperty = runFoldsNumberOfThreadTextField.textProperty();
                    ConfigData.instance.RUN_FOLDS_NUMBER_OF_THREAD.bind(textProperty);
                });
    }

    private void initAlgorithmParallelAttributes() {
        runAlgorithmNumberOfThreadTextField.setDisable(true);
        ConfigData.instance.RUN_ALGORITHMS_PARALLEL.bind(new SimpleStringProperty(String.valueOf(false)));
        runAlgorithmParallelCheckBox.selectedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    runAlgorithmNumberOfThreadTextField.setDisable(!newValue);
                    ConfigData.instance.RUN_ALGORITHMS_PARALLEL
                            .bind(new SimpleStringProperty(String.valueOf(newValue)));
                    if (!newValue) {
                        runAlgorithmNumberOfThreadTextField.setText("");
                        errorMessage.setText("");
                    }
                    final StringProperty textProperty = runAlgorithmNumberOfThreadTextField.textProperty();
                    ConfigData.instance.RUN_ALGORITHMS_NUMBER_OF_THREAD.bind(textProperty);
                });
    }

    /**
     *
     */
    private void initRandimizationSeedAttributes() {
        randomizationSeedTextField.setDisable(true);
        randomizationSeedCheckBox.selectedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    randomizationSeedTextField.setDisable(!newValue);
                    if (!newValue) {
                        randomizationSeedTextField.setText("");
                        errorMessage.setText("");
                    }
                    final StringProperty textProperty = randomizationSeedTextField.textProperty();
                    ConfigData.instance.RANDOMIZATION_SEED.bind(textProperty);
                });
    }

    private void createNewDataSource(String fileLabel, String defaultPath, StringProperty filePathProperty, StringProperty seperatorProperty) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/DataSourceWizardFXMLView.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DataSourceWizardController controller = fxmlLoader.getController();
        controller.setFileLabel(fileLabel + " File: ");
        controller.setDefaultPath(defaultPath);
        controller.fillWithSampleData();
        Pair<Parent, DataSourceWizardController> pair = new Pair<>(root, controller);
        dataSources.add(pair);
        datasetsVbox.getChildren().add(datasetsVbox.getChildren().size() - 1, root);


        //bindings
        controller.getSeperatorComboSelector().getSelectionModel().selectFirst();
        seperatorProperty.bind(controller.getSeperatorComboSelector().getValue().getText());
        controller.getSeperatorComboSelector().setOnAction(event -> {
            seperatorProperty.bind(controller.getSeperatorComboSelector().getValue().getText());
        });
        filePathProperty.bind(controller.getFilePathField().textProperty());
    }

    @Override
    public boolean isValid() {
        final StringBuilder overalErrorMessage = new StringBuilder();
        boolean overalError = false;
        //TODO: FIX FILE SELECTOR VALIDATION
//        if (ratingFileText.getText() == null || ratingFileText.getText().isEmpty()) {
//            overalErrorMessage.append("The rating file should be selected").append("\n");
//            overalError = true;
//        }
        if (randomizationSeedCheckBox.isSelected()) {
            try {
                Double.parseDouble(randomizationSeedTextField.getText());
            } catch (final Exception exception) {
                overalErrorMessage.append("Seed is not valid").append("\n");
                overalError = true;
            }
        }
        if (runAlgorithmParallelCheckBox.isSelected()) {
            if (!runAlgorithmNumberOfThreadTextField.getText().isEmpty()) {
                try {
                    Integer.parseInt(runAlgorithmNumberOfThreadTextField.getText());
                } catch (final Exception exception) {
                    overalErrorMessage.append("Number of cores for running algorithms in parallel mode is not valid").append("\n");
                    overalError = true;
                }
            }
        }
        if (runFoldsParallelCheckBox.isSelected()) {
            if (!runFoldsNumberOfThreadTextField.getText().isEmpty()) {
                try {
                    Integer.parseInt(runFoldsNumberOfThreadTextField.getText());
                } catch (final Exception exception) {
                    overalErrorMessage.append("Number of cores for running folds in parallel mode is not valid").append("\n");
                    overalError = true;
                }
            }
        }

        if (!overalError) {
            errorMessage.setText("");
            return true;
        } else {
            errorMessage.setText(overalErrorMessage.toString());
            return false;
        }
    }

    @FXML
    void onNewDataSourceAction(ActionEvent event) {
        //TODO: IMPLEMENT DATASOURCE ADDING

    }

    @FXML
    void onRemoveAction(ActionEvent event) {
        //TODO: IMPLEMENT DATASOURCE REMOVING
    }

    @Override
    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void fillWithSampleData() {
        for (Pair<Parent, DataSourceWizardController> pair : dataSources) {
            pair.getValue().fillWithSampleData();
        }
    }

    @Override
    public void reset() {
        runAlgorithmNumberOfThreadTextField.setText("");
        runAlgorithmParallelCheckBox.setSelected(false);
        runFoldsNumberOfThreadTextField.setText("");
        runFoldsParallelCheckBox.setSelected(false);
        for (Pair<Parent, DataSourceWizardController> pair : dataSources) {
            pair.getValue().reset();
        }
    }
}
