/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import com.validation.manager.core.VMAuditedObject;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@Entity
@EntityListeners(com.validation.manager.core.AuditedEntityListener.class)
@Table(name = "vm_user")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "VmUser.findAll", query = "SELECT v FROM VmUser v"),
    @NamedQuery(name = "VmUser.findById", query = "SELECT v FROM VmUser v WHERE v.id = :id"),
    @NamedQuery(name = "VmUser.findByUsername", query = "SELECT v FROM VmUser v WHERE v.username = :username"),
    @NamedQuery(name = "VmUser.findByPassword", query = "SELECT v FROM VmUser v WHERE v.password = :password"),
    @NamedQuery(name = "VmUser.findByEmail", query = "SELECT v FROM VmUser v WHERE v.email = :email"),
    @NamedQuery(name = "VmUser.findByFirst", query = "SELECT v FROM VmUser v WHERE v.first = :first"),
    @NamedQuery(name = "VmUser.findByLast", query = "SELECT v FROM VmUser v WHERE v.last = :last"),
    @NamedQuery(name = "VmUser.findByLocale", query = "SELECT v FROM VmUser v WHERE v.locale = :locale"),
    @NamedQuery(name = "VmUser.findByLastModified", query = "SELECT v FROM VmUser v WHERE v.lastModified = :lastModified"),
    @NamedQuery(name = "VmUser.findByAttempts", query = "SELECT v FROM VmUser v WHERE v.attempts = :attempts")})
public class VmUser extends VMAuditedObject implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "VM_UserGEN")
    @TableGenerator(name = "VM_UserGEN",
    table = "vm_id",
    pkColumnName = "table_name",
    valueColumnName = "last_id",
    pkColumnValue = "vm_user",
    initialValue = 1000,
    allocationSize = 1)
    @NotNull
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "username", nullable = false, length = 45)
    private String username;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "password", nullable = false, length = 45)
    private String password;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "email", nullable = false, length = 100)
    private String email;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "first", nullable = false, length = 45)
    private String first;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "last", nullable = false, length = 45)
    private String last;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "locale", nullable = false, length = 10)
    private String locale;
    @Basic(optional = false)
    @NotNull
    @Column(name = "last_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified;
    @Basic(optional = false)
    @NotNull
    @Column(name = "attempts", nullable = false)
    private int attempts;
    @JoinTable(name = "user_has_role", joinColumns = {
        @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)})
    @ManyToMany
    private List<Role> roleList;
    @JoinTable(name = "user_has_corrective_action", joinColumns = {
        @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "corrective_action_id", referencedColumnName = "id", nullable = false)})
    @ManyToMany
    private List<CorrectiveAction> correctiveActionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vmUser")
    private List<UserModifiedRecord> userModifiedRecordList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vmUser")
    private List<UserHasInvestigation> userHasInvestigationList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vmUser")
    private List<TestCase> testCaseList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vmUser")
    private List<UserAssigment> userAssigmentList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vmUser1")
    private List<UserAssigment> userAssigmentList1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vmUser")
    private List<VmException> vmExceptionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vmUser")
    private List<UserTestProjectRole> userTestProjectRoleList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vmUser")
    private List<UserHasRootCause> userHasRootCauseList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vmUser")
    private List<UserTestPlanRole> userTestPlanRoleList;
    @JoinColumn(name = "user_status_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private UserStatus userStatus;

    public VmUser() {
    }

    public VmUser(String username, String password, String email, String first, String last, String locale, Date lastModified, UserStatus userStatus, int attempts) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.first = first;
        this.last = last;
        this.locale = locale;
        this.lastModified = lastModified;
        this.attempts = attempts;
        this.userStatus = userStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    @XmlTransient
    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    @XmlTransient
    public List<CorrectiveAction> getCorrectiveActionList() {
        return correctiveActionList;
    }

    public void setCorrectiveActionList(List<CorrectiveAction> correctiveActionList) {
        this.correctiveActionList = correctiveActionList;
    }

    @XmlTransient
    public List<UserModifiedRecord> getUserModifiedRecordList() {
        return userModifiedRecordList;
    }

    public void setUserModifiedRecordList(List<UserModifiedRecord> userModifiedRecordList) {
        this.userModifiedRecordList = userModifiedRecordList;
    }

    @XmlTransient
    public List<UserHasInvestigation> getUserHasInvestigationList() {
        return userHasInvestigationList;
    }

    public void setUserHasInvestigationList(List<UserHasInvestigation> userHasInvestigationList) {
        this.userHasInvestigationList = userHasInvestigationList;
    }

    @XmlTransient
    public List<TestCase> getTestCaseList() {
        return testCaseList;
    }

    public void setTestCaseList(List<TestCase> testCaseList) {
        this.testCaseList = testCaseList;
    }

    @XmlTransient
    public List<UserAssigment> getUserAssigmentList() {
        return userAssigmentList;
    }

    public void setUserAssigmentList(List<UserAssigment> userAssigmentList) {
        this.userAssigmentList = userAssigmentList;
    }

    @XmlTransient
    public List<UserAssigment> getUserAssigmentList1() {
        return userAssigmentList1;
    }

    public void setUserAssigmentList1(List<UserAssigment> userAssigmentList1) {
        this.userAssigmentList1 = userAssigmentList1;
    }

    @XmlTransient
    public List<VmException> getVmExceptionList() {
        return vmExceptionList;
    }

    public void setVmExceptionList(List<VmException> vmExceptionList) {
        this.vmExceptionList = vmExceptionList;
    }

    @XmlTransient
    public List<UserTestProjectRole> getUserTestProjectRoleList() {
        return userTestProjectRoleList;
    }

    public void setUserTestProjectRoleList(List<UserTestProjectRole> userTestProjectRoleList) {
        this.userTestProjectRoleList = userTestProjectRoleList;
    }

    @XmlTransient
    public List<UserHasRootCause> getUserHasRootCauseList() {
        return userHasRootCauseList;
    }

    public void setUserHasRootCauseList(List<UserHasRootCause> userHasRootCauseList) {
        this.userHasRootCauseList = userHasRootCauseList;
    }

    @XmlTransient
    public List<UserTestPlanRole> getUserTestPlanRoleList() {
        return userTestPlanRoleList;
    }

    public void setUserTestPlanRoleList(List<UserTestPlanRole> userTestPlanRoleList) {
        this.userTestPlanRoleList = userTestPlanRoleList;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
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
        if (!(object instanceof VmUser)) {
            return false;
        }
        VmUser other = (VmUser) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.VmUser[ id=" + id + " ]";
    }
}
