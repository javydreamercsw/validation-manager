package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class ProjectServer extends Project implements EntityServer<Project> {

    public ProjectServer(String name, String notes) {
        super(name);
        setNotes(notes);
        setId(0);
        setProjectList(new ArrayList<Project>());
        setRequirementSpecList(new ArrayList<RequirementSpec>());
        setTestProjectList(new ArrayList<TestProject>());
    }

    public ProjectServer(int id) {
        Project product = new ProjectJpaController(
                DataBaseManager.getEntityManagerFactory()).findProject(id);
        update((ProjectServer) this, product);
    }

    public ProjectServer(Project p) {
        Project product = new ProjectJpaController(
                DataBaseManager.getEntityManagerFactory()).findProject(p.getId());
        update((ProjectServer) this, product);
    }

    @Override
    public int write2DB() throws IllegalOrphanException, NonexistentEntityException, Exception {
        Project p;
        if (getId() > 0) {
            p = new ProjectJpaController(DataBaseManager.getEntityManagerFactory()).findProject(getId());
            update(p, this);
            new ProjectJpaController(DataBaseManager.getEntityManagerFactory()).edit(p);
        } else {
            p = new Project(getName());
            update(p, this);
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

    public void update(Project target, Project source) {
        target.setNotes(source.getNotes());
        target.setName(source.getName());
        target.setParentProjectId(source.getParentProjectId());
        target.setProjectList(source.getProjectList());
        target.setRequirementSpecList(source.getRequirementSpecList());
        target.setTestProjectList(source.getTestProjectList());
        target.setId(source.getId());
    }

    public static List<Project> getProjects() {
        return new ProjectJpaController(DataBaseManager.getEntityManagerFactory()).findProjectEntities();
    }

    public List<Project> getChildren() {
        ArrayList<Project> children = new ArrayList<Project>();
        for (Project p : getProjects()) {
            if (p.getParentProjectId().getId().equals(getId())) {
                children.add(p);
            }
        }
        return children;
    }

    public static List<Requirement> getRequirements(Project p) {
        ProjectServer project = new ProjectServer(p);
        List<Requirement> requirements = new ArrayList<Requirement>();
        for (RequirementSpec rs : project.getRequirementSpecList()) {
            requirements.addAll(RequirementSpecServer.getRequirements(rs));
        }
        for (Project sp : project.getProjectList()) {
            requirements.addAll(getRequirements(sp));
        }
        return requirements;
    }

    public void update() {
        update(this, getEntity());
    }
}
