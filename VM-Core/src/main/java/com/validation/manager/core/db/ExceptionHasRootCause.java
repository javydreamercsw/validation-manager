package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
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
@Table(name = "exception_has_root_cause")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ExceptionHasRootCause.findAll",
            query = "SELECT e FROM ExceptionHasRootCause e")
    , @NamedQuery(name = "ExceptionHasRootCause.findByExceptionId",
            query = "SELECT e FROM ExceptionHasRootCause e WHERE "
            + "e.exceptionHasRootCausePK.exceptionId = :exceptionId")
    , @NamedQuery(name = "ExceptionHasRootCause.findByExceptionReporterId",
            query = "SELECT e FROM ExceptionHasRootCause e WHERE "
            + "e.exceptionHasRootCausePK.exceptionReporterId = :exceptionReporterId")
    , @NamedQuery(name = "ExceptionHasRootCause.findByRootCauseId",
            query = "SELECT e FROM ExceptionHasRootCause e WHERE "
            + "e.exceptionHasRootCausePK.rootCauseId = :rootCauseId")
    , @NamedQuery(name = "ExceptionHasRootCause.findByRootCauseRootCauseTypeId",
            query = "SELECT e FROM ExceptionHasRootCause e WHERE "
            + "e.exceptionHasRootCausePK.rootCauseRootCauseTypeId = :rootCauseRootCauseTypeId")})
public class ExceptionHasRootCause implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ExceptionHasRootCausePK exceptionHasRootCausePK;
    @JoinColumns({
        @JoinColumn(name = "root_cause_id", referencedColumnName = "id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "root_cause_root_cause_type_id",
                referencedColumnName = "root_cause_type_id",
                insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private RootCause rootCause;

    public ExceptionHasRootCause() {
    }

    public ExceptionHasRootCause(ExceptionHasRootCausePK exceptionHasRootCausePK) {
        this.exceptionHasRootCausePK = exceptionHasRootCausePK;
    }

    public ExceptionHasRootCause(int exceptionId, int exceptionReporterId,
            int rootCauseId, int rootCauseRootCauseTypeId) {
        this.exceptionHasRootCausePK = new ExceptionHasRootCausePK(exceptionId,
                exceptionReporterId, rootCauseId, rootCauseRootCauseTypeId);
    }

    public ExceptionHasRootCausePK getExceptionHasRootCausePK() {
        return exceptionHasRootCausePK;
    }

    public void setExceptionHasRootCausePK(ExceptionHasRootCausePK exceptionHasRootCausePK) {
        this.exceptionHasRootCausePK = exceptionHasRootCausePK;
    }

    public RootCause getRootCause() {
        return rootCause;
    }

    public void setRootCause(RootCause rootCause) {
        this.rootCause = rootCause;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (exceptionHasRootCausePK != null
                ? exceptionHasRootCausePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof ExceptionHasRootCause)) {
            return false;
        }
        ExceptionHasRootCause other = (ExceptionHasRootCause) object;
        return !((this.exceptionHasRootCausePK == null
                && other.exceptionHasRootCausePK != null)
                || (this.exceptionHasRootCausePK != null
                && !this.exceptionHasRootCausePK.equals(other.exceptionHasRootCausePK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.ExceptionHasRootCause[ "
                + "exceptionHasRootCausePK=" + exceptionHasRootCausePK + " ]";
    }

}
