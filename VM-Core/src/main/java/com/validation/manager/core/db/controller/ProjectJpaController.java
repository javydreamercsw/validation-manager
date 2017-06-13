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
package com.validation.manager.core.db.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.TestProject;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ProjectJpaController implements Serializable {

    public ProjectJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Project project) {
        if (project.getTestProjectList() == null) {
            project.setTestProjectList(new ArrayList<>());
        }
        if (project.getProjectList() == null) {
            project.setProjectList(new ArrayList<>());
        }
        if (project.getRequirementSpecList() == null) {
            project.setRequirementSpecList(new ArrayList<>());
        }
        if (project.getHistoryList() == null) {
            project.setHistoryList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Project parentProjectId = project.getParentProjectId();
            if (parentProjectId != null) {
                parentProjectId = em.getReference(parentProjectId.getClass(), parentProjectId.getId());
                project.setParentProjectId(parentProjectId);
            }
            List<TestProject> attachedTestProjectList = new ArrayList<>();
            for (TestProject testProjectListTestProjectToAttach : project.getTestProjectList()) {
                testProjectListTestProjectToAttach = em.getReference(testProjectListTestProjectToAttach.getClass(), testProjectListTestProjectToAttach.getId());
                attachedTestProjectList.add(testProjectListTestProjectToAttach);
            }
            project.setTestProjectList(attachedTestProjectList);
            List<Project> attachedProjectList = new ArrayList<>();
            for (Project projectListProjectToAttach : project.getProjectList()) {
                projectListProjectToAttach = em.getReference(projectListProjectToAttach.getClass(), projectListProjectToAttach.getId());
                attachedProjectList.add(projectListProjectToAttach);
            }
            project.setProjectList(attachedProjectList);
            List<RequirementSpec> attachedRequirementSpecList = new ArrayList<>();
            for (RequirementSpec requirementSpecListRequirementSpecToAttach : project.getRequirementSpecList()) {
                requirementSpecListRequirementSpecToAttach = em.getReference(requirementSpecListRequirementSpecToAttach.getClass(), requirementSpecListRequirementSpecToAttach.getRequirementSpecPK());
                attachedRequirementSpecList.add(requirementSpecListRequirementSpecToAttach);
            }
            project.setRequirementSpecList(attachedRequirementSpecList);
            List<History> attachedHistoryList = new ArrayList<>();
            for (History historyListHistoryToAttach : project.getHistoryList()) {
                historyListHistoryToAttach = em.getReference(historyListHistoryToAttach.getClass(), historyListHistoryToAttach.getId());
                attachedHistoryList.add(historyListHistoryToAttach);
            }
            project.setHistoryList(attachedHistoryList);
            em.persist(project);
            if (parentProjectId != null) {
                parentProjectId.getProjectList().add(project);
                parentProjectId = em.merge(parentProjectId);
            }
            for (TestProject testProjectListTestProject : project.getTestProjectList()) {
                testProjectListTestProject.getProjectList().add(project);
                testProjectListTestProject = em.merge(testProjectListTestProject);
            }
            for (Project projectListProject : project.getProjectList()) {
                Project oldParentProjectIdOfProjectListProject = projectListProject.getParentProjectId();
                projectListProject.setParentProjectId(project);
                projectListProject = em.merge(projectListProject);
                if (oldParentProjectIdOfProjectListProject != null) {
                    oldParentProjectIdOfProjectListProject.getProjectList().remove(projectListProject);
                    oldParentProjectIdOfProjectListProject = em.merge(oldParentProjectIdOfProjectListProject);
                }
            }
            for (RequirementSpec requirementSpecListRequirementSpec : project.getRequirementSpecList()) {
                Project oldProjectOfRequirementSpecListRequirementSpec = requirementSpecListRequirementSpec.getProject();
                requirementSpecListRequirementSpec.setProject(project);
                requirementSpecListRequirementSpec = em.merge(requirementSpecListRequirementSpec);
                if (oldProjectOfRequirementSpecListRequirementSpec != null) {
                    oldProjectOfRequirementSpecListRequirementSpec.getRequirementSpecList().remove(requirementSpecListRequirementSpec);
                    oldProjectOfRequirementSpecListRequirementSpec = em.merge(oldProjectOfRequirementSpecListRequirementSpec);
                }
            }
            for (History historyListHistory : project.getHistoryList()) {
                Project oldProjectIdOfHistoryListHistory = historyListHistory.getProjectId();
                historyListHistory.setProjectId(project);
                historyListHistory = em.merge(historyListHistory);
                if (oldProjectIdOfHistoryListHistory != null) {
                    oldProjectIdOfHistoryListHistory.getHistoryList().remove(historyListHistory);
                    oldProjectIdOfHistoryListHistory = em.merge(oldProjectIdOfHistoryListHistory);
                }
            }
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Project project) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Project persistentProject = em.find(Project.class, project.getId());
            Project parentProjectIdOld = persistentProject.getParentProjectId();
            Project parentProjectIdNew = project.getParentProjectId();
            List<TestProject> testProjectListOld = persistentProject.getTestProjectList();
            List<TestProject> testProjectListNew = project.getTestProjectList();
            List<Project> projectListOld = persistentProject.getProjectList();
            List<Project> projectListNew = project.getProjectList();
            List<RequirementSpec> requirementSpecListOld = persistentProject.getRequirementSpecList();
            List<RequirementSpec> requirementSpecListNew = project.getRequirementSpecList();
            List<History> historyListOld = persistentProject.getHistoryList();
            List<History> historyListNew = project.getHistoryList();
            List<String> illegalOrphanMessages = null;
            for (RequirementSpec requirementSpecListOldRequirementSpec : requirementSpecListOld) {
                if (!requirementSpecListNew.contains(requirementSpecListOldRequirementSpec)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain RequirementSpec " + requirementSpecListOldRequirementSpec + " since its project field is not nullable.");
                }
            }
            for (History historyListOldHistory : historyListOld) {
                if (!historyListNew.contains(historyListOldHistory)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain History " + historyListOldHistory + " since its projectId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (parentProjectIdNew != null) {
                parentProjectIdNew = em.getReference(parentProjectIdNew.getClass(), parentProjectIdNew.getId());
                project.setParentProjectId(parentProjectIdNew);
            }
            List<TestProject> attachedTestProjectListNew = new ArrayList<>();
            for (TestProject testProjectListNewTestProjectToAttach : testProjectListNew) {
                testProjectListNewTestProjectToAttach = em.getReference(testProjectListNewTestProjectToAttach.getClass(), testProjectListNewTestProjectToAttach.getId());
                attachedTestProjectListNew.add(testProjectListNewTestProjectToAttach);
            }
            testProjectListNew = attachedTestProjectListNew;
            project.setTestProjectList(testProjectListNew);
            List<Project> attachedProjectListNew = new ArrayList<>();
            for (Project projectListNewProjectToAttach : projectListNew) {
                projectListNewProjectToAttach = em.getReference(projectListNewProjectToAttach.getClass(), projectListNewProjectToAttach.getId());
                attachedProjectListNew.add(projectListNewProjectToAttach);
            }
            projectListNew = attachedProjectListNew;
            project.setProjectList(projectListNew);
            List<RequirementSpec> attachedRequirementSpecListNew = new ArrayList<>();
            for (RequirementSpec requirementSpecListNewRequirementSpecToAttach : requirementSpecListNew) {
                requirementSpecListNewRequirementSpecToAttach = em.getReference(requirementSpecListNewRequirementSpecToAttach.getClass(), requirementSpecListNewRequirementSpecToAttach.getRequirementSpecPK());
                attachedRequirementSpecListNew.add(requirementSpecListNewRequirementSpecToAttach);
            }
            requirementSpecListNew = attachedRequirementSpecListNew;
            project.setRequirementSpecList(requirementSpecListNew);
            List<History> attachedHistoryListNew = new ArrayList<>();
            for (History historyListNewHistoryToAttach : historyListNew) {
                historyListNewHistoryToAttach = em.getReference(historyListNewHistoryToAttach.getClass(), historyListNewHistoryToAttach.getId());
                attachedHistoryListNew.add(historyListNewHistoryToAttach);
            }
            historyListNew = attachedHistoryListNew;
            project.setHistoryList(historyListNew);
            project = em.merge(project);
            if (parentProjectIdOld != null && !parentProjectIdOld.equals(parentProjectIdNew)) {
                parentProjectIdOld.getProjectList().remove(project);
                parentProjectIdOld = em.merge(parentProjectIdOld);
            }
            if (parentProjectIdNew != null && !parentProjectIdNew.equals(parentProjectIdOld)) {
                parentProjectIdNew.getProjectList().add(project);
                parentProjectIdNew = em.merge(parentProjectIdNew);
            }
            for (TestProject testProjectListOldTestProject : testProjectListOld) {
                if (!testProjectListNew.contains(testProjectListOldTestProject)) {
                    testProjectListOldTestProject.getProjectList().remove(project);
                    testProjectListOldTestProject = em.merge(testProjectListOldTestProject);
                }
            }
            for (TestProject testProjectListNewTestProject : testProjectListNew) {
                if (!testProjectListOld.contains(testProjectListNewTestProject)) {
                    testProjectListNewTestProject.getProjectList().add(project);
                    testProjectListNewTestProject = em.merge(testProjectListNewTestProject);
                }
            }
            for (Project projectListOldProject : projectListOld) {
                if (!projectListNew.contains(projectListOldProject)) {
                    projectListOldProject.setParentProjectId(null);
                    projectListOldProject = em.merge(projectListOldProject);
                }
            }
            for (Project projectListNewProject : projectListNew) {
                if (!projectListOld.contains(projectListNewProject)) {
                    Project oldParentProjectIdOfProjectListNewProject = projectListNewProject.getParentProjectId();
                    projectListNewProject.setParentProjectId(project);
                    projectListNewProject = em.merge(projectListNewProject);
                    if (oldParentProjectIdOfProjectListNewProject != null && !oldParentProjectIdOfProjectListNewProject.equals(project)) {
                        oldParentProjectIdOfProjectListNewProject.getProjectList().remove(projectListNewProject);
                        oldParentProjectIdOfProjectListNewProject = em.merge(oldParentProjectIdOfProjectListNewProject);
                    }
                }
            }
            for (RequirementSpec requirementSpecListNewRequirementSpec : requirementSpecListNew) {
                if (!requirementSpecListOld.contains(requirementSpecListNewRequirementSpec)) {
                    Project oldProjectOfRequirementSpecListNewRequirementSpec = requirementSpecListNewRequirementSpec.getProject();
                    requirementSpecListNewRequirementSpec.setProject(project);
                    requirementSpecListNewRequirementSpec = em.merge(requirementSpecListNewRequirementSpec);
                    if (oldProjectOfRequirementSpecListNewRequirementSpec != null && !oldProjectOfRequirementSpecListNewRequirementSpec.equals(project)) {
                        oldProjectOfRequirementSpecListNewRequirementSpec.getRequirementSpecList().remove(requirementSpecListNewRequirementSpec);
                        oldProjectOfRequirementSpecListNewRequirementSpec = em.merge(oldProjectOfRequirementSpecListNewRequirementSpec);
                    }
                }
            }
            for (History historyListNewHistory : historyListNew) {
                if (!historyListOld.contains(historyListNewHistory)) {
                    Project oldProjectIdOfHistoryListNewHistory = historyListNewHistory.getProjectId();
                    historyListNewHistory.setProjectId(project);
                    historyListNewHistory = em.merge(historyListNewHistory);
                    if (oldProjectIdOfHistoryListNewHistory != null && !oldProjectIdOfHistoryListNewHistory.equals(project)) {
                        oldProjectIdOfHistoryListNewHistory.getHistoryList().remove(historyListNewHistory);
                        oldProjectIdOfHistoryListNewHistory = em.merge(oldProjectIdOfHistoryListNewHistory);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = project.getId();
                if (findProject(id) == null) {
                    throw new NonexistentEntityException("The project with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Project project;
            try {
                project = em.getReference(Project.class, id);
                project.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The project with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<RequirementSpec> requirementSpecListOrphanCheck = project.getRequirementSpecList();
            for (RequirementSpec requirementSpecListOrphanCheckRequirementSpec : requirementSpecListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Project (" + project + ") cannot be destroyed since the RequirementSpec " + requirementSpecListOrphanCheckRequirementSpec + " in its requirementSpecList field has a non-nullable project field.");
            }
            List<History> historyListOrphanCheck = project.getHistoryList();
            for (History historyListOrphanCheckHistory : historyListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Project (" + project + ") cannot be destroyed since the History " + historyListOrphanCheckHistory + " in its historyList field has a non-nullable projectId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Project parentProjectId = project.getParentProjectId();
            if (parentProjectId != null) {
                parentProjectId.getProjectList().remove(project);
                parentProjectId = em.merge(parentProjectId);
            }
            List<TestProject> testProjectList = project.getTestProjectList();
            for (TestProject testProjectListTestProject : testProjectList) {
                testProjectListTestProject.getProjectList().remove(project);
                testProjectListTestProject = em.merge(testProjectListTestProject);
            }
            List<Project> projectList = project.getProjectList();
            for (Project projectListProject : projectList) {
                projectListProject.setParentProjectId(null);
                projectListProject = em.merge(projectListProject);
            }
            em.remove(project);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Project> findProjectEntities() {
        return findProjectEntities(true, -1, -1);
    }

    public List<Project> findProjectEntities(int maxResults, int firstResult) {
        return findProjectEntities(false, maxResults, firstResult);
    }

    private List<Project> findProjectEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Project.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        }
        finally {
            em.close();
        }
    }

    public Project findProject(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Project.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getProjectCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Project> rt = cq.from(Project.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
