package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.RequirementSpec;
import java.beans.IntrospectionException;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class UIRequirementSpecNode extends AbstractRefreshableNode {

    public UIRequirementSpecNode(RequirementSpec spec) throws IntrospectionException {
        super(spec,
                new SpecNodeChildFactory(spec),
                Lookups.singleton(spec));
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Contacts-alt.png");
    }

    @Override
    public String getName() {
        return getLookup().lookup(RequirementSpec.class).getName();
    }
}
