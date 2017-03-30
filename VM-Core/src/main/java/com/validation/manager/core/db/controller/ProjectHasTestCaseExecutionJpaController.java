/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.ProjectHasTestCaseExecution;
import com.validation.manager.core.db.ProjectHasTestCaseExecutionPK;
import com.validation.manager.core.db.TestCaseExecution;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ProjectHasTestCaseExecutionJpaController implements Serializable {

    public ProjectHasTestCaseExecutionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ProjectHasTestCaseExecution projectHasTestCaseExecution) throws PreexistingEntityException, Exception {
        if (projectHasTestCaseExecution.getProjectHasTestCaseExecutionPK() == null) {
            projectHasTestCaseExecution.setProjectHasTestCaseExecutionPK(new ProjectHasTestCaseExecutionPK());
        }
        projectHasTestCaseExecution.getProjectHasTestCaseExecutionPK().setProjectId(projectHasTestCaseExecution.getProject().getId());
        projectHasTestCaseExecution.getProjectHasTestCaseExecutionPK().setTestCaseExecutionId(projectHasTestCaseExecution.getTestCaseExecution().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Project project = projectHasTestCaseExecution.getProject();
            if (project != null) {
                project = em.getReference(project.getClass(), project.getId());
                projectHasTestCaseExecution.setProject(project);
            }
            TestCaseExecution testCaseExecution = projectHasTestCaseExecution.getTestCaseExecution();
            if (testCaseExecution != null) {
                testCaseExecution = em.getReference(testCaseExecution.getClass(), testCaseExecution.getId());
                projectHasTestCaseExecution.setTestCaseExecution(testCaseExecution);
            }
            VmUser createdBy = projectHasTestCaseExecution.getCreatedBy();
            if (createdBy != null) {
                createdBy = em.getReference(createdBy.getClass(), createdBy.getId());
                projectHasTestCaseExecution.setCreatedBy(createdBy);
            }
            em.persist(projectHasTestCaseExecution);
            if (project != null) {
                project.getProjectHasTestCaseExecutionList().add(projectHasTestCaseExecution);
                project = em.merge(project);
            }
            if (testCaseExecution != null) {
                testCaseExecution.getProjectHasTestCaseExecutionList().add(projectHasTestCaseExecution);
                testCaseExecution = em.merge(testCaseExecution);
            }
            if (createdBy != null) {
                createdBy.getProjectHasTestCaseExecutionList().add(projectHasTestCaseExecution);
                createdBy = em.merge(createdBy);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findProjectHasTestCaseExecution(projectHasTestCaseExecution.getProjectHasTestCaseExecutionPK()) != null) {
                throw new PreexistingEntityException("ProjectHasTestCaseExecution " + projectHasTestCaseExecution + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ProjectHasTestCaseExecution projectHasTestCaseExecution) throws NonexistentEntityException, Exception {
        projectHasTestCaseExecution.getProjectHasTestCaseExecutionPK().setProjectId(projectHasTestCaseExecution.getProject().getId());
        projectHasTestCaseExecution.getProjectHasTestCaseExecutionPK().setTestCaseExecutionId(projectHasTestCaseExecution.getTestCaseExecution().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ProjectHasTestCaseExecution persistentProjectHasTestCaseExecution = em.find(ProjectHasTestCaseExecution.class, projectHasTestCaseExecution.getProjectHasTestCaseExecutionPK());
            Project projectOld = persistentProjectHasTestCaseExecution.getProject();
            Project projectNew = projectHasTestCaseExecution.getProject();
            TestCaseExecution testCaseExecutionOld = persistentProjectHasTestCaseExecution.getTestCaseExecution();
            TestCaseExecution testCaseExecutionNew = projectHasTestCaseExecution.getTestCaseExecution();
            VmUser createdByOld = persistentProjectHasTestCaseExecution.getCreatedBy();
            VmUser createdByNew = projectHasTestCaseExecution.getCreatedBy();
            if (projectNew != null) {
                projectNew = em.getReference(projectNew.getClass(), projectNew.getId());
                projectHasTestCaseExecution.setProject(projectNew);
            }
            if (testCaseExecutionNew != null) {
                testCaseExecutionNew = em.getReference(testCaseExecutionNew.getClass(), testCaseExecutionNew.getId());
                projectHasTestCaseExecution.setTestCaseExecution(testCaseExecutionNew);
            }
            if (createdByNew != null) {
                createdByNew = em.getReference(createdByNew.getClass(), createdByNew.getId());
                projectHasTestCaseExecution.setCreatedBy(createdByNew);
            }
            projectHasTestCaseExecution = em.merge(projectHasTestCaseExecution);
            if (projectOld != null && !projectOld.equals(projectNew)) {
                projectOld.getProjectHasTestCaseExecutionList().remove(projectHasTestCaseExecution);
                projectOld = em.merge(projectOld);
            }
            if (projectNew != null && !projectNew.equals(projectOld)) {
                projectNew.getProjectHasTestCaseExecutionList().add(projectHasTestCaseExecution);
                projectNew = em.merge(projectNew);
            }
            if (testCaseExecutionOld != null && !testCaseExecutionOld.equals(testCaseExecutionNew)) {
                testCaseExecutionOld.getProjectHasTestCaseExecutionList().remove(projectHasTestCaseExecution);
                testCaseExecutionOld = em.merge(testCaseExecutionOld);
            }
            if (testCaseExecutionNew != null && !testCaseExecutionNew.equals(testCaseExecutionOld)) {
                testCaseExecutionNew.getProjectHasTestCaseExecutionList().add(projectHasTestCaseExecution);
                testCaseExecutionNew = em.merge(testCaseExecutionNew);
            }
            if (createdByOld != null && !createdByOld.equals(createdByNew)) {
                createdByOld.getProjectHasTestCaseExecutionList().remove(projectHasTestCaseExecution);
                createdByOld = em.merge(createdByOld);
            }
            if (createdByNew != null && !createdByNew.equals(createdByOld)) {
                createdByNew.getProjectHasTestCaseExecutionList().add(projectHasTestCaseExecution);
                createdByNew = em.merge(createdByNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                ProjectHasTestCaseExecutionPK id = projectHasTestCaseExecution.getProjectHasTestCaseExecutionPK();
                if (findProjectHasTestCaseExecution(id) == null) {
                    throw new NonexistentEntityException("The projectHasTestCaseExecution with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(ProjectHasTestCaseExecutionPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ProjectHasTestCaseExecution projectHasTestCaseExecution;
            try {
                projectHasTestCaseExecution = em.getReference(ProjectHasTestCaseExecution.class, id);
                projectHasTestCaseExecution.getProjectHasTestCaseExecutionPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The projectHasTestCaseExecution with id " + id + " no longer exists.", enfe);
            }
            Project project = projectHasTestCaseExecution.getProject();
            if (project != null) {
                project.getProjectHasTestCaseExecutionList().remove(projectHasTestCaseExecution);
                project = em.merge(project);
            }
            TestCaseExecution testCaseExecution = projectHasTestCaseExecution.getTestCaseExecution();
            if (testCaseExecution != null) {
                testCaseExecution.getProjectHasTestCaseExecutionList().remove(projectHasTestCaseExecution);
                testCaseExecution = em.merge(testCaseExecution);
            }
            VmUser createdBy = projectHasTestCaseExecution.getCreatedBy();
            if (createdBy != null) {
                createdBy.getProjectHasTestCaseExecutionList().remove(projectHasTestCaseExecution);
                createdBy = em.merge(createdBy);
            }
            em.remove(projectHasTestCaseExecution);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ProjectHasTestCaseExecution> findProjectHasTestCaseExecutionEntities() {
        return findProjectHasTestCaseExecutionEntities(true, -1, -1);
    }

    public List<ProjectHasTestCaseExecution> findProjectHasTestCaseExecutionEntities(int maxResults, int firstResult) {
        return findProjectHasTestCaseExecutionEntities(false, maxResults, firstResult);
    }

    private List<ProjectHasTestCaseExecution> findProjectHasTestCaseExecutionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ProjectHasTestCaseExecution.class));
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

    public ProjectHasTestCaseExecution findProjectHasTestCaseExecution(ProjectHasTestCaseExecutionPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ProjectHasTestCaseExecution.class, id);
        } finally {
            em.close();
        }
    }

    public int getProjectHasTestCaseExecutionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ProjectHasTestCaseExecution> rt = cq.from(ProjectHasTestCaseExecution.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
