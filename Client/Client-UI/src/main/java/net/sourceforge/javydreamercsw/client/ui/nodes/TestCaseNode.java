package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.TestCase;
import java.beans.IntrospectionException;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
class TestCaseNode extends AbstractRefreshableBeanNode {

    public TestCaseNode(TestCase tc) throws IntrospectionException {
        super(tc,
                new TestCaseChildFactory(tc), new InstanceContent());
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Notepad.png");
    }

    @Override
    public String getName() {
        return "Test Case #" + getLookup().lookup(TestCase.class).getTestCasePK().getId();
    }
}
