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
import com.validation.manager.core.db.UserStatus;
import com.validation.manager.core.db.Role;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.CorrectiveAction;
import com.validation.manager.core.db.UserModifiedRecord;
import com.validation.manager.core.db.UserHasInvestigation;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.UserAssigment;
import com.validation.manager.core.db.VmException;
import com.validation.manager.core.db.UserTestProjectRole;
import com.validation.manager.core.db.UserHasRootCause;
import com.validation.manager.core.db.UserTestPlanRole;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class VmUserJpaController implements Serializable {

    public VmUserJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(VmUser vmUser) throws PreexistingEntityException, Exception {
        if (vmUser.getRoleList() == null) {
            vmUser.setRoleList(new ArrayList<Role>());
        }
        if (vmUser.getCorrectiveActionList() == null) {
            vmUser.setCorrectiveActionList(new ArrayList<CorrectiveAction>());
        }
        if (vmUser.getUserModifiedRecordList() == null) {
            vmUser.setUserModifiedRecordList(new ArrayList<UserModifiedRecord>());
        }
        if (vmUser.getUserHasInvestigationList() == null) {
            vmUser.setUserHasInvestigationList(new ArrayList<UserHasInvestigation>());
        }
        if (vmUser.getTestCaseList() == null) {
            vmUser.setTestCaseList(new ArrayList<TestCase>());
        }
        if (vmUser.getUserAssigmentList() == null) {
            vmUser.setUserAssigmentList(new ArrayList<UserAssigment>());
        }
        if (vmUser.getUserAssigmentList1() == null) {
            vmUser.setUserAssigmentList1(new ArrayList<UserAssigment>());
        }
        if (vmUser.getVmExceptionList() == null) {
            vmUser.setVmExceptionList(new ArrayList<VmException>());
        }
        if (vmUser.getUserTestProjectRoleList() == null) {
            vmUser.setUserTestProjectRoleList(new ArrayList<UserTestProjectRole>());
        }
        if (vmUser.getUserHasRootCauseList() == null) {
            vmUser.setUserHasRootCauseList(new ArrayList<UserHasRootCause>());
        }
        if (vmUser.getUserTestPlanRoleList() == null) {
            vmUser.setUserTestPlanRoleList(new ArrayList<UserTestPlanRole>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserStatus userStatusId = vmUser.getUserStatusId();
            if (userStatusId != null) {
                userStatusId = em.getReference(userStatusId.getClass(), userStatusId.getId());
                vmUser.setUserStatusId(userStatusId);
            }
            List<Role> attachedRoleList = new ArrayList<Role>();
            for (Role roleListRoleToAttach : vmUser.getRoleList()) {
                roleListRoleToAttach = em.getReference(roleListRoleToAttach.getClass(), roleListRoleToAttach.getId());
                attachedRoleList.add(roleListRoleToAttach);
            }
            vmUser.setRoleList(attachedRoleList);
            List<CorrectiveAction> attachedCorrectiveActionList = new ArrayList<CorrectiveAction>();
            for (CorrectiveAction correctiveActionListCorrectiveActionToAttach : vmUser.getCorrectiveActionList()) {
                correctiveActionListCorrectiveActionToAttach = em.getReference(correctiveActionListCorrectiveActionToAttach.getClass(), correctiveActionListCorrectiveActionToAttach.getId());
                attachedCorrectiveActionList.add(correctiveActionListCorrectiveActionToAttach);
            }
            vmUser.setCorrectiveActionList(attachedCorrectiveActionList);
            List<UserModifiedRecord> attachedUserModifiedRecordList = new ArrayList<UserModifiedRecord>();
            for (UserModifiedRecord userModifiedRecordListUserModifiedRecordToAttach : vmUser.getUserModifiedRecordList()) {
                userModifiedRecordListUserModifiedRecordToAttach = em.getReference(userModifiedRecordListUserModifiedRecordToAttach.getClass(), userModifiedRecordListUserModifiedRecordToAttach.getUserModifiedRecordPK());
                attachedUserModifiedRecordList.add(userModifiedRecordListUserModifiedRecordToAttach);
            }
            vmUser.setUserModifiedRecordList(attachedUserModifiedRecordList);
            List<UserHasInvestigation> attachedUserHasInvestigationList = new ArrayList<UserHasInvestigation>();
            for (UserHasInvestigation userHasInvestigationListUserHasInvestigationToAttach : vmUser.getUserHasInvestigationList()) {
                userHasInvestigationListUserHasInvestigationToAttach = em.getReference(userHasInvestigationListUserHasInvestigationToAttach.getClass(), userHasInvestigationListUserHasInvestigationToAttach.getUserHasInvestigationPK());
                attachedUserHasInvestigationList.add(userHasInvestigationListUserHasInvestigationToAttach);
            }
            vmUser.setUserHasInvestigationList(attachedUserHasInvestigationList);
            List<TestCase> attachedTestCaseList = new ArrayList<TestCase>();
            for (TestCase testCaseListTestCaseToAttach : vmUser.getTestCaseList()) {
                testCaseListTestCaseToAttach = em.getReference(testCaseListTestCaseToAttach.getClass(), testCaseListTestCaseToAttach.getTestCasePK());
                attachedTestCaseList.add(testCaseListTestCaseToAttach);
            }
            vmUser.setTestCaseList(attachedTestCaseList);
            List<UserAssigment> attachedUserAssigmentList = new ArrayList<UserAssigment>();
            for (UserAssigment userAssigmentListUserAssigmentToAttach : vmUser.getUserAssigmentList()) {
                userAssigmentListUserAssigmentToAttach = em.getReference(userAssigmentListUserAssigmentToAttach.getClass(), userAssigmentListUserAssigmentToAttach.getUserAssigmentPK());
                attachedUserAssigmentList.add(userAssigmentListUserAssigmentToAttach);
            }
            vmUser.setUserAssigmentList(attachedUserAssigmentList);
            List<UserAssigment> attachedUserAssigmentList1 = new ArrayList<UserAssigment>();
            for (UserAssigment userAssigmentList1UserAssigmentToAttach : vmUser.getUserAssigmentList1()) {
                userAssigmentList1UserAssigmentToAttach = em.getReference(userAssigmentList1UserAssigmentToAttach.getClass(), userAssigmentList1UserAssigmentToAttach.getUserAssigmentPK());
                attachedUserAssigmentList1.add(userAssigmentList1UserAssigmentToAttach);
            }
            vmUser.setUserAssigmentList1(attachedUserAssigmentList1);
            List<VmException> attachedVmExceptionList = new ArrayList<VmException>();
            for (VmException vmExceptionListVmExceptionToAttach : vmUser.getVmExceptionList()) {
                vmExceptionListVmExceptionToAttach = em.getReference(vmExceptionListVmExceptionToAttach.getClass(), vmExceptionListVmExceptionToAttach.getVmExceptionPK());
                attachedVmExceptionList.add(vmExceptionListVmExceptionToAttach);
            }
            vmUser.setVmExceptionList(attachedVmExceptionList);
            List<UserTestProjectRole> attachedUserTestProjectRoleList = new ArrayList<UserTestProjectRole>();
            for (UserTestProjectRole userTestProjectRoleListUserTestProjectRoleToAttach : vmUser.getUserTestProjectRoleList()) {
                userTestProjectRoleListUserTestProjectRoleToAttach = em.getReference(userTestProjectRoleListUserTestProjectRoleToAttach.getClass(), userTestProjectRoleListUserTestProjectRoleToAttach.getUserTestProjectRolePK());
                attachedUserTestProjectRoleList.add(userTestProjectRoleListUserTestProjectRoleToAttach);
            }
            vmUser.setUserTestProjectRoleList(attachedUserTestProjectRoleList);
            List<UserHasRootCause> attachedUserHasRootCauseList = new ArrayList<UserHasRootCause>();
            for (UserHasRootCause userHasRootCauseListUserHasRootCauseToAttach : vmUser.getUserHasRootCauseList()) {
                userHasRootCauseListUserHasRootCauseToAttach = em.getReference(userHasRootCauseListUserHasRootCauseToAttach.getClass(), userHasRootCauseListUserHasRootCauseToAttach.getUserHasRootCausePK());
                attachedUserHasRootCauseList.add(userHasRootCauseListUserHasRootCauseToAttach);
            }
            vmUser.setUserHasRootCauseList(attachedUserHasRootCauseList);
            List<UserTestPlanRole> attachedUserTestPlanRoleList = new ArrayList<UserTestPlanRole>();
            for (UserTestPlanRole userTestPlanRoleListUserTestPlanRoleToAttach : vmUser.getUserTestPlanRoleList()) {
                userTestPlanRoleListUserTestPlanRoleToAttach = em.getReference(userTestPlanRoleListUserTestPlanRoleToAttach.getClass(), userTestPlanRoleListUserTestPlanRoleToAttach.getUserTestPlanRolePK());
                attachedUserTestPlanRoleList.add(userTestPlanRoleListUserTestPlanRoleToAttach);
            }
            vmUser.setUserTestPlanRoleList(attachedUserTestPlanRoleList);
            em.persist(vmUser);
            if (userStatusId != null) {
                userStatusId.getVmUserList().add(vmUser);
                userStatusId = em.merge(userStatusId);
            }
            for (Role roleListRole : vmUser.getRoleList()) {
                roleListRole.getVmUserList().add(vmUser);
                roleListRole = em.merge(roleListRole);
            }
            for (CorrectiveAction correctiveActionListCorrectiveAction : vmUser.getCorrectiveActionList()) {
                correctiveActionListCorrectiveAction.getVmUserList().add(vmUser);
                correctiveActionListCorrectiveAction = em.merge(correctiveActionListCorrectiveAction);
            }
            for (UserModifiedRecord userModifiedRecordListUserModifiedRecord : vmUser.getUserModifiedRecordList()) {
                VmUser oldVmUserOfUserModifiedRecordListUserModifiedRecord = userModifiedRecordListUserModifiedRecord.getVmUser();
                userModifiedRecordListUserModifiedRecord.setVmUser(vmUser);
                userModifiedRecordListUserModifiedRecord = em.merge(userModifiedRecordListUserModifiedRecord);
                if (oldVmUserOfUserModifiedRecordListUserModifiedRecord != null) {
                    oldVmUserOfUserModifiedRecordListUserModifiedRecord.getUserModifiedRecordList().remove(userModifiedRecordListUserModifiedRecord);
                    oldVmUserOfUserModifiedRecordListUserModifiedRecord = em.merge(oldVmUserOfUserModifiedRecordListUserModifiedRecord);
                }
            }
            for (UserHasInvestigation userHasInvestigationListUserHasInvestigation : vmUser.getUserHasInvestigationList()) {
                VmUser oldVmUserOfUserHasInvestigationListUserHasInvestigation = userHasInvestigationListUserHasInvestigation.getVmUser();
                userHasInvestigationListUserHasInvestigation.setVmUser(vmUser);
                userHasInvestigationListUserHasInvestigation = em.merge(userHasInvestigationListUserHasInvestigation);
                if (oldVmUserOfUserHasInvestigationListUserHasInvestigation != null) {
                    oldVmUserOfUserHasInvestigationListUserHasInvestigation.getUserHasInvestigationList().remove(userHasInvestigationListUserHasInvestigation);
                    oldVmUserOfUserHasInvestigationListUserHasInvestigation = em.merge(oldVmUserOfUserHasInvestigationListUserHasInvestigation);
                }
            }
            for (TestCase testCaseListTestCase : vmUser.getTestCaseList()) {
                VmUser oldAuthorIdOfTestCaseListTestCase = testCaseListTestCase.getAuthorId();
                testCaseListTestCase.setAuthorId(vmUser);
                testCaseListTestCase = em.merge(testCaseListTestCase);
                if (oldAuthorIdOfTestCaseListTestCase != null) {
                    oldAuthorIdOfTestCaseListTestCase.getTestCaseList().remove(testCaseListTestCase);
                    oldAuthorIdOfTestCaseListTestCase = em.merge(oldAuthorIdOfTestCaseListTestCase);
                }
            }
            for (UserAssigment userAssigmentListUserAssigment : vmUser.getUserAssigmentList()) {
                VmUser oldVmUserOfUserAssigmentListUserAssigment = userAssigmentListUserAssigment.getVmUser();
                userAssigmentListUserAssigment.setVmUser(vmUser);
                userAssigmentListUserAssigment = em.merge(userAssigmentListUserAssigment);
                if (oldVmUserOfUserAssigmentListUserAssigment != null) {
                    oldVmUserOfUserAssigmentListUserAssigment.getUserAssigmentList().remove(userAssigmentListUserAssigment);
                    oldVmUserOfUserAssigmentListUserAssigment = em.merge(oldVmUserOfUserAssigmentListUserAssigment);
                }
            }
            for (UserAssigment userAssigmentList1UserAssigment : vmUser.getUserAssigmentList1()) {
                VmUser oldAssigneeIdOfUserAssigmentList1UserAssigment = userAssigmentList1UserAssigment.getAssigneeId();
                userAssigmentList1UserAssigment.setAssigneeId(vmUser);
                userAssigmentList1UserAssigment = em.merge(userAssigmentList1UserAssigment);
                if (oldAssigneeIdOfUserAssigmentList1UserAssigment != null) {
                    oldAssigneeIdOfUserAssigmentList1UserAssigment.getUserAssigmentList1().remove(userAssigmentList1UserAssigment);
                    oldAssigneeIdOfUserAssigmentList1UserAssigment = em.merge(oldAssigneeIdOfUserAssigmentList1UserAssigment);
                }
            }
            for (VmException vmExceptionListVmException : vmUser.getVmExceptionList()) {
                VmUser oldVmUserOfVmExceptionListVmException = vmExceptionListVmException.getVmUser();
                vmExceptionListVmException.setVmUser(vmUser);
                vmExceptionListVmException = em.merge(vmExceptionListVmException);
                if (oldVmUserOfVmExceptionListVmException != null) {
                    oldVmUserOfVmExceptionListVmException.getVmExceptionList().remove(vmExceptionListVmException);
                    oldVmUserOfVmExceptionListVmException = em.merge(oldVmUserOfVmExceptionListVmException);
                }
            }
            for (UserTestProjectRole userTestProjectRoleListUserTestProjectRole : vmUser.getUserTestProjectRoleList()) {
                VmUser oldVmUserOfUserTestProjectRoleListUserTestProjectRole = userTestProjectRoleListUserTestProjectRole.getVmUser();
                userTestProjectRoleListUserTestProjectRole.setVmUser(vmUser);
                userTestProjectRoleListUserTestProjectRole = em.merge(userTestProjectRoleListUserTestProjectRole);
                if (oldVmUserOfUserTestProjectRoleListUserTestProjectRole != null) {
                    oldVmUserOfUserTestProjectRoleListUserTestProjectRole.getUserTestProjectRoleList().remove(userTestProjectRoleListUserTestProjectRole);
                    oldVmUserOfUserTestProjectRoleListUserTestProjectRole = em.merge(oldVmUserOfUserTestProjectRoleListUserTestProjectRole);
                }
            }
            for (UserHasRootCause userHasRootCauseListUserHasRootCause : vmUser.getUserHasRootCauseList()) {
                VmUser oldVmUserOfUserHasRootCauseListUserHasRootCause = userHasRootCauseListUserHasRootCause.getVmUser();
                userHasRootCauseListUserHasRootCause.setVmUser(vmUser);
                userHasRootCauseListUserHasRootCause = em.merge(userHasRootCauseListUserHasRootCause);
                if (oldVmUserOfUserHasRootCauseListUserHasRootCause != null) {
                    oldVmUserOfUserHasRootCauseListUserHasRootCause.getUserHasRootCauseList().remove(userHasRootCauseListUserHasRootCause);
                    oldVmUserOfUserHasRootCauseListUserHasRootCause = em.merge(oldVmUserOfUserHasRootCauseListUserHasRootCause);
                }
            }
            for (UserTestPlanRole userTestPlanRoleListUserTestPlanRole : vmUser.getUserTestPlanRoleList()) {
                VmUser oldVmUserOfUserTestPlanRoleListUserTestPlanRole = userTestPlanRoleListUserTestPlanRole.getVmUser();
                userTestPlanRoleListUserTestPlanRole.setVmUser(vmUser);
                userTestPlanRoleListUserTestPlanRole = em.merge(userTestPlanRoleListUserTestPlanRole);
                if (oldVmUserOfUserTestPlanRoleListUserTestPlanRole != null) {
                    oldVmUserOfUserTestPlanRoleListUserTestPlanRole.getUserTestPlanRoleList().remove(userTestPlanRoleListUserTestPlanRole);
                    oldVmUserOfUserTestPlanRoleListUserTestPlanRole = em.merge(oldVmUserOfUserTestPlanRoleListUserTestPlanRole);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findVmUser(vmUser.getId()) != null) {
                throw new PreexistingEntityException("VmUser " + vmUser + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(VmUser vmUser) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            VmUser persistentVmUser = em.find(VmUser.class, vmUser.getId());
            UserStatus userStatusIdOld = persistentVmUser.getUserStatusId();
            UserStatus userStatusIdNew = vmUser.getUserStatusId();
            List<Role> roleListOld = persistentVmUser.getRoleList();
            List<Role> roleListNew = vmUser.getRoleList();
            List<CorrectiveAction> correctiveActionListOld = persistentVmUser.getCorrectiveActionList();
            List<CorrectiveAction> correctiveActionListNew = vmUser.getCorrectiveActionList();
            List<UserModifiedRecord> userModifiedRecordListOld = persistentVmUser.getUserModifiedRecordList();
            List<UserModifiedRecord> userModifiedRecordListNew = vmUser.getUserModifiedRecordList();
            List<UserHasInvestigation> userHasInvestigationListOld = persistentVmUser.getUserHasInvestigationList();
            List<UserHasInvestigation> userHasInvestigationListNew = vmUser.getUserHasInvestigationList();
            List<TestCase> testCaseListOld = persistentVmUser.getTestCaseList();
            List<TestCase> testCaseListNew = vmUser.getTestCaseList();
            List<UserAssigment> userAssigmentListOld = persistentVmUser.getUserAssigmentList();
            List<UserAssigment> userAssigmentListNew = vmUser.getUserAssigmentList();
            List<UserAssigment> userAssigmentList1Old = persistentVmUser.getUserAssigmentList1();
            List<UserAssigment> userAssigmentList1New = vmUser.getUserAssigmentList1();
            List<VmException> vmExceptionListOld = persistentVmUser.getVmExceptionList();
            List<VmException> vmExceptionListNew = vmUser.getVmExceptionList();
            List<UserTestProjectRole> userTestProjectRoleListOld = persistentVmUser.getUserTestProjectRoleList();
            List<UserTestProjectRole> userTestProjectRoleListNew = vmUser.getUserTestProjectRoleList();
            List<UserHasRootCause> userHasRootCauseListOld = persistentVmUser.getUserHasRootCauseList();
            List<UserHasRootCause> userHasRootCauseListNew = vmUser.getUserHasRootCauseList();
            List<UserTestPlanRole> userTestPlanRoleListOld = persistentVmUser.getUserTestPlanRoleList();
            List<UserTestPlanRole> userTestPlanRoleListNew = vmUser.getUserTestPlanRoleList();
            List<String> illegalOrphanMessages = null;
            for (UserModifiedRecord userModifiedRecordListOldUserModifiedRecord : userModifiedRecordListOld) {
                if (!userModifiedRecordListNew.contains(userModifiedRecordListOldUserModifiedRecord)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain UserModifiedRecord " + userModifiedRecordListOldUserModifiedRecord + " since its vmUser field is not nullable.");
                }
            }
            for (UserHasInvestigation userHasInvestigationListOldUserHasInvestigation : userHasInvestigationListOld) {
                if (!userHasInvestigationListNew.contains(userHasInvestigationListOldUserHasInvestigation)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain UserHasInvestigation " + userHasInvestigationListOldUserHasInvestigation + " since its vmUser field is not nullable.");
                }
            }
            for (UserAssigment userAssigmentListOldUserAssigment : userAssigmentListOld) {
                if (!userAssigmentListNew.contains(userAssigmentListOldUserAssigment)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain UserAssigment " + userAssigmentListOldUserAssigment + " since its vmUser field is not nullable.");
                }
            }
            for (VmException vmExceptionListOldVmException : vmExceptionListOld) {
                if (!vmExceptionListNew.contains(vmExceptionListOldVmException)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain VmException " + vmExceptionListOldVmException + " since its vmUser field is not nullable.");
                }
            }
            for (UserTestProjectRole userTestProjectRoleListOldUserTestProjectRole : userTestProjectRoleListOld) {
                if (!userTestProjectRoleListNew.contains(userTestProjectRoleListOldUserTestProjectRole)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain UserTestProjectRole " + userTestProjectRoleListOldUserTestProjectRole + " since its vmUser field is not nullable.");
                }
            }
            for (UserHasRootCause userHasRootCauseListOldUserHasRootCause : userHasRootCauseListOld) {
                if (!userHasRootCauseListNew.contains(userHasRootCauseListOldUserHasRootCause)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain UserHasRootCause " + userHasRootCauseListOldUserHasRootCause + " since its vmUser field is not nullable.");
                }
            }
            for (UserTestPlanRole userTestPlanRoleListOldUserTestPlanRole : userTestPlanRoleListOld) {
                if (!userTestPlanRoleListNew.contains(userTestPlanRoleListOldUserTestPlanRole)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain UserTestPlanRole " + userTestPlanRoleListOldUserTestPlanRole + " since its vmUser field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (userStatusIdNew != null) {
                userStatusIdNew = em.getReference(userStatusIdNew.getClass(), userStatusIdNew.getId());
                vmUser.setUserStatusId(userStatusIdNew);
            }
            List<Role> attachedRoleListNew = new ArrayList<Role>();
            for (Role roleListNewRoleToAttach : roleListNew) {
                roleListNewRoleToAttach = em.getReference(roleListNewRoleToAttach.getClass(), roleListNewRoleToAttach.getId());
                attachedRoleListNew.add(roleListNewRoleToAttach);
            }
            roleListNew = attachedRoleListNew;
            vmUser.setRoleList(roleListNew);
            List<CorrectiveAction> attachedCorrectiveActionListNew = new ArrayList<CorrectiveAction>();
            for (CorrectiveAction correctiveActionListNewCorrectiveActionToAttach : correctiveActionListNew) {
                correctiveActionListNewCorrectiveActionToAttach = em.getReference(correctiveActionListNewCorrectiveActionToAttach.getClass(), correctiveActionListNewCorrectiveActionToAttach.getId());
                attachedCorrectiveActionListNew.add(correctiveActionListNewCorrectiveActionToAttach);
            }
            correctiveActionListNew = attachedCorrectiveActionListNew;
            vmUser.setCorrectiveActionList(correctiveActionListNew);
            List<UserModifiedRecord> attachedUserModifiedRecordListNew = new ArrayList<UserModifiedRecord>();
            for (UserModifiedRecord userModifiedRecordListNewUserModifiedRecordToAttach : userModifiedRecordListNew) {
                userModifiedRecordListNewUserModifiedRecordToAttach = em.getReference(userModifiedRecordListNewUserModifiedRecordToAttach.getClass(), userModifiedRecordListNewUserModifiedRecordToAttach.getUserModifiedRecordPK());
                attachedUserModifiedRecordListNew.add(userModifiedRecordListNewUserModifiedRecordToAttach);
            }
            userModifiedRecordListNew = attachedUserModifiedRecordListNew;
            vmUser.setUserModifiedRecordList(userModifiedRecordListNew);
            List<UserHasInvestigation> attachedUserHasInvestigationListNew = new ArrayList<UserHasInvestigation>();
            for (UserHasInvestigation userHasInvestigationListNewUserHasInvestigationToAttach : userHasInvestigationListNew) {
                userHasInvestigationListNewUserHasInvestigationToAttach = em.getReference(userHasInvestigationListNewUserHasInvestigationToAttach.getClass(), userHasInvestigationListNewUserHasInvestigationToAttach.getUserHasInvestigationPK());
                attachedUserHasInvestigationListNew.add(userHasInvestigationListNewUserHasInvestigationToAttach);
            }
            userHasInvestigationListNew = attachedUserHasInvestigationListNew;
            vmUser.setUserHasInvestigationList(userHasInvestigationListNew);
            List<TestCase> attachedTestCaseListNew = new ArrayList<TestCase>();
            for (TestCase testCaseListNewTestCaseToAttach : testCaseListNew) {
                testCaseListNewTestCaseToAttach = em.getReference(testCaseListNewTestCaseToAttach.getClass(), testCaseListNewTestCaseToAttach.getTestCasePK());
                attachedTestCaseListNew.add(testCaseListNewTestCaseToAttach);
            }
            testCaseListNew = attachedTestCaseListNew;
            vmUser.setTestCaseList(testCaseListNew);
            List<UserAssigment> attachedUserAssigmentListNew = new ArrayList<UserAssigment>();
            for (UserAssigment userAssigmentListNewUserAssigmentToAttach : userAssigmentListNew) {
                userAssigmentListNewUserAssigmentToAttach = em.getReference(userAssigmentListNewUserAssigmentToAttach.getClass(), userAssigmentListNewUserAssigmentToAttach.getUserAssigmentPK());
                attachedUserAssigmentListNew.add(userAssigmentListNewUserAssigmentToAttach);
            }
            userAssigmentListNew = attachedUserAssigmentListNew;
            vmUser.setUserAssigmentList(userAssigmentListNew);
            List<UserAssigment> attachedUserAssigmentList1New = new ArrayList<UserAssigment>();
            for (UserAssigment userAssigmentList1NewUserAssigmentToAttach : userAssigmentList1New) {
                userAssigmentList1NewUserAssigmentToAttach = em.getReference(userAssigmentList1NewUserAssigmentToAttach.getClass(), userAssigmentList1NewUserAssigmentToAttach.getUserAssigmentPK());
                attachedUserAssigmentList1New.add(userAssigmentList1NewUserAssigmentToAttach);
            }
            userAssigmentList1New = attachedUserAssigmentList1New;
            vmUser.setUserAssigmentList1(userAssigmentList1New);
            List<VmException> attachedVmExceptionListNew = new ArrayList<VmException>();
            for (VmException vmExceptionListNewVmExceptionToAttach : vmExceptionListNew) {
                vmExceptionListNewVmExceptionToAttach = em.getReference(vmExceptionListNewVmExceptionToAttach.getClass(), vmExceptionListNewVmExceptionToAttach.getVmExceptionPK());
                attachedVmExceptionListNew.add(vmExceptionListNewVmExceptionToAttach);
            }
            vmExceptionListNew = attachedVmExceptionListNew;
            vmUser.setVmExceptionList(vmExceptionListNew);
            List<UserTestProjectRole> attachedUserTestProjectRoleListNew = new ArrayList<UserTestProjectRole>();
            for (UserTestProjectRole userTestProjectRoleListNewUserTestProjectRoleToAttach : userTestProjectRoleListNew) {
                userTestProjectRoleListNewUserTestProjectRoleToAttach = em.getReference(userTestProjectRoleListNewUserTestProjectRoleToAttach.getClass(), userTestProjectRoleListNewUserTestProjectRoleToAttach.getUserTestProjectRolePK());
                attachedUserTestProjectRoleListNew.add(userTestProjectRoleListNewUserTestProjectRoleToAttach);
            }
            userTestProjectRoleListNew = attachedUserTestProjectRoleListNew;
            vmUser.setUserTestProjectRoleList(userTestProjectRoleListNew);
            List<UserHasRootCause> attachedUserHasRootCauseListNew = new ArrayList<UserHasRootCause>();
            for (UserHasRootCause userHasRootCauseListNewUserHasRootCauseToAttach : userHasRootCauseListNew) {
                userHasRootCauseListNewUserHasRootCauseToAttach = em.getReference(userHasRootCauseListNewUserHasRootCauseToAttach.getClass(), userHasRootCauseListNewUserHasRootCauseToAttach.getUserHasRootCausePK());
                attachedUserHasRootCauseListNew.add(userHasRootCauseListNewUserHasRootCauseToAttach);
            }
            userHasRootCauseListNew = attachedUserHasRootCauseListNew;
            vmUser.setUserHasRootCauseList(userHasRootCauseListNew);
            List<UserTestPlanRole> attachedUserTestPlanRoleListNew = new ArrayList<UserTestPlanRole>();
            for (UserTestPlanRole userTestPlanRoleListNewUserTestPlanRoleToAttach : userTestPlanRoleListNew) {
                userTestPlanRoleListNewUserTestPlanRoleToAttach = em.getReference(userTestPlanRoleListNewUserTestPlanRoleToAttach.getClass(), userTestPlanRoleListNewUserTestPlanRoleToAttach.getUserTestPlanRolePK());
                attachedUserTestPlanRoleListNew.add(userTestPlanRoleListNewUserTestPlanRoleToAttach);
            }
            userTestPlanRoleListNew = attachedUserTestPlanRoleListNew;
            vmUser.setUserTestPlanRoleList(userTestPlanRoleListNew);
            vmUser = em.merge(vmUser);
            if (userStatusIdOld != null && !userStatusIdOld.equals(userStatusIdNew)) {
                userStatusIdOld.getVmUserList().remove(vmUser);
                userStatusIdOld = em.merge(userStatusIdOld);
            }
            if (userStatusIdNew != null && !userStatusIdNew.equals(userStatusIdOld)) {
                userStatusIdNew.getVmUserList().add(vmUser);
                userStatusIdNew = em.merge(userStatusIdNew);
            }
            for (Role roleListOldRole : roleListOld) {
                if (!roleListNew.contains(roleListOldRole)) {
                    roleListOldRole.getVmUserList().remove(vmUser);
                    roleListOldRole = em.merge(roleListOldRole);
                }
            }
            for (Role roleListNewRole : roleListNew) {
                if (!roleListOld.contains(roleListNewRole)) {
                    roleListNewRole.getVmUserList().add(vmUser);
                    roleListNewRole = em.merge(roleListNewRole);
                }
            }
            for (CorrectiveAction correctiveActionListOldCorrectiveAction : correctiveActionListOld) {
                if (!correctiveActionListNew.contains(correctiveActionListOldCorrectiveAction)) {
                    correctiveActionListOldCorrectiveAction.getVmUserList().remove(vmUser);
                    correctiveActionListOldCorrectiveAction = em.merge(correctiveActionListOldCorrectiveAction);
                }
            }
            for (CorrectiveAction correctiveActionListNewCorrectiveAction : correctiveActionListNew) {
                if (!correctiveActionListOld.contains(correctiveActionListNewCorrectiveAction)) {
                    correctiveActionListNewCorrectiveAction.getVmUserList().add(vmUser);
                    correctiveActionListNewCorrectiveAction = em.merge(correctiveActionListNewCorrectiveAction);
                }
            }
            for (UserModifiedRecord userModifiedRecordListNewUserModifiedRecord : userModifiedRecordListNew) {
                if (!userModifiedRecordListOld.contains(userModifiedRecordListNewUserModifiedRecord)) {
                    VmUser oldVmUserOfUserModifiedRecordListNewUserModifiedRecord = userModifiedRecordListNewUserModifiedRecord.getVmUser();
                    userModifiedRecordListNewUserModifiedRecord.setVmUser(vmUser);
                    userModifiedRecordListNewUserModifiedRecord = em.merge(userModifiedRecordListNewUserModifiedRecord);
                    if (oldVmUserOfUserModifiedRecordListNewUserModifiedRecord != null && !oldVmUserOfUserModifiedRecordListNewUserModifiedRecord.equals(vmUser)) {
                        oldVmUserOfUserModifiedRecordListNewUserModifiedRecord.getUserModifiedRecordList().remove(userModifiedRecordListNewUserModifiedRecord);
                        oldVmUserOfUserModifiedRecordListNewUserModifiedRecord = em.merge(oldVmUserOfUserModifiedRecordListNewUserModifiedRecord);
                    }
                }
            }
            for (UserHasInvestigation userHasInvestigationListNewUserHasInvestigation : userHasInvestigationListNew) {
                if (!userHasInvestigationListOld.contains(userHasInvestigationListNewUserHasInvestigation)) {
                    VmUser oldVmUserOfUserHasInvestigationListNewUserHasInvestigation = userHasInvestigationListNewUserHasInvestigation.getVmUser();
                    userHasInvestigationListNewUserHasInvestigation.setVmUser(vmUser);
                    userHasInvestigationListNewUserHasInvestigation = em.merge(userHasInvestigationListNewUserHasInvestigation);
                    if (oldVmUserOfUserHasInvestigationListNewUserHasInvestigation != null && !oldVmUserOfUserHasInvestigationListNewUserHasInvestigation.equals(vmUser)) {
                        oldVmUserOfUserHasInvestigationListNewUserHasInvestigation.getUserHasInvestigationList().remove(userHasInvestigationListNewUserHasInvestigation);
                        oldVmUserOfUserHasInvestigationListNewUserHasInvestigation = em.merge(oldVmUserOfUserHasInvestigationListNewUserHasInvestigation);
                    }
                }
            }
            for (TestCase testCaseListOldTestCase : testCaseListOld) {
                if (!testCaseListNew.contains(testCaseListOldTestCase)) {
                    testCaseListOldTestCase.setAuthorId(null);
                    testCaseListOldTestCase = em.merge(testCaseListOldTestCase);
                }
            }
            for (TestCase testCaseListNewTestCase : testCaseListNew) {
                if (!testCaseListOld.contains(testCaseListNewTestCase)) {
                    VmUser oldAuthorIdOfTestCaseListNewTestCase = testCaseListNewTestCase.getAuthorId();
                    testCaseListNewTestCase.setAuthorId(vmUser);
                    testCaseListNewTestCase = em.merge(testCaseListNewTestCase);
                    if (oldAuthorIdOfTestCaseListNewTestCase != null && !oldAuthorIdOfTestCaseListNewTestCase.equals(vmUser)) {
                        oldAuthorIdOfTestCaseListNewTestCase.getTestCaseList().remove(testCaseListNewTestCase);
                        oldAuthorIdOfTestCaseListNewTestCase = em.merge(oldAuthorIdOfTestCaseListNewTestCase);
                    }
                }
            }
            for (UserAssigment userAssigmentListNewUserAssigment : userAssigmentListNew) {
                if (!userAssigmentListOld.contains(userAssigmentListNewUserAssigment)) {
                    VmUser oldVmUserOfUserAssigmentListNewUserAssigment = userAssigmentListNewUserAssigment.getVmUser();
                    userAssigmentListNewUserAssigment.setVmUser(vmUser);
                    userAssigmentListNewUserAssigment = em.merge(userAssigmentListNewUserAssigment);
                    if (oldVmUserOfUserAssigmentListNewUserAssigment != null && !oldVmUserOfUserAssigmentListNewUserAssigment.equals(vmUser)) {
                        oldVmUserOfUserAssigmentListNewUserAssigment.getUserAssigmentList().remove(userAssigmentListNewUserAssigment);
                        oldVmUserOfUserAssigmentListNewUserAssigment = em.merge(oldVmUserOfUserAssigmentListNewUserAssigment);
                    }
                }
            }
            for (UserAssigment userAssigmentList1OldUserAssigment : userAssigmentList1Old) {
                if (!userAssigmentList1New.contains(userAssigmentList1OldUserAssigment)) {
                    userAssigmentList1OldUserAssigment.setAssigneeId(null);
                    userAssigmentList1OldUserAssigment = em.merge(userAssigmentList1OldUserAssigment);
                }
            }
            for (UserAssigment userAssigmentList1NewUserAssigment : userAssigmentList1New) {
                if (!userAssigmentList1Old.contains(userAssigmentList1NewUserAssigment)) {
                    VmUser oldAssigneeIdOfUserAssigmentList1NewUserAssigment = userAssigmentList1NewUserAssigment.getAssigneeId();
                    userAssigmentList1NewUserAssigment.setAssigneeId(vmUser);
                    userAssigmentList1NewUserAssigment = em.merge(userAssigmentList1NewUserAssigment);
                    if (oldAssigneeIdOfUserAssigmentList1NewUserAssigment != null && !oldAssigneeIdOfUserAssigmentList1NewUserAssigment.equals(vmUser)) {
                        oldAssigneeIdOfUserAssigmentList1NewUserAssigment.getUserAssigmentList1().remove(userAssigmentList1NewUserAssigment);
                        oldAssigneeIdOfUserAssigmentList1NewUserAssigment = em.merge(oldAssigneeIdOfUserAssigmentList1NewUserAssigment);
                    }
                }
            }
            for (VmException vmExceptionListNewVmException : vmExceptionListNew) {
                if (!vmExceptionListOld.contains(vmExceptionListNewVmException)) {
                    VmUser oldVmUserOfVmExceptionListNewVmException = vmExceptionListNewVmException.getVmUser();
                    vmExceptionListNewVmException.setVmUser(vmUser);
                    vmExceptionListNewVmException = em.merge(vmExceptionListNewVmException);
                    if (oldVmUserOfVmExceptionListNewVmException != null && !oldVmUserOfVmExceptionListNewVmException.equals(vmUser)) {
                        oldVmUserOfVmExceptionListNewVmException.getVmExceptionList().remove(vmExceptionListNewVmException);
                        oldVmUserOfVmExceptionListNewVmException = em.merge(oldVmUserOfVmExceptionListNewVmException);
                    }
                }
            }
            for (UserTestProjectRole userTestProjectRoleListNewUserTestProjectRole : userTestProjectRoleListNew) {
                if (!userTestProjectRoleListOld.contains(userTestProjectRoleListNewUserTestProjectRole)) {
                    VmUser oldVmUserOfUserTestProjectRoleListNewUserTestProjectRole = userTestProjectRoleListNewUserTestProjectRole.getVmUser();
                    userTestProjectRoleListNewUserTestProjectRole.setVmUser(vmUser);
                    userTestProjectRoleListNewUserTestProjectRole = em.merge(userTestProjectRoleListNewUserTestProjectRole);
                    if (oldVmUserOfUserTestProjectRoleListNewUserTestProjectRole != null && !oldVmUserOfUserTestProjectRoleListNewUserTestProjectRole.equals(vmUser)) {
                        oldVmUserOfUserTestProjectRoleListNewUserTestProjectRole.getUserTestProjectRoleList().remove(userTestProjectRoleListNewUserTestProjectRole);
                        oldVmUserOfUserTestProjectRoleListNewUserTestProjectRole = em.merge(oldVmUserOfUserTestProjectRoleListNewUserTestProjectRole);
                    }
                }
            }
            for (UserHasRootCause userHasRootCauseListNewUserHasRootCause : userHasRootCauseListNew) {
                if (!userHasRootCauseListOld.contains(userHasRootCauseListNewUserHasRootCause)) {
                    VmUser oldVmUserOfUserHasRootCauseListNewUserHasRootCause = userHasRootCauseListNewUserHasRootCause.getVmUser();
                    userHasRootCauseListNewUserHasRootCause.setVmUser(vmUser);
                    userHasRootCauseListNewUserHasRootCause = em.merge(userHasRootCauseListNewUserHasRootCause);
                    if (oldVmUserOfUserHasRootCauseListNewUserHasRootCause != null && !oldVmUserOfUserHasRootCauseListNewUserHasRootCause.equals(vmUser)) {
                        oldVmUserOfUserHasRootCauseListNewUserHasRootCause.getUserHasRootCauseList().remove(userHasRootCauseListNewUserHasRootCause);
                        oldVmUserOfUserHasRootCauseListNewUserHasRootCause = em.merge(oldVmUserOfUserHasRootCauseListNewUserHasRootCause);
                    }
                }
            }
            for (UserTestPlanRole userTestPlanRoleListNewUserTestPlanRole : userTestPlanRoleListNew) {
                if (!userTestPlanRoleListOld.contains(userTestPlanRoleListNewUserTestPlanRole)) {
                    VmUser oldVmUserOfUserTestPlanRoleListNewUserTestPlanRole = userTestPlanRoleListNewUserTestPlanRole.getVmUser();
                    userTestPlanRoleListNewUserTestPlanRole.setVmUser(vmUser);
                    userTestPlanRoleListNewUserTestPlanRole = em.merge(userTestPlanRoleListNewUserTestPlanRole);
                    if (oldVmUserOfUserTestPlanRoleListNewUserTestPlanRole != null && !oldVmUserOfUserTestPlanRoleListNewUserTestPlanRole.equals(vmUser)) {
                        oldVmUserOfUserTestPlanRoleListNewUserTestPlanRole.getUserTestPlanRoleList().remove(userTestPlanRoleListNewUserTestPlanRole);
                        oldVmUserOfUserTestPlanRoleListNewUserTestPlanRole = em.merge(oldVmUserOfUserTestPlanRoleListNewUserTestPlanRole);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = vmUser.getId();
                if (findVmUser(id) == null) {
                    throw new NonexistentEntityException("The vmUser with id " + id + " no longer exists.");
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
            VmUser vmUser;
            try {
                vmUser = em.getReference(VmUser.class, id);
                vmUser.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The vmUser with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<UserModifiedRecord> userModifiedRecordListOrphanCheck = vmUser.getUserModifiedRecordList();
            for (UserModifiedRecord userModifiedRecordListOrphanCheckUserModifiedRecord : userModifiedRecordListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This VmUser (" + vmUser + ") cannot be destroyed since the UserModifiedRecord " + userModifiedRecordListOrphanCheckUserModifiedRecord + " in its userModifiedRecordList field has a non-nullable vmUser field.");
            }
            List<UserHasInvestigation> userHasInvestigationListOrphanCheck = vmUser.getUserHasInvestigationList();
            for (UserHasInvestigation userHasInvestigationListOrphanCheckUserHasInvestigation : userHasInvestigationListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This VmUser (" + vmUser + ") cannot be destroyed since the UserHasInvestigation " + userHasInvestigationListOrphanCheckUserHasInvestigation + " in its userHasInvestigationList field has a non-nullable vmUser field.");
            }
            List<UserAssigment> userAssigmentListOrphanCheck = vmUser.getUserAssigmentList();
            for (UserAssigment userAssigmentListOrphanCheckUserAssigment : userAssigmentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This VmUser (" + vmUser + ") cannot be destroyed since the UserAssigment " + userAssigmentListOrphanCheckUserAssigment + " in its userAssigmentList field has a non-nullable vmUser field.");
            }
            List<VmException> vmExceptionListOrphanCheck = vmUser.getVmExceptionList();
            for (VmException vmExceptionListOrphanCheckVmException : vmExceptionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This VmUser (" + vmUser + ") cannot be destroyed since the VmException " + vmExceptionListOrphanCheckVmException + " in its vmExceptionList field has a non-nullable vmUser field.");
            }
            List<UserTestProjectRole> userTestProjectRoleListOrphanCheck = vmUser.getUserTestProjectRoleList();
            for (UserTestProjectRole userTestProjectRoleListOrphanCheckUserTestProjectRole : userTestProjectRoleListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This VmUser (" + vmUser + ") cannot be destroyed since the UserTestProjectRole " + userTestProjectRoleListOrphanCheckUserTestProjectRole + " in its userTestProjectRoleList field has a non-nullable vmUser field.");
            }
            List<UserHasRootCause> userHasRootCauseListOrphanCheck = vmUser.getUserHasRootCauseList();
            for (UserHasRootCause userHasRootCauseListOrphanCheckUserHasRootCause : userHasRootCauseListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This VmUser (" + vmUser + ") cannot be destroyed since the UserHasRootCause " + userHasRootCauseListOrphanCheckUserHasRootCause + " in its userHasRootCauseList field has a non-nullable vmUser field.");
            }
            List<UserTestPlanRole> userTestPlanRoleListOrphanCheck = vmUser.getUserTestPlanRoleList();
            for (UserTestPlanRole userTestPlanRoleListOrphanCheckUserTestPlanRole : userTestPlanRoleListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This VmUser (" + vmUser + ") cannot be destroyed since the UserTestPlanRole " + userTestPlanRoleListOrphanCheckUserTestPlanRole + " in its userTestPlanRoleList field has a non-nullable vmUser field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            UserStatus userStatusId = vmUser.getUserStatusId();
            if (userStatusId != null) {
                userStatusId.getVmUserList().remove(vmUser);
                userStatusId = em.merge(userStatusId);
            }
            List<Role> roleList = vmUser.getRoleList();
            for (Role roleListRole : roleList) {
                roleListRole.getVmUserList().remove(vmUser);
                roleListRole = em.merge(roleListRole);
            }
            List<CorrectiveAction> correctiveActionList = vmUser.getCorrectiveActionList();
            for (CorrectiveAction correctiveActionListCorrectiveAction : correctiveActionList) {
                correctiveActionListCorrectiveAction.getVmUserList().remove(vmUser);
                correctiveActionListCorrectiveAction = em.merge(correctiveActionListCorrectiveAction);
            }
            List<TestCase> testCaseList = vmUser.getTestCaseList();
            for (TestCase testCaseListTestCase : testCaseList) {
                testCaseListTestCase.setAuthorId(null);
                testCaseListTestCase = em.merge(testCaseListTestCase);
            }
            List<UserAssigment> userAssigmentList1 = vmUser.getUserAssigmentList1();
            for (UserAssigment userAssigmentList1UserAssigment : userAssigmentList1) {
                userAssigmentList1UserAssigment.setAssigneeId(null);
                userAssigmentList1UserAssigment = em.merge(userAssigmentList1UserAssigment);
            }
            em.remove(vmUser);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<VmUser> findVmUserEntities() {
        return findVmUserEntities(true, -1, -1);
    }

    public List<VmUser> findVmUserEntities(int maxResults, int firstResult) {
        return findVmUserEntities(false, maxResults, firstResult);
    }

    private List<VmUser> findVmUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(VmUser.class));
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

    public VmUser findVmUser(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(VmUser.class, id);
        } finally {
            em.close();
        }
    }

    public int getVmUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<VmUser> rt = cq.from(VmUser.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
