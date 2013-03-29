package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.TestProject;
import java.beans.IntrospectionException;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
class UITestProjectNode extends AbstractRefreshableBeanNode {

    public UITestProjectNode(TestProject tp) throws IntrospectionException {
        super(tp,
                new TestProjectChildFactory(tp), new InstanceContent());
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Folder.png");
    }

    @Override
    public String getName() {
        return getLookup().lookup(TestProject.class).getName();
    }
}
