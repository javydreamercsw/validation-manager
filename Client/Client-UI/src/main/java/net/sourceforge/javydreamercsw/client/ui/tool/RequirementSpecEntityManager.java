package net.sourceforge.javydreamercsw.client.ui.tool;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.api.entity.manager.VMEntityManager;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.controller.RequirementSpecJpaController;
import com.validation.manager.core.server.core.RequirementSpecServer;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = VMEntityManager.class)
public class RequirementSpecEntityManager implements VMEntityManager<RequirementSpec> {

    private Map<Integer, RequirementSpec> map = new HashMap<>();

    public RequirementSpecEntityManager() {
        //This has no versioning so is safe to intialize
        List<RequirementSpec> specs = new RequirementSpecJpaController(
                DataBaseManager.getEntityManagerFactory())
                .findRequirementSpecEntities();
        for (RequirementSpec spec : specs) {
            map.put(spec.getRequirementSpecPK().getId(), spec);
        }
    }

    @Override
    public boolean supportEntity(Class entity) {
        return entity.isInstance(RequirementSpec.class);
    }

    @Override
    public void updateEntity(RequirementSpec entity) {
        if (map.containsKey(entity.getRequirementSpecPK().getId())) {
            RequirementSpecServer rs = new RequirementSpecServer(entity);
            rs.update();
            map.put(entity.getRequirementSpecPK().getId(), rs.getEntity());
        }
    }

    @Override
    public void removeEntity(RequirementSpec entity) {
        if (map.containsKey(entity.getRequirementSpecPK().getId())) {
            map.remove(entity.getRequirementSpecPK().getId());
        }
    }

    @Override
    public Collection<RequirementSpec> getEntities() {
        return map.values();
    }

    @Override
    public void addEntity(RequirementSpec entity) {
        if (!map.containsKey(entity.getRequirementSpecPK().getId())) {
            RequirementSpecServer rs = new RequirementSpecServer(entity);
            rs.update();
            map.put(entity.getRequirementSpecPK().getId(), rs.getEntity());
        }
    }

    @Override
    public RequirementSpec getEntity(Object entity) {
        assert entity instanceof Integer : "Invalid parameter!";
        return map.get((Integer) entity);
    }
}
