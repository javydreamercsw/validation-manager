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
package com.validation.manager.core.db;

import com.validation.manager.core.history.Auditable;
import com.validation.manager.core.history.Versionable;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "vm_user")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "VmUser.findAll",
            query = "SELECT v FROM VmUser v")
    , @NamedQuery(name = "VmUser.findById",
            query = "SELECT v FROM VmUser v WHERE v.id = :id")
    , @NamedQuery(name = "VmUser.findByUsername",
            query = "SELECT v FROM VmUser v WHERE v.username = :username")
    , @NamedQuery(name = "VmUser.findByPassword",
            query = "SELECT v FROM VmUser v WHERE v.password = :password")
    , @NamedQuery(name = "VmUser.findByEmail",
            query = "SELECT v FROM VmUser v WHERE v.email = :email")
    , @NamedQuery(name = "VmUser.findByFirstName",
            query = "SELECT v FROM VmUser v WHERE v.firstName = :firstName")
    , @NamedQuery(name = "VmUser.findByLastName",
            query = "SELECT v FROM VmUser v WHERE v.lastName = :lastName")
    , @NamedQuery(name = "VmUser.findByLocale",
            query = "SELECT v FROM VmUser v WHERE v.locale = :locale")
    , @NamedQuery(name = "VmUser.findByAttempts",
            query = "SELECT v FROM VmUser v WHERE v.attempts = :attempts")})
