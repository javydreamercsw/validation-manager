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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "versionable")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Versionable.findAll", query = "SELECT v FROM Versionable v"),
    @NamedQuery(name = "Versionable.findByVersionId", query = "SELECT v FROM Versionable v WHERE v.versionId = :versionId"),
    @NamedQuery(name = "Versionable.findByMajorVersion", query = "SELECT v FROM Versionable v WHERE v.majorVersion = :majorVersion"),
    @NamedQuery(name = "Versionable.findByMidVersion", query = "SELECT v FROM Versionable v WHERE v.midVersion = :midVersion"),
    @NamedQuery(name = "Versionable.findByMinorVersion", query = "SELECT v FROM Versionable v WHERE v.minorVersion = :minorVersion")})
public class Versionable implements Serializable {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "versionable")
    private List<Requirement> requirementList;

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "version")
    @TableGenerator(name = "version", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "versioning",
            allocationSize = 1,
            initialValue = 1)
    @NotNull
    @Column(name = "version_id")
    private Integer versionId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "major_version")
    private int majorVersion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "mid_version")
    private int midVersion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "minor_version")
    private int minorVersion;

    public Versionable() {
    }

    public Versionable(Integer versionId) {
        this.versionId = versionId;
    }

    public Versionable(int majorVersion, int midVersion, int minorVersion) {
        this.majorVersion = majorVersion;
        this.midVersion = midVersion;
        this.minorVersion = minorVersion;
    }

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    public int getMidVersion() {
        return midVersion;
    }

    public void setMidVersion(int midVersion) {
        this.midVersion = midVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (versionId != null ? versionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Versionable)) {
            return false;
        }
        Versionable other = (Versionable) object;
        if ((this.versionId == null && other.versionId != null) || (this.versionId != null && !this.versionId.equals(other.versionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.Versionable[ versionId=" + versionId + " ]";
    }

    @XmlTransient
    @JsonIgnore
    public List<Requirement> getRequirementList() {
        return requirementList;
    }

    public void setRequirementList(List<Requirement> requirementList) {
        this.requirementList = requirementList;
    }

}
