package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.controller.RequirementSpecNodeJpaController;
import java.beans.IntrospectionException;
import java.util.Iterator;
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
        //Add Requirements
        for (Iterator<Requirement> it =
                node.getRequirementList().iterator(); it.hasNext();) {
            Requirement req = it.next();
            toPopulate.add(req);
        }
        //Add other nodes
        for (Iterator<RequirementSpecNode> it =
                node.getRequirementSpecNodeList().iterator(); it.hasNext();) {
            RequirementSpecNode req = it.next();
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
                return new UIRequirementNode(req);
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
        RequirementSpecNodeJpaController controller =
                new RequirementSpecNodeJpaController(
                DataBaseManager.getEntityManagerFactory());
        node = controller.findRequirementSpecNode(node.getRequirementSpecNodePK());
    }
}
