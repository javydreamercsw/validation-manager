package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.Step;
import java.beans.IntrospectionException;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
class StepNode extends BeanNode {

    public StepNode(Step step) throws IntrospectionException {
        super(step,
                Children.create(new StepChildFactory(step), true),
                Lookups.singleton(step));
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Note.png");
    }

    @Override
    public String getName() {
        Step step = getLookup().lookup(Step.class);
        return "Step # " + step.getStepSequence();
    }
}
