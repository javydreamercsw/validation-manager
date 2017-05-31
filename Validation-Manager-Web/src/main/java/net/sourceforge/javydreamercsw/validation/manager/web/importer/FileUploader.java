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
package net.sourceforge.javydreamercsw.validation.manager.web.importer;

import com.vaadin.ui.Upload;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class FileUploader implements Upload.Receiver {

    private File file;         // File to write to.
    private static final Logger LOG
            = Logger.getLogger(FileUploader.class.getName());

    // Callback method to begin receiving the upload.
    @Override
    public OutputStream receiveUpload(String filename,
            String MIMEType) {
        FileOutputStream fos; // Output stream to write to
        try {
            file = File.createTempFile("upload",
                    filename.substring(filename.lastIndexOf('.')));
            // Open the file for writing.
            fos = new FileOutputStream(getFile());
        } catch (FileNotFoundException ex) {
            // Error while opening the file. Not reported here.
            LOG.log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            // Error while opening the file. Not reported here.
            LOG.log(Level.SEVERE, null, ex);
            return null;
        }
        return fos; // Return the output stream to write to
    }

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }
}
