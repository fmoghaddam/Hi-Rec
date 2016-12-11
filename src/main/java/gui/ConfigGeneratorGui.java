package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
		currentStage = stage;
		stage.setScene(new Scene(new WizardMaker(stage), WIDTH, HEIGHT));
		stage.show();
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
