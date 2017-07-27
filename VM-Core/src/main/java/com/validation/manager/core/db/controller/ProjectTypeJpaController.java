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
import com.validation.manager.core.db.ProjectType;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ProjectTypeJpaController implements Serializable {

    public ProjectTypeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ProjectType projectType) {
        if (projectType.getProjectList() == null) {
            projectType.setProjectList(new ArrayList<Project>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Project> attachedProjectList = new ArrayList<Project>();
            for (Project projectListProjectToAttach : projectType.getProjectList()) {
                projectListProjectToAttach = em.getReference(projectListProjectToAttach.getClass(), projectListProjectToAttach.getId());
                attachedProjectList.add(projectListProjectToAttach);
            }
            projectType.setProjectList(attachedProjectList);
            em.persist(projectType);
            for (Project projectListProject : projectType.getProjectList()) {
                ProjectType oldProjectTypeIdOfProjectListProject = projectListProject.getProjectTypeId();
                projectListProject.setProjectTypeId(projectType);
                projectListProject = em.merge(projectListProject);
                if (oldProjectTypeIdOfProjectListProject != null) {
                    oldProjectTypeIdOfProjectListProject.getProjectList().remove(projectListProject);
                    oldProjectTypeIdOfProjectListProject = em.merge(oldProjectTypeIdOfProjectListProject);
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

    public void edit(ProjectType projectType) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ProjectType persistentProjectType = em.find(ProjectType.class, projectType.getId());
            List<Project> projectListOld = persistentProjectType.getProjectList();
            List<Project> projectListNew = projectType.getProjectList();
            List<String> illegalOrphanMessages = null;
            for (Project projectListOldProject : projectListOld) {
                if (!projectListNew.contains(projectListOldProject)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Project " + projectListOldProject + " since its projectTypeId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Project> attachedProjectListNew = new ArrayList<Project>();
            for (Project projectListNewProjectToAttach : projectListNew) {
                projectListNewProjectToAttach = em.getReference(projectListNewProjectToAttach.getClass(), projectListNewProjectToAttach.getId());
                attachedProjectListNew.add(projectListNewProjectToAttach);
            }
            projectListNew = attachedProjectListNew;
            projectType.setProjectList(projectListNew);
            projectType = em.merge(projectType);
            for (Project projectListNewProject : projectListNew) {
                if (!projectListOld.contains(projectListNewProject)) {
                    ProjectType oldProjectTypeIdOfProjectListNewProject = projectListNewProject.getProjectTypeId();
                    projectListNewProject.setProjectTypeId(projectType);
                    projectListNewProject = em.merge(projectListNewProject);
                    if (oldProjectTypeIdOfProjectListNewProject != null && !oldProjectTypeIdOfProjectListNewProject.equals(projectType)) {
                        oldProjectTypeIdOfProjectListNewProject.getProjectList().remove(projectListNewProject);
                        oldProjectTypeIdOfProjectListNewProject = em.merge(oldProjectTypeIdOfProjectListNewProject);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = projectType.getId();
                if (findProjectType(id) == null) {
                    throw new NonexistentEntityException("The projectType with id " + id + " no longer exists.");
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
            ProjectType projectType;
            try {
                projectType = em.getReference(ProjectType.class, id);
                projectType.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The projectType with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Project> projectListOrphanCheck = projectType.getProjectList();
            for (Project projectListOrphanCheckProject : projectListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This ProjectType (" + projectType + ") cannot be destroyed since the Project " + projectListOrphanCheckProject + " in its projectList field has a non-nullable projectTypeId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(projectType);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ProjectType> findProjectTypeEntities() {
        return findProjectTypeEntities(true, -1, -1);
    }

    public List<ProjectType> findProjectTypeEntities(int maxResults, int firstResult) {
        return findProjectTypeEntities(false, maxResults, firstResult);
    }

    private List<ProjectType> findProjectTypeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ProjectType.class));
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

    public ProjectType findProjectType(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ProjectType.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getProjectTypeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ProjectType> rt = cq.from(ProjectType.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
