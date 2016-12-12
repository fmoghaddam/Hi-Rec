package gui.pages;

import gui.ConfigGeneratorGui;
import gui.WizardPage;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * @author FBM
 *
 */
public class FirstPage extends WizardPage {
	private VBox generateConfigVBox;
	private VBox runVBox;
	private Text generateConfigText;
	private Text runText;
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

		generateConfigText = new Text(
				"If this is the first time than you want to use this software, "
				+ "you need to first generate a configuration file. "
				+ "This option allows you to create your configuration file "
				+ "in an interactive way. Finally you will be able to "
				+ "generate your configuration file for next usage or "
				+ "just run the application with it.");
		generateConfigText.setWrappingWidth(ConfigGeneratorGui.WIDTH - 50);
		runText = new Text(
				"If you already have a configuration file "
				+ "and want to run the application based "
				+ "on that, this option can be used.");
		runText.setWrappingWidth(ConfigGeneratorGui.WIDTH - 50);

		generateConfigVBox = new VBox(10.0, generateConfigText, generateConfigFileBtn);
		generateConfigVBox.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
				+ "-fx-border-insets: 5;" + "-fx-border-radius: 5;" + "-fx-border-color: black;");
		generateConfigVBox.setAlignment(Pos.CENTER);

		runVBox = new VBox(10.0, runText, runApplicationBtn);
		runVBox.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
				+ "-fx-border-insets: 5;" + "-fx-border-radius: 5;" + "-fx-border-color: black;");
		runVBox.setAlignment(Pos.CENTER);

		generateConfigFileBtn.setOnAction(event -> {
			nextPage();
		});

		runApplicationBtn.setOnAction(event -> {
			navTo(6);
		});

		return initLayout();
	}

	private Parent initLayout() {
		final GridPane gridpane = new GridPane();
		gridpane.setHgap(10);
		gridpane.setVgap(10);
		gridpane.add(generateConfigVBox, 0, 0);
		gridpane.add(runVBox, 0, 1);
		gridpane.setAlignment(Pos.CENTER);
		return gridpane;
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
