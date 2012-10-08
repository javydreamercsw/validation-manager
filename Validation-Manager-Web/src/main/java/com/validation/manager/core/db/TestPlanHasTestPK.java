/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@Embeddable
public class TestPlanHasTestPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "test_plan_id", nullable = false)
    private int testPlanId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "test_plan_test_project_id", nullable = false)
    private int testPlanTestProjectId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "test_id", nullable = false)
    private int testId;

    public TestPlanHasTestPK() {
    }

    public TestPlanHasTestPK(int testPlanId, int testPlanTestProjectId, int testId) {
        this.testPlanId = testPlanId;
        this.testPlanTestProjectId = testPlanTestProjectId;
        this.testId = testId;
    }

    public int getTestPlanId() {
        return testPlanId;
    }

    public void setTestPlanId(int testPlanId) {
        this.testPlanId = testPlanId;
    }

    public int getTestPlanTestProjectId() {
        return testPlanTestProjectId;
    }

    public void setTestPlanTestProjectId(int testPlanTestProjectId) {
        this.testPlanTestProjectId = testPlanTestProjectId;
    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) testPlanId;
        hash += (int) testPlanTestProjectId;
        hash += (int) testId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TestPlanHasTestPK)) {
            return false;
        }
        TestPlanHasTestPK other = (TestPlanHasTestPK) object;
        if (this.testPlanId != other.testPlanId) {
            return false;
        }
        if (this.testPlanTestProjectId != other.testPlanTestProjectId) {
            return false;
        }
        if (this.testId != other.testId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.TestPlanHasTestPK[ testPlanId=" + testPlanId + ", testPlanTestProjectId=" + testPlanTestProjectId + ", testId=" + testId + " ]";
    }
    
}
