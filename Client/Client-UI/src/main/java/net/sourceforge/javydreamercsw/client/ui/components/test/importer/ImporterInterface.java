package net.sourceforge.javydreamercsw.client.ui.components.test.importer;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface ImporterInterface {

    /**
     * Display an imported table.
     *
     * @param index table index to display (1-n)
     */
    void displayTable(Integer index);

    /**
     * Enable the UI
     *
     * @param valid true if is to be enabled.
     */
    void enableUI(boolean valid);

}
