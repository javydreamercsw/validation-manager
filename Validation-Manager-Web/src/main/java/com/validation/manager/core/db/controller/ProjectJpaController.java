/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.ProjectHasTestProject;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.TestProject;
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
            project.setTestProjectList(new ArrayList<TestProject>());
        }
        if (project.getRequirementSpecList() == null) {
            project.setRequirementSpecList(new ArrayList<RequirementSpec>());
        }
        if (project.getProjectList() == null) {
            project.setProjectList(new ArrayList<Project>());
        }
        if (project.getProjectHasTestProjectList() == null) {
            project.setProjectHasTestProjectList(new ArrayList<ProjectHasTestProject>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Project projectRel = project.getProject();
            if (projectRel != null) {
                projectRel = em.getReference(projectRel.getClass(), projectRel.getId());
                project.setProject(projectRel);
            }
            List<TestProject> attachedTestProjectList = new ArrayList<TestProject>();
            for (TestProject testProjectListTestProjectToAttach : project.getTestProjectList()) {
                testProjectListTestProjectToAttach = em.getReference(testProjectListTestProjectToAttach.getClass(), testProjectListTestProjectToAttach.getId());
                attachedTestProjectList.add(testProjectListTestProjectToAttach);
            }
            project.setTestProjectList(attachedTestProjectList);
            List<RequirementSpec> attachedRequirementSpecList = new ArrayList<RequirementSpec>();
            for (RequirementSpec requirementSpecListRequirementSpecToAttach : project.getRequirementSpecList()) {
                requirementSpecListRequirementSpecToAttach = em.getReference(requirementSpecListRequirementSpecToAttach.getClass(), requirementSpecListRequirementSpecToAttach.getRequirementSpecPK());
                attachedRequirementSpecList.add(requirementSpecListRequirementSpecToAttach);
            }
            project.setRequirementSpecList(attachedRequirementSpecList);
            List<Project> attachedProjectList = new ArrayList<Project>();
            for (Project projectListProjectToAttach : project.getProjectList()) {
                projectListProjectToAttach = em.getReference(projectListProjectToAttach.getClass(), projectListProjectToAttach.getId());
                attachedProjectList.add(projectListProjectToAttach);
            }
            project.setProjectList(attachedProjectList);
            List<ProjectHasTestProject> attachedProjectHasTestProjectList = new ArrayList<ProjectHasTestProject>();
            for (ProjectHasTestProject projectHasTestProjectListProjectHasTestProjectToAttach : project.getProjectHasTestProjectList()) {
                projectHasTestProjectListProjectHasTestProjectToAttach = em.getReference(projectHasTestProjectListProjectHasTestProjectToAttach.getClass(), projectHasTestProjectListProjectHasTestProjectToAttach.getProjectHasTestProjectPK());
                attachedProjectHasTestProjectList.add(projectHasTestProjectListProjectHasTestProjectToAttach);
            }
            project.setProjectHasTestProjectList(attachedProjectHasTestProjectList);
            em.persist(project);
            if (projectRel != null) {
                projectRel.getProjectList().add(project);
                projectRel = em.merge(projectRel);
            }
            for (TestProject testProjectListTestProject : project.getTestProjectList()) {
                testProjectListTestProject.getProjectList().add(project);
                testProjectListTestProject = em.merge(testProjectListTestProject);
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
            for (Project projectListProject : project.getProjectList()) {
                Project oldProjectOfProjectListProject = projectListProject.getProject();
                projectListProject.setProject(project);
                projectListProject = em.merge(projectListProject);
                if (oldProjectOfProjectListProject != null) {
                    oldProjectOfProjectListProject.getProjectList().remove(projectListProject);
                    oldProjectOfProjectListProject = em.merge(oldProjectOfProjectListProject);
                }
            }
            for (ProjectHasTestProject projectHasTestProjectListProjectHasTestProject : project.getProjectHasTestProjectList()) {
                Project oldProjectOfProjectHasTestProjectListProjectHasTestProject = projectHasTestProjectListProjectHasTestProject.getProject();
                projectHasTestProjectListProjectHasTestProject.setProject(project);
                projectHasTestProjectListProjectHasTestProject = em.merge(projectHasTestProjectListProjectHasTestProject);
                if (oldProjectOfProjectHasTestProjectListProjectHasTestProject != null) {
                    oldProjectOfProjectHasTestProjectListProjectHasTestProject.getProjectHasTestProjectList().remove(projectHasTestProjectListProjectHasTestProject);
                    oldProjectOfProjectHasTestProjectListProjectHasTestProject = em.merge(oldProjectOfProjectHasTestProjectListProjectHasTestProject);
                }
            }
            em.getTransaction().commit();
        } finally {
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
            Project projectRelOld = persistentProject.getProject();
            Project projectRelNew = project.getProject();
            List<TestProject> testProjectListOld = persistentProject.getTestProjectList();
            List<TestProject> testProjectListNew = project.getTestProjectList();
            List<RequirementSpec> requirementSpecListOld = persistentProject.getRequirementSpecList();
            List<RequirementSpec> requirementSpecListNew = project.getRequirementSpecList();
            List<Project> projectListOld = persistentProject.getProjectList();
            List<Project> projectListNew = project.getProjectList();
            List<ProjectHasTestProject> projectHasTestProjectListOld = persistentProject.getProjectHasTestProjectList();
            List<ProjectHasTestProject> projectHasTestProjectListNew = project.getProjectHasTestProjectList();
            List<String> illegalOrphanMessages = null;
            for (RequirementSpec requirementSpecListOldRequirementSpec : requirementSpecListOld) {
                if (!requirementSpecListNew.contains(requirementSpecListOldRequirementSpec)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RequirementSpec " + requirementSpecListOldRequirementSpec + " since its project field is not nullable.");
                }
            }
            for (ProjectHasTestProject projectHasTestProjectListOldProjectHasTestProject : projectHasTestProjectListOld) {
                if (!projectHasTestProjectListNew.contains(projectHasTestProjectListOldProjectHasTestProject)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ProjectHasTestProject " + projectHasTestProjectListOldProjectHasTestProject + " since its project field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (projectRelNew != null) {
                projectRelNew = em.getReference(projectRelNew.getClass(), projectRelNew.getId());
                project.setProject(projectRelNew);
            }
            List<TestProject> attachedTestProjectListNew = new ArrayList<TestProject>();
            for (TestProject testProjectListNewTestProjectToAttach : testProjectListNew) {
                testProjectListNewTestProjectToAttach = em.getReference(testProjectListNewTestProjectToAttach.getClass(), testProjectListNewTestProjectToAttach.getId());
                attachedTestProjectListNew.add(testProjectListNewTestProjectToAttach);
            }
            testProjectListNew = attachedTestProjectListNew;
            project.setTestProjectList(testProjectListNew);
            List<RequirementSpec> attachedRequirementSpecListNew = new ArrayList<RequirementSpec>();
            for (RequirementSpec requirementSpecListNewRequirementSpecToAttach : requirementSpecListNew) {
                requirementSpecListNewRequirementSpecToAttach = em.getReference(requirementSpecListNewRequirementSpecToAttach.getClass(), requirementSpecListNewRequirementSpecToAttach.getRequirementSpecPK());
                attachedRequirementSpecListNew.add(requirementSpecListNewRequirementSpecToAttach);
            }
            requirementSpecListNew = attachedRequirementSpecListNew;
            project.setRequirementSpecList(requirementSpecListNew);
            List<Project> attachedProjectListNew = new ArrayList<Project>();
            for (Project projectListNewProjectToAttach : projectListNew) {
                projectListNewProjectToAttach = em.getReference(projectListNewProjectToAttach.getClass(), projectListNewProjectToAttach.getId());
                attachedProjectListNew.add(projectListNewProjectToAttach);
            }
            projectListNew = attachedProjectListNew;
            project.setProjectList(projectListNew);
            List<ProjectHasTestProject> attachedProjectHasTestProjectListNew = new ArrayList<ProjectHasTestProject>();
            for (ProjectHasTestProject projectHasTestProjectListNewProjectHasTestProjectToAttach : projectHasTestProjectListNew) {
                projectHasTestProjectListNewProjectHasTestProjectToAttach = em.getReference(projectHasTestProjectListNewProjectHasTestProjectToAttach.getClass(), projectHasTestProjectListNewProjectHasTestProjectToAttach.getProjectHasTestProjectPK());
                attachedProjectHasTestProjectListNew.add(projectHasTestProjectListNewProjectHasTestProjectToAttach);
            }
            projectHasTestProjectListNew = attachedProjectHasTestProjectListNew;
            project.setProjectHasTestProjectList(projectHasTestProjectListNew);
            project = em.merge(project);
            if (projectRelOld != null && !projectRelOld.equals(projectRelNew)) {
                projectRelOld.getProjectList().remove(project);
                projectRelOld = em.merge(projectRelOld);
            }
            if (projectRelNew != null && !projectRelNew.equals(projectRelOld)) {
                projectRelNew.getProjectList().add(project);
                projectRelNew = em.merge(projectRelNew);
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
            for (Project projectListOldProject : projectListOld) {
                if (!projectListNew.contains(projectListOldProject)) {
                    projectListOldProject.setProject(null);
                    projectListOldProject = em.merge(projectListOldProject);
                }
            }
            for (Project projectListNewProject : projectListNew) {
                if (!projectListOld.contains(projectListNewProject)) {
                    Project oldProjectOfProjectListNewProject = projectListNewProject.getProject();
                    projectListNewProject.setProject(project);
                    projectListNewProject = em.merge(projectListNewProject);
                    if (oldProjectOfProjectListNewProject != null && !oldProjectOfProjectListNewProject.equals(project)) {
                        oldProjectOfProjectListNewProject.getProjectList().remove(projectListNewProject);
                        oldProjectOfProjectListNewProject = em.merge(oldProjectOfProjectListNewProject);
                    }
                }
            }
            for (ProjectHasTestProject projectHasTestProjectListNewProjectHasTestProject : projectHasTestProjectListNew) {
                if (!projectHasTestProjectListOld.contains(projectHasTestProjectListNewProjectHasTestProject)) {
                    Project oldProjectOfProjectHasTestProjectListNewProjectHasTestProject = projectHasTestProjectListNewProjectHasTestProject.getProject();
                    projectHasTestProjectListNewProjectHasTestProject.setProject(project);
                    projectHasTestProjectListNewProjectHasTestProject = em.merge(projectHasTestProjectListNewProjectHasTestProject);
                    if (oldProjectOfProjectHasTestProjectListNewProjectHasTestProject != null && !oldProjectOfProjectHasTestProjectListNewProjectHasTestProject.equals(project)) {
                        oldProjectOfProjectHasTestProjectListNewProjectHasTestProject.getProjectHasTestProjectList().remove(projectHasTestProjectListNewProjectHasTestProject);
                        oldProjectOfProjectHasTestProjectListNewProjectHasTestProject = em.merge(oldProjectOfProjectHasTestProjectListNewProjectHasTestProject);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = project.getId();
                if (findProject(id) == null) {
                    throw new NonexistentEntityException("The project with id " + id + " no longer exists.");
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
            Project project;
            try {
                project = em.getReference(Project.class, id);
                project.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The project with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<RequirementSpec> requirementSpecListOrphanCheck = project.getRequirementSpecList();
            for (RequirementSpec requirementSpecListOrphanCheckRequirementSpec : requirementSpecListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Project (" + project + ") cannot be destroyed since the RequirementSpec " + requirementSpecListOrphanCheckRequirementSpec + " in its requirementSpecList field has a non-nullable project field.");
            }
            List<ProjectHasTestProject> projectHasTestProjectListOrphanCheck = project.getProjectHasTestProjectList();
            for (ProjectHasTestProject projectHasTestProjectListOrphanCheckProjectHasTestProject : projectHasTestProjectListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Project (" + project + ") cannot be destroyed since the ProjectHasTestProject " + projectHasTestProjectListOrphanCheckProjectHasTestProject + " in its projectHasTestProjectList field has a non-nullable project field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Project projectRel = project.getProject();
            if (projectRel != null) {
                projectRel.getProjectList().remove(project);
                projectRel = em.merge(projectRel);
            }
            List<TestProject> testProjectList = project.getTestProjectList();
            for (TestProject testProjectListTestProject : testProjectList) {
                testProjectListTestProject.getProjectList().remove(project);
                testProjectListTestProject = em.merge(testProjectListTestProject);
            }
            List<Project> projectList = project.getProjectList();
            for (Project projectListProject : projectList) {
                projectListProject.setProject(null);
                projectListProject = em.merge(projectListProject);
            }
            em.remove(project);
            em.getTransaction().commit();
        } finally {
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
        } finally {
            em.close();
        }
    }

    public Project findProject(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Project.class, id);
        } finally {
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
        } finally {
            em.close();
        }
    }
    
}
