package net.sourceforge.javydreamercsw.client.ui.nodes;

import java.beans.IntrospectionException;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import net.sourceforge.javydreamercsw.client.ui.nodes.actions.RefreshAction;
import net.sourceforge.javydreamercsw.client.ui.nodes.capability.RefreshableCapability;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class AbstractRefreshableBeanNode extends BeanNode
        implements RefreshableCapability {

    private AbstractChildFactory factory;

    public AbstractRefreshableBeanNode(Object bean, 
            AbstractChildFactory factory, InstanceContent content) throws IntrospectionException {
        super(bean, factory == null ? Children.LEAF : 
                Children.create(factory, true), new AbstractLookup(content));
        this.factory = factory;
        //Add abilities
        content.add(new RefreshableCapability() {
            @Override
            public void refresh() {
                AbstractRefreshableBeanNode.this.refresh();
            }

            @Override
            public Lookup createAdditionalLookup(Lookup lkp) {
                return Lookup.EMPTY;
            }
        });
        content.add(bean);
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
        //Add refresh action if node has capability
        return getLookup().lookupAll(RefreshableCapability.class).isEmpty()
                ? new Action[]{} : new Action[]{new RefreshAction(this)};
    }

    @Override
    public Lookup createAdditionalLookup(Lookup lkp) {
        return Lookup.EMPTY;
    }
}
