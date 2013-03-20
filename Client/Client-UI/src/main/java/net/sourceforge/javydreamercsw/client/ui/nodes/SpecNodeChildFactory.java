package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.RequirementSpec;
import java.beans.IntrospectionException;
import java.util.Iterator;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class SpecNodeChildFactory extends ChildFactory<com.validation.manager.core.db.RequirementSpecNode> {

    private final RequirementSpec spec;

    public SpecNodeChildFactory(RequirementSpec spec) {
        this.spec = spec;
    }

    @Override
    protected boolean createKeys(List<com.validation.manager.core.db.RequirementSpecNode> toPopulate) {
        for (Iterator<com.validation.manager.core.db.RequirementSpecNode> it =
                spec.getRequirementSpecNodeList().iterator(); it.hasNext();) {
            toPopulate.add(it.next());
        }
        return true;
    }
    
    @Override
    protected Node[] createNodesForKey(com.validation.manager.core.db.RequirementSpecNode key) {
        return new Node[]{createNodeForKey(key)};
        }

    @Override
    protected Node createNodeForKey(com.validation.manager.core.db.RequirementSpecNode key) {
        try {
            return new UIRequirementSpecNodeNode(key);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }
}
