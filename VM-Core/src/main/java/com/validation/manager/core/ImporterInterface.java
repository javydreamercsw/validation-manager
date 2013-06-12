package com.validation.manager.core;

import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface ImporterInterface<T> {

    /**
     * Import the file.
     *
     * @return List of imported entities
     * @throws ImportException If something goes wrong in the importing process.
     */
    List<T> importFile() throws VMException;

    List<T> importFile(boolean header) throws VMException;

    boolean processImport() throws VMException;
}
