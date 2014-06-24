package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.controller.StepJpaController;
import com.validation.manager.core.server.core.RequirementServer;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
class StepChildFactory extends AbstractChildFactory {

    private Step step;

    public StepChildFactory(Step step) {
        this.step = step;
    }

    @Override
    protected boolean createKeys(List<Object> toPopulate) {
        List<String> processed = new ArrayList<>();
        for (Requirement r : step.getRequirementList()) {
            if (!processed.contains(r.getUniqueId().trim())) {
                processed.add(r.getUniqueId().trim());
                RequirementServer rs = new RequirementServer(r);
                toPopulate.add(Collections.max(rs.getVersions(), null));
            }
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
                return new UIRequirementNode(req, null);
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
        StepJpaController controller
                = new StepJpaController(DataBaseManager.getEntityManagerFactory());
        step = controller.findStep(step.getStepPK());
    }
}
