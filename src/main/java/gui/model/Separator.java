package gui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author FBM
 *
 */
public enum Separator {
	SemiColon(";"),
	Tab("\\t"),
	Comma(",");
	
	private StringProperty text;
	
	/**
	 * 
	 */
	private Separator(String text) {
		this.text = new SimpleStringProperty(text);
	}
	
	public StringProperty getText(){
		return text;
	}
}
