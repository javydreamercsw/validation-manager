package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.server.core.TestProjectServer;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import net.sourceforge.javydreamercsw.client.ui.nodes.actions.CreateTestPlanAction;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
class UITestProjectNode extends AbstractVMBeanNode {

    public UITestProjectNode(TestProject tp) throws IntrospectionException {
        super(tp,
                new TestProjectChildFactory(tp), new InstanceContent());
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Folder.png");
    }

    @Override
    public String getName() {
        return getLookup().lookup(TestProject.class).getName();
    }
    
    @Override
    public Action[] getActions(boolean b) {
        List<Action> actions = new ArrayList<>();
        actions.addAll(Arrays.asList(super.getActions(b)));
        actions.add(new CreateTestPlanAction());
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public void refreshMyself() {
        TestProjectServer rs = new TestProjectServer(getLookup().lookup(TestProject.class));
        rs.update((TestProject) getBean(), rs.getEntity());
    }
}
