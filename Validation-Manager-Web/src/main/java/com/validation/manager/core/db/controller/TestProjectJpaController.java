/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.ProjectHasTestProject;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.UserTestProjectRole;
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
public class TestProjectJpaController implements Serializable {

    public TestProjectJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TestProject testProject) {
        if (testProject.getProjectList() == null) {
            testProject.setProjectList(new ArrayList<Project>());
        }
        if (testProject.getUserTestProjectRoleList() == null) {
            testProject.setUserTestProjectRoleList(new ArrayList<UserTestProjectRole>());
        }
        if (testProject.getTestPlanList() == null) {
            testProject.setTestPlanList(new ArrayList<TestPlan>());
        }
        if (testProject.getProjectHasTestProjectList() == null) {
            testProject.setProjectHasTestProjectList(new ArrayList<ProjectHasTestProject>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Project> attachedProjectList = new ArrayList<Project>();
            for (Project projectListProjectToAttach : testProject.getProjectList()) {
                projectListProjectToAttach = em.getReference(projectListProjectToAttach.getClass(), projectListProjectToAttach.getId());
                attachedProjectList.add(projectListProjectToAttach);
            }
            testProject.setProjectList(attachedProjectList);
            List<UserTestProjectRole> attachedUserTestProjectRoleList = new ArrayList<UserTestProjectRole>();
            for (UserTestProjectRole userTestProjectRoleListUserTestProjectRoleToAttach : testProject.getUserTestProjectRoleList()) {
                userTestProjectRoleListUserTestProjectRoleToAttach = em.getReference(userTestProjectRoleListUserTestProjectRoleToAttach.getClass(), userTestProjectRoleListUserTestProjectRoleToAttach.getUserTestProjectRolePK());
                attachedUserTestProjectRoleList.add(userTestProjectRoleListUserTestProjectRoleToAttach);
            }
            testProject.setUserTestProjectRoleList(attachedUserTestProjectRoleList);
            List<TestPlan> attachedTestPlanList = new ArrayList<TestPlan>();
            for (TestPlan testPlanListTestPlanToAttach : testProject.getTestPlanList()) {
                testPlanListTestPlanToAttach = em.getReference(testPlanListTestPlanToAttach.getClass(), testPlanListTestPlanToAttach.getTestPlanPK());
                attachedTestPlanList.add(testPlanListTestPlanToAttach);
            }
            testProject.setTestPlanList(attachedTestPlanList);
            List<ProjectHasTestProject> attachedProjectHasTestProjectList = new ArrayList<ProjectHasTestProject>();
            for (ProjectHasTestProject projectHasTestProjectListProjectHasTestProjectToAttach : testProject.getProjectHasTestProjectList()) {
                projectHasTestProjectListProjectHasTestProjectToAttach = em.getReference(projectHasTestProjectListProjectHasTestProjectToAttach.getClass(), projectHasTestProjectListProjectHasTestProjectToAttach.getProjectHasTestProjectPK());
                attachedProjectHasTestProjectList.add(projectHasTestProjectListProjectHasTestProjectToAttach);
            }
            testProject.setProjectHasTestProjectList(attachedProjectHasTestProjectList);
            em.persist(testProject);
            for (Project projectListProject : testProject.getProjectList()) {
                projectListProject.getTestProjectList().add(testProject);
                projectListProject = em.merge(projectListProject);
            }
            for (UserTestProjectRole userTestProjectRoleListUserTestProjectRole : testProject.getUserTestProjectRoleList()) {
                TestProject oldTestProjectOfUserTestProjectRoleListUserTestProjectRole = userTestProjectRoleListUserTestProjectRole.getTestProject();
                userTestProjectRoleListUserTestProjectRole.setTestProject(testProject);
                userTestProjectRoleListUserTestProjectRole = em.merge(userTestProjectRoleListUserTestProjectRole);
                if (oldTestProjectOfUserTestProjectRoleListUserTestProjectRole != null) {
                    oldTestProjectOfUserTestProjectRoleListUserTestProjectRole.getUserTestProjectRoleList().remove(userTestProjectRoleListUserTestProjectRole);
                    oldTestProjectOfUserTestProjectRoleListUserTestProjectRole = em.merge(oldTestProjectOfUserTestProjectRoleListUserTestProjectRole);
                }
            }
            for (TestPlan testPlanListTestPlan : testProject.getTestPlanList()) {
                TestProject oldTestProjectOfTestPlanListTestPlan = testPlanListTestPlan.getTestProject();
                testPlanListTestPlan.setTestProject(testProject);
                testPlanListTestPlan = em.merge(testPlanListTestPlan);
                if (oldTestProjectOfTestPlanListTestPlan != null) {
                    oldTestProjectOfTestPlanListTestPlan.getTestPlanList().remove(testPlanListTestPlan);
                    oldTestProjectOfTestPlanListTestPlan = em.merge(oldTestProjectOfTestPlanListTestPlan);
                }
            }
            for (ProjectHasTestProject projectHasTestProjectListProjectHasTestProject : testProject.getProjectHasTestProjectList()) {
                TestProject oldTestProjectOfProjectHasTestProjectListProjectHasTestProject = projectHasTestProjectListProjectHasTestProject.getTestProject();
                projectHasTestProjectListProjectHasTestProject.setTestProject(testProject);
                projectHasTestProjectListProjectHasTestProject = em.merge(projectHasTestProjectListProjectHasTestProject);
                if (oldTestProjectOfProjectHasTestProjectListProjectHasTestProject != null) {
                    oldTestProjectOfProjectHasTestProjectListProjectHasTestProject.getProjectHasTestProjectList().remove(projectHasTestProjectListProjectHasTestProject);
                    oldTestProjectOfProjectHasTestProjectListProjectHasTestProject = em.merge(oldTestProjectOfProjectHasTestProjectListProjectHasTestProject);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TestProject testProject) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TestProject persistentTestProject = em.find(TestProject.class, testProject.getId());
            List<Project> projectListOld = persistentTestProject.getProjectList();
            List<Project> projectListNew = testProject.getProjectList();
            List<UserTestProjectRole> userTestProjectRoleListOld = persistentTestProject.getUserTestProjectRoleList();
            List<UserTestProjectRole> userTestProjectRoleListNew = testProject.getUserTestProjectRoleList();
            List<TestPlan> testPlanListOld = persistentTestProject.getTestPlanList();
            List<TestPlan> testPlanListNew = testProject.getTestPlanList();
            List<ProjectHasTestProject> projectHasTestProjectListOld = persistentTestProject.getProjectHasTestProjectList();
            List<ProjectHasTestProject> projectHasTestProjectListNew = testProject.getProjectHasTestProjectList();
            List<String> illegalOrphanMessages = null;
            for (UserTestProjectRole userTestProjectRoleListOldUserTestProjectRole : userTestProjectRoleListOld) {
                if (!userTestProjectRoleListNew.contains(userTestProjectRoleListOldUserTestProjectRole)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain UserTestProjectRole " + userTestProjectRoleListOldUserTestProjectRole + " since its testProject field is not nullable.");
                }
            }
            for (TestPlan testPlanListOldTestPlan : testPlanListOld) {
                if (!testPlanListNew.contains(testPlanListOldTestPlan)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain TestPlan " + testPlanListOldTestPlan + " since its testProject field is not nullable.");
                }
            }
            for (ProjectHasTestProject projectHasTestProjectListOldProjectHasTestProject : projectHasTestProjectListOld) {
                if (!projectHasTestProjectListNew.contains(projectHasTestProjectListOldProjectHasTestProject)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ProjectHasTestProject " + projectHasTestProjectListOldProjectHasTestProject + " since its testProject field is not nullable.");
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
            testProject.setProjectList(projectListNew);
            List<UserTestProjectRole> attachedUserTestProjectRoleListNew = new ArrayList<UserTestProjectRole>();
            for (UserTestProjectRole userTestProjectRoleListNewUserTestProjectRoleToAttach : userTestProjectRoleListNew) {
                userTestProjectRoleListNewUserTestProjectRoleToAttach = em.getReference(userTestProjectRoleListNewUserTestProjectRoleToAttach.getClass(), userTestProjectRoleListNewUserTestProjectRoleToAttach.getUserTestProjectRolePK());
                attachedUserTestProjectRoleListNew.add(userTestProjectRoleListNewUserTestProjectRoleToAttach);
            }
            userTestProjectRoleListNew = attachedUserTestProjectRoleListNew;
            testProject.setUserTestProjectRoleList(userTestProjectRoleListNew);
            List<TestPlan> attachedTestPlanListNew = new ArrayList<TestPlan>();
            for (TestPlan testPlanListNewTestPlanToAttach : testPlanListNew) {
                testPlanListNewTestPlanToAttach = em.getReference(testPlanListNewTestPlanToAttach.getClass(), testPlanListNewTestPlanToAttach.getTestPlanPK());
                attachedTestPlanListNew.add(testPlanListNewTestPlanToAttach);
            }
            testPlanListNew = attachedTestPlanListNew;
            testProject.setTestPlanList(testPlanListNew);
            List<ProjectHasTestProject> attachedProjectHasTestProjectListNew = new ArrayList<ProjectHasTestProject>();
            for (ProjectHasTestProject projectHasTestProjectListNewProjectHasTestProjectToAttach : projectHasTestProjectListNew) {
                projectHasTestProjectListNewProjectHasTestProjectToAttach = em.getReference(projectHasTestProjectListNewProjectHasTestProjectToAttach.getClass(), projectHasTestProjectListNewProjectHasTestProjectToAttach.getProjectHasTestProjectPK());
                attachedProjectHasTestProjectListNew.add(projectHasTestProjectListNewProjectHasTestProjectToAttach);
            }
            projectHasTestProjectListNew = attachedProjectHasTestProjectListNew;
            testProject.setProjectHasTestProjectList(projectHasTestProjectListNew);
            testProject = em.merge(testProject);
            for (Project projectListOldProject : projectListOld) {
                if (!projectListNew.contains(projectListOldProject)) {
                    projectListOldProject.getTestProjectList().remove(testProject);
                    projectListOldProject = em.merge(projectListOldProject);
                }
            }
            for (Project projectListNewProject : projectListNew) {
                if (!projectListOld.contains(projectListNewProject)) {
                    projectListNewProject.getTestProjectList().add(testProject);
                    projectListNewProject = em.merge(projectListNewProject);
                }
            }
            for (UserTestProjectRole userTestProjectRoleListNewUserTestProjectRole : userTestProjectRoleListNew) {
                if (!userTestProjectRoleListOld.contains(userTestProjectRoleListNewUserTestProjectRole)) {
                    TestProject oldTestProjectOfUserTestProjectRoleListNewUserTestProjectRole = userTestProjectRoleListNewUserTestProjectRole.getTestProject();
                    userTestProjectRoleListNewUserTestProjectRole.setTestProject(testProject);
                    userTestProjectRoleListNewUserTestProjectRole = em.merge(userTestProjectRoleListNewUserTestProjectRole);
                    if (oldTestProjectOfUserTestProjectRoleListNewUserTestProjectRole != null && !oldTestProjectOfUserTestProjectRoleListNewUserTestProjectRole.equals(testProject)) {
                        oldTestProjectOfUserTestProjectRoleListNewUserTestProjectRole.getUserTestProjectRoleList().remove(userTestProjectRoleListNewUserTestProjectRole);
                        oldTestProjectOfUserTestProjectRoleListNewUserTestProjectRole = em.merge(oldTestProjectOfUserTestProjectRoleListNewUserTestProjectRole);
                    }
                }
            }
            for (TestPlan testPlanListNewTestPlan : testPlanListNew) {
                if (!testPlanListOld.contains(testPlanListNewTestPlan)) {
                    TestProject oldTestProjectOfTestPlanListNewTestPlan = testPlanListNewTestPlan.getTestProject();
                    testPlanListNewTestPlan.setTestProject(testProject);
                    testPlanListNewTestPlan = em.merge(testPlanListNewTestPlan);
                    if (oldTestProjectOfTestPlanListNewTestPlan != null && !oldTestProjectOfTestPlanListNewTestPlan.equals(testProject)) {
                        oldTestProjectOfTestPlanListNewTestPlan.getTestPlanList().remove(testPlanListNewTestPlan);
                        oldTestProjectOfTestPlanListNewTestPlan = em.merge(oldTestProjectOfTestPlanListNewTestPlan);
                    }
                }
            }
            for (ProjectHasTestProject projectHasTestProjectListNewProjectHasTestProject : projectHasTestProjectListNew) {
                if (!projectHasTestProjectListOld.contains(projectHasTestProjectListNewProjectHasTestProject)) {
                    TestProject oldTestProjectOfProjectHasTestProjectListNewProjectHasTestProject = projectHasTestProjectListNewProjectHasTestProject.getTestProject();
                    projectHasTestProjectListNewProjectHasTestProject.setTestProject(testProject);
                    projectHasTestProjectListNewProjectHasTestProject = em.merge(projectHasTestProjectListNewProjectHasTestProject);
                    if (oldTestProjectOfProjectHasTestProjectListNewProjectHasTestProject != null && !oldTestProjectOfProjectHasTestProjectListNewProjectHasTestProject.equals(testProject)) {
                        oldTestProjectOfProjectHasTestProjectListNewProjectHasTestProject.getProjectHasTestProjectList().remove(projectHasTestProjectListNewProjectHasTestProject);
                        oldTestProjectOfProjectHasTestProjectListNewProjectHasTestProject = em.merge(oldTestProjectOfProjectHasTestProjectListNewProjectHasTestProject);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = testProject.getId();
                if (findTestProject(id) == null) {
                    throw new NonexistentEntityException("The testProject with id " + id + " no longer exists.");
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
            TestProject testProject;
            try {
                testProject = em.getReference(TestProject.class, id);
                testProject.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The testProject with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<UserTestProjectRole> userTestProjectRoleListOrphanCheck = testProject.getUserTestProjectRoleList();
            for (UserTestProjectRole userTestProjectRoleListOrphanCheckUserTestProjectRole : userTestProjectRoleListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This TestProject (" + testProject + ") cannot be destroyed since the UserTestProjectRole " + userTestProjectRoleListOrphanCheckUserTestProjectRole + " in its userTestProjectRoleList field has a non-nullable testProject field.");
            }
            List<TestPlan> testPlanListOrphanCheck = testProject.getTestPlanList();
            for (TestPlan testPlanListOrphanCheckTestPlan : testPlanListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This TestProject (" + testProject + ") cannot be destroyed since the TestPlan " + testPlanListOrphanCheckTestPlan + " in its testPlanList field has a non-nullable testProject field.");
            }
            List<ProjectHasTestProject> projectHasTestProjectListOrphanCheck = testProject.getProjectHasTestProjectList();
            for (ProjectHasTestProject projectHasTestProjectListOrphanCheckProjectHasTestProject : projectHasTestProjectListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This TestProject (" + testProject + ") cannot be destroyed since the ProjectHasTestProject " + projectHasTestProjectListOrphanCheckProjectHasTestProject + " in its projectHasTestProjectList field has a non-nullable testProject field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Project> projectList = testProject.getProjectList();
            for (Project projectListProject : projectList) {
                projectListProject.getTestProjectList().remove(testProject);
                projectListProject = em.merge(projectListProject);
            }
            em.remove(testProject);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TestProject> findTestProjectEntities() {
        return findTestProjectEntities(true, -1, -1);
    }

    public List<TestProject> findTestProjectEntities(int maxResults, int firstResult) {
        return findTestProjectEntities(false, maxResults, firstResult);
    }

    private List<TestProject> findTestProjectEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TestProject.class));
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

    public TestProject findTestProject(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TestProject.class, id);
        } finally {
            em.close();
        }
    }

    public int getTestProjectCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TestProject> rt = cq.from(TestProject.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
