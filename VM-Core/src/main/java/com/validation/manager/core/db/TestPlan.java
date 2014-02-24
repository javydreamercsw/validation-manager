package com.validation.manager.core.db;

import com.validation.manager.core.VMAuditedObject;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "test_plan")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TestPlan.findAll", query = "SELECT t FROM TestPlan t"),
    @NamedQuery(name = "TestPlan.findByActive", query = "SELECT t FROM TestPlan t WHERE t.active = :active"),
    @NamedQuery(name = "TestPlan.findByIsOpen", query = "SELECT t FROM TestPlan t WHERE t.isOpen = :isOpen"),
    @NamedQuery(name = "TestPlan.findById", query = "SELECT t FROM TestPlan t WHERE t.testPlanPK.id = :id"),
    @NamedQuery(name = "TestPlan.findByTestProjectId", query = "SELECT t FROM TestPlan t WHERE t.testPlanPK.testProjectId = :testProjectId")})
public class TestPlan extends VMAuditedObject implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TestPlanPK testPlanPK;
    @Column(name = "active")
    private Boolean active;
    @Column(name = "is_open")
    private Boolean isOpen;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "notes")
    private String notes;
    @JoinColumn(name = "test_project_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private TestProject testProject;
    @OneToMany(mappedBy = "testPlan")
    private List<TestPlan> testPlanList;
    @JoinColumns({
        @JoinColumn(name = "regression_test_plan_id", referencedColumnName = "id"),
        @JoinColumn(name = "regression_test_plan_test_project_id", referencedColumnName = "test_project_id")})
    @ManyToOne
    private TestPlan testPlan;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "testPlan")
    private List<TestPlanHasTest> testPlanHasTestList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "testPlan")
    private List<UserTestPlanRole> userTestPlanRoleList;

    public TestPlan() {
    }

    public TestPlan(TestPlanPK testPlanPK) {
        this.testPlanPK = testPlanPK;
    }

    public TestPlan(TestProject testProject, boolean active, boolean isOpen) {
        this.testProject = testProject;
        this.testPlanPK = new TestPlanPK(testProject.getId());
        this.active = active;
        this.isOpen = isOpen;
    }

    public TestPlanPK getTestPlanPK() {
        return testPlanPK;
    }

    public void setTestPlanPK(TestPlanPK testPlanPK) {
        this.testPlanPK = testPlanPK;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Boolean isOpen) {
        this.isOpen = isOpen;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public TestProject getTestProject() {
        return testProject;
    }

    public void setTestProject(TestProject testProject) {
        this.testProject = testProject;
    }

    @XmlTransient
    @JsonIgnore
    public List<TestPlan> getTestPlanList() {
        return testPlanList;
    }

    public void setTestPlanList(List<TestPlan> testPlanList) {
        this.testPlanList = testPlanList;
    }

    public TestPlan getTestPlan() {
        return testPlan;
    }

    public void setTestPlan(TestPlan testPlan) {
        this.testPlan = testPlan;
    }

    @XmlTransient
    @JsonIgnore
    public List<TestPlanHasTest> getTestPlanHasTestList() {
        return testPlanHasTestList;
    }

    public void setTestPlanHasTestList(List<TestPlanHasTest> testPlanHasTestList) {
        this.testPlanHasTestList = testPlanHasTestList;
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
        hash += (testPlanPK != null ? testPlanPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TestPlan)) {
            return false;
        }
        TestPlan other = (TestPlan) object;
        return (this.testPlanPK != null || other.testPlanPK == null) && (this.testPlanPK == null || this.testPlanPK.equals(other.testPlanPK));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.TestPlan[ testPlanPK=" + testPlanPK + " ]";
    }

}
