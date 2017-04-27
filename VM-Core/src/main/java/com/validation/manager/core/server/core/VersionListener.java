/**
 * This class handles versionable classes and handles the versioning
 * piece of the code.
 */
package com.validation.manager.core.server.core;

import com.validation.manager.core.AuditedObject;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.mapped.Versionable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityTransaction;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.sessions.CopyGroup;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class VersionListener {

    private final static Logger LOG
            = Logger.getLogger(VersionListener.class.getSimpleName());
    //Class and a list of keys already processed.
    private final Map<Class, List<Object>> PROCESSED = new HashMap<>();

    @PrePersist
    public synchronized void onCreation(Object entity) {
        //Handle audit
        if (entity instanceof AuditedObject) {
            AuditedObject ao = (AuditedObject) entity;
            if (ao.getModifierId() == null) {
                //By default blame system
                ao.setModifierId(1);
            }
            if (ao.getReason() == null) {
                ao.setReason("audit.general.creation");
            }
            ao.setModificationTime(new Date());
        }
        if (entity instanceof Versionable
                && DataBaseManager.isVersioningEnabled()) {
            Versionable versionable = (Versionable) entity;
            //Make sure the version is initialized
            if (versionable.getVersionId() <= 0) {
                try {
                    if (VMIdServer.getVMId("version") == null) {
                        new VMIdServer("version", 0).write2DB();
                    }
                    versionable.setVersionId(VMIdServer.getNextId("version"));
                }
                catch (VMException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    @PreUpdate
    public synchronized void onChange(Object entity) {
        //Handle audit
        if (entity instanceof AuditedObject) {
            AuditedObject ao = (AuditedObject) entity;
            if (ao.getModifierId() == null) {
                //By default blame system
                ao.setModifierId(1);
            }
            if (ao.getReason() == null) {
                ao.setReason("audit.general.modified");
            }
            ao.setModificationTime(new Date());
        }
        //Handle versioning
        if (entity instanceof Versionable
                && DataBaseManager.isVersioningEnabled()) {
            Versionable versionable = (Versionable) entity;
            if (versionable.isChangeVersionable()
                    && !PROCESSED.containsKey(versionable.getClass())
                    || (PROCESSED.containsKey(versionable.getClass())
                    && !PROCESSED.get(versionable.getClass()).contains(DataBaseManager
                            .getEntityManagerFactory()
                            .getPersistenceUnitUtil()
                            .getIdentifier(versionable)))) {
                EntityTransaction t = DataBaseManager.getEntityManager()
                        .getTransaction();
                if (!t.isActive()) {
                    t.begin();
                }
                try {
                    //Get the original one from the database
                    Versionable original = DataBaseManager
                            .getEntityManager()
                            .find(versionable.getClass(),
                                    DataBaseManager
                                            .getEntityManagerFactory()
                                            .getPersistenceUnitUtil()
                                            .getIdentifier(versionable));
                    LOG.log(Level.FINE, "Incoming change: {0}",
                            entity.toString());
                    //Copy the current state to keep it in history.
                    Versionable clone = cloneEntity(original);
                    clone.setDirty(false);
                    LOG.log(Level.FINE, "Clone: {0}",
                            clone.toString());
                    if (!PROCESSED.containsKey(versionable.getClass())) {
                        PROCESSED.put(versionable.getClass(), new ArrayList<>());
                    }
                    PROCESSED.get(versionable.getClass()).add(DataBaseManager
                            .getEntityManagerFactory()
                            .getPersistenceUnitUtil()
                            .getIdentifier(clone));
                    PROCESSED.get(versionable.getClass()).add(DataBaseManager
                            .getEntityManagerFactory()
                            .getPersistenceUnitUtil()
                            .getIdentifier(versionable));
                    DataBaseManager.getEntityManager().persist(clone);
                    //Create the new version
                    versionable.setMinorVersion(clone.getMinorVersion() + 1);
                    versionable.setDirty(true);
                    DataBaseManager.getEntityManager().merge(versionable);
                    t.commit();
                }
                catch (Exception ex) {
                    LOG.log(Level.WARNING, "Class {0} is not properly "
                            + "set up. Make sure constructor calls "
                            + "super() so the version is initialized.",
                            versionable.getClass());
                    LOG.log(Level.SEVERE, null, ex);
                    if (t.isActive()) {
                        t.rollback();
                    }
                }
            } else {
                LOG.fine("Ignore changes!");
            }
        }
        PROCESSED.clear();
    }

    public Versionable cloneEntity(Versionable entity) {
        CopyGroup group = new CopyGroup();
        group.setShouldResetPrimaryKey(true);
        Versionable copy = (Versionable) DataBaseManager.getEntityManager()
                .unwrap(JpaEntityManager.class).copy(entity, group);
        return copy;
    }
}
