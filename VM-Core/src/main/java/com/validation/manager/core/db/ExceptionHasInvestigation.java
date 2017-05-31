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
@Table(name = "exception_has_investigation")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ExceptionHasInvestigation.findAll",
            query = "SELECT e FROM ExceptionHasInvestigation e")
    , @NamedQuery(name = "ExceptionHasInvestigation.findByExceptionId",
            query = "SELECT e FROM ExceptionHasInvestigation e WHERE "
            + "e.exceptionHasInvestigationPK.exceptionId = :exceptionId")
    , @NamedQuery(name = "ExceptionHasInvestigation.findByExceptionReporterId",
            query = "SELECT e FROM ExceptionHasInvestigation e WHERE "
            + "e.exceptionHasInvestigationPK.exceptionReporterId = :exceptionReporterId")
    , @NamedQuery(name = "ExceptionHasInvestigation.findByInvestigationId",
            query = "SELECT e FROM ExceptionHasInvestigation e WHERE "
            + "e.exceptionHasInvestigationPK.investigationId = :investigationId")})
public class ExceptionHasInvestigation implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ExceptionHasInvestigationPK exceptionHasInvestigationPK;
    @JoinColumn(name = "investigation_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Investigation investigation;

    public ExceptionHasInvestigation() {
    }

    public ExceptionHasInvestigation(ExceptionHasInvestigationPK exceptionHasInvestigationPK) {
        this.exceptionHasInvestigationPK = exceptionHasInvestigationPK;
    }

    public ExceptionHasInvestigation(int exceptionId, int exceptionReporterId,
            int investigationId) {
        this.exceptionHasInvestigationPK
                = new ExceptionHasInvestigationPK(exceptionId,
                        exceptionReporterId, investigationId);
    }

    public ExceptionHasInvestigationPK getExceptionHasInvestigationPK() {
        return exceptionHasInvestigationPK;
    }

    public void setExceptionHasInvestigationPK(ExceptionHasInvestigationPK exceptionHasInvestigationPK) {
        this.exceptionHasInvestigationPK = exceptionHasInvestigationPK;
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
        hash += (exceptionHasInvestigationPK != null
                ? exceptionHasInvestigationPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof ExceptionHasInvestigation)) {
            return false;
        }
        ExceptionHasInvestigation other = (ExceptionHasInvestigation) object;
        return !((this.exceptionHasInvestigationPK == null
                && other.exceptionHasInvestigationPK != null)
                || (this.exceptionHasInvestigationPK != null
                && !this.exceptionHasInvestigationPK
                        .equals(other.exceptionHasInvestigationPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.ExceptionHasInvestigation[ "
                + "exceptionHasInvestigationPK=" + exceptionHasInvestigationPK
                + " ]";
    }
}
