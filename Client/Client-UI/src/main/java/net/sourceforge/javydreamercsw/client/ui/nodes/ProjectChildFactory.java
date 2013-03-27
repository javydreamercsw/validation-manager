package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Project;
import java.util.Iterator;
import java.util.List;
import net.sourceforge.javydreamercsw.client.ui.DataBaseTool;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ProjectChildFactory extends AbstractChildFactory {

    @Override
    protected boolean createKeys(List<Object> list) {
        if (DataBaseTool.getEmf() != null) {
            List<Object> projects = DataBaseManager.createdQuery(
                    "select p from Project p where p.parentProjectId is null");
            for (Iterator<Object> it = projects.iterator(); it.hasNext();) {
                list.add((Project) it.next());
            }
        }
        return true;
    }
}
