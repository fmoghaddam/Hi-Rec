package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public enum DataType {
	LowLevelFeature("LowLevelFeature"),
	Genre("Genre"),
	Tag("Tag"),
	Rating("Rating"),
	Personality("Personality"), 
	LowLevelFeatureGenre("LowLevelFeatureGenre");
	
	private StringProperty text;

	private DataType(String text) {
		this.text = new SimpleStringProperty(text);
	}

	public StringProperty getText() {
		return text;
	}
}
