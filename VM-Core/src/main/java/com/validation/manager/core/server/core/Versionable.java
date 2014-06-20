package com.validation.manager.core.server.core;

import com.validation.manager.core.VMAuditedObject;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@MappedSuperclass
public class Versionable extends VMAuditedObject implements Serializable,
        Comparable<Versionable> {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Min(0)
    @Column(name = "major_version")
    private int majorVersion;
    @Basic(optional = false)
    @NotNull
    @Min(0)
    @Column(name = "mid_version")
    private int midVersion;
    @Basic(optional = false)
    @NotNull
    @Min(1)
    @Column(name = "minor_version")
    private int minorVersion;
//    @Basic(optional = false)
//    @Column(name = "last_modified", insertable = false, updatable = false,
//            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date lastModified;
//    @Basic(optional = false)
//    @Column(name = "updated_by")
//    private int updatedBy;

    public Versionable() {
    }

    public Versionable(int majorVersion, int midVersion, int minorVersion) {
        this.majorVersion = majorVersion;
        this.midVersion = midVersion;
        this.minorVersion = minorVersion;
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
        hash += majorVersion;
        hash += midVersion;
        hash += minorVersion;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Versionable)) {
            return false;
        }
        return this.equals(object);
    }

    @Override
    public String toString() {
        return "Version [ " + majorVersion
                + "." + midVersion + "." + minorVersion + " ]";
    }

    @Override
    public int compareTo(Versionable o) {
        if (getMajorVersion() > o.getMajorVersion()) {
            return 1;
        } else if (getMajorVersion() < o.getMajorVersion()) {
            return -1;
        }//Same major version
        else if (getMidVersion() > o.getMidVersion()) {
            return 1;
        } else if (getMidVersion() < o.getMidVersion()) {
            return -1;
        } //Same mid version
        else if (getMinorVersion() > o.getMinorVersion()) {
            return 1;
        } else if (getMinorVersion() < o.getMinorVersion()) {
            return -1;
        }
        //Everything the same
        return 0;
    }
//
//    /**
//     * @return the lastModified
//     */
//    public Date getLastModified() {
//        return lastModified;
//}
//
//    /**
//     * @param lastModified the lastModified to set
//     */
//    public void setLastModified(Date lastModified) {
//        this.lastModified = lastModified;
//    }
//
//    /**
//     * @return the updatedBy
//     */
//    public int getUpdatedBy() {
//        return updatedBy;
//    }
//
//    /**
//     * @param updatedBy the updatedBy to set
//     */
//    public void setUpdatedBy(int updatedBy) {
//        this.updatedBy = updatedBy;
//    }
}
