/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.ProjectHasTestCaseExecution;
import com.validation.manager.core.db.TestCaseExecution;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TestCaseExecutionJpaController implements Serializable {

    public TestCaseExecutionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TestCaseExecution testCaseExecution) {
        if (testCaseExecution.getExecutionStepList() == null) {
            testCaseExecution.setExecutionStepList(new ArrayList<>());
        }
        if (testCaseExecution.getProjects() == null) {
            testCaseExecution.setProjects(new ArrayList<>());
        }
        if (testCaseExecution.getProjectHasTestCaseExecutionList() == null) {
            testCaseExecution.setProjectHasTestCaseExecutionList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<ExecutionStep> attachedExecutionStepList = new ArrayList<>();
            for (ExecutionStep executionStepListExecutionStepToAttach : testCaseExecution.getExecutionStepList()) {
                executionStepListExecutionStepToAttach = em.getReference(executionStepListExecutionStepToAttach.getClass(), executionStepListExecutionStepToAttach.getExecutionStepPK());
                attachedExecutionStepList.add(executionStepListExecutionStepToAttach);
            }
            testCaseExecution.setExecutionStepList(attachedExecutionStepList);
            List<Project> attachedProjects = new ArrayList<>();
            for (Project projectsProjectToAttach : testCaseExecution.getProjects()) {
                projectsProjectToAttach = em.getReference(projectsProjectToAttach.getClass(), projectsProjectToAttach.getId());
                attachedProjects.add(projectsProjectToAttach);
            }
            testCaseExecution.setProjects(attachedProjects);
            List<ProjectHasTestCaseExecution> attachedProjectHasTestCaseExecutionList = new ArrayList<>();
            for (ProjectHasTestCaseExecution projectHasTestCaseExecutionListProjectHasTestCaseExecutionToAttach : testCaseExecution.getProjectHasTestCaseExecutionList()) {
                projectHasTestCaseExecutionListProjectHasTestCaseExecutionToAttach = em.getReference(projectHasTestCaseExecutionListProjectHasTestCaseExecutionToAttach.getClass(), projectHasTestCaseExecutionListProjectHasTestCaseExecutionToAttach.getProjectHasTestCaseExecutionPK());
                attachedProjectHasTestCaseExecutionList.add(projectHasTestCaseExecutionListProjectHasTestCaseExecutionToAttach);
            }
            testCaseExecution.setProjectHasTestCaseExecutionList(attachedProjectHasTestCaseExecutionList);
            em.persist(testCaseExecution);
            for (ExecutionStep executionStepListExecutionStep : testCaseExecution.getExecutionStepList()) {
                TestCaseExecution oldTestCaseExecutionOfExecutionStepListExecutionStep = executionStepListExecutionStep.getTestCaseExecution();
                executionStepListExecutionStep.setTestCaseExecution(testCaseExecution);
                executionStepListExecutionStep = em.merge(executionStepListExecutionStep);
                if (oldTestCaseExecutionOfExecutionStepListExecutionStep != null) {
                    oldTestCaseExecutionOfExecutionStepListExecutionStep.getExecutionStepList().remove(executionStepListExecutionStep);
                    oldTestCaseExecutionOfExecutionStepListExecutionStep = em.merge(oldTestCaseExecutionOfExecutionStepListExecutionStep);
                }
            }
            for (Project projectsProject : testCaseExecution.getProjects()) {
                projectsProject.getTestCaseExecutions().add(testCaseExecution);
                projectsProject = em.merge(projectsProject);
            }
            for (ProjectHasTestCaseExecution projectHasTestCaseExecutionListProjectHasTestCaseExecution : testCaseExecution.getProjectHasTestCaseExecutionList()) {
                TestCaseExecution oldTestCaseExecutionOfProjectHasTestCaseExecutionListProjectHasTestCaseExecution = projectHasTestCaseExecutionListProjectHasTestCaseExecution.getTestCaseExecution();
                projectHasTestCaseExecutionListProjectHasTestCaseExecution.setTestCaseExecution(testCaseExecution);
                projectHasTestCaseExecutionListProjectHasTestCaseExecution = em.merge(projectHasTestCaseExecutionListProjectHasTestCaseExecution);
                if (oldTestCaseExecutionOfProjectHasTestCaseExecutionListProjectHasTestCaseExecution != null) {
                    oldTestCaseExecutionOfProjectHasTestCaseExecutionListProjectHasTestCaseExecution.getProjectHasTestCaseExecutionList().remove(projectHasTestCaseExecutionListProjectHasTestCaseExecution);
                    oldTestCaseExecutionOfProjectHasTestCaseExecutionListProjectHasTestCaseExecution = em.merge(oldTestCaseExecutionOfProjectHasTestCaseExecutionListProjectHasTestCaseExecution);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TestCaseExecution testCaseExecution) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TestCaseExecution persistentTestCaseExecution = em.find(TestCaseExecution.class, testCaseExecution.getId());
            List<ExecutionStep> executionStepListOld = persistentTestCaseExecution.getExecutionStepList();
            List<ExecutionStep> executionStepListNew = testCaseExecution.getExecutionStepList();
            List<Project> projectsOld = persistentTestCaseExecution.getProjects();
            List<Project> projectsNew = testCaseExecution.getProjects();
            List<ProjectHasTestCaseExecution> projectHasTestCaseExecutionListOld = persistentTestCaseExecution.getProjectHasTestCaseExecutionList();
            List<ProjectHasTestCaseExecution> projectHasTestCaseExecutionListNew = testCaseExecution.getProjectHasTestCaseExecutionList();
            List<String> illegalOrphanMessages = null;
            for (ExecutionStep executionStepListOldExecutionStep : executionStepListOld) {
                if (!executionStepListNew.contains(executionStepListOldExecutionStep)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain ExecutionStep " + executionStepListOldExecutionStep + " since its testCaseExecution field is not nullable.");
                }
            }
            for (ProjectHasTestCaseExecution projectHasTestCaseExecutionListOldProjectHasTestCaseExecution : projectHasTestCaseExecutionListOld) {
                if (!projectHasTestCaseExecutionListNew.contains(projectHasTestCaseExecutionListOldProjectHasTestCaseExecution)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain ProjectHasTestCaseExecution " + projectHasTestCaseExecutionListOldProjectHasTestCaseExecution + " since its testCaseExecution field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<ExecutionStep> attachedExecutionStepListNew = new ArrayList<>();
            for (ExecutionStep executionStepListNewExecutionStepToAttach : executionStepListNew) {
                executionStepListNewExecutionStepToAttach = em.getReference(executionStepListNewExecutionStepToAttach.getClass(), executionStepListNewExecutionStepToAttach.getExecutionStepPK());
                attachedExecutionStepListNew.add(executionStepListNewExecutionStepToAttach);
            }
            executionStepListNew = attachedExecutionStepListNew;
            testCaseExecution.setExecutionStepList(executionStepListNew);
            List<Project> attachedProjectsNew = new ArrayList<>();
            for (Project projectsNewProjectToAttach : projectsNew) {
                projectsNewProjectToAttach = em.getReference(projectsNewProjectToAttach.getClass(), projectsNewProjectToAttach.getId());
                attachedProjectsNew.add(projectsNewProjectToAttach);
            }
            projectsNew = attachedProjectsNew;
            testCaseExecution.setProjects(projectsNew);
            List<ProjectHasTestCaseExecution> attachedProjectHasTestCaseExecutionListNew = new ArrayList<>();
            for (ProjectHasTestCaseExecution projectHasTestCaseExecutionListNewProjectHasTestCaseExecutionToAttach : projectHasTestCaseExecutionListNew) {
                projectHasTestCaseExecutionListNewProjectHasTestCaseExecutionToAttach = em.getReference(projectHasTestCaseExecutionListNewProjectHasTestCaseExecutionToAttach.getClass(), projectHasTestCaseExecutionListNewProjectHasTestCaseExecutionToAttach.getProjectHasTestCaseExecutionPK());
                attachedProjectHasTestCaseExecutionListNew.add(projectHasTestCaseExecutionListNewProjectHasTestCaseExecutionToAttach);
            }
            projectHasTestCaseExecutionListNew = attachedProjectHasTestCaseExecutionListNew;
            testCaseExecution.setProjectHasTestCaseExecutionList(projectHasTestCaseExecutionListNew);
            testCaseExecution = em.merge(testCaseExecution);
            for (ExecutionStep executionStepListNewExecutionStep : executionStepListNew) {
                if (!executionStepListOld.contains(executionStepListNewExecutionStep)) {
                    TestCaseExecution oldTestCaseExecutionOfExecutionStepListNewExecutionStep = executionStepListNewExecutionStep.getTestCaseExecution();
                    executionStepListNewExecutionStep.setTestCaseExecution(testCaseExecution);
                    executionStepListNewExecutionStep = em.merge(executionStepListNewExecutionStep);
                    if (oldTestCaseExecutionOfExecutionStepListNewExecutionStep != null && !oldTestCaseExecutionOfExecutionStepListNewExecutionStep.equals(testCaseExecution)) {
                        oldTestCaseExecutionOfExecutionStepListNewExecutionStep.getExecutionStepList().remove(executionStepListNewExecutionStep);
                        oldTestCaseExecutionOfExecutionStepListNewExecutionStep = em.merge(oldTestCaseExecutionOfExecutionStepListNewExecutionStep);
                    }
                }
            }
            for (Project projectsOldProject : projectsOld) {
                if (!projectsNew.contains(projectsOldProject)) {
                    projectsOldProject.getTestCaseExecutions().remove(testCaseExecution);
                    projectsOldProject = em.merge(projectsOldProject);
                }
            }
            for (Project projectsNewProject : projectsNew) {
                if (!projectsOld.contains(projectsNewProject)) {
                    projectsNewProject.getTestCaseExecutions().add(testCaseExecution);
                    projectsNewProject = em.merge(projectsNewProject);
                }
            }
            for (ProjectHasTestCaseExecution projectHasTestCaseExecutionListNewProjectHasTestCaseExecution : projectHasTestCaseExecutionListNew) {
                if (!projectHasTestCaseExecutionListOld.contains(projectHasTestCaseExecutionListNewProjectHasTestCaseExecution)) {
                    TestCaseExecution oldTestCaseExecutionOfProjectHasTestCaseExecutionListNewProjectHasTestCaseExecution = projectHasTestCaseExecutionListNewProjectHasTestCaseExecution.getTestCaseExecution();
                    projectHasTestCaseExecutionListNewProjectHasTestCaseExecution.setTestCaseExecution(testCaseExecution);
                    projectHasTestCaseExecutionListNewProjectHasTestCaseExecution = em.merge(projectHasTestCaseExecutionListNewProjectHasTestCaseExecution);
                    if (oldTestCaseExecutionOfProjectHasTestCaseExecutionListNewProjectHasTestCaseExecution != null && !oldTestCaseExecutionOfProjectHasTestCaseExecutionListNewProjectHasTestCaseExecution.equals(testCaseExecution)) {
                        oldTestCaseExecutionOfProjectHasTestCaseExecutionListNewProjectHasTestCaseExecution.getProjectHasTestCaseExecutionList().remove(projectHasTestCaseExecutionListNewProjectHasTestCaseExecution);
                        oldTestCaseExecutionOfProjectHasTestCaseExecutionListNewProjectHasTestCaseExecution = em.merge(oldTestCaseExecutionOfProjectHasTestCaseExecutionListNewProjectHasTestCaseExecution);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = testCaseExecution.getId();
                if (findTestCaseExecution(id) == null) {
                    throw new NonexistentEntityException("The testCaseExecution with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
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
            TestCaseExecution testCaseExecution;
            try {
                testCaseExecution = em.getReference(TestCaseExecution.class, id);
                testCaseExecution.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The testCaseExecution with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<ExecutionStep> executionStepListOrphanCheck = testCaseExecution.getExecutionStepList();
            for (ExecutionStep executionStepListOrphanCheckExecutionStep : executionStepListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This TestCaseExecution (" + testCaseExecution + ") cannot be destroyed since the ExecutionStep " + executionStepListOrphanCheckExecutionStep + " in its executionStepList field has a non-nullable testCaseExecution field.");
            }
            List<ProjectHasTestCaseExecution> projectHasTestCaseExecutionListOrphanCheck = testCaseExecution.getProjectHasTestCaseExecutionList();
            for (ProjectHasTestCaseExecution projectHasTestCaseExecutionListOrphanCheckProjectHasTestCaseExecution : projectHasTestCaseExecutionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This TestCaseExecution (" + testCaseExecution + ") cannot be destroyed since the ProjectHasTestCaseExecution " + projectHasTestCaseExecutionListOrphanCheckProjectHasTestCaseExecution + " in its projectHasTestCaseExecutionList field has a non-nullable testCaseExecution field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Project> projects = testCaseExecution.getProjects();
            for (Project projectsProject : projects) {
                projectsProject.getTestCaseExecutions().remove(testCaseExecution);
                projectsProject = em.merge(projectsProject);
            }
            em.remove(testCaseExecution);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TestCaseExecution> findTestCaseExecutionEntities() {
        return findTestCaseExecutionEntities(true, -1, -1);
    }

    public List<TestCaseExecution> findTestCaseExecutionEntities(int maxResults, int firstResult) {
        return findTestCaseExecutionEntities(false, maxResults, firstResult);
    }

    private List<TestCaseExecution> findTestCaseExecutionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TestCaseExecution.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public TestCaseExecution findTestCaseExecution(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TestCaseExecution.class, id);
        } finally {
            em.close();
        }
    }

    public int getTestCaseExecutionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TestCaseExecution> rt = cq.from(TestCaseExecution.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
