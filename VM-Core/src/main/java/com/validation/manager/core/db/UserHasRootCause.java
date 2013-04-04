/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import com.validation.manager.core.db.fmea.RootCause;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "user_has_root_cause")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserHasRootCause.findAll", query = "SELECT u FROM UserHasRootCause u"),
    @NamedQuery(name = "UserHasRootCause.findByUserId", query = "SELECT u FROM UserHasRootCause u WHERE u.userHasRootCausePK.userId = :userId"),
    @NamedQuery(name = "UserHasRootCause.findByRootCauseId", query = "SELECT u FROM UserHasRootCause u WHERE u.userHasRootCausePK.rootCauseId = :rootCauseId"),
    @NamedQuery(name = "UserHasRootCause.findByRootCauseRootCauseTypeId", query = "SELECT u FROM UserHasRootCause u WHERE u.userHasRootCausePK.rootCauseRootCauseTypeId = :rootCauseRootCauseTypeId"),
    @NamedQuery(name = "UserHasRootCause.findByStartDate", query = "SELECT u FROM UserHasRootCause u WHERE u.startDate = :startDate"),
    @NamedQuery(name = "UserHasRootCause.findByEndDate", query = "SELECT u FROM UserHasRootCause u WHERE u.endDate = :endDate")})
public class UserHasRootCause implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected UserHasRootCausePK userHasRootCausePK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private VmUser vmUser;
    @JoinColumns({
        @JoinColumn(name = "root_cause_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "root_cause_root_cause_type_id", referencedColumnName = "root_cause_type_id", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private RootCause rootCause;

    public UserHasRootCause() {
    }

    public UserHasRootCause(UserHasRootCausePK userHasRootCausePK) {
        this.userHasRootCausePK = userHasRootCausePK;
    }

    public UserHasRootCause(UserHasRootCausePK userHasRootCausePK, Date startDate) {
        this.userHasRootCausePK = userHasRootCausePK;
        this.startDate = startDate;
    }

    public UserHasRootCause(int userId, int rootCauseId, int rootCauseRootCauseTypeId) {
        this.userHasRootCausePK = new UserHasRootCausePK(userId, rootCauseId, rootCauseRootCauseTypeId);
    }

    public UserHasRootCausePK getUserHasRootCausePK() {
        return userHasRootCausePK;
    }

    public void setUserHasRootCausePK(UserHasRootCausePK userHasRootCausePK) {
        this.userHasRootCausePK = userHasRootCausePK;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public VmUser getVmUser() {
        return vmUser;
    }

    public void setVmUser(VmUser vmUser) {
        this.vmUser = vmUser;
    }

    public RootCause getRootCause() {
        return rootCause;
    }

    public void setRootCause(RootCause rootCause) {
        this.rootCause = rootCause;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userHasRootCausePK != null ? userHasRootCausePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserHasRootCause)) {
            return false;
        }
        UserHasRootCause other = (UserHasRootCause) object;
        if ((this.userHasRootCausePK == null && other.userHasRootCausePK != null) || (this.userHasRootCausePK != null && !this.userHasRootCausePK.equals(other.userHasRootCausePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.UserHasRootCause[ userHasRootCausePK=" + userHasRootCausePK + " ]";
    }
    
}
