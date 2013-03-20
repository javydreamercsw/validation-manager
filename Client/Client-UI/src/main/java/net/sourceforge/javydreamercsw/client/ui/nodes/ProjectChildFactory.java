package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.TestProject;
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
public class ProjectChildFactory extends ChildFactory<Object> {

    @Override
    protected boolean createKeys(List<Object> list) {
        //Connect to database
        DataBaseTool.connect();
        if (DataBaseTool.getEmf() != null) {
            List<Object> projects = DataBaseManager.createdQuery(
                    "select p from Project p where p.parentProjectId is null");
            for (Iterator<Object> it = projects.iterator(); it.hasNext();) {
                list.add((Project) it.next());
            }
        }
        return true;
    }

    @Override
    protected Node[] createNodesForKey(Object key) {
        return new Node[]{createNodeForKey(key)};
    }

    @Override
    protected Node createNodeForKey(Object key) {
        try {
            if (key instanceof Project) {
                Project project = (Project) key;
                return new ProjectNode(project);
            } else if (key instanceof RequirementSpec) {
                RequirementSpec rs = (RequirementSpec) key;
                return new UIRequirementSpecNode(rs);
                //TODO: Enable test projects
//            } else if (key instanceof TestProject) {
//                TestProject tp = (TestProject) key;
//                return null;//new UITestProjectNode(tp);
            } else {
                return null;
            }
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }
}
