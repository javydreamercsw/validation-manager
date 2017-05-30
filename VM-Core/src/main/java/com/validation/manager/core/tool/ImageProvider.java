package com.validation.manager.core.tool;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public interface ImageProvider<T> {

    /**
     * Provide icons for entities.
     *
     * @param e Entity to get the icon for.
     * @param type Icon type
     * @return Icon or null if none found.
     */
    public BufferedImage getIcon(T e, int type) throws IOException;
    
    /**
     * Is this entity supported
     * @param e Object to get the icon for.
     * @return true if supported
     */
    public boolean supported(Object e);
}
