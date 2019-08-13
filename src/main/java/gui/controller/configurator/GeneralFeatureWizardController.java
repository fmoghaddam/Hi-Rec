package gui.controller.configurator;

import gui.WizardControllerInterface;
import gui.model.ConfigData;
import gui.model.ErrorMessage;
import gui.model.Metrics;
import gui.model.SimilarityFunctions;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GeneralFeatureWizardController implements Initializable, WizardControllerInterface {

    @FXML
    private CheckBox dropMostPopularItemsCheckBox;

    @FXML
    private CheckBox tTestCheckbox;

    @FXML
    private ListView<Metrics> metricsList;

    @FXML
    private ChoiceBox<SimilarityFunctions> similarityChoice;

    @FXML
    private TextField topNTextField;

    @FXML
    private TextField minimumRatingForPositiveRatingTextField;

    @FXML
    private TextField atNTextField;

    @FXML
    private TextField dropMostPopularItemsTextField;


    @FXML
    private TextField randomizationSeedTextField;

    @FXML
    private Slider numberOfThreadsForAlgorithmsSlider;

    @FXML
    private Slider numberOfThreadsForFoldsSlider;

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

    private ErrorMessage errorMessage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorMessage = new ErrorMessage();
        initMetricAttributes();
        initDropMostPopularItemAttributes();
        initSimilarityFunctionAttribute();
        bindToConfigData();
        initFoldNumberAttributes();
        initFoldParallelAttributes();
        initAlgorithmParallelAttributes();
        initRandimizationSeedAttributes();
    }

    private void bindToConfigData() {
        ConfigData.instance.CALCULATE_TTEST.bind(tTestCheckbox.selectedProperty().asString());
        ConfigData.instance.AT_N.bind(atNTextField.textProperty());
        ConfigData.instance.MINIMUM_THRESHOLD_FOR_POSITIVE_RATING
                .bind(minimumRatingForPositiveRatingTextField.textProperty());
        ConfigData.instance.TOP_N.bind(topNTextField.textProperty());
    }

    private void initMetricAttributes() {
        metricsList.setItems(FXCollections.observableArrayList(Metrics.values()));
        metricsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        metricsList.getSelectionModel().selectFirst();
        StringBinding selectedMetricsBinding = Bindings.createStringBinding(() -> {
            ObservableList<Metrics> selectedItems = metricsList.getSelectionModel().getSelectedItems();
            if (selectedItems.isEmpty()) {
                return "";
            }
            return String.join(",",
                    selectedItems.stream()
                            .map(a -> a.getText().get())
                            .collect(Collectors.toList()));

        }, metricsList.getSelectionModel().getSelectedItems());
        ConfigData.instance.METRICS.bind(selectedMetricsBinding);

    }

    private void initDropMostPopularItemAttributes() {
        dropMostPopularItemsTextField.disableProperty().bind(dropMostPopularItemsCheckBox.selectedProperty().not());

        ConfigData.instance.DROP_POPULAR_ITEM_NUMBER.bind(dropMostPopularItemsTextField.textProperty());
        ConfigData.instance.DROP_POPULAR_ITEM.bind(dropMostPopularItemsCheckBox.selectedProperty().asString());
    }

    private void initSimilarityFunctionAttribute() {
        similarityChoice.setItems(FXCollections.observableArrayList(SimilarityFunctions.values()));
        similarityChoice.getSelectionModel().selectFirst();

        ConfigData.instance.SIMILARITY_FUNCTION.bind(similarityChoice.valueProperty().asString());
    }

    private void initFoldNumberAttributes() {
        numberOfFoldsLabel.textProperty().bind(numberOfFoldsSlider.valueProperty().asString("%.0f"));

        ConfigData.instance.NUMBER_OF_FOLDS.bind(numberOfFoldsSlider.valueProperty().asString("%.0f"));
    }

    private void initFoldParallelAttributes() {
        numberOfThreadsForFoldsSlider.disableProperty().bind(runFoldsParallelCheckBox.selectedProperty().not());

        ConfigData.instance.RUN_FOLDS_PARALLEL.bind(runFoldsParallelCheckBox.selectedProperty().asString());
        ConfigData.instance.RUN_FOLDS_NUMBER_OF_THREAD.bind(
                numberOfThreadsForFoldsSlider.valueProperty().asString("%.0f"));

        numberOfThreadsForFoldsSlider.setMax(Runtime.getRuntime().availableProcessors());
    }

    private void initAlgorithmParallelAttributes() {
        numberOfThreadsForAlgorithmsSlider.disableProperty().bind(runAlgorithmParallelCheckBox.selectedProperty().not());

        ConfigData.instance.RUN_ALGORITHMS_PARALLEL.bind(runAlgorithmParallelCheckBox.selectedProperty().asString());
        ConfigData.instance.RUN_ALGORITHMS_NUMBER_OF_THREAD.bind(
                numberOfThreadsForAlgorithmsSlider.valueProperty().asString("%.0f"));
        numberOfThreadsForAlgorithmsSlider.setMax(Runtime.getRuntime().availableProcessors());
    }

    /**
     *
     */
    private void initRandimizationSeedAttributes() {
        randomizationSeedTextField.disableProperty().bind(randomizationSeedCheckBox.selectedProperty().not());
        ConfigData.instance.RANDOMIZATION_SEED.bind(randomizationSeedTextField.textProperty());
    }


    @Override
    public boolean isValid() {
        final StringBuilder overalErrorMessage = new StringBuilder();
        boolean overalError = false;
        try {
            Integer.parseInt(topNTextField.getText());
        } catch (final Exception exception) {
            overalErrorMessage.append("TopN is not valid").append("\n");
            overalError = true;
        }

        try {
            Double.parseDouble(minimumRatingForPositiveRatingTextField.getText());
        } catch (final Exception exception) {
            overalErrorMessage.append("Minimum value for positive ratings is not valid").append("\n");
            overalError = true;
        }

        try {
            Integer.parseInt(atNTextField.getText());
        } catch (final Exception exception) {
            overalErrorMessage.append("@N is not valid").append("\n");
            overalError = true;
        }

        if (dropMostPopularItemsCheckBox.isSelected()) {
            try {
                Integer.parseInt(dropMostPopularItemsTextField.getText());
            } catch (final Exception exception) {
                overalErrorMessage.append("Number of most popular items is not valid").append("\n");
                overalError = true;
            }
        }

        if (metricsList.getSelectionModel().getSelectedItems() == null || metricsList.getSelectionModel().getSelectedItems().isEmpty()) {
            overalErrorMessage.append("At lease 1 metric should be selected").append("\n");
            overalError = true;
        }
        if (randomizationSeedCheckBox.isSelected()) {
            try {
                Double.parseDouble(randomizationSeedTextField.getText());
            } catch (final Exception exception) {
                overalErrorMessage.append("Seed is not valid").append("\n");
                overalError = true;
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

    @Override
    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void fillWithSampleData() {
        topNTextField.setText("10");
        minimumRatingForPositiveRatingTextField.setText("3.5");
        atNTextField.setText("10");
        dropMostPopularItemsCheckBox.setSelected(true);
        dropMostPopularItemsTextField.setText("800");
        metricsList.getSelectionModel().select(Metrics.RMSE);
        metricsList.getSelectionModel().select(Metrics.NDCG);
    }


    @Override
    public void reset() {
        topNTextField.setText("");
        minimumRatingForPositiveRatingTextField.setText("");
        atNTextField.setText("");
        dropMostPopularItemsCheckBox.setSelected(false);
        dropMostPopularItemsTextField.setText("");
        metricsList.getSelectionModel().clearSelection();
        numberOfThreadsForAlgorithmsSlider.setValue(1);
        runAlgorithmParallelCheckBox.setSelected(false);
        numberOfThreadsForFoldsSlider.setValue(1);
        runFoldsParallelCheckBox.setSelected(false);
    }

    @Override
    public void fillWithPropertyFile(Properties properties) {
        topNTextField.setText(properties.getProperty("TOP_N"));
        minimumRatingForPositiveRatingTextField.setText(properties.getProperty("MINIMUM_THRESHOLD_FOR_POSITIVE_RATING"));
        atNTextField.setText(properties.getProperty("AT_N"));
        dropMostPopularItemsCheckBox.setSelected(Boolean.parseBoolean(properties.getProperty("DROP_POPULAR_ITEM")));
        dropMostPopularItemsTextField.setText(properties.getProperty("DROP_POPULAR_ITEM_NUMBER"));
        String[] metrics = properties.getProperty("METRICS").split(",");
        metricsList.getSelectionModel().clearSelection();
        for (String metric : metrics) {
            metricsList.getSelectionModel().select(Metrics.valueOf(metric));
        }
        numberOfThreadsForAlgorithmsSlider.setValue(Double.parseDouble(properties.getProperty("RUN_ALGORITHMS_NUMBER_OF_THREAD")));
        runAlgorithmParallelCheckBox.setSelected(Boolean.parseBoolean(properties.getProperty("RUN_ALGORITHMS_PARALLEL")));
        numberOfThreadsForFoldsSlider.setValue(Double.parseDouble(properties.getProperty("RUN_FOLDS_NUMBER_OF_THREAD")));
        runFoldsParallelCheckBox.setSelected(Boolean.parseBoolean(properties.getProperty("RUN_FOLDS_PARALLEL")));
    }
}
