package gui.controller;


import gui.messages.WebPageOpeningRequestMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import util.MessageBus;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutUsController implements Initializable {

    @FXML
    public void clickOnHyperlink(ActionEvent event) {
        String link = ((Hyperlink) event.getSource()).getText();
        MessageBus.getInstance().getBus().post(new WebPageOpeningRequestMessage(link));
    }

    @FXML
    public void clickOnUserProfileHyperlink(ActionEvent event) {
        String link = ((Hyperlink) event.getSource()).getAccessibleHelp();
        MessageBus.getInstance().getBus().post(new WebPageOpeningRequestMessage(link));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

}
