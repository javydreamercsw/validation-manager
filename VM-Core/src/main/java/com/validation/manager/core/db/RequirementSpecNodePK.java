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
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Embeddable
public class RequirementSpecNodePK implements Serializable {
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "RequirementSpecNodeGen")
    @TableGenerator(name = "RequirementSpecNodeGen", table = "vm_id",
    pkColumnName = "table_name",
    valueColumnName = "last_id",
    pkColumnValue = "requirement_spec_node",
    allocationSize = 1,
    initialValue = 1000)
    @NotNull
    @Column(name = "id")
    private int id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "requirement_spec_id")
    private int requirementSpecId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "requirement_spec_project_id")
    private int requirementSpecProjectId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "requirement_spec_spec_level_id")
    private int requirementSpecSpecLevelId;

    public RequirementSpecNodePK() {
    }

    public RequirementSpecNodePK(int requirementSpecId, int requirementSpecProjectId, int requirementSpecSpecLevelId) {
        this.requirementSpecId = requirementSpecId;
        this.requirementSpecProjectId = requirementSpecProjectId;
        this.requirementSpecSpecLevelId = requirementSpecSpecLevelId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequirementSpecId() {
        return requirementSpecId;
    }

    public void setRequirementSpecId(int requirementSpecId) {
        this.requirementSpecId = requirementSpecId;
    }

    public int getRequirementSpecProjectId() {
        return requirementSpecProjectId;
    }

    public void setRequirementSpecProjectId(int requirementSpecProjectId) {
        this.requirementSpecProjectId = requirementSpecProjectId;
    }

    public int getRequirementSpecSpecLevelId() {
        return requirementSpecSpecLevelId;
    }

    public void setRequirementSpecSpecLevelId(int requirementSpecSpecLevelId) {
        this.requirementSpecSpecLevelId = requirementSpecSpecLevelId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) requirementSpecId;
        hash += (int) requirementSpecProjectId;
        hash += (int) requirementSpecSpecLevelId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RequirementSpecNodePK)) {
            return false;
        }
        RequirementSpecNodePK other = (RequirementSpecNodePK) object;
        if (this.id != other.id) {
            return false;
        }
        if (this.requirementSpecId != other.requirementSpecId) {
            return false;
        }
        if (this.requirementSpecProjectId != other.requirementSpecProjectId) {
            return false;
        }
        if (this.requirementSpecSpecLevelId != other.requirementSpecSpecLevelId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RequirementSpecNodePK[ id=" + id + ", requirementSpecId=" + requirementSpecId + ", requirementSpecProjectId=" + requirementSpecProjectId + ", requirementSpecSpecLevelId=" + requirementSpecSpecLevelId + " ]";
    }
    
}
