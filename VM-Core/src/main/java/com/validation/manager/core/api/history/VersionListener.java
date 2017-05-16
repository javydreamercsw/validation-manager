package com.validation.manager.core.api.history;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.History;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class VersionListener {

    private final static Logger LOG
            = Logger.getLogger(VersionListener.class.getSimpleName());

    @PostLoad
    public synchronized void onLoad(Object entity) {
        if (entity instanceof Versionable
                && DataBaseManager.isVersioningEnabled()) {
            try {
                //Load the audit values from last time
                Versionable v = (Versionable) entity;
                if (!v.getHistoryList().isEmpty()) {
                    History h = v.getHistoryList().get(v.getHistoryList()
                            .size() - 1);
                    v.setMajorVersion(h.getMajorVersion());
                    v.setMidVersion(h.getMidVersion());
                    v.setMinorVersion(h.getMinorVersion());
                } else {
                    v.setMajorVersion(0);
                    v.setMidVersion(0);
                    v.setMinorVersion(1);
                }
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
    }

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
                if (v.getReason() == null
                        || v.getReason().equals("audit.general.creation")) {
                    v.setReason("audit.general.modified");
                }
                v.updateHistory();
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
    }
}
