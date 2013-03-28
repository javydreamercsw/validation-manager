package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.Step;
import java.beans.IntrospectionException;
import java.util.List;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
class StepChildFactory extends AbstractChildFactory {

    private final Step step;

    public StepChildFactory(Step step) {
        this.step = step;
    }

    @Override
    protected boolean createKeys(List<Object> toPopulate) {
        for (Requirement r : step.getRequirementList()) {
            toPopulate.add(r);
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
            } else {
                return null;
            }
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }
}
