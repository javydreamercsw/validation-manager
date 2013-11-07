package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.controller.RequirementSpecJpaController;
import java.beans.IntrospectionException;
import java.util.Iterator;
import java.util.List;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class SpecNodeChildFactory extends AbstractChildFactory {

    private RequirementSpec spec;

    public SpecNodeChildFactory(RequirementSpec spec) {
        this.spec = spec;
    }

    @Override
    protected boolean createKeys(List<Object> toPopulate) {
        for (Iterator<com.validation.manager.core.db.RequirementSpecNode> it
                = spec.getRequirementSpecNodeList().iterator(); it.hasNext();) {
            toPopulate.add(it.next());
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
            if (key instanceof RequirementSpecNode) {
                RequirementSpecNode rsn = (RequirementSpecNode) key;
                return new UIRequirementSpecNodeNode(rsn);
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
        RequirementSpecJpaController controller
                = new RequirementSpecJpaController(
                        DataBaseManager.getEntityManagerFactory());
        spec = controller.findRequirementSpec(spec.getRequirementSpecPK());
    }
}
