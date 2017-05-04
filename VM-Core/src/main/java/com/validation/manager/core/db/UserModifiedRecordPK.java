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
public class UserModifiedRecordPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "user_id")
    private int userId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "record_id")
    private int recordId;

    public UserModifiedRecordPK() {
    }

    public UserModifiedRecordPK(int userId, int recordId) {
        this.userId = userId;
        this.recordId = recordId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) userId;
        hash += (int) recordId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof UserModifiedRecordPK)) {
            return false;
        }
        UserModifiedRecordPK other = (UserModifiedRecordPK) object;
        if (this.userId != other.userId) {
            return false;
        }
        return this.recordId == other.recordId;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.UserModifiedRecordPK[ userId="
                + userId + ", recordId=" + recordId + " ]";
    }
}
