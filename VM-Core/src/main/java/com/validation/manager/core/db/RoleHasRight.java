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
@Table(name = "role_has_right")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RoleHasRight.findAll", query = "SELECT r FROM RoleHasRight r"),
    @NamedQuery(name = "RoleHasRight.findByRoleId", query = "SELECT r FROM RoleHasRight r WHERE r.roleHasRightPK.roleId = :roleId"),
    @NamedQuery(name = "RoleHasRight.findByRightId", query = "SELECT r FROM RoleHasRight r WHERE r.roleHasRightPK.rightId = :rightId")})
public class RoleHasRight implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RoleHasRightPK roleHasRightPK;
    @JoinColumn(name = "role_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Role role;
    @JoinColumn(name = "right_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private UserRight userRight;

    public RoleHasRight() {
    }

    public RoleHasRight(RoleHasRightPK roleHasRightPK) {
        this.roleHasRightPK = roleHasRightPK;
    }

    public RoleHasRight(int roleId, int rightId) {
        this.roleHasRightPK = new RoleHasRightPK(roleId, rightId);
    }

    public RoleHasRightPK getRoleHasRightPK() {
        return roleHasRightPK;
    }

    public void setRoleHasRightPK(RoleHasRightPK roleHasRightPK) {
        this.roleHasRightPK = roleHasRightPK;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UserRight getUserRight() {
        return userRight;
    }

    public void setUserRight(UserRight userRight) {
        this.userRight = userRight;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roleHasRightPK != null ? roleHasRightPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RoleHasRight)) {
            return false;
        }
        RoleHasRight other = (RoleHasRight) object;
        if ((this.roleHasRightPK == null && other.roleHasRightPK != null) || (this.roleHasRightPK != null && !this.roleHasRightPK.equals(other.roleHasRightPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RoleHasRight[ roleHasRightPK=" + roleHasRightPK + " ]";
    }
    
}
