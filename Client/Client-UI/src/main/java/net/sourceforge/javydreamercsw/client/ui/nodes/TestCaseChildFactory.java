package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.fmea.RiskControl;
import java.beans.IntrospectionException;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
class TestCaseChildFactory extends ChildFactory<Object> {

    private final TestCase tc;

    public TestCaseChildFactory(TestCase tc) {
        this.tc = tc;
    }

    @Override
    protected boolean createKeys(List<Object> toPopulate) {
        for (Step s : tc.getStepList()) {
            toPopulate.add(s);
        }
        for (RiskControl rc : tc.getRiskControlList()) {
            toPopulate.add(rc);
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
            if (key instanceof Step) {
                Step step = (Step) key;
                return new StepNode(step);
            } else if (key instanceof RiskControl) {
                RiskControl rs = (RiskControl) key;
                return new RiskControlNode(rs);
            } else {
                return null;
            }
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }
}
