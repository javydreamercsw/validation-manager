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
package net.sourceforge.javydreamercsw.validation.manager.web.file;

import com.vaadin.ui.Window;
import java.io.File;
import java.io.IOException;

/**
 * Display a file.
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public interface IFileDisplay {

    /**
     * Are we capable of displaying this file?
     *
     * @param f file to display
     * @return true if capable, false otherwise
     */
    boolean supportFile(File f);

    /**
     * Are we capable of displaying this file?
     *
     * @param name File name to display
     * @return true if capable, false otherwise
     */
    boolean supportFile(String name);

    /**
     * Get component to display the specified file.
     *
     * @param f File to display
     * @return Component to view this file.
     */
    Window getViewer(File f);

    /**
     * Load the file from a byte array (stored in the database)
     *
     * @param name File name
     * @param bytes File stored in bytes.
     * @return A proper file.
     * @throws java.io.IOException
     */
    File loadFile(String name, byte[] bytes) throws IOException;
}
