/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "execution_result")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ExecutionResult.findAll", query = "SELECT e FROM ExecutionResult e")
    , @NamedQuery(name = "ExecutionResult.findById", query = "SELECT e FROM ExecutionResult e WHERE e.id = :id")
    , @NamedQuery(name = "ExecutionResult.findByResultName", query = "SELECT e FROM ExecutionResult e WHERE e.resultName = :resultName")})
public class ExecutionResult implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "result_name")
    private String resultName;
    @OneToMany(mappedBy = "resultId")
    private List<ExecutionStep> executionStepList;

    public ExecutionResult() {
    }

    public ExecutionResult(Integer id) {
        this.id = id;
    }

    public ExecutionResult(Integer id, String resultName) {
        this.id = id;
        this.resultName = resultName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getResultName() {
        return resultName;
    }

    public void setResultName(String resultName) {
        this.resultName = resultName;
    }

    @XmlTransient
    @JsonIgnore
    public List<ExecutionStep> getExecutionStepList() {
        return executionStepList;
    }

    public void setExecutionStepList(List<ExecutionStep> executionStepList) {
        this.executionStepList = executionStepList;
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
        if (!(object instanceof ExecutionResult)) {
            return false;
        }
        ExecutionResult other = (ExecutionResult) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.ExecutionResult[ id=" + id + " ]";
    }

}
