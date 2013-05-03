package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ProjectServer extends Project implements EntityServer {

    public ProjectServer(String name, String notes) {
        super(name);
        setNotes(notes);
        setId(0);
    }

    public ProjectServer(Project p) {
        Project product = new ProjectJpaController(
                DataBaseManager.getEntityManagerFactory()).findProject(p.getId());
        setNotes(product.getNotes());
        setName(product.getName());
        setId(product.getId());
        setRequirementSpecList(product.getRequirementSpecList());
        setTestProjectList(product.getTestProjectList());
    }

    @Override
    public int write2DB() throws IllegalOrphanException, NonexistentEntityException, Exception {
        if (getId() > 0) {
            Project p = new ProjectJpaController(DataBaseManager.getEntityManagerFactory()).findProject(getId());
            p.setNotes(getNotes());
            p.setName(getName());
            p.setParentProjectId(getParentProjectId());
            p.setProjectList(getProjectList() == null
                    ? new ArrayList<Project>() : getProjectList());
            p.setRequirementSpecList(getRequirementSpecList() == null
                    ? new ArrayList<RequirementSpec>() : getRequirementSpecList());
            p.setTestProjectList(getTestProjectList() == null
                    ? new ArrayList<TestProject>() : getTestProjectList());
            new ProjectJpaController(DataBaseManager.getEntityManagerFactory()).edit(p);
        } else {
            Project p = new Project(getName());
            p.setNotes(getNotes());
            p.setParentProjectId(getParentProjectId());
            p.setProjectList(getProjectList() == null
                    ? new ArrayList<Project>() : getProjectList());
            p.setRequirementSpecList(getRequirementSpecList() == null
                    ? new ArrayList<RequirementSpec>() : getRequirementSpecList());
            p.setTestProjectList(getTestProjectList() == null
                    ? new ArrayList<TestProject>() : getTestProjectList());
            new ProjectJpaController(DataBaseManager.getEntityManagerFactory()).create(p);
            setId(p.getId());
        }
        return getId();
    }

    public static void deleteProject(Project p) {
        try {
            new ProjectJpaController(DataBaseManager.getEntityManagerFactory()).destroy(p.getId());
        } catch (IllegalOrphanException ex) {
            Logger.getLogger(ProjectServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(ProjectServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Project getEntity() {
        return new ProjectJpaController(DataBaseManager.getEntityManagerFactory()).findProject(getId());
    }
}
