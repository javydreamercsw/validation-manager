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
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "attachment")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Attachment.findAll",
            query = "SELECT a FROM Attachment a")
    , @NamedQuery(name = "Attachment.findById",
            query = "SELECT a FROM Attachment a WHERE a.attachmentPK.id = :id")
    , @NamedQuery(name = "Attachment.findByAttachmentTypeId",
            query = "SELECT a FROM Attachment a WHERE a.attachmentPK.attachmentTypeId = :attachmentTypeId")
    , @NamedQuery(name = "Attachment.findByStringValue",
            query = "SELECT a FROM Attachment a WHERE a.stringValue = :stringValue")
    , @NamedQuery(name = "Attachment.findByFileName",
            query = "SELECT a FROM Attachment a WHERE a.fileName = :fileName")})
public class Attachment implements Serializable {

    @Lob
    @Column(name = "file")
    private byte[] file;

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected AttachmentPK attachmentPK;
    @Size(max = 255)
    @Column(name = "string_value")
    private String stringValue;
    @Lob
    @Size(max = 2_147_483_647)
    @Column(name = "TEXT_VALUE")
    private String textValue;
    @Size(max = 255)
    @Column(name = "file_name")
    private String fileName;
    @JoinColumn(name = "attachment_type_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private AttachmentType attachmentType;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "attachment")
    private List<ExecutionStepHasAttachment> executionStepHasAttachmentList;

    public Attachment() {
    }

    public Attachment(AttachmentPK attachmentPK) {
        this.attachmentPK = attachmentPK;
    }

    public Attachment(int id, int attachmentTypeId) {
        this.attachmentPK = new AttachmentPK(id, attachmentTypeId);
    }

    public AttachmentPK getAttachmentPK() {
        return attachmentPK;
    }

    public void setAttachmentPK(AttachmentPK attachmentPK) {
        this.attachmentPK = attachmentPK;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public AttachmentType getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(AttachmentType attachmentType) {
        this.attachmentType = attachmentType;
    }

    @XmlTransient
    @JsonIgnore
    public List<ExecutionStepHasAttachment> getExecutionStepHasAttachmentList() {
        return executionStepHasAttachmentList;
    }

    public void setExecutionStepHasAttachmentList(List<ExecutionStepHasAttachment> executionStepHasAttachmentList) {
        this.executionStepHasAttachmentList = executionStepHasAttachmentList;
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
        return !((this.attachmentPK == null && other.attachmentPK != null)
                || (this.attachmentPK != null
                && !this.attachmentPK.equals(other.attachmentPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.Attachment[ attachmentPK="
                + attachmentPK + " ]";
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
