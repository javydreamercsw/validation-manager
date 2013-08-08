/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "role")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Role.findAll", query = "SELECT r FROM Role r"),
    @NamedQuery(name = "Role.findById", query = "SELECT r FROM Role r WHERE r.id = :id"),
    @NamedQuery(name = "Role.findByDescription", query = "SELECT r FROM Role r WHERE r.description = :description")})
public class Role implements Serializable {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role")
    private List<UserHasRole> userHasRoleList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role")
    private List<RoleHasRight> roleHasRightList;
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "RoleGen")
    @TableGenerator(name = "RoleGen", table = "vm_id",
    pkColumnName = "table_name",
    valueColumnName = "last_id",
    pkColumnValue = "role",
    allocationSize = 1,
    initialValue = 1000)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "description")
    private String description;
    @Lob
    @Size(max = 65535)
    @Column(name = "notes")
    private String notes;
    @JoinTable(name = "user_has_role", joinColumns = {
        @JoinColumn(name = "role_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "user_id", referencedColumnName = "id")})
    @ManyToMany
    private List<VmUser> vmUserList;
    @ManyToMany(mappedBy = "roleList")
    private List<UserRight> userRightList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role")
    private List<UserTestProjectRole> userTestProjectRoleList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role")
    private List<UserTestPlanRole> userTestPlanRoleList;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Role)) {
            return false;
        }
        Role other = (Role) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.Role[ id=" + id + " ]";
    }

    @XmlTransient
    @JsonIgnore
    public List<UserHasRole> getUserHasRoleList() {
        return userHasRoleList;
    }

    public void setUserHasRoleList(List<UserHasRole> userHasRoleList) {
        this.userHasRoleList = userHasRoleList;
    }

    @XmlTransient
    @JsonIgnore
    public List<RoleHasRight> getRoleHasRightList() {
        return roleHasRightList;
    }

    public void setRoleHasRightList(List<RoleHasRight> roleHasRightList) {
        this.roleHasRightList = roleHasRightList;
    }
}
