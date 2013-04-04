package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
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
        Project project = new ProjectJpaController(
                DataBaseManager.getEntityManagerFactory()).findProject(p.getId());
        setNotes(project.getNotes());
        setName(project.getName());
        setId(project.getId());
        setProjectList(project.getProjectList());
        setRequirementSpecList(project.getRequirementSpecList());
        setTestProjectList(project.getTestProjectList());
    }
    
    @Override
    public int write2DB() throws IllegalOrphanException, NonexistentEntityException, Exception {
        Project p;
        if (getId() > 0) {
            p = new ProjectJpaController(DataBaseManager.getEntityManagerFactory()).findProject(getId());
            p.setNotes(getNotes());
            p.setName(getName());
            p.setParentProjectId(getParentProjectId());
            p.setProjectList(getProjectList());
            p.setRequirementSpecList(getRequirementSpecList());
            p.setTestProjectList(getTestProjectList());
            new ProjectJpaController(DataBaseManager.getEntityManagerFactory()).edit(p);
        } else {
            p = new Project(getName());
            p.setNotes(getNotes());
            p.setParentProjectId(getParentProjectId());
            p.setProjectList(getProjectList());
            p.setRequirementSpecList(getRequirementSpecList());
            p.setTestProjectList(getTestProjectList());
            new ProjectJpaController(DataBaseManager.getEntityManagerFactory()).create(p);
            setId(p.getId());
        }
        setProjectList(p.getProjectList());
        setRequirementSpecList(p.getRequirementSpecList());
        setTestProjectList(p.getTestProjectList());
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
