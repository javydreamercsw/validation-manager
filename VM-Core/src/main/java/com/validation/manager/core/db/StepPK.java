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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
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
    @Column(name = "id")
    private int id;
    @Column(name = "test_case_test_id")
    private int testCaseTestId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "test_case_id")
    private int testCaseId;

    public StepPK() {
    }

    public StepPK(int testCaseId, int testCaseTestId) {
        this.testCaseId = testCaseId;
    }

    public int getTestCaseTestId() {
        return testCaseTestId;
    }

    public void setTestCaseTestId(int testCaseTestId) {
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) testCaseTestId;
        hash += (int) id;
        hash += (int) testCaseId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StepPK)) {
            return false;
        }
        StepPK other = (StepPK) object;
        if (this.testCaseTestId != other.testCaseTestId) {
            return false;
        }
        if (this.id != other.id) {
            return false;
        }
        return this.testCaseId == other.testCaseId;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.StepPK[ testCaseTestId=" + testCaseTestId + ", id=" + id + ", testCaseId=" + testCaseId + " ]";
    }

}
