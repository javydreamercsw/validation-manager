package com.validation.manager.core;

import com.validation.manager.core.ImportException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
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
    List<T> importFile() throws ImportException;

    List<T> importFile(boolean header) throws ImportException;

    boolean processImport() throws PreexistingEntityException;
}
