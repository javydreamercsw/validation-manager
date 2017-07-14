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

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Window;
import java.io.File;
import net.sourceforge.javydreamercsw.validation.manager.web.component.VMWindow;
import org.apache.commons.io.FilenameUtils;
import org.openide.util.lookup.ServiceProvider;
import pl.pdfviewer.PdfViewer;

/**
 * Display a PDF file. Based on code from:
 * http://jcraane.blogspot.co.uk/2010/09/printing-in-vaadin.html
 *
 * This class creates a PDF with the iText library. This class implements the
 * StreamSource interface which defines the getStream method.
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = IFileDisplay.class)
public class PDFDisplay extends AbstractFileDisplay {

    @Override
    public boolean supportFile(String name) {
        return FilenameUtils.getExtension(name).equals("pdf");
    }

    @Override
    public Window getViewer(File f) {
        VMWindow w = new VMWindow(f.getName());
        PdfViewer pdfViewer = new PdfViewer(f);
        pdfViewer.setSizeFull();
        w.setContent(pdfViewer);
        w.setHeight(80, Sizeable.Unit.PERCENTAGE);
        w.setWidth(80, Sizeable.Unit.PERCENTAGE);
        return w;
    }
}
