package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.Project;
import com.validation.manager.core.server.core.ProjectServer;
import java.beans.IntrospectionException;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ProjectRequirementsNode extends AbstractRefreshableBeanNode {

    public ProjectRequirementsNode(Project parent) throws IntrospectionException {
        super(parent,
                new RequirementNodeChildFactory(parent), new InstanceContent());
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Contacts-alt.png");
    }

    @Override
    public String getName() {
        return "Requirements";
    }

    @Override
    public void refreshMyself() {
        ProjectServer rs = new ProjectServer(getLookup().lookup(Project.class));
        rs.update((Project) getBean(), rs.getEntity());
    }
}
