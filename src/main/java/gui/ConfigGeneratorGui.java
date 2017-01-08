package gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import run.HiRec;

/**
 * @author FBM
 *
 */
public class ConfigGeneratorGui extends Application {

	private static final String APP_VERSION = "v2.1.1";
	private static final String LEATEST_RELEASE_URL = "https://github.com/fmoghaddam/Hi-Rec/releases/latest";
	private static final String LATEST_RELEASE_JSON_URL = "https://api.github.com/repos/fmoghaddam/Hi-Rec/releases/latest";

	private static final Logger LOG = Logger.getLogger(ConfigGeneratorGui.class.getCanonicalName());

	public static final int HEIGHT = 800;
	public static final int WIDTH = 670;

	private static Stage currentStage;
	private MenuBar menu;
	private WizardMaker wizardMaker;
	
	public static void main(String[] args) throws Exception {
		launch(args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage stage) throws Exception {
		showSplash(stage);
		this.menu = createMenu();
		showMainApplication(stage);
	}

	/**
	 * @param stage
	 * @return
	 */
	private MenuBar createMenu() {
		final MenuBar menuBar = new MenuBar();
		final Menu menuFile = new Menu("File");
		final Menu menuHelp = new Menu("Help");

		final MenuItem reset = new MenuItem("Reset");
		reset.setOnAction(e -> {
			wizardMaker.reset();
		});
		
		final MenuItem exit = new MenuItem("Exit");
		exit.setOnAction(e -> {
			Platform.exit();
		});
		final MenuItem fillWithSampleDate = new MenuItem("Fill With Sample Data");
		fillWithSampleDate.setOnAction(e->{
			wizardMaker.fillWithSampleData();
		});

		menuFile.getItems().add(fillWithSampleDate);
		menuFile.getItems().add(reset);
		exit.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.ALT_DOWN));
		menuFile.getItems().add(exit);

		final MenuItem aboutUs = new MenuItem("About Us");
		aboutUs.setOnAction(t -> {
			createAboutUs();
		});

		menuHelp.getItems().addAll(aboutUs);

