/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.fmea;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "root_cause")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RootCause.findAll", query = "SELECT r FROM RootCause r"),
    @NamedQuery(name = "RootCause.findById", query = "SELECT r FROM RootCause r WHERE r.rootCausePK.id = :id"),
    @NamedQuery(name = "RootCause.findByRootCauseTypeId", query = "SELECT r FROM RootCause r WHERE r.rootCausePK.rootCauseTypeId = :rootCauseTypeId")})
public class RootCause implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RootCausePK rootCausePK;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "details")
    private String details;
    @JoinColumn(name = "root_cause_type_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private RootCauseType rootCauseType;

    public RootCause() {
    }

    public RootCause(RootCausePK rootCausePK) {
        this.rootCausePK = rootCausePK;
    }

    public RootCause(RootCausePK rootCausePK, String details) {
        this.rootCausePK = rootCausePK;
        this.details = details;
    }

    public RootCause(int rootCauseTypeId) {
        this.rootCausePK = new RootCausePK(rootCauseTypeId);
    }

    public RootCausePK getRootCausePK() {
        return rootCausePK;
    }

    public void setRootCausePK(RootCausePK rootCausePK) {
        this.rootCausePK = rootCausePK;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public RootCauseType getRootCauseType() {
        return rootCauseType;
    }

    public void setRootCauseType(RootCauseType rootCauseType) {
        this.rootCauseType = rootCauseType;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (rootCausePK != null ? rootCausePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RootCause)) {
            return false;
        }
        RootCause other = (RootCause) object;
        if ((this.rootCausePK == null && other.rootCausePK != null) || (this.rootCausePK != null && !this.rootCausePK.equals(other.rootCausePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.fmea.RootCause[ rootCausePK=" + rootCausePK + " ]";
    }
    
}