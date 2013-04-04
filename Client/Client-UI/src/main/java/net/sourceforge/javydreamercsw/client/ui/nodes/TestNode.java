package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.Test;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import net.sourceforge.javydreamercsw.client.ui.nodes.actions.CreateTestCaseAction;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
class TestNode extends AbstractRefreshableBeanNode {

    public TestNode(Test test) throws IntrospectionException {
        super(test,
                new TestChildFactory(test), new InstanceContent());
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Text-Edit.png");
    }

    @Override
    public String getName() {
        return getLookup().lookup(Test.class).getName();
    }
    
    @Override
    public Action[] getActions(boolean b) {
        List<Action> actions = new ArrayList<Action>();
        actions.addAll(Arrays.asList(super.getActions(b)));
        actions.add(new CreateTestCaseAction());
        return actions.toArray(new Action[actions.size()]);
    }
}
