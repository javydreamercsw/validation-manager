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
@Table(name = "assignment_status")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AssignmentStatus.findAll",
            query = "SELECT a FROM AssignmentStatus a")
    , @NamedQuery(name = "AssignmentStatus.findById",
            query = "SELECT a FROM AssignmentStatus a WHERE a.id = :id")
    , @NamedQuery(name = "AssignmentStatus.findByName",
            query = "SELECT a FROM AssignmentStatus a WHERE a.name = :name")})
public class AssignmentStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE,
            generator = "Assignment_Status_IDGEN")
    @TableGenerator(name = "Assignment_Status_IDGEN", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "assigment_status",
            initialValue = 1_000,
            allocationSize = 1)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(max = 2_147_483_647)
    @Column(name = "description")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "assignmentStatus")
    private List<UserAssigment> userAssigmentList;

    public AssignmentStatus() {
    }

    public AssignmentStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    @JsonIgnore
    public List<UserAssigment> getUserAssigmentList() {
        return userAssigmentList;
    }

    public void setUserAssigmentList(List<UserAssigment> userAssigmentList) {
        this.userAssigmentList = userAssigmentList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof AssignmentStatus)) {
            return false;
        }
        AssignmentStatus other = (AssignmentStatus) object;
        return (this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.AssignmentStatus[ id=" + id + " ]";
    }
}
