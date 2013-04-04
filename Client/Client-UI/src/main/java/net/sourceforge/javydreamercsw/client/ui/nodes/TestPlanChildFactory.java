package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Test;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestPlanHasTest;
import com.validation.manager.core.db.controller.TestPlanJpaController;
import java.beans.IntrospectionException;
import java.util.Iterator;
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
        for (Iterator<TestPlanHasTest> it =
                tp.getTestPlanHasTestList().iterator(); it.hasNext();) {
            TestPlanHasTest tpht = it.next();
            toPopulate.add(tpht.getTest());
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
            if (key instanceof Test) {
                Test test = (Test) key;
                return new TestNode(test);
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
        TestPlanJpaController controller =
                new TestPlanJpaController(DataBaseManager.getEntityManagerFactory());
        tp = controller.findTestPlan(tp.getTestPlanPK());
    }
}
