package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.controller.RequirementJpaController;
import java.util.List;
import net.sourceforge.javydreamercsw.client.ui.components.database.DataBaseTool;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementTestChildFactory extends AbstractChildFactory {

    private final Requirement req;

    public RequirementTestChildFactory(Requirement req) {
        this.req = req;
    }

    @Override
    protected boolean createKeys(List<Object> list) {
        if (DataBaseTool.getEmf() != null) {
            RequirementJpaController controller
                    = new RequirementJpaController(DataBaseManager.getEntityManagerFactory());
            Requirement requirement
                    = controller.findRequirement(req.getId());
            for (Step s : requirement.getStepList()) {
                list.add(s);
            }
        }
        return true;
    }

    @Override
    protected void updateBean() {
        //Nothing to do, createKeys already does.
    }
}
