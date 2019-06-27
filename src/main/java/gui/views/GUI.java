package gui.views;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Test extends Application {

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/AboutUsFXMLView.fxml"));
//        Parent root = fxmlLoader.load();
//        AboutUsController controller = fxmlLoader.<AboutUsController>getController();
//        controller.setHostServices(getHostServices());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/MainPageFXMLView.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/stylesheet.css");

        stage.setTitle("Test");
        stage.setScene(scene);
        stage.show();


    }


}
