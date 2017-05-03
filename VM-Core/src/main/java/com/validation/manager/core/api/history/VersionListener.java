package com.validation.manager.core.api.history;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.mapped.Versionable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
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
                v.updateHistory();
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
                v.updateHistory();
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
    }

    public Versionable cloneEntity(Versionable entity) {
        CopyGroup group = new CopyGroup();
        group.setShouldResetPrimaryKey(true);
        Versionable copy = (Versionable) DataBaseManager.getEntityManager()
                .unwrap(JpaEntityManager.class).copy(entity, group);
        return copy;
    }
}
