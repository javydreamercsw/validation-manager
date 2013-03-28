package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.Test;
import java.beans.IntrospectionException;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
class TestNode extends BeanNode {

    public TestNode(Test test) throws IntrospectionException {
        super(test,
                Children.create(new TestChildFactory(test), true),
                Lookups.singleton(test));
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Text-Edit.png");
    }

    @Override
    public String getName() {
        return getLookup().lookup(Test.class).getName();
    }
}