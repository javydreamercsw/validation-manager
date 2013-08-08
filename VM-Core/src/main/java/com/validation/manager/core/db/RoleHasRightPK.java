/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Embeddable
public class RoleHasRightPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "role_id")
    private int roleId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "right_id")
    private int rightId;

    public RoleHasRightPK() {
    }

    public RoleHasRightPK(int roleId, int rightId) {
        this.roleId = roleId;
        this.rightId = rightId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getRightId() {
        return rightId;
    }

    public void setRightId(int rightId) {
        this.rightId = rightId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) roleId;
        hash += (int) rightId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RoleHasRightPK)) {
            return false;
        }
        RoleHasRightPK other = (RoleHasRightPK) object;
        if (this.roleId != other.roleId) {
            return false;
        }
        if (this.rightId != other.rightId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RoleHasRightPK[ roleId=" + roleId + ", rightId=" + rightId + " ]";
    }
    
}
