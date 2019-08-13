package gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;

public abstract class WizardPage extends VBox {
	public Button priorButton = new Button("_Previous");
	public Button nextButton = new Button("N_ext");
	public Button cancelButton = new Button("Cancel");

	protected List<Button> extraButtons = new ArrayList<>();
	public WizardPage(final String title){
		this(title,Collections.emptyList());
	}
	
	public WizardPage(final String title, List<Button> list) {
		extraButtons = list;
		final Label label = new Label();
		label.setText(title);
		label.setStyle("-fx-font-weight: bold; -fx-padding: 0 0 5 0;");
		getChildren().add(label);
		setId(title);
		setSpacing(5);
		this.setAlignment(Pos.CENTER);
		final Region spring = new Region();
		VBox.setVgrow(spring, Priority.ALWAYS);
		getChildren().addAll(getContent(), spring, getButtons());

		priorButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				priorPage();
			}
		});
		nextButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				if (validate()) {
					nextPage();
				}else{
					final Alert alert = new Alert(Alert.AlertType.ERROR);
		            alert.setTitle("Error");
		            alert.setContentText(getErrorMessage());
		            alert.initStyle(StageStyle.UTILITY);
		            alert.showAndWait();
				}
			}
		});
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				getWizard().cancel();
			}
		});
	}

	/**
	 * 
	 */
	protected String getErrorMessage() {
		return "";
	}

	public HBox getButtons() {
		Region spring = new Region();
		HBox.setHgrow(spring, Priority.ALWAYS);
		HBox buttonBar = new HBox(5);
		cancelButton.setCancelButton(true);
		buttonBar.getChildren().addAll(spring, priorButton, nextButton, cancelButton);
		for(final Button btn: extraButtons){
			buttonBar.getChildren().add(btn);
		}
		return buttonBar;
	}

	protected abstract void fillWithSampleData();
	protected abstract void reset();
	
	protected abstract Parent getContent();

	protected boolean validate(){
		return true;
	}

	protected void reloadIfNeeded(){
		
	}

	protected boolean shouldHideNext() {
		return false;
	}

	protected boolean shouldHideCancel() {
		return false;
	}

	protected boolean shouldHideBack() {
		return false;
	}
	
	protected boolean shouldDisbaleNext() {
		return false;
	}

	protected boolean shouldDisbaleCancel() {
		return false;
	}

	protected boolean shouldDisbaleBack() {
		return false;
	}

	public boolean hasNextPage() {
		return getWizard().hasNextPage();
	}

	public boolean hasPriorPage() {
		return getWizard().hasPriorPage();
	}

	public void nextPage() {
		getWizard().nextPage();
	}

	public void priorPage() {
		getWizard().priorPage();
	}

	public void navTo(String id) {
		getWizard().navTo(id);
	}

	public void navTo(int id) {
		getWizard().navTo(id);
	}

	public Wizard getWizard() {
		return (Wizard) getParent();
	}

	public void manageButtons() {
		if (!hasPriorPage()) {
			priorButton.setDisable(true);
		}

		if (!hasNextPage()) {
			nextButton.setDisable(true);
		}
	}

	/**
	 * 
	 */
	public void manageHidingButtons() {
		if (shouldHideNext()) {
			nextButton.setVisible(false);
		}
		if (shouldHideCancel()) {
			cancelButton.setVisible(false);
		}
		if (shouldHideBack()) {
			priorButton.setVisible(false);
		}

	}
	
	/**
	 * 
	 */
	public void manageDisbalingButtons() {
		if (shouldDisbaleNext()) {
			nextButton.setDisable(true);
		}
		if (shouldDisbaleCancel()) {
			cancelButton.setDisable(true);
		}
		if (shouldDisbaleBack()) {
			priorButton.setDisable(true);
		}

	}

	public void setExtraButtons(List<Button> extraButtons) {
		this.extraButtons = extraButtons;
	}

}
