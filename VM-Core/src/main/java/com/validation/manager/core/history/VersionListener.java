package com.validation.manager.core.history;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.History;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PostLoad;

/**
 *
 * @author Javier Ortiz Bultron javier.ortiz.78@gmail.com
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
}
