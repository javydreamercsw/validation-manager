/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "exception_has_corrective_action")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ExceptionHasCorrectiveAction.findAll", query = "SELECT e FROM ExceptionHasCorrectiveAction e")
    , @NamedQuery(name = "ExceptionHasCorrectiveAction.findByExceptionId", query = "SELECT e FROM ExceptionHasCorrectiveAction e WHERE e.exceptionHasCorrectiveActionPK.exceptionId = :exceptionId")
    , @NamedQuery(name = "ExceptionHasCorrectiveAction.findByExceptionReporterId", query = "SELECT e FROM ExceptionHasCorrectiveAction e WHERE e.exceptionHasCorrectiveActionPK.exceptionReporterId = :exceptionReporterId")
    , @NamedQuery(name = "ExceptionHasCorrectiveAction.findByCorrectiveActionId", query = "SELECT e FROM ExceptionHasCorrectiveAction e WHERE e.exceptionHasCorrectiveActionPK.correctiveActionId = :correctiveActionId")})
public class ExceptionHasCorrectiveAction implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ExceptionHasCorrectiveActionPK exceptionHasCorrectiveActionPK;
    @JoinColumn(name = "corrective_action_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private CorrectiveAction correctiveAction;

    public ExceptionHasCorrectiveAction() {
    }

    public ExceptionHasCorrectiveAction(ExceptionHasCorrectiveActionPK exceptionHasCorrectiveActionPK) {
        this.exceptionHasCorrectiveActionPK = exceptionHasCorrectiveActionPK;
    }

    public ExceptionHasCorrectiveAction(int exceptionId, int exceptionReporterId, int correctiveActionId) {
        this.exceptionHasCorrectiveActionPK = new ExceptionHasCorrectiveActionPK(exceptionId, exceptionReporterId, correctiveActionId);
    }

    public ExceptionHasCorrectiveActionPK getExceptionHasCorrectiveActionPK() {
        return exceptionHasCorrectiveActionPK;
    }

    public void setExceptionHasCorrectiveActionPK(ExceptionHasCorrectiveActionPK exceptionHasCorrectiveActionPK) {
        this.exceptionHasCorrectiveActionPK = exceptionHasCorrectiveActionPK;
    }

    public CorrectiveAction getCorrectiveAction() {
        return correctiveAction;
    }

    public void setCorrectiveAction(CorrectiveAction correctiveAction) {
        this.correctiveAction = correctiveAction;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (exceptionHasCorrectiveActionPK != null ? exceptionHasCorrectiveActionPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ExceptionHasCorrectiveAction)) {
            return false;
        }
        ExceptionHasCorrectiveAction other = (ExceptionHasCorrectiveAction) object;
        if ((this.exceptionHasCorrectiveActionPK == null && other.exceptionHasCorrectiveActionPK != null) || (this.exceptionHasCorrectiveActionPK != null && !this.exceptionHasCorrectiveActionPK.equals(other.exceptionHasCorrectiveActionPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.ExceptionHasCorrectiveAction[ exceptionHasCorrectiveActionPK=" + exceptionHasCorrectiveActionPK + " ]";
    }

}
