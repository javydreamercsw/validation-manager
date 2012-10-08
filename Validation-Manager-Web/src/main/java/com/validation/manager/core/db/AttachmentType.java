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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "attachment_type")
@NamedQueries({
    @NamedQuery(name = "AttachmentType.findAll", query = "SELECT a FROM AttachmentType a"),
    @NamedQuery(name = "AttachmentType.findById", query = "SELECT a FROM AttachmentType a WHERE a.id = :id"),
    @NamedQuery(name = "AttachmentType.findByType", query = "SELECT a FROM AttachmentType a WHERE a.type = :type")})
public class AttachmentType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Attachment_Type_IDGEN")
    @TableGenerator(name = "Attachment_Type_IDGEN", table = "vm_id",
    pkColumnName = "table_name",
    valueColumnName = "last_id",
    pkColumnValue = "attachment_type",
    initialValue = 1000,
    allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "TYPE", nullable = false, length = 255)
    private String type;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "attachmentType")
    private List<Attachment> attachmentList;

    public AttachmentType() {
    }

    public AttachmentType(String type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Attachment> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(List<Attachment> attachmentList) {
        this.attachmentList = attachmentList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof AttachmentType)) {
            return false;
        }
        AttachmentType other = (AttachmentType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.AttachmentType[id=" + id + "]";
    }
}
