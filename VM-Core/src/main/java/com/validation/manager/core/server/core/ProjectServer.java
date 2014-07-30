package com.validation.manager.core.server.core;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import static com.validation.manager.core.DataBaseManager.isVersioningEnabled;
import static com.validation.manager.core.DataBaseManager.namedQuery;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.VMException;
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
import static java.util.logging.Logger.getLogger;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class ProjectServer extends Project
        implements EntityServer<Project>, VersionableServer<Project> {

    public ProjectServer(String name, String notes) {
        super(name);
        setNotes(notes);
        setId(0);
        setProjectList(new ArrayList<>());
        setRequirementSpecList(new ArrayList<>());
        setTestProjectList(new ArrayList<>());
    }

    public ProjectServer(int id) {
        Project product = new ProjectJpaController(
                getEntityManagerFactory()).findProject(id);
        update((ProjectServer) this, product);
    }

    public ProjectServer(Project p) {
        Project product = new ProjectJpaController(
                getEntityManagerFactory()).findProject(p.getId());
        update((ProjectServer) this, product);
    }

    @Override
    public int write2DB() throws IllegalOrphanException, NonexistentEntityException, Exception {
        Project p;
        if (getId() > 0) {
            //Check what has changed, if is only relationshipd, don't version
            //Get the one from DB
            if (isVersioningEnabled() && isChangeVersionable()) {
                p = new Project(getName());
                update(p, this, false);
                p.setMajorVersion(getMajorVersion());
                p.setMidVersion(getMidVersion());
                p.setMinorVersion(getMinorVersion() + 1);
                //Store in data base.
                new ProjectJpaController(getEntityManagerFactory()).create(p);
                update(this, p);
            } else {
                p = new ProjectJpaController(getEntityManagerFactory()).findProject(getId());
                update(p, this);
                new ProjectJpaController(getEntityManagerFactory()).edit(p);
            }
        } else {
            p = new Project(getName());
            update(p, this);
            new ProjectJpaController(getEntityManagerFactory()).create(p);
            setId(p.getId());
        }
        return getId();
    }

    public static void deleteProject(Project p) throws VMException {
        if (p.getProjectList().isEmpty()) {
            try {
                if (p.getRequirementSpecList().isEmpty()) {
                    new ProjectJpaController(getEntityManagerFactory()).destroy(p.getId());
                } else {
                    throw new VMException("Unable to delete project with Requirement Specifications!");
                }
            } catch (IllegalOrphanException | NonexistentEntityException ex) {
                getLogger(ProjectServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            throw new VMException("Unable to delete project with children!");
        }
    }

    @Override
    public Project getEntity() {
        return new ProjectJpaController(getEntityManagerFactory()).findProject(getId());
    }

    private void update(Project target, Project source, boolean copyId) {
        target.setNotes(source.getNotes());
        target.setName(source.getName());
        target.setParentProjectId(source.getParentProjectId());
        target.setProjectList(source.getProjectList());
        target.setRequirementSpecList(source.getRequirementSpecList());
        target.setTestProjectList(source.getTestProjectList());
        if (copyId) {
            target.setId(source.getId());
        }
    }

    @Override
    public void update(Project target, Project source) {
        update(target, source, true);
    }

    public static List<Project> getProjects() {
        return new ProjectJpaController(getEntityManagerFactory()).findProjectEntities();
    }

    public List<Project> getChildren() {
        ArrayList<Project> children = new ArrayList<>();
        for (Project p : getProjects()) {
            if (p.getParentProjectId() != null) {
                if (p.getParentProjectId().getId().equals(getId())) {
                    children.add(p);
                }
            }
        }
        return children;
    }

    public static List<Requirement> getRequirements(Project p) {
        ProjectServer project = new ProjectServer(p);
        List<Requirement> requirements = new ArrayList<>();
        for (RequirementSpec rs : project.getRequirementSpecList()) {
            requirements.addAll(RequirementSpecServer.getRequirements(rs));
        }
        for (Project sp : project.getProjectList()) {
            requirements.addAll(getRequirements(sp));
        }
        return requirements;
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    @Override
    public List<Project> getVersions() {
        List<Project> versions = new ArrayList<>();
        parameters.clear();
        parameters.put("id", getEntity().getId());
        for (Object obj : namedQuery("Project.findById",
                parameters)) {
            versions.add((Project) obj);
        }
        return versions;
    }

    @Override
    public boolean isChangeVersionable() {
        return !getName().equals(getEntity().getName())
                || !getNotes().equals(getEntity().getNotes());
    }

    public void copy(Project newProject) {
        update(this, newProject);
    }
}
