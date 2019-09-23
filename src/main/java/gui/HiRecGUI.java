package gui;

import com.google.common.eventbus.Subscribe;
import gui.controller.HomePageController;
import gui.messages.StopAllRequestMessage;
import gui.messages.WebPageOpeningRequestMessage;
import javafx.animation.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
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
        Group logo = new Group();
        ParallelTransition parallelTransition = new ParallelTransition();
        String[] fixedParts = new String[]{"M134.57,29.83h21.69l.66.73V57.67l26-27.48h32.87" +
                "v.37l-34.18,37H156.92v53.12l-.66.73H134.57l-.66-.73V30.57Z",
                "M302,29.83q5.83,0,8.87,8.79V55.48q-32.62,35.26-34.84," +
                        "37H243.19v-.37l34.18-36.63h-35.5v40.3h68.36l.66.73" +
                        "v24.18l-.66.73H227.75q-6.08,0-8.87-9.16V39.72q0-6.78,8.22-9.89Z",
                "M398.07,29.83q5.83,0,8.87,8.79V54.74l-.66.73H337.93v40.3h68.36l.66.73" +
                        "v15q0,6.78-8.22,9.89H323.8q-5.83,0-8.87-8.79V39.72q0-6.78,8.22-9.89Z"};
        for (String path : fixedParts) {
            SVGPath svgPath = new SVGPath();
            svgPath.setContent(path);
            FillTransition fillTransition = new FillTransition();
            fillTransition.setShape(svgPath);
            fillTransition.setDelay(Duration.seconds(.5));
            fillTransition.setDuration(Duration.seconds(6));
            fillTransition.setFromValue(Color.web("#143a52"));
            fillTransition.setToValue(Color.web("#c9fdd7"));
            parallelTransition.getChildren().add(fillTransition);
            logo.getChildren().add(svgPath);
        }
        SVGPath hLogo = new SVGPath();
        hLogo.setContent("M13.71,29.83H27.18q6.08,0,8.87,9.16V62.44h46V39.72q0-6.78,8.22-9.89h14.13l.66.73" +
                "v90.12l-.66.73H90.94q-5.83,0-8.87-8.79V88.08h-46v23.45q0,6.78-8.22,9.89H13.71l-.66-.73V30.57Z");
        FillTransition hLogoFillTransition = new FillTransition();
        hLogoFillTransition.setShape(hLogo);
        hLogoFillTransition.setDuration(Duration.seconds(6));
        hLogoFillTransition.setFromValue(Color.web("#143a52"));
        hLogoFillTransition.setToValue(Color.web("#03051e"));
        parallelTransition.getChildren().add(hLogoFillTransition);
        logo.getChildren().add(hLogo);

        Group animatedGroup = new Group();
        String[] animatedParts = new String[]{
                "M130.94 98.08 108.27 121.09 108.27 121.42 130.29 121.42 130.94 120.76 130.94 98.08z",
                "M108.27 121.42 130.94 98.41 130.94 98.08 108.92 98.08 108.27 98.73 108.27 121.42z",
                "M130.94 75.74 108.27 98.75 108.27 99.08 130.29 99.08 130.94 98.42 130.94 75.74z",
                "M108.27 99.08 130.94 76.07 130.94 75.74 108.92 75.74 108.27 76.39 108.27 99.08z",
                "M130.94 53.17 108.27 76.18 108.27 76.51 130.29 76.51 130.94 75.86 130.94 53.17z",
                "M108.27 76.51 130.94 53.5 130.94 53.17 108.92 53.17 108.27 53.83 108.27 76.51z",
                "M108.27 53.17 130.94 30.16 130.94 29.83 108.92 29.83 108.27 30.49 108.27 53.17z"
        };
        SequentialTransition sequentialTransition = new SequentialTransition();
        sequentialTransition.setDelay(Duration.seconds(1));
        for (String path : animatedParts) {
            SVGPath svgPath = new SVGPath();
            svgPath.setOpacity(0);
            svgPath.setContent(path);
            animatedGroup.getChildren().add(svgPath);
            FadeTransition fadeTransition = new FadeTransition();
            fadeTransition.setNode(svgPath);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);
            fadeTransition.setDuration(Duration.millis(850));
            sequentialTransition.getChildren().add(fadeTransition);

            FillTransition fillTransition = new FillTransition();
            fillTransition.setShape(svgPath);
            fillTransition.setDuration(Duration.seconds(7));
            fillTransition.setFromValue(Color.BLACK);
            fillTransition.setToValue(Color.web("#03051e"));
            parallelTransition.getChildren().add(fillTransition);
        }


        Rectangle background = new Rectangle(500, 200);
        background.setArcHeight(60);
        background.setArcWidth(60);
        background.setFill(Color.web("#143a52"));
        background.setStroke(Color.WHITE);
        root.getChildren().add(background);
        Label label = new Label();
        label.setFont(Font.font("Calibri", 18));
        label.setTextFill(Color.WHITE);
        label.setAlignment(Pos.CENTER_LEFT);
        label.setTextAlignment(TextAlignment.LEFT);
        label.setPadding(new Insets(150, 0, 0, 0));
        Transition transition = new Transition() {
            {
                setCycleDuration(Duration.seconds(7));
                setCycleCount(1);
            }

            @Override
            protected void interpolate(double frac) {
                if (frac < 0.4) {
                    label.setText("Loading HiRec ...");
                } else if (frac < 0.7) {
                    label.setText("Looking for Algorithms ...");
                } else {
                    label.setText("Loading GUI components ...");
                }
            }
        };

        parallelTransition.getChildren().add(transition);
        parallelTransition.getChildren().add(sequentialTransition);

        logo.getChildren().add(animatedGroup);
        root.getChildren().add(logo);
        root.getChildren().add(label);
        parallelTransition.play();

        parallelTransition.setOnFinished(event -> {
            stage.close();
        });
        Scene scene = new Scene(root, 700, 700);
        scene.setFill(Color.TRANSPARENT);

        stage.setScene(scene);
        stage.centerOnScreen();
        stage.requestFocus();
        stage.setAlwaysOnTop(true);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/images/logo256x256.png")));


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
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/images/logo256x256.png")));
        stage.setScene(scene);

        stage.show();
        stage.requestFocus();
    }


}
