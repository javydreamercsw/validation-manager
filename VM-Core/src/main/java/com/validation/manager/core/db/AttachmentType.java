package com.validation.manager.core.db;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
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
@Table(name = "attachment_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AttachmentType.findAll",
            query = "SELECT a FROM AttachmentType a")
    , @NamedQuery(name = "AttachmentType.findById",
            query = "SELECT a FROM AttachmentType a WHERE a.id = :id")
    , @NamedQuery(name = "AttachmentType.findByFkTable",
            query = "SELECT a FROM AttachmentType a WHERE a.fkTable = :fkTable")
    , @NamedQuery(name = "AttachmentType.findByType",
            query = "SELECT a FROM AttachmentType a WHERE a.type = :type")})
public class AttachmentType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "fk_table")
    private String fkTable;
    @Lob
    @Size(max = 65535)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "TYPE")
    private String type;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "attachmentType")
    private List<Attachment> attachmentList;

    public AttachmentType() {
    }

    public AttachmentType(Integer id) {
        this.id = id;
    }

    public AttachmentType(Integer id, String fkTable, String type) {
        this.id = id;
        this.fkTable = fkTable;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFkTable() {
        return fkTable;
    }

    public void setFkTable(String fkTable) {
        this.fkTable = fkTable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlTransient
    @JsonIgnore
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
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AttachmentType)) {
            return false;
        }
        AttachmentType other = (AttachmentType) object;
        return !((this.id == null && other.id != null)
                || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.AttachmentType[ id=" + id + " ]";
    }

}
