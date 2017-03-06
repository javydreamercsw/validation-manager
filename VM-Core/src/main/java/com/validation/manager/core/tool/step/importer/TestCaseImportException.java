package com.validation.manager.core.tool.step.importer;

import com.validation.manager.core.VMException;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TestCaseImportException extends VMException {

    public TestCaseImportException(String message) {
        super(message);
    }

    public TestCaseImportException(Throwable cause) {
        super(cause);
    }
}
