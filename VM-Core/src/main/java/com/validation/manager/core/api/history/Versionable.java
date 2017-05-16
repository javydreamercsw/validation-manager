package com.validation.manager.core.api.history;

import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.FieldType;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.HistoryField;
import com.validation.manager.core.server.core.FieldTypeServer;
import com.validation.manager.core.server.core.HistoryFieldServer;
import com.validation.manager.core.server.core.HistoryServer;
import com.validation.manager.core.server.core.VMUserServer;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.openide.util.Exceptions;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "VERSIONABLE_TYPE")
public abstract class Versionable implements Comparable<Versionable>,
        Serializable {

    private static final Logger LOG
            = Logger.getLogger(Versionable.class.getSimpleName());

    @Column(name = "majorVersion", insertable = false, updatable = false)
    private Integer majorVersion = 0;
    @Column(name = "midVersion", insertable = false, updatable = false)
    private Integer midVersion = 0;
    @Column(name = "minorVersion", insertable = false, updatable = false)
    private Integer minorVersion = 1;
    @Column(name = "reason", insertable = false, updatable = false)
    private String reason;
    @Column(name = "modifierId", insertable = false, updatable = false)
    private int modifierId = -1;
    @Column(name = "modificationTime", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modificationTime;
    @Column(name = "dirty")
    @Basic(optional = false)
    private boolean dirty = false;

    public int getModifierId() {
        return modifierId;
    }

    public void setModifierId(int modifierId) {
        this.modifierId = modifierId;
    }

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

    public boolean getDirty() {
        return this.dirty;
    }

    /**
     * @param dirty the dirty to set
     */
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    /**
     * Increase major version.
     */
    public void increaseMajorVersion() {
        setMajorVersion(getMajorVersion() + 1);
        setMidVersion(0);
        setMinorVersion(0);
        try {
            if (this instanceof EntityServer) {
                EntityServer vs = (EntityServer) this;
                //Copy the version changes
                Versionable ao = (Versionable) vs.getEntity();
                ao.update(ao, this);
                updateHistory(ao);
                vs.write2DB();
            } else {
                updateHistory(this);
            }
        }
        catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Increase major version.
     */
    public void increaseMidVersion() {
        setMidVersion(getMidVersion() + 1);
        setMinorVersion(0);
        try {
            if (this instanceof EntityServer) {
                EntityServer vs = (EntityServer) this;
                //Copy the version changes
                Versionable v = (Versionable) vs.getEntity();
                v.update(v, this);
                updateHistory(v);
                vs.write2DB();
            } else {
                updateHistory(this);
            }
        }
        catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Increase minor is done by default when updating a record.
     *
     * @param target
     * @param source
     */
    public void update(Versionable target, Versionable source) {
        target.setDirty(source.getDirty());
        target.setMajorVersion(source.getMajorVersion());
        target.setMidVersion(source.getMidVersion());
        target.setMinorVersion(source.getMinorVersion());
        target.setModifierId(source.getModifierId());
        target.setReason(source.getReason());
        target.setModificationTime(source.getModificationTime());
        target.setHistoryList(source.getHistoryList());
        try {
            target.updateHistory();
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    public String toString() {
        return "Version: " + getMajorVersion() + "." + getMidVersion() + "."
                + getMinorVersion();
    }

    public enum CHANGE_LEVEL {
        MINOR,
        MODERATE,
        MAJOR
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

    public static synchronized boolean auditable(Versionable v) {
        History current;
        if (v.getHistoryList() != null && !v.getHistoryList().isEmpty()) {
            current = v.getHistoryList().get(v.getHistoryList().size() - 1);
            for (HistoryField hf : current.getHistoryFieldList()) {
                try {
                    //Compare audit field vs. the record in history.
                    Object o = FieldUtils.readField(FieldUtils.getField(v.getClass(),
                            hf.getFieldName(), true), v);
                    if ((o == null && !hf.getFieldValue().equals("null"))
                            || (!(o instanceof byte[]) && o != null
                            && !o.equals(hf.getFieldValue()))
                            || ((o instanceof byte[])
                            && !new String((byte[]) o,
                                    StandardCharsets.UTF_8)
                                    .equals(hf.getFieldValue()))) {
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
        //No history so it is auditable if it has marked fields for audit.
        return !FieldUtils.getFieldsListWithAnnotation(v.getClass(),
                Auditable.class).isEmpty();
    }

    public void updateHistory() throws Exception {
        updateHistory(this);
    }

    protected void updateHistory(Versionable v) throws Exception {
        //Only if an auditable field has been modified
        if (auditable(v)) {
            //Add history of creation
            HistoryServer hs = new HistoryServer();
            if (v.getModifierId() < 0) {
                try {
                    //By default blame system
                    hs.setModifierId(new VMUserServer(1).getEntity());
                }
                catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            } else {
                hs.setModifierId(new VMUserServer(v.getModifierId()).getEntity());
            }

            if (v.getHistoryList() != null && !v.getHistoryList().isEmpty()) {
                if (v.getReason() == null
                        || v.getReason().equals("audit.general.creation")) {
                    v.setReason("audit.general.modified");
                }
                History last = v.getHistoryList().get(v.getHistoryList().size() - 1);
                if ((v.getMajorVersion() == 0 && v.getMidVersion() == 0) // It has default values
                        || last.getMajorVersion() == v.getMajorVersion() // Or it has a higher mid/major version assigned.
                        && last.getMidVersion() == v.getMidVersion()) {
                    //Make it one more than latest
                    hs.setMinorVersion(last.getMinorVersion() + 1);
                } else {
                    //Copy values from object as it was forced changed.
                    hs.setMajorVersion(v.getMajorVersion());
                    hs.setMidVersion(v.getMidVersion());
                    hs.setMinorVersion(v.getMinorVersion());
                }
            } else {
                if (v.getReason() == null) {
                    v.setReason("audit.general.creation");
                }
            }
            hs.setReason(v.getReason());
            hs.setModificationTime(v.getModificationTime() == null
                    ? new Date() : v.getModificationTime());
            hs.write2DB();
            //Check the fields to be placed in history
            updateFields(hs, v);
        }
    }

    private synchronized void updateFields(HistoryServer hs, Versionable v)
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
            if (value instanceof byte[]) {
                byte[] bytes = (byte[]) value;
                value = new String(bytes, StandardCharsets.UTF_8);
            }
            hf.setFieldValue(value == null ? "null" : value.toString());
            hf.write2DB();
            hs.getHistoryFieldList().add(hf.getEntity());
        }
        hs.write2DB();
        v.addHistory(hs.getEntity());
    }
}
