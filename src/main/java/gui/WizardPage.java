package gui;

import gui.model.ConfigData;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public abstract class WizardPage extends VBox {
	public Button priorButton = new Button("_Previous");
	public Button nextButton = new Button("N_ext");
	public Button cancelButton = new Button("Cancel");

	public WizardPage(String title) {
		final Label label = new Label();
		label.setText(title);
		label.setStyle("-fx-font-weight: bold; -fx-padding: 0 0 5 0;");
		getChildren().add(label);
		setId(title);
		setSpacing(5);
		setStyle(
				"-fx-padding:10; -fx-background-color: honeydew; -fx-border-color: derive(honeydew, -30%); -fx-border-width: 3;");
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
				System.err.println(ConfigData.instance);
				if (validate()) {
					nextPage();
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

	public HBox getButtons() {
		Region spring = new Region();
		HBox.setHgrow(spring, Priority.ALWAYS);
		HBox buttonBar = new HBox(5);
		cancelButton.setCancelButton(true);
		buttonBar.getChildren().addAll(spring, priorButton, nextButton, cancelButton);
		return buttonBar;
	}

	protected abstract Parent getContent();

	protected abstract boolean validate();

	protected abstract void reloadIfNeeded();

	protected boolean shouldHideNext() {
		return false;
	}

	protected boolean shouldHideCancel() {
		return false;
	}

	protected boolean shouldHideBack() {
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

}
