/*
 * This is the interface content shown in the main screen must implement
 */
package com.validation.manager.core;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public interface IMainContentProvider extends ContentProvider, NotificationProvider {

    /**
     * Should this component be displayed?
     *
     * @return true if meant to be displayed, false otherwise.
     */
    boolean shouldDisplay();
}
