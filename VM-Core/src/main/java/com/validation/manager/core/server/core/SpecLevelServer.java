package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.SpecLevel;
import com.validation.manager.core.db.controller.SpecLevelJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class SpecLevelServer extends SpecLevel implements EntityServer<SpecLevel> {

    public SpecLevelServer(String name, String description) {
        super(name, description);
        setId(0);
    }

    @Override
    public int write2DB() throws IllegalOrphanException,
            NonexistentEntityException, Exception {
        if (getId() > 0) {
            SpecLevel sl = new SpecLevelJpaController(
                    DataBaseManager.getEntityManagerFactory()).findSpecLevel(getId());
            update(sl, this);
            new SpecLevelJpaController(
                    DataBaseManager.getEntityManagerFactory()).edit(sl);
        } else {
            SpecLevel sl = new SpecLevel();
            update(sl, this);
            new SpecLevelJpaController(
                    DataBaseManager.getEntityManagerFactory()).create(sl);
            setId(sl.getId());
        }
        return getId();
    }

    public static void deleteSpecLevel(SpecLevel sl)
            throws IllegalOrphanException, NonexistentEntityException {
        new SpecLevelJpaController(
                DataBaseManager.getEntityManagerFactory()).destroy(sl.getId());
    }

    public SpecLevel getEntity() {
        return new SpecLevelJpaController(
                DataBaseManager.getEntityManagerFactory()).findSpecLevel(getId());
    }

    public void update(SpecLevel target, SpecLevel source) {
        target.setDescription(source.getDescription());
        target.setName(source.getName());
        if (source.getRequirementSpecList() != null) {
            target.setRequirementSpecList(source.getRequirementSpecList());
        }
    }
    
    public void update() {
        update(this, getEntity());
    }

    public static List<SpecLevel> getLevels() {
        return new SpecLevelJpaController(DataBaseManager.getEntityManagerFactory()).findSpecLevelEntities();
    }
}
