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
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
@Table(name = "test_case")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TestCase.findAll",
            query = "SELECT t FROM TestCase t")
    , @NamedQuery(name = "TestCase.findById",
            query = "SELECT t FROM TestCase t WHERE t.testCasePK.id = :id")
    , @NamedQuery(name = "TestCase.findByTestCaseTypeId",
            query = "SELECT t FROM TestCase t WHERE t.testCasePK.testCaseTypeId = :testCaseTypeId")
    , @NamedQuery(name = "TestCase.findByName",
            query = "SELECT t FROM TestCase t WHERE t.name = :name")
    , @NamedQuery(name = "TestCase.findByCreationDate",
            query = "SELECT t FROM TestCase t WHERE t.creationDate = :creationDate")
    , @NamedQuery(name = "TestCase.findByActive",
            query = "SELECT t FROM TestCase t WHERE t.active = :active")
    , @NamedQuery(name = "TestCase.findByIsOpen",
            query = "SELECT t FROM TestCase t WHERE t.isOpen = :isOpen")})
public class TestCase implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TestCasePK testCasePK;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Column(name = "summary")
    private byte[] summary;
    @Size(min = 1, max = 255)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Column(name = "active")
    private Boolean active;
    @Column(name = "is_open")
    private Boolean isOpen;
    @JoinTable(name = "test_plan_has_test_case", joinColumns = {
        @JoinColumn(name = "test_case_id", referencedColumnName = "id")
        ,@JoinColumn(name = "test_case_type_id", referencedColumnName = "test_case_type_id",
                insertable = false, updatable = false)},
            inverseJoinColumns = {
                @JoinColumn(name = "test_plan_id", referencedColumnName = "id")
                , @JoinColumn(name = "test_plan_test_project_id",
                        referencedColumnName = "test_project_id")})
    @ManyToMany
    private List<TestPlan> testPlanList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "testCase")
    private List<RiskControlHasTestCase> riskControlHasTestCaseList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "testCase")
    private List<Step> stepList;
    @JoinColumn(name = "test_case_type_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private TestCaseType testCaseType;

    public TestCase() {
    }

    public TestCase(TestCasePK testCasePK) {
        this.testCasePK = testCasePK;
    }

    public TestCase(String name, Date creationDate) {
        this.name = name;
        this.creationDate = creationDate;
    }

    public TestCase(int testCaseId, int testCaseTypeId) {
        this.testCasePK = new TestCasePK(testCaseId, testCaseTypeId);
    }

    public TestCasePK getTestCasePK() {
        return testCasePK;
    }

    public void setTestCasePK(TestCasePK testCasePK) {
        this.testCasePK = testCasePK;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
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

    @XmlTransient
    @JsonIgnore
    public List<TestPlan> getTestPlanList() {
        return testPlanList;
    }

    public void setTestPlanList(List<TestPlan> testPlanList) {
        this.testPlanList = testPlanList;
    }

    public TestCaseType getTestCaseType() {
        return testCaseType;
    }

    public void setTestCaseType(TestCaseType testCaseType) {
        this.testCaseType = testCaseType;
    }

    @XmlTransient
    @JsonIgnore
    public List<RiskControlHasTestCase> getRiskControlHasTestCaseList() {
        return riskControlHasTestCaseList;
    }

    public void setRiskControlHasTestCaseList(List<RiskControlHasTestCase> riskControlHasTestCaseList) {
        this.riskControlHasTestCaseList = riskControlHasTestCaseList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Step> getStepList() {
        return stepList;
    }

    public void setStepList(List<Step> stepList) {
        this.stepList = stepList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (testCasePK != null ? testCasePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TestCase)) {
            return false;
        }
        TestCase other = (TestCase) object;
        return !((this.testCasePK == null && other.testCasePK != null)
                || (this.testCasePK != null && !this.testCasePK.equals(other.testCasePK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.TestCase[ testCasePK=" + testCasePK + " ]";
    }

    public byte[] getSummary() {
        return summary;
    }

    public void setSummary(byte[] summary) {
        this.summary = summary;
    }
}
