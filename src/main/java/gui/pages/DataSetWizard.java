package gui.pages;

import java.io.File;

import gui.ConfigGeneratorGui;
import gui.WizardPage;
import gui.model.ConfigData;
import gui.model.ErrorMessage;
import gui.model.Separator;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;

/**
 * @author FBM
 *
 */
public class DataSetWizard extends WizardPage {
	private ErrorMessage errorMessage;

	private FileChooser lowLevelFileChooser;
	private Button lowLevelFileBtn;
	private TextField lowLevelFileText;
	private Label lowLevelFileLabel;
	private ComboBox<Separator> lowLevelFileSeparator;
	private Label lowLevelFileSeparatorLabel;

	private FileChooser genreFileChooser;
	private Button genreFileBtn;
	private TextField genreFileText;
	private Label genreFileLabel;
	private ComboBox<Separator> genreFileSeparator;
	private Label genreFileSeparatorLabel;

	private FileChooser tagFileChooser;
	private Button tagFileBtn;
	private TextField tagFileText;
	private Label tagFileLabel;
	private ComboBox<Separator> tagFileSeparator;
	private Label tagFileSeparatorLabel;

	private FileChooser ratingFileChooser;
	private Button ratingFileBtn;
	private TextField ratingFileText;
	private Label ratingFileLabel;
	private ComboBox<Separator> ratingFileSeparator;
	private Label ratingFileSeparatorLabel;

	private VBox description;
	private static final int TEXT_FIELD_WIDTH = 200;
	
	private Label numberOfFolds;
	private Slider slider;
	private Label numberOfFoldsValue;

	private CheckBox randomizationSeedCheckBox;
	private TextField randomizationSeedTextField;

	private CheckBox runAlgorithmParallelCheckBox;
	private TextField runAlgorithmNumberOfThreadTextField;

	private CheckBox runFoldsParallelCheckBox;
	private TextField runFoldsNumberOfThreadTextField;
	

	/**
	 * @param title
	 */
	public DataSetWizard() {
		super("Dataset/Cross Validation Setting");
	}

	@Override
	protected void fillWithSampleData(){
		final String home = System.getProperty("user.dir");
		lowLevelFileText.setText(home+File.separator+"data"+File.separator+"LLVisualFeatures13K_QuantileLog.csv");
		genreFileText.setText(home+File.separator+"data"+File.separator+"Genre.csv");
		tagFileText.setText(home+File.separator+"data"+File.separator+"Tag.csv");
		ratingFileText.setText(home+File.separator+"data"+File.separator+"5%RatingsSampledByRating.csv");
	}

	/**
	 * 
	 */
	private void initErrorLabel() {
		errorMessage = new ErrorMessage();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.WizardPage#getContent()
	 */
	@Override
	public Parent getContent() {
		handleLowLevelAttributes();
		handleGenreAttributes();
		handleTagAttributes();
		handleRatingAttributes();
		initErrorLabel();
		initDescription();
		initFoldNumberAttributes();
		initRandimizationSeedAttributes();
		initAlgorithmParallelAttributes();
		initFoldParallelAttributes();
		return initLayout();
	}

	/**
	 * 
	 */
	private void initFoldNumberAttributes() {
		numberOfFolds = new Label("Number of folds");
		numberOfFoldsValue = new Label();
		numberOfFoldsValue.setFont(new Font("Arial", 30));

		slider = new Slider();
		slider.setMin(2);
		slider.setMax(50);
		slider.setValue(5);
		slider.setShowTickLabels(true);
		slider.setMajorTickUnit(5);
		slider.setBlockIncrement(1);
		slider.setPrefWidth(300);
		numberOfFoldsValue.setText(String.valueOf((int) slider.getValue()));
		ConfigData.instance.NUMBER_OF_FOLDS.bind(new SimpleStringProperty(String.valueOf((int) slider.getValue())));
		slider.valueProperty().addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> {
			numberOfFoldsValue.setText(String.valueOf((int) slider.getValue()));
			ConfigData.instance.NUMBER_OF_FOLDS.bind(new SimpleStringProperty(String.valueOf((int) slider.getValue())));
		});
	}
	
