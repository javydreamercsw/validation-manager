/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
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
@Table(name = "project")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Project.findAll", query = "SELECT p FROM Project p"),
    @NamedQuery(name = "Project.findById", query = "SELECT p FROM Project p WHERE p.id = :id"),
    @NamedQuery(name = "Project.findByName", query = "SELECT p FROM Project p WHERE p.name = :name")})
public class Project implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "ProjectGen")
    @TableGenerator(name = "ProjectGen", table = "vm_id",
    pkColumnName = "table_name",
    valueColumnName = "last_id",
    pkColumnValue = "project",
    allocationSize = 1,
    initialValue=1000)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "name")
    private String name;
    @Lob
    @Size(max = 65535)
    @Column(name = "notes")
    private String notes;
    @JoinTable(name = "project_has_test_project", joinColumns = {
        @JoinColumn(name = "project_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "test_project_id", referencedColumnName = "id")})
    @ManyToMany
    private List<TestProject> testProjectList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    private List<RequirementSpec> requirementSpecList;
    @OneToMany(mappedBy = "parentProjectId")
    private List<Project> projectList;
    @JoinColumn(name = "parent_project_id", referencedColumnName = "id")
    @ManyToOne
    private Project parentProjectId;

    public Project() {
    }

    public Project(String name) {
        this.name = name;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @XmlTransient
    @JsonIgnore
    public List<TestProject> getTestProjectList() {
        return testProjectList;
    }

    public void setTestProjectList(List<TestProject> testProjectList) {
        this.testProjectList = testProjectList;
    }

    @XmlTransient
    @JsonIgnore
    public List<RequirementSpec> getRequirementSpecList() {
        return requirementSpecList;
    }

    public void setRequirementSpecList(List<RequirementSpec> requirementSpecList) {
        this.requirementSpecList = requirementSpecList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Project> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }

    public Project getParentProjectId() {
        return parentProjectId;
    }

    public void setParentProjectId(Project parentProjectId) {
        this.parentProjectId = parentProjectId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Project)) {
            return false;
        }
        Project other = (Project) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.Project[ id=" + id + " ]";
    }
    
}
