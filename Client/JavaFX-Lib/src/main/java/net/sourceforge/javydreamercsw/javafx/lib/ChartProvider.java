package net.sourceforge.javydreamercsw.javafx.lib;

import java.util.Map;
import javafx.scene.chart.Chart;
import java.util.Properties;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface ChartProvider<T> {
    /**
     * A map with properties to set and the type of value expected. Useful for
     * displaying properties on the client side.
     * 
     * Properties should follow this format: id, Description.
     * 
     * Where id is a String id unique for the property within the ChartProvider 
     * and Description is the text to be displayed on the client.
     * @return Property Map.
     */
    Map<Properties, Class> getPropertyMap();
    
    /**
     * Get the chart properties.
     * @return Char properties
     */
    Properties getProperties();
    
    /**
     * Set the properties to the chart.
     * @param props 
     */
    void setProperties(Properties props);

    /**
     * Checks if provider has charts for the provided class.
     *
     * @param c Class to look chars for.
     * @return true if supported, false otherwise.
     */
    boolean supports(Class c);

    /**
     * Get chart.
     * @param entity Entity to get chart from.
     * @return Chart.
     */
    Chart getChart(T entity);
    
    /**
     * Some information of the chart provided by this provider.
     * @return Chart name.
     */
    String getName();
    
    /**
     * Some information of the chart provided by this provider.
     * @return Chart description.
     */
    String getDescription();
}
