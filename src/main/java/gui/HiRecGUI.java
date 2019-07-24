package gui;

import gui.controller.HomePageController;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class HiRecGUI extends Application {

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


    private void showSplashScreen() {
        Stage stage = new Stage();
        StackPane root = new StackPane();
        root.getChildren().add(new Circle(200, Color.RED));
        for (int i = 0; i < 180; i += 10) {
            Rectangle a = new Rectangle(450, 450);
            a.setFill(Color.rgb(i + 70, i + 20, i + 60));
            RotateTransition transition = new RotateTransition(Duration.seconds(10), a);
            transition.setFromAngle(360);
            transition.setToAngle(i);

            transition.setDelay(Duration.seconds(0.5));
            transition.setCycleCount(1);
            transition.play();
            transition.setOnFinished(event -> {

                stage.close();
            });
            root.getChildren().add(a);
        }
        Image image = new Image("/images/logo.png");
        ImageView imageView = new ImageView(image);
        root.getChildren().add(imageView);
        Scene scene = new Scene(root, 700, 700);
        scene.setFill(Color.TRANSPARENT);

        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setAlwaysOnTop(true);
        stage.initStyle(StageStyle.TRANSPARENT);

        stage.showAndWait();
    }

    @Override
    public void start(Stage stage) throws Exception {
        showSplashScreen();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/HomePageFXMLView.fxml"));
        Parent root = fxmlLoader.load();
        HomePageController controller = fxmlLoader.getController();
        controller.setStage(stage);

        Scene scene = new Scene(root);

        scene.getStylesheets().add("/styles/stylesheet.css");
        stage.setMinHeight(600);
        stage.setMinWidth(600);
        stage.setTitle("Hi-Rec");
        stage.setScene(scene);
        stage.show();
    }


}
