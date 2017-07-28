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
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Window;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import net.sourceforge.javydreamercsw.validation.manager.web.component.VMWindow;
import org.apache.commons.io.FilenameUtils;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = IFileDisplay.class)
public class TextDisplay extends AbstractFileDisplay
        implements IFileDisplay {

    @Override
    public boolean supportFile(String name) {
        return FilenameUtils.getExtension(name).equals("txt");
    }

    @Override
    public Window getViewer(File f) {
        BufferedReader br = null;
        Window w = new VMWindow(f.getName());
        w.setHeight(80, Sizeable.Unit.PERCENTAGE);
        w.setWidth(80, Sizeable.Unit.PERCENTAGE);
        //Just a plain panel will do
        TextArea text = new TextArea();
        text.setSizeFull();
        w.setContent(text);
        try {
            br = new BufferedReader(new FileReader(f));
            String line;
            StringBuilder sb = new StringBuilder();
            try {
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                text.setValue(sb.toString());
                text.setReadOnly(true);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return w;
    }
}
