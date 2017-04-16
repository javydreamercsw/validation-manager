package net.sourceforge.javydreamercsw.validation.manager.web.file;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Window;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import net.sourceforge.javydreamercsw.validation.manager.web.VMWindow;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = IFileDisplay.class)
public class DocxDisplay extends AbstractFileDisplay
        implements IFileDisplay {

    @Override
    public boolean supportFile(String name) {
        return FilenameUtils.getExtension(name).equals("docx");
    }

    @Override
    public Window getViewer(File f) {
        FileInputStream fis = null;
        Window w = new VMWindow(f.getName());
        w.center();
        w.setModal(true);
        w.setHeight(80, Sizeable.Unit.PERCENTAGE);
        w.setWidth(80, Sizeable.Unit.PERCENTAGE);
        Panel p = new Panel();
        p.setSizeFull();
        //Just a plain panel will do
        TextArea text = new TextArea();
        text.setSizeFull();
        StringBuilder sb = new StringBuilder();
        try {
            fis = new FileInputStream(f);
            XWPFDocument document = new XWPFDocument(fis);
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            paragraphs.forEach((para) -> {
                sb.append(para.getText()).append("\n");
            });
            text.setValue(sb.toString());
            text.setReadOnly(true);
            p.setContent(text);
            w.setContent(p);
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
