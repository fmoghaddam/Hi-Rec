package gui.controller.configurator;

import gui.Resetable;
import gui.Validable;
import gui.model.ConfigData;
import gui.model.ErrorMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ConfigReviewController implements Initializable, Resetable, Validable {

    @FXML
    private TextArea configTextField;

    public void refreshConfig() {
        fillContent();

    }

    @FXML
    void onSaveButtonAction(ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("config.properties");
        final File file = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());
        if (file != null) {
            try {
                final FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(configTextField.getText());
                fileWriter.close();
            } catch (final IOException ex) {

            }
        }
    }


    private void fillContent() {
        configTextField.setText(ConfigData.instance.allConfigToString());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshConfig();
    }

    @Override
    public void reset() {
        fillContent();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public ErrorMessage getErrorMessage() {
        return null;
    }
}
