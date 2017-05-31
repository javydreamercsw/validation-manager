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
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "user_modified_record")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserModifiedRecord.findAll",
            query = "SELECT u FROM UserModifiedRecord u")
    , @NamedQuery(name = "UserModifiedRecord.findByUserId",
            query = "SELECT u FROM UserModifiedRecord u WHERE "
            + "u.userModifiedRecordPK.userId = :userId")
    , @NamedQuery(name = "UserModifiedRecord.findByRecordId",
            query = "SELECT u FROM UserModifiedRecord u WHERE "
            + "u.userModifiedRecordPK.recordId = :recordId")
    , @NamedQuery(name = "UserModifiedRecord.findByModifiedDate",
            query = "SELECT u FROM UserModifiedRecord u WHERE "
            + "u.modifiedDate = :modifiedDate")
    , @NamedQuery(name = "UserModifiedRecord.findByReason",
            query = "SELECT u FROM UserModifiedRecord u WHERE "
            + "u.reason = :reason")})
public class UserModifiedRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected UserModifiedRecordPK userModifiedRecordPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "reason")
    private String reason;
    @JoinColumn(name = "user_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private VmUser vmUser;

    public UserModifiedRecord() {
    }

    public UserModifiedRecord(UserModifiedRecordPK userModifiedRecordPK) {
        this.userModifiedRecordPK = userModifiedRecordPK;
    }

    public UserModifiedRecord(UserModifiedRecordPK userModifiedRecordPK,
            Date modifiedDate, String reason) {
        this.userModifiedRecordPK = userModifiedRecordPK;
        this.modifiedDate = modifiedDate;
        this.reason = reason;
    }

    public UserModifiedRecord(int userId, int recordId) {
        this.userModifiedRecordPK = new UserModifiedRecordPK(userId, recordId);
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
        hash += (userModifiedRecordPK != null
                ? userModifiedRecordPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof UserModifiedRecord)) {
            return false;
        }
        UserModifiedRecord other = (UserModifiedRecord) object;
        return !((this.userModifiedRecordPK == null
                && other.userModifiedRecordPK != null)
                || (this.userModifiedRecordPK != null
                && !this.userModifiedRecordPK.equals(other.userModifiedRecordPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.UserModifiedRecord[ "
                + "userModifiedRecordPK=" + userModifiedRecordPK + " ]";
    }
}
