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

    private AbstractChildFactory factory;

    public AbstractRefreshableNode(Object bean, AbstractChildFactory factory, Lookup lkp) throws IntrospectionException {
        super(bean, factory == null ? Children.LEAF : Children.create(factory, true), lkp);
        this.factory = factory;
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
