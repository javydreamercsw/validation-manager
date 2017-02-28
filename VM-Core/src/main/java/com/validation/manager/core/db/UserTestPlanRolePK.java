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
public class UserTestPlanRolePK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "test_plan_id")
    private int testPlanId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "test_plan_test_project_id")
    private int testPlanTestProjectId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "user_id")
    private int userId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "role_id")
    private int roleId;

    public UserTestPlanRolePK() {
    }

    public UserTestPlanRolePK(int testPlanId, int testPlanTestProjectId,
            int userId, int roleId) {
        this.testPlanId = testPlanId;
        this.testPlanTestProjectId = testPlanTestProjectId;
        this.userId = userId;
        this.roleId = roleId;
    }

    public int getTestPlanId() {
        return testPlanId;
    }

    public void setTestPlanId(int testPlanId) {
        this.testPlanId = testPlanId;
    }

    public int getTestPlanTestProjectId() {
        return testPlanTestProjectId;
    }

    public void setTestPlanTestProjectId(int testPlanTestProjectId) {
        this.testPlanTestProjectId = testPlanTestProjectId;
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
        hash += (int) testPlanId;
        hash += (int) testPlanTestProjectId;
        hash += (int) userId;
        hash += (int) roleId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserTestPlanRolePK)) {
            return false;
        }
        UserTestPlanRolePK other = (UserTestPlanRolePK) object;
        if (this.testPlanId != other.testPlanId) {
            return false;
        }
        if (this.testPlanTestProjectId != other.testPlanTestProjectId) {
            return false;
        }
        if (this.userId != other.userId) {
            return false;
        }
        if (this.roleId != other.roleId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.UserTestPlanRolePK[ testPlanId="
                + testPlanId + ", testPlanTestProjectId=" + testPlanTestProjectId
                + ", userId=" + userId + ", roleId=" + roleId + " ]";
    }
}
