package net.sourceforge.javydreamercsw.client.ui.components;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface RequirementStatusFilterChangeProvider {

    /**
     * Register a listener.
     *
     * @param listener Listener to register
     */
    void register(RequirementStatusFilterChangeListener listener);
    
    /**
     * Unregister a listener.
     *
     * @param listener Listener to unregister
     */
    void unregister(RequirementStatusFilterChangeListener listener);
}
