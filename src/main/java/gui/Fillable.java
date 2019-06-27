package gui;

import gui.model.ErrorMessage;

public interface Fillable {

    boolean isValid();

    ErrorMessage getErrorMessage();

    void fillWithSampleData();
}
