package net.sourceforge.javydreamercsw.validation.manager.web.importer;

import com.vaadin.ui.Upload;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class FileUploader implements Upload.Receiver {

    private File file;         // File to write to.

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
            Exceptions.printStackTrace(ex);
            return null;
        } catch (IOException ex) {
            // Error while opening the file. Not reported here.
            Exceptions.printStackTrace(ex);
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
