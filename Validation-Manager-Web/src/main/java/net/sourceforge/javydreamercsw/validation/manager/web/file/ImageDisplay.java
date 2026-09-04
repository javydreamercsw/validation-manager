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
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.StreamResource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.javydreamercsw.validation.manager.web.component.VMWindow;
import org.apache.commons.io.FilenameUtils;
import org.openide.util.lookup.ServiceProvider;

/**
 * TODO: (phase-4b-2) the ImageViewer add-on has no Flow port; the image set is
 * displayed as a horizontal strip of Image components (clicking one updates
 * the dialog title) instead of the old paged carousel.
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = IFileDisplay.class)
public class ImageDisplay extends AbstractFileDisplay {

    private final List<File> images;
    private final List<String> validExtensions;

    public ImageDisplay() {
        images = new ArrayList<>();
        validExtensions = new ArrayList<>();
        validExtensions.add("png");
        validExtensions.add("jpg");
        validExtensions.add("jpeg");
        validExtensions.add("bmp");
        validExtensions.add("tif");
        validExtensions.add("tiff");
        validExtensions.add("gif");
    }

    @Override
    public boolean supportFile(String name) {
        return validExtensions.contains(FilenameUtils.getExtension(name));
    }

    public void setImages(List<File> files) {
        images.clear();
        images.addAll(files);
    }

    @Override
    public Component getViewer(File f) {
        VMWindow w = new VMWindow(f.getName());
        if (!images.contains(f)) {
            images.add(f);
        }
        HorizontalLayout strip = new HorizontalLayout();
        strip.setSizeFull();
        for (File img : images) {
            Image image = new Image(toResource(img), img.getName());
            image.setHeight(60, Unit.PERCENTAGE);
            image.addClickListener(e -> w.setHeaderTitle(img.getName()));
            strip.add(image);
        }
        w.add(strip);
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
