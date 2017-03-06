package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.controller.TestPlanJpaController;
import java.beans.IntrospectionException;
import java.util.List;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
class TestPlanChildFactory extends AbstractChildFactory {

    private TestPlan tp;

    public TestPlanChildFactory(TestPlan tp) {
        this.tp = tp;
    }

    @Override
    protected boolean createKeys(List<Object> toPopulate) {
        tp.getTestCaseList().forEach((tc) -> {
            toPopulate.add(tc);
        });
        return true;
    }

    @Override
    protected Node[] createNodesForKey(Object key) {
        return new Node[]{createNodeForKey(key)};
    }

    @Override
    protected Node createNodeForKey(Object key) {
        try {
            if (key instanceof TestCase) {
                TestCase test = (TestCase) key;
                return new TestCaseNode(test);
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
        TestPlanJpaController controller
                = new TestPlanJpaController(DataBaseManager
                        .getEntityManagerFactory());
        tp = controller.findTestPlan(tp.getTestPlanPK());
    }
}
