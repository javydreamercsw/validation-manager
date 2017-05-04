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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "field_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FieldType.findAll", query = "SELECT f FROM FieldType f")
    , @NamedQuery(name = "FieldType.findById",
            query = "SELECT f FROM FieldType f WHERE f.id = :id")
    , @NamedQuery(name = "FieldType.findByTypeName",
            query = "SELECT f FROM FieldType f WHERE f.typeName = :typeName")})
public class FieldType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "type_name")
    private String typeName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fieldType")
    private List<HistoryField> historyFieldList;

    public FieldType() {
    }

    public FieldType(Integer id) {
        this.id = id;
    }

    public FieldType(Integer id, String typeName) {
        this.id = id;
        this.typeName = typeName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @XmlTransient
    @JsonIgnore
    public List<HistoryField> getHistoryFieldList() {
        return historyFieldList;
    }

    public void setHistoryFieldList(List<HistoryField> historyFieldList) {
        this.historyFieldList = historyFieldList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof FieldType)) {
            return false;
        }
        FieldType other = (FieldType) object;
        return !((this.id == null && other.id != null)
                || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.FieldType[ id=" + id + " ]";
    }
}
