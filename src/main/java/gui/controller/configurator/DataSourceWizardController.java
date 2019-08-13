package gui.controller.configurator;


import gui.WizardControllerInterface;
import gui.messages.DataSourceChanged;
import gui.model.ErrorMessage;
import gui.model.Separator;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import util.MessageBus;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.ResourceBundle;

public class DataSourceWizardController implements Initializable, WizardControllerInterface {


    @FXML
    private HBox infoAreaHBox;


    @FXML
    private VBox dropAreaVBox;


    @FXML
    private TextField filePathField;

    @FXML
    private ComboBox<Separator> seperatorComboSelector;

    @FXML
    private Label fileLabel;

    @FXML
    private SVGPath dropIconSVG;

    private String defaultPath;
    @FXML
    private Label dropFileLabel;
    private String lastFilePath;

    private CheckBox enablerCheckBox;

    public String getDefaultPath() {
        return defaultPath;
    }

    public void setDefaultPath(String defaultPath) {
        this.defaultPath = defaultPath;
    }

    public void setFileLabel(String text) {
        fileLabel.setText(text);
        dropFileLabel.setText(text);
    }

    public CheckBox getEnablerCheckBox() {
        return enablerCheckBox;
    }

    public TextField getFilePathField() {
        return filePathField;
    }

    public ComboBox<Separator> getSeperatorComboSelector() {
        return seperatorComboSelector;
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

        infoAreaHBox.setVisible(true);
        infoAreaHBox.setManaged(true);
        dropAreaVBox.setVisible(false);
        dropAreaVBox.setManaged(false);
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
        infoAreaHBox.setVisible(false);
        infoAreaHBox.setManaged(false);
        seperatorComboSelector.setItems(FXCollections.observableArrayList(Separator.values()));
        seperatorComboSelector.getSelectionModel().select(0);

    }

    @FXML
    void onFileUploadMouseClicked(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("CSV", "*.csv"),
                new FileChooser.ExtensionFilter("TXT", "*.txt"));
        fileChooser.setTitle("Open Resource File");

        File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        if (file != null) {

            if (Files.exists(Paths.get(file.getAbsolutePath()))) {
                filePathField.setText(file.getAbsolutePath());
                infoAreaHBox.setVisible(true);
                infoAreaHBox.setManaged(true);
                dropAreaVBox.setVisible(false);
                dropAreaVBox.setManaged(false);
            }
        }

    }

    @FXML
    void editSourceOnAction(ActionEvent event) {
        reset();
    }

    @FXML
    void onFileUploadMouseEntered(MouseEvent event) {
        dropIconSVG.setFill(Color.web("226089"));
    }

    @FXML
    void onFileUploadMouseExited(MouseEvent event) {
        dropIconSVG.setFill(Color.web("000000"));
    }

    @Override
    public boolean isValid() {
        return true;
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
        infoAreaHBox.setVisible(true);
        infoAreaHBox.setManaged(true);
        dropAreaVBox.setVisible(false);
        dropAreaVBox.setManaged(false);
        getSeperatorComboSelector().getSelectionModel().selectFirst();
    }


    @Override
    public void reset() {
        filePathField.setText("");
        seperatorComboSelector.getSelectionModel().select(0);
        infoAreaHBox.setVisible(false);
        infoAreaHBox.setManaged(false);
        dropAreaVBox.setVisible(true);
        dropAreaVBox.setManaged(true);
    }

    @Override
    public void fillWithPropertyFile(Properties properties) {

    }

    public void setEnablerCheckbox(CheckBox checkBox) {
        this.enablerCheckBox = checkBox;
        enablerCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue) {
                if (lastFilePath == null) {
                    lastFilePath = getFilePathField().textProperty().getValue();
                } else {
                    getFilePathField().textProperty().setValue(lastFilePath);
                }
            } else {
                if (lastFilePath == null) {
                    lastFilePath = getFilePathField().textProperty().getValue();
                }
                getFilePathField().textProperty().setValue("");
            }
            MessageBus.getInstance().getBus().post(new DataSourceChanged());
        });
    }
}
