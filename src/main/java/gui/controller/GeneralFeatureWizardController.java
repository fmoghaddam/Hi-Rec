package gui.controller;

import gui.WizardControllerInterface;
import gui.model.ConfigData;
import gui.model.ErrorMessage;
import gui.model.Metrics;
import gui.model.SimilarityFunctions;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

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

    private ErrorMessage errorMessage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initMetricAttributes();
        initDropMostPopularItemAttributes();
        initSimilarityFunctionAttribute();
        bindToConfigData();
    }

    private void bindToConfigData() {
        tTestCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            ConfigData.instance.CALCULATE_TTEST.bind(new SimpleStringProperty(String.valueOf(newValue)));
        });
        ConfigData.instance.CALCULATE_TTEST.bind(new SimpleStringProperty(String.valueOf(false)));

        metricsList.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Metrics>) c -> {
            final ObservableList<Metrics> selectedItems = metricsList.getSelectionModel().getSelectedItems();
            if (selectedItems.size() < 1) {
                return;
            }
            StringBuilder result = new StringBuilder();
            for (final Metrics metrics : selectedItems) {
                result.append(metrics.getText().get()).append(",");
            }
            ConfigData.instance.METRICS.bind(new SimpleStringProperty(result.substring(0, result.length() - 1)));
        });

        dropMostPopularItemsCheckBox.selectedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (!newValue) {
                        dropMostPopularItemsTextField.setDisable(true);
                        dropMostPopularItemsTextField.setText("");
                    } else {
                        dropMostPopularItemsTextField.setDisable(false);
                    }
                    ConfigData.instance.DROP_POPULAR_ITEM_NUMBER.bind(dropMostPopularItemsTextField.textProperty());
                    ConfigData.instance.DROP_POPULAR_ITEM.bind(new SimpleStringProperty(String.valueOf(newValue)));
                });


        ConfigData.instance.DROP_POPULAR_ITEM.bind(new SimpleStringProperty(String.valueOf(false)));
        ConfigData.instance.AT_N.bind(atNTextField.textProperty());
        ConfigData.instance.MINIMUM_THRESHOLD_FOR_POSITIVE_RATING
                .bind(minimumRatingForPositiveRatingTextField.textProperty());
        ConfigData.instance.TOP_N.bind(topNTextField.textProperty());

        similarityChoice.setOnAction(event -> {
            ConfigData.instance.SIMILARITY_FUNCTION.bind(similarityChoice.getValue().getText());
        });
        ConfigData.instance.SIMILARITY_FUNCTION.bind(similarityChoice.getValue().getText());


    }

    private void initMetricAttributes() {
        metricsList.setItems(FXCollections.observableArrayList(Metrics.values()));
        metricsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }

    private void initDropMostPopularItemAttributes() {

        dropMostPopularItemsTextField.setDisable(true);
        dropMostPopularItemsCheckBox.selectedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (!newValue) {
                        dropMostPopularItemsTextField.setDisable(true);
                        dropMostPopularItemsTextField.setText("");
                    } else {
                        dropMostPopularItemsTextField.setDisable(false);
                    }
                });
    }

    private void initSimilarityFunctionAttribute() {
        similarityChoice.setItems(FXCollections.observableArrayList(SimilarityFunctions.values()));
        similarityChoice.getSelectionModel().selectFirst();
    }


    /*
     * (non-Javadoc)
     *
     * @see gui.WizardPage#validate()
     */
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
        if (!overalError) {
            errorMessage.setText("");
            return true;
        } else {
            errorMessage.setText(overalErrorMessage.toString());
            return false;
        }

    }

    /* (non-Javadoc)
     * @see gui.WizardPage#getErrorMessage()
     */
    @Override
    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    /* (non-Javadoc)
     * @see gui.WizardPage#fillWithSampleData()
     */
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
    }

}
