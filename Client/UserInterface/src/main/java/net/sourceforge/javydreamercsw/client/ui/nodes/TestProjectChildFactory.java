package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.controller.TestProjectJpaController;
import java.beans.IntrospectionException;
import java.util.Iterator;
import java.util.List;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
class TestProjectChildFactory extends AbstractChildFactory {

    private TestProject tp;

    public TestProjectChildFactory(TestProject tp) {
        this.tp = tp;
    }

    @Override
    protected boolean createKeys(List<Object> toPopulate) {
        //Add Requirements
        for (Iterator<TestPlan> it =
                tp.getTestPlanList().iterator(); it.hasNext();) {
            TestPlan req = it.next();
            toPopulate.add(req);
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
            if (key instanceof TestPlan) {
                TestPlan plan = (TestPlan) key;
                return new TestPlanNode(plan);
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
        TestProjectJpaController controller =
                new TestProjectJpaController(DataBaseManager.getEntityManagerFactory());
        tp = controller.findTestProject(tp.getId());
    }
}
