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
@Table(name = "activity_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ActivityType.findAll",
            query = "SELECT a FROM ActivityType a")
    , @NamedQuery(name = "ActivityType.findById",
            query = "SELECT a FROM ActivityType a WHERE a.id = :id")
    , @NamedQuery(name = "ActivityType.findByTypeName",
            query = "SELECT a FROM ActivityType a WHERE a.typeName = :typeName")})
public class ActivityType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE,
            generator = "Activity_Type_IDGEN")
    @TableGenerator(name = "Activity_Type_IDGEN", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "activity_type",
            initialValue = 1_000,
            allocationSize = 1)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "type_name")
    private String typeName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "activityType")
    private List<Activity> activityList;

    public ActivityType() {
    }

    public ActivityType(String typeName) {
        this.typeName = typeName;
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

    @XmlTransient
    @JsonIgnore
    public List<Activity> getActivityList() {
        return activityList;
    }

    public void setActivityList(List<Activity> activityList) {
        this.activityList = activityList;
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
        if (!(object instanceof ActivityType)) {
            return false;
        }
        ActivityType other = (ActivityType) object;
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.ActivityType[ id=" + id + " ]";
    }
}
