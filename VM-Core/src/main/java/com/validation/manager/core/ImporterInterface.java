package com.validation.manager.core;

import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 * @param <T>
 */
public interface ImporterInterface<T> {

    /**
     * Import the file.
     *
     * @return List of imported entities
     * @throws ImportException If something goes wrong in the importing process.
     */
    List<T> importFile() throws VMException;

    /**
     * Import the file.
     *
     * @param header File has header row.
     * @return List of imported entities
     * @throws ImportException If something goes wrong in the importing process.
     */
    List<T> importFile(boolean header) throws VMException;

    /**
     * Process the imported data.
     *
     * @return true if processed successfully, false otherwise.
     * @throws VMException
     */
    boolean processImport() throws VMException;
}
