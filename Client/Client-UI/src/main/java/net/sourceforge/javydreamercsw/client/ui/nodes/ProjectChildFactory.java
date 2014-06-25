package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.controller.ProjectJpaController;
import java.util.List;
import net.sourceforge.javydreamercsw.client.ui.components.database.DataBaseTool;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ProjectChildFactory extends AbstractChildFactory {

    @Override
    protected boolean createKeys(List<Object> list) {
        if (DataBaseTool.getEmf() != null) {
            List<Project> projects = new ProjectJpaController(DataBaseManager.getEntityManagerFactory()).findProjectEntities();
            for (Project p : projects) {
                if (p.getParentProjectId() == null) {
                    list.add(p);
                }
            }
        }
        return true;
    }

    @Override
    protected void updateBean() {
        //Nothing to do, createKeys already does.
    }
}
