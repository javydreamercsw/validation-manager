package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Embeddable
public class UserTestProjectRolePK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "test_project_id")
    private int testProjectId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "user_id")
    private int userId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "role_id")
    private int roleId;

    public UserTestProjectRolePK() {
    }

    public UserTestProjectRolePK(int testProjectId, int userId, int roleId) {
        this.testProjectId = testProjectId;
        this.userId = userId;
        this.roleId = roleId;
    }

    public int getTestProjectId() {
        return testProjectId;
    }

    public void setTestProjectId(int testProjectId) {
        this.testProjectId = testProjectId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) testProjectId;
        hash += (int) userId;
        hash += (int) roleId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserTestProjectRolePK)) {
            return false;
        }
        UserTestProjectRolePK other = (UserTestProjectRolePK) object;
        if (this.testProjectId != other.testProjectId) {
            return false;
        }
        if (this.userId != other.userId) {
            return false;
        }
        return this.roleId == other.roleId;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.UserTestProjectRolePK[ "
                + "testProjectId=" + testProjectId + ", userId=" + userId
                + ", roleId=" + roleId + " ]";
    }
}
