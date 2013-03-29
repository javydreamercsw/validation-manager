package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.Requirement;
import java.beans.IntrospectionException;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class UIRequirementNode extends AbstractRefreshableBeanNode {

    public UIRequirementNode(Requirement req) throws IntrospectionException {
        super(req,
                null, new InstanceContent());
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Document.png");
    }

    @Override
    public String getName() {
        return getLookup().lookup(Requirement.class).getUniqueId();
    }
}
