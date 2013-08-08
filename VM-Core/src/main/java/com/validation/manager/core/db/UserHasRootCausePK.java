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
public class UserHasRootCausePK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "user_id")
    private int userId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "root_cause_id")
    private int rootCauseId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "root_cause_root_cause_type_id")
    private int rootCauseRootCauseTypeId;

    public UserHasRootCausePK() {
    }

    public UserHasRootCausePK(int userId, int rootCauseId, int rootCauseRootCauseTypeId) {
        this.userId = userId;
        this.rootCauseId = rootCauseId;
        this.rootCauseRootCauseTypeId = rootCauseRootCauseTypeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRootCauseId() {
        return rootCauseId;
    }

    public void setRootCauseId(int rootCauseId) {
        this.rootCauseId = rootCauseId;
    }

    public int getRootCauseRootCauseTypeId() {
        return rootCauseRootCauseTypeId;
    }

    public void setRootCauseRootCauseTypeId(int rootCauseRootCauseTypeId) {
        this.rootCauseRootCauseTypeId = rootCauseRootCauseTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) rootCauseId;
        hash += (int) rootCauseRootCauseTypeId;
        hash += (int) userId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserHasRootCausePK)) {
            return false;
        }
        UserHasRootCausePK other = (UserHasRootCausePK) object;
        if (this.rootCauseId != other.rootCauseId) {
            return false;
        }
        if (this.rootCauseRootCauseTypeId != other.rootCauseRootCauseTypeId) {
            return false;
        }
        if (this.userId != other.userId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.UserHasRootCausePK[ rootCauseId=" + rootCauseId + ", rootCauseRootCauseTypeId=" + rootCauseRootCauseTypeId + ", userId=" + userId + " ]";
    }
    
}
