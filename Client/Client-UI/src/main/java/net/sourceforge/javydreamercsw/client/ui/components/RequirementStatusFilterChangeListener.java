package net.sourceforge.javydreamercsw.client.ui.components;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface RequirementStatusFilterChangeListener {

    /**
     * Filtered ids changed
     *
     * @param ids list of id's from database of statuses to filter out.
     */
    void filterChange(Integer[] ids);
}
