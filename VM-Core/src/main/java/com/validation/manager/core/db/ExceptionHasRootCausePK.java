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
public class ExceptionHasRootCausePK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "exception_id")
    private int exceptionId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "exception_reporter_id")
    private int exceptionReporterId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "root_cause_id")
    private int rootCauseId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "root_cause_root_cause_type_id")
    private int rootCauseRootCauseTypeId;

    public ExceptionHasRootCausePK() {
    }

    public ExceptionHasRootCausePK(int exceptionId, int exceptionReporterId,
            int rootCauseId, int rootCauseRootCauseTypeId) {
        this.exceptionId = exceptionId;
        this.exceptionReporterId = exceptionReporterId;
        this.rootCauseId = rootCauseId;
        this.rootCauseRootCauseTypeId = rootCauseRootCauseTypeId;
    }

    public int getExceptionId() {
        return exceptionId;
    }

    public void setExceptionId(int exceptionId) {
        this.exceptionId = exceptionId;
    }

    public int getExceptionReporterId() {
        return exceptionReporterId;
    }

    public void setExceptionReporterId(int exceptionReporterId) {
        this.exceptionReporterId = exceptionReporterId;
    }

    public int getRootCauseId() {
        return rootCauseId;
    }

    public void setRootCauseId(int rootCauseId) {
        this.rootCauseId = rootCauseId;
    }

    public int getRootCauseRootCauseTypeId() {
        return rootCauseRootCauseTypeId;
    }

    public void setRootCauseRootCauseTypeId(int rootCauseRootCauseTypeId) {
        this.rootCauseRootCauseTypeId = rootCauseRootCauseTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) exceptionId;
        hash += (int) exceptionReporterId;
        hash += (int) rootCauseId;
        hash += (int) rootCauseRootCauseTypeId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof ExceptionHasRootCausePK)) {
            return false;
        }
        ExceptionHasRootCausePK other = (ExceptionHasRootCausePK) object;
        if (this.exceptionId != other.exceptionId) {
            return false;
        }
        if (this.exceptionReporterId != other.exceptionReporterId) {
            return false;
        }
        if (this.rootCauseId != other.rootCauseId) {
            return false;
        }
        return this.rootCauseRootCauseTypeId == other.rootCauseRootCauseTypeId;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.ExceptionHasRootCausePK[ exceptionId="
                + exceptionId + ", exceptionReporterId=" + exceptionReporterId
                + ", rootCauseId=" + rootCauseId + ", rootCauseRootCauseTypeId="
                + rootCauseRootCauseTypeId + " ]";
    }
}
