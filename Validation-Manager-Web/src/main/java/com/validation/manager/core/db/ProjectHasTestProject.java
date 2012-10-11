/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "project_has_test_project")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProjectHasTestProject.findAll", query = "SELECT p FROM ProjectHasTestProject p"),
    @NamedQuery(name = "ProjectHasTestProject.findByProjectId", query = "SELECT p FROM ProjectHasTestProject p WHERE p.projectHasTestProjectPK.projectId = :projectId"),
    @NamedQuery(name = "ProjectHasTestProject.findByTestProjectId", query = "SELECT p FROM ProjectHasTestProject p WHERE p.projectHasTestProjectPK.testProjectId = :testProjectId")})
public class ProjectHasTestProject implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ProjectHasTestProjectPK projectHasTestProjectPK;
    @JoinColumn(name = "test_project_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private TestProject testProject;
    @JoinColumn(name = "project_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Project project;

    public ProjectHasTestProject() {
    }

    public ProjectHasTestProject(ProjectHasTestProjectPK projectHasTestProjectPK) {
        this.projectHasTestProjectPK = projectHasTestProjectPK;
    }

    public ProjectHasTestProject(int projectId, int testProjectId) {
        this.projectHasTestProjectPK = new ProjectHasTestProjectPK(projectId, testProjectId);
    }

    public ProjectHasTestProjectPK getProjectHasTestProjectPK() {
        return projectHasTestProjectPK;
    }

    public void setProjectHasTestProjectPK(ProjectHasTestProjectPK projectHasTestProjectPK) {
        this.projectHasTestProjectPK = projectHasTestProjectPK;
    }

    public TestProject getTestProject() {
        return testProject;
    }

    public void setTestProject(TestProject testProject) {
        this.testProject = testProject;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (projectHasTestProjectPK != null ? projectHasTestProjectPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProjectHasTestProject)) {
            return false;
        }
        ProjectHasTestProject other = (ProjectHasTestProject) object;
        if ((this.projectHasTestProjectPK == null && other.projectHasTestProjectPK != null) || (this.projectHasTestProjectPK != null && !this.projectHasTestProjectPK.equals(other.projectHasTestProjectPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.ProjectHasTestProject[ projectHasTestProjectPK=" + projectHasTestProjectPK + " ]";
    }
    
}
