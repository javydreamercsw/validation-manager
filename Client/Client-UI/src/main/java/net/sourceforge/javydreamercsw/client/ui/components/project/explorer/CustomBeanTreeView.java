/*
 * Based on code from: https://netbeans.org/projects/platform/lists/dev/archive/2013-09/message/90
 */
package net.sourceforge.javydreamercsw.client.ui.components.project.explorer;

import net.sourceforge.javydreamercsw.client.ui.nodes.NodeExpansionListener;
import org.openide.explorer.view.BeanTreeView;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class CustomBeanTreeView extends BeanTreeView {

    private final NodeExpansionListener nodeExpansionListener
            = new NodeExpansionListener();

    @Override
    public void addNotify() {
        super.addNotify();
        tree.addTreeExpansionListener(nodeExpansionListener);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        tree.removeTreeExpansionListener(nodeExpansionListener);
    }
}
