package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.Project;
import java.beans.IntrospectionException;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ProjectRequirementsNode extends AbstractRefreshableNode{
     public ProjectRequirementsNode(Project parent) throws IntrospectionException {
        super(parent,
                new RequirementNodeChildFactory(parent),
                Lookups.singleton(parent));
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Contacts-alt.png");
    }

    @Override
    public String getName() {
        return "Requirements";
    }
}
