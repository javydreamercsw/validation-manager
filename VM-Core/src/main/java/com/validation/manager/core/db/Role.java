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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
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
@Table(name = "role")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Role.findAll", query = "SELECT r FROM Role r")
    , @NamedQuery(name = "Role.findById",
            query = "SELECT r FROM Role r WHERE r.id = :id")
    , @NamedQuery(name = "Role.findByName",
            query = "SELECT r FROM Role r WHERE r.roleName = :name")})
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role")
    private List<ExecutionStepHasVmUser> executionStepHasVmUserList;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "RoleGen")
    @TableGenerator(name = "RoleGen", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "role",
            allocationSize = 1,
            initialValue = 1_000)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "role_name")
    private String roleName;
    @Lob
    @Size(max = 65_535)
    @Column(name = "description")
    private String description;
    @JoinTable(name = "user_has_role", joinColumns = {
        @JoinColumn(name = "role_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                @JoinColumn(name = "user_id", referencedColumnName = "id")})
    @ManyToMany
    private List<VmUser> vmUserList;
    @ManyToMany(mappedBy = "roleList")
    private List<UserRight> userRightList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role")
    private List<UserTestProjectRole> userTestProjectRoleList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role")
    private List<UserTestPlanRole> userTestPlanRoleList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role")
    private List<UserHasRole> userHasRoleList;

    public Role() {
    }

    public Role(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    @JsonIgnore
    public List<VmUser> getVmUserList() {
        return vmUserList;
    }

    public void setVmUserList(List<VmUser> vmUserList) {
        this.vmUserList = vmUserList;
    }

    @XmlTransient
    @JsonIgnore
    public List<UserRight> getUserRightList() {
        return userRightList;
    }

    public void setUserRightList(List<UserRight> userRightList) {
        this.userRightList = userRightList;
    }

    @XmlTransient
    @JsonIgnore
    public List<UserTestProjectRole> getUserTestProjectRoleList() {
        return userTestProjectRoleList;
    }

    public void setUserTestProjectRoleList(List<UserTestProjectRole> userTestProjectRoleList) {
        this.userTestProjectRoleList = userTestProjectRoleList;
    }

    @XmlTransient
    @JsonIgnore
    public List<UserTestPlanRole> getUserTestPlanRoleList() {
        return userTestPlanRoleList;
    }

    public void setUserTestPlanRoleList(List<UserTestPlanRole> userTestPlanRoleList) {
        this.userTestPlanRoleList = userTestPlanRoleList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof Role)) {
            return false;
        }
        Role other = (Role) object;
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.Role[ id=" + id + " ]";
    }

    @XmlTransient
    @JsonIgnore
    public List<ExecutionStepHasVmUser> getExecutionStepHasVmUserList() {
        return executionStepHasVmUserList;
    }

    public void setExecutionStepHasVmUserList(List<ExecutionStepHasVmUser> executionStepHasVmUserList) {
        this.executionStepHasVmUserList = executionStepHasVmUserList;
    }

    @XmlTransient
    @JsonIgnore
    public List<UserHasRole> getUserHasRoleList() {
        return userHasRoleList;
    }

    public void setUserHasRoleList(List<UserHasRole> userHasRoleList) {
        this.userHasRoleList = userHasRoleList;
    }
}
