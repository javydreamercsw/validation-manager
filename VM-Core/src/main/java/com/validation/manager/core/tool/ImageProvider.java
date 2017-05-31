package com.validation.manager.core.tool;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 * @param <T> Class
 */
public interface ImageProvider<T> {

    /**
     * Provide icons for entities.
     *
     * @param e Entity to get the icon for.
     * @param type Icon type
     * @return Icon or null if none found.
     * @throws java.io.IOException If there's an error retrieving the icon.
     */
    public BufferedImage getIcon(T e, int type) throws IOException;

    /**
     * Is this entity supported
     *
     * @param e Object to get the icon for.
     * @return true if supported
     */
    public boolean supported(Object e);
}
