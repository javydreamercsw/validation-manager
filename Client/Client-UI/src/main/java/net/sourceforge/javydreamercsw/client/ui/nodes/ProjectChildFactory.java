package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Project;
import java.beans.IntrospectionException;
import java.util.Iterator;
import java.util.List;
import net.sourceforge.javydreamercsw.client.ui.DataBaseTool;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ProjectChildFactory extends ChildFactory<Project> {

    @Override
    protected boolean createKeys(List<Project> list) {
        //Connect to database
        DataBaseTool.connect();
        list.clear();
        List<Object> projects = DataBaseManager.createdQuery(
                "select p from Project p where p.parentProjectId is null");
        for (Iterator<Object> it = projects.iterator(); it.hasNext();) {
            list.add((Project) it.next());
        }
        return true;
    }

    @Override
    protected Node[] createNodesForKey(Project key) {
        return new Node[]{createNodeForKey(key)};
    }

    @Override
    protected Node createNodeForKey(Project key) {
        try {
            return new ProjectNode(key);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }
}
