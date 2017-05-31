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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
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
