package com.validation.manager.core.tool.requirement.importer;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementImportException extends Exception {

    private static ResourceBundle rb =
            ResourceBundle.getBundle(
            "com.validation.manager.resources.VMMessages", Locale.getDefault());

    public RequirementImportException(String message) {
        super(rb.containsKey(message) ? rb.getString(message) : message);
    }
}
