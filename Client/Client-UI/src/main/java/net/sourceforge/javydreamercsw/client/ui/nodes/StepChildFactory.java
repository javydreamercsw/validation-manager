package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.controller.StepJpaController;
import java.beans.IntrospectionException;
import java.util.ArrayList;
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
        List<Requirement> toAdd = new ArrayList<>();
        for (Requirement r : step.getRequirementList()) {
            //Handle different versions of same requirement, just show the latest one.
            if (toAdd.isEmpty()) {
                //Nothing there, just add it
                toAdd.add(r);
            } else {
                for (Requirement in : toAdd) {
                    if (r.getUniqueId().equals(in.getUniqueId())) {
                        //They have the same Unique ID, so they are versions of the same requirement
                        if (in.compareTo(r) < 0) {
                            //The one in is older. Remove it and replace with the new one
                            toAdd.remove(in);
                            toAdd.add(r);
                        } else {
                            //The one in is either the same or greater, just keep it
                        }
                    }
                }
            }
        }
        toPopulate.addAll(toAdd);
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
