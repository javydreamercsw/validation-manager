package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.Project;
import java.awt.event.ActionEvent;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ProjectNode extends BeanNode {

    public ProjectNode(Project project) throws IntrospectionException {
        super(project,
                Children.create(new SubProjectChildFactory(project), true),
                Lookups.singleton(project));
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Folder.png");
    }

    @Override
    public String getName() {
        return getLookup().lookup(Project.class).getName();
    }

    @Override
    public Action[] getActions(boolean b) {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        return actions.toArray(new Action[actions.size()]);
    }
}
