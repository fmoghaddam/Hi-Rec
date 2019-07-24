package gui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author FBM
 *
 */
public enum Separator {
	Comma(","),
	SemiColon(";"),
	Tab("\\t");
	
	private StringProperty text;

	Separator(String text) {
		this.text = new SimpleStringProperty(text);
	}
	
	public StringProperty getText(){
		return text;
	}
}
