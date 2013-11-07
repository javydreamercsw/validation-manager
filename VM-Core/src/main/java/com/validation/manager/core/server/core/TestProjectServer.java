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
public class TestProjectServer extends TestProject
        implements EntityServer<TestProject> {

    public TestProjectServer(String name, boolean active) {
        super(name, active);
        setId(0);
    }
    
    public TestProjectServer(TestProject tp){
        update(this, tp);
    }

    @Override
    public int write2DB() throws IllegalOrphanException, NonexistentEntityException, Exception {
        TestProject tp;
        if (getId() > 0) {
            tp = new TestProjectJpaController(DataBaseManager.getEntityManagerFactory()).findTestProject(getId());
            update(tp, this);
            new TestProjectJpaController(DataBaseManager.getEntityManagerFactory()).edit(tp);
        } else {
            tp = new TestProject(getName(), getActive());
            update(tp, this);
            new TestProjectJpaController(DataBaseManager.getEntityManagerFactory()).create(tp);
        }
        update(this, tp);
        return getId();
    }

    public TestProject getEntity() {
        return new TestProjectJpaController(
                DataBaseManager.getEntityManagerFactory())
                .findTestProject(getId());
    }

    public void update(TestProject target, TestProject source) {
        target.setActive(source.getActive());
        target.setName(source.getName());
        target.setNotes(source.getNotes());
    }
}
