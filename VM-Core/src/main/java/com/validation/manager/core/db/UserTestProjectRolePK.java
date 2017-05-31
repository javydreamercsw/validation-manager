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
