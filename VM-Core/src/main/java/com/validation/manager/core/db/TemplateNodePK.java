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
public class TemplateNodePK implements Serializable {

    @Basic(optional = false)
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TemplateNodeGen")
    @TableGenerator(name = "TemplateNodeGen", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "template_node",
            allocationSize = 1,
            initialValue = 1_000)
    private int id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "template_id")
    private int templateId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "template_node_type_id")
    private int templateNodeTypeId;

    public TemplateNodePK() {
    }

    public TemplateNodePK(int templateId, int templateNodeTypeId) {
        this.templateId = templateId;
        this.templateNodeTypeId = templateNodeTypeId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public int getTemplateNodeTypeId() {
        return templateNodeTypeId;
    }

    public void setTemplateNodeTypeId(int templateNodeTypeId) {
        this.templateNodeTypeId = templateNodeTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) templateId;
        hash += (int) templateNodeTypeId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TemplateNodePK)) {
            return false;
        }
        TemplateNodePK other = (TemplateNodePK) object;
        if (this.id != other.id) {
            return false;
        }
        if (this.templateId != other.templateId) {
            return false;
        }
        if (this.templateNodeTypeId != other.templateNodeTypeId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.TemplateNodePK[ id=" + id + ", templateId=" + templateId + ", templateNodeTypeId=" + templateNodeTypeId + " ]";
    }

}
