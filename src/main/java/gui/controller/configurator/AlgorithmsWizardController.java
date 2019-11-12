package gui.controller.configurator;

import gui.WizardControllerInterface;
import gui.model.ConfigData;
import gui.model.ErrorMessage;
import interfaces.AbstractRecommender;
import interfaces.Recommender;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TitledPane;
import org.apache.log4j.Logger;
import org.reflections.Reflections;
import util.ClassInstantiator;
import util.Pair;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;

public class AlgorithmsWizardController implements Initializable, WizardControllerInterface {

    private static final Logger LOG = Logger.getLogger(AlgorithmsWizardController.class.getSimpleName());

    private IntegerProperty numberOfConfigurations = new SimpleIntegerProperty(0);

    private List<Pair<Parent, AlgorithmConfigurationWizardController>> algorithms;

    private ErrorMessage errorMessage;

    @FXML
    private Accordion algorithmsHolderBox;

    @FXML
    private ChoiceBox<Recommender> availableAlgorithmsChoiceBox;

    @FXML
    void onNewAlgorithmAction(ActionEvent event) {
        numberOfConfigurations.setValue(numberOfConfigurations.get() + 1);
        addNewAlgorithmConfigurationWizard(availableAlgorithmsChoiceBox.getSelectionModel().getSelectedItem()
                , numberOfConfigurations.get());
    }

    @FXML
    void onRemoveAction(ActionEvent event) {
        if (numberOfConfigurations.get() > 0) {
            ConfigData.instance.removeAllAlgoParameterWithId(numberOfConfigurations.get());
            numberOfConfigurations.setValue(numberOfConfigurations.get() - 1);
            algorithmsHolderBox.getPanes().remove(algorithmsHolderBox.getPanes().size() - 1);
            algorithms.remove(algorithms.size() - 1);
        }

    }


    private void addNewAlgorithmConfigurationWizard(Recommender algorithm, int id) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/AlgorithmConfigurationWizardFXMLView.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        AlgorithmConfigurationWizardController controller = fxmlLoader.getController();
        controller.setAlgorithm(algorithm);
        algorithmsHolderBox.getPanes().add(new TitledPane(id + " - " + algorithm.getClass().getSimpleName(), root));
        Pair<Parent, AlgorithmConfigurationWizardController> pair = new Pair<>(root, controller);
        algorithms.add(pair);
        controller.createConfigurationGUI(algorithm, id);
    }


    @Override
    public boolean isValid() {
        if (numberOfConfigurations.getValue() <= 0) {
            errorMessage.setText("At lease 1 algorithm should be configured");
            return false;
        } else {
            final StringBuilder result = new StringBuilder();
            for (int i = 1; i <= algorithms.size(); i++) {
                AlgorithmConfigurationWizardController algoConfig = algorithms.get(i - 1).getSecond();
                if (!algoConfig.isValid()) {
                    result.append("In configuration " + i + ": ").append("\n").append(algoConfig.getErrorMessage().getText()).append("\n");
                }
            }
            errorMessage.setText(result.toString());
            return errorMessage.getText() == null || errorMessage.getText().isEmpty();
        }
    }

    @Override
    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void fillWithSampleData() {
        numberOfConfigurations.setValue(numberOfConfigurations.get() + 1);
        AbstractRecommender instantiateClass = (AbstractRecommender) ClassInstantiator
                .instantiateClass("algorithms.ItemBasedNN");
        addNewAlgorithmConfigurationWizard(instantiateClass, numberOfConfigurations.get());
        algorithms.get(0).getSecond().fillWithSampleData();
        algorithmsHolderBox.getPanes().get(0).setExpanded(true);
    }

    @Override
    public void reset() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorMessage = new ErrorMessage();
        Reflections reflections = new Reflections(".*");
        Set<Class<? extends Recommender>> classes = reflections.getSubTypesOf(Recommender.class);
        List<Recommender> availableAlgorithms = new ArrayList<>();
        for (Class<? extends Recommender> recommender : classes) {
            if (!Modifier.isAbstract(recommender.getModifiers())) {
                Recommender r = null;
                try {
                    r = recommender.newInstance();
                    availableAlgorithms.add(r);
                } catch (InstantiationException e) {
                    LOG.error(e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    LOG.error(e.getMessage(), e);
                }

            }

        }

        availableAlgorithmsChoiceBox.setItems(FXCollections.observableArrayList(availableAlgorithms));
        availableAlgorithmsChoiceBox.getSelectionModel().select(0);
        algorithms = new ArrayList<>();
        ConfigData.instance.NUMBER_OF_CONFIGURATION.bind(numberOfConfigurations.asString());
    }

    @Override
    public void fillWithPropertyFile(Properties properties) {
        int numberOfConfiguration = Integer.parseInt(properties.getProperty("NUMBER_OF_CONFIGURATION"));
        numberOfConfigurations.setValue(numberOfConfiguration);
        for (int i = 0; i < numberOfConfiguration; i++) {
            String propertyKey = "ALGORITHM_" + (i + 1) + "_NAME";
            String algoName = properties.getProperty(propertyKey);
            AbstractRecommender instantiateClass = (AbstractRecommender) ClassInstantiator
                    .instantiateClass(algoName);
            addNewAlgorithmConfigurationWizard(instantiateClass, i + 1);

            algorithms.get(i).getSecond().fillWithPropertyFile(properties);
        }
    }
}
