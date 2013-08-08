/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "user_has_role")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserHasRole.findAll", query = "SELECT u FROM UserHasRole u"),
    @NamedQuery(name = "UserHasRole.findByUserId", query = "SELECT u FROM UserHasRole u WHERE u.userHasRolePK.userId = :userId"),
    @NamedQuery(name = "UserHasRole.findByRoleId", query = "SELECT u FROM UserHasRole u WHERE u.userHasRolePK.roleId = :roleId")})
public class UserHasRole implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected UserHasRolePK userHasRolePK;
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private VmUser vmUser;
    @JoinColumn(name = "role_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Role role;

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

    public VmUser getVmUser() {
        return vmUser;
    }

    public void setVmUser(VmUser vmUser) {
        this.vmUser = vmUser;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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
        if ((this.userHasRolePK == null && other.userHasRolePK != null) || (this.userHasRolePK != null && !this.userHasRolePK.equals(other.userHasRolePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.UserHasRole[ userHasRolePK=" + userHasRolePK + " ]";
    }
    
}
