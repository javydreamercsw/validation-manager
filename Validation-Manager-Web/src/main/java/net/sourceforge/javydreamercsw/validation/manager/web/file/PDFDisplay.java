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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.server.StreamResource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import net.sourceforge.javydreamercsw.validation.manager.web.component.VMWindow;
import org.apache.commons.io.FilenameUtils;
import org.openide.util.lookup.ServiceProvider;

/**
 * Display a PDF file. The pdfviewer add-on has no Flow port; browsers render
 * PDFs natively, so the file is streamed into an iframe pointed at a
 * {@link StreamResource}.
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
    public Component getViewer(File f) {
        Dialog w = new VMWindow(f.getName());
        IFrame viewer = new IFrame();
        viewer.setSrc(toResource(f));
        viewer.setSizeFull();
        w.add(viewer);
        w.setWidth("80%");
        w.setHeight("80%");
        return w;
    }

    private StreamResource toResource(File file) {
        return new StreamResource(file.getName(), () -> {
            try {
                return new FileInputStream(file);
            } catch (IOException ex) {
                return new java.io.ByteArrayInputStream(new byte[0]);
            }
        });
    }
}
