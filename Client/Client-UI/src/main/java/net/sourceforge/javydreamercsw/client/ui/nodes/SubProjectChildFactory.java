package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.TestProject;
import java.util.Iterator;
import java.util.List;
import net.sourceforge.javydreamercsw.client.ui.DataBaseTool;

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
            for (Iterator<Project> it = 
                    parent.getProjectList().iterator(); it.hasNext();) {
                Project project = it.next();
                list.add(project);
            }
        }
        for (Iterator<RequirementSpec> it =
                parent.getRequirementSpecList().iterator(); it.hasNext();) {
            RequirementSpec rs = it.next();
            list.add(rs);
        }
        for (Iterator<TestProject> it = 
                parent.getTestProjectList().iterator(); it.hasNext();) {
            TestProject tp = it.next();
            list.add(tp);
        }
        return true;
    }
}
