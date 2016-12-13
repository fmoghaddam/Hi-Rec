/**
 * 
 */
package gui.pages;

import java.util.ArrayList;
import java.util.List;

import gui.WizardPage;
import gui.model.ConfigData;
import gui.model.ErrorMessage;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * @author FBM
 *
 */
public class AlgorithmWizard extends WizardPage {

	private ScrollPane scrollPane;

	private Label numberOfConfiguration;
	private Label numberOfConfigurationValue;
	private Slider slider;

	private GridPane gridpane;
	private GridPane algorithmGridpane;
	
	private ErrorMessage errorMessage;
	private List<AlgorithmComponent> algorithmComponents;

	/**
	 * @param title
	 */
	public AlgorithmWizard() {
		super("Algorithm Configuration");
	}

	/**
	 * 
	 */
	private void initConfigurationNumberAttributes() {
		numberOfConfiguration = new Label("Number of Algorithms");

		numberOfConfigurationValue = new Label("0");
		numberOfConfigurationValue.setFont(new Font("Arial", 30));

		slider = new Slider();
		slider.setMin(0);
		slider.setMax(10);
		slider.setValue(0);
		slider.setShowTickLabels(true);
		slider.setPrefWidth(400);
		slider.setMajorTickUnit(1);

		slider.valueProperty().addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> {
			numberOfConfigurationValue.setText(String.valueOf((int) slider.getValue()));
			ConfigData.instance.NUMBER_OF_CONFIGURATION
					.bind(new SimpleStringProperty(String.valueOf((int) slider.getValue())));
			final int value = (int) slider.getValue();
			ConfigData.instance.ALGORITHM_PARAMETERS.clear();
			addAlgorithmComponent(value);
		});
		
		algorithmComponents = new ArrayList<>();
	}

	/**
	 * @param value
	 */
	private void addAlgorithmComponent(int value) {
		algorithmGridpane.getChildren().clear();
		algorithmComponents.clear();
		for (int i = 1; i <= value; i++) {
			final AlgorithmComponent algorithmComponent = new AlgorithmComponent(i);
			algorithmComponents.add(algorithmComponent);
			algorithmGridpane.add(algorithmComponent.getLayout(), 0, i);
		}
		algorithmGridpane.setAlignment(Pos.CENTER);
	}

	/**
	 * 
	 */
	private void initErrorLabel() {
		errorMessage = new ErrorMessage();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.WizardPage#getContent()
	 */
	@Override
	public Parent getContent() {
		initConfigurationNumberAttributes();
		initErrorLabel();
		return initLayout();
	}

	/**
	 * @return
	 */
	private Parent initLayout() {
		gridpane = new GridPane();
		gridpane.setHgap(10);
		gridpane.setVgap(10);

		gridpane.add(numberOfConfiguration, 0, 0);
		gridpane.add(slider, 1, 0);
		gridpane.add(numberOfConfigurationValue, 2, 0);

		algorithmGridpane = new GridPane();
		algorithmGridpane.setHgap(20);
		algorithmGridpane.setVgap(10);

		final VBox vbox = new VBox(5, gridpane, algorithmGridpane);
		algorithmGridpane.setAlignment(Pos.CENTER);
		gridpane.setAlignment(Pos.CENTER);
		
		scrollPane = new ScrollPane();
		scrollPane.setContent(vbox);
		scrollPane.setStyle("-fx-background-color:transparent;-fx-background: rgb(255, 248, 220);");
		return scrollPane;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.WizardPage#validate()
	 */
	@Override
	public boolean validate() {
		if(slider.getValue()<=0){
			errorMessage.setText("At lease 1 algorithm should be configured");
			return false;
		}else{
			final StringBuilder result = new StringBuilder();
			for(int i=1;i<=algorithmComponents.size();i++){
				final AlgorithmComponent algo = algorithmComponents.get(i-1);
				final String validate = algo.validate();
				if(validate!=null && !validate.isEmpty()){
					result.append("In configuration "+i+": ").append(validate).append("\n");
				}
			}
			errorMessage.setText(result.toString());
			if(errorMessage.getText()==null || errorMessage.getText().isEmpty()){
				return true;
			}else{
				return false;
			}
		}
	}

	/* (non-Javadoc)
	 * @see gui.WizardPage#getErrorMessage()
	 */
	@Override
	protected String getErrorMessage() {
		return errorMessage.getText();
	}

}
