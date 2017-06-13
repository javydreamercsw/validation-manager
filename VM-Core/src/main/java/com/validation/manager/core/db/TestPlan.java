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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "test_plan")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TestPlan.findAll",
            query = "SELECT t FROM TestPlan t")
    , @NamedQuery(name = "TestPlan.findById",
            query = "SELECT t FROM TestPlan t WHERE t.testPlanPK.id = :id")
    , @NamedQuery(name = "TestPlan.findByTestProjectId",
            query = "SELECT t FROM TestPlan t WHERE t.testPlanPK.testProjectId = :testProjectId")
    , @NamedQuery(name = "TestPlan.findByName",
            query = "SELECT t FROM TestPlan t WHERE t.name = :name")
    , @NamedQuery(name = "TestPlan.findByActive",
            query = "SELECT t FROM TestPlan t WHERE t.active = :active")
    , @NamedQuery(name = "TestPlan.findByIsOpen",
            query = "SELECT t FROM TestPlan t WHERE t.isOpen = :isOpen")})
public class TestPlan implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TestPlanPK testPlanPK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "name")
    private String name;
    @Lob
    @Size(max = 65_535)
    @Column(name = "notes")
    private String notes;
    @Basic(optional = false)
    @NotNull
    @Column(name = "active")
    private boolean active;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_open")
    private boolean isOpen;
    @ManyToMany(mappedBy = "testPlanList")
    private List<TestCase> testCaseList;
    @OneToMany(mappedBy = "testPlan")
    private List<TestPlan> testPlanList;
    @JoinColumns({
        @JoinColumn(name = "regression_test_plan_id",
                referencedColumnName = "id")
        , @JoinColumn(name = "regression_test_plan_test_project_id",
                referencedColumnName = "test_project_id")})
    @ManyToOne
    private TestPlan testPlan;
    @JoinColumn(name = "test_project_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private TestProject testProject;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    @XmlTransient
    @JsonIgnore
    public List<TestCase> getTestCaseList() {
        return testCaseList;
    }

    public void setTestCaseList(List<TestCase> testCaseList) {
        this.testCaseList = testCaseList;
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

    public TestProject getTestProject() {
        return testProject;
    }

    public void setTestProject(TestProject testProject) {
        this.testProject = testProject;
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
        
        if (!(object instanceof TestPlan)) {
            return false;
        }
        TestPlan other = (TestPlan) object;
        return !((this.testPlanPK == null && other.testPlanPK != null)
                || (this.testPlanPK != null
                && !this.testPlanPK.equals(other.testPlanPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.TestPlan[ testPlanPK="
                + testPlanPK + " ]";
    }
}
