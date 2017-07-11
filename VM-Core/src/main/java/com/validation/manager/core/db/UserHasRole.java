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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "user_has_role")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserHasRole.findAll",
            query = "SELECT u FROM UserHasRole u")
    , @NamedQuery(name = "UserHasRole.findByUserId",
            query = "SELECT u FROM UserHasRole u WHERE u.userHasRolePK.userId = :userId")
    , @NamedQuery(name = "UserHasRole.findByRoleId",
            query = "SELECT u FROM UserHasRole u WHERE u.userHasRolePK.roleId = :roleId")})
public class UserHasRole implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected UserHasRolePK userHasRolePK;
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    @ManyToOne
    private Project projectId;
    @JoinColumn(name = "role_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Role role;
    @JoinColumn(name = "user_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private VmUser vmUser;

    public UserHasRole() {
    }

    public UserHasRole(UserHasRolePK userHasRolePK) {
        this.userHasRolePK = userHasRolePK;
    }

    public UserHasRole(int userId, int roleId) {
        this.userHasRolePK = new UserHasRolePK(userId, roleId);
    }

    public UserHasRolePK getUserHasRolePK() {
        return userHasRolePK;
    }

    public void setUserHasRolePK(UserHasRolePK userHasRolePK) {
        this.userHasRolePK = userHasRolePK;
    }

    public Project getProjectId() {
        return projectId;
    }

    public void setProjectId(Project projectId) {
        this.projectId = projectId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public VmUser getVmUser() {
        return vmUser;
    }

    public void setVmUser(VmUser vmUser) {
        this.vmUser = vmUser;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userHasRolePK != null ? userHasRolePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserHasRole)) {
            return false;
        }
        UserHasRole other = (UserHasRole) object;
        return !((this.userHasRolePK == null && other.userHasRolePK != null)
                || (this.userHasRolePK != null
                && !this.userHasRolePK.equals(other.userHasRolePK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.UserHasRole[ userHasRolePK="
                + userHasRolePK + " ]";
    }

}
