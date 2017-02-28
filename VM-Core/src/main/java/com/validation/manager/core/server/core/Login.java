package com.validation.manager.core.server.core;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@MappedSuperclass
public class Login extends Versionable implements Serializable {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Min(0)
    @Column(name = "attempts")
    private int attempts;
    @Basic(optional = false)
    @Column(name = "last_login", insertable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified;

    public Login() {
    }

    public Login(int attempts) {
        this.attempts = attempts;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += attempts;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Login)) {
            return false;
        }
        return this.equals(object);
    }

    @Override
    public String toString() {
        return "Login [ " + attempts + " ]";
    }

    /**
     * @return the lastModified
     */
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * @param lastModified the lastModified to set
     */
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public void update(Versionable target, Versionable source) {
        target.setMajorVersion(source.getMajorVersion());
        target.setMidVersion(source.getMidVersion());
        target.setMinorVersion(source.getMinorVersion());
        super.update(target, source);
    }
}
