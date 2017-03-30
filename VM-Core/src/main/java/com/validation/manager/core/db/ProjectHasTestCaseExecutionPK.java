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
public class ProjectHasTestCaseExecutionPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "project_id")
    private int projectId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "test_case_execution_id")
    private int testCaseExecutionId;

    public ProjectHasTestCaseExecutionPK() {
    }

    public ProjectHasTestCaseExecutionPK(int projectId, int testCaseExecutionId) {
        this.projectId = projectId;
        this.testCaseExecutionId = testCaseExecutionId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getTestCaseExecutionId() {
        return testCaseExecutionId;
    }

    public void setTestCaseExecutionId(int testCaseExecutionId) {
        this.testCaseExecutionId = testCaseExecutionId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) projectId;
        hash += (int) testCaseExecutionId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProjectHasTestCaseExecutionPK)) {
            return false;
        }
        ProjectHasTestCaseExecutionPK other = (ProjectHasTestCaseExecutionPK) object;
        if (this.projectId != other.projectId) {
            return false;
        }
        if (this.testCaseExecutionId != other.testCaseExecutionId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.ProjectHasTestCaseExecutionPK[ projectId=" + projectId + ", testCaseExecutionId=" + testCaseExecutionId + " ]";
    }

}
