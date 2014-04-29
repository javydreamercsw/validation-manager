package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
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

    public TestProjectServer(TestProject tp) {
        update(this, tp);
    }

    @Override
    public int write2DB() throws IllegalOrphanException, NonexistentEntityException, Exception {
        TestProject tp;
        if (getId() > 0) {
            tp = new TestProjectJpaController(getEntityManagerFactory()).findTestProject(getId());
            update(tp, this);
            new TestProjectJpaController(getEntityManagerFactory()).edit(tp);
        } else {
            tp = new TestProject(getName(), getActive());
            update(tp, this);
            new TestProjectJpaController(getEntityManagerFactory()).create(tp);
        }
        update(this, tp);
        return getId();
    }

    @Override
    public TestProject getEntity() {
        return new TestProjectJpaController(
                getEntityManagerFactory())
                .findTestProject(getId());
    }

    @Override
    public void update(TestProject target, TestProject source) {
        target.setActive(source.getActive());
        target.setName(source.getName());
        target.setNotes(source.getNotes());
        target.setId(source.getId());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
