package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Project;
import java.util.Iterator;
import java.util.List;
import org.openide.nodes.ChildFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ProjectChildFactory extends ChildFactory<Project>{

    @Override
    protected boolean createKeys(List<Project> list) {
        list.clear();
        List<Object> projects = DataBaseManager.createdQuery(
                "select p from Project p where parentProjectId=null");
        for (Iterator<Object> it = projects.iterator(); it.hasNext();) {
            list.add((Project) it.next());
        }
        return true;
    }
}
