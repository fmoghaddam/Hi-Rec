package gui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author FBM
 *
 */
public enum Algorithms {
	ItemBasedNN("ItemBasedNN"), FactorizationMachine("FactorizationMachine"), AveragePopularity(
			"AveragePopularity"), FunkSVD("FunkSVD"), HybridTagLowLevel("HybridTagLowLevel"), ImplicitDataMatrixFactorization("ImplicitDataMatrixFactorization");

	private StringProperty text;

	private Algorithms(String text) {
		this.text = new SimpleStringProperty(text);
	}

	public StringProperty getText() {
		return text;
	}
}
