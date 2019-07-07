package gui.controller;


import gui.Resetable;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutUsController implements Initializable, Resetable {

    private HostServices hostServices;

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @FXML
    public void clickOnHyperlink(ActionEvent event) {
        String link = ((Hyperlink) event.getSource()).getText();
        hostServices.showDocument(link);
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @Override
    public void reset() {

    }
}
