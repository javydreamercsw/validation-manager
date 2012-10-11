/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.ProjectHasTestProject;
import com.validation.manager.core.db.ProjectHasTestProjectPK;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.io.Serializable;
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
public class ProjectHasTestProjectJpaController implements Serializable {

    public ProjectHasTestProjectJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ProjectHasTestProject projectHasTestProject) throws PreexistingEntityException, Exception {
        if (projectHasTestProject.getProjectHasTestProjectPK() == null) {
            projectHasTestProject.setProjectHasTestProjectPK(new ProjectHasTestProjectPK());
        }
        projectHasTestProject.getProjectHasTestProjectPK().setTestProjectId(projectHasTestProject.getTestProject().getId());
        projectHasTestProject.getProjectHasTestProjectPK().setprojectId(projectHasTestProject.getProject().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TestProject testProject = projectHasTestProject.getTestProject();
            if (testProject != null) {
                testProject = em.getReference(testProject.getClass(), testProject.getId());
                projectHasTestProject.setTestProject(testProject);
            }
            Project project = projectHasTestProject.getProject();
            if (project != null) {
                project = em.getReference(project.getClass(), project.getId());
                projectHasTestProject.setProject(project);
            }
            em.persist(projectHasTestProject);
            if (testProject != null) {
                testProject.getProjectHasTestProjectList().add(projectHasTestProject);
                testProject = em.merge(testProject);
            }
            if (project != null) {
                project.getProjectHasTestProjectList().add(projectHasTestProject);
                project = em.merge(project);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findProjectHasTestProject(projectHasTestProject.getProjectHasTestProjectPK()) != null) {
                throw new PreexistingEntityException("ProjectHasTestProject " + projectHasTestProject + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ProjectHasTestProject projectHasTestProject) throws NonexistentEntityException, Exception {
        projectHasTestProject.getProjectHasTestProjectPK().setTestProjectId(projectHasTestProject.getTestProject().getId());
        projectHasTestProject.getProjectHasTestProjectPK().setprojectId(projectHasTestProject.getProject().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ProjectHasTestProject persistentProjectHasTestProject = em.find(ProjectHasTestProject.class, projectHasTestProject.getProjectHasTestProjectPK());
            TestProject testProjectOld = persistentProjectHasTestProject.getTestProject();
            TestProject testProjectNew = projectHasTestProject.getTestProject();
            Project projectOld = persistentProjectHasTestProject.getProject();
            Project projectNew = projectHasTestProject.getProject();
            if (testProjectNew != null) {
                testProjectNew = em.getReference(testProjectNew.getClass(), testProjectNew.getId());
                projectHasTestProject.setTestProject(testProjectNew);
            }
            if (projectNew != null) {
                projectNew = em.getReference(projectNew.getClass(), projectNew.getId());
                projectHasTestProject.setProject(projectNew);
            }
            projectHasTestProject = em.merge(projectHasTestProject);
            if (testProjectOld != null && !testProjectOld.equals(testProjectNew)) {
                testProjectOld.getProjectHasTestProjectList().remove(projectHasTestProject);
                testProjectOld = em.merge(testProjectOld);
            }
            if (testProjectNew != null && !testProjectNew.equals(testProjectOld)) {
                testProjectNew.getProjectHasTestProjectList().add(projectHasTestProject);
                testProjectNew = em.merge(testProjectNew);
            }
            if (projectOld != null && !projectOld.equals(projectNew)) {
                projectOld.getProjectHasTestProjectList().remove(projectHasTestProject);
                projectOld = em.merge(projectOld);
            }
            if (projectNew != null && !projectNew.equals(projectOld)) {
                projectNew.getProjectHasTestProjectList().add(projectHasTestProject);
                projectNew = em.merge(projectNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                ProjectHasTestProjectPK id = projectHasTestProject.getProjectHasTestProjectPK();
                if (findProjectHasTestProject(id) == null) {
                    throw new NonexistentEntityException("The projectHasTestProject with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(ProjectHasTestProjectPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ProjectHasTestProject projectHasTestProject;
            try {
                projectHasTestProject = em.getReference(ProjectHasTestProject.class, id);
                projectHasTestProject.getProjectHasTestProjectPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The projectHasTestProject with id " + id + " no longer exists.", enfe);
            }
            TestProject testProject = projectHasTestProject.getTestProject();
            if (testProject != null) {
                testProject.getProjectHasTestProjectList().remove(projectHasTestProject);
                testProject = em.merge(testProject);
            }
            Project project = projectHasTestProject.getProject();
            if (project != null) {
                project.getProjectHasTestProjectList().remove(projectHasTestProject);
                project = em.merge(project);
            }
            em.remove(projectHasTestProject);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ProjectHasTestProject> findProjectHasTestProjectEntities() {
        return findProjectHasTestProjectEntities(true, -1, -1);
    }

    public List<ProjectHasTestProject> findProjectHasTestProjectEntities(int maxResults, int firstResult) {
        return findProjectHasTestProjectEntities(false, maxResults, firstResult);
    }

    private List<ProjectHasTestProject> findProjectHasTestProjectEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ProjectHasTestProject.class));
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

    public ProjectHasTestProject findProjectHasTestProject(ProjectHasTestProjectPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ProjectHasTestProject.class, id);
        } finally {
            em.close();
        }
    }

    public int getProjectHasTestProjectCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ProjectHasTestProject> rt = cq.from(ProjectHasTestProject.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
