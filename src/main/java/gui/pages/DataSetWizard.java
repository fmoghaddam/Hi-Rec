package gui.pages;

import java.io.File;

import gui.ConfigGeneratorGui;
import gui.WizardPage;
import gui.model.ConfigData;
import gui.model.ErrorMessage;
import gui.model.Separator;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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

	/**
	 * @param title
	 */
	public DataSetWizard() {
		super("Dataset wizard");
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
		return initLayout();
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
		
		final VBox mainLayout = new VBox(5.0, gridpane,description);		
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
		if (ratingFileText.getText() == null || ratingFileText.getText().isEmpty()) {
			errorMessage.setText("The rating file should be selected");
			return false;
		} else {
			errorMessage.setText("");
			return true;
		}
	}

	/* (non-Javadoc)
	 * @see gui.WizardPage#getErrorMessage()
	 */
	@Override
	protected String getErrorMessage() {
		return errorMessage.getText();
	}
}
