package com.validation.manager.core.db;

import java.io.Serializable;
import java.util.Date;
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
    @NamedQuery(name = "TestCase.findAll",
            query = "SELECT t FROM TestCase t")
    , @NamedQuery(name = "TestCase.findById",
            query = "SELECT t FROM TestCase t WHERE t.id = :id")
    , @NamedQuery(name = "TestCase.findByName",
            query = "SELECT t FROM TestCase t WHERE t.name = :name")
    , @NamedQuery(name = "TestCase.findByCreationDate",
            query = "SELECT t FROM TestCase t WHERE t.creationDate = :creationDate")
    , @NamedQuery(name = "TestCase.findByActive",
            query = "SELECT t FROM TestCase t WHERE t.active = :active")
    , @NamedQuery(name = "TestCase.findByIsOpen",
            query = "SELECT t FROM TestCase t WHERE t.isOpen = :isOpen")})
public class TestCase implements Serializable {

    @Lob
    @Column(name = "summary")
    private byte[] summary;

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE,
            generator = "TestCase_IDGEN")
    @TableGenerator(name = "TestCase_IDGEN", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "test_case",
            initialValue = 1000,
            allocationSize = 1)
    private Integer id;
    @Basic(optional = false)
    @NotNull
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
        @JoinColumn(name = "test_case_id", referencedColumnName = "id")},
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

    public TestCase() {
    }

    public TestCase(int id) {
        this.id = id;
    }

    public TestCase(String name, Date creationDate) {
        this.name = name;
        this.creationDate = creationDate;
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
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof TestCase)) {
            return false;
        }
        TestCase other = (TestCase) object;
        return !((this.id == null && other.id != null)
                || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.TestCase[ id=" + id + " ]";
    }

    public byte[] getSummary() {
        return summary;
    }

    public void setSummary(byte[] summary) {
        this.summary = summary;
    }
}
