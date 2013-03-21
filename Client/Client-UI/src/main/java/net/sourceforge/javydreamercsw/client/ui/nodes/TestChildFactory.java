package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.Test;
import com.validation.manager.core.db.TestCase;
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
class TestChildFactory extends ChildFactory<TestCase> {

    private final Test test;

    public TestChildFactory(Test test) {
        this.test = test;
    }

    @Override
    protected boolean createKeys(List<TestCase> toPopulate) {
        for (Iterator<TestCase> it =
                test.getTestCaseList().iterator(); it.hasNext();) {
            TestCase tc = it.next();
            toPopulate.add(tc);
        }
        return true;
    }
    
    @Override
    protected Node[] createNodesForKey(TestCase key) {
        return new Node[]{createNodeForKey(key)};
    }

    @Override
    protected Node createNodeForKey(TestCase key) {
        try {
            return new TestCaseNode(key);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }
}
