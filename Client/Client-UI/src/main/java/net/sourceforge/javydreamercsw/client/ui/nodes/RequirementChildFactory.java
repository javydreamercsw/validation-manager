package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.controller.RequirementSpecNodeJpaController;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.sourceforge.javydreamercsw.client.ui.components.RequirementStatusFilterChangeListener;
import net.sourceforge.javydreamercsw.client.ui.components.RequirementStatusFilterChangeProvider;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementChildFactory extends AbstractChildFactory
        implements RequirementStatusFilterChangeListener {

    private RequirementSpecNode node;
    private Integer[] ids = new Integer[0];

    public RequirementChildFactory(RequirementSpecNode node) {
        this.node = node;
        for (RequirementStatusFilterChangeProvider provider
                : Lookup.getDefault().lookupAll(RequirementStatusFilterChangeProvider.class)) {
            provider.register((RequirementChildFactory) this);
        }
    }

    public RequirementChildFactory() {
        //To comply with ServiceProvider
    }

    @Override
    protected boolean createKeys(List<Object> toPopulate) {
        for (Requirement req : node.getRequirementList()) {
            //Filter out status ids
            if (!new ArrayList<>(Arrays.asList(ids)).contains(req.getRequirementStatusId().getId())) {
                toPopulate.add(req);
            }
        }
        Collections.sort(toPopulate, new Comparator<Object>() {

            @Override
            public int compare(Object o1, Object o2) {
                //Sort them by unique id
                return ((Requirement) o1).getUniqueId().compareToIgnoreCase(((Requirement) o2).getUniqueId());
            }
        });
        for (RequirementSpecNode req : node.getRequirementSpecNodeList()) {
            toPopulate.add(req);
        }
        return true;
    }

    @Override
    protected Node[] createNodesForKey(Object key) {
        return new Node[]{createNodeForKey(key)};
    }

    @Override
    protected Node createNodeForKey(Object key) {
        try {
            if (key instanceof Requirement) {
                Requirement req = (Requirement) key;
                return new UIRequirementNode(req,
                        new RequirementTestChildFactory(req));
            } else if (key instanceof RequirementSpecNode) {
                RequirementSpecNode rs = (RequirementSpecNode) key;
                return new UIRequirementSpecNodeNode(rs);
            } else {
                return null;
            }
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    @Override
    protected void updateBean() {
        RequirementSpecNodeJpaController controller
                = new RequirementSpecNodeJpaController(
                        DataBaseManager.getEntityManagerFactory());
        node = controller.findRequirementSpecNode(node.getRequirementSpecNodePK());
    }

    @Override
    public void filterChange(Integer[] ids) {
        this.ids = ids;
        refresh();
    }
}
