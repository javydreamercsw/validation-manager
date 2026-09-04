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
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
 * The ImageViewer add-on has no Flow port; a paged viewer is rebuilt from
 * core components: a large image with prev/next controls and a clickable
 * thumbnail strip (mirrors the old carousel, including selecting an image by
 * clicking its thumbnail).
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
        VerticalLayout viewer = new VerticalLayout();
        viewer.setSizeFull();
        viewer.setPadding(false);
        viewer.setSpacing(true);
        com.vaadin.flow.component.html.Image main = new com.vaadin.flow.component.html.Image(
                toResource(images.get(0)), images.get(0).getName());
        main.setWidth(100, Unit.PERCENTAGE);
        main.setHeight(70, Unit.PERCENTAGE);
        Button prev = new Button(new Icon(VaadinIcon.ANGLE_LEFT));
        Button next = new Button(new Icon(VaadinIcon.ANGLE_RIGHT));
        prev.addClickListener(e -> {
            int index = images.indexOf(currentFile(main));
            if (index > 0) {
                setMain(main, images.get(index - 1), w);
            }
        });
        next.addClickListener(e -> {
            int index = images.indexOf(currentFile(main));
            if (index >= 0 && index < images.size() - 1) {
                setMain(main, images.get(index + 1), w);
            }
        });
        HorizontalLayout controls = new HorizontalLayout(prev, next);
        controls.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        HorizontalLayout strip = new HorizontalLayout();
        strip.setWidthFull();
        strip.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        for (File img : images) {
            com.vaadin.flow.component.html.Image thumb
                    = new com.vaadin.flow.component.html.Image(toResource(img),
                            img.getName());
            thumb.setHeight(60, Unit.PIXELS);
            thumb.getStyle().set("cursor", "pointer");
            thumb.addClickListener(e -> setMain(main, img, w));
            strip.add(thumb);
        }
        viewer.add(main, controls, strip);
        viewer.expand(main);
        w.add(viewer);
        w.setWidth("80%");
        w.setHeight("80%");
        return w;
    }

    private void setMain(com.vaadin.flow.component.html.Image main, File img,
            VMWindow w) {
        main.setSrc(toResource(img));
        main.setAlt(img.getName());
        w.setHeaderTitle(img.getName());
    }

    private File currentFile(com.vaadin.flow.component.html.Image image) {
        String alt = image.getAlt().orElse(null);
        for (File img : images) {
            if (img.getName().equals(alt)) {
                return img;
            }
        }
        return null;
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
