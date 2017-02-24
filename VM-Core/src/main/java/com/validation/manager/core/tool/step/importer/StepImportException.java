package com.validation.manager.core.tool.step.importer;

import static java.util.Locale.getDefault;
import java.util.ResourceBundle;
import static java.util.ResourceBundle.getBundle;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class StepImportException extends Exception {

    private static ResourceBundle rb =
            getBundle(
            "com.validation.manager.resources.VMMessages", getDefault());

    public StepImportException(String message) {
        super(rb.containsKey(message) ? rb.getString(message) : message);
    }
}
