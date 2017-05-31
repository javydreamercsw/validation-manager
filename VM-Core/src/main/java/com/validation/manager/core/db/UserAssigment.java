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

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "user_assigment")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserAssigment.findAll",
            query = "SELECT u FROM UserAssigment u")
    , @NamedQuery(name = "UserAssigment.findById",
            query = "SELECT u FROM UserAssigment u WHERE u.userAssigmentPK.id = :id")
    , @NamedQuery(name = "UserAssigment.findByAssignerId",
            query = "SELECT u FROM UserAssigment u WHERE "
            + "u.userAssigmentPK.assignerId = :assignerId")
    , @NamedQuery(name = "UserAssigment.findByAssigmentTypeId",
            query = "SELECT u FROM UserAssigment u WHERE "
            + "u.userAssigmentPK.assigmentTypeId = :assigmentTypeId")
    , @NamedQuery(name = "UserAssigment.findByAssignmentStatusId",
            query = "SELECT u FROM UserAssigment u WHERE "
            + "u.userAssigmentPK.assignmentStatusId = :assignmentStatusId")
    , @NamedQuery(name = "UserAssigment.findByDeadline",
            query = "SELECT u FROM UserAssigment u WHERE u.deadline = :deadline")
    , @NamedQuery(name = "UserAssigment.findByCreationTime",
            query = "SELECT u FROM UserAssigment u WHERE u.creationTime = :creationTime")})
public class UserAssigment implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected UserAssigmentPK userAssigmentPK;
    @Column(name = "deadline")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deadline;
    @Basic(optional = false)
    @NotNull
    @Column(name = "creation_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;
    @JoinColumn(name = "assigment_type_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private AssigmentType assigmentType;
    @JoinColumn(name = "assignment_status_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private AssignmentStatus assignmentStatus;
    @JoinColumn(name = "assigner_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private VmUser vmUser;
    @JoinColumn(name = "assignee_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private VmUser assigneeId;

    public UserAssigment() {
    }

    public UserAssigment(UserAssigmentPK userAssigmentPK) {
        this.userAssigmentPK = userAssigmentPK;
    }

    public UserAssigment(UserAssigmentPK userAssigmentPK, Date creationTime) {
        this.userAssigmentPK = userAssigmentPK;
        this.creationTime = creationTime;
    }

    public UserAssigment(int assignerId, int assigmentTypeId,
            int assignmentStatusId) {
        this.userAssigmentPK = new UserAssigmentPK(assignerId,
                assigmentTypeId, assignmentStatusId);
    }

    public UserAssigmentPK getUserAssigmentPK() {
        return userAssigmentPK;
    }

    public void setUserAssigmentPK(UserAssigmentPK userAssigmentPK) {
        this.userAssigmentPK = userAssigmentPK;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public AssigmentType getAssigmentType() {
        return assigmentType;
    }

    public void setAssigmentType(AssigmentType assigmentType) {
        this.assigmentType = assigmentType;
    }

    public AssignmentStatus getAssignmentStatus() {
        return assignmentStatus;
    }

    public void setAssignmentStatus(AssignmentStatus assignmentStatus) {
        this.assignmentStatus = assignmentStatus;
    }

    public VmUser getVmUser() {
        return vmUser;
    }

    public void setVmUser(VmUser vmUser) {
        this.vmUser = vmUser;
    }

    public VmUser getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(VmUser assigneeId) {
        this.assigneeId = assigneeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userAssigmentPK != null ? userAssigmentPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof UserAssigment)) {
            return false;
        }
        UserAssigment other = (UserAssigment) object;
        return !((this.userAssigmentPK == null && other.userAssigmentPK != null)
                || (this.userAssigmentPK != null
                && !this.userAssigmentPK.equals(other.userAssigmentPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.UserAssigment[ userAssigmentPK="
                + userAssigmentPK + " ]";
    }
}
