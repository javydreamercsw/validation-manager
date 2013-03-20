package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.Project;
import java.beans.IntrospectionException;
import org.openide.nodes.BeanNode;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ProjectNode extends BeanNode{
    public ProjectNode(Project project) throws IntrospectionException{
        super(project,
                null/*Children.create(new SubProjectChildFactory(project), true)*/,
                Lookups.singleton(project));
    }

    @Override
    public String getName() {
        return getLookup().lookup(Project.class).getName();
    }
}