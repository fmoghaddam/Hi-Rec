package gui.pages;

import gui.WizardPage;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

/**
 * @author FBM
 *
 */
public class FirstPage extends WizardPage {
	private Button generateConfigFileBtn;
	private Button runApplicationBtn;

	/**
	 * @param title
	 */
	public FirstPage() {
		super("");
		this.setAlignment(Pos.CENTER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.WizardPage#getContent()
	 */
	@Override
	public Parent getContent() {
		generateConfigFileBtn = new Button("Generate new config.properties");
		runApplicationBtn = new Button("Run the application");

		generateConfigFileBtn.setOnAction(event -> {
			nextPage();
		});

		runApplicationBtn.setOnAction(event -> {
			navTo(5);
		});

		return initLayout();
	}

	private Parent initLayout() {
		final GridPane gridpane = new GridPane();
		gridpane.setHgap(10);
		gridpane.setVgap(10);
		gridpane.add(generateConfigFileBtn, 0, 0);
		gridpane.add(runApplicationBtn, 1, 0);
		gridpane.setAlignment(Pos.CENTER);
		return gridpane;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.WizardPage#validate()
	 */
	@Override
	public boolean validate() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.WizardPage#reloadIfNeeded()
	 */
	@Override
	public void reloadIfNeeded() {
		// Empty function
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.WizardPage#shouldHideNext()
	 */
	@Override
	protected boolean shouldHideNext() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.WizardPage#shouldHideCancel()
	 */
	@Override
	protected boolean shouldHideCancel() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.WizardPage#shouldHideBack()
	 */
	@Override
	protected boolean shouldHideBack() {
		return true;
	}

}
