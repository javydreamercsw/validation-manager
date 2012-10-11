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
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Embeddable
public class ProjectHasTestProjectPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "project_id")
    private int projectId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "test_project_id")
    private int testProjectId;

    public ProjectHasTestProjectPK() {
    }

    public ProjectHasTestProjectPK(int projectId, int testProjectId) {
        this.projectId = projectId;
        this.testProjectId = testProjectId;
    }

    public int getprojectId() {
        return projectId;
    }

    public void setprojectId(int projectId) {
        this.projectId = projectId;
    }

    public int getTestProjectId() {
        return testProjectId;
    }

    public void setTestProjectId(int testProjectId) {
        this.testProjectId = testProjectId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) projectId;
        hash += (int) testProjectId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProjectHasTestProjectPK)) {
            return false;
        }
        ProjectHasTestProjectPK other = (ProjectHasTestProjectPK) object;
        if (this.projectId != other.projectId) {
            return false;
        }
        if (this.testProjectId != other.testProjectId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.ProjectHasTestProjectPK[ projectId=" + projectId + ", testProjectId=" + testProjectId + " ]";
    }
    
}
