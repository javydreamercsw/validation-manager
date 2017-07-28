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
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "template")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Template.findAll",
            query = "SELECT t FROM Template t")
    , @NamedQuery(name = "Template.findById",
            query = "SELECT t FROM Template t WHERE t.id = :id")
    , @NamedQuery(name = "Template.findByTemplateName",
            query = "SELECT t FROM Template t WHERE t.templateName = :templateName")})
public class Template implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TemplateGen")
    @TableGenerator(name = "TemplateGen", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "template",
            allocationSize = 1,
            initialValue = 1_000)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "template_name")
    private String templateName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "template")
    private List<TemplateNode> templateNodeList;
    @JoinColumn(name = "project_type_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ProjectType projectTypeId;

    public Template() {
    }

    public Template(Integer id) {
        this.id = id;
    }

    public Template(Integer id, String templateName) {
        this.id = id;
        this.templateName = templateName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    @XmlTransient
    @JsonIgnore
    public List<TemplateNode> getTemplateNodeList() {
        return templateNodeList;
    }

    public void setTemplateNodeList(List<TemplateNode> templateNodeList) {
        this.templateNodeList = templateNodeList;
    }

    public ProjectType getProjectTypeId() {
        return projectTypeId;
    }

    public void setProjectTypeId(ProjectType projectTypeId) {
        this.projectTypeId = projectTypeId;
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
        if (!(object instanceof Template)) {
            return false;
        }
        Template other = (Template) object;
        return !((this.id == null && other.id != null)
                || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.Template[ id=" + id + " ]";
    }
}
