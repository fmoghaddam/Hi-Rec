package gui.controller;

import gui.controller.configurator.ConfiguratorMainPageController;
import gui.controller.executor.ExecutorMainPageController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomePageController implements Initializable {
    private Pair<Parent, ConfiguratorMainPageController> configuratorHomePage;
    private Pair<Parent, ExecutorMainPageController> executorHomePage;
    private Pair<Parent, HomePageController> homePage;

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
    }

    public void goToConfiguratorPage() {
        setStageContent(configuratorHomePage.getFirst());
    }

    public void goToHomePage() {
        setStageContent(homePage.getFirst());
    }

    @FXML
    void aboutMenuOnAction(ActionEvent event) {
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
    void generateConfigButtonOnAction(ActionEvent event) {
        setStageContent(configuratorHomePage.getFirst());
    }

    @FXML
    void generateConfigMenuOnAction(ActionEvent event) {
        setStageContent(configuratorHomePage.getFirst());
    }

    @FXML
    void runConfigButtonOnAction(ActionEvent event) {
        setStageContent(executorHomePage.getFirst());
    }

    @FXML
    void runConfigMenuOnAction(ActionEvent event) {
        setStageContent(executorHomePage.getFirst());
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        homePage = new Pair<>(rootAnchorPane, this);
        FXMLLoader fxmlLoaderConfigurator = new FXMLLoader(getClass().getResource("/views/ConfiguratorMainPageFXMLView.fxml"));
        try {
            Parent root = fxmlLoaderConfigurator.load();
            ConfiguratorMainPageController controller = fxmlLoaderConfigurator.getController();
            controller.setHomePage(homePage);
            configuratorHomePage = new Pair<>(root, controller);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FXMLLoader fxmlLoaderExecutor = new FXMLLoader(getClass().getResource("/views/ExecutorMainPageFXMLView.fxml"));
        try {
            Parent root = fxmlLoaderExecutor.load();
            ExecutorMainPageController controller = fxmlLoaderExecutor.getController();
            executorHomePage = new Pair<>(root, controller);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
