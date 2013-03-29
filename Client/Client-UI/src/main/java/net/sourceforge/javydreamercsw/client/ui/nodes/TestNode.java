package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.Test;
import java.beans.IntrospectionException;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
class TestNode extends AbstractRefreshableBeanNode {

    public TestNode(Test test) throws IntrospectionException {
        super(test,
                new TestChildFactory(test), new InstanceContent());
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Text-Edit.png");
    }

    @Override
    public String getName() {
        return getLookup().lookup(Test.class).getName();
    }
}
