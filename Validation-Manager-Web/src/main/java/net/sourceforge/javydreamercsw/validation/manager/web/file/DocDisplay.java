package net.sourceforge.javydreamercsw.validation.manager.web.file;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Window;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import net.sourceforge.javydreamercsw.validation.manager.web.VMWindow;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = IFileDisplay.class)
public class DocDisplay extends AbstractFileDisplay
        implements IFileDisplay {

    @Override
    public boolean supportFile(String name) {
        return FilenameUtils.getExtension(name).equals("doc");
    }

    @Override
    public Window getViewer(File f) {
        FileInputStream fis = null;
        Window w = new VMWindow(f.getName());
        w.center();
        w.setModal(true);
        w.setHeight(80, Sizeable.Unit.PERCENTAGE);
        w.setWidth(80, Sizeable.Unit.PERCENTAGE);
        //Just a plain panel will do
        TextArea text = new TextArea();
        text.setSizeFull();
        StringBuilder sb = new StringBuilder();
        try {
            fis = new FileInputStream(f);
            HWPFDocument document = new HWPFDocument(fis);
            WordExtractor extractor = new WordExtractor(document);
            String[] fileData = extractor.getParagraphText();
            for (String fileData1 : fileData) {
                if (fileData1 != null) {
                    sb.append(fileData1).append("\n");
                }
            }
            text.setValue(sb.toString());
            text.setReadOnly(true);
            w.setContent(text);
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return w;
    }
}
