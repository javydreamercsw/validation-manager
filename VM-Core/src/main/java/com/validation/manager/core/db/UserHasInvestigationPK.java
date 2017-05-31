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
        return "com.validation.manager.core.db.UserHasInvestigationPK[ userId="
                + userId + ", investigationId=" + investigationId + " ]";
    }
}
