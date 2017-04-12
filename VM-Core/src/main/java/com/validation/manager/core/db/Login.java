/**
 * This file was generated by the Jeddict
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public abstract class Login extends Versionable implements Serializable {

    @Column(name = "attempts")
    @Basic(optional = false)
    @NotNull
    @Min(value = 0)
    private int attempts;

    @Column(name = "last_login")
    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified;

    public int getAttempts() {
        return this.attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public Date getLastModified() {
        return this.lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}