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
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "corrective_action")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CorrectiveAction.findAll",
            query = "SELECT c FROM CorrectiveAction c")
    , @NamedQuery(name = "CorrectiveAction.findById",
            query = "SELECT c FROM CorrectiveAction c WHERE c.id = :id")})
public class CorrectiveAction implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "correctiveAction")
    private List<ExceptionHasCorrectiveAction> exceptionHasCorrectiveActionList;

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "CAGen")
    @TableGenerator(name = "CAGen", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "corrective_action",
            allocationSize = 1,
            initialValue = 1_000)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65_535)
    @Column(name = "details")
    private String details;
    @JoinTable(name = "user_has_corrective_action", joinColumns = {
        @JoinColumn(name = "corrective_action_id",
                referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "user_id", referencedColumnName = "id")})
    @ManyToMany
    private List<VmUser> vmUserList;

    public CorrectiveAction() {
    }

    public CorrectiveAction(String details) {
        this.details = details;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @XmlTransient
    @JsonIgnore
    public List<VmUser> getVmUserList() {
        return vmUserList;
    }

    public void setVmUserList(List<VmUser> vmUserList) {
        this.vmUserList = vmUserList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof CorrectiveAction)) {
            return false;
        }
        CorrectiveAction other = (CorrectiveAction) object;
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.CorrectiveAction[ id=" + id + " ]";
    }

    @XmlTransient
    @JsonIgnore
    public List<ExceptionHasCorrectiveAction> getExceptionHasCorrectiveActionList() {
        return exceptionHasCorrectiveActionList;
    }

    public void setExceptionHasCorrectiveActionList(List<ExceptionHasCorrectiveAction> exceptionHasCorrectiveActionList) {
        this.exceptionHasCorrectiveActionList = exceptionHasCorrectiveActionList;
    }
}
