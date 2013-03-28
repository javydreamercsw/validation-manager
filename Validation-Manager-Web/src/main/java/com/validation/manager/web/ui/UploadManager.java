package com.validation.manager.web.ui;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
class UploadManager extends CustomComponent implements Upload.SucceededListener, Upload.FailedListener, Upload.Receiver {

    private File file; // File to write to.
    private boolean success = false;
    private static final Logger LOG = Logger.getLogger(UploadManager.class.getSimpleName());

    // Callback method to begin receiving the upload.
    @Override
    public OutputStream receiveUpload(String filename, String MIMEType) {
        FileOutputStream fos; // Output stream to write to
        VMWeb.getInstance().setFileName(filename);
        try {
            //Create upload folder if needed
            File uploads = new File(System.getProperty("user.dir") 
                    + System.getProperty("file.separator"));
            uploads.mkdirs();
            uploads.deleteOnExit();
            file = File.createTempFile("temp", ".vmtf", uploads);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return null;
        }
        getFile().deleteOnExit();
        try {
            // Open the file for writing.
            fos = new FileOutputStream(getFile());
        } catch (final java.io.FileNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return null;
        }
        return fos; // Return the output stream to write to
    }

    @Override
    public void uploadSucceeded(SucceededEvent event) {
        success = true;
    }

    @Override
    public void uploadFailed(FailedEvent event) {
        LOG.log(Level.SEVERE, null, event.getReason());
        file = null;
        VMWeb.getInstance().setFileName(null);
        success = false;
    }

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return VMWeb.getInstance().getFileName();
    }

    /**
     * @return the success
     */
    public boolean isSuccess() {
        return success;
    }
}
