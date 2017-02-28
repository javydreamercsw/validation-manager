package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Embeddable
public class AttachmentPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private int id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "attachment_type_id")
    private int attachmentTypeId;

    public AttachmentPK() {
    }

    public AttachmentPK(int id, int attachmentTypeId) {
        this.id = id;
        this.attachmentTypeId = attachmentTypeId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AttachmentPK)) {
            return false;
        }
        AttachmentPK other = (AttachmentPK) object;
        if (this.id != other.id) {
            return false;
        }
        return this.attachmentTypeId == other.attachmentTypeId;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.AttachmentPK[ id=" + id
                + ", attachmentTypeId=" + attachmentTypeId + " ]";
    }

}
