package com.validation.manager.core;

import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@MappedSuperclass
public abstract class VMAuditedObject implements AuditedObject {

    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(max = 2147483647)
    @Column(name = "reason")
    private String reason;
    /**
     * Default to Admin
     */
    @Basic(optional = false)
    @NotNull
    @Column(name = "modifier_id")
    private int modifierId = 1;
    @Basic(optional = false)
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modification_time")
    private Date modificationTime;

    @Override
    public void setReason(String r) {
        this.reason = r;
    }

    @Override
    public String getReason() {
        return reason;
    }

    @Override
    public void setModifierId(int id) {
        this.modifierId = id;
    }

    @Override
    public Integer getModifierId() {
        return modifierId;
    }

    @Override
    public Date getModificationTime() {
        return modificationTime;
    }

    @Override
    public void setModificationTime(Date d) {
        this.modificationTime = d;
    }

    /**
     * Update the fields.
     *
     * @param target target object
     * @param source source object
     */
    public void update(VMAuditedObject target,
            VMAuditedObject source) {
        target.setReason(source.getReason());
        target.setModificationTime(source.getModificationTime());
        target.setModifierId(source.getModifierId());
    }

    @Override
    public boolean isChangeVersionable() {
        return true;
    }
}
