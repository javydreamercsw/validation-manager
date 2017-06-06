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
public class RequirementSpecPK implements Serializable {

    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "ReqSpecGen")
    @TableGenerator(name = "ReqSpecGen", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "requirement_spec",
            allocationSize = 1,
            initialValue = 1_000)
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
        return "com.validation.manager.core.db.RequirementSpecPK[ id=" + id
                + ", projectId=" + projectId + ", specLevelId=" + specLevelId + " ]";
    }

}
