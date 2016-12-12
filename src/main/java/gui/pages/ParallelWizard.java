package gui.pages;

import gui.WizardPage;
import gui.model.ConfigData;
import gui.model.ErrorMessage;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * @author FBM
 *
 */
public class ParallelWizard extends WizardPage {

	private CheckBox runAlgorithmParallelCheckBox;
	private TextField runAlgorithmNumberOfThreadTextField;
	private Label runAlgorithmParallelDescription;

	private CheckBox runFoldsParallelCheckBox;
	private TextField runFoldsNumberOfThreadTextField;
	private Label runFoldParallelDescription;

	private ErrorMessage errorMessage;

	/**
	 * @param title
	 */
	public ParallelWizard() {
		super("Cross Validation setting");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.WizardPage#getContent()
	 */
	@Override
	public Parent getContent() {
		initAlgorithmParallelAttributes();
		initFoldParallelAttributes();
		initErrorLabel();
		return initLayout();
	}

	/**
	 * 
	 */
	private void initFoldParallelAttributes() {
		runFoldsParallelCheckBox = new CheckBox("Run Folds Parrallel?");
		runFoldsNumberOfThreadTextField = new TextField();
		runFoldsNumberOfThreadTextField.setDisable(true);
		ConfigData.instance.RUN_FOLDS_PARALLEL.bind(new SimpleStringProperty(String.valueOf(false)));
		runFoldsParallelCheckBox.selectedProperty()
				.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
					runFoldsNumberOfThreadTextField.setDisable(!newValue);
					ConfigData.instance.RUN_FOLDS_PARALLEL.bind(new SimpleStringProperty(String.valueOf(newValue)));
					if (!newValue) {
						runFoldsNumberOfThreadTextField.setText("");
						errorMessage.setText("");
					}
					final StringProperty textProperty = runFoldsNumberOfThreadTextField.textProperty();
					ConfigData.instance.RUN_FOLDS_NUMBER_OF_THREAD.bind(textProperty);
				});
		runFoldParallelDescription = new Label(
				"If you leave this element empty, maximum number of threads will be used");
	}

	/**
	 * 
	 */
	private void initErrorLabel() {
		errorMessage = new ErrorMessage();
	}

	/**
	 * 
	 */
	private void initAlgorithmParallelAttributes() {
		runAlgorithmParallelCheckBox = new CheckBox("Run Algorithms Parrallel?");
		runAlgorithmNumberOfThreadTextField = new TextField();
		runAlgorithmNumberOfThreadTextField.setDisable(true);
		ConfigData.instance.RUN_ALGORITHMS_PARALLEL.bind(new SimpleStringProperty(String.valueOf(false)));
		runAlgorithmParallelCheckBox.selectedProperty()
				.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
					runAlgorithmNumberOfThreadTextField.setDisable(!newValue);
					ConfigData.instance.RUN_ALGORITHMS_PARALLEL
							.bind(new SimpleStringProperty(String.valueOf(newValue)));
					if (!newValue) {
						runAlgorithmNumberOfThreadTextField.setText("");
						errorMessage.setText("");
					}
					final StringProperty textProperty = runAlgorithmNumberOfThreadTextField.textProperty();
					ConfigData.instance.RUN_ALGORITHMS_NUMBER_OF_THREAD.bind(textProperty);
				});
		runAlgorithmParallelDescription = new Label(
				"If you leave this element empty, maximum number of threads will be used");
	}

	/**
	 * @return
	 */
	private Parent initLayout() {

		final GridPane algorithmGrid = new GridPane();
		algorithmGrid.add(runAlgorithmParallelCheckBox, 0, 0);
		algorithmGrid.add(runAlgorithmNumberOfThreadTextField, 1, 0);
		algorithmGrid.setHgap(10);
		algorithmGrid.setVgap(10);
		
		final GridPane foldGrid = new GridPane();
		foldGrid.add(runFoldsParallelCheckBox, 0, 0);
		foldGrid.add(runFoldsNumberOfThreadTextField, 1, 0);
		foldGrid.setHgap(10);
		foldGrid.setVgap(10);
		
		final VBox mainLayout = new VBox(10.0, new VBox(5.0, algorithmGrid, runAlgorithmParallelDescription),
				new VBox(5.0, foldGrid, runFoldParallelDescription), errorMessage);
		return mainLayout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.WizardPage#validate()
	 */
	@Override
	public boolean validate() {
		final StringBuilder localErrorMessage = new StringBuilder();
		if (runAlgorithmParallelCheckBox.isSelected()) {
			if (runAlgorithmNumberOfThreadTextField.getText() != null
					&& !runAlgorithmNumberOfThreadTextField.getText().isEmpty()) {
				try {
					Integer.parseInt(runAlgorithmNumberOfThreadTextField.getText());
				} catch (final Exception exception) {
					localErrorMessage.append("Number if thread for running algorithms in parallel mode is not valid")
							.append("\n");
				}
			}
		}
		if (runFoldsParallelCheckBox.isSelected()) {
			if(runFoldsNumberOfThreadTextField.getText() != null
					&& !runFoldsNumberOfThreadTextField.getText().isEmpty()){
			try {
				Integer.parseInt(runFoldsNumberOfThreadTextField.getText());
			} catch (final Exception exception) {
				localErrorMessage.append("Number if thread for running folds in parallel mode is not valid")
						.append("\n");
			}
			}
		}
		if (localErrorMessage.toString().isEmpty()) {
			return true;
		} else {
			this.errorMessage.setText(localErrorMessage.toString());
			return false;
		}
	}

}
