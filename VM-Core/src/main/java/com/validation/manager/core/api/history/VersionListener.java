package com.validation.manager.core.api.history;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.FieldType;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.HistoryField;
import com.validation.manager.core.db.mapped.Versionable;
import com.validation.manager.core.server.core.FieldTypeServer;
import com.validation.manager.core.server.core.HistoryFieldServer;
import com.validation.manager.core.server.core.HistoryServer;
import com.validation.manager.core.server.core.VMUserServer;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.sessions.CopyGroup;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class VersionListener {

    private final static Logger LOG
            = Logger.getLogger(VersionListener.class.getSimpleName());

    @PrePersist
    public synchronized void onCreation(Object entity) {
        //Handle audit
        if (entity instanceof Versionable
                && DataBaseManager.isVersioningEnabled()) {
            try {
                Versionable v = (Versionable) entity;
                if (v.getReason() == null) {
                    v.setReason("audit.general.creation");
                }
                setHistory(v);
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
    }

    @PreUpdate
    public synchronized void onChange(Object entity) {
        //Handle audit
        if (entity instanceof Versionable
                && DataBaseManager.isVersioningEnabled()) {
            try {
                Versionable v = (Versionable) entity;
                if (v.getReason() == null) {
                    v.setReason("audit.general.modified");
                }
                setHistory(v);
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
    }

    private synchronized void setHistory(Versionable v) throws Exception {
        //Only if an auditable field has been modified
        if (auditable(v)) {
            //Add history of creation
            HistoryServer hs = new HistoryServer();
            if (v.getModifierId() == null) {
                try {
                    //By default blame system
                    hs.setModifierId(new VMUserServer(1).getEntity());
                }
                catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
            hs.setReason(v.getReason());
            if (v.getHistoryList() != null && !v.getHistoryList().isEmpty()) {
                History last = v.getHistoryList().get(v
                        .getHistoryList().size() - 1);
                if ((v.getMajorVersion() == 0 && v.getMidVersion() == 0) // It has default values
                        || last.getVersionMajor() == v.getMajorVersion() // Or it has a higher mid/major version assigned.
                        && last.getVersionMid() == v.getMidVersion()) {
                    //Make it one more than latest
                    hs.setVersionMinor(last.getVersionMinor() + 1);
                }
            }
            hs.setModificationTime(v.getModificationTime() == null
                    ? new Date() : v.getModificationTime());
            hs.write2DB();
            //Check the fields to be placed in history
            updateFields(hs, v);
        }
    }

    public Versionable cloneEntity(Versionable entity) {
        CopyGroup group = new CopyGroup();
        group.setShouldResetPrimaryKey(true);
        Versionable copy = (Versionable) DataBaseManager.getEntityManager()
                .unwrap(JpaEntityManager.class).copy(entity, group);
        return copy;
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
            hf.setFieldValue(value == null ? "null" : value.toString());
            hf.write2DB();
            hs.getHistoryFieldList().add(hf.getEntity());
        }
        hs.write2DB();
        v.addHistory(hs.getEntity());
        DataBaseManager.getEntityManager().persist(v);
    }

    private synchronized boolean auditable(Versionable v) {
        History current;
        if (v.getHistoryList() != null && !v.getHistoryList().isEmpty()) {
            current = v.getHistoryList().get(v.getHistoryList().size() - 1);
        } else {
            //Check if the changed fields are auditable or not
            return true;
        }
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
        return false;
    }
}
