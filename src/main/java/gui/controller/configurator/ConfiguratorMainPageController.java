package gui.controller.configurator;

import gui.*;
import gui.controller.HomePageController;
import gui.model.ConfigData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Callback;
import util.Pair;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.ResourceBundle;

public class ConfiguratorMainPageController implements Initializable, Resetable {
    @FXML
    private BorderPane borderPane;
    private Navigator navigator;

    @FXML
    private Button previousPageButton;

    @FXML
    private Button homePageButton;


    @FXML
    private TreeView<Pair<String, Integer>> treeView;

    @FXML
    private Button runButton;

    @FXML
    private Button nextPageButton;

    private Pair<Parent, HomePageController> homePage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        navigator = new Navigator();

        TreeItem pages = new TreeItem("Pages");

        treeView.setOnMouseClicked(event -> {
            TreeItem<Pair<String, Integer>> selectedItem = treeView.getSelectionModel().getSelectedItem();
            Validable currentPageController1 = (Validable) navigator.getCurrentPageController();
            if (currentPageController1.isValid()) {
                borderPane.setCenter(navigator.navigateToIndex(selectedItem.getValue().getSecond()));
                Initializable currentPageController = navigator.getCurrentPageController();
                if (currentPageController.getClass().equals(ConfigReviewController.class)) {
                    ((ConfigReviewController) currentPageController).refreshConfig();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input Error");
                alert.setHeaderText("Please fix the following configuration errors: ");
                alert.setContentText(currentPageController1.getErrorMessage().getText());
                alert.showAndWait();
            }
            refreshButtonBar();
        });

        treeView.setCellFactory(new Callback<TreeView<Pair<String, Integer>>, TreeCell<Pair<String, Integer>>>() {
            @Override
            public TreeCell<Pair<String, Integer>> call(TreeView<Pair<String, Integer>> param) {
                return new TreeCell<Pair<String, Integer>>() {
                    @Override
                    protected void updateItem(Pair<String, Integer> item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText("");
                            setGraphic(null);
                            setCursor(Cursor.DEFAULT);
                        } else {
                            setCursor(Cursor.HAND);
                            setGraphic(new Circle(4, Color.GRAY));
                            setText(item.getFirst());
                        }
                    }
                };
            }
        });

        treeView.setRoot(pages);
        treeView.setShowRoot(false);

        String[] allPagesNames = {"Dataset Wizard", "Algorithms Wizard", "General Features", "Confirm Page"};
        String[] allPages = {"/views/DatasetWizardFXMLView.fxml",
                "/views/AlgorithmsWizardFXMLView.fxml",
                "/views/GeneralFeatureWizardFXMLView.fxml"
                , "/views/ConfigReviewFXMLView.fxml"};
        boolean loadFromPropertyFile = Files.exists(Paths.get("config.properties"));
        Properties properties = null;
        if (loadFromPropertyFile) {
            properties = new Properties();
            try (FileReader fileReader = new FileReader(new File("config.properties"))) {
                properties.load(fileReader);
            } catch (Exception ex) {

            }

        }
        for (int i = 0; i < allPages.length; i++) {
            String page = allPages[i];

            TreeItem<Pair<String, Integer>> treeItem = new TreeItem(new Pair<>(allPagesNames[i], i));

            pages.getChildren().add(treeItem);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(page));
            try {
                Parent root = fxmlLoader.load();
                Initializable controller = fxmlLoader.getController();
                if (loadFromPropertyFile && controller instanceof FillableFromPropertiy) {
                    ((FillableFromPropertiy) controller).fillWithPropertyFile(properties);
                } else if (controller instanceof Fillable) {
                    ((Fillable) controller).fillWithSampleData();
                }
                navigator.addPage(root, controller);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        borderPane.setCenter(navigator.getCurrentPage());
        refreshButtonBar();
    }

    @FXML
    void fillAllPagesMenuOnAction(ActionEvent event) {

    }

    @FXML
    void fillThisPageMenuOnAction(ActionEvent event) {

    }


    @FXML
    void resetAllPagesMenuOnAction(ActionEvent event) {

    }

    @FXML
    void resetThisPageMenuOnAction(ActionEvent event) {

    }

    private void fillConfigFileWithNewData() {
        try {
            String filename = "config.properties";
            FileWriter fw = new FileWriter(filename, false);
            fw.write(ConfigData.instance.allConfigToString());
            fw.close();
        } catch (IOException ioe) {

        }
    }

    @FXML
    void onRunButtonAction(ActionEvent event) {
        fillConfigFileWithNewData();
        reset();
        refreshButtonBar();
        homePage.getSecond().goToExecutorPage();
    }

    @FXML
    void homePageButtonOnAction(ActionEvent event) {
        fillConfigFileWithNewData();
        reset();
        refreshButtonBar();
        homePage.getSecond().goToHomePage();
    }

    @FXML
    void onNextButtonAction(ActionEvent event) {
        Validable currentPageController1 = (Validable) navigator.getCurrentPageController();
        if (currentPageController1.isValid()) {
            borderPane.setCenter(navigator.getNextPage());
            Initializable currentPageController = navigator.getCurrentPageController();
            if (currentPageController.getClass().equals(ConfigReviewController.class)) {
                ((ConfigReviewController) currentPageController).refreshConfig();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText("Please fix the following configuration errors: ");
            alert.setContentText(currentPageController1.getErrorMessage().getText());
            alert.showAndWait();
        }
        refreshButtonBar();
    }

    private void refreshButtonBar() {
        treeView.getSelectionModel().select(navigator.getCurrentPageIndex());
        if (navigator.isFirstPage()) {
            nextPageButton.setDisable(false);
            previousPageButton.setDisable(true);
            runButton.setVisible(false);
            runButton.setManaged(false);
        } else if (navigator.isLastPage()) {
            nextPageButton.setDisable(true);
            previousPageButton.setDisable(false);
            runButton.setVisible(true);
            runButton.setManaged(true);
        } else {
            runButton.setVisible(false);
            runButton.setManaged(false);
            previousPageButton.setDisable(false);
            nextPageButton.setDisable(false);
        }
    }

    @FXML
    void onPreviousButtonAction(ActionEvent event) {
        borderPane.setCenter(navigator.getPreviousPage());
        refreshButtonBar();
    }

    @Override
    public void reset() {
        borderPane.setCenter(navigator.goToFirstPage());
        refreshButtonBar();
    }

    public void setHomePage(Pair<Parent, HomePageController> homePage) {
        this.homePage = homePage;
    }
}
