package gui.views;

import gui.Navigator;
import gui.Resetable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainPageController implements Initializable, Resetable {
    @FXML
    private BorderPane borderPane;
    private Navigator navigator;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        navigator = new Navigator();

        String[] allPages = {"/views/DatasetWizardFXMLView.fxml", "/views/GeneralFeatureWizardFXMLView.fxml"};
        for (String page : allPages) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(page));
            try {
                Parent root = fxmlLoader.load();
                navigator.addPage(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        borderPane.setCenter(navigator.getCurrentPage());
    }

    @FXML
    void onNextButtonAction(ActionEvent event) {
        borderPane.setCenter(navigator.getNextPage());
    }

    @FXML
    void onPreviousButtonAction(ActionEvent event) {
        borderPane.setCenter(navigator.getPreviousPage());
    }

    @Override
    public void reset() {

    }
}
