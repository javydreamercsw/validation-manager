package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.TestCase;
import java.beans.IntrospectionException;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
class TestCaseNode extends BeanNode {

    public TestCaseNode(TestCase tc) throws IntrospectionException {
        super(tc,
                Children.create(new TestCaseChildFactory(tc), true),
                Lookups.singleton(tc));
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Notepad.png");
    }

    @Override
    public String getName() {
        return "Test Case #" + getLookup().lookup(TestCase.class).getTestCasePK().getTestId();
    }
}
