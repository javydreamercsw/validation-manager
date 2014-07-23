/*
 * Based on code from: https://netbeans.org/projects/platform/lists/dev/archive/2013-09/message/90
 */
package net.sourceforge.javydreamercsw.client.ui.nodes;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import net.sourceforge.javydreamercsw.client.ui.nodes.capability.NodeExpansion;
import org.openide.nodes.Node;
import org.openide.explorer.view.Visualizer;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class NodeExpansionListener implements TreeExpansionListener {

    @Override
    public void treeExpanded(TreeExpansionEvent event) {
        Node node = getNode(event);
        if (node != null) {
            for (NodeExpansion ne : node.getLookup().lookupAll(NodeExpansion.class)) {
                ne.nodeExpanded();
            }
        }
    }

    @Override
    public void treeCollapsed(TreeExpansionEvent event) {
        Node node = getNode(event);
        if (node != null) {
            for (NodeExpansion ne : node.getLookup().lookupAll(NodeExpansion.class)) {
                ne.nodeCollapsed();
            }
        }
    }

    private static Node getNode(TreeExpansionEvent treeExpansionEvent) {
        try {
            return Visualizer.findNode(treeExpansionEvent.getPath().getLastPathComponent());
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
