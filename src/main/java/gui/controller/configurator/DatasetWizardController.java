package gui.controller.configurator;

import gui.WizardControllerInterface;
import gui.model.ConfigData;
import gui.model.ErrorMessage;
import gui.model.Separator;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class DatasetWizardController implements Initializable, WizardControllerInterface {
    private ErrorMessage errorMessage;

    @FXML
    private CheckBox ratingsDatasetCheckBox;

    @FXML
    private CheckBox tagDatasetCheckBox;

    @FXML
    private CheckBox genresDatasetCheckBox;

    @FXML
    private CheckBox visualFeaturesDatasetCheckBox;


    @FXML
    private VBox datasetsVbox;


    private List<Pair<Parent, DataSourceWizardController>> dataSources;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        errorMessage = new ErrorMessage();
        dataSources = new ArrayList<>();
        final String home = System.getProperty("user.dir");
        CheckBox[] checkBoxes = {ratingsDatasetCheckBox, visualFeaturesDatasetCheckBox,
                genresDatasetCheckBox, tagDatasetCheckBox};
        String[] mandatoryFiles = {"Rating", "Visual Features", "Genre", "Tag"};
        String[] defaultPaths = {home + "\\data\\5%RatingsSampledByRating.csv",
                home + "\\data\\LLVisualFeatures13K_QuantileLog.csv",
                home + "\\data\\Genre.csv", home + "\\data\\Tag.csv"};
        StringProperty[] correspondFilePathConfigProperty = {
                ConfigData.instance.RATING_FILE_PATH, ConfigData.instance.LOW_LEVEL_FILE_PATH,
                ConfigData.instance.GENRE_FILE_PATH, ConfigData.instance.TAG_FILE_PATH};
        StringProperty[] correspondSeperatorConfigProperty = {
                ConfigData.instance.RATING_FILE_SEPARATOR, ConfigData.instance.LOW_LEVEL_FILE_SEPARATOR,
                ConfigData.instance.GENRE_FILE_SEPARATOR, ConfigData.instance.TAG_FILE_SEPARATOR};
        for (int i = 0; i < mandatoryFiles.length; i++) {
            createNewDataSource(mandatoryFiles[i], defaultPaths[i],
                    correspondFilePathConfigProperty[i],
                    correspondSeperatorConfigProperty[i], checkBoxes[i]);
        }


    }


    private void createNewDataSource(String fileLabel, String defaultPath,
                                     StringProperty filePathProperty, StringProperty seperatorProperty,
                                     CheckBox checkBox) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/DataSourceWizardFXMLView.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DataSourceWizardController controller = fxmlLoader.getController();
        controller.setFileLabel(fileLabel + " File");
        controller.setDefaultPath(defaultPath);
        controller.fillWithSampleData();
        controller.setEnablerCheckbox(checkBox);
        Pair<Parent, DataSourceWizardController> pair = new Pair<>(root, controller);
        dataSources.add(pair);
        datasetsVbox.getChildren().add(root);
        root.visibleProperty().bind(checkBox.selectedProperty());
        root.managedProperty().bind(checkBox.selectedProperty());

        //bindings

        seperatorProperty.bind(controller.getSeperatorComboSelector().getValue().getText());
        filePathProperty.bind(controller.getFilePathField().textProperty());
    }

    @Override
    public boolean isValid() {
        final StringBuilder overalErrorMessage = new StringBuilder();
        boolean overalError = false;
        //TODO: FIX FILE SELECTOR VALIDATION
//        if (ratingFileText.getText() == null || ratingFileText.getText().isEmpty()) {
//            overalErrorMessage.append("The rating file should be selected").append("\n");
//            overalError = true;
//        }
        return true;

    }

    @Override
    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void fillWithSampleData() {
        for (Pair<Parent, DataSourceWizardController> pair : dataSources) {
            pair.getSecond().fillWithSampleData();
        }
    }

    @Override
    public void reset() {
        for (Pair<Parent, DataSourceWizardController> pair : dataSources) {
            pair.getSecond().reset();
        }
    }

    @Override
    public void fillWithPropertyFile(Properties properties) {
        String[] correspondPropertyName = {
                "RATING_FILE_", "LOW_LEVEL_FILE_",
                "GENRE_FILE_", "TAG_FILE_"};
        for (int i = 0; i < correspondPropertyName.length; i++) {
            String filePath = properties.getProperty(correspondPropertyName[i] + "PATH");
            if (filePath.isEmpty()) {
                dataSources.get(i).getSecond().getEnablerCheckBox().setSelected(false);
            }
            dataSources.get(i).getSecond().getFilePathField().setText(filePath);
            dataSources.get(i).getSecond().getSeperatorComboSelector().getSelectionModel()
                    .select(Separator.fromString(correspondPropertyName[i] + "SEPARATOR"));
        }
    }
}
