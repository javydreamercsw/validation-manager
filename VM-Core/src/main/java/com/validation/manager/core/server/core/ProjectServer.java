package com.validation.manager.core.server.core;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.Project;
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

    private static final long serialVersionUID = 3434510483033583117L;

    public ProjectServer(String name, String notes) {
        super(name);
        setNotes(notes);
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
    public int write2DB() throws IllegalOrphanException,
            NonexistentEntityException, Exception {
        Project p;
        if (getId() == null) {
            p = new Project(getName());
            update(p, this);
            new ProjectJpaController(getEntityManagerFactory()).create(p);
            setId(p.getId());
        } else {
            p = getEntity();
            update(p, this);
            new ProjectJpaController(getEntityManagerFactory()).edit(p);
        }
        return getId();
    }

    public static void deleteProject(Project p) throws VMException {
        if (p.getProjectList().isEmpty()) {
            try {
                if (p.getRequirementSpecList().isEmpty()) {
                    new ProjectJpaController(getEntityManagerFactory())
                            .destroy(p.getId());
                } else {
                    throw new VMException("Unable to delete project with Requirement Specifications!");
                }
            }
            catch (IllegalOrphanException | NonexistentEntityException ex) {
                getLogger(ProjectServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            throw new VMException("Unable to delete project with children!");
        }
    }

    @Override
    public Project getEntity() {
        return new ProjectJpaController(getEntityManagerFactory())
                .findProject(getId());
    }

    @Override
    public void update(Project target, Project source) {
        target.setNotes(source.getNotes());
        target.setName(source.getName());
        target.setParentProjectId(source.getParentProjectId());
        target.setProjectList(source.getProjectList());
        target.setRequirementSpecList(source.getRequirementSpecList());
        target.setTestProjectList(source.getTestProjectList());
        target.setId(source.getId());
        super.update(target, source);
    }

    public static List<Project> getProjects() {
        return new ProjectJpaController(getEntityManagerFactory())
                .findProjectEntities();
    }

    public List<Project> getChildren() {
        ArrayList<Project> children = new ArrayList<>();
        getProjects().stream().filter((p)
                -> (p.getParentProjectId() != null)).filter((p)
                -> (p.getParentProjectId().getId().equals(getId())))
                .forEachOrdered((p) -> {
                    children.add(p);
                });
        return children;
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    public void copy(Project newProject) {
        update(this, newProject);
    }

    /**
     * Get the Test Projects for this project.
     *
     * @return Test projects for this project.
     */
    public List<TestProject> getTestProjects() {
        return getTestProjects(false);
    }

    /**
     * Get the Test Projects for this project.
     *
     * @param includeSubProjects true to include the sub projects as well.
     *
     * @return Test projects for this project.
     */
    public List<TestProject> getTestProjects(boolean includeSubProjects) {
        List<TestProject> tps = new ArrayList<>();
        tps.addAll(getTestProjectList());
        if (includeSubProjects) {
            for (Project p : getProjectList()) {
                tps.addAll(new ProjectServer(p)
                        .getTestProjects(includeSubProjects));
            }
        }
        return tps;
    }
}
