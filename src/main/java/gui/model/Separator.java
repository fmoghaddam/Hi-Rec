package gui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author FBM
 */
public enum Separator {
    Comma(","),
    SemiColon(";"),
    Tab("\\t");

    private StringProperty text;

    Separator(String text) {
        this.text = new SimpleStringProperty(text);
    }

    public static Separator fromString(String separator) {
        if (separator.equals(Comma.getText().getValue())) {
            return Comma;
        } else if (separator.equals(SemiColon.getText().getValue())) {
            return SemiColon;
        } else if (separator.equals(Tab.getText().getValue())) {
            return Tab;
        }
        return null;
    }

    public StringProperty getText() {
        return text;
    }
}
