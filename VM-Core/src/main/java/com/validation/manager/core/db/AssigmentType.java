package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "assigment_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AssigmentType.findAll",
            query = "SELECT a FROM AssigmentType a"),
    @NamedQuery(name = "AssigmentType.findById",
            query = "SELECT a FROM AssigmentType a WHERE a.id = :id"),
    @NamedQuery(name = "AssigmentType.findByFkTable",
            query = "SELECT a FROM AssigmentType a WHERE a.fkTable = :fkTable")})
public class AssigmentType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Assignment_Type_IDGEN")
    @TableGenerator(name = "Assignment_Type_IDGEN", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "assigment_type",
            initialValue = 1000,
            allocationSize = 1)
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

    public AssigmentType() {
    }

    public AssigmentType(String fkTable) {
        this.fkTable = fkTable;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AssigmentType)) {
            return false;
        }
        AssigmentType other = (AssigmentType) object;
        if ((this.id == null && other.id != null) || (this.id != null
                && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.AssigmentType[ id=" + id + " ]";
    }

}
