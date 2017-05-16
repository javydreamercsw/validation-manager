package com.validation.manager.core.api.report;

import java.awt.Component;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface ReportChooserInterface {

    /**
     * Set chooser visible.
     *
     * @param visible
     */
    public void setVisible(boolean visible);

    /**
     * Set it relative to a component.
     *
     * @param c
     */
    public void setLocationRelativeTo(Component c);

    /**
     * Check if the dialog is visible.
     *
     * @return true if visible.
     */
    public boolean isVisible();
}
