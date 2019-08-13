package gui.controller;

import gui.controller.configurator.ConfiguratorMainPageController;
import gui.controller.executor.ExecutorMainPageController;
import gui.messages.WebPageOpeningRequestMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.MessageBus;
import util.Pair;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class HomePageController implements Initializable {
    private Pair<Parent, ConfiguratorMainPageController> configuratorHomePage;
    private Pair<Parent, ExecutorMainPageController> executorHomePage;
    private Pair<Parent, HomePageController> homePage;
    @FXML
    private Button runButton;

    @FXML
    private AnchorPane rootAnchorPane;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void setStageContent(Parent root) {
        if (stage == null) {
            throw new IllegalStateException("No Stage Provided");
        }
        Scene scene;
        if (root.getScene() != null) {
            scene = root.getScene();
        } else {
            scene = new Scene(root);
        }
        stage.setScene(scene);
        stage.sizeToScene();
    }

    public void goToExecutorPage() {
        setStageContent(executorHomePage.getFirst());
        executorHomePage.getSecond().execute();
    }

    public void initializeExecutorPage() {
        FXMLLoader fxmlLoaderExecutor =
                new FXMLLoader(getClass().getResource("/views/ExecutorMainPageFXMLView.fxml"));
        try {
            Parent root = fxmlLoaderExecutor.load();
            ExecutorMainPageController controller = fxmlLoaderExecutor.getController();
            controller.setHomePage(homePage);
            executorHomePage = new Pair<>(root, controller);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToConfiguratorPage() {
        setStageContent(configuratorHomePage.getFirst());
    }

    public void goToHomePage() {
        setStageContent(homePage.getFirst());
        if (Files.exists(Paths.get("config.properties"))) {
            runButton.setDisable(false);
        } else {
            runButton.setDisable(true);
        }
    }


    @FXML
    void generateConfigButtonOnAction(ActionEvent event) {
        goToConfiguratorPage();
    }


    @FXML
    void runConfigButtonOnAction(ActionEvent event) {
        goToExecutorPage();
    }


    @FXML
    void onAboutUsButtonAction(ActionEvent event) {
        FXMLLoader fxmlLoaderConfigurator = new FXMLLoader(getClass().getResource("/views/AboutUsFXMLView.fxml"));
        try {
            Parent root = fxmlLoaderConfigurator.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onHelpButtonAction(ActionEvent event) {
        MessageBus.getInstance().getBus().post(
                new WebPageOpeningRequestMessage("https://fmoghaddam.github.io/Hi-Rec/#faq"));
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (Files.exists(Paths.get("config.properties"))) {
            runButton.setDisable(false);
        } else {
            runButton.setDisable(true);
        }
        homePage = new Pair<>(rootAnchorPane, this);
        FXMLLoader fxmlLoaderConfigurator =
                new FXMLLoader(getClass().getResource("/views/ConfiguratorMainPageFXMLView.fxml"));
        try {
            Parent root = fxmlLoaderConfigurator.load();
            ConfiguratorMainPageController controller = fxmlLoaderConfigurator.getController();
            controller.setHomePage(homePage);
            configuratorHomePage = new Pair<>(root, controller);
        } catch (IOException e) {
            e.printStackTrace();
        }

        initializeExecutorPage();


    }
}
