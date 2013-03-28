package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.Project;
import java.beans.IntrospectionException;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ProjectRequirementsNode extends BeanNode{
     public ProjectRequirementsNode(Project parent) throws IntrospectionException {
        super(parent,
                Children.create(new RequirementNodeChildFactory(parent), true));
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Contacts-alt.png");
    }

    @Override
    public String getName() {
        return "Requirements";
    }
}
