package gui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author FBM
 */
public enum SimilarityFunctions {
    Cosine("cosine");

    private StringProperty text;

    SimilarityFunctions(String text) {
        this.text = new SimpleStringProperty(text);
    }

    public StringProperty getText() {
        return text;
    }
}
