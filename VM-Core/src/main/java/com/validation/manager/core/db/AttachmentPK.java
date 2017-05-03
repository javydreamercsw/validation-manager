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
public class AttachmentPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE,
            generator = "Attachment_IDGEN")
    @TableGenerator(name = "Attachment_IDGEN", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "attachment",
            initialValue = 1,
            allocationSize = 1)
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
