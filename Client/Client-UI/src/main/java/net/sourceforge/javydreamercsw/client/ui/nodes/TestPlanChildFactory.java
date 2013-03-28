package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.Test;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestPlanHasTest;
import com.validation.manager.core.db.TestProject;
import java.beans.IntrospectionException;
import java.util.Iterator;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
class TestPlanChildFactory extends ChildFactory<Test> {

    private final TestPlan tp;

    public TestPlanChildFactory(TestPlan tp) {
        this.tp = tp;
    }

    @Override
    protected boolean createKeys(List<Test> toPopulate) {
        for (Iterator<TestPlanHasTest> it = 
                tp.getTestPlanHasTestList().iterator(); it.hasNext();) {
            TestPlanHasTest tpht = it.next();
            toPopulate.add(tpht.getTest());
        }
        return true;
    }
    
    @Override
    protected Node[] createNodesForKey(Test key) {
        return new Node[]{createNodeForKey(key)};
    }

    @Override
    protected Node createNodeForKey(Test key) {
        try {
            return new TestNode(key);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }
}
