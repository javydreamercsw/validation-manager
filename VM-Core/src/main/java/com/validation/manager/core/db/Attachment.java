package com.validation.manager.core.db;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
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
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
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

    @Basic(optional = false)
    @NotNull
    @Lob
    @Column(name = "file")
    private byte[] file;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "attachment")
    private List<ExecutionStepHasAttachment> executionStepHasAttachmentList;
    @JoinTable(name = "execution_step_has_attachment", joinColumns = {
        @JoinColumn(name = "attachment_id", referencedColumnName = "id")
        , @JoinColumn(name = "attachment_attachment_type_id",
                referencedColumnName = "attachment_type_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "execution_step_test_case_execution_id",
                        referencedColumnName = "test_case_execution_id")
                , @JoinColumn(name = "execution_step_step_id",
                        referencedColumnName = "step_id")
                , @JoinColumn(name = "execution_step_step_test_case_id",
                        referencedColumnName = "step_test_case_id")})
    @ManyToMany
    private List<ExecutionStep> executionStepList;

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected AttachmentPK attachmentPK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "string_value")
    private String stringValue;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "TEXT_VALUE")
    private String textValue;
    @Size(max = 255)
    @Column(name = "file_name")
    private String fileName;
    @JoinColumn(name = "attachment_type_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private AttachmentType attachmentType;

    public Attachment() {
    }

    public Attachment(AttachmentPK attachmentPK) {
        this.attachmentPK = attachmentPK;
    }

    public Attachment(AttachmentPK attachmentPK, byte[] file, String stringValue) {
        this.attachmentPK = attachmentPK;
        this.file = file;
        this.stringValue = stringValue;
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

    public void setFileName(String filename) {
        this.fileName = filename;
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
        // TODO: Warning - this method won't work in the case the id fields are not set
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

    @XmlTransient
    @JsonIgnore
    public List<ExecutionStep> getExecutionStepList() {
        return executionStepList;
    }

    public void setExecutionStepList(List<ExecutionStep> executionStepList) {
        this.executionStepList = executionStepList;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    @XmlTransient
    @JsonIgnore
    public List<ExecutionStepHasAttachment> getExecutionStepHasAttachmentList() {
        return executionStepHasAttachmentList;
    }

    public void setExecutionStepHasAttachmentList(List<ExecutionStepHasAttachment> executionStepHasAttachmentList) {
        this.executionStepHasAttachmentList = executionStepHasAttachmentList;
    }
}
