package gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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

	public static final int HEIGHT = 800;
	public static final int WIDTH = 670;

	private static Stage currentStage;
	private MenuBar menu;

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
		this.menu=createMenu();		
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
        
        final MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(t -> {
        	Platform.exit();
		});        

        exit.setAccelerator(new KeyCodeCombination(KeyCode.X,KeyCombination.ALT_DOWN));
        menuFile.getItems().add(exit);
        
        final MenuItem aboutUs = new MenuItem("About Us");
        aboutUs.setOnAction(t -> {
        	final Stage dialog = new Stage();
        	dialog.centerOnScreen();
        	dialog.initModality(Modality.APPLICATION_MODAL);
        	dialog.setScene(creareAboutUs());
        	dialog.showAndWait();
		});        

        menuHelp.getItems().addAll(aboutUs);
        
        menuBar.getMenus().addAll(menuFile,menuHelp);
        return menuBar;
	}

	private Scene creareAboutUs() {
		return new Scene(new HBox(new Text("SSSSSSSSSSSSSSSSSSSS")));
	}

	private void showMainApplication(Stage stage) {
		Platform.runLater(() -> {
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				//Not important
			}
			final Stage newStage = new Stage();
			currentStage = newStage;
			final BorderPane borderPane = new BorderPane();
			borderPane.setTop(menu);
			borderPane.setCenter(new WizardMaker(stage));
			final Scene scence = new Scene(borderPane,WIDTH,HEIGHT);
			//final String css = this.getClass().getResource("/DarkTheme.css").toExternalForm();
			//scence.getStylesheets().add(css);
			newStage.setScene(scence);
			newStage.setResizable(false);
			newStage.setTitle("Hi-Rec Client");
			newStage.centerOnScreen();
			newStage.show();
			stage.close();
		});
	}

	/**
	 * @param stage
	 * @throws FileNotFoundException 
	 * @throws InterruptedException 
	 */
	private void showSplash(Stage initStage) throws FileNotFoundException, InterruptedException{
		initStage.centerOnScreen();
		final URL resource = HiRec.class.getResource("/logo.jpg");
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

	/* (non-Javadoc)
	 * @see javafx.application.Application#stop()
	 */
	@Override
	public void stop() throws Exception {
		Platform.exit();
	}
}
