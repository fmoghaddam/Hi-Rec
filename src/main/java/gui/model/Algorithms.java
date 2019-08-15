package gui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author FBM
 *
 */
public enum Algorithms {
	ItemBasedNN("ItemBasedNN"), FactorizationMachine("FactorizationMachine"), AveragePopularity(
			"AveragePopularity"), FunkSVD("FunkSVD"), HybridTagLowLevel("HybridTagLowLevel");

	private StringProperty text;

    Algorithms(String text) {
		this.text = new SimpleStringProperty(text);
	}

	public StringProperty getText() {
		return text;
	}
}
