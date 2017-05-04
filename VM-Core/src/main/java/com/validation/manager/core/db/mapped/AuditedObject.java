/**
 * This file was generated by the Jeddict
 */
package com.validation.manager.core.db.mapped;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.api.history.Auditable;
import com.validation.manager.core.db.FieldType;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.HistoryField;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.server.core.FieldTypeServer;
import com.validation.manager.core.server.core.HistoryFieldServer;
import com.validation.manager.core.server.core.HistoryServer;
import com.validation.manager.core.server.core.VMUserServer;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.reflect.FieldUtils;

public abstract class AuditedObject
        implements Comparable<AuditedObject>, Serializable {

    private final static Logger LOG
            = Logger.getLogger(AuditedObject.class.getSimpleName());

    private Integer majorVersion = 0;
    private Integer midVersion = 0;
    private Integer minorVersion = 1;
    private String reason;
    private VmUser modifierId;
    private Date modificationTime;

    public Integer getMajorVersion() {
        return this.majorVersion;
    }

    public void setMajorVersion(Integer majorVersion) {
        this.majorVersion = majorVersion;
    }

    public Integer getMidVersion() {
        return this.midVersion;
    }

    public void setMidVersion(Integer midVersion) {
        this.midVersion = midVersion;
    }

    public Integer getMinorVersion() {
        return this.minorVersion;
    }

    public void setMinorVersion(Integer minorVersion) {
        this.minorVersion = minorVersion;
    }

    public VmUser getModifierId() {
        return modifierId;
    }

    public void setModifierId(VmUser modifierId) {
        this.modifierId = modifierId;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getModificationTime() {
        return this.modificationTime;
    }

    public void setModificationTime(Date modificationTime) {
        this.modificationTime = modificationTime;
    }

    /**
     * Get history for this entity.
     *
     * @return history
     */
    public abstract List<History> getHistoryList();

    /**
     * Set history for this entity
     *
     * @param historyList
     */
    public abstract void setHistoryList(List<History> historyList);

    /**
     * Update the fields.
     *
     * @param target target object
     * @param source source object
     */
    public void update(AuditedObject target,
            AuditedObject source) {
        target.setMajorVersion(source.getMajorVersion());
        target.setMidVersion(source.getMidVersion());
        target.setMinorVersion(source.getMinorVersion());
        target.setModifierId(source.getModifierId());
        target.setReason(source.getReason());
        target.setModificationTime(source.getModificationTime());
        target.setHistoryList(source.getHistoryList());
    }

    @Override
    public int compareTo(AuditedObject o) {
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

    /**
     * Add history to this entity.
     *
     * @param history History to add.
     */
    public void addHistory(History history) {
        if (getHistoryList() == null) {
            setHistoryList(new ArrayList<>());
        }
        getHistoryList().add(history);
    }

    private synchronized boolean auditable(AuditedObject v) {
        History current;
        if (v.getHistoryList() != null && !v.getHistoryList().isEmpty()) {
            current = v.getHistoryList().get(v.getHistoryList().size() - 1);
            for (HistoryField hf : current.getHistoryFieldList()) {
                try {
                    //Compare audit field vs. the record in history.
                    Object o = FieldUtils.readField(FieldUtils.getField(v.getClass(),
                            hf.getFieldName(), true), v);
                    if ((o == null && !hf.getFieldValue().equals("null"))
                            || (o != null && !o.equals(hf.getFieldValue()))) {
                        return true;
                    }
                }
                catch (SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
            //As last check the version fields for changes (i.e. baselineing, etc.)
            return current.getMajorVersion() < v.getMajorVersion()
                    || current.getMidVersion() < v.getMidVersion()
                    || current.getMinorVersion() < v.getMinorVersion();
        }
        //No history so it is auditable.
        return true;
    }

    public void updateHistory() throws Exception {
        updateHistory(this);
    }

    protected void updateHistory(AuditedObject v) throws Exception {
        //Only if an auditable field has been modified
        if (auditable(v)) {
            //Add history of creation
            HistoryServer hs = new HistoryServer();
            if (getModifierId() == null) {
                try {
                    //By default blame system
                    hs.setModifierId(new VMUserServer(1).getEntity());
                }
                catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
            hs.setReason(getReason());
            if (getHistoryList() != null && !getHistoryList().isEmpty()) {
                History last = getHistoryList().get(getHistoryList().size() - 1);
                if ((getMajorVersion() == 0 && getMidVersion() == 0) // It has default values
                        || last.getMajorVersion() == getMajorVersion() // Or it has a higher mid/major version assigned.
                        && last.getMidVersion() == getMidVersion()) {
                    //Make it one more than latest
                    hs.setMinorVersion(last.getMinorVersion() + 1);
                } else {
                    //Copy values from object as it was forced changed.
                    hs.setMajorVersion(getMajorVersion());
                    hs.setMidVersion(getMidVersion());
                    hs.setMinorVersion(getMinorVersion());
                }
            }
            hs.setModificationTime(getModificationTime() == null
                    ? new Date() : getModificationTime());
            hs.write2DB();
            //Check the fields to be placed in history
            updateFields(hs, v);
        }
    }

    private synchronized void updateFields(HistoryServer hs, AuditedObject v)
            throws IllegalArgumentException, IllegalAccessException, Exception {
        for (Field field : FieldUtils.getFieldsListWithAnnotation(v.getClass(),
                Auditable.class)) {
            Class type = field.getType();
            String name = field.getName();
            FieldType ft = FieldTypeServer.findType(type.getSimpleName());
            if (ft == null) {
                FieldTypeServer fts = new FieldTypeServer();
                fts.setTypeName(type.getSimpleName());
                fts.write2DB();
                ft = fts.getEntity();
            }
            HistoryFieldServer hf
                    = new HistoryFieldServer(ft.getId(), hs.getId());
            hf.setFieldName(name);
            hf.setFieldType(ft);
            hf.setHistory(hs.getEntity());
            field.setAccessible(true);
            Object value = field.get(v);
            hf.setFieldValue(value == null ? "null" : value.toString());
            hf.write2DB();
            hs.getHistoryFieldList().add(hf.getEntity());
        }
        hs.write2DB();
        v.addHistory(hs.getEntity());
        DataBaseManager.getEntityManager().persist(v);
    }
}