package com.validation.manager.core.server.core;

import com.validation.manager.core.AuditedEntityListener;
import com.validation.manager.core.VMAuditedObject;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@MappedSuperclass
@EntityListeners(AuditedEntityListener.class)
public class Versionable extends VMAuditedObject implements Serializable {

    private static final long serialVersionUID = 1L;
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
    private boolean inheritRelationships = false;

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
        return "com.validation.manager.core.db.Versionable[ " + majorVersion
                + "." + midVersion + "." + minorVersion + " ]";
    }

    /**
     * @return the inheritRelationships
     */
    public boolean isInheritRelationships() {
        return inheritRelationships;
    }

    /**
     * @param inheritRelationships the inheritRelationships to set
     */
    public void setInheritRelationships(boolean inheritRelationships) {
        this.inheritRelationships = inheritRelationships;
    }
}