		menuBar.getMenus().addAll(menuFile, menuHelp);
		return menuBar;
	}

	private void createAboutUs() {
		final Stage newStage = new Stage();
		final VBox vBox = createAboutUsContent();

		final Scene scene = new Scene(vBox);
		newStage.setScene(scene);
		newStage.setResizable(false);
		newStage.setTitle("About Us");
		vBox.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
				+ "-fx-border-insets: 5;" + "-fx-border-radius: 5;" + "-fx-border-color: gray;"
				+ "-fx-background: rgb(255, 248, 220);");
		newStage.centerOnScreen();
		newStage.initModality(Modality.APPLICATION_MODAL);
		newStage.showAndWait();
	}

	private VBox createAboutUsContent() {
		final StringBuilder content = new StringBuilder();
		content.append("For more infomration about this software you can visit following GitHub page:").append("\n");
		final Hyperlink link = new Hyperlink();
		link.setVisited(false);
		final String linkAddress = "https://fmoghaddam.github.io/Hi-Rec/";
		link.setText(linkAddress);
		link.setStyle(" -fx-border-color: transparent;-fx-padding: 4 0 4 0;");
		link.setOnAction(e -> {
			getHostServices().showDocument(linkAddress);
		});
		final VBox vBox = new VBox(10.0, new Text(content.toString()), link);
		return vBox;
	}

	private void showMainApplication(Stage stage) {
		Platform.runLater(() -> {
			showVersionControl();
			try {
				Thread.sleep(2000);
				stage.close();
			} catch (final Exception exception) {
				// Not important
			}

			showConfirmation();
			final Stage newStage = new Stage();
			currentStage = newStage;
			final BorderPane borderPane = new BorderPane();
			final ScrollPane scrollPane = new ScrollPane(borderPane);
			scrollPane.setFitToWidth(true);
			scrollPane.setFitToHeight(true);
			borderPane.setTop(menu);
			wizardMaker = new WizardMaker(stage);
			borderPane.setCenter(wizardMaker);
			final Scene scence = new Scene(scrollPane, WIDTH, HEIGHT);
			newStage.setScene(scence);
			newStage.setResizable(true);
			newStage.setTitle("Hi-Rec Client");
			newStage.centerOnScreen();
			newStage.show();
		});
	}

	private void showVersionControl() {
		final String version = checkVersion();
		if (version!=null && !APP_VERSION.equals(version)) {
			showNewVersionIsAvailable(version);
		}
	}

	private void showNewVersionIsAvailable(final String version) {
		final Stage newStageConfirmation = new Stage();
		final StringBuilder content = new StringBuilder();
		content.append("A new version " + version + " is available.").append("\n");
		content.append("You can download it from :").append("\n");
		final Hyperlink link = new Hyperlink(version);
		link.setVisited(false);
		link.setStyle(" -fx-border-color: transparent;-fx-padding: 4 0 4 0;");
		link.setOnAction(e -> {
			getHostServices().showDocument(LEATEST_RELEASE_URL);
		});
		final Button confiramtionBtn = new Button("OK");
		final VBox vBox = new VBox(5.0, new Text(content.toString()), link, confiramtionBtn);
		confiramtionBtn.setOnAction(event -> {
			newStageConfirmation.close();
		});

		final Scene scene = new Scene(vBox);
		newStageConfirmation.setScene(scene);
		newStageConfirmation.setResizable(false);
		newStageConfirmation.initStyle(StageStyle.UNDECORATED);
		vBox.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
				+ "-fx-border-insets: 5;" + "-fx-border-radius: 5;" + "-fx-border-color: gray;"
				+ "-fx-background: rgb(255, 248, 220);");
		newStageConfirmation.centerOnScreen();
		newStageConfirmation.initModality(Modality.APPLICATION_MODAL);
		newStageConfirmation.showAndWait();
	}

	private String checkVersion() {
		try {
			final URL url = new URL(LATEST_RELEASE_JSON_URL);
			final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			final BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			final String jsonText = readAll(br);
			final JSONObject json = new JSONObject(jsonText);
			final String versionName = (String) json.get("tag_name");
			conn.disconnect();
			return versionName;
		} catch (final IOException exception) {
			LOG.error(exception);
			return null;
		}
	}

	private String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	private void showConfirmation() {
		final Stage newStageConfirmation = new Stage();
		final VBox vBox = createConfirmationContent(newStageConfirmation);

		final Scene scene = new Scene(vBox);
		newStageConfirmation.setScene(scene);
		newStageConfirmation.setResizable(false);
		newStageConfirmation.initStyle(StageStyle.UNDECORATED);
		vBox.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
				+ "-fx-border-insets: 5;" + "-fx-border-radius: 5;" + "-fx-border-color: gray;"
				+ "-fx-background: rgb(255, 248, 220);");
		newStageConfirmation.centerOnScreen();
		newStageConfirmation.initModality(Modality.APPLICATION_MODAL);
		newStageConfirmation.showAndWait();
	}

	/**
	 * @return
	 */
	private VBox createConfirmationContent(Stage stage) {
		final StringBuilder content = new StringBuilder();
		content.append("This framework developed to be used across with Mise-en-scène Project.").append("\n");
		content.append(
				"To acknowledge the use of this recommendation engine in your work, please cite the following paper:")
				.append("\n");
		final Hyperlink link = new Hyperlink(
				"How to Combine Visual Features with Tags to Improve the Movie  Recommendation  Accuracy");
		link.setVisited(false);
		final String linkAddress = "https://www.researchgate.net/publication/308780370_How_to_Combine_Visual_Features_with_Tags_to_Improve_Movie_Recommendation_Accuracy";
		link.setStyle(" -fx-border-color: transparent;-fx-padding: 4 0 4 0;");
		link.setOnAction(e -> {
			getHostServices().showDocument(linkAddress);
		});
		final CheckBox confiramtionCheckBox = new CheckBox("I accept ");
		final VBox vBox = new VBox(5.0, new Text(content.toString()), link, confiramtionCheckBox);
		confiramtionCheckBox.setOnAction(event -> {
			if (confiramtionCheckBox.isSelected()) {
				stage.close();
			}
		});
		return vBox;
	}

	/**
	 * @param stage
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 */
	private void showSplash(Stage initStage) throws FileNotFoundException, InterruptedException {
		initStage.centerOnScreen();
		final URL resource = HiRec.class.getResource("/images/logo.png");
		final File imageFile = new File(resource.getFile());
		final Image image = new Image(imageFile.toURI().toString());
		final ImageView splash = new ImageView(image);
		final Pane splashLayout = new VBox();
		splashLayout.getChildren().addAll(splash);
		splashLayout.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
		splashLayout.setEffect(new DropShadow());

		final Scene splashScene = new Scene(splashLayout);
		initStage.initStyle(StageStyle.UNDECORATED);
		initStage.setScene(splashScene);
		initStage.show();
	}

	/**
	 * @return the currentStage
	 */
	public static final Stage getCurrentStage() {
		return currentStage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#stop()
	 */
	@Override
	public void stop() throws Exception {
		Platform.exit();
		System.exit(0);
	}
}
