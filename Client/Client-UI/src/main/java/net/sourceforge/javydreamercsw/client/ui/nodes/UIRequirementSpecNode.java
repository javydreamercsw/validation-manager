package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.server.core.RequirementSpecServer;
import java.beans.IntrospectionException;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class UIRequirementSpecNode extends AbstractRefreshableBeanNode {

    public UIRequirementSpecNode(RequirementSpec spec) throws IntrospectionException {
        super(spec,
                new SpecNodeChildFactory(spec), new InstanceContent());
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Contacts-alt.png");
    }

    @Override
    public String getName() {
        return getLookup().lookup(RequirementSpec.class).getName();
    }

    @Override
    public void refreshMyself() {
        RequirementSpecServer rs = new RequirementSpecServer(getLookup().lookup(RequirementSpec.class));
        rs.update((RequirementSpec) getBean(), rs.getEntity());
    }
}
