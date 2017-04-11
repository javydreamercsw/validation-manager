package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Embeddable
public class UserAssigmentPK implements Serializable {

    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE,
            generator = "UserAssignmentGEN")
    @TableGenerator(name = "UserAssignmentGEN",
            table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "user_assignment",
            initialValue = 1000,
            allocationSize = 1)
    @NotNull
    @Column(name = "id")
    private int id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "assigner_id")
    private int assignerId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "assigment_type_id")
    private int assigmentTypeId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "assignment_status_id")
    private int assignmentStatusId;

    public UserAssigmentPK() {
    }

    public UserAssigmentPK(int assignerId, int assigmentTypeId,
            int assignmentStatusId) {
        this.assignerId = assignerId;
        this.assigmentTypeId = assigmentTypeId;
        this.assignmentStatusId = assignmentStatusId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAssignerId() {
        return assignerId;
    }

    public void setAssignerId(int assignerId) {
        this.assignerId = assignerId;
    }

    public int getAssigmentTypeId() {
        return assigmentTypeId;
    }

    public void setAssigmentTypeId(int assigmentTypeId) {
        this.assigmentTypeId = assigmentTypeId;
    }

    public int getAssignmentStatusId() {
        return assignmentStatusId;
    }

    public void setAssignmentStatusId(int assignmentStatusId) {
        this.assignmentStatusId = assignmentStatusId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) assignerId;
        hash += (int) assigmentTypeId;
        hash += (int) assignmentStatusId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserAssigmentPK)) {
            return false;
        }
        UserAssigmentPK other = (UserAssigmentPK) object;
        if (this.id != other.id) {
            return false;
        }
        if (this.assignerId != other.assignerId) {
            return false;
        }
        if (this.assigmentTypeId != other.assigmentTypeId) {
            return false;
        }
        return this.assignmentStatusId == other.assignmentStatusId;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.UserAssigmentPK[ id=" + id
                + ", assignerId=" + assignerId + ", assigmentTypeId="
                + assigmentTypeId + ", assignmentStatusId="
                + assignmentStatusId + " ]";
    }
}
