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
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
@Table(name = "baseline")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Baseline.findAll",
            query = "SELECT b FROM Baseline b")
    , @NamedQuery(name = "Baseline.findById",
            query = "SELECT b FROM Baseline b WHERE b.id = :id")
    , @NamedQuery(name = "Baseline.findByCreationDate",
            query = "SELECT b FROM Baseline b WHERE b.creationDate = :creationDate")
    , @NamedQuery(name = "Baseline.findByBaselineName",
            query = "SELECT b FROM Baseline b WHERE b.baselineName = :baselineName")})
public class Baseline implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE,
            generator = "Baseline_IDGEN")
    @TableGenerator(name = "Baseline_IDGEN", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "baseline",
            initialValue = 1,
            allocationSize = 1)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "baseline_name")
    private String baselineName;
    @Lob
    @Size(max = 2_147_483_647)
    @Column(name = "description")
    private String description;
    @JoinTable(name = "baseline_has_history", joinColumns = {
        @JoinColumn(name = "baseline_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "history_id", referencedColumnName = "id")})
    @ManyToMany
    private List<History> historyList;
    @JoinColumns({
        @JoinColumn(name = "requirement_spec_id", referencedColumnName = "id")
        , @JoinColumn(name = "requirement_spec_project_id", referencedColumnName = "project_id")
        , @JoinColumn(name = "requirement_spec_spec_level_id", referencedColumnName = "spec_level_id")})
    @ManyToOne(optional = false)
    private RequirementSpec requirementSpec;

    public Baseline() {
    }

    public Baseline(Date creationDate, String baselineName) {
        this.creationDate = creationDate;
        this.baselineName = baselineName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getBaselineName() {
        return baselineName;
    }

    public void setBaselineName(String baselineName) {
        this.baselineName = baselineName;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof Baseline)) {
            return false;
        }
        Baseline other = (Baseline) object;
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.Baseline[ id=" + id + " ]";
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    @JsonIgnore
    public List<History> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<History> historyList) {
        this.historyList = historyList;
    }

    public RequirementSpec getRequirementSpec() {
        return requirementSpec;
    }

    public void setRequirementSpec(RequirementSpec requirementSpec) {
        this.requirementSpec = requirementSpec;
    }
}
