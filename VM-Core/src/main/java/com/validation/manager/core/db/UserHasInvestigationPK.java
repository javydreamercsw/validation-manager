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
public class UserHasInvestigationPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "user_id")
    private int userId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "investigation_id")
    private int investigationId;

    public UserHasInvestigationPK() {
    }

    public UserHasInvestigationPK(int userId, int investigationId) {
        this.userId = userId;
        this.investigationId = investigationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getInvestigationId() {
        return investigationId;
    }

    public void setInvestigationId(int investigationId) {
        this.investigationId = investigationId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) userId;
        hash += (int) investigationId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserHasInvestigationPK)) {
            return false;
        }
        UserHasInvestigationPK other = (UserHasInvestigationPK) object;
        if (this.userId != other.userId) {
            return false;
        }
        return this.investigationId == other.investigationId;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.UserHasInvestigationPK[ userId=" + userId + ", investigationId=" + investigationId + " ]";
    }

}
