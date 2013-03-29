package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.Step;
import java.beans.IntrospectionException;
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
}
