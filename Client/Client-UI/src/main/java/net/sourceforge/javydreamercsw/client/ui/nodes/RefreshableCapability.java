package net.sourceforge.javydreamercsw.client.ui.nodes;

import org.netbeans.spi.project.LookupProvider;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface RefreshableCapability extends LookupProvider {

    /**
     * Refresh this node.
     */
    void refresh();
}
