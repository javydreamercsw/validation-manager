package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.server.core.TestPlanServer;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import net.sourceforge.javydreamercsw.client.ui.nodes.actions.CreateTestAction;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
class TestPlanNode extends AbstractRefreshableBeanNode {

    public TestPlanNode(TestPlan tp) throws IntrospectionException {
        super(tp,
                new TestPlanChildFactory(tp), new InstanceContent());
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Folder.png");
    }

    @Override
    public String getName() {
        return "Test Plan #" + getLookup().lookup(TestPlan.class).getTestPlanPK().getId();
    }

    @Override
    public Action[] getActions(boolean b) {
        List<Action> actions = new ArrayList<Action>();
        actions.addAll(Arrays.asList(super.getActions(b)));
        actions.add(new CreateTestAction());
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public void refreshMyself() {
//        TestPlanServer rs = new TestPlanServer(getLookup().lookup(TestPlan.class));
//        rs.update((TestPlan) getBean(), rs.getEntity());
    }
}
