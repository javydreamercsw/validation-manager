package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.server.core.RequirementSpecServer;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class UIRequirementSpecNode extends AbstractVMBeanNode {

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
    public Action[] getActions(boolean b) {
        List<Action> actions = new ArrayList<>();
        actions.addAll(Arrays.asList(super.getActions(b)));
        //TODO: actions.add(new EditRequirementSpecCaseAction());
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public void refreshMyself() {
        RequirementSpecServer rs = new RequirementSpecServer(getLookup().lookup(RequirementSpec.class));
        rs.update((RequirementSpec) getBean(), rs.getEntity());
    }
}
