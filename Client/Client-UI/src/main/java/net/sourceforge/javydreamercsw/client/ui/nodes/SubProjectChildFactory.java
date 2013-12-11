package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.TestProject;
import java.util.List;
import net.sourceforge.javydreamercsw.client.ui.components.database.DataBaseTool;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class SubProjectChildFactory extends ProjectChildFactory {
    
    private final Project parent;
    
    public SubProjectChildFactory(Project parent) {
        this.parent = parent;
    }
    
    @Override
    protected boolean createKeys(List<Object> list) {
        //Add sub projects
        if (DataBaseTool.getEmf() != null) {
            for (Project project : parent.getProjectList()) {
                list.add(project);
            }
        }
        //Add requirement specs
        for (RequirementSpec rs : parent.getRequirementSpecList()) {
            list.add(rs);
        }
        //Add test projects
        for (TestProject tp : parent.getTestProjectList()) {
            list.add(tp);
        }
        return true;
    }
}
