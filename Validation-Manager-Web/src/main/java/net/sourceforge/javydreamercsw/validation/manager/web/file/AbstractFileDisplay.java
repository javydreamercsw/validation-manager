package net.sourceforge.javydreamercsw.validation.manager.web.file;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public abstract class AbstractFileDisplay implements IFileDisplay {

    @Override
    public boolean supportFile(File f) {
        return supportFile(f.getName());
    }

    @Override
    public File loadFile(String name, byte[] bytes) throws IOException {
        File result = new File(System.getProperty("java.io.tmpdir")
                + System.getProperty("file.separator") + name);
        FileUtils.writeByteArrayToFile(result, bytes);
        return result;
    }
}
