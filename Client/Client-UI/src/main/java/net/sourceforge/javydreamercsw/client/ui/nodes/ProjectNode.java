package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.Project;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import net.sourceforge.javydreamercsw.client.ui.nodes.actions.CreateProjectAction;
import net.sourceforge.javydreamercsw.client.ui.nodes.actions.CreateRequirementSpecAction;
import net.sourceforge.javydreamercsw.client.ui.nodes.actions.CreateTestProjectAction;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ProjectNode extends AbstractRefreshableBeanNode {

    public ProjectNode(Project project) throws IntrospectionException {
        super(project, new SubProjectChildFactory(project), new InstanceContent());
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Folder.png");
    }

    @Override
    public String getName() {
        return getLookup().lookup(Project.class).getName();
    }

    @Override
    public Action[] getActions(boolean b) {
        List<Action> actions = new ArrayList<Action>();
        actions.addAll(Arrays.asList(super.getActions(b)));
        actions.add(new CreateProjectAction());
        actions.add(new CreateRequirementSpecAction());
        actions.add(new CreateTestProjectAction());
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public void refreshMyself() {
//        ProjectServer rs = new ProjectServer(getLookup().lookup(Project.class));
//        rs.update((Project) getBean(), rs.getEntity());
    }
}
