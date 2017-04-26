/*
 * This is the interface content shown in the main screen must implement
 */
package com.validation.manager.core;

import com.vaadin.ui.Component;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface IMainContentProvider {

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
     * Return the component caption. This will be attempted to be translated.
     *
     * @return component caption
     */
    String getComponentCaption();

    /**
     * Refresh the contents.
     */
    void update();

    /**
     * Should this component be displayed?
     *
     * @return true if meant to be displayed, false otherwise.
     */
    boolean shouldDisplay();

    /**
     * Make any notifications to the user as needed.
     */
    void processNotification();
}
