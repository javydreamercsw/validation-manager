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
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
@Table(name = "template_node")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TemplateNode.findAll",
            query = "SELECT t FROM TemplateNode t")
    , @NamedQuery(name = "TemplateNode.findById",
            query = "SELECT t FROM TemplateNode t WHERE t.templateNodePK.id = :id")
    , @NamedQuery(name = "TemplateNode.findByTemplateId",
            query = "SELECT t FROM TemplateNode t WHERE t.templateNodePK.templateId = :templateId")
    , @NamedQuery(name = "TemplateNode.findByTemplateNodeTypeId",
            query = "SELECT t FROM TemplateNode t WHERE t.templateNodePK.templateNodeTypeId = :templateNodeTypeId")
    , @NamedQuery(name = "TemplateNode.findByNodeName",
            query = "SELECT t FROM TemplateNode t WHERE t.nodeName = :nodeName")})
public class TemplateNode implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TemplateNodePK templateNodePK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "node_name")
    private String nodeName;
    @JoinColumn(name = "template_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Template template;
    @OneToMany(mappedBy = "templateNode")
    private List<TemplateNode> templateNodeList;
    @JoinColumns({
        @JoinColumn(name = "parent_template_node_id", referencedColumnName = "id")
        , @JoinColumn(name = "template_node_template_id",
                referencedColumnName = "template_id")
        , @JoinColumn(name = "parent_template_node_template_node_type_id",
                referencedColumnName = "template_node_type_id")})
    @ManyToOne
    private TemplateNode templateNode;
    @JoinColumn(name = "template_node_type_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private TemplateNodeType templateNodeType;

    public TemplateNode() {
    }

    public TemplateNode(TemplateNodePK templateNodePK) {
        this.templateNodePK = templateNodePK;
    }

    public TemplateNode(TemplateNodePK templateNodePK, String nodeName) {
        this.templateNodePK = templateNodePK;
        this.nodeName = nodeName;
    }

    public TemplateNode(int templateId, int templateNodeTypeId) {
        this.templateNodePK = new TemplateNodePK(templateId, templateNodeTypeId);
    }

    public TemplateNodePK getTemplateNodePK() {
        return templateNodePK;
    }

    public void setTemplateNodePK(TemplateNodePK templateNodePK) {
        this.templateNodePK = templateNodePK;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    @XmlTransient
    @JsonIgnore
    public List<TemplateNode> getTemplateNodeList() {
        return templateNodeList;
    }

    public void setTemplateNodeList(List<TemplateNode> templateNodeList) {
        this.templateNodeList = templateNodeList;
    }

    public TemplateNode getTemplateNode() {
        return templateNode;
    }

    public void setTemplateNode(TemplateNode templateNode) {
        this.templateNode = templateNode;
    }

    public TemplateNodeType getTemplateNodeType() {
        return templateNodeType;
    }

    public void setTemplateNodeType(TemplateNodeType templateNodeType) {
        this.templateNodeType = templateNodeType;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (templateNodePK != null ? templateNodePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TemplateNode)) {
            return false;
        }
        TemplateNode other = (TemplateNode) object;
        return !((this.templateNodePK == null
                && other.templateNodePK != null)
                || (this.templateNodePK != null
                && !this.templateNodePK.equals(other.templateNodePK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.TemplateNode[ templateNodePK="
                + templateNodePK + " ]";
    }
}
