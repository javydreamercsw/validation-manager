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
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.UserTestProjectRole;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
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
            testProject.setProjectList(new ArrayList<>());
        }
        if (testProject.getUserTestProjectRoleList() == null) {
            testProject.setUserTestProjectRoleList(new ArrayList<>());
        }
        if (testProject.getTestPlanList() == null) {
            testProject.setTestPlanList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Project> attachedProjectList = new ArrayList<>();
            for (Project projectListProjectToAttach : testProject.getProjectList()) {
                projectListProjectToAttach = em.getReference(projectListProjectToAttach.getClass(), projectListProjectToAttach.getId());
                attachedProjectList.add(projectListProjectToAttach);
            }
            testProject.setProjectList(attachedProjectList);
            List<UserTestProjectRole> attachedUserTestProjectRoleList = new ArrayList<>();
            for (UserTestProjectRole userTestProjectRoleListUserTestProjectRoleToAttach : testProject.getUserTestProjectRoleList()) {
                userTestProjectRoleListUserTestProjectRoleToAttach = em.getReference(userTestProjectRoleListUserTestProjectRoleToAttach.getClass(), userTestProjectRoleListUserTestProjectRoleToAttach.getUserTestProjectRolePK());
                attachedUserTestProjectRoleList.add(userTestProjectRoleListUserTestProjectRoleToAttach);
            }
            testProject.setUserTestProjectRoleList(attachedUserTestProjectRoleList);
            List<TestPlan> attachedTestPlanList = new ArrayList<>();
            for (TestPlan testPlanListTestPlanToAttach : testProject.getTestPlanList()) {
                testPlanListTestPlanToAttach = em.getReference(testPlanListTestPlanToAttach.getClass(), testPlanListTestPlanToAttach.getTestPlanPK());
                attachedTestPlanList.add(testPlanListTestPlanToAttach);
            }
            testProject.setTestPlanList(attachedTestPlanList);
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
            em.getTransaction().commit();
        }
        finally {
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
            List<String> illegalOrphanMessages = null;
            for (UserTestProjectRole userTestProjectRoleListOldUserTestProjectRole : userTestProjectRoleListOld) {
                if (!userTestProjectRoleListNew.contains(userTestProjectRoleListOldUserTestProjectRole)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain UserTestProjectRole " + userTestProjectRoleListOldUserTestProjectRole + " since its testProject field is not nullable.");
                }
            }
            for (TestPlan testPlanListOldTestPlan : testPlanListOld) {
                if (!testPlanListNew.contains(testPlanListOldTestPlan)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain TestPlan " + testPlanListOldTestPlan + " since its testProject field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Project> attachedProjectListNew = new ArrayList<>();
            for (Project projectListNewProjectToAttach : projectListNew) {
                projectListNewProjectToAttach = em.getReference(projectListNewProjectToAttach.getClass(), projectListNewProjectToAttach.getId());
                attachedProjectListNew.add(projectListNewProjectToAttach);
            }
            projectListNew = attachedProjectListNew;
            testProject.setProjectList(projectListNew);
            List<UserTestProjectRole> attachedUserTestProjectRoleListNew = new ArrayList<>();
            for (UserTestProjectRole userTestProjectRoleListNewUserTestProjectRoleToAttach : userTestProjectRoleListNew) {
                userTestProjectRoleListNewUserTestProjectRoleToAttach = em.getReference(userTestProjectRoleListNewUserTestProjectRoleToAttach.getClass(), userTestProjectRoleListNewUserTestProjectRoleToAttach.getUserTestProjectRolePK());
                attachedUserTestProjectRoleListNew.add(userTestProjectRoleListNewUserTestProjectRoleToAttach);
            }
            userTestProjectRoleListNew = attachedUserTestProjectRoleListNew;
            testProject.setUserTestProjectRoleList(userTestProjectRoleListNew);
            List<TestPlan> attachedTestPlanListNew = new ArrayList<>();
            for (TestPlan testPlanListNewTestPlanToAttach : testPlanListNew) {
                testPlanListNewTestPlanToAttach = em.getReference(testPlanListNewTestPlanToAttach.getClass(), testPlanListNewTestPlanToAttach.getTestPlanPK());
                attachedTestPlanListNew.add(testPlanListNewTestPlanToAttach);
            }
            testPlanListNew = attachedTestPlanListNew;
            testProject.setTestPlanList(testPlanListNew);
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
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = testProject.getId();
                if (findTestProject(id) == null) {
                    throw new NonexistentEntityException("The testProject with id " + id + " no longer exists.");
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
            TestProject testProject;
            try {
                testProject = em.getReference(TestProject.class, id);
                testProject.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The testProject with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<UserTestProjectRole> userTestProjectRoleListOrphanCheck = testProject.getUserTestProjectRoleList();
            for (UserTestProjectRole userTestProjectRoleListOrphanCheckUserTestProjectRole : userTestProjectRoleListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This TestProject (" + testProject + ") cannot be destroyed since the UserTestProjectRole " + userTestProjectRoleListOrphanCheckUserTestProjectRole + " in its userTestProjectRoleList field has a non-nullable testProject field.");
            }
            List<TestPlan> testPlanListOrphanCheck = testProject.getTestPlanList();
            for (TestPlan testPlanListOrphanCheckTestPlan : testPlanListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This TestProject (" + testProject + ") cannot be destroyed since the TestPlan " + testPlanListOrphanCheckTestPlan + " in its testPlanList field has a non-nullable testProject field.");
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
        }
        finally {
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
        }
        finally {
            em.close();
        }
    }

    public TestProject findTestProject(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TestProject.class, id);
        }
        finally {
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
        }
        finally {
            em.close();
        }
    }

}
