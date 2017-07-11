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
public class DataEntryPropertyPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @GeneratedValue(strategy = GenerationType.TABLE,
            generator = "Data_Entry_Type_IDGEN")
    @TableGenerator(name = "Data_Entry_Type_IDGEN", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "data_entry_type",
            initialValue = 0,
            allocationSize = 1)
    @Column(name = "id")
    private int id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "data_entry_id")
    private int dataEntryId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "data_entry_data_entry_type_id")
    private int dataEntryDataEntryTypeId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "data_entry_step_id")
    private int dataEntryStepId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "data_entry_step_test_case_id")
    private int dataEntryStepTestCaseId;

    public DataEntryPropertyPK() {
    }

    public DataEntryPropertyPK(int id, int dataEntryId, int dataEntryDataEntryTypeId, int dataEntryStepId, int dataEntryStepTestCaseId) {
        this.id = id;
        this.dataEntryId = dataEntryId;
        this.dataEntryDataEntryTypeId = dataEntryDataEntryTypeId;
        this.dataEntryStepId = dataEntryStepId;
        this.dataEntryStepTestCaseId = dataEntryStepTestCaseId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDataEntryId() {
        return dataEntryId;
    }

    public void setDataEntryId(int dataEntryId) {
        this.dataEntryId = dataEntryId;
    }

    public int getDataEntryDataEntryTypeId() {
        return dataEntryDataEntryTypeId;
    }

    public void setDataEntryDataEntryTypeId(int dataEntryDataEntryTypeId) {
        this.dataEntryDataEntryTypeId = dataEntryDataEntryTypeId;
    }

    public int getDataEntryStepId() {
        return dataEntryStepId;
    }

    public void setDataEntryStepId(int dataEntryStepId) {
        this.dataEntryStepId = dataEntryStepId;
    }

    public int getDataEntryStepTestCaseId() {
        return dataEntryStepTestCaseId;
    }

    public void setDataEntryStepTestCaseId(int dataEntryStepTestCaseId) {
        this.dataEntryStepTestCaseId = dataEntryStepTestCaseId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) dataEntryId;
        hash += (int) dataEntryDataEntryTypeId;
        hash += (int) dataEntryStepId;
        hash += (int) dataEntryStepTestCaseId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DataEntryPropertyPK)) {
            return false;
        }
        DataEntryPropertyPK other = (DataEntryPropertyPK) object;
        if (this.id != other.id) {
            return false;
        }
        if (this.dataEntryId != other.dataEntryId) {
            return false;
        }
        if (this.dataEntryDataEntryTypeId != other.dataEntryDataEntryTypeId) {
            return false;
        }
        if (this.dataEntryStepId != other.dataEntryStepId) {
            return false;
        }
        if (this.dataEntryStepTestCaseId != other.dataEntryStepTestCaseId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.DataEntryPropertyPK[ id=" + id + ", dataEntryId=" + dataEntryId + ", dataEntryDataEntryTypeId=" + dataEntryDataEntryTypeId + ", dataEntryStepId=" + dataEntryStepId + ", dataEntryStepTestCaseId=" + dataEntryStepTestCaseId + " ]";
    }

}
