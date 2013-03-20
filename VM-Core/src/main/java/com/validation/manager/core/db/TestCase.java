/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import com.validation.manager.core.db.fmea.RiskControl;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
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
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "test_case")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TestCase.findAll", query = "SELECT t FROM TestCase t"),
    @NamedQuery(name = "TestCase.findById", query = "SELECT t FROM TestCase t WHERE t.testCasePK.id = :id"),
    @NamedQuery(name = "TestCase.findByTestId", query = "SELECT t FROM TestCase t WHERE t.testCasePK.testId = :testId"),
    @NamedQuery(name = "TestCase.findByVersion", query = "SELECT t FROM TestCase t WHERE t.version = :version"),
    @NamedQuery(name = "TestCase.findByCreationDate", query = "SELECT t FROM TestCase t WHERE t.creationDate = :creationDate"),
    @NamedQuery(name = "TestCase.findByActive", query = "SELECT t FROM TestCase t WHERE t.active = :active"),
    @NamedQuery(name = "TestCase.findByIsOpen", query = "SELECT t FROM TestCase t WHERE t.isOpen = :isOpen")})
public class TestCase implements Serializable {
    @ManyToMany(mappedBy = "testCaseList")
    private List<RiskControl> riskControlList;
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TestCasePK testCasePK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version")
    private short version;
    @Lob
    @Size(max = 65535)
    @Column(name = "summary")
    private String summary;
    @Lob
    @Size(max = 65535)
    @Column(name = "expected_results")
    private String expectedResults;
    @Basic(optional = false)
    @NotNull
    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Column(name = "active")
    private Boolean active;
    @Column(name = "is_open")
    private Boolean isOpen;
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private VmUser authorId;
    @JoinColumn(name = "test_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Test test;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "testCase")
    private List<Step> stepList;

    public TestCase() {
    }

    public TestCase(TestCasePK testCasePK) {
        this.testCasePK = testCasePK;
    }

    public TestCase(TestCasePK testCasePK, short version, Date creationDate) {
        this.testCasePK = testCasePK;
        this.version = version;
        this.creationDate = creationDate;
    }

    public TestCase(int testId) {
        this.testCasePK = new TestCasePK(testId);
    }

    public TestCasePK getTestCasePK() {
        return testCasePK;
    }

    public void setTestCasePK(TestCasePK testCasePK) {
        this.testCasePK = testCasePK;
    }

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getExpectedResults() {
        return expectedResults;
    }

    public void setExpectedResults(String expectedResults) {
        this.expectedResults = expectedResults;
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

    public VmUser getAuthorId() {
        return authorId;
    }

    public void setAuthorId(VmUser authorId) {
        this.authorId = authorId;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
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
        if ((this.testCasePK == null && other.testCasePK != null) || (this.testCasePK != null && !this.testCasePK.equals(other.testCasePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.TestCase[ testCasePK=" + testCasePK + " ]";
    }

    @XmlTransient
    @JsonIgnore
    public List<RiskControl> getRiskControlList() {
        return riskControlList;
    }

    public void setRiskControlList(List<RiskControl> riskControlList) {
        this.riskControlList = riskControlList;
    }
    
}