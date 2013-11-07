/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Embeddable
public class TestPlanHasTestPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "test_id")
    private int testId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "test_plan_id")
    private int testPlanId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "test_plan_test_project_id")
    private int testPlanTestProjectId;

    public TestPlanHasTestPK() {
    }

    public TestPlanHasTestPK(int testId, int testPlanId, int testPlanTestProjectId) {
        this.testId = testId;
        this.testPlanId = testPlanId;
        this.testPlanTestProjectId = testPlanTestProjectId;
    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) testId;
        hash += (int) testPlanId;
        hash += (int) testPlanTestProjectId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TestPlanHasTestPK)) {
            return false;
        }
        TestPlanHasTestPK other = (TestPlanHasTestPK) object;
        if (this.testId != other.testId) {
            return false;
        }
        if (this.testPlanId != other.testPlanId) {
            return false;
        }
        return this.testPlanTestProjectId == other.testPlanTestProjectId;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.TestPlanHasTestPK[ testId=" + testId + ", testPlanId=" + testPlanId + ", testPlanTestProjectId=" + testPlanTestProjectId + " ]";
    }

}
