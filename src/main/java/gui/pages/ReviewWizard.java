package gui.pages;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;


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

	private TextArea textArea;
	private Button exportButton;
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
		textArea.setPrefHeight(ConfigGeneratorGui.HEIGHT-100);
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
					System.out.println(ex.getMessage());
				}
			}
		});
		startButton = new Button("Start The Application");
		startButton.setOnAction(event->{
			fillConfigFileWithNewData();
			navTo(7);
		});
		
		fillContent();
		final VBox hbox = new VBox(10.0, textArea, new HBox(5.0,exportButton,startButton));
		return hbox;
	}

	private void fillConfigFileWithNewData() {
		try
		{
		    final String filename= "config.properties";
		    final FileWriter fw = new FileWriter(filename,false); //the true will append the new data
		    fw.write(textArea.getText());//appends the string to the file
		    fw.close();
		}
		catch(IOException ioe)
		{
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
					}else{
						result.append(field.getName()).append("=").append("\n");
					}
				} else if (field.get(ConfigData.instance) instanceof Map) {
					@SuppressWarnings("unchecked")
					final Map<String, StringProperty> map = (Map<String, StringProperty>) field
							.get(ConfigData.instance);
					for (final Entry<String, StringProperty> entry : map.entrySet()) {
						if (entry.getValue().get() != null) {
							result.append(entry.getKey()).append("=").append(entry.getValue().get()).append("\n");
						}else{
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
	 * @see gui.WizardPage#shouldDisbaleNext()
	 */
	@Override
	protected boolean shouldDisbaleNext() {
		return true;
	}
	
}
