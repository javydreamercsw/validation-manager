package com.validation.manager.core.tool;

import java.net.URL;
import javax.swing.ImageIcon;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class Tool {

    public static ImageIcon createImageIcon(String path, String description) {
        return createImageIcon(path, description, null);
    }

    public static ImageIcon createImageIcon(String path, String description, Class relativeTo) {
        URL imgURL = relativeTo == null ? Tool.class.getResource(path)
                : relativeTo.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            return null;
        }
    }
}
