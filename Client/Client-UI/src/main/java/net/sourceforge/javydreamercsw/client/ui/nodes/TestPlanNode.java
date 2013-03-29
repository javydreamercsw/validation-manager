package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.TestPlan;
import java.beans.IntrospectionException;
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
}
