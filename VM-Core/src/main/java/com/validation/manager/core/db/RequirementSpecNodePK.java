/* 
 * Copyright 2017 Javier A. Ortiz Bultron javier.ortiz.78@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Embeddable
public class RequirementSpecNodePK implements Serializable {

    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE,
            generator = "RequirementSpecNodeGen")
    @TableGenerator(name = "RequirementSpecNodeGen", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "requirement_spec_node",
            allocationSize = 1,
            initialValue = 1_000)
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

    public RequirementSpecNodePK(int requirementSpecId,
            int requirementSpecProjectId, int requirementSpecSpecLevelId) {
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
        return this.requirementSpecSpecLevelId == other.requirementSpecSpecLevelId;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RequirementSpecNodePK[ id="
                + id + ", requirementSpecId=" + requirementSpecId
                + ", requirementSpecProjectId=" + requirementSpecProjectId
                + ", requirementSpecSpecLevelId=" + requirementSpecSpecLevelId + " ]";
    }
}
