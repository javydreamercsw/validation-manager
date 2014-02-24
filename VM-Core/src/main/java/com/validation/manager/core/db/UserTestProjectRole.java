package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
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
@Table(name = "user_test_project_role")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserTestProjectRole.findAll",
            query = "SELECT u FROM UserTestProjectRole u"),
    @NamedQuery(name = "UserTestProjectRole.findByTestProjectId",
            query = "SELECT u FROM UserTestProjectRole u WHERE u.userTestProjectRolePK.testProjectId = :testProjectId"),
    @NamedQuery(name = "UserTestProjectRole.findByUserId",
            query = "SELECT u FROM UserTestProjectRole u WHERE u.userTestProjectRolePK.userId = :userId"),
    @NamedQuery(name = "UserTestProjectRole.findByRoleId",
            query = "SELECT u FROM UserTestProjectRole u WHERE u.userTestProjectRolePK.roleId = :roleId")})
public class UserTestProjectRole implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected UserTestProjectRolePK userTestProjectRolePK;
    @JoinColumn(name = "user_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private VmUser vmUser;
    @JoinColumn(name = "test_project_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private TestProject testProject;
    @JoinColumn(name = "role_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Role role;

    public UserTestProjectRole() {
    }

    public UserTestProjectRole(UserTestProjectRolePK userTestProjectRolePK) {
        this.userTestProjectRolePK = userTestProjectRolePK;
    }

    public UserTestProjectRole(int testProjectId, int userId, int roleId) {
        this.userTestProjectRolePK = new UserTestProjectRolePK(testProjectId, userId, roleId);
    }

    public UserTestProjectRolePK getUserTestProjectRolePK() {
        return userTestProjectRolePK;
    }

    public void setUserTestProjectRolePK(UserTestProjectRolePK userTestProjectRolePK) {
        this.userTestProjectRolePK = userTestProjectRolePK;
    }

    public VmUser getVmUser() {
        return vmUser;
    }

    public void setVmUser(VmUser vmUser) {
        this.vmUser = vmUser;
    }

    public TestProject getTestProject() {
        return testProject;
    }

    public void setTestProject(TestProject testProject) {
        this.testProject = testProject;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userTestProjectRolePK != null ? userTestProjectRolePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserTestProjectRole)) {
            return false;
        }
        UserTestProjectRole other = (UserTestProjectRole) object;
        return (this.userTestProjectRolePK != null || other.userTestProjectRolePK == null) && (this.userTestProjectRolePK == null || this.userTestProjectRolePK.equals(other.userTestProjectRolePK));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.UserTestProjectRole[ userTestProjectRolePK=" + userTestProjectRolePK + " ]";
    }

}
