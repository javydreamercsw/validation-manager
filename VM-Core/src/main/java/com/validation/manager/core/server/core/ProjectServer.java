/*
 * Copyright 2017 Javier A. Ortiz Bultron javier.ortiz.78@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.validation.manager.core.server.core;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.ProjectType;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.controller.ProjectJpaController;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class ProjectServer extends Project
        implements EntityServer<Project>, VersionableServer<Project> {

    private static final long serialVersionUID = 3_434_510_483_033_583_117L;

    public ProjectServer(String name, String notes, ProjectType type) {
        super(name);
        setNotes(notes);
        setProjectTypeId(type);
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
        if (p.getId() != null) {
            Project product = new ProjectJpaController(
                    getEntityManagerFactory()).findProject(p.getId());
            update((ProjectServer) this, product);
        } else {
            update((ProjectServer) this, p);
        }
    }

    @Override
    public int write2DB() throws VMException {
        try {
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
        }
        catch (Exception ex) {
            throw new VMException(ex);
        }
        return getId();
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
        target.setUserHasRoleList(source.getUserHasRoleList());
        target.setProjectTypeId(source.getProjectTypeId());
        target.setFmeaList(source.getFmeaList());
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
