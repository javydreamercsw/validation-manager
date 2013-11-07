package com.validation.manager.core.api.report;

import java.awt.Component;
import java.util.Map;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface ReportParameterInterface {

    /**
     * Set parameter dialog visible.
     *
     * @param visible
     */
    public void setVisible(boolean visible);

    /**
     * Check if the dialog is visible.
     *
     * @return true if visible.
     */
    public boolean isVisible();

    /**
     * Get the updated parameters.
     *
     * @return Map with the parameters
     */
    public Map getParameters();

    /**
     * Set the default parameters/values
     *
     * @param parameters the parameters to set
     */
    public void setParameters(Map parameters);

    /**
     * Set it relative to a component.
     *
     * @param c
     */
    public void setLocationRelativeTo(Component c);
}
