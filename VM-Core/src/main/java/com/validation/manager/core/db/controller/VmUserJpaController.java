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
import com.validation.manager.core.db.UserStatus;
import com.validation.manager.core.db.CorrectiveAction;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.Role;
import com.validation.manager.core.db.RootCause;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.UserTestProjectRole;
import com.validation.manager.core.db.UserTestPlanRole;
import com.validation.manager.core.db.UserModifiedRecord;
import com.validation.manager.core.db.UserHasInvestigation;
import com.validation.manager.core.db.UserAssigment;
import com.validation.manager.core.db.ExecutionStepHasIssue;
import com.validation.manager.core.db.ExecutionStepHasVmUser;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.Notification;
import com.validation.manager.core.db.UserHasRole;
import com.validation.manager.core.db.Activity;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.WorkflowInstance;
import com.validation.manager.core.db.WorkflowInstanceHasTransition;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class VmUserJpaController implements Serializable {

    public VmUserJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(VmUser vmUser) {
        if (vmUser.getCorrectiveActionList() == null) {
            vmUser.setCorrectiveActionList(new ArrayList<>());
        }
        if (vmUser.getRoleList() == null) {
            vmUser.setRoleList(new ArrayList<>());
        }
        if (vmUser.getRootCauseList() == null) {
            vmUser.setRootCauseList(new ArrayList<>());
        }
        if (vmUser.getExecutionStepList() == null) {
            vmUser.setExecutionStepList(new ArrayList<>());
        }
        if (vmUser.getExecutionStepList1() == null) {
            vmUser.setExecutionStepList1(new ArrayList<>());
        }
        if (vmUser.getUserTestProjectRoleList() == null) {
            vmUser.setUserTestProjectRoleList(new ArrayList<>());
        }
        if (vmUser.getUserTestPlanRoleList() == null) {
            vmUser.setUserTestPlanRoleList(new ArrayList<>());
        }
        if (vmUser.getUserModifiedRecordList() == null) {
            vmUser.setUserModifiedRecordList(new ArrayList<>());
        }
        if (vmUser.getUserHasInvestigationList() == null) {
            vmUser.setUserHasInvestigationList(new ArrayList<>());
        }
        if (vmUser.getUserAssigmentList() == null) {
            vmUser.setUserAssigmentList(new ArrayList<>());
        }
        if (vmUser.getUserAssigmentList1() == null) {
            vmUser.setUserAssigmentList1(new ArrayList<>());
        }
        if (vmUser.getExecutionStepHasIssueList() == null) {
            vmUser.setExecutionStepHasIssueList(new ArrayList<>());
        }
        if (vmUser.getExecutionStepHasVmUserList() == null) {
            vmUser.setExecutionStepHasVmUserList(new ArrayList<>());
        }
        if (vmUser.getHistoryModificationList() == null) {
            vmUser.setHistoryModificationList(new ArrayList<>());
        }
        if (vmUser.getNotificationList() == null) {
            vmUser.setNotificationList(new ArrayList<>());
        }
        if (vmUser.getNotificationList1() == null) {
            vmUser.setNotificationList1(new ArrayList<>());
        }
        if (vmUser.getHistoryList() == null) {
            vmUser.setHistoryList(new ArrayList<>());
        }
        if (vmUser.getUserHasRoleList() == null) {
            vmUser.setUserHasRoleList(new ArrayList<>());
        }
        if (vmUser.getActivityList() == null) {
            vmUser.setActivityList(new ArrayList<>());
        }
        if (vmUser.getWorkflowInstanceList() == null) {
            vmUser.setWorkflowInstanceList(new ArrayList<>());
        }
        if (vmUser.getWorkflowInstanceHasTransitionList() == null) {
            vmUser.setWorkflowInstanceHasTransitionList(new ArrayList<>());
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
            List<CorrectiveAction> attachedCorrectiveActionList = new ArrayList<>();
            for (CorrectiveAction correctiveActionListCorrectiveActionToAttach : vmUser.getCorrectiveActionList()) {
                correctiveActionListCorrectiveActionToAttach = em.getReference(correctiveActionListCorrectiveActionToAttach.getClass(), correctiveActionListCorrectiveActionToAttach.getId());
                attachedCorrectiveActionList.add(correctiveActionListCorrectiveActionToAttach);
            }
            vmUser.setCorrectiveActionList(attachedCorrectiveActionList);
            List<Role> attachedRoleList = new ArrayList<>();
            for (Role roleListRoleToAttach : vmUser.getRoleList()) {
                roleListRoleToAttach = em.getReference(roleListRoleToAttach.getClass(), roleListRoleToAttach.getId());
                attachedRoleList.add(roleListRoleToAttach);
            }
            vmUser.setRoleList(attachedRoleList);
            List<RootCause> attachedRootCauseList = new ArrayList<>();
            for (RootCause rootCauseListRootCauseToAttach : vmUser.getRootCauseList()) {
                rootCauseListRootCauseToAttach = em.getReference(rootCauseListRootCauseToAttach.getClass(), rootCauseListRootCauseToAttach.getRootCausePK());
                attachedRootCauseList.add(rootCauseListRootCauseToAttach);
            }
            vmUser.setRootCauseList(attachedRootCauseList);
            List<ExecutionStep> attachedExecutionStepList = new ArrayList<>();
            for (ExecutionStep executionStepListExecutionStepToAttach : vmUser.getExecutionStepList()) {
                executionStepListExecutionStepToAttach = em.getReference(executionStepListExecutionStepToAttach.getClass(), executionStepListExecutionStepToAttach.getExecutionStepPK());
                attachedExecutionStepList.add(executionStepListExecutionStepToAttach);
            }
            vmUser.setExecutionStepList(attachedExecutionStepList);
            List<ExecutionStep> attachedExecutionStepList1 = new ArrayList<>();
            for (ExecutionStep executionStepList1ExecutionStepToAttach : vmUser.getExecutionStepList1()) {
                executionStepList1ExecutionStepToAttach = em.getReference(executionStepList1ExecutionStepToAttach.getClass(), executionStepList1ExecutionStepToAttach.getExecutionStepPK());
                attachedExecutionStepList1.add(executionStepList1ExecutionStepToAttach);
            }
            vmUser.setExecutionStepList1(attachedExecutionStepList1);
            List<UserTestProjectRole> attachedUserTestProjectRoleList = new ArrayList<>();
            for (UserTestProjectRole userTestProjectRoleListUserTestProjectRoleToAttach : vmUser.getUserTestProjectRoleList()) {
                userTestProjectRoleListUserTestProjectRoleToAttach = em.getReference(userTestProjectRoleListUserTestProjectRoleToAttach.getClass(), userTestProjectRoleListUserTestProjectRoleToAttach.getUserTestProjectRolePK());
                attachedUserTestProjectRoleList.add(userTestProjectRoleListUserTestProjectRoleToAttach);
            }
            vmUser.setUserTestProjectRoleList(attachedUserTestProjectRoleList);
            List<UserTestPlanRole> attachedUserTestPlanRoleList = new ArrayList<>();
            for (UserTestPlanRole userTestPlanRoleListUserTestPlanRoleToAttach : vmUser.getUserTestPlanRoleList()) {
                userTestPlanRoleListUserTestPlanRoleToAttach = em.getReference(userTestPlanRoleListUserTestPlanRoleToAttach.getClass(), userTestPlanRoleListUserTestPlanRoleToAttach.getUserTestPlanRolePK());
                attachedUserTestPlanRoleList.add(userTestPlanRoleListUserTestPlanRoleToAttach);
            }
            vmUser.setUserTestPlanRoleList(attachedUserTestPlanRoleList);
            List<UserModifiedRecord> attachedUserModifiedRecordList = new ArrayList<>();
            for (UserModifiedRecord userModifiedRecordListUserModifiedRecordToAttach : vmUser.getUserModifiedRecordList()) {
                userModifiedRecordListUserModifiedRecordToAttach = em.getReference(userModifiedRecordListUserModifiedRecordToAttach.getClass(), userModifiedRecordListUserModifiedRecordToAttach.getUserModifiedRecordPK());
                attachedUserModifiedRecordList.add(userModifiedRecordListUserModifiedRecordToAttach);
            }
            vmUser.setUserModifiedRecordList(attachedUserModifiedRecordList);
            List<UserHasInvestigation> attachedUserHasInvestigationList = new ArrayList<>();
            for (UserHasInvestigation userHasInvestigationListUserHasInvestigationToAttach : vmUser.getUserHasInvestigationList()) {
                userHasInvestigationListUserHasInvestigationToAttach = em.getReference(userHasInvestigationListUserHasInvestigationToAttach.getClass(), userHasInvestigationListUserHasInvestigationToAttach.getUserHasInvestigationPK());
                attachedUserHasInvestigationList.add(userHasInvestigationListUserHasInvestigationToAttach);
            }
            vmUser.setUserHasInvestigationList(attachedUserHasInvestigationList);
            List<UserAssigment> attachedUserAssigmentList = new ArrayList<>();
            for (UserAssigment userAssigmentListUserAssigmentToAttach : vmUser.getUserAssigmentList()) {
                userAssigmentListUserAssigmentToAttach = em.getReference(userAssigmentListUserAssigmentToAttach.getClass(), userAssigmentListUserAssigmentToAttach.getUserAssigmentPK());
                attachedUserAssigmentList.add(userAssigmentListUserAssigmentToAttach);
            }
            vmUser.setUserAssigmentList(attachedUserAssigmentList);
            List<UserAssigment> attachedUserAssigmentList1 = new ArrayList<>();
            for (UserAssigment userAssigmentList1UserAssigmentToAttach : vmUser.getUserAssigmentList1()) {
                userAssigmentList1UserAssigmentToAttach = em.getReference(userAssigmentList1UserAssigmentToAttach.getClass(), userAssigmentList1UserAssigmentToAttach.getUserAssigmentPK());
                attachedUserAssigmentList1.add(userAssigmentList1UserAssigmentToAttach);
            }
            vmUser.setUserAssigmentList1(attachedUserAssigmentList1);
            List<ExecutionStepHasIssue> attachedExecutionStepHasIssueList = new ArrayList<>();
            for (ExecutionStepHasIssue executionStepHasIssueListExecutionStepHasIssueToAttach : vmUser.getExecutionStepHasIssueList()) {
                executionStepHasIssueListExecutionStepHasIssueToAttach = em.getReference(executionStepHasIssueListExecutionStepHasIssueToAttach.getClass(), executionStepHasIssueListExecutionStepHasIssueToAttach.getExecutionStepHasIssuePK());
                attachedExecutionStepHasIssueList.add(executionStepHasIssueListExecutionStepHasIssueToAttach);
            }
            vmUser.setExecutionStepHasIssueList(attachedExecutionStepHasIssueList);
            List<ExecutionStepHasVmUser> attachedExecutionStepHasVmUserList = new ArrayList<>();
            for (ExecutionStepHasVmUser executionStepHasVmUserListExecutionStepHasVmUserToAttach : vmUser.getExecutionStepHasVmUserList()) {
                executionStepHasVmUserListExecutionStepHasVmUserToAttach = em.getReference(executionStepHasVmUserListExecutionStepHasVmUserToAttach.getClass(), executionStepHasVmUserListExecutionStepHasVmUserToAttach.getExecutionStepHasVmUserPK());
                attachedExecutionStepHasVmUserList.add(executionStepHasVmUserListExecutionStepHasVmUserToAttach);
            }
            vmUser.setExecutionStepHasVmUserList(attachedExecutionStepHasVmUserList);
            List<History> attachedHistoryModificationList = new ArrayList<>();
            for (History historyModificationListHistoryToAttach : vmUser.getHistoryModificationList()) {
                historyModificationListHistoryToAttach = em.getReference(historyModificationListHistoryToAttach.getClass(), historyModificationListHistoryToAttach.getId());
                attachedHistoryModificationList.add(historyModificationListHistoryToAttach);
            }
            vmUser.setHistoryModificationList(attachedHistoryModificationList);
            List<Notification> attachedNotificationList = new ArrayList<>();
            for (Notification notificationListNotificationToAttach : vmUser.getNotificationList()) {
                notificationListNotificationToAttach = em.getReference(notificationListNotificationToAttach.getClass(), notificationListNotificationToAttach.getNotificationPK());
                attachedNotificationList.add(notificationListNotificationToAttach);
            }
            vmUser.setNotificationList(attachedNotificationList);
            List<Notification> attachedNotificationList1 = new ArrayList<>();
            for (Notification notificationList1NotificationToAttach : vmUser.getNotificationList1()) {
                notificationList1NotificationToAttach = em.getReference(notificationList1NotificationToAttach.getClass(), notificationList1NotificationToAttach.getNotificationPK());
                attachedNotificationList1.add(notificationList1NotificationToAttach);
            }
            vmUser.setNotificationList1(attachedNotificationList1);
            List<History> attachedHistoryList = new ArrayList<>();
            for (History historyListHistoryToAttach : vmUser.getHistoryList()) {
                historyListHistoryToAttach = em.getReference(historyListHistoryToAttach.getClass(), historyListHistoryToAttach.getId());
                attachedHistoryList.add(historyListHistoryToAttach);
            }
            vmUser.setHistoryList(attachedHistoryList);
            List<UserHasRole> attachedUserHasRoleList = new ArrayList<>();
            for (UserHasRole userHasRoleListUserHasRoleToAttach : vmUser.getUserHasRoleList()) {
                userHasRoleListUserHasRoleToAttach = em.getReference(userHasRoleListUserHasRoleToAttach.getClass(), userHasRoleListUserHasRoleToAttach.getUserHasRolePK());
                attachedUserHasRoleList.add(userHasRoleListUserHasRoleToAttach);
            }
            vmUser.setUserHasRoleList(attachedUserHasRoleList);
            List<Activity> attachedActivityList = new ArrayList<>();
            for (Activity activityListActivityToAttach : vmUser.getActivityList()) {
                activityListActivityToAttach = em.getReference(activityListActivityToAttach.getClass(), activityListActivityToAttach.getActivityPK());
                attachedActivityList.add(activityListActivityToAttach);
            }
            vmUser.setActivityList(attachedActivityList);
            List<WorkflowInstance> attachedWorkflowInstanceList = new ArrayList<>();
            for (WorkflowInstance workflowInstanceListWorkflowInstanceToAttach : vmUser.getWorkflowInstanceList()) {
                workflowInstanceListWorkflowInstanceToAttach = em.getReference(workflowInstanceListWorkflowInstanceToAttach.getClass(), workflowInstanceListWorkflowInstanceToAttach.getWorkflowInstancePK());
                attachedWorkflowInstanceList.add(workflowInstanceListWorkflowInstanceToAttach);
            }
            vmUser.setWorkflowInstanceList(attachedWorkflowInstanceList);
            List<WorkflowInstanceHasTransition> attachedWorkflowInstanceHasTransitionList = new ArrayList<>();
            for (WorkflowInstanceHasTransition workflowInstanceHasTransitionListWorkflowInstanceHasTransitionToAttach : vmUser.getWorkflowInstanceHasTransitionList()) {
                workflowInstanceHasTransitionListWorkflowInstanceHasTransitionToAttach = em.getReference(workflowInstanceHasTransitionListWorkflowInstanceHasTransitionToAttach.getClass(), workflowInstanceHasTransitionListWorkflowInstanceHasTransitionToAttach.getWorkflowInstanceHasTransitionPK());
                attachedWorkflowInstanceHasTransitionList.add(workflowInstanceHasTransitionListWorkflowInstanceHasTransitionToAttach);
            }
            vmUser.setWorkflowInstanceHasTransitionList(attachedWorkflowInstanceHasTransitionList);
            em.persist(vmUser);
            if (userStatusId != null) {
                userStatusId.getVmUserList().add(vmUser);
                userStatusId = em.merge(userStatusId);
            }
            for (CorrectiveAction correctiveActionListCorrectiveAction : vmUser.getCorrectiveActionList()) {
                correctiveActionListCorrectiveAction.getVmUserList().add(vmUser);
                correctiveActionListCorrectiveAction = em.merge(correctiveActionListCorrectiveAction);
            }
            for (Role roleListRole : vmUser.getRoleList()) {
                roleListRole.getVmUserList().add(vmUser);
                roleListRole = em.merge(roleListRole);
            }
            for (RootCause rootCauseListRootCause : vmUser.getRootCauseList()) {
                rootCauseListRootCause.getVmUserList().add(vmUser);
                rootCauseListRootCause = em.merge(rootCauseListRootCause);
            }
            for (ExecutionStep executionStepListExecutionStep : vmUser.getExecutionStepList()) {
                VmUser oldAssigneeOfExecutionStepListExecutionStep = executionStepListExecutionStep.getAssignee();
                executionStepListExecutionStep.setAssignee(vmUser);
                executionStepListExecutionStep = em.merge(executionStepListExecutionStep);
                if (oldAssigneeOfExecutionStepListExecutionStep != null) {
                    oldAssigneeOfExecutionStepListExecutionStep.getExecutionStepList().remove(executionStepListExecutionStep);
                    oldAssigneeOfExecutionStepListExecutionStep = em.merge(oldAssigneeOfExecutionStepListExecutionStep);
                }
            }
            for (ExecutionStep executionStepList1ExecutionStep : vmUser.getExecutionStepList1()) {
                VmUser oldAssignerOfExecutionStepList1ExecutionStep = executionStepList1ExecutionStep.getAssigner();
                executionStepList1ExecutionStep.setAssigner(vmUser);
                executionStepList1ExecutionStep = em.merge(executionStepList1ExecutionStep);
                if (oldAssignerOfExecutionStepList1ExecutionStep != null) {
                    oldAssignerOfExecutionStepList1ExecutionStep.getExecutionStepList1().remove(executionStepList1ExecutionStep);
                    oldAssignerOfExecutionStepList1ExecutionStep = em.merge(oldAssignerOfExecutionStepList1ExecutionStep);
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
            for (UserTestPlanRole userTestPlanRoleListUserTestPlanRole : vmUser.getUserTestPlanRoleList()) {
                VmUser oldVmUserOfUserTestPlanRoleListUserTestPlanRole = userTestPlanRoleListUserTestPlanRole.getVmUser();
                userTestPlanRoleListUserTestPlanRole.setVmUser(vmUser);
                userTestPlanRoleListUserTestPlanRole = em.merge(userTestPlanRoleListUserTestPlanRole);
                if (oldVmUserOfUserTestPlanRoleListUserTestPlanRole != null) {
                    oldVmUserOfUserTestPlanRoleListUserTestPlanRole.getUserTestPlanRoleList().remove(userTestPlanRoleListUserTestPlanRole);
                    oldVmUserOfUserTestPlanRoleListUserTestPlanRole = em.merge(oldVmUserOfUserTestPlanRoleListUserTestPlanRole);
                }
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
            for (ExecutionStepHasIssue executionStepHasIssueListExecutionStepHasIssue : vmUser.getExecutionStepHasIssueList()) {
                executionStepHasIssueListExecutionStepHasIssue.getVmUserList().add(vmUser);
                executionStepHasIssueListExecutionStepHasIssue = em.merge(executionStepHasIssueListExecutionStepHasIssue);
            }
            for (ExecutionStepHasVmUser executionStepHasVmUserListExecutionStepHasVmUser : vmUser.getExecutionStepHasVmUserList()) {
                VmUser oldVmUserOfExecutionStepHasVmUserListExecutionStepHasVmUser = executionStepHasVmUserListExecutionStepHasVmUser.getVmUser();
                executionStepHasVmUserListExecutionStepHasVmUser.setVmUser(vmUser);
                executionStepHasVmUserListExecutionStepHasVmUser = em.merge(executionStepHasVmUserListExecutionStepHasVmUser);
                if (oldVmUserOfExecutionStepHasVmUserListExecutionStepHasVmUser != null) {
                    oldVmUserOfExecutionStepHasVmUserListExecutionStepHasVmUser.getExecutionStepHasVmUserList().remove(executionStepHasVmUserListExecutionStepHasVmUser);
                    oldVmUserOfExecutionStepHasVmUserListExecutionStepHasVmUser = em.merge(oldVmUserOfExecutionStepHasVmUserListExecutionStepHasVmUser);
                }
            }
            for (History historyModificationListHistory : vmUser.getHistoryModificationList()) {
                VmUser oldModifierIdOfHistoryModificationListHistory = historyModificationListHistory.getModifierId();
                historyModificationListHistory.setModifierId(vmUser);
                historyModificationListHistory = em.merge(historyModificationListHistory);
                if (oldModifierIdOfHistoryModificationListHistory != null) {
                    oldModifierIdOfHistoryModificationListHistory.getHistoryModificationList().remove(historyModificationListHistory);
                    oldModifierIdOfHistoryModificationListHistory = em.merge(oldModifierIdOfHistoryModificationListHistory);
                }
            }
            for (Notification notificationListNotification : vmUser.getNotificationList()) {
                VmUser oldTargetUserOfNotificationListNotification = notificationListNotification.getTargetUser();
                notificationListNotification.setTargetUser(vmUser);
                notificationListNotification = em.merge(notificationListNotification);
                if (oldTargetUserOfNotificationListNotification != null) {
                    oldTargetUserOfNotificationListNotification.getNotificationList().remove(notificationListNotification);
                    oldTargetUserOfNotificationListNotification = em.merge(oldTargetUserOfNotificationListNotification);
                }
            }
            for (Notification notificationList1Notification : vmUser.getNotificationList1()) {
                VmUser oldAuthorOfNotificationList1Notification = notificationList1Notification.getAuthor();
                notificationList1Notification.setAuthor(vmUser);
                notificationList1Notification = em.merge(notificationList1Notification);
                if (oldAuthorOfNotificationList1Notification != null) {
                    oldAuthorOfNotificationList1Notification.getNotificationList1().remove(notificationList1Notification);
                    oldAuthorOfNotificationList1Notification = em.merge(oldAuthorOfNotificationList1Notification);
                }
            }
            for (History historyListHistory : vmUser.getHistoryList()) {
                VmUser oldVmUserIdOfHistoryListHistory = historyListHistory.getVmUserId();
                historyListHistory.setVmUserId(vmUser);
                historyListHistory = em.merge(historyListHistory);
                if (oldVmUserIdOfHistoryListHistory != null) {
                    oldVmUserIdOfHistoryListHistory.getHistoryList().remove(historyListHistory);
                    oldVmUserIdOfHistoryListHistory = em.merge(oldVmUserIdOfHistoryListHistory);
                }
            }
            for (UserHasRole userHasRoleListUserHasRole : vmUser.getUserHasRoleList()) {
                VmUser oldVmUserOfUserHasRoleListUserHasRole = userHasRoleListUserHasRole.getVmUser();
                userHasRoleListUserHasRole.setVmUser(vmUser);
                userHasRoleListUserHasRole = em.merge(userHasRoleListUserHasRole);
                if (oldVmUserOfUserHasRoleListUserHasRole != null) {
                    oldVmUserOfUserHasRoleListUserHasRole.getUserHasRoleList().remove(userHasRoleListUserHasRole);
                    oldVmUserOfUserHasRoleListUserHasRole = em.merge(oldVmUserOfUserHasRoleListUserHasRole);
                }
            }
            for (Activity activityListActivity : vmUser.getActivityList()) {
                VmUser oldSourceUserOfActivityListActivity = activityListActivity.getSourceUser();
                activityListActivity.setSourceUser(vmUser);
                activityListActivity = em.merge(activityListActivity);
                if (oldSourceUserOfActivityListActivity != null) {
                    oldSourceUserOfActivityListActivity.getActivityList().remove(activityListActivity);
                    oldSourceUserOfActivityListActivity = em.merge(oldSourceUserOfActivityListActivity);
                }
            }
            for (WorkflowInstance workflowInstanceListWorkflowInstance : vmUser.getWorkflowInstanceList()) {
                VmUser oldAssignedUserOfWorkflowInstanceListWorkflowInstance = workflowInstanceListWorkflowInstance.getAssignedUser();
                workflowInstanceListWorkflowInstance.setAssignedUser(vmUser);
                workflowInstanceListWorkflowInstance = em.merge(workflowInstanceListWorkflowInstance);
                if (oldAssignedUserOfWorkflowInstanceListWorkflowInstance != null) {
                    oldAssignedUserOfWorkflowInstanceListWorkflowInstance.getWorkflowInstanceList().remove(workflowInstanceListWorkflowInstance);
                    oldAssignedUserOfWorkflowInstanceListWorkflowInstance = em.merge(oldAssignedUserOfWorkflowInstanceListWorkflowInstance);
                }
            }
            for (WorkflowInstanceHasTransition workflowInstanceHasTransitionListWorkflowInstanceHasTransition : vmUser.getWorkflowInstanceHasTransitionList()) {
                VmUser oldTransitionerOfWorkflowInstanceHasTransitionListWorkflowInstanceHasTransition = workflowInstanceHasTransitionListWorkflowInstanceHasTransition.getTransitioner();
                workflowInstanceHasTransitionListWorkflowInstanceHasTransition.setTransitioner(vmUser);
                workflowInstanceHasTransitionListWorkflowInstanceHasTransition = em.merge(workflowInstanceHasTransitionListWorkflowInstanceHasTransition);
                if (oldTransitionerOfWorkflowInstanceHasTransitionListWorkflowInstanceHasTransition != null) {
                    oldTransitionerOfWorkflowInstanceHasTransitionListWorkflowInstanceHasTransition.getWorkflowInstanceHasTransitionList().remove(workflowInstanceHasTransitionListWorkflowInstanceHasTransition);
                    oldTransitionerOfWorkflowInstanceHasTransitionListWorkflowInstanceHasTransition = em.merge(oldTransitionerOfWorkflowInstanceHasTransitionListWorkflowInstanceHasTransition);
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

    public void edit(VmUser vmUser) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            VmUser persistentVmUser = em.find(VmUser.class, vmUser.getId());
            UserStatus userStatusIdOld = persistentVmUser.getUserStatusId();
            UserStatus userStatusIdNew = vmUser.getUserStatusId();
            List<CorrectiveAction> correctiveActionListOld = persistentVmUser.getCorrectiveActionList();
            List<CorrectiveAction> correctiveActionListNew = vmUser.getCorrectiveActionList();
            List<Role> roleListOld = persistentVmUser.getRoleList();
            List<Role> roleListNew = vmUser.getRoleList();
            List<RootCause> rootCauseListOld = persistentVmUser.getRootCauseList();
            List<RootCause> rootCauseListNew = vmUser.getRootCauseList();
            List<ExecutionStep> executionStepListOld = persistentVmUser.getExecutionStepList();
            List<ExecutionStep> executionStepListNew = vmUser.getExecutionStepList();
            List<ExecutionStep> executionStepList1Old = persistentVmUser.getExecutionStepList1();
            List<ExecutionStep> executionStepList1New = vmUser.getExecutionStepList1();
            List<UserTestProjectRole> userTestProjectRoleListOld = persistentVmUser.getUserTestProjectRoleList();
            List<UserTestProjectRole> userTestProjectRoleListNew = vmUser.getUserTestProjectRoleList();
            List<UserTestPlanRole> userTestPlanRoleListOld = persistentVmUser.getUserTestPlanRoleList();
            List<UserTestPlanRole> userTestPlanRoleListNew = vmUser.getUserTestPlanRoleList();
            List<UserModifiedRecord> userModifiedRecordListOld = persistentVmUser.getUserModifiedRecordList();
            List<UserModifiedRecord> userModifiedRecordListNew = vmUser.getUserModifiedRecordList();
            List<UserHasInvestigation> userHasInvestigationListOld = persistentVmUser.getUserHasInvestigationList();
            List<UserHasInvestigation> userHasInvestigationListNew = vmUser.getUserHasInvestigationList();
            List<UserAssigment> userAssigmentListOld = persistentVmUser.getUserAssigmentList();
            List<UserAssigment> userAssigmentListNew = vmUser.getUserAssigmentList();
            List<UserAssigment> userAssigmentList1Old = persistentVmUser.getUserAssigmentList1();
            List<UserAssigment> userAssigmentList1New = vmUser.getUserAssigmentList1();
            List<ExecutionStepHasIssue> executionStepHasIssueListOld = persistentVmUser.getExecutionStepHasIssueList();
            List<ExecutionStepHasIssue> executionStepHasIssueListNew = vmUser.getExecutionStepHasIssueList();
            List<ExecutionStepHasVmUser> executionStepHasVmUserListOld = persistentVmUser.getExecutionStepHasVmUserList();
            List<ExecutionStepHasVmUser> executionStepHasVmUserListNew = vmUser.getExecutionStepHasVmUserList();
            List<History> historyModificationListOld = persistentVmUser.getHistoryModificationList();
            List<History> historyModificationListNew = vmUser.getHistoryModificationList();
            List<Notification> notificationListOld = persistentVmUser.getNotificationList();
            List<Notification> notificationListNew = vmUser.getNotificationList();
            List<Notification> notificationList1Old = persistentVmUser.getNotificationList1();
            List<Notification> notificationList1New = vmUser.getNotificationList1();
            List<History> historyListOld = persistentVmUser.getHistoryList();
            List<History> historyListNew = vmUser.getHistoryList();
            List<UserHasRole> userHasRoleListOld = persistentVmUser.getUserHasRoleList();
            List<UserHasRole> userHasRoleListNew = vmUser.getUserHasRoleList();
            List<Activity> activityListOld = persistentVmUser.getActivityList();
            List<Activity> activityListNew = vmUser.getActivityList();
            List<WorkflowInstance> workflowInstanceListOld = persistentVmUser.getWorkflowInstanceList();
            List<WorkflowInstance> workflowInstanceListNew = vmUser.getWorkflowInstanceList();
            List<WorkflowInstanceHasTransition> workflowInstanceHasTransitionListOld = persistentVmUser.getWorkflowInstanceHasTransitionList();
            List<WorkflowInstanceHasTransition> workflowInstanceHasTransitionListNew = vmUser.getWorkflowInstanceHasTransitionList();
            List<String> illegalOrphanMessages = null;
            for (UserTestProjectRole userTestProjectRoleListOldUserTestProjectRole : userTestProjectRoleListOld) {
                if (!userTestProjectRoleListNew.contains(userTestProjectRoleListOldUserTestProjectRole)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain UserTestProjectRole " + userTestProjectRoleListOldUserTestProjectRole + " since its vmUser field is not nullable.");
                }
            }
            for (UserTestPlanRole userTestPlanRoleListOldUserTestPlanRole : userTestPlanRoleListOld) {
                if (!userTestPlanRoleListNew.contains(userTestPlanRoleListOldUserTestPlanRole)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain UserTestPlanRole " + userTestPlanRoleListOldUserTestPlanRole + " since its vmUser field is not nullable.");
                }
            }
            for (UserModifiedRecord userModifiedRecordListOldUserModifiedRecord : userModifiedRecordListOld) {
                if (!userModifiedRecordListNew.contains(userModifiedRecordListOldUserModifiedRecord)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain UserModifiedRecord " + userModifiedRecordListOldUserModifiedRecord + " since its vmUser field is not nullable.");
                }
            }
            for (UserHasInvestigation userHasInvestigationListOldUserHasInvestigation : userHasInvestigationListOld) {
                if (!userHasInvestigationListNew.contains(userHasInvestigationListOldUserHasInvestigation)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain UserHasInvestigation " + userHasInvestigationListOldUserHasInvestigation + " since its vmUser field is not nullable.");
                }
            }
            for (UserAssigment userAssigmentListOldUserAssigment : userAssigmentListOld) {
                if (!userAssigmentListNew.contains(userAssigmentListOldUserAssigment)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain UserAssigment " + userAssigmentListOldUserAssigment + " since its vmUser field is not nullable.");
                }
            }
            for (UserAssigment userAssigmentList1OldUserAssigment : userAssigmentList1Old) {
                if (!userAssigmentList1New.contains(userAssigmentList1OldUserAssigment)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain UserAssigment " + userAssigmentList1OldUserAssigment + " since its assigneeId field is not nullable.");
                }
            }
            for (ExecutionStepHasVmUser executionStepHasVmUserListOldExecutionStepHasVmUser : executionStepHasVmUserListOld) {
                if (!executionStepHasVmUserListNew.contains(executionStepHasVmUserListOldExecutionStepHasVmUser)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain ExecutionStepHasVmUser " + executionStepHasVmUserListOldExecutionStepHasVmUser + " since its vmUser field is not nullable.");
                }
            }
            for (History historyModificationListOldHistory : historyModificationListOld) {
                if (!historyModificationListNew.contains(historyModificationListOldHistory)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain History " + historyModificationListOldHistory + " since its modifierId field is not nullable.");
                }
            }
            for (Notification notificationListOldNotification : notificationListOld) {
                if (!notificationListNew.contains(notificationListOldNotification)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Notification " + notificationListOldNotification + " since its targetUser field is not nullable.");
                }
            }
            for (Notification notificationList1OldNotification : notificationList1Old) {
                if (!notificationList1New.contains(notificationList1OldNotification)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Notification " + notificationList1OldNotification + " since its author field is not nullable.");
                }
            }
            for (History historyListOldHistory : historyListOld) {
                if (!historyListNew.contains(historyListOldHistory)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain History " + historyListOldHistory + " since its vmUserId field is not nullable.");
                }
            }
            for (UserHasRole userHasRoleListOldUserHasRole : userHasRoleListOld) {
                if (!userHasRoleListNew.contains(userHasRoleListOldUserHasRole)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain UserHasRole " + userHasRoleListOldUserHasRole + " since its vmUser field is not nullable.");
                }
            }
            for (Activity activityListOldActivity : activityListOld) {
                if (!activityListNew.contains(activityListOldActivity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Activity " + activityListOldActivity + " since its sourceUser field is not nullable.");
                }
            }
            for (WorkflowInstanceHasTransition workflowInstanceHasTransitionListOldWorkflowInstanceHasTransition : workflowInstanceHasTransitionListOld) {
                if (!workflowInstanceHasTransitionListNew.contains(workflowInstanceHasTransitionListOldWorkflowInstanceHasTransition)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain WorkflowInstanceHasTransition " + workflowInstanceHasTransitionListOldWorkflowInstanceHasTransition + " since its transitioner field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (userStatusIdNew != null) {
                userStatusIdNew = em.getReference(userStatusIdNew.getClass(), userStatusIdNew.getId());
                vmUser.setUserStatusId(userStatusIdNew);
            }
            List<CorrectiveAction> attachedCorrectiveActionListNew = new ArrayList<>();
            for (CorrectiveAction correctiveActionListNewCorrectiveActionToAttach : correctiveActionListNew) {
                correctiveActionListNewCorrectiveActionToAttach = em.getReference(correctiveActionListNewCorrectiveActionToAttach.getClass(), correctiveActionListNewCorrectiveActionToAttach.getId());
                attachedCorrectiveActionListNew.add(correctiveActionListNewCorrectiveActionToAttach);
            }
            correctiveActionListNew = attachedCorrectiveActionListNew;
            vmUser.setCorrectiveActionList(correctiveActionListNew);
            List<Role> attachedRoleListNew = new ArrayList<>();
            for (Role roleListNewRoleToAttach : roleListNew) {
                roleListNewRoleToAttach = em.getReference(roleListNewRoleToAttach.getClass(), roleListNewRoleToAttach.getId());
                attachedRoleListNew.add(roleListNewRoleToAttach);
            }
            roleListNew = attachedRoleListNew;
            vmUser.setRoleList(roleListNew);
            List<RootCause> attachedRootCauseListNew = new ArrayList<>();
            for (RootCause rootCauseListNewRootCauseToAttach : rootCauseListNew) {
                rootCauseListNewRootCauseToAttach = em.getReference(rootCauseListNewRootCauseToAttach.getClass(), rootCauseListNewRootCauseToAttach.getRootCausePK());
                attachedRootCauseListNew.add(rootCauseListNewRootCauseToAttach);
            }
            rootCauseListNew = attachedRootCauseListNew;
            vmUser.setRootCauseList(rootCauseListNew);
            List<ExecutionStep> attachedExecutionStepListNew = new ArrayList<>();
            for (ExecutionStep executionStepListNewExecutionStepToAttach : executionStepListNew) {
                executionStepListNewExecutionStepToAttach = em.getReference(executionStepListNewExecutionStepToAttach.getClass(), executionStepListNewExecutionStepToAttach.getExecutionStepPK());
                attachedExecutionStepListNew.add(executionStepListNewExecutionStepToAttach);
            }
            executionStepListNew = attachedExecutionStepListNew;
            vmUser.setExecutionStepList(executionStepListNew);
            List<ExecutionStep> attachedExecutionStepList1New = new ArrayList<>();
            for (ExecutionStep executionStepList1NewExecutionStepToAttach : executionStepList1New) {
                executionStepList1NewExecutionStepToAttach = em.getReference(executionStepList1NewExecutionStepToAttach.getClass(), executionStepList1NewExecutionStepToAttach.getExecutionStepPK());
                attachedExecutionStepList1New.add(executionStepList1NewExecutionStepToAttach);
            }
            executionStepList1New = attachedExecutionStepList1New;
            vmUser.setExecutionStepList1(executionStepList1New);
            List<UserTestProjectRole> attachedUserTestProjectRoleListNew = new ArrayList<>();
            for (UserTestProjectRole userTestProjectRoleListNewUserTestProjectRoleToAttach : userTestProjectRoleListNew) {
                userTestProjectRoleListNewUserTestProjectRoleToAttach = em.getReference(userTestProjectRoleListNewUserTestProjectRoleToAttach.getClass(), userTestProjectRoleListNewUserTestProjectRoleToAttach.getUserTestProjectRolePK());
                attachedUserTestProjectRoleListNew.add(userTestProjectRoleListNewUserTestProjectRoleToAttach);
            }
            userTestProjectRoleListNew = attachedUserTestProjectRoleListNew;
            vmUser.setUserTestProjectRoleList(userTestProjectRoleListNew);
            List<UserTestPlanRole> attachedUserTestPlanRoleListNew = new ArrayList<>();
            for (UserTestPlanRole userTestPlanRoleListNewUserTestPlanRoleToAttach : userTestPlanRoleListNew) {
                userTestPlanRoleListNewUserTestPlanRoleToAttach = em.getReference(userTestPlanRoleListNewUserTestPlanRoleToAttach.getClass(), userTestPlanRoleListNewUserTestPlanRoleToAttach.getUserTestPlanRolePK());
                attachedUserTestPlanRoleListNew.add(userTestPlanRoleListNewUserTestPlanRoleToAttach);
            }
            userTestPlanRoleListNew = attachedUserTestPlanRoleListNew;
            vmUser.setUserTestPlanRoleList(userTestPlanRoleListNew);
            List<UserModifiedRecord> attachedUserModifiedRecordListNew = new ArrayList<>();
            for (UserModifiedRecord userModifiedRecordListNewUserModifiedRecordToAttach : userModifiedRecordListNew) {
                userModifiedRecordListNewUserModifiedRecordToAttach = em.getReference(userModifiedRecordListNewUserModifiedRecordToAttach.getClass(), userModifiedRecordListNewUserModifiedRecordToAttach.getUserModifiedRecordPK());
                attachedUserModifiedRecordListNew.add(userModifiedRecordListNewUserModifiedRecordToAttach);
            }
            userModifiedRecordListNew = attachedUserModifiedRecordListNew;
            vmUser.setUserModifiedRecordList(userModifiedRecordListNew);
            List<UserHasInvestigation> attachedUserHasInvestigationListNew = new ArrayList<>();
            for (UserHasInvestigation userHasInvestigationListNewUserHasInvestigationToAttach : userHasInvestigationListNew) {
                userHasInvestigationListNewUserHasInvestigationToAttach = em.getReference(userHasInvestigationListNewUserHasInvestigationToAttach.getClass(), userHasInvestigationListNewUserHasInvestigationToAttach.getUserHasInvestigationPK());
                attachedUserHasInvestigationListNew.add(userHasInvestigationListNewUserHasInvestigationToAttach);
            }
            userHasInvestigationListNew = attachedUserHasInvestigationListNew;
            vmUser.setUserHasInvestigationList(userHasInvestigationListNew);
            List<UserAssigment> attachedUserAssigmentListNew = new ArrayList<>();
            for (UserAssigment userAssigmentListNewUserAssigmentToAttach : userAssigmentListNew) {
                userAssigmentListNewUserAssigmentToAttach = em.getReference(userAssigmentListNewUserAssigmentToAttach.getClass(), userAssigmentListNewUserAssigmentToAttach.getUserAssigmentPK());
                attachedUserAssigmentListNew.add(userAssigmentListNewUserAssigmentToAttach);
            }
            userAssigmentListNew = attachedUserAssigmentListNew;
            vmUser.setUserAssigmentList(userAssigmentListNew);
            List<UserAssigment> attachedUserAssigmentList1New = new ArrayList<>();
            for (UserAssigment userAssigmentList1NewUserAssigmentToAttach : userAssigmentList1New) {
                userAssigmentList1NewUserAssigmentToAttach = em.getReference(userAssigmentList1NewUserAssigmentToAttach.getClass(), userAssigmentList1NewUserAssigmentToAttach.getUserAssigmentPK());
                attachedUserAssigmentList1New.add(userAssigmentList1NewUserAssigmentToAttach);
            }
            userAssigmentList1New = attachedUserAssigmentList1New;
            vmUser.setUserAssigmentList1(userAssigmentList1New);
            List<ExecutionStepHasIssue> attachedExecutionStepHasIssueListNew = new ArrayList<>();
            for (ExecutionStepHasIssue executionStepHasIssueListNewExecutionStepHasIssueToAttach : executionStepHasIssueListNew) {
                executionStepHasIssueListNewExecutionStepHasIssueToAttach = em.getReference(executionStepHasIssueListNewExecutionStepHasIssueToAttach.getClass(), executionStepHasIssueListNewExecutionStepHasIssueToAttach.getExecutionStepHasIssuePK());
                attachedExecutionStepHasIssueListNew.add(executionStepHasIssueListNewExecutionStepHasIssueToAttach);
            }
            executionStepHasIssueListNew = attachedExecutionStepHasIssueListNew;
            vmUser.setExecutionStepHasIssueList(executionStepHasIssueListNew);
            List<ExecutionStepHasVmUser> attachedExecutionStepHasVmUserListNew = new ArrayList<>();
            for (ExecutionStepHasVmUser executionStepHasVmUserListNewExecutionStepHasVmUserToAttach : executionStepHasVmUserListNew) {
                executionStepHasVmUserListNewExecutionStepHasVmUserToAttach = em.getReference(executionStepHasVmUserListNewExecutionStepHasVmUserToAttach.getClass(), executionStepHasVmUserListNewExecutionStepHasVmUserToAttach.getExecutionStepHasVmUserPK());
                attachedExecutionStepHasVmUserListNew.add(executionStepHasVmUserListNewExecutionStepHasVmUserToAttach);
            }
            executionStepHasVmUserListNew = attachedExecutionStepHasVmUserListNew;
            vmUser.setExecutionStepHasVmUserList(executionStepHasVmUserListNew);
            List<History> attachedHistoryModificationListNew = new ArrayList<>();
            for (History historyModificationListNewHistoryToAttach : historyModificationListNew) {
                historyModificationListNewHistoryToAttach = em.getReference(historyModificationListNewHistoryToAttach.getClass(), historyModificationListNewHistoryToAttach.getId());
                attachedHistoryModificationListNew.add(historyModificationListNewHistoryToAttach);
            }
            historyModificationListNew = attachedHistoryModificationListNew;
            vmUser.setHistoryModificationList(historyModificationListNew);
            List<Notification> attachedNotificationListNew = new ArrayList<>();
            for (Notification notificationListNewNotificationToAttach : notificationListNew) {
                notificationListNewNotificationToAttach = em.getReference(notificationListNewNotificationToAttach.getClass(), notificationListNewNotificationToAttach.getNotificationPK());
                attachedNotificationListNew.add(notificationListNewNotificationToAttach);
            }
            notificationListNew = attachedNotificationListNew;
            vmUser.setNotificationList(notificationListNew);
            List<Notification> attachedNotificationList1New = new ArrayList<>();
            for (Notification notificationList1NewNotificationToAttach : notificationList1New) {
                notificationList1NewNotificationToAttach = em.getReference(notificationList1NewNotificationToAttach.getClass(), notificationList1NewNotificationToAttach.getNotificationPK());
                attachedNotificationList1New.add(notificationList1NewNotificationToAttach);
            }
            notificationList1New = attachedNotificationList1New;
            vmUser.setNotificationList1(notificationList1New);
            List<History> attachedHistoryListNew = new ArrayList<>();
            for (History historyListNewHistoryToAttach : historyListNew) {
                historyListNewHistoryToAttach = em.getReference(historyListNewHistoryToAttach.getClass(), historyListNewHistoryToAttach.getId());
                attachedHistoryListNew.add(historyListNewHistoryToAttach);
            }
            historyListNew = attachedHistoryListNew;
            vmUser.setHistoryList(historyListNew);
            List<UserHasRole> attachedUserHasRoleListNew = new ArrayList<>();
            for (UserHasRole userHasRoleListNewUserHasRoleToAttach : userHasRoleListNew) {
                userHasRoleListNewUserHasRoleToAttach = em.getReference(userHasRoleListNewUserHasRoleToAttach.getClass(), userHasRoleListNewUserHasRoleToAttach.getUserHasRolePK());
                attachedUserHasRoleListNew.add(userHasRoleListNewUserHasRoleToAttach);
            }
            userHasRoleListNew = attachedUserHasRoleListNew;
            vmUser.setUserHasRoleList(userHasRoleListNew);
            List<Activity> attachedActivityListNew = new ArrayList<>();
            for (Activity activityListNewActivityToAttach : activityListNew) {
                activityListNewActivityToAttach = em.getReference(activityListNewActivityToAttach.getClass(), activityListNewActivityToAttach.getActivityPK());
                attachedActivityListNew.add(activityListNewActivityToAttach);
            }
            activityListNew = attachedActivityListNew;
            vmUser.setActivityList(activityListNew);
            List<WorkflowInstance> attachedWorkflowInstanceListNew = new ArrayList<>();
            for (WorkflowInstance workflowInstanceListNewWorkflowInstanceToAttach : workflowInstanceListNew) {
                workflowInstanceListNewWorkflowInstanceToAttach = em.getReference(workflowInstanceListNewWorkflowInstanceToAttach.getClass(), workflowInstanceListNewWorkflowInstanceToAttach.getWorkflowInstancePK());
                attachedWorkflowInstanceListNew.add(workflowInstanceListNewWorkflowInstanceToAttach);
            }
            workflowInstanceListNew = attachedWorkflowInstanceListNew;
            vmUser.setWorkflowInstanceList(workflowInstanceListNew);
            List<WorkflowInstanceHasTransition> attachedWorkflowInstanceHasTransitionListNew = new ArrayList<>();
            for (WorkflowInstanceHasTransition workflowInstanceHasTransitionListNewWorkflowInstanceHasTransitionToAttach : workflowInstanceHasTransitionListNew) {
                workflowInstanceHasTransitionListNewWorkflowInstanceHasTransitionToAttach = em.getReference(workflowInstanceHasTransitionListNewWorkflowInstanceHasTransitionToAttach.getClass(), workflowInstanceHasTransitionListNewWorkflowInstanceHasTransitionToAttach.getWorkflowInstanceHasTransitionPK());
                attachedWorkflowInstanceHasTransitionListNew.add(workflowInstanceHasTransitionListNewWorkflowInstanceHasTransitionToAttach);
            }
            workflowInstanceHasTransitionListNew = attachedWorkflowInstanceHasTransitionListNew;
            vmUser.setWorkflowInstanceHasTransitionList(workflowInstanceHasTransitionListNew);
            vmUser = em.merge(vmUser);
            if (userStatusIdOld != null && !userStatusIdOld.equals(userStatusIdNew)) {
                userStatusIdOld.getVmUserList().remove(vmUser);
                userStatusIdOld = em.merge(userStatusIdOld);
            }
            if (userStatusIdNew != null && !userStatusIdNew.equals(userStatusIdOld)) {
                userStatusIdNew.getVmUserList().add(vmUser);
                userStatusIdNew = em.merge(userStatusIdNew);
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
            for (RootCause rootCauseListOldRootCause : rootCauseListOld) {
                if (!rootCauseListNew.contains(rootCauseListOldRootCause)) {
                    rootCauseListOldRootCause.getVmUserList().remove(vmUser);
                    rootCauseListOldRootCause = em.merge(rootCauseListOldRootCause);
                }
            }
            for (RootCause rootCauseListNewRootCause : rootCauseListNew) {
                if (!rootCauseListOld.contains(rootCauseListNewRootCause)) {
                    rootCauseListNewRootCause.getVmUserList().add(vmUser);
                    rootCauseListNewRootCause = em.merge(rootCauseListNewRootCause);
                }
            }
            for (ExecutionStep executionStepListOldExecutionStep : executionStepListOld) {
                if (!executionStepListNew.contains(executionStepListOldExecutionStep)) {
                    executionStepListOldExecutionStep.setAssignee(null);
                    executionStepListOldExecutionStep = em.merge(executionStepListOldExecutionStep);
                }
            }
            for (ExecutionStep executionStepListNewExecutionStep : executionStepListNew) {
                if (!executionStepListOld.contains(executionStepListNewExecutionStep)) {
                    VmUser oldAssigneeOfExecutionStepListNewExecutionStep = executionStepListNewExecutionStep.getAssignee();
                    executionStepListNewExecutionStep.setAssignee(vmUser);
                    executionStepListNewExecutionStep = em.merge(executionStepListNewExecutionStep);
                    if (oldAssigneeOfExecutionStepListNewExecutionStep != null && !oldAssigneeOfExecutionStepListNewExecutionStep.equals(vmUser)) {
                        oldAssigneeOfExecutionStepListNewExecutionStep.getExecutionStepList().remove(executionStepListNewExecutionStep);
                        oldAssigneeOfExecutionStepListNewExecutionStep = em.merge(oldAssigneeOfExecutionStepListNewExecutionStep);
                    }
                }
            }
            for (ExecutionStep executionStepList1OldExecutionStep : executionStepList1Old) {
                if (!executionStepList1New.contains(executionStepList1OldExecutionStep)) {
                    executionStepList1OldExecutionStep.setAssigner(null);
                    executionStepList1OldExecutionStep = em.merge(executionStepList1OldExecutionStep);
                }
            }
            for (ExecutionStep executionStepList1NewExecutionStep : executionStepList1New) {
                if (!executionStepList1Old.contains(executionStepList1NewExecutionStep)) {
                    VmUser oldAssignerOfExecutionStepList1NewExecutionStep = executionStepList1NewExecutionStep.getAssigner();
                    executionStepList1NewExecutionStep.setAssigner(vmUser);
                    executionStepList1NewExecutionStep = em.merge(executionStepList1NewExecutionStep);
                    if (oldAssignerOfExecutionStepList1NewExecutionStep != null && !oldAssignerOfExecutionStepList1NewExecutionStep.equals(vmUser)) {
                        oldAssignerOfExecutionStepList1NewExecutionStep.getExecutionStepList1().remove(executionStepList1NewExecutionStep);
                        oldAssignerOfExecutionStepList1NewExecutionStep = em.merge(oldAssignerOfExecutionStepList1NewExecutionStep);
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
            for (ExecutionStepHasIssue executionStepHasIssueListOldExecutionStepHasIssue : executionStepHasIssueListOld) {
                if (!executionStepHasIssueListNew.contains(executionStepHasIssueListOldExecutionStepHasIssue)) {
                    executionStepHasIssueListOldExecutionStepHasIssue.getVmUserList().remove(vmUser);
                    executionStepHasIssueListOldExecutionStepHasIssue = em.merge(executionStepHasIssueListOldExecutionStepHasIssue);
                }
            }
            for (ExecutionStepHasIssue executionStepHasIssueListNewExecutionStepHasIssue : executionStepHasIssueListNew) {
                if (!executionStepHasIssueListOld.contains(executionStepHasIssueListNewExecutionStepHasIssue)) {
                    executionStepHasIssueListNewExecutionStepHasIssue.getVmUserList().add(vmUser);
                    executionStepHasIssueListNewExecutionStepHasIssue = em.merge(executionStepHasIssueListNewExecutionStepHasIssue);
                }
            }
            for (ExecutionStepHasVmUser executionStepHasVmUserListNewExecutionStepHasVmUser : executionStepHasVmUserListNew) {
                if (!executionStepHasVmUserListOld.contains(executionStepHasVmUserListNewExecutionStepHasVmUser)) {
                    VmUser oldVmUserOfExecutionStepHasVmUserListNewExecutionStepHasVmUser = executionStepHasVmUserListNewExecutionStepHasVmUser.getVmUser();
                    executionStepHasVmUserListNewExecutionStepHasVmUser.setVmUser(vmUser);
                    executionStepHasVmUserListNewExecutionStepHasVmUser = em.merge(executionStepHasVmUserListNewExecutionStepHasVmUser);
                    if (oldVmUserOfExecutionStepHasVmUserListNewExecutionStepHasVmUser != null && !oldVmUserOfExecutionStepHasVmUserListNewExecutionStepHasVmUser.equals(vmUser)) {
                        oldVmUserOfExecutionStepHasVmUserListNewExecutionStepHasVmUser.getExecutionStepHasVmUserList().remove(executionStepHasVmUserListNewExecutionStepHasVmUser);
                        oldVmUserOfExecutionStepHasVmUserListNewExecutionStepHasVmUser = em.merge(oldVmUserOfExecutionStepHasVmUserListNewExecutionStepHasVmUser);
                    }
                }
            }
            for (History historyModificationListNewHistory : historyModificationListNew) {
                if (!historyModificationListOld.contains(historyModificationListNewHistory)) {
                    VmUser oldModifierIdOfHistoryModificationListNewHistory = historyModificationListNewHistory.getModifierId();
                    historyModificationListNewHistory.setModifierId(vmUser);
                    historyModificationListNewHistory = em.merge(historyModificationListNewHistory);
                    if (oldModifierIdOfHistoryModificationListNewHistory != null && !oldModifierIdOfHistoryModificationListNewHistory.equals(vmUser)) {
                        oldModifierIdOfHistoryModificationListNewHistory.getHistoryModificationList().remove(historyModificationListNewHistory);
                        oldModifierIdOfHistoryModificationListNewHistory = em.merge(oldModifierIdOfHistoryModificationListNewHistory);
                    }
                }
            }
            for (Notification notificationListNewNotification : notificationListNew) {
                if (!notificationListOld.contains(notificationListNewNotification)) {
                    VmUser oldTargetUserOfNotificationListNewNotification = notificationListNewNotification.getTargetUser();
                    notificationListNewNotification.setTargetUser(vmUser);
                    notificationListNewNotification = em.merge(notificationListNewNotification);
                    if (oldTargetUserOfNotificationListNewNotification != null && !oldTargetUserOfNotificationListNewNotification.equals(vmUser)) {
                        oldTargetUserOfNotificationListNewNotification.getNotificationList().remove(notificationListNewNotification);
                        oldTargetUserOfNotificationListNewNotification = em.merge(oldTargetUserOfNotificationListNewNotification);
                    }
                }
            }
            for (Notification notificationList1NewNotification : notificationList1New) {
                if (!notificationList1Old.contains(notificationList1NewNotification)) {
                    VmUser oldAuthorOfNotificationList1NewNotification = notificationList1NewNotification.getAuthor();
                    notificationList1NewNotification.setAuthor(vmUser);
                    notificationList1NewNotification = em.merge(notificationList1NewNotification);
                    if (oldAuthorOfNotificationList1NewNotification != null && !oldAuthorOfNotificationList1NewNotification.equals(vmUser)) {
                        oldAuthorOfNotificationList1NewNotification.getNotificationList1().remove(notificationList1NewNotification);
                        oldAuthorOfNotificationList1NewNotification = em.merge(oldAuthorOfNotificationList1NewNotification);
                    }
                }
            }
            for (History historyListNewHistory : historyListNew) {
                if (!historyListOld.contains(historyListNewHistory)) {
                    VmUser oldVmUserIdOfHistoryListNewHistory = historyListNewHistory.getVmUserId();
                    historyListNewHistory.setVmUserId(vmUser);
                    historyListNewHistory = em.merge(historyListNewHistory);
                    if (oldVmUserIdOfHistoryListNewHistory != null && !oldVmUserIdOfHistoryListNewHistory.equals(vmUser)) {
                        oldVmUserIdOfHistoryListNewHistory.getHistoryList().remove(historyListNewHistory);
                        oldVmUserIdOfHistoryListNewHistory = em.merge(oldVmUserIdOfHistoryListNewHistory);
                    }
                }
            }
            for (UserHasRole userHasRoleListNewUserHasRole : userHasRoleListNew) {
                if (!userHasRoleListOld.contains(userHasRoleListNewUserHasRole)) {
                    VmUser oldVmUserOfUserHasRoleListNewUserHasRole = userHasRoleListNewUserHasRole.getVmUser();
                    userHasRoleListNewUserHasRole.setVmUser(vmUser);
                    userHasRoleListNewUserHasRole = em.merge(userHasRoleListNewUserHasRole);
                    if (oldVmUserOfUserHasRoleListNewUserHasRole != null && !oldVmUserOfUserHasRoleListNewUserHasRole.equals(vmUser)) {
                        oldVmUserOfUserHasRoleListNewUserHasRole.getUserHasRoleList().remove(userHasRoleListNewUserHasRole);
                        oldVmUserOfUserHasRoleListNewUserHasRole = em.merge(oldVmUserOfUserHasRoleListNewUserHasRole);
                    }
                }
            }
            for (Activity activityListNewActivity : activityListNew) {
                if (!activityListOld.contains(activityListNewActivity)) {
                    VmUser oldSourceUserOfActivityListNewActivity = activityListNewActivity.getSourceUser();
                    activityListNewActivity.setSourceUser(vmUser);
                    activityListNewActivity = em.merge(activityListNewActivity);
                    if (oldSourceUserOfActivityListNewActivity != null && !oldSourceUserOfActivityListNewActivity.equals(vmUser)) {
                        oldSourceUserOfActivityListNewActivity.getActivityList().remove(activityListNewActivity);
                        oldSourceUserOfActivityListNewActivity = em.merge(oldSourceUserOfActivityListNewActivity);
                    }
                }
            }
            for (WorkflowInstance workflowInstanceListOldWorkflowInstance : workflowInstanceListOld) {
                if (!workflowInstanceListNew.contains(workflowInstanceListOldWorkflowInstance)) {
                    workflowInstanceListOldWorkflowInstance.setAssignedUser(null);
                    workflowInstanceListOldWorkflowInstance = em.merge(workflowInstanceListOldWorkflowInstance);
                }
            }
            for (WorkflowInstance workflowInstanceListNewWorkflowInstance : workflowInstanceListNew) {
                if (!workflowInstanceListOld.contains(workflowInstanceListNewWorkflowInstance)) {
                    VmUser oldAssignedUserOfWorkflowInstanceListNewWorkflowInstance = workflowInstanceListNewWorkflowInstance.getAssignedUser();
                    workflowInstanceListNewWorkflowInstance.setAssignedUser(vmUser);
                    workflowInstanceListNewWorkflowInstance = em.merge(workflowInstanceListNewWorkflowInstance);
                    if (oldAssignedUserOfWorkflowInstanceListNewWorkflowInstance != null && !oldAssignedUserOfWorkflowInstanceListNewWorkflowInstance.equals(vmUser)) {
                        oldAssignedUserOfWorkflowInstanceListNewWorkflowInstance.getWorkflowInstanceList().remove(workflowInstanceListNewWorkflowInstance);
                        oldAssignedUserOfWorkflowInstanceListNewWorkflowInstance = em.merge(oldAssignedUserOfWorkflowInstanceListNewWorkflowInstance);
                    }
                }
            }
            for (WorkflowInstanceHasTransition workflowInstanceHasTransitionListNewWorkflowInstanceHasTransition : workflowInstanceHasTransitionListNew) {
                if (!workflowInstanceHasTransitionListOld.contains(workflowInstanceHasTransitionListNewWorkflowInstanceHasTransition)) {
                    VmUser oldTransitionerOfWorkflowInstanceHasTransitionListNewWorkflowInstanceHasTransition = workflowInstanceHasTransitionListNewWorkflowInstanceHasTransition.getTransitioner();
                    workflowInstanceHasTransitionListNewWorkflowInstanceHasTransition.setTransitioner(vmUser);
                    workflowInstanceHasTransitionListNewWorkflowInstanceHasTransition = em.merge(workflowInstanceHasTransitionListNewWorkflowInstanceHasTransition);
                    if (oldTransitionerOfWorkflowInstanceHasTransitionListNewWorkflowInstanceHasTransition != null && !oldTransitionerOfWorkflowInstanceHasTransitionListNewWorkflowInstanceHasTransition.equals(vmUser)) {
                        oldTransitionerOfWorkflowInstanceHasTransitionListNewWorkflowInstanceHasTransition.getWorkflowInstanceHasTransitionList().remove(workflowInstanceHasTransitionListNewWorkflowInstanceHasTransition);
                        oldTransitionerOfWorkflowInstanceHasTransitionListNewWorkflowInstanceHasTransition = em.merge(oldTransitionerOfWorkflowInstanceHasTransitionListNewWorkflowInstanceHasTransition);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = vmUser.getId();
                if (findVmUser(id) == null) {
                    throw new NonexistentEntityException("The vmUser with id " + id + " no longer exists.");
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
            VmUser vmUser;
            try {
                vmUser = em.getReference(VmUser.class, id);
                vmUser.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The vmUser with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<UserTestProjectRole> userTestProjectRoleListOrphanCheck = vmUser.getUserTestProjectRoleList();
            for (UserTestProjectRole userTestProjectRoleListOrphanCheckUserTestProjectRole : userTestProjectRoleListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This VmUser (" + vmUser + ") cannot be destroyed since the UserTestProjectRole " + userTestProjectRoleListOrphanCheckUserTestProjectRole + " in its userTestProjectRoleList field has a non-nullable vmUser field.");
            }
            List<UserTestPlanRole> userTestPlanRoleListOrphanCheck = vmUser.getUserTestPlanRoleList();
            for (UserTestPlanRole userTestPlanRoleListOrphanCheckUserTestPlanRole : userTestPlanRoleListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This VmUser (" + vmUser + ") cannot be destroyed since the UserTestPlanRole " + userTestPlanRoleListOrphanCheckUserTestPlanRole + " in its userTestPlanRoleList field has a non-nullable vmUser field.");
            }
            List<UserModifiedRecord> userModifiedRecordListOrphanCheck = vmUser.getUserModifiedRecordList();
            for (UserModifiedRecord userModifiedRecordListOrphanCheckUserModifiedRecord : userModifiedRecordListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This VmUser (" + vmUser + ") cannot be destroyed since the UserModifiedRecord " + userModifiedRecordListOrphanCheckUserModifiedRecord + " in its userModifiedRecordList field has a non-nullable vmUser field.");
            }
            List<UserHasInvestigation> userHasInvestigationListOrphanCheck = vmUser.getUserHasInvestigationList();
            for (UserHasInvestigation userHasInvestigationListOrphanCheckUserHasInvestigation : userHasInvestigationListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This VmUser (" + vmUser + ") cannot be destroyed since the UserHasInvestigation " + userHasInvestigationListOrphanCheckUserHasInvestigation + " in its userHasInvestigationList field has a non-nullable vmUser field.");
            }
            List<UserAssigment> userAssigmentListOrphanCheck = vmUser.getUserAssigmentList();
            for (UserAssigment userAssigmentListOrphanCheckUserAssigment : userAssigmentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This VmUser (" + vmUser + ") cannot be destroyed since the UserAssigment " + userAssigmentListOrphanCheckUserAssigment + " in its userAssigmentList field has a non-nullable vmUser field.");
            }
            List<UserAssigment> userAssigmentList1OrphanCheck = vmUser.getUserAssigmentList1();
            for (UserAssigment userAssigmentList1OrphanCheckUserAssigment : userAssigmentList1OrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This VmUser (" + vmUser + ") cannot be destroyed since the UserAssigment " + userAssigmentList1OrphanCheckUserAssigment + " in its userAssigmentList1 field has a non-nullable assigneeId field.");
            }
            List<ExecutionStepHasVmUser> executionStepHasVmUserListOrphanCheck = vmUser.getExecutionStepHasVmUserList();
            for (ExecutionStepHasVmUser executionStepHasVmUserListOrphanCheckExecutionStepHasVmUser : executionStepHasVmUserListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This VmUser (" + vmUser + ") cannot be destroyed since the ExecutionStepHasVmUser " + executionStepHasVmUserListOrphanCheckExecutionStepHasVmUser + " in its executionStepHasVmUserList field has a non-nullable vmUser field.");
            }
            List<History> historyModificationListOrphanCheck = vmUser.getHistoryModificationList();
            for (History historyModificationListOrphanCheckHistory : historyModificationListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This VmUser (" + vmUser + ") cannot be destroyed since the History " + historyModificationListOrphanCheckHistory + " in its historyModificationList field has a non-nullable modifierId field.");
            }
            List<Notification> notificationListOrphanCheck = vmUser.getNotificationList();
            for (Notification notificationListOrphanCheckNotification : notificationListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This VmUser (" + vmUser + ") cannot be destroyed since the Notification " + notificationListOrphanCheckNotification + " in its notificationList field has a non-nullable targetUser field.");
            }
            List<Notification> notificationList1OrphanCheck = vmUser.getNotificationList1();
            for (Notification notificationList1OrphanCheckNotification : notificationList1OrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This VmUser (" + vmUser + ") cannot be destroyed since the Notification " + notificationList1OrphanCheckNotification + " in its notificationList1 field has a non-nullable author field.");
            }
            List<History> historyListOrphanCheck = vmUser.getHistoryList();
            for (History historyListOrphanCheckHistory : historyListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This VmUser (" + vmUser + ") cannot be destroyed since the History " + historyListOrphanCheckHistory + " in its historyList field has a non-nullable vmUserId field.");
            }
            List<UserHasRole> userHasRoleListOrphanCheck = vmUser.getUserHasRoleList();
            for (UserHasRole userHasRoleListOrphanCheckUserHasRole : userHasRoleListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This VmUser (" + vmUser + ") cannot be destroyed since the UserHasRole " + userHasRoleListOrphanCheckUserHasRole + " in its userHasRoleList field has a non-nullable vmUser field.");
            }
            List<Activity> activityListOrphanCheck = vmUser.getActivityList();
            for (Activity activityListOrphanCheckActivity : activityListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This VmUser (" + vmUser + ") cannot be destroyed since the Activity " + activityListOrphanCheckActivity + " in its activityList field has a non-nullable sourceUser field.");
            }
            List<WorkflowInstanceHasTransition> workflowInstanceHasTransitionListOrphanCheck = vmUser.getWorkflowInstanceHasTransitionList();
            for (WorkflowInstanceHasTransition workflowInstanceHasTransitionListOrphanCheckWorkflowInstanceHasTransition : workflowInstanceHasTransitionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This VmUser (" + vmUser + ") cannot be destroyed since the WorkflowInstanceHasTransition " + workflowInstanceHasTransitionListOrphanCheckWorkflowInstanceHasTransition + " in its workflowInstanceHasTransitionList field has a non-nullable transitioner field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            UserStatus userStatusId = vmUser.getUserStatusId();
            if (userStatusId != null) {
                userStatusId.getVmUserList().remove(vmUser);
                userStatusId = em.merge(userStatusId);
            }
            List<CorrectiveAction> correctiveActionList = vmUser.getCorrectiveActionList();
            for (CorrectiveAction correctiveActionListCorrectiveAction : correctiveActionList) {
                correctiveActionListCorrectiveAction.getVmUserList().remove(vmUser);
                correctiveActionListCorrectiveAction = em.merge(correctiveActionListCorrectiveAction);
            }
            List<Role> roleList = vmUser.getRoleList();
            for (Role roleListRole : roleList) {
                roleListRole.getVmUserList().remove(vmUser);
                roleListRole = em.merge(roleListRole);
            }
            List<RootCause> rootCauseList = vmUser.getRootCauseList();
            for (RootCause rootCauseListRootCause : rootCauseList) {
                rootCauseListRootCause.getVmUserList().remove(vmUser);
                rootCauseListRootCause = em.merge(rootCauseListRootCause);
            }
            List<ExecutionStep> executionStepList = vmUser.getExecutionStepList();
            for (ExecutionStep executionStepListExecutionStep : executionStepList) {
                executionStepListExecutionStep.setAssignee(null);
                executionStepListExecutionStep = em.merge(executionStepListExecutionStep);
            }
            List<ExecutionStep> executionStepList1 = vmUser.getExecutionStepList1();
            for (ExecutionStep executionStepList1ExecutionStep : executionStepList1) {
                executionStepList1ExecutionStep.setAssigner(null);
                executionStepList1ExecutionStep = em.merge(executionStepList1ExecutionStep);
            }
            List<ExecutionStepHasIssue> executionStepHasIssueList = vmUser.getExecutionStepHasIssueList();
            for (ExecutionStepHasIssue executionStepHasIssueListExecutionStepHasIssue : executionStepHasIssueList) {
                executionStepHasIssueListExecutionStepHasIssue.getVmUserList().remove(vmUser);
                executionStepHasIssueListExecutionStepHasIssue = em.merge(executionStepHasIssueListExecutionStepHasIssue);
            }
            List<WorkflowInstance> workflowInstanceList = vmUser.getWorkflowInstanceList();
            for (WorkflowInstance workflowInstanceListWorkflowInstance : workflowInstanceList) {
                workflowInstanceListWorkflowInstance.setAssignedUser(null);
                workflowInstanceListWorkflowInstance = em.merge(workflowInstanceListWorkflowInstance);
            }
            em.remove(vmUser);
            em.getTransaction().commit();
        }
        finally {
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
        }
        finally {
            em.close();
        }
    }

    public VmUser findVmUser(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(VmUser.class, id);
        }
        finally {
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
        }
        finally {
            em.close();
        }
    }

}
