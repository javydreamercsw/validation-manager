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

import com.validation.manager.core.history.Auditable;
import com.validation.manager.core.history.Versionable;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
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
@Table(name = "vm_setting")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "VmSetting.findAll",
            query = "SELECT v FROM VmSetting v")
    , @NamedQuery(name = "VmSetting.findById",
            query = "SELECT v FROM VmSetting v WHERE v.id = :id")
    , @NamedQuery(name = "VmSetting.findBySetting",
            query = "SELECT v FROM VmSetting v WHERE v.setting = :setting")
    , @NamedQuery(name = "VmSetting.findByBoolVal",
            query = "SELECT v FROM VmSetting v WHERE v.boolVal = :boolVal")
    , @NamedQuery(name = "VmSetting.findByIntVal",
            query = "SELECT v FROM VmSetting v WHERE v.intVal = :intVal")})
public class VmSetting extends Versionable implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "VM_SettingGEN")
    @TableGenerator(name = "VM_SettingGEN",
            table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "vm_setting",
            initialValue = 1_000,
            allocationSize = 1)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Auditable
    @Column(name = "setting")
    private String setting;
    @Auditable
    @Column(name = "bool_val")
    private Boolean boolVal;
    @Auditable
    @Column(name = "int_val")
    private Integer intVal;
    @Lob
    @Size(max = 2_147_483_647)
    @Auditable
    @Column(name = "long_val")
    private String longVal;
    @Lob
    @Size(max = 2_147_483_647)
    @Auditable
    @Column(name = "string_val")
    private String stringVal;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vmSettingId")
    private List<History> historyList;
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    @ManyToOne
    private Project projectId;

    public VmSetting() {
    }

    public VmSetting(String setting) {
        this.setting = setting;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

    public Boolean getBoolVal() {
        return boolVal;
    }

    public void setBoolVal(Boolean boolVal) {
        this.boolVal = boolVal;
    }

    public Integer getIntVal() {
        return intVal;
    }

    public void setIntVal(Integer intVal) {
        this.intVal = intVal;
    }

    public String getLongVal() {
        return longVal;
    }

    public void setLongVal(String longVal) {
        this.longVal = longVal;
    }

    public String getStringVal() {
        return stringVal;
    }

    public void setStringVal(String stringVal) {
        this.stringVal = stringVal;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof VmSetting)) {
            return false;
        }
        VmSetting other = (VmSetting) object;
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.VmSetting[ id=" + id + " ]";
    }

    @XmlTransient
    @JsonIgnore
    @Override
    public List<History> getHistoryList() {
        return historyList;
    }

    /**
     * @param historyList the historyList to set
     */
    @Override
    public void setHistoryList(List<History> historyList) {
        this.historyList = historyList;
    }

    public Project getProjectId() {
        return projectId;
    }

    public void setProjectId(Project projectId) {
        this.projectId = projectId;
    }
}
