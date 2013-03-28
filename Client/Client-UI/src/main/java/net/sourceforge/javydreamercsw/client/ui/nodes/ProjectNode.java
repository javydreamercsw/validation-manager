package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.Project;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import net.sourceforge.javydreamercsw.client.ui.nodes.actions.CreateProjectAction;
import net.sourceforge.javydreamercsw.client.ui.nodes.actions.CreateRequirementSpecAction;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ProjectNode extends AbstractRefreshableNode {

    public ProjectNode(Project project) throws IntrospectionException {
        super(project,
                null,
                Lookups.singleton(project));
        factory = new SubProjectChildFactory(project);
        setChildren(Children.create(factory, true));
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
        return actions.toArray(new Action[actions.size()]);
    }
}
