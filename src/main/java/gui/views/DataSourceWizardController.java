package gui.views;

import gui.WizardControllerInterface;
import gui.model.ErrorMessage;
import gui.model.Separator;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class DataSourceWizardController implements Initializable, WizardControllerInterface {
    @FXML
    private TextField filePathField;

    @FXML
    private ComboBox<Separator> seperatorComboSelector;

    @FXML
    private Label fileLabel;

    private String defaultPath;

    public String getDefaultPath() {
        return defaultPath;
    }

    public void setDefaultPath(String defaultPath) {
        this.defaultPath = defaultPath;
    }

    public void setFileLabel(String text) {
        fileLabel.setText(text);
    }

    public TextField getFilePathField() {
        return filePathField;
    }

    public ComboBox<Separator> getSeperatorComboSelector() {
        return seperatorComboSelector;
    }

    public
    @FXML
    void onFileChooserAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("CSV", "*.csv"),
                new FileChooser.ExtensionFilter("TXT", "*.txt"));
        fileChooser.setTitle("Open Resource File");

        File file = fileChooser.showOpenDialog(null);
        filePathField.setText(file.getAbsolutePath());
    }

    @FXML
    void fileDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        String filePath = "";
        if (db.hasFiles()) {
            success = true;
            for (File file : db.getFiles()) {
                filePath = file.getAbsolutePath();
            }
        }
        event.setDropCompleted(success);
        event.consume();
        filePathField.setText(filePath);
    }

    @FXML
    void fileDragOver(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        } else {
            event.consume();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        seperatorComboSelector.setItems(FXCollections.observableArrayList(Separator.values()));
    }

    @Override
    public boolean isValid() {
        return false;
        //TODO: VALIDATION FOR DATASOURCE
    }

    @Override
    public ErrorMessage getErrorMessage() {
        return null;
        //TODO: ERROR HANDLING
    }

    @Override
    public void fillWithSampleData() {
        filePathField.setText(defaultPath);
    }

    @Override
    public void reset() {
        filePathField.setText("");
        seperatorComboSelector.getSelectionModel().clearSelection();
    }
}
