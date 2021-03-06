package gui.pages;

import java.io.File;
import java.net.URL;

import gui.ConfigGeneratorGui;
import gui.WizardPage;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import run.HiRec;

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
				"If this is the first time you want to use this software, "
				+ "you need to first generate a configuration file. "
				+ "This option allows you to create your configuration file "
				+ "in an interactive way. Finally you will be able to "
				+ "generate your configuration file for next usage or "
				+ "just run the application.");
		generateConfigText.setTextAlignment(TextAlignment.JUSTIFY);

		generateConfigText.setWrappingWidth(ConfigGeneratorGui.WIDTH - 60);
		runText = new Text(
				"If you already have a configuration file "
				+ "and want to run the application based "
				+ "on that, select this option.");
		runText.setWrappingWidth(ConfigGeneratorGui.WIDTH - 60);
		runText.setTextAlignment(TextAlignment.JUSTIFY);
		
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
			navTo(4);
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
		
		final URL resourceMilan = HiRec.class.getResource("/images/milan.png");
		final File imageFileMilan = new File(resourceMilan.getFile());
		final Image imageMilan = new Image(imageFileMilan.toURI().toString());
        final ImageView ivMilan = new ImageView();
        ivMilan.setImage(imageMilan);
        
        final URL resourceUnibz = HiRec.class.getResource("/images/unibz.png");
		final File imageFileUnibz = new File(resourceUnibz.getFile());
		final Image imageUnibz = new Image(imageFileUnibz.toURI().toString());
        final ImageView ivUnibz = new ImageView();
        ivUnibz.setImage(imageUnibz);
        
        final URL resourceOkit = HiRec.class.getResource("/images/okit.png");
		final File imageFileOkit = new File(resourceOkit.getFile());
		final Image imageOkit = new Image(imageFileOkit.toURI().toString());
        final ImageView ivOkit = new ImageView();
        ivOkit.setImage(imageOkit);
        
        final HBox logoHBox = new HBox(5.0,ivMilan,ivUnibz);
        final VBox logoVBox = new VBox(5.0,logoHBox,ivOkit);
        logoVBox.setAlignment(Pos.CENTER);
        logoHBox.setAlignment(Pos.CENTER);
        gridpane.add(logoVBox, 0, 2);
		
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

	/* (non-Javadoc)
	 * @see gui.WizardPage#fillWithSampleData()
	 */
	@Override
	protected void fillWithSampleData() {
		
	}

	@Override
	protected void reset() {
		
	}
}
