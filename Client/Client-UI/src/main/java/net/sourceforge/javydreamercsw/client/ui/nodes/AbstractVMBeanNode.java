package net.sourceforge.javydreamercsw.client.ui.nodes;

import java.beans.IntrospectionException;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import net.sourceforge.javydreamercsw.client.ui.components.project.explorer.IProjectExplorer;
import net.sourceforge.javydreamercsw.client.ui.nodes.actions.RefreshAction;
import net.sourceforge.javydreamercsw.client.ui.nodes.capability.NodeExpansion;
import net.sourceforge.javydreamercsw.client.ui.nodes.capability.RefreshableCapability;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class AbstractVMBeanNode extends BeanNode
        implements RefreshableCapability {

    private AbstractChildFactory factory;
    private static final Logger LOG
            = Logger.getLogger(AbstractVMBeanNode.class.getSimpleName());

    public AbstractVMBeanNode(Object bean,
            AbstractChildFactory factory, InstanceContent content) throws IntrospectionException {
        super(bean, factory == null ? Children.LEAF
                : Children.create(factory, true), new AbstractLookup(content));
        this.factory = factory;
        //Add abilities
        content.add(new RefreshableCapability() {
            @Override
            public void refresh() {
                AbstractVMBeanNode.this.refresh();
            }

            @Override
            public Lookup createAdditionalLookup(Lookup lkp) {
                return Lookup.EMPTY;
            }
        });
        content.add(new NodeExpansion() {

            @Override
            public void nodeExpanded() {
                LOG.log(Level.FINE, "{0} node expanded!", getName());
                Set<TopComponent> opened = 
                        WindowManager.getDefault().getRegistry().getOpened();
                for (TopComponent tc : opened) {
                    if (tc instanceof IProjectExplorer) {
                        try {
                            IProjectExplorer ipe = (IProjectExplorer) tc;
                            ipe.getExplorerManager().setSelectedNodes(
                                    new Node[]{AbstractVMBeanNode.this});
                        } catch (PropertyVetoException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            }

            @Override
            public void nodeCollapsed() {
                LOG.log(Level.FINE, "{0} node collapsed!", getName());
            }
        });
        content.add(bean);
    }

    @Override
    public void refresh() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //Refresh children
                if (factory != null) {
                    factory.refresh();
                }
                //Refresh bean
                refreshMyself();
            }
        });
    }

    /**
     * Refresh the node itself.
     */
    public abstract void refreshMyself();

    @Override
    public Action[] getActions(boolean context) {
        List<Action> actions = new ArrayList<>();
        //Add refresh action if node has capability
        if (!getLookup().lookupAll(RefreshableCapability.class).isEmpty()) {
            actions.add(new RefreshAction(this));
        }
        return actions.size() > 0
                ? actions.toArray(new Action[actions.size()]) : new Action[]{};
    }

    @Override
    public Lookup createAdditionalLookup(Lookup lkp) {
        return Lookup.EMPTY;
    }
}
