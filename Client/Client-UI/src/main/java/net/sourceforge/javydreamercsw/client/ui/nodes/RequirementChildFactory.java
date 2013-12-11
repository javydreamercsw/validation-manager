package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.controller.RequirementSpecNodeJpaController;
import java.beans.IntrospectionException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementChildFactory extends AbstractChildFactory {

    private RequirementSpecNode node;

    public RequirementChildFactory(RequirementSpecNode node) {
        this.node = node;
    }

    @Override
    protected boolean createKeys(List<Object> toPopulate) {
        for (Requirement req : node.getRequirementList()) {
            toPopulate.add(req);
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
}
