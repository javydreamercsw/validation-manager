/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestPlanHasTest;
import com.validation.manager.core.db.TestPlanPK;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.UserTestPlanRole;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
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
public class TestPlanJpaController implements Serializable {

    public TestPlanJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TestPlan testPlan) throws PreexistingEntityException, Exception {
        if (testPlan.getTestPlanPK() == null) {
            testPlan.setTestPlanPK(new TestPlanPK());
        }
        if (testPlan.getTestPlanList() == null) {
            testPlan.setTestPlanList(new ArrayList<TestPlan>());
        }
        if (testPlan.getTestPlanHasTestList() == null) {
            testPlan.setTestPlanHasTestList(new ArrayList<TestPlanHasTest>());
        }
        if (testPlan.getUserTestPlanRoleList() == null) {
            testPlan.setUserTestPlanRoleList(new ArrayList<UserTestPlanRole>());
        }
        testPlan.getTestPlanPK().setTestProjectId(testPlan.getTestProject().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TestPlan testPlanRel = testPlan.getTestPlan();
            if (testPlanRel != null) {
                testPlanRel = em.getReference(testPlanRel.getClass(), testPlanRel.getTestPlanPK());
                testPlan.setTestPlan(testPlanRel);
            }
            TestProject testProject = testPlan.getTestProject();
            if (testProject != null) {
                testProject = em.getReference(testProject.getClass(), testProject.getId());
                testPlan.setTestProject(testProject);
            }
            List<TestPlan> attachedTestPlanList = new ArrayList<TestPlan>();
            for (TestPlan testPlanListTestPlanToAttach : testPlan.getTestPlanList()) {
                testPlanListTestPlanToAttach = em.getReference(testPlanListTestPlanToAttach.getClass(), testPlanListTestPlanToAttach.getTestPlanPK());
                attachedTestPlanList.add(testPlanListTestPlanToAttach);
            }
            testPlan.setTestPlanList(attachedTestPlanList);
            List<TestPlanHasTest> attachedTestPlanHasTestList = new ArrayList<TestPlanHasTest>();
            for (TestPlanHasTest testPlanHasTestListTestPlanHasTestToAttach : testPlan.getTestPlanHasTestList()) {
                testPlanHasTestListTestPlanHasTestToAttach = em.getReference(testPlanHasTestListTestPlanHasTestToAttach.getClass(), testPlanHasTestListTestPlanHasTestToAttach.getTestPlanHasTestPK());
                attachedTestPlanHasTestList.add(testPlanHasTestListTestPlanHasTestToAttach);
            }
            testPlan.setTestPlanHasTestList(attachedTestPlanHasTestList);
            List<UserTestPlanRole> attachedUserTestPlanRoleList = new ArrayList<UserTestPlanRole>();
            for (UserTestPlanRole userTestPlanRoleListUserTestPlanRoleToAttach : testPlan.getUserTestPlanRoleList()) {
                userTestPlanRoleListUserTestPlanRoleToAttach = em.getReference(userTestPlanRoleListUserTestPlanRoleToAttach.getClass(), userTestPlanRoleListUserTestPlanRoleToAttach.getUserTestPlanRolePK());
                attachedUserTestPlanRoleList.add(userTestPlanRoleListUserTestPlanRoleToAttach);
            }
            testPlan.setUserTestPlanRoleList(attachedUserTestPlanRoleList);
            em.persist(testPlan);
            if (testPlanRel != null) {
                testPlanRel.getTestPlanList().add(testPlan);
                testPlanRel = em.merge(testPlanRel);
            }
            if (testProject != null) {
                testProject.getTestPlanList().add(testPlan);
                testProject = em.merge(testProject);
            }
            for (TestPlan testPlanListTestPlan : testPlan.getTestPlanList()) {
                TestPlan oldTestPlanOfTestPlanListTestPlan = testPlanListTestPlan.getTestPlan();
                testPlanListTestPlan.setTestPlan(testPlan);
                testPlanListTestPlan = em.merge(testPlanListTestPlan);
                if (oldTestPlanOfTestPlanListTestPlan != null) {
                    oldTestPlanOfTestPlanListTestPlan.getTestPlanList().remove(testPlanListTestPlan);
                    oldTestPlanOfTestPlanListTestPlan = em.merge(oldTestPlanOfTestPlanListTestPlan);
                }
            }
            for (TestPlanHasTest testPlanHasTestListTestPlanHasTest : testPlan.getTestPlanHasTestList()) {
                TestPlan oldTestPlanOfTestPlanHasTestListTestPlanHasTest = testPlanHasTestListTestPlanHasTest.getTestPlan();
                testPlanHasTestListTestPlanHasTest.setTestPlan(testPlan);
                testPlanHasTestListTestPlanHasTest = em.merge(testPlanHasTestListTestPlanHasTest);
                if (oldTestPlanOfTestPlanHasTestListTestPlanHasTest != null) {
                    oldTestPlanOfTestPlanHasTestListTestPlanHasTest.getTestPlanHasTestList().remove(testPlanHasTestListTestPlanHasTest);
                    oldTestPlanOfTestPlanHasTestListTestPlanHasTest = em.merge(oldTestPlanOfTestPlanHasTestListTestPlanHasTest);
                }
            }
            for (UserTestPlanRole userTestPlanRoleListUserTestPlanRole : testPlan.getUserTestPlanRoleList()) {
                TestPlan oldTestPlanOfUserTestPlanRoleListUserTestPlanRole = userTestPlanRoleListUserTestPlanRole.getTestPlan();
                userTestPlanRoleListUserTestPlanRole.setTestPlan(testPlan);
                userTestPlanRoleListUserTestPlanRole = em.merge(userTestPlanRoleListUserTestPlanRole);
                if (oldTestPlanOfUserTestPlanRoleListUserTestPlanRole != null) {
                    oldTestPlanOfUserTestPlanRoleListUserTestPlanRole.getUserTestPlanRoleList().remove(userTestPlanRoleListUserTestPlanRole);
                    oldTestPlanOfUserTestPlanRoleListUserTestPlanRole = em.merge(oldTestPlanOfUserTestPlanRoleListUserTestPlanRole);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTestPlan(testPlan.getTestPlanPK()) != null) {
                throw new PreexistingEntityException("TestPlan " + testPlan + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TestPlan testPlan) throws IllegalOrphanException, NonexistentEntityException, Exception {
        testPlan.getTestPlanPK().setTestProjectId(testPlan.getTestProject().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TestPlan persistentTestPlan = em.find(TestPlan.class, testPlan.getTestPlanPK());
            TestPlan testPlanRelOld = persistentTestPlan.getTestPlan();
            TestPlan testPlanRelNew = testPlan.getTestPlan();
            TestProject testProjectOld = persistentTestPlan.getTestProject();
            TestProject testProjectNew = testPlan.getTestProject();
            List<TestPlan> testPlanListOld = persistentTestPlan.getTestPlanList();
            List<TestPlan> testPlanListNew = testPlan.getTestPlanList();
            List<TestPlanHasTest> testPlanHasTestListOld = persistentTestPlan.getTestPlanHasTestList();
            List<TestPlanHasTest> testPlanHasTestListNew = testPlan.getTestPlanHasTestList();
            List<UserTestPlanRole> userTestPlanRoleListOld = persistentTestPlan.getUserTestPlanRoleList();
            List<UserTestPlanRole> userTestPlanRoleListNew = testPlan.getUserTestPlanRoleList();
            List<String> illegalOrphanMessages = null;
            for (TestPlanHasTest testPlanHasTestListOldTestPlanHasTest : testPlanHasTestListOld) {
                if (!testPlanHasTestListNew.contains(testPlanHasTestListOldTestPlanHasTest)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain TestPlanHasTest " + testPlanHasTestListOldTestPlanHasTest + " since its testPlan field is not nullable.");
                }
            }
            for (UserTestPlanRole userTestPlanRoleListOldUserTestPlanRole : userTestPlanRoleListOld) {
                if (!userTestPlanRoleListNew.contains(userTestPlanRoleListOldUserTestPlanRole)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain UserTestPlanRole " + userTestPlanRoleListOldUserTestPlanRole + " since its testPlan field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (testPlanRelNew != null) {
                testPlanRelNew = em.getReference(testPlanRelNew.getClass(), testPlanRelNew.getTestPlanPK());
                testPlan.setTestPlan(testPlanRelNew);
            }
            if (testProjectNew != null) {
                testProjectNew = em.getReference(testProjectNew.getClass(), testProjectNew.getId());
                testPlan.setTestProject(testProjectNew);
            }
            List<TestPlan> attachedTestPlanListNew = new ArrayList<TestPlan>();
            for (TestPlan testPlanListNewTestPlanToAttach : testPlanListNew) {
                testPlanListNewTestPlanToAttach = em.getReference(testPlanListNewTestPlanToAttach.getClass(), testPlanListNewTestPlanToAttach.getTestPlanPK());
                attachedTestPlanListNew.add(testPlanListNewTestPlanToAttach);
            }
            testPlanListNew = attachedTestPlanListNew;
            testPlan.setTestPlanList(testPlanListNew);
            List<TestPlanHasTest> attachedTestPlanHasTestListNew = new ArrayList<TestPlanHasTest>();
            for (TestPlanHasTest testPlanHasTestListNewTestPlanHasTestToAttach : testPlanHasTestListNew) {
                testPlanHasTestListNewTestPlanHasTestToAttach = em.getReference(testPlanHasTestListNewTestPlanHasTestToAttach.getClass(), testPlanHasTestListNewTestPlanHasTestToAttach.getTestPlanHasTestPK());
                attachedTestPlanHasTestListNew.add(testPlanHasTestListNewTestPlanHasTestToAttach);
            }
            testPlanHasTestListNew = attachedTestPlanHasTestListNew;
            testPlan.setTestPlanHasTestList(testPlanHasTestListNew);
            List<UserTestPlanRole> attachedUserTestPlanRoleListNew = new ArrayList<UserTestPlanRole>();
            for (UserTestPlanRole userTestPlanRoleListNewUserTestPlanRoleToAttach : userTestPlanRoleListNew) {
                userTestPlanRoleListNewUserTestPlanRoleToAttach = em.getReference(userTestPlanRoleListNewUserTestPlanRoleToAttach.getClass(), userTestPlanRoleListNewUserTestPlanRoleToAttach.getUserTestPlanRolePK());
                attachedUserTestPlanRoleListNew.add(userTestPlanRoleListNewUserTestPlanRoleToAttach);
            }
            userTestPlanRoleListNew = attachedUserTestPlanRoleListNew;
            testPlan.setUserTestPlanRoleList(userTestPlanRoleListNew);
            testPlan = em.merge(testPlan);
            if (testPlanRelOld != null && !testPlanRelOld.equals(testPlanRelNew)) {
                testPlanRelOld.getTestPlanList().remove(testPlan);
                testPlanRelOld = em.merge(testPlanRelOld);
            }
            if (testPlanRelNew != null && !testPlanRelNew.equals(testPlanRelOld)) {
                testPlanRelNew.getTestPlanList().add(testPlan);
                testPlanRelNew = em.merge(testPlanRelNew);
            }
            if (testProjectOld != null && !testProjectOld.equals(testProjectNew)) {
                testProjectOld.getTestPlanList().remove(testPlan);
                testProjectOld = em.merge(testProjectOld);
            }
            if (testProjectNew != null && !testProjectNew.equals(testProjectOld)) {
                testProjectNew.getTestPlanList().add(testPlan);
                testProjectNew = em.merge(testProjectNew);
            }
            for (TestPlan testPlanListOldTestPlan : testPlanListOld) {
                if (!testPlanListNew.contains(testPlanListOldTestPlan)) {
                    testPlanListOldTestPlan.setTestPlan(null);
                    testPlanListOldTestPlan = em.merge(testPlanListOldTestPlan);
                }
            }
            for (TestPlan testPlanListNewTestPlan : testPlanListNew) {
                if (!testPlanListOld.contains(testPlanListNewTestPlan)) {
                    TestPlan oldTestPlanOfTestPlanListNewTestPlan = testPlanListNewTestPlan.getTestPlan();
                    testPlanListNewTestPlan.setTestPlan(testPlan);
                    testPlanListNewTestPlan = em.merge(testPlanListNewTestPlan);
                    if (oldTestPlanOfTestPlanListNewTestPlan != null && !oldTestPlanOfTestPlanListNewTestPlan.equals(testPlan)) {
                        oldTestPlanOfTestPlanListNewTestPlan.getTestPlanList().remove(testPlanListNewTestPlan);
                        oldTestPlanOfTestPlanListNewTestPlan = em.merge(oldTestPlanOfTestPlanListNewTestPlan);
                    }
                }
            }
            for (TestPlanHasTest testPlanHasTestListNewTestPlanHasTest : testPlanHasTestListNew) {
                if (!testPlanHasTestListOld.contains(testPlanHasTestListNewTestPlanHasTest)) {
                    TestPlan oldTestPlanOfTestPlanHasTestListNewTestPlanHasTest = testPlanHasTestListNewTestPlanHasTest.getTestPlan();
                    testPlanHasTestListNewTestPlanHasTest.setTestPlan(testPlan);
                    testPlanHasTestListNewTestPlanHasTest = em.merge(testPlanHasTestListNewTestPlanHasTest);
                    if (oldTestPlanOfTestPlanHasTestListNewTestPlanHasTest != null && !oldTestPlanOfTestPlanHasTestListNewTestPlanHasTest.equals(testPlan)) {
                        oldTestPlanOfTestPlanHasTestListNewTestPlanHasTest.getTestPlanHasTestList().remove(testPlanHasTestListNewTestPlanHasTest);
                        oldTestPlanOfTestPlanHasTestListNewTestPlanHasTest = em.merge(oldTestPlanOfTestPlanHasTestListNewTestPlanHasTest);
                    }
                }
            }
            for (UserTestPlanRole userTestPlanRoleListNewUserTestPlanRole : userTestPlanRoleListNew) {
                if (!userTestPlanRoleListOld.contains(userTestPlanRoleListNewUserTestPlanRole)) {
                    TestPlan oldTestPlanOfUserTestPlanRoleListNewUserTestPlanRole = userTestPlanRoleListNewUserTestPlanRole.getTestPlan();
                    userTestPlanRoleListNewUserTestPlanRole.setTestPlan(testPlan);
                    userTestPlanRoleListNewUserTestPlanRole = em.merge(userTestPlanRoleListNewUserTestPlanRole);
                    if (oldTestPlanOfUserTestPlanRoleListNewUserTestPlanRole != null && !oldTestPlanOfUserTestPlanRoleListNewUserTestPlanRole.equals(testPlan)) {
                        oldTestPlanOfUserTestPlanRoleListNewUserTestPlanRole.getUserTestPlanRoleList().remove(userTestPlanRoleListNewUserTestPlanRole);
                        oldTestPlanOfUserTestPlanRoleListNewUserTestPlanRole = em.merge(oldTestPlanOfUserTestPlanRoleListNewUserTestPlanRole);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                TestPlanPK id = testPlan.getTestPlanPK();
                if (findTestPlan(id) == null) {
                    throw new NonexistentEntityException("The testPlan with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(TestPlanPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TestPlan testPlan;
            try {
                testPlan = em.getReference(TestPlan.class, id);
                testPlan.getTestPlanPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The testPlan with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<TestPlanHasTest> testPlanHasTestListOrphanCheck = testPlan.getTestPlanHasTestList();
            for (TestPlanHasTest testPlanHasTestListOrphanCheckTestPlanHasTest : testPlanHasTestListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This TestPlan (" + testPlan + ") cannot be destroyed since the TestPlanHasTest " + testPlanHasTestListOrphanCheckTestPlanHasTest + " in its testPlanHasTestList field has a non-nullable testPlan field.");
            }
            List<UserTestPlanRole> userTestPlanRoleListOrphanCheck = testPlan.getUserTestPlanRoleList();
            for (UserTestPlanRole userTestPlanRoleListOrphanCheckUserTestPlanRole : userTestPlanRoleListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This TestPlan (" + testPlan + ") cannot be destroyed since the UserTestPlanRole " + userTestPlanRoleListOrphanCheckUserTestPlanRole + " in its userTestPlanRoleList field has a non-nullable testPlan field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            TestPlan testPlanRel = testPlan.getTestPlan();
            if (testPlanRel != null) {
                testPlanRel.getTestPlanList().remove(testPlan);
                testPlanRel = em.merge(testPlanRel);
            }
            TestProject testProject = testPlan.getTestProject();
            if (testProject != null) {
                testProject.getTestPlanList().remove(testPlan);
                testProject = em.merge(testProject);
            }
            List<TestPlan> testPlanList = testPlan.getTestPlanList();
            for (TestPlan testPlanListTestPlan : testPlanList) {
                testPlanListTestPlan.setTestPlan(null);
                testPlanListTestPlan = em.merge(testPlanListTestPlan);
            }
            em.remove(testPlan);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TestPlan> findTestPlanEntities() {
        return findTestPlanEntities(true, -1, -1);
    }

    public List<TestPlan> findTestPlanEntities(int maxResults, int firstResult) {
        return findTestPlanEntities(false, maxResults, firstResult);
    }

    private List<TestPlan> findTestPlanEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TestPlan.class));
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

    public TestPlan findTestPlan(TestPlanPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TestPlan.class, id);
        } finally {
            em.close();
        }
    }

    public int getTestPlanCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TestPlan> rt = cq.from(TestPlan.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
