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

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@Embeddable
public class AttachmentPK implements Serializable {

    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "AttachmentGen")
    @TableGenerator(name = "AttachmentGen", table = "vm_id",
    pkColumnName = "table_name",
    valueColumnName = "last_id",
    pkColumnValue = "attachment",
    allocationSize = 1,
    initialValue = 1000)
    @Column(name = "ID", nullable = false)
    private int id;
    @Basic(optional = false)
    @Column(name = "ATTACHMENT_TYPE_ID", nullable = false)
    private int attachmentTypeId;

    public AttachmentPK() {
    }

    public AttachmentPK(int attachmentTypeId) {
        this.attachmentTypeId = attachmentTypeId;
    }

    public int getId() {
        return id;
    }

    public int getAttachmentTypeId() {
        return attachmentTypeId;
    }

    public void setAttachmentTypeId(int attachmentTypeId) {
        this.attachmentTypeId = attachmentTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) attachmentTypeId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof AttachmentPK)) {
            return false;
        }
        AttachmentPK other = (AttachmentPK) object;
        if (this.id != other.id) {
            return false;
        }
        if (this.attachmentTypeId != other.attachmentTypeId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.AttachmentPK[id=" + id + ", attachmentTypeId=" + attachmentTypeId + "]";
    }
}
