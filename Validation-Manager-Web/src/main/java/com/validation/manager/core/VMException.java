package com.validation.manager.core;

import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class VMException extends Exception {

    String vm_message = "";

    public VMException() {
        super();
        vm_message = "";
    }

    public VMException(String s) {
        super(s);
        vm_message = s;
    }

    public VMException(List<String> messages) {
        for (String s : messages) {
            vm_message += s + "\n";
        }
    }

    public VMException(Throwable cause) {
        super(cause);
        vm_message = cause.getLocalizedMessage();
    }

    @Override
    public String toString() {
        return vm_message;
    }
}
