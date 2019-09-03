package gui;

import com.google.common.eventbus.Subscribe;
import gui.controller.HomePageController;
import gui.messages.StopAllRequestMessage;
import gui.messages.WebPageOpeningRequestMessage;
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
import util.MessageBus;
import util.Pair;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
        root.setStyle("-fx-background-color: transparent");
        root.getChildren().add(new Circle(200, Color.RED));
        for (int i = 0; i < 180; i += 50) {
            Rectangle a = new Rectangle(450, 450);
            a.setFill(Color.rgb(i + 70, i + 20, i + 60));
            RotateTransition transition = new RotateTransition(Duration.seconds(6), a);
            transition.setFromAngle(0);
            transition.setToAngle(i / 5);

            transition.setDelay(Duration.seconds(0.5));
            transition.setCycleCount(1);
            transition.play();

            transition.setOnFinished(event -> {

                stage.close();
            });
            root.getChildren().add(a);
        }
        Image image = new Image(this.getClass().getResourceAsStream("/images/logo.png"));
        ImageView imageView = new ImageView(image);
        root.getChildren().add(imageView);
        Scene scene = new Scene(root, 700, 700);
        scene.setFill(Color.TRANSPARENT);

        stage.setScene(scene);
        stage.centerOnScreen();
        stage.requestFocus();
        stage.setAlwaysOnTop(true);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/images/logo.png")));
        stage.showAndWait();
    }

    @Subscribe
    public void openWebPageRequest(WebPageOpeningRequestMessage webPageOpeningRequestMessage) {
        getHostServices().showDocument(webPageOpeningRequestMessage.getWebpageURL());
    }

    @Override
    public void stop() throws Exception {

        super.stop();
        MessageBus.getInstance().getBus().post(new StopAllRequestMessage());

    }

    @Override
    public void start(Stage stage) throws Exception {
        MessageBus.getInstance().register(this);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<Pair<Parent, HomePageController>> callable = new Callable<Pair<Parent, HomePageController>>() {
            @Override
            public Pair<Parent, HomePageController> call() throws Exception {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/HomePageFXMLView.fxml"));
                Parent root = fxmlLoader.load();
                HomePageController controller = fxmlLoader.getController();
                return new Pair<>(root, controller);
            }
        };

        Future<Pair<Parent, HomePageController>> submit = executor.submit(callable);
        showSplashScreen();
        Pair<Parent, HomePageController> parentHomePageControllerPair = submit.get();
        executor.shutdownNow();


        parentHomePageControllerPair.getSecond().setStage(stage);

        Scene scene = new Scene(parentHomePageControllerPair.getFirst());

        scene.getStylesheets().add("/styles/stylesheet.css");
        stage.setMinHeight(600);
        stage.setMinWidth(600);
        stage.setTitle("Hi-Rec");
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/images/logo.png")));
        stage.setScene(scene);

        stage.show();
        stage.requestFocus();
    }


}
