package net.sourceforge.javydreamercsw.client.ui.nodes;

import java.beans.IntrospectionException;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import net.sourceforge.javydreamercsw.client.ui.nodes.actions.RefreshAction;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class AbstractRefreshableNode extends BeanNode implements RefreshableNode {

    protected AbstractChildFactory factory;

    public AbstractRefreshableNode(Object bean, Children children, Lookup lkp) throws IntrospectionException {
        super(bean, children, lkp);
    }

    @Override
    public void refresh() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                factory.refresh();
            }
        });
    }

    @Override
    public Action[] getActions(boolean context) {
        //Add refresh action
        return new Action[]{new RefreshAction(this)};
    }
}
