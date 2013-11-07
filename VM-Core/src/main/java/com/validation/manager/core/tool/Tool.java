package com.validation.manager.core.tool;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
        return imgURL == null ? null : new ImageIcon(imgURL, description);
    }

    public static void removeDuplicates(List list) {
        Set set = new HashSet();
        List newList = new ArrayList();
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (set.add(element)) {
                newList.add(element);
            }
        }
        list.clear();
        list.addAll(newList);
    }
}
