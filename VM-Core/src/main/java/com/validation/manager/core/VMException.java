package com.validation.manager.core;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class VMException extends Exception {

    private String vm_message = "";
    private static ResourceBundle rb =
            ResourceBundle.getBundle(
            "com.validation.manager.resources.VMMessages", Locale.getDefault());

    public VMException() {
        super();
    }

    public VMException(String message) {
        super(rb.containsKey(message) ? rb.getString(message) : message);
    }

    public VMException(List<String> messages) {
        for (String s : messages) {
            vm_message += s + "\n";
        }
    }

    public VMException(Throwable cause) {
        super(cause);
    }

    @Override
    public String toString() {
        return vm_message.isEmpty() ? super.getLocalizedMessage() : vm_message;
    }
}
