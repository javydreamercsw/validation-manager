/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@Embeddable
public class StepPK implements Serializable {

    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "StepGen")
    @TableGenerator(name = "StepGen", table = "vm_id",
    pkColumnName = "table_name",
    valueColumnName = "last_id",
    pkColumnValue = "step",
    allocationSize = 1,
    initialValue = 1000)
    @NotNull
    @Column(name = "id", nullable = false)
    private int id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "test_case_id", nullable = false)
    private int testCaseId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "test_case_test_id", nullable = false)
    private int testCaseTestId;

    public StepPK() {
    }

    public StepPK(int testCaseId, int testCaseTestId) {
        this.testCaseId = testCaseId;
        this.testCaseTestId = testCaseTestId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(int testCaseId) {
        this.testCaseId = testCaseId;
    }

    public int getTestCaseTestId() {
        return testCaseTestId;
    }

    public void setTestCaseTestId(int testCaseTestId) {
        this.testCaseTestId = testCaseTestId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) testCaseId;
        hash += (int) testCaseTestId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof StepPK)) {
            return false;
        }
        StepPK other = (StepPK) object;
        if (this.id != other.id) {
            return false;
        }
        if (this.testCaseId != other.testCaseId) {
            return false;
        }
        if (this.testCaseTestId != other.testCaseTestId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.StepPK[ id=" + id + ", testCaseId=" + testCaseId + ", testCaseTestId=" + testCaseTestId + " ]";
    }
    
}
