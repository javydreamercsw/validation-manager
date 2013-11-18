package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.server.core.RequirementServer;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import net.sourceforge.javydreamercsw.client.ui.nodes.actions.EditRequirementAction;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class UIRequirementNode extends AbstractVMBeanNode {

    public UIRequirementNode(Requirement req) throws IntrospectionException {
        super(req,
                null, new InstanceContent());
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Document.png");
        setShortDescription(req.getDescription());
    }

    @Override
    public String getName() {
        return getLookup().lookup(Requirement.class).getUniqueId();
    }

    @Override
    public Action[] getActions(boolean b) {
        List<Action> actions = new ArrayList<>();
        actions.addAll(Arrays.asList(super.getActions(b)));
        actions.add(new EditRequirementAction());
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public void refreshMyself() {
        RequirementServer rs = new RequirementServer(getLookup().lookup(Requirement.class));
        rs.update((Requirement) getBean(), rs.getEntity());
    }
}
