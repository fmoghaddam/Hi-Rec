package gui.pages;

import gui.WizardPage;
import gui.model.ConfigData;
import gui.model.ErrorMessage;
import gui.model.Metrics;
import gui.model.SimilarityFunctions;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * @author FBM
 *
 */
public class GeneralFeatureWizard extends WizardPage {

	private ComboBox<SimilarityFunctions> similairtyFunction;
	private Label similairtyFunctionLable;

	private TextField topN;
	private Label topNLabel;

	private TextField minimumRatingForPositiveRatingTextField;
	private Label minimumRatingForPositiveRatingLabel;

	private TextField atN;
	private Label atNLabel;

	private ListView<Metrics> metrics;
	private Label metricsLabel;

	private CheckBox calculateTTest;

	private CheckBox dropMostPopularItemsCheckBox;
	private TextField dropMostPopularItemsTextField;

	private ErrorMessage errorMessage;

	/**
	 * @param title
	 */
	public GeneralFeatureWizard() {
		super("General Parameters");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.WizardPage#getContent()
	 */
	@Override
	public Parent getContent() {
		initSimilarityFunctionAttribute();
		initTopNAttribute();
		initMinimumThresholdRatingAttribute();
		initAtNAttributes();
		initDropMostPopularItemAttributes();
		initTTestAttributes();
		initMetricAttributes();
		initErrorLabel();
		return initLayout();
	}

	/**
	 * 
	 */
	private void initErrorLabel() {
		errorMessage = new ErrorMessage();
	}

	private void initMetricAttributes() {
		metricsLabel = new Label("Metrics");
		metrics = new ListView<>(FXCollections.observableArrayList(Metrics.values()));
		metrics.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		metrics.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Metrics>) c -> {
			final ObservableList<Metrics> selectedItems = metrics.getSelectionModel().getSelectedItems();
			StringBuilder result = new StringBuilder();
			for (final Metrics metrics : selectedItems) {
				result.append(metrics.getText().get()).append(",");
			}
			ConfigData.instance.METRICS.bind(new SimpleStringProperty(result.substring(0, result.length() - 1)));
		});
	}

	private void initTTestAttributes() {
		calculateTTest = new CheckBox("Calculate T-Test");
		calculateTTest.setSelected(false);
		calculateTTest.selectedProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
			ConfigData.instance.CALCULATE_TTEST.bind(new SimpleStringProperty(String.valueOf(newValue)));
		});
		ConfigData.instance.CALCULATE_TTEST.bind(new SimpleStringProperty(String.valueOf(false)));
	}

	private void initDropMostPopularItemAttributes() {
		dropMostPopularItemsCheckBox = new CheckBox("Drop most popular items");
		dropMostPopularItemsCheckBox.setSelected(false);
		ConfigData.instance.DROP_POPULAR_ITEM.bind(new SimpleStringProperty(String.valueOf(false)));

		dropMostPopularItemsTextField = new TextField();
		dropMostPopularItemsTextField.setDisable(true);
		dropMostPopularItemsCheckBox.selectedProperty()
				.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
					if (!newValue) {
						dropMostPopularItemsTextField.setDisable(true);
						dropMostPopularItemsTextField.setText("");
					} else {
						dropMostPopularItemsTextField.setDisable(false);
					}
					ConfigData.instance.DROP_POPULAR_ITEM_NUMBER.bind(dropMostPopularItemsTextField.textProperty());
					ConfigData.instance.DROP_POPULAR_ITEM.bind(new SimpleStringProperty(String.valueOf(newValue)));
				});
	}

	private void initAtNAttributes() {
		atN = new TextField();
		atNLabel = new Label("@N");
		ConfigData.instance.AT_N.bind(atN.textProperty());
	}

	private void initMinimumThresholdRatingAttribute() {
		minimumRatingForPositiveRatingLabel = new Label("Minimum value for positive ratings");
		minimumRatingForPositiveRatingTextField = new TextField();
		ConfigData.instance.MINIMUM_THRESHOLD_FOR_POSITIVE_RATING
				.bind(minimumRatingForPositiveRatingTextField.textProperty());
	}

	private void initTopNAttribute() {
		topN = new TextField();
		topNLabel = new Label("TopN");
		ConfigData.instance.TOP_N.bind(topN.textProperty());
	}

	private void initSimilarityFunctionAttribute() {
		similairtyFunction = new ComboBox<>(FXCollections.observableArrayList(SimilarityFunctions.values()));
		similairtyFunction.getSelectionModel().selectFirst();
		similairtyFunctionLable = new Label("Similarity Function");
		similairtyFunction.setOnAction(event -> {
			ConfigData.instance.SIMILARITY_FUNCTION.bind(similairtyFunction.getValue().getText());
		});
		ConfigData.instance.SIMILARITY_FUNCTION.bind(similairtyFunction.getValue().getText());
	}

	/**
	 * @return
	 */
	private Parent initLayout() {
		final GridPane gridpane = new GridPane();
		gridpane.setHgap(10);
		gridpane.setVgap(10);
		gridpane.setAlignment(Pos.CENTER);

		gridpane.add(similairtyFunctionLable, 0, 0);
		gridpane.add(similairtyFunction, 1, 0);

		gridpane.add(topNLabel, 0, 1);
		gridpane.add(topN, 1, 1);

		gridpane.add(minimumRatingForPositiveRatingLabel, 0, 2);
		gridpane.add(minimumRatingForPositiveRatingTextField, 1, 2);

		gridpane.add(atNLabel, 0, 3);
		gridpane.add(atN, 1, 3);

		gridpane.add(dropMostPopularItemsCheckBox, 0, 4);
		gridpane.add(dropMostPopularItemsTextField, 1, 4);

		gridpane.add(calculateTTest, 0, 5);

		gridpane.add(metricsLabel, 0, 6);
		gridpane.add(metrics, 1, 6);

		final VBox mainLayout = new VBox(5.0, gridpane, errorMessage);
		return mainLayout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.WizardPage#validate()
	 */
	@Override
	public boolean validate() {
		final StringBuilder overalErrorMessage = new StringBuilder();
		boolean overalError = false;
		try {
			Integer.parseInt(topN.getText());
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
			Integer.parseInt(atN.getText());
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

		if(metrics.getSelectionModel().getSelectedItems()==null || metrics.getSelectionModel().getSelectedItems().isEmpty()){
			overalErrorMessage.append("At lease 1 metric shoudl be selected").append("\n");
			overalError=true;
		}
		if (!overalError) {
			errorMessage.setText("");
			return true;
		} else {
			errorMessage.setText(overalErrorMessage.toString());
			return false;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.WizardPage#reloadIfNeeded()
	 */
	@Override
	public void reloadIfNeeded() {
		// TODO Auto-generated method stub

	}

}
