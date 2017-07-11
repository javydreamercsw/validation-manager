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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
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
@Table(name = "data_entry_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DataEntryType.findAll",
            query = "SELECT d FROM DataEntryType d")
    , @NamedQuery(name = "DataEntryType.findById",
            query = "SELECT d FROM DataEntryType d WHERE d.id = :id")
    , @NamedQuery(name = "DataEntryType.findByTypeName",
            query = "SELECT d FROM DataEntryType d WHERE d.typeName = :typeName")})
public class DataEntryType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "type_name")
    private String typeName;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 2147483647)
    @Column(name = "type_description")
    private String typeDescription;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataEntryType")
    private List<DataEntry> dataEntryList;

    public DataEntryType() {
    }

    public DataEntryType(Integer id) {
        this.id = id;
    }

    public DataEntryType(Integer id, String typeName, String typeDescription) {
        this.id = id;
        this.typeName = typeName;
        this.typeDescription = typeDescription;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeDescription() {
        return typeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }

    @XmlTransient
    @JsonIgnore
    public List<DataEntry> getDataEntryList() {
        return dataEntryList;
    }

    public void setDataEntryList(List<DataEntry> dataEntryList) {
        this.dataEntryList = dataEntryList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DataEntryType)) {
            return false;
        }
        DataEntryType other = (DataEntryType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.DataEntryType[ id=" + id + " ]";
    }

}
