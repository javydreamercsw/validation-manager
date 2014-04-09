package net.sourceforge.javydreamercsw.client.ui.components;

import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface ImportMappingInterface {

    /**
     * @return the key
     */
    String getValue();

    /**
     * @return is required
     */
    boolean isRequired();
    
    /**
     * Should this mapping be ignored?
     * @return true if ti should.
     */
    boolean isIgnored();
    
    /**
     * Get the value for the provided key
     * @param key provided key
     * @return value for the provided key or null if non found.
     */
    ImportMappingInterface getMappingValue(String key);

    /**
     * Return yourself to expose the values
     * @return 
     */
    public List<ImportMappingInterface> getValues();
}
