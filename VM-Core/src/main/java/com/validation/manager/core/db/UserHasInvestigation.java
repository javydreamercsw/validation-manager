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
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "user_has_investigation")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserHasInvestigation.findAll",
            query = "SELECT u FROM UserHasInvestigation u")
    , @NamedQuery(name = "UserHasInvestigation.findByUserId",
            query = "SELECT u FROM UserHasInvestigation u WHERE "
            + "u.userHasInvestigationPK.userId = :userId")
    , @NamedQuery(name = "UserHasInvestigation.findByInvestigationId",
            query = "SELECT u FROM UserHasInvestigation u WHERE "
            + "u.userHasInvestigationPK.investigationId = :investigationId")
    , @NamedQuery(name = "UserHasInvestigation.findByStartDate",
            query = "SELECT u FROM UserHasInvestigation u WHERE "
            + "u.startDate = :startDate")
    , @NamedQuery(name = "UserHasInvestigation.findByCloseDate",
            query = "SELECT u FROM UserHasInvestigation u WHERE "
            + "u.closeDate = :closeDate")})
public class UserHasInvestigation implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected UserHasInvestigationPK userHasInvestigationPK;
    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Column(name = "close_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date closeDate;
    @JoinColumn(name = "investigation_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Investigation investigation;
    @JoinColumn(name = "user_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private VmUser vmUser;

    public UserHasInvestigation() {
    }

    public UserHasInvestigation(UserHasInvestigationPK userHasInvestigationPK) {
        this.userHasInvestigationPK = userHasInvestigationPK;
    }

    public UserHasInvestigation(int userId, int investigationId) {
        this.userHasInvestigationPK
                = new UserHasInvestigationPK(userId, investigationId);
    }

    public UserHasInvestigationPK getUserHasInvestigationPK() {
        return userHasInvestigationPK;
    }

    public void setUserHasInvestigationPK(UserHasInvestigationPK userHasInvestigationPK) {
        this.userHasInvestigationPK = userHasInvestigationPK;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public Investigation getInvestigation() {
        return investigation;
    }

    public void setInvestigation(Investigation investigation) {
        this.investigation = investigation;
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
        hash += (userHasInvestigationPK != null ? userHasInvestigationPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof UserHasInvestigation)) {
            return false;
        }
        UserHasInvestigation other = (UserHasInvestigation) object;
        return !((this.userHasInvestigationPK == null
                && other.userHasInvestigationPK != null)
                || (this.userHasInvestigationPK != null
                && !this.userHasInvestigationPK.equals(other.userHasInvestigationPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.UserHasInvestigation[ "
                + "userHasInvestigationPK=" + userHasInvestigationPK + " ]";
    }
}
