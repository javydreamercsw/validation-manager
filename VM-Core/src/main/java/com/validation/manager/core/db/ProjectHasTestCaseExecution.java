/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
@Table(name = "project_has_test_case_execution")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProjectHasTestCaseExecution.findAll", query = "SELECT p FROM ProjectHasTestCaseExecution p")
    , @NamedQuery(name = "ProjectHasTestCaseExecution.findByProjectId", query = "SELECT p FROM ProjectHasTestCaseExecution p WHERE p.projectHasTestCaseExecutionPK.projectId = :projectId")
    , @NamedQuery(name = "ProjectHasTestCaseExecution.findByTestCaseExecutionId", query = "SELECT p FROM ProjectHasTestCaseExecution p WHERE p.projectHasTestCaseExecutionPK.testCaseExecutionId = :testCaseExecutionId")
    , @NamedQuery(name = "ProjectHasTestCaseExecution.findByCreationTime", query = "SELECT p FROM ProjectHasTestCaseExecution p WHERE p.creationTime = :creationTime")})
public class ProjectHasTestCaseExecution implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ProjectHasTestCaseExecutionPK projectHasTestCaseExecutionPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "creation_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;
    @JoinColumn(name = "project_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Project project;
    @JoinColumn(name = "test_case_execution_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private TestCaseExecution testCaseExecution;
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private VmUser createdBy;

    public ProjectHasTestCaseExecution() {
    }

    public ProjectHasTestCaseExecution(ProjectHasTestCaseExecutionPK projectHasTestCaseExecutionPK) {
        this.projectHasTestCaseExecutionPK = projectHasTestCaseExecutionPK;
    }

    public ProjectHasTestCaseExecution(ProjectHasTestCaseExecutionPK projectHasTestCaseExecutionPK, Date creationTime) {
        this.projectHasTestCaseExecutionPK = projectHasTestCaseExecutionPK;
        this.creationTime = creationTime;
    }

    public ProjectHasTestCaseExecution(int projectId, int testCaseExecutionId) {
        this.projectHasTestCaseExecutionPK = new ProjectHasTestCaseExecutionPK(projectId, testCaseExecutionId);
    }

    public ProjectHasTestCaseExecutionPK getProjectHasTestCaseExecutionPK() {
        return projectHasTestCaseExecutionPK;
    }

    public void setProjectHasTestCaseExecutionPK(ProjectHasTestCaseExecutionPK projectHasTestCaseExecutionPK) {
        this.projectHasTestCaseExecutionPK = projectHasTestCaseExecutionPK;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public TestCaseExecution getTestCaseExecution() {
        return testCaseExecution;
    }

    public void setTestCaseExecution(TestCaseExecution testCaseExecution) {
        this.testCaseExecution = testCaseExecution;
    }

    public VmUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(VmUser createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (projectHasTestCaseExecutionPK != null ? projectHasTestCaseExecutionPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProjectHasTestCaseExecution)) {
            return false;
        }
        ProjectHasTestCaseExecution other = (ProjectHasTestCaseExecution) object;
        if ((this.projectHasTestCaseExecutionPK == null && other.projectHasTestCaseExecutionPK != null) || (this.projectHasTestCaseExecutionPK != null && !this.projectHasTestCaseExecutionPK.equals(other.projectHasTestCaseExecutionPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.ProjectHasTestCaseExecution[ projectHasTestCaseExecutionPK=" + projectHasTestCaseExecutionPK + " ]";
    }

}
