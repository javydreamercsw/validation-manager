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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
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
        return this.roleId == other.roleId;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.UserTestPlanRolePK[ testPlanId="
                + testPlanId + ", testPlanTestProjectId=" + testPlanTestProjectId
                + ", userId=" + userId + ", roleId=" + roleId + " ]";
    }
}
