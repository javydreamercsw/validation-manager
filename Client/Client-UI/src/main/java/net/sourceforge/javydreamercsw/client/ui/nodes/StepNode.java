package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.Step;
import com.validation.manager.core.server.core.StepServer;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import net.sourceforge.javydreamercsw.client.ui.nodes.actions.EditTestStepAction;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
class StepNode extends AbstractRefreshableBeanNode {

    public StepNode(Step step) throws IntrospectionException {
        super(step,
                new StepChildFactory(step), new InstanceContent());
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Note.png");
    }

    @Override
    public String getName() {
        Step step = getLookup().lookup(Step.class);
        return "Step # " + step.getStepSequence();
    }
    
    @Override
    public Action[] getActions(boolean b) {
        List<Action> actions = new ArrayList<Action>();
        actions.addAll(Arrays.asList(super.getActions(b)));
        actions.add(new EditTestStepAction());
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public void refreshMyself() {
//        StepServer rs = new StepServer(getLookup().lookup(Step.class));
//        rs.update((Step) getBean(), rs.getEntity());
    }
}
