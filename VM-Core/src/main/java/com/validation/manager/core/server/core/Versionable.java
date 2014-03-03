package com.validation.manager.core.server.core;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@MappedSuperclass
public class Versionable implements Serializable {

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
    @Column(name = "id")
    private Integer id;
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
        this.id = versionId;
    }

    public Versionable(int majorVersion, int midVersion, int minorVersion) {
        this.majorVersion = majorVersion;
        this.midVersion = midVersion;
        this.minorVersion = minorVersion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer versionId) {
        this.id = versionId;
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
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Versionable)) {
            return false;
        }
        Versionable other = (Versionable) object;
        if ((this.id == null && other.id != null)
                || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.Versionable[ versionId=" + id + " ]";
    }
}
