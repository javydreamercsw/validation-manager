package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.controller.TestProjectJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TestProjectServer extends TestProject implements EntityServer {

    public TestProjectServer(String name, boolean active) {
        super(name, active);
        setId(0);
    }

    @Override
    public int write2DB() throws IllegalOrphanException, NonexistentEntityException, Exception {
        if (getId() > 0) {
            TestProject tp = new TestProjectJpaController(DataBaseManager.getEntityManagerFactory()).findTestProject(getId());
            tp.setActive(getActive());
            tp.setName(getName());
            tp.setNotes(getNotes());
            new TestProjectJpaController(DataBaseManager.getEntityManagerFactory()).edit(tp);
        } else {
            TestProject tp = new TestProject(getName(), getActive());
            new TestProjectJpaController(DataBaseManager.getEntityManagerFactory()).create(tp);
            setId(tp.getId());
        }
        return getId();
    }

    public TestProject getEntity() {
        return new TestProjectJpaController(
                DataBaseManager.getEntityManagerFactory())
                .findTestProject(getId());
    }
}
