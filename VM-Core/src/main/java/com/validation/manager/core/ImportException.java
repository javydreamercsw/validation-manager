package com.validation.manager.core;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ImportException extends Exception {

    private static ResourceBundle rb =
            ResourceBundle.getBundle(
            "com.validation.manager.resources.VMMessages", Locale.getDefault());

    public ImportException(String message) {
        super(rb.containsKey(message) ? rb.getString(message) : message);
    }
}
