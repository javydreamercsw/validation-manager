package com.validation.manager.core.server.core;

import com.validation.manager.core.VMAuditedObject;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "VERSIONABLE_TYPE")
public class Versionable extends VMAuditedObject
        implements Serializable,
        Comparable<Versionable> {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Min(0)
    @Column(name = "major_version")
    private Integer majorVersion;
    @Basic(optional = false)
    @NotNull
    @Min(0)
    @Column(name = "mid_version")
    private Integer midVersion;
    @Basic(optional = false)
    @NotNull
    @Min(1)
    @Column(name = "minor_version")
    private Integer minorVersion;

    public Versionable() {
        majorVersion = 0;
        midVersion = 0;
        minorVersion = 1;
    }

    public Versionable(int majorVersion, int midVersion,
            int minorVersion) {
        this.majorVersion = majorVersion;
        this.midVersion = midVersion;
        this.minorVersion = minorVersion;
    }

    public Integer getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(Integer majorVersion) {
        this.majorVersion = majorVersion;
    }

    public Integer getMidVersion() {
        return midVersion;
    }

    public void setMidVersion(Integer midVersion) {
        this.midVersion = midVersion;
    }

    public Integer getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(Integer minorVersion) {
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
        // TODO: Warning - this method won't work in the case the versionableId fields are not set
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
        if (!Objects.equals(getMajorVersion(),
                o.getMajorVersion())) {
            return getMajorVersion() - o.getMajorVersion();
        }//Same major version
        else if (!Objects.equals(getMidVersion(),
                o.getMidVersion())) {
            return getMidVersion() - o.getMidVersion();
        } //Same mid version
        else if (!Objects.equals(getMinorVersion(),
                o.getMinorVersion())) {
            return getMinorVersion() - o.getMinorVersion();
        }
        //Everything the same
        return 0;
    }

    public void update(Versionable target, Versionable source) {
        target.setMajorVersion(source.getMajorVersion());
        target.setMidVersion(source.getMidVersion());
        target.setMinorVersion(source.getMinorVersion());
        super.update(target, source);
    }
}
