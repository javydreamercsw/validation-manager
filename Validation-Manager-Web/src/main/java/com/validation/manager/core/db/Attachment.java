/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "attachment")
@NamedQueries({
    @NamedQuery(name = "Attachment.findAll", query = "SELECT a FROM Attachment a"),
    @NamedQuery(name = "Attachment.findByStringValue", query = "SELECT a FROM Attachment a WHERE a.stringValue = :stringValue"),
    @NamedQuery(name = "Attachment.findById", query = "SELECT a FROM Attachment a WHERE a.attachmentPK.id = :id"),
    @NamedQuery(name = "Attachment.findByAttachmentTypeId", query = "SELECT a FROM Attachment a WHERE a.attachmentPK.attachmentTypeId = :attachmentTypeId")})
public class Attachment implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Lob
    @Column(name = "file")
    private byte[] file;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "string_value")
    private String stringValue;
    @JoinColumn(name = "attachment_type_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private AttachmentType attachmentType;

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected AttachmentPK attachmentPK;
    @Lob
    @Column(name = "TEXT_VALUE", length = 2147483647)
    private String textValue;

    public Attachment() {
    }

    public Attachment(AttachmentPK attachmentPK) {
        this.attachmentPK = attachmentPK;
    }

    public Attachment(int attachmentTypeId) {
        this.attachmentPK = new AttachmentPK(attachmentTypeId);
    }

    public AttachmentPK getAttachmentPK() {
        return attachmentPK;
    }

    public void setAttachmentPK(AttachmentPK attachmentPK) {
        this.attachmentPK = attachmentPK;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public AttachmentType getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(AttachmentType attachmentType) {
        this.attachmentType = attachmentType;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (attachmentPK != null ? attachmentPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof Attachment)) {
            return false;
        }
        Attachment other = (Attachment) object;
        if ((this.attachmentPK == null && other.attachmentPK != null) || (this.attachmentPK != null && !this.attachmentPK.equals(other.attachmentPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.Attachment[attachmentPK=" + attachmentPK + "]";
    }
}
