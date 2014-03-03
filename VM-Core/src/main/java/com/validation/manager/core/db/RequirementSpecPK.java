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
public class RequirementSpecPK implements Serializable {

    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "ReqSpecGen")
    @TableGenerator(name = "ReqSpecGen", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "requirement_spec",
            allocationSize = 1,
            initialValue = 1000)
    @NotNull
    @Column(name = "id")
    private int id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "project_id")
    private int projectId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "spec_level_id")
    private int specLevelId;

    public RequirementSpecPK() {
    }

    public RequirementSpecPK(int projectId, int specLevelId) {
        this.projectId = projectId;
        this.specLevelId = specLevelId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getSpecLevelId() {
        return specLevelId;
    }

    public void setSpecLevelId(int specLevelId) {
        this.specLevelId = specLevelId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) projectId;
        hash += (int) specLevelId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RequirementSpecPK)) {
            return false;
        }
        RequirementSpecPK other = (RequirementSpecPK) object;
        if (this.id != other.id) {
            return false;
        }
        if (this.projectId != other.projectId) {
            return false;
        }
        return this.specLevelId == other.specLevelId;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RequirementSpecPK[ id=" + id + ", projectId=" + projectId + ", specLevelId=" + specLevelId + " ]";
    }

}
