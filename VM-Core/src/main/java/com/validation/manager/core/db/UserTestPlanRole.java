/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "user_test_plan_role")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserTestPlanRole.findAll", query = "SELECT u FROM UserTestPlanRole u"),
    @NamedQuery(name = "UserTestPlanRole.findByTestPlanId", query = "SELECT u FROM UserTestPlanRole u WHERE u.userTestPlanRolePK.testPlanId = :testPlanId"),
    @NamedQuery(name = "UserTestPlanRole.findByTestPlanTestProjectId", query = "SELECT u FROM UserTestPlanRole u WHERE u.userTestPlanRolePK.testPlanTestProjectId = :testPlanTestProjectId"),
    @NamedQuery(name = "UserTestPlanRole.findByUserId", query = "SELECT u FROM UserTestPlanRole u WHERE u.userTestPlanRolePK.userId = :userId"),
    @NamedQuery(name = "UserTestPlanRole.findByRoleId", query = "SELECT u FROM UserTestPlanRole u WHERE u.userTestPlanRolePK.roleId = :roleId")})
public class UserTestPlanRole implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected UserTestPlanRolePK userTestPlanRolePK;
    @JoinColumn(name = "role_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Role role;
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private VmUser vmUser;
    @JoinColumns({
        @JoinColumn(name = "test_plan_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "test_plan_test_project_id", referencedColumnName = "test_project_id", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private TestPlan testPlan;

    public UserTestPlanRole() {
    }

    public UserTestPlanRole(UserTestPlanRolePK userTestPlanRolePK) {
        this.userTestPlanRolePK = userTestPlanRolePK;
    }

    public UserTestPlanRole(TestPlan tpl, VmUser user, Role role) {
        this.userTestPlanRolePK = new UserTestPlanRolePK(tpl.getTestPlanPK().getId(),
                tpl.getTestPlanPK().getTestProjectId(), user.getId(), role.getId());
        this.testPlan = tpl;
        this.vmUser=user;
        this.role=role;
    }

    public UserTestPlanRolePK getUserTestPlanRolePK() {
        return userTestPlanRolePK;
    }

    public void setUserTestPlanRolePK(UserTestPlanRolePK userTestPlanRolePK) {
        this.userTestPlanRolePK = userTestPlanRolePK;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public VmUser getVmUser() {
        return vmUser;
    }

    public void setVmUser(VmUser vmUser) {
        this.vmUser = vmUser;
    }

    public TestPlan getTestPlan() {
        return testPlan;
    }

    public void setTestPlan(TestPlan testPlan) {
        this.testPlan = testPlan;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userTestPlanRolePK != null ? userTestPlanRolePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserTestPlanRole)) {
            return false;
        }
        UserTestPlanRole other = (UserTestPlanRole) object;
        if ((this.userTestPlanRolePK == null && other.userTestPlanRolePK != null) || (this.userTestPlanRolePK != null && !this.userTestPlanRolePK.equals(other.userTestPlanRolePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.UserTestPlanRole[ userTestPlanRolePK=" + userTestPlanRolePK + " ]";
    }
    
}
