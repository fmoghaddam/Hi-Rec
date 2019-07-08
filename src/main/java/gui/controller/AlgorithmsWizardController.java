package gui.controller;

import gui.WizardControllerInterface;
import gui.model.ErrorMessage;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class AlgorithmsWizardController implements Initializable, WizardControllerInterface {
    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public ErrorMessage getErrorMessage() {
        return null;
    }

    @Override
    public void fillWithSampleData() {

    }

    @Override
    public void reset() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
