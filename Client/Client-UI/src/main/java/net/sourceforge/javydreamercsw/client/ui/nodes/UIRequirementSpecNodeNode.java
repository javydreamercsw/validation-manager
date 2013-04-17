package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.RequirementSpecNode;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import net.sourceforge.javydreamercsw.client.ui.nodes.actions.ImportRequirementAction;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class UIRequirementSpecNodeNode extends AbstractRefreshableBeanNode {

    public UIRequirementSpecNodeNode(RequirementSpecNode node) throws IntrospectionException {
        super(node,
                new RequirementChildFactory(node), new InstanceContent());
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Contacts-alt.png");
    }

    @Override
    public String getName() {
        return getLookup().lookup(RequirementSpecNode.class).getName();
    }

    @Override
    public Action[] getActions(boolean context) {
        List<Action> actions = new ArrayList<Action>();
        actions.addAll(Arrays.asList(super.getActions(context)));
//        actions.add(new CreateRequirementAction());
        actions.add(new ImportRequirementAction());
        return actions.toArray(new Action[actions.size()]);
    }
}
