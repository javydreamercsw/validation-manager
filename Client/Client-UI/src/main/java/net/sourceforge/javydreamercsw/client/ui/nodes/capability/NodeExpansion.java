/*
 * Based on code from: https://netbeans.org/projects/platform/lists/dev/archive/2013-09/message/90
 */
package net.sourceforge.javydreamercsw.client.ui.nodes.capability;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface NodeExpansion {

    /**
     * Node expanded.
     */
    void nodeExpanded();

    /**
     * Node collapsed.
     */
    void nodeCollapsed();
}
