/*
 * Copyright 2017 Javier A. Ortiz Bultron javier.ortiz.78@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.validation.manager.core;

import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 * @param <T> Class type for template.
 */
public interface ImporterInterface<T> {

    /**
     * Import the file.
     *
     * @return List of imported entities
     * @throws VMException If something goes wrong in the importing process.
     */
    List<T> importFile() throws VMException;

    /**
     * Import the file.
     *
     * @param header File has header row.
     * @return List of imported entities
     * @throws VMException If something goes wrong in the importing process.
     */
    List<T> importFile(boolean header) throws VMException;

    /**
     * Process the imported data.
     *
     * @return true if processed successfully, false otherwise.
     * @throws VMException if there was an issue during import.
     */
    boolean processImport() throws VMException;
}
