/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "user_has_investigation")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserHasInvestigation.findAll", query = "SELECT u FROM UserHasInvestigation u"),
    @NamedQuery(name = "UserHasInvestigation.findByCloseDate", query = "SELECT u FROM UserHasInvestigation u WHERE u.closeDate = :closeDate"),
    @NamedQuery(name = "UserHasInvestigation.findByStartDate", query = "SELECT u FROM UserHasInvestigation u WHERE u.startDate = :startDate"),
    @NamedQuery(name = "UserHasInvestigation.findByUserId", query = "SELECT u FROM UserHasInvestigation u WHERE u.userHasInvestigationPK.userId = :userId"),
    @NamedQuery(name = "UserHasInvestigation.findByInvestigationId", query = "SELECT u FROM UserHasInvestigation u WHERE u.userHasInvestigationPK.investigationId = :investigationId")})
public class UserHasInvestigation implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected UserHasInvestigationPK userHasInvestigationPK;
    @Column(name = "close_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date closeDate;
    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private VmUser vmUser;
    @JoinColumn(name = "investigation_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Investigation investigation;

    public UserHasInvestigation() {
    }

    public UserHasInvestigation(UserHasInvestigationPK userHasInvestigationPK) {
        this.userHasInvestigationPK = userHasInvestigationPK;
    }

    public UserHasInvestigation(int userId, int investigationId) {
        this.userHasInvestigationPK = new UserHasInvestigationPK(userId, investigationId);
    }

    public UserHasInvestigationPK getUserHasInvestigationPK() {
        return userHasInvestigationPK;
    }

    public void setUserHasInvestigationPK(UserHasInvestigationPK userHasInvestigationPK) {
        this.userHasInvestigationPK = userHasInvestigationPK;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public VmUser getVmUser() {
        return vmUser;
    }

    public void setVmUser(VmUser vmUser) {
        this.vmUser = vmUser;
    }

    public Investigation getInvestigation() {
        return investigation;
    }

    public void setInvestigation(Investigation investigation) {
        this.investigation = investigation;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userHasInvestigationPK != null ? userHasInvestigationPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserHasInvestigation)) {
            return false;
        }
        UserHasInvestigation other = (UserHasInvestigation) object;
        return (this.userHasInvestigationPK != null || other.userHasInvestigationPK == null) && (this.userHasInvestigationPK == null || this.userHasInvestigationPK.equals(other.userHasInvestigationPK));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.UserHasInvestigation[ userHasInvestigationPK=" + userHasInvestigationPK + " ]";
    }

}
