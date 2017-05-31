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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "exception_has_corrective_action")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ExceptionHasCorrectiveAction.findAll",
            query = "SELECT e FROM ExceptionHasCorrectiveAction e")
    , @NamedQuery(name = "ExceptionHasCorrectiveAction.findByExceptionId",
            query = "SELECT e FROM ExceptionHasCorrectiveAction e WHERE "
            + "e.exceptionHasCorrectiveActionPK.exceptionId = :exceptionId")
    , @NamedQuery(name = "ExceptionHasCorrectiveAction.findByExceptionReporterId",
            query = "SELECT e FROM ExceptionHasCorrectiveAction e WHERE "
            + "e.exceptionHasCorrectiveActionPK.exceptionReporterId = :exceptionReporterId")
    , @NamedQuery(name = "ExceptionHasCorrectiveAction.findByCorrectiveActionId",
            query = "SELECT e FROM ExceptionHasCorrectiveAction e WHERE "
            + "e.exceptionHasCorrectiveActionPK.correctiveActionId = :correctiveActionId")})
public class ExceptionHasCorrectiveAction implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ExceptionHasCorrectiveActionPK exceptionHasCorrectiveActionPK;
    @JoinColumn(name = "corrective_action_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private CorrectiveAction correctiveAction;

    public ExceptionHasCorrectiveAction() {
    }

    public ExceptionHasCorrectiveAction(ExceptionHasCorrectiveActionPK exceptionHasCorrectiveActionPK) {
        this.exceptionHasCorrectiveActionPK = exceptionHasCorrectiveActionPK;
    }

    public ExceptionHasCorrectiveAction(int exceptionId, int exceptionReporterId,
            int correctiveActionId) {
        this.exceptionHasCorrectiveActionPK
                = new ExceptionHasCorrectiveActionPK(exceptionId,
                        exceptionReporterId, correctiveActionId);
    }

    public ExceptionHasCorrectiveActionPK getExceptionHasCorrectiveActionPK() {
        return exceptionHasCorrectiveActionPK;
    }

    public void setExceptionHasCorrectiveActionPK(ExceptionHasCorrectiveActionPK exceptionHasCorrectiveActionPK) {
        this.exceptionHasCorrectiveActionPK = exceptionHasCorrectiveActionPK;
    }

    public CorrectiveAction getCorrectiveAction() {
        return correctiveAction;
    }

    public void setCorrectiveAction(CorrectiveAction correctiveAction) {
        this.correctiveAction = correctiveAction;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (exceptionHasCorrectiveActionPK != null
                ? exceptionHasCorrectiveActionPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof ExceptionHasCorrectiveAction)) {
            return false;
        }
        ExceptionHasCorrectiveAction other = (ExceptionHasCorrectiveAction) object;
        return !((this.exceptionHasCorrectiveActionPK == null
                && other.exceptionHasCorrectiveActionPK != null)
                || (this.exceptionHasCorrectiveActionPK != null
                && !this.exceptionHasCorrectiveActionPK
                        .equals(other.exceptionHasCorrectiveActionPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.ExceptionHasCorrectiveAction["
                + " exceptionHasCorrectiveActionPK="
                + exceptionHasCorrectiveActionPK + " ]";
    }
}
