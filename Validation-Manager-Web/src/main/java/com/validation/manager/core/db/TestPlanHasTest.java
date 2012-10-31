/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "test_plan_has_test")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TestPlanHasTest.findAll", query = "SELECT t FROM TestPlanHasTest t"),
    @NamedQuery(name = "TestPlanHasTest.findByTestPlanId", query = "SELECT t FROM TestPlanHasTest t WHERE t.testPlanHasTestPK.testPlanId = :testPlanId"),
    @NamedQuery(name = "TestPlanHasTest.findByTestPlanTestProjectId", query = "SELECT t FROM TestPlanHasTest t WHERE t.testPlanHasTestPK.testPlanTestProjectId = :testPlanTestProjectId"),
    @NamedQuery(name = "TestPlanHasTest.findByTestId", query = "SELECT t FROM TestPlanHasTest t WHERE t.testPlanHasTestPK.testId = :testId"),
    @NamedQuery(name = "TestPlanHasTest.findByStartDate", query = "SELECT t FROM TestPlanHasTest t WHERE t.startDate = :startDate"),
    @NamedQuery(name = "TestPlanHasTest.findByEndDate", query = "SELECT t FROM TestPlanHasTest t WHERE t.endDate = :endDate"),
    @NamedQuery(name = "TestPlanHasTest.findByNodeOrder", query = "SELECT t FROM TestPlanHasTest t WHERE t.nodeOrder = :nodeOrder")})
public class TestPlanHasTest implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TestPlanHasTestPK testPlanHasTestPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "node_order")
    private int nodeOrder;
    @JoinColumns({
        @JoinColumn(name = "test_plan_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "test_plan_test_project_id", referencedColumnName = "test_project_id", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private TestPlan testPlan;
    @JoinColumn(name = "test_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Test test;

    public TestPlanHasTest() {
    }

    public TestPlanHasTest(TestPlanHasTestPK testPlanHasTestPK) {
        this.testPlanHasTestPK = testPlanHasTestPK;
    }

    public TestPlanHasTest(TestPlanHasTestPK testPlanHasTestPK, Date startDate, int nodeOrder) {
        this.testPlanHasTestPK = testPlanHasTestPK;
        this.startDate = startDate;
        this.nodeOrder = nodeOrder;
    }

    public TestPlanHasTest(int testPlanId, int testPlanTestProjectId, int testId) {
        this.testPlanHasTestPK = new TestPlanHasTestPK(testPlanId, testPlanTestProjectId, testId);
    }

    public TestPlanHasTestPK getTestPlanHasTestPK() {
        return testPlanHasTestPK;
    }

    public void setTestPlanHasTestPK(TestPlanHasTestPK testPlanHasTestPK) {
        this.testPlanHasTestPK = testPlanHasTestPK;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getNodeOrder() {
        return nodeOrder;
    }

    public void setNodeOrder(int nodeOrder) {
        this.nodeOrder = nodeOrder;
    }

    public TestPlan getTestPlan() {
        return testPlan;
    }

    public void setTestPlan(TestPlan testPlan) {
        this.testPlan = testPlan;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (testPlanHasTestPK != null ? testPlanHasTestPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TestPlanHasTest)) {
            return false;
        }
        TestPlanHasTest other = (TestPlanHasTest) object;
        if ((this.testPlanHasTestPK == null && other.testPlanHasTestPK != null) || (this.testPlanHasTestPK != null && !this.testPlanHasTestPK.equals(other.testPlanHasTestPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.TestPlanHasTest[ testPlanHasTestPK=" + testPlanHasTestPK + " ]";
    }
    
}