public class VmUser extends Versionable implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sourceUser")
    private List<Activity> activityList;

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "VM_UserGEN")
    @TableGenerator(name = "VM_UserGEN",
            table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "vm_user",
            initialValue = 1_000,
            allocationSize = 1)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Auditable
    @Column(name = "username")
    private String username;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "password")
    @Auditable
    private String password;
    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`"
            + "{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:"
            + "[a-z0-9-]*[a-z0-9])?",
            message = "Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Auditable
    @Column(name = "email")
    private String email;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "first_name")
    @Auditable
    private String firstName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "last_name")
    @Auditable
    private String lastName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Auditable
    @Column(name = "locale")
    private String locale;
    @ManyToMany(mappedBy = "vmUserList")
    private List<CorrectiveAction> correctiveActionList;
    @ManyToMany(mappedBy = "vmUserList")
    private List<Role> roleList;
    @ManyToMany(mappedBy = "vmUserList")
    private List<RootCause> rootCauseList;
    @JoinColumn(name = "user_status_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private UserStatus userStatusId;
    @OneToMany(mappedBy = "assignee")
    private List<ExecutionStep> executionStepList;
    @OneToMany(mappedBy = "assigner")
    private List<ExecutionStep> executionStepList1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vmUser")
    private List<UserTestProjectRole> userTestProjectRoleList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vmUser")
    private List<UserTestPlanRole> userTestPlanRoleList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vmUser")
    private List<UserModifiedRecord> userModifiedRecordList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vmUser")
    private List<UserHasInvestigation> userHasInvestigationList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vmUser")
    private List<UserAssigment> userAssigmentList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "assigneeId")
    private List<UserAssigment> userAssigmentList1;
    @ManyToMany(mappedBy = "vmUserList")
    private List<ExecutionStepHasIssue> executionStepHasIssueList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vmUser")
    private List<ExecutionStepHasVmUser> executionStepHasVmUserList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "modifierId")
    private List<History> historyModificationList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "targetUser")
    private List<Notification> notificationList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "author")
    private List<Notification> notificationList1;
    @OneToMany(mappedBy = "vmUserId")
    private List<History> historyList;
    @Basic(optional = false)
    @NotNull
    @Column(name = "attempts")
    @Min(value = 0)
    private int attempts;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vmUser")
    private List<UserHasRole> userHasRoleList;
    @OneToMany(mappedBy = "assignedUser")
    private List<WorkflowInstance> workflowInstanceList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transitioner")
    private List<WorkflowInstanceHasTransition> workflowInstanceHasTransitionList;

    public VmUser() {
        super();
    }

    public VmUser(String username, String password, String email, String first,
            String last, String locale, UserStatus userStatus, int attempts) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = first;
        this.lastName = last;
        this.locale = locale;
        this.attempts = attempts;
        this.userStatusId = userStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    @XmlTransient
    @JsonIgnore
    public List<CorrectiveAction> getCorrectiveActionList() {
        return correctiveActionList;
    }

    public void setCorrectiveActionList(List<CorrectiveAction> correctiveActionList) {
        this.correctiveActionList = correctiveActionList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    @XmlTransient
    @JsonIgnore
    public List<RootCause> getRootCauseList() {
        return rootCauseList;
    }

    public void setRootCauseList(List<RootCause> rootCauseList) {
        this.rootCauseList = rootCauseList;
    }

    public UserStatus getUserStatusId() {
        return userStatusId;
    }

    public void setUserStatusId(UserStatus userStatusId) {
        this.userStatusId = userStatusId;
    }

    @XmlTransient
    @JsonIgnore
    public List<ExecutionStep> getExecutionStepList() {
        return executionStepList;
    }

    public void setExecutionStepList(List<ExecutionStep> executionStepList) {
        this.executionStepList = executionStepList;
    }

    @XmlTransient
    @JsonIgnore
    public List<ExecutionStep> getExecutionStepList1() {
        return executionStepList1;
    }

    public void setExecutionStepList1(List<ExecutionStep> executionStepList1) {
        this.executionStepList1 = executionStepList1;
    }

    @XmlTransient
    @JsonIgnore
    public List<UserTestProjectRole> getUserTestProjectRoleList() {
        return userTestProjectRoleList;
    }

    public void setUserTestProjectRoleList(List<UserTestProjectRole> userTestProjectRoleList) {
        this.userTestProjectRoleList = userTestProjectRoleList;
    }

    @XmlTransient
    @JsonIgnore
    public List<UserTestPlanRole> getUserTestPlanRoleList() {
        return userTestPlanRoleList;
    }

    public void setUserTestPlanRoleList(List<UserTestPlanRole> userTestPlanRoleList) {
        this.userTestPlanRoleList = userTestPlanRoleList;
    }

    @XmlTransient
    @JsonIgnore
    public List<UserModifiedRecord> getUserModifiedRecordList() {
        return userModifiedRecordList;
    }

    public void setUserModifiedRecordList(List<UserModifiedRecord> userModifiedRecordList) {
        this.userModifiedRecordList = userModifiedRecordList;
    }

    @XmlTransient
    @JsonIgnore
    public List<UserHasInvestigation> getUserHasInvestigationList() {
        return userHasInvestigationList;
    }

    public void setUserHasInvestigationList(List<UserHasInvestigation> userHasInvestigationList) {
        this.userHasInvestigationList = userHasInvestigationList;
    }

    @XmlTransient
    @JsonIgnore
    public List<UserAssigment> getUserAssigmentList() {
        return userAssigmentList;
    }

    public void setUserAssigmentList(List<UserAssigment> userAssigmentList) {
        this.userAssigmentList = userAssigmentList;
    }

    @XmlTransient
    @JsonIgnore
    public List<UserAssigment> getUserAssigmentList1() {
        return userAssigmentList1;
    }

    public void setUserAssigmentList1(List<UserAssigment> userAssigmentList1) {
        this.userAssigmentList1 = userAssigmentList1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof VmUser)) {
            return false;
        }
        VmUser other = (VmUser) object;
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.VmUser[ id=" + id + " ]";
    }

    @XmlTransient
    @JsonIgnore
    public List<ExecutionStepHasIssue> getExecutionStepHasIssueList() {
        return executionStepHasIssueList;
    }

    public void setExecutionStepHasIssueList(List<ExecutionStepHasIssue> executionStepHasIssueList) {
        this.executionStepHasIssueList = executionStepHasIssueList;
    }

    @XmlTransient
    @JsonIgnore
    public List<ExecutionStepHasVmUser> getExecutionStepHasVmUserList() {
        return executionStepHasVmUserList;
    }

    public void setExecutionStepHasVmUserList(List<ExecutionStepHasVmUser> executionStepHasVmUserList) {
        this.executionStepHasVmUserList = executionStepHasVmUserList;
    }

    @XmlTransient
    @JsonIgnore
    public List<History> getHistoryModificationList() {
        return historyModificationList;
    }

    public void setHistoryModificationList(List<History> historyList) {
        this.historyModificationList = historyList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Notification> getNotificationList() {
        return notificationList;
    }

    public void setNotificationList(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Notification> getNotificationList1() {
        return notificationList1;
    }

    public void setNotificationList1(List<Notification> notificationList1) {
        this.notificationList1 = notificationList1;
    }

    @XmlTransient
    @JsonIgnore
    @Override
    public List<History> getHistoryList() {
        return historyList;
    }

    @Override
    public void setHistoryList(List<History> historyList) {
        this.historyList = historyList;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    @XmlTransient
    @JsonIgnore
    public List<UserHasRole> getUserHasRoleList() {
        return userHasRoleList;
    }

    public void setUserHasRoleList(List<UserHasRole> userHasRoleList) {
        this.userHasRoleList = userHasRoleList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Activity> getActivityList() {
        return activityList;
    }

    public void setActivityList(List<Activity> activityList) {
        this.activityList = activityList;
    }

    @XmlTransient
    @JsonIgnore
    public List<WorkflowInstance> getWorkflowInstanceList() {
        return workflowInstanceList;
    }

    public void setWorkflowInstanceList(List<WorkflowInstance> workflowInstanceList) {
        this.workflowInstanceList = workflowInstanceList;
    }

    @XmlTransient
    @JsonIgnore
    public List<WorkflowInstanceHasTransition> getWorkflowInstanceHasTransitionList() {
        return workflowInstanceHasTransitionList;
    }

    public void setWorkflowInstanceHasTransitionList(List<WorkflowInstanceHasTransition> workflowInstanceHasTransitionList) {
        this.workflowInstanceHasTransitionList = workflowInstanceHasTransitionList;
    }
}
