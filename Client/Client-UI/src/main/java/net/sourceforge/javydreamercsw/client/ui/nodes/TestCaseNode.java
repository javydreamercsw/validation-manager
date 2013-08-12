package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.server.core.TestCaseServer;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import net.sourceforge.javydreamercsw.client.ui.nodes.actions.CreateTestStepAction;
import net.sourceforge.javydreamercsw.client.ui.nodes.actions.ImportTestStepAction;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
class TestCaseNode extends AbstractRefreshableBeanNode {

    public TestCaseNode(TestCase tc) throws IntrospectionException {
        super(tc,
                new TestCaseChildFactory(tc), new InstanceContent());
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Notepad.png");
    }

    @Override
    public String getName() {
        return getLookup().lookup(TestCase.class).getName();
    }

    @Override
    public Action[] getActions(boolean b) {
        List<Action> actions = new ArrayList<Action>();
        actions.addAll(Arrays.asList(super.getActions(b)));
        actions.add(new CreateTestStepAction());
        actions.add(new ImportTestStepAction());
        //TODO: actions.add(new DeleteTestStepAction());
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public void refreshMyself() {
        TestCaseServer rs =
                new TestCaseServer(getLookup().lookup(TestCase.class).getTestCasePK());
        rs.update((TestCase) getBean(), rs.getEntity());
    }
}
