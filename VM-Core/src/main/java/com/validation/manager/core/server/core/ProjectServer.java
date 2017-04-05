package com.validation.manager.core.server.core;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import static com.validation.manager.core.DataBaseManager.namedQuery;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
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
    public int write2DB() throws IllegalOrphanException,
            NonexistentEntityException, Exception {
        Project p;
        if (getId() != null && getId() > 0) {
            p = new ProjectJpaController(getEntityManagerFactory())
                    .findProject(getId());
            update(p, this);
            new ProjectJpaController(getEntityManagerFactory()).edit(p);
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
                    new ProjectJpaController(getEntityManagerFactory())
                            .destroy(p.getId());
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
        target.setTestCaseExecutions(source.getTestCaseExecutions());
        target.setId(source.getId());
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

    public static List<Requirement> getRequirements(Project p) {
        ProjectServer project = new ProjectServer(p);
        ArrayList<Requirement> requirements = new ArrayList<>();
        if (project.getRequirementSpecList() != null) {
            project.getRequirementSpecList().forEach((rs) -> {
                requirements.addAll(RequirementSpecServer.getRequirements(rs));
            });
        }
        if (project.getProjectList() != null) {
            project.getProjectList().forEach((sp) -> {
                requirements.addAll(getRequirements(sp));
            });
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
        namedQuery("Project.findById",
                parameters).forEach((obj) -> {
                    versions.add((Project) obj);
                });
        return versions;
    }

    public void copy(Project newProject) {
        update(this, newProject);
    }
}
