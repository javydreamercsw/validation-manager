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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "data_entry_property")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DataEntryProperty.findAll",
            query = "SELECT d FROM DataEntryProperty d")
    , @NamedQuery(name = "DataEntryProperty.findById",
            query = "SELECT d FROM DataEntryProperty d WHERE d.dataEntryPropertyPK.id = :id")
    , @NamedQuery(name = "DataEntryProperty.findByDataEntryId",
            query = "SELECT d FROM DataEntryProperty d WHERE d.dataEntryPropertyPK.dataEntryId = :dataEntryId")
    , @NamedQuery(name = "DataEntryProperty.findByDataEntryDataEntryTypeId",
            query = "SELECT d FROM DataEntryProperty d WHERE d.dataEntryPropertyPK.dataEntryDataEntryTypeId = :dataEntryDataEntryTypeId")
    , @NamedQuery(name = "DataEntryProperty.findByDataEntryStepId",
            query = "SELECT d FROM DataEntryProperty d WHERE d.dataEntryPropertyPK.dataEntryStepId = :dataEntryStepId")
    , @NamedQuery(name = "DataEntryProperty.findByDataEntryStepTestCaseId",
            query = "SELECT d FROM DataEntryProperty d WHERE d.dataEntryPropertyPK.dataEntryStepTestCaseId = :dataEntryStepTestCaseId")
    , @NamedQuery(name = "DataEntryProperty.findByPropertyName",
            query = "SELECT d FROM DataEntryProperty d WHERE d.propertyName = :propertyName")})
public class DataEntryProperty implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected DataEntryPropertyPK dataEntryPropertyPK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "property_name")
    private String propertyName;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 2147483647)
    @Column(name = "property_value")
    private String propertyValue;
    @JoinColumns({
        @JoinColumn(name = "data_entry_id", referencedColumnName = "id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "data_entry_data_entry_type_id",
                referencedColumnName = "data_entry_type_id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "data_entry_step_id",
                referencedColumnName = "step_id", insertable = false,
                updatable = false)
        , @JoinColumn(name = "data_entry_step_test_case_id",
                referencedColumnName = "step_test_case_id",
                insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private DataEntry dataEntry;

    public DataEntryProperty() {
    }

    public DataEntryProperty(DataEntryPropertyPK dataEntryPropertyPK) {
        this.dataEntryPropertyPK = dataEntryPropertyPK;
    }

    public DataEntryProperty(DataEntryPropertyPK dataEntryPropertyPK,
            String propertyName, String propertyValue) {
        this.dataEntryPropertyPK = dataEntryPropertyPK;
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

    public DataEntryProperty(int id, int dataEntryId,
            int dataEntryDataEntryTypeId, int dataEntryStepId,
            int dataEntryStepTestCaseId) {
        this.dataEntryPropertyPK = new DataEntryPropertyPK(id,
                dataEntryId, dataEntryDataEntryTypeId, dataEntryStepId,
                dataEntryStepTestCaseId);
    }

    public DataEntryPropertyPK getDataEntryPropertyPK() {
        return dataEntryPropertyPK;
    }

    public void setDataEntryPropertyPK(DataEntryPropertyPK dataEntryPropertyPK) {
        this.dataEntryPropertyPK = dataEntryPropertyPK;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public DataEntry getDataEntry() {
        return dataEntry;
    }

    public void setDataEntry(DataEntry dataEntry) {
        this.dataEntry = dataEntry;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dataEntryPropertyPK != null ? dataEntryPropertyPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DataEntryProperty)) {
            return false;
        }
        DataEntryProperty other = (DataEntryProperty) object;
        return !((this.dataEntryPropertyPK == null
                && other.dataEntryPropertyPK != null)
                || (this.dataEntryPropertyPK != null
                && !this.dataEntryPropertyPK.equals(other.dataEntryPropertyPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.DataEntryProperty[ dataEntryPropertyPK="
                + dataEntryPropertyPK + " ]";
    }
}
