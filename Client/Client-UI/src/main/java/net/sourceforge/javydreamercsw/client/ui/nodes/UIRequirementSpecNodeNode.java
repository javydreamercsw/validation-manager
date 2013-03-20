package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.RequirementSpecNode;
import java.beans.IntrospectionException;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class UIRequirementSpecNodeNode extends BeanNode {

    public UIRequirementSpecNodeNode(RequirementSpecNode node) throws IntrospectionException {
        super(node,
                Children.create(new RequirementChildFactory(node), true),
                Lookups.singleton(node));
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Contacts-alt.png");
    }

    @Override
    public String getName() {
        return getLookup().lookup(RequirementSpecNode.class).getName();
    }
}
