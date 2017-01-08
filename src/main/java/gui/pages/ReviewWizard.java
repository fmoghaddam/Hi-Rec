package gui.pages;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import gui.ConfigGeneratorGui;
import gui.WizardPage;
import gui.model.ConfigData;
import javafx.beans.property.StringProperty;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

/**
 * @author FBM
 *
 */
public class ReviewWizard extends WizardPage {

	private static final Logger LOG = Logger.getLogger(ReviewWizard.class.getCanonicalName());

	private TextArea textArea;
	private Button exportButton;
	private Button importButton;
	private Button startButton;

	/**
	 * @param title
	 */
	public ReviewWizard() {
		super("Reveiw");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.WizardPage#getContent()
	 */
	@Override
	public Parent getContent() {
		textArea = new TextArea();
		textArea.setPrefHeight(ConfigGeneratorGui.HEIGHT - 100);
		exportButton = new Button("Export Config file");
		exportButton.setOnAction(event -> {
			final FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialFileName("config.properties");
			final File file = fileChooser.showSaveDialog(ConfigGeneratorGui.getCurrentStage());
			if (file != null) {
				try {
					final FileWriter fileWriter = new FileWriter(file);
					fileWriter.write(textArea.getText());
					fileWriter.close();
				} catch (final IOException ex) {
					LOG.error(ex.getMessage());
				}
			}
		});
		startButton = new Button("Run Application");
		startButton.setOnAction(event -> {
			fillConfigFileWithNewData();
			navTo(5);
		});

		importButton = new Button("Import Config file");
		importButton.setOnAction(event -> {
			handleImportFile();
		});

		fillContent();
		final VBox hbox = new VBox(10.0, textArea, new HBox(5.0, exportButton, importButton, startButton));
		return hbox;
	}

	private void fillConfigFileWithNewData() {
		try {
			final String filename = "config.properties";
			final FileWriter fw = new FileWriter(filename, false);
			fw.write(textArea.getText());
			fw.close();
		} catch (IOException ioe) {
			LOG.error(ioe);
		}
	}

	/**
	 * 
	 */
	private void handleImportFile() {
		final FileChooser fileChooser = new FileChooser();
		final File file = fileChooser.showOpenDialog(ConfigGeneratorGui.getCurrentStage());
		if (file != null) {
			BufferedReader br = null;
			FileReader fr = null;
			final StringBuilder fileContent = new StringBuilder();
			try {
				fr = new FileReader(file);
				br = new BufferedReader(fr);
				String sCurrentLine;
				br = new BufferedReader(new FileReader(file));
				while ((sCurrentLine = br.readLine()) != null) {
					fileContent.append(sCurrentLine).append("\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null)
						br.close();
					if (fr != null)
						fr.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			textArea.clear();
			textArea.setText(fileContent.toString());
		}
	}

	/**
	 * 
	 */
	private void fillContent() {
		final StringBuilder result = new StringBuilder();
		try {
			final Field[] allFields = ConfigData.instance.getClass().getDeclaredFields();
			for (final Field field : allFields) {
				if (field.getName().contains("instance")) {
					continue;
				}
				if (field.get(ConfigData.instance) instanceof StringProperty) {
					if (((StringProperty) field.get(ConfigData.instance)).get() != null) {
						result.append(field.getName()).append("=")
								.append(((StringProperty) field.get(ConfigData.instance)).get()).append("\n");
					} else {
						result.append(field.getName()).append("=").append("\n");
					}
				} else if (field.get(ConfigData.instance) instanceof Map) {
					@SuppressWarnings("unchecked")
					final Map<String, StringProperty> map = (Map<String, StringProperty>) field
							.get(ConfigData.instance);
					for (final Entry<String, StringProperty> entry : map.entrySet()) {
						if (entry.getValue().get() != null) {
							result.append(entry.getKey()).append("=").append(entry.getValue().get()).append("\n");
						} else {
							result.append(entry.getKey()).append("=").append("\n");
						}
					}
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		textArea.setText(result.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.WizardPage#reloadIfNeeded()
	 */
	@Override
	public void reloadIfNeeded() {
		fillContent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.WizardPage#shouldDisbaleNext()
	 */
	@Override
	protected boolean shouldDisbaleNext() {
		return true;
	}

	/* (non-Javadoc)
	 * @see gui.WizardPage#fillWithSampleData()
	 */
	@Override
	protected void fillWithSampleData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void reset() {
		textArea.setText("");
	}
}
