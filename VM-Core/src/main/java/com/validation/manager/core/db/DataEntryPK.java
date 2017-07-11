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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Embeddable
public class DataEntryPK implements Serializable {

    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE,
            generator = "Data_Entry_IDGEN")
    @TableGenerator(name = "Data_Entry_IDGEN", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "data_entry",
            initialValue = 0,
            allocationSize = 1)
    @Column(name = "id")
    private int id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "data_entry_type_id")
    private int dataEntryTypeId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "step_id")
    private int stepId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "step_test_case_id")
    private int stepTestCaseId;

    public DataEntryPK() {
    }

    public DataEntryPK(int id, int dataEntryTypeId, int stepId, int stepTestCaseId) {
        this.id = id;
        this.dataEntryTypeId = dataEntryTypeId;
        this.stepId = stepId;
        this.stepTestCaseId = stepTestCaseId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDataEntryTypeId() {
        return dataEntryTypeId;
    }

    public void setDataEntryTypeId(int dataEntryTypeId) {
        this.dataEntryTypeId = dataEntryTypeId;
    }

    public int getStepId() {
        return stepId;
    }

    public void setStepId(int stepId) {
        this.stepId = stepId;
    }

    public int getStepTestCaseId() {
        return stepTestCaseId;
    }

    public void setStepTestCaseId(int stepTestCaseId) {
        this.stepTestCaseId = stepTestCaseId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) dataEntryTypeId;
        hash += (int) stepId;
        hash += (int) stepTestCaseId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DataEntryPK)) {
            return false;
        }
        DataEntryPK other = (DataEntryPK) object;
        if (this.id != other.id) {
            return false;
        }
        if (this.dataEntryTypeId != other.dataEntryTypeId) {
            return false;
        }
        if (this.stepId != other.stepId) {
            return false;
        }
        if (this.stepTestCaseId != other.stepTestCaseId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.DataEntryPK[ id=" + id + ", dataEntryTypeId=" + dataEntryTypeId + ", stepId=" + stepId + ", stepTestCaseId=" + stepTestCaseId + " ]";
    }

}
