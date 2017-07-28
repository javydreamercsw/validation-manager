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

import com.vaadin.server.FileResource;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Window;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.javydreamercsw.validation.manager.web.component.VMWindow;
import org.apache.commons.io.FilenameUtils;
import org.openide.util.lookup.ServiceProvider;
import org.tepi.imageviewer.ImageViewer;
import org.tepi.imageviewer.ImageViewer.ImageSelectionListener;

@ServiceProvider(service = IFileDisplay.class)
public class ImageDisplay extends AbstractFileDisplay
        implements ImageSelectionListener {

    private final ImageViewer imageViewer;
    private final List<FileResource> images;
    private final List<String> validExtensions;
    private Window w;

    public ImageDisplay() {
        this.imageViewer = new ImageViewer();
        images = new ArrayList<>();
        validExtensions = new ArrayList<>();
        validExtensions.add("png");
        validExtensions.add("jpg");
        validExtensions.add("jpeg");
        validExtensions.add("bmp");
        validExtensions.add("tif");
        validExtensions.add("tiff");
        validExtensions.add("gif");
        imageViewer.setSizeFull();
        imageViewer.setAnimationEnabled(false);
        imageViewer.setSideImageRelativeWidth(0.7f);
        imageViewer.addListener(ImageDisplay.this);
    }

    @Override
    public boolean supportFile(String name) {
        return validExtensions.contains(FilenameUtils.getExtension(name));
    }

    public void setImages(List<File> files) {
        images.clear();
        files.forEach((f) -> {
            images.add(new FileResource(f));
        });
    }

    @Override
    public Window getViewer(File f) {
        w = new VMWindow(f.getName());
        boolean found = false;
        for (FileResource fr : images) {
            if (fr.getFilename().equals(f.getName())) {
                found = true;
                break;
            }
        }
        if (!found) {
            images.add(new FileResource(f));
        }
        imageViewer.setImages(images);
        w.setContent(imageViewer);
        w.setHeight(80, Sizeable.Unit.PERCENTAGE);
        w.setWidth(80, Sizeable.Unit.PERCENTAGE);
        imageViewer.setCenterImageIndex(0);
        imageViewer.focus();
        return w;
    }

    @Override
    public void imageSelected(ImageViewer.ImageSelectedEvent e) {
        w.setCaption(images.get(e.getSelectedImageIndex()).getFilename());
    }
}
