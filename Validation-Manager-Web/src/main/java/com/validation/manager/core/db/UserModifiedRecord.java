/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "user_modified_record")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserModifiedRecord.findAll", query = "SELECT u FROM UserModifiedRecord u"),
    @NamedQuery(name = "UserModifiedRecord.findByUserId", query = "SELECT u FROM UserModifiedRecord u WHERE u.userModifiedRecordPK.userId = :userId"),
    @NamedQuery(name = "UserModifiedRecord.findByRecordId", query = "SELECT u FROM UserModifiedRecord u WHERE u.userModifiedRecordPK.recordId = :recordId"),
    @NamedQuery(name = "UserModifiedRecord.findByModifiedDate", query = "SELECT u FROM UserModifiedRecord u WHERE u.modifiedDate = :modifiedDate"),
    @NamedQuery(name = "UserModifiedRecord.findByReason", query = "SELECT u FROM UserModifiedRecord u WHERE u.reason = :reason")})
public class UserModifiedRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected UserModifiedRecordPK userModifiedRecordPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "modified_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "reason", nullable = false, length = 45)
    private String reason;
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private VmUser vmUser;

    public UserModifiedRecord() {
    }

    public UserModifiedRecord(int user_id) {
        this.userModifiedRecordPK = new UserModifiedRecordPK(user_id);
    }

    public UserModifiedRecord(int user_id, Date modifiedDate, String reason) {
        this.userModifiedRecordPK = new UserModifiedRecordPK(user_id);
        this.modifiedDate = modifiedDate;
        this.reason = reason;
    }

    public UserModifiedRecordPK getUserModifiedRecordPK() {
        return userModifiedRecordPK;
    }

    public void setUserModifiedRecordPK(UserModifiedRecordPK userModifiedRecordPK) {
        this.userModifiedRecordPK = userModifiedRecordPK;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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
        hash += (userModifiedRecordPK != null ? userModifiedRecordPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserModifiedRecord)) {
            return false;
        }
        UserModifiedRecord other = (UserModifiedRecord) object;
        if ((this.userModifiedRecordPK == null && other.userModifiedRecordPK != null) || (this.userModifiedRecordPK != null && !this.userModifiedRecordPK.equals(other.userModifiedRecordPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.UserModifiedRecord[ userModifiedRecordPK=" + userModifiedRecordPK + " ]";
    }
    
}
