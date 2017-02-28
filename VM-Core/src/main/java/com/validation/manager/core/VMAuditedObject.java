package com.validation.manager.core;

import com.validation.manager.core.adapter.TimestampAdapter;
import java.sql.Timestamp;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@MappedSuperclass
public class VMAuditedObject implements AuditedObject {

    private boolean auditable = true;
    private String reason;
    /**
     * Default to Admin
     */
    private int modifierId = 1;
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    private Timestamp modDate;

    @Override
    public void setModificationReason(String r) {
        this.reason = r;
    }

    @Override
    public String getModificationReason() {
        return reason;
    }

    @Override
    public void setModifierId(int id) {
        this.modifierId = id;
    }

    @Override
    public int getModifierId() {
        return modifierId;
    }

    @Override
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public Timestamp getModificationTime() {
        return modDate;
    }

    @Override
    public void setModificationTime(Timestamp d) {
        this.modDate = d;
    }

    /**
     * @return the auditable
     */
    public boolean isAuditable() {
        return auditable;
    }

    /**
     * @param auditable the auditable to set
     */
    public void setAuditable(boolean auditable) {
        this.auditable = auditable;
    }

    /**
     * Update the fields.
     *
     * @param target target object
     * @param source source object
     */
    public void update(VMAuditedObject target, VMAuditedObject source) {
        target.setAuditable(source.isAuditable());
        target.setModificationReason(source.getModificationReason());
        target.setModificationTime(source.getModificationTime());
        target.setModifierId(source.getModifierId());
    }
}
