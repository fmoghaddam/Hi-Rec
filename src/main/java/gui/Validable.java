package gui;

import gui.model.ErrorMessage;

public interface Validable {
    boolean isValid();

    ErrorMessage getErrorMessage();
}
