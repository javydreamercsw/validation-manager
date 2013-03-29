/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import com.validation.manager.core.VMAuditedObject;
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
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;
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
@Table(name = "test_project", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TestProject.findAll", query = "SELECT t FROM TestProject t"),
    @NamedQuery(name = "TestProject.findById", query = "SELECT t FROM TestProject t WHERE t.id = :id"),
    @NamedQuery(name = "TestProject.findByName", query = "SELECT t FROM TestProject t WHERE t.name = :name"),
    @NamedQuery(name = "TestProject.findByActive", query = "SELECT t FROM TestProject t WHERE t.active = :active")})
public class TestProject extends VMAuditedObject implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "VMTestProject_IDGEN")
    //Reserving 10000 Test Project for regression of VM itself
    @TableGenerator(name = "VMTestProject_IDGEN", table = "vm_id",
    pkColumnName = "table_name",
    valueColumnName = "last_id",
    pkColumnValue = "test_project",
    allocationSize = 1,
    initialValue = 1000)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Column(name = "active")
    private boolean active;
    @Lob
    @Size(max = 65535)
    @Column(name = "notes")
    private String notes;
    @ManyToMany(mappedBy = "testProjectList")
    private List<Project> projectList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "testProject")
    private List<UserTestProjectRole> userTestProjectRoleList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "testProject")
    private List<TestPlan> testPlanList;

    public TestProject() {
    }

    public TestProject(String name, boolean active) {
        this.name = name;
        this.active = active;
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

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @XmlTransient
    @JsonIgnore
    public List<Project> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
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
    public List<TestPlan> getTestPlanList() {
        return testPlanList;
    }

    public void setTestPlanList(List<TestPlan> testPlanList) {
        this.testPlanList = testPlanList;
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
        if (!(object instanceof TestProject)) {
            return false;
        }
        TestProject other = (TestProject) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.TestProject[ id=" + id + " ]";
    }
    
}
