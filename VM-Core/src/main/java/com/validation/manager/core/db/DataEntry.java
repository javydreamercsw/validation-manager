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
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "data_entry")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DataEntry.findAll", query = "SELECT d FROM DataEntry d")
    , @NamedQuery(name = "DataEntry.findById", query = "SELECT d FROM DataEntry d WHERE d.dataEntryPK.id = :id")
    , @NamedQuery(name = "DataEntry.findByDataEntryTypeId", query = "SELECT d FROM DataEntry d WHERE d.dataEntryPK.dataEntryTypeId = :dataEntryTypeId")
    , @NamedQuery(name = "DataEntry.findByStepId", query = "SELECT d FROM DataEntry d WHERE d.dataEntryPK.stepId = :stepId")
    , @NamedQuery(name = "DataEntry.findByStepTestCaseId", query = "SELECT d FROM DataEntry d WHERE d.dataEntryPK.stepTestCaseId = :stepTestCaseId")
    , @NamedQuery(name = "DataEntry.findByEntryName", query = "SELECT d FROM DataEntry d WHERE d.entryName = :entryName")})
public class DataEntry implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected DataEntryPK dataEntryPK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "entry_name")
    private String entryName;
    @JoinColumn(name = "data_entry_type_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private DataEntryType dataEntryType;
    @JoinColumns({
        @JoinColumn(name = "step_id", referencedColumnName = "id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "step_test_case_id",
                referencedColumnName = "test_case_id", insertable = false,
                updatable = false)})
    @ManyToOne(optional = false)
    private Step step;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataEntry")
    private List<DataEntryProperty> dataEntryPropertyList;

    public DataEntry() {
    }

    public DataEntry(DataEntryPK dataEntryPK) {
        this.dataEntryPK = dataEntryPK;
    }

    public DataEntry(DataEntryPK dataEntryPK, String entryName) {
        this.dataEntryPK = dataEntryPK;
        this.entryName = entryName;
    }

    public DataEntry(int id, int dataEntryTypeId, int stepId, int stepTestCaseId) {
        this.dataEntryPK = new DataEntryPK(id, dataEntryTypeId, stepId, stepTestCaseId);
    }

    public DataEntryPK getDataEntryPK() {
        return dataEntryPK;
    }

    public void setDataEntryPK(DataEntryPK dataEntryPK) {
        this.dataEntryPK = dataEntryPK;
    }

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    public DataEntryType getDataEntryType() {
        return dataEntryType;
    }

    public void setDataEntryType(DataEntryType dataEntryType) {
        this.dataEntryType = dataEntryType;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dataEntryPK != null ? dataEntryPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DataEntry)) {
            return false;
        }
        DataEntry other = (DataEntry) object;
        return !((this.dataEntryPK == null && other.dataEntryPK != null)
                || (this.dataEntryPK != null
                && !this.dataEntryPK.equals(other.dataEntryPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.DataEntry[ dataEntryPK="
                + dataEntryPK + " ]";
    }

    @XmlTransient
    @JsonIgnore
    public List<DataEntryProperty> getDataEntryPropertyList() {
        return dataEntryPropertyList;
    }

    public void setDataEntryPropertyList(List<DataEntryProperty> dataEntryPropertyList) {
        this.dataEntryPropertyList = dataEntryPropertyList;
    }
}
