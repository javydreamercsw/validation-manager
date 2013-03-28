package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.Requirement;
import java.beans.IntrospectionException;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class UIRequirementNode extends AbstractRefreshableNode {

    public UIRequirementNode(Requirement req) throws IntrospectionException {
        super(req,
                null,
                Lookups.singleton(req));
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Document.png");
    }

    @Override
    public String getName() {
        return getLookup().lookup(Requirement.class).getUniqueId();
    }
}
