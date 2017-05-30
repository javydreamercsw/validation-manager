package com.validation.manager.core;

import com.vaadin.ui.Component;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public interface ContentProvider {

    /**
     * Return the component caption. This will be attempted to be translated.
     *
     * @return component caption
     */
    String getComponentCaption();

    /**
     * The Content of the provider
     *
     * @return content.
     */
    Component getContent();

    /**
     * This is the unique id of the provider
     *
     * @return unique id for the provider.
     */
    String getId();

    /**
     * Refresh the contents.
     */
    void update();
}
