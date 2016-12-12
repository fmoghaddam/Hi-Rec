package gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import run.HiRec;

/**
 * @author FBM
 *
 */
public class ConfigGeneratorGui extends Application {

	public static final int HEIGHT = 880;
	public static final int WIDTH = 670;

	private static Stage currentStage;

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
		
		Platform.runLater(() -> {
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
			}
			final Stage newStage = new Stage();
			currentStage = newStage;
			newStage.setScene(new Scene(new WizardMaker(stage), WIDTH, HEIGHT));
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
		System.exit(1);
	}
}