	/**
	 * 
	 */
	private void initFoldParallelAttributes() {
		runFoldsParallelCheckBox = new CheckBox("Run Folds Parallel?");
		runFoldsNumberOfThreadTextField = new TextField();
		runFoldsNumberOfThreadTextField.setDisable(true);
		final Tooltip tooltip = new Tooltip();
		tooltip.setText(
		    "If you leave this element empty,\nmaximum number of cores will be used"
		);
		runFoldsNumberOfThreadTextField.setTooltip(tooltip);
		runFoldsNumberOfThreadTextField.setPromptText("If you leave this element empty,\nmaximum number of cores will be used");
		
		ConfigData.instance.RUN_FOLDS_PARALLEL.bind(new SimpleStringProperty(String.valueOf(false)));
		runFoldsParallelCheckBox.selectedProperty()
				.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
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
	
	/**
	 * 
	 */
	private void initAlgorithmParallelAttributes() {
		runAlgorithmParallelCheckBox = new CheckBox("Run Algorithms Parallel?");
		runAlgorithmNumberOfThreadTextField = new TextField();
		runAlgorithmNumberOfThreadTextField.setDisable(true);
		final Tooltip tooltip = new Tooltip();
		tooltip.setText(
		    "If you leave this element empty,\nmaximum number of cores will be used"
		);
		runAlgorithmNumberOfThreadTextField.setTooltip(tooltip);
		runAlgorithmNumberOfThreadTextField.setPromptText("If you leave this element empty,\nmaximum number of cores will be used");
		ConfigData.instance.RUN_ALGORITHMS_PARALLEL.bind(new SimpleStringProperty(String.valueOf(false)));
		runAlgorithmParallelCheckBox.selectedProperty()
				.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
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
		randomizationSeedCheckBox = new CheckBox("Set Randomization Seed?");
		randomizationSeedTextField = new TextField();
		randomizationSeedTextField.setDisable(true);

		randomizationSeedCheckBox.selectedProperty()
				.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
					randomizationSeedTextField.setDisable(!newValue);
					if (!newValue) {
						randomizationSeedTextField.setText("");
						errorMessage.setText("");
					}
					final StringProperty textProperty = randomizationSeedTextField.textProperty();
					ConfigData.instance.RANDOMIZATION_SEED.bind(textProperty);
				});
	}
	/**
	 * 
	 */
	private void initDescription() {
		description = new VBox();
		final String content = createContent();
		final Text text = new Text(content);
		text.setWrappingWidth(ConfigGeneratorGui.WIDTH-60);
		text.setTextAlignment(TextAlignment.JUSTIFY);
		text.setFill(Color.BLUE);
		description.getChildren().add(text);
		description.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
				+ "-fx-border-insets: 5;" + "-fx-border-radius: 5;" + "-fx-border-color: gray");
	}

	/**
	 * @return
	 */
	private String createContent() {
		final StringBuilder content = new StringBuilder();
		content.append("1) If the files have some meta data, '#' must be added at the beginning before running the application. All the lines with '#' will be ignored.").append("\n");
		content.append("2) Rating file format:").append("\n");
		content.append("\t User Id,Item Id,Rating").append("\n");
		content.append("3) Low Level feature file format:").append("\n");
		content.append("\t Item id,feature 1,feature 2,feature 3, ...").append("\n");
		content.append("4) Genre file format:").append("\n");
		content.append("\t Genre file should be preprocessed and converted to the binary representatiopn:").append("\n");
		content.append("\t Item id,0,0,1,0,1,1,0,...").append("\n");
		content.append("5) Tag file format:").append("\n");
		content.append("\t Item id,Tag 1,Tag 2, ....").append("\n");
		return content.toString();
	}

	private Parent initLayout() {

		final GridPane gridpane = new GridPane();
		gridpane.setAlignment(Pos.CENTER);
		gridpane.add(lowLevelFileLabel, 0, 0);
		gridpane.add(lowLevelFileBtn, 1, 0);
		gridpane.add(lowLevelFileText, 2, 0);
		gridpane.add(lowLevelFileSeparatorLabel, 3, 0);
		gridpane.add(lowLevelFileSeparator, 4, 0);

		gridpane.add(genreFileLabel, 0, 1);
		gridpane.add(genreFileBtn, 1, 1);
		gridpane.add(genreFileText, 2, 1);
		gridpane.add(genreFileSeparatorLabel, 3, 1);
		gridpane.add(genreFileSeparator, 4, 1);

		gridpane.add(tagFileLabel, 0, 2);
		gridpane.add(tagFileBtn, 1, 2);
		gridpane.add(tagFileText, 2, 2);
		gridpane.add(tagFileSeparatorLabel, 3, 2);
		gridpane.add(tagFileSeparator, 4, 2);

		gridpane.add(ratingFileLabel, 0, 3);
		gridpane.add(ratingFileBtn, 1, 3);
		gridpane.add(ratingFileText, 2, 3);
		gridpane.add(ratingFileSeparatorLabel, 3, 3);
		gridpane.add(ratingFileSeparator, 4, 3);

		gridpane.setHgap(10);
		gridpane.setVgap(10);
		gridpane.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
				+ "-fx-border-insets: 5;" + "-fx-border-radius: 5;" + "-fx-border-color: gray");
		
		final GridPane crossValidationGridpane = new GridPane();
		crossValidationGridpane.setAlignment(Pos.CENTER);
		crossValidationGridpane.setHgap(20);
		crossValidationGridpane.setVgap(10);

		crossValidationGridpane.add(numberOfFolds, 0, 0);
		crossValidationGridpane.add(slider, 1, 0);

		crossValidationGridpane.add(randomizationSeedCheckBox, 0, 1);
		crossValidationGridpane.add(randomizationSeedTextField, 1, 1);
		crossValidationGridpane.add(runAlgorithmParallelCheckBox, 0, 2);
		crossValidationGridpane.add(runAlgorithmNumberOfThreadTextField, 1, 2);
		crossValidationGridpane.add(runFoldsParallelCheckBox, 0, 3);
		crossValidationGridpane.add(runFoldsNumberOfThreadTextField, 1, 3);
				
		final HBox crossValidationHBox = new HBox(20.0,crossValidationGridpane,numberOfFoldsValue);
		crossValidationHBox.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
				+ "-fx-border-insets: 5;" + "-fx-border-radius: 5;" + "-fx-border-color: gray");
		
		final VBox mainLayout = new VBox(5.0, gridpane, crossValidationHBox,description);		
		return mainLayout;
	}

	/**
	 * 
	 */
	private void handleRatingAttributes() {
		ratingFileChooser = new FileChooser();
		ratingFileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.*"),
				new FileChooser.ExtensionFilter("CSV", "*.csv"), new FileChooser.ExtensionFilter("TXT", "*.txt"));

		ratingFileBtn = new Button("Browse");
		ratingFileBtn.setOnAction(event -> {
			final File file = ratingFileChooser.showOpenDialog(ConfigGeneratorGui.getCurrentStage());
			if (file != null) {
				ratingFileText.setText(file.getAbsolutePath());
				ratingFileChooser.setInitialDirectory(file.getParentFile());
				genreFileChooser.setInitialDirectory(file.getParentFile());
				lowLevelFileChooser.setInitialDirectory(file.getParentFile());
				tagFileChooser.setInitialDirectory(file.getParentFile());
			}
		});

		ratingFileText = new TextField();
		ratingFileText.setPrefWidth(TEXT_FIELD_WIDTH);
		ratingFileText.setEditable(false);

		ratingFileLabel = new Label("Rating file");

		ratingFileSeparator = new ComboBox<>(FXCollections.observableArrayList(Separator.values()));
		ratingFileSeparator.getSelectionModel().selectFirst();
		ConfigData.instance.RATING_FILE_SEPARATOR.bind(ratingFileSeparator.getValue().getText());
		ratingFileSeparator.setOnAction(event -> {
			ConfigData.instance.RATING_FILE_SEPARATOR.bind(ratingFileSeparator.getValue().getText());
		});
		ratingFileSeparatorLabel = new Label("Rating file separator");

		ConfigData.instance.RATING_FILE_PATH.bind(ratingFileText.textProperty());
	}

	/**
	 * 
	 */
	private void handleTagAttributes() {
		tagFileChooser = new FileChooser();
		tagFileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.*"),
				new FileChooser.ExtensionFilter("CSV", "*.csv"), new FileChooser.ExtensionFilter("TXT", "*.txt"));

		tagFileBtn = new Button("Browse");
		tagFileBtn.setOnAction(event -> {
			final File file = tagFileChooser.showOpenDialog(ConfigGeneratorGui.getCurrentStage());
			if (file != null) {
				tagFileText.setText(file.getAbsolutePath());
				ratingFileChooser.setInitialDirectory(file.getParentFile());
				genreFileChooser.setInitialDirectory(file.getParentFile());
				lowLevelFileChooser.setInitialDirectory(file.getParentFile());
				tagFileChooser.setInitialDirectory(file.getParentFile());
			}
		});

		tagFileText = new TextField();
		tagFileText.setPrefWidth(TEXT_FIELD_WIDTH);
		tagFileText.setEditable(false);

		tagFileLabel = new Label("Tag file");

		tagFileSeparator = new ComboBox<>(FXCollections.observableArrayList(Separator.values()));
		tagFileSeparator.getSelectionModel().selectFirst();
		ConfigData.instance.TAG_FILE_SEPARATOR.bind(tagFileSeparator.getValue().getText());
		tagFileSeparator.setOnAction(event -> {
			ConfigData.instance.TAG_FILE_SEPARATOR.bind(tagFileSeparator.getValue().getText());
		});
		tagFileSeparatorLabel = new Label("Tag file separator");

		ConfigData.instance.TAG_FILE_PATH.bind(tagFileText.textProperty());
	}

	/**
	 * 
	 */
	private void handleGenreAttributes() {
		genreFileChooser = new FileChooser();
		genreFileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.*"),
				new FileChooser.ExtensionFilter("CSV", "*.csv"), new FileChooser.ExtensionFilter("TXT", "*.txt"));

		genreFileBtn = new Button("Browse");
		genreFileBtn.setOnAction(event -> {
			final File file = genreFileChooser.showOpenDialog(ConfigGeneratorGui.getCurrentStage());
			if (file != null) {
				genreFileText.setText(file.getAbsolutePath());
				ratingFileChooser.setInitialDirectory(file.getParentFile());
				genreFileChooser.setInitialDirectory(file.getParentFile());
				lowLevelFileChooser.setInitialDirectory(file.getParentFile());
				tagFileChooser.setInitialDirectory(file.getParentFile());
			}
		});

		genreFileText = new TextField();
		genreFileText.setPrefWidth(TEXT_FIELD_WIDTH);
		genreFileText.setEditable(false);

		genreFileLabel = new Label("Genre file");

		genreFileSeparator = new ComboBox<>(FXCollections.observableArrayList(Separator.values()));
		genreFileSeparator.getSelectionModel().selectFirst();
		ConfigData.instance.GENRE_FILE_SEPARATOR.bind(genreFileSeparator.getValue().getText());
		genreFileSeparator.setOnAction(event -> {
			ConfigData.instance.GENRE_FILE_SEPARATOR.bind(genreFileSeparator.getValue().getText());
		});
		genreFileSeparatorLabel = new Label("Genre file separator");

		ConfigData.instance.GENRE_FILE_PATH.bind(genreFileText.textProperty());
	}

	/**
	 * 
	 */
	private void handleLowLevelAttributes() {
		lowLevelFileChooser = new FileChooser();
		lowLevelFileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.*"),
				new FileChooser.ExtensionFilter("CSV", "*.csv"), new FileChooser.ExtensionFilter("TXT", "*.txt"));

		lowLevelFileBtn = new Button("Browse");
		lowLevelFileBtn.setOnAction(event -> {
			final File file = lowLevelFileChooser.showOpenDialog(ConfigGeneratorGui.getCurrentStage());
			if (file != null) {
				lowLevelFileText.setText(file.getAbsolutePath());
				ratingFileChooser.setInitialDirectory(file.getParentFile());
				genreFileChooser.setInitialDirectory(file.getParentFile());
				lowLevelFileChooser.setInitialDirectory(file.getParentFile());
				tagFileChooser.setInitialDirectory(file.getParentFile());
			}
		});

		lowLevelFileText = new TextField();
		lowLevelFileText.setPrefWidth(TEXT_FIELD_WIDTH);
		lowLevelFileText.setEditable(false);

		lowLevelFileLabel = new Label("LowLevel feature file");

		lowLevelFileSeparator = new ComboBox<>(FXCollections.observableArrayList(Separator.values()));
		lowLevelFileSeparator.getSelectionModel().selectFirst();
		ConfigData.instance.LOW_LEVEL_FILE_SEPARATOR.bind(lowLevelFileSeparator.getValue().getText());
		lowLevelFileSeparator.setOnAction(event -> {
			ConfigData.instance.LOW_LEVEL_FILE_SEPARATOR.bind(lowLevelFileSeparator.getValue().getText());
		});
		lowLevelFileSeparatorLabel = new Label("LowLevel feature file separator");

		ConfigData.instance.LOW_LEVEL_FILE_PATH.bind(lowLevelFileText.textProperty());
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
		if (ratingFileText.getText() == null || ratingFileText.getText().isEmpty()) {
			overalErrorMessage.append("The rating file should be selected").append("\n");
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
		if (runAlgorithmParallelCheckBox.isSelected()) {
			if(!runAlgorithmNumberOfThreadTextField.getText().isEmpty()){
				try {
					Integer.parseInt(runAlgorithmNumberOfThreadTextField.getText());
				} catch (final Exception exception) {
					overalErrorMessage.append("Number of cores for running algorithms in parallel mode is not valid").append("\n");
					overalError = true;
				}
			}
		}
		if (runFoldsParallelCheckBox.isSelected()) {
			if(!runFoldsNumberOfThreadTextField.getText().isEmpty()){
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

	/* (non-Javadoc)
	 * @see gui.WizardPage#getErrorMessage()
	 */
	@Override
	protected String getErrorMessage() {
		return errorMessage.getText();
	}

	@Override
	protected void reset() {
		lowLevelFileText.setText("");
		genreFileText.setText("");
		tagFileText.setText("");
		ratingFileText.setText("");
		runAlgorithmNumberOfThreadTextField.setText("");
		runAlgorithmParallelCheckBox.setSelected(false);
		runFoldsNumberOfThreadTextField.setText("");
		runFoldsParallelCheckBox.setSelected(false);
	}
}
