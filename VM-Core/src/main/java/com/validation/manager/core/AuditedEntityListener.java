package com.validation.manager.core;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import static com.validation.manager.core.DataBaseManager.namedQuery;
import com.validation.manager.core.db.UserModifiedRecord;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.UserModifiedRecordJpaController;
import com.validation.manager.core.db.controller.UserStatusJpaController;
import com.validation.manager.core.db.controller.VmUserJpaController;
import com.validation.manager.core.server.core.VMIdServer;
import static com.validation.manager.core.server.core.VMIdServer.getNextId;
import com.validation.manager.core.tool.MD5;
import static com.validation.manager.core.tool.MD5.encrypt;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import static java.util.Locale.getDefault;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class AuditedEntityListener {

    private static boolean enabled = true;
    private AuditedObject last;
    private static final Logger LOG = 
            getLogger(AuditedEntityListener.class.getSimpleName());

    /**
     * Just before persisting an entity.
     *
     * @param o object to be persisted
     * @throws Exception if something goes wrong
     */
    @PrePersist
    public void persistEntity(Object o) throws Exception {
        LOG.log(Level.FINE, "Pre-persist: {0}", o);
        if (o instanceof AuditedObject && isEnabled()) {
            AuditedObject auditedObject = (AuditedObject) o;
            if (auditedObject.getModificationReason() == null
                    || auditedObject.getModificationReason().isEmpty()) {
                auditedObject.setModificationReason("audit.general.create");
            }
            auditTrail(o);
        }
    }

    /**
     * Just before removing an entity.
     *
     * @param o object to be removed
     * @throws Exception if something goes wrong
     */
    @PreRemove
    public void removeEntity(Object o) throws Exception {
        LOG.log(Level.FINE, "Pre-remove: {0}", o);
        if (o instanceof AuditedObject) {
            AuditedObject auditedObject = (AuditedObject) o;
            if (auditedObject.getModificationReason() == null
                    || auditedObject.getModificationReason().isEmpty()) {
                auditedObject.setModificationReason("audit.general.delete");
            }
            auditTrail(o);
        }
    }

    /**
     * Just before an entity is updated.
     *
     * @param o object to be updated
     * @throws Exception if something goes wrong
     */
    @PreUpdate
    public void updateEntity(Object o) throws Exception {
        if (o instanceof AuditedObject) {
            AuditedObject auditedObject = (AuditedObject) o;
            LOG.log(Level.FINE, "Pre-update: {0}", o);
            if (auditedObject.getModificationReason() == null
                    || auditedObject.getModificationReason().isEmpty()) {
                auditedObject.setModificationReason("audit.general.modified");
            }
            auditTrail(o);
        }
    }

    /**
     * Performs the audit trail functions.
     *
     * @param o Object to be audited
     * @throws Exception if something goes wrong
     */
    @SuppressWarnings("unchecked")
    private void auditTrail(final Object o) throws Exception {
        try {
            /**
             * VmUser modifying the record.
             */
            VmUser modifier;
            if (o instanceof AuditedObject) {
                VMAuditedObject auditedObject = (VMAuditedObject) o;
                if (last != null && last.equals(auditedObject)) {
                    setEnabled(false);
                }
                if (isEnabled()) {
                    last = auditedObject;
                    if (auditedObject.isAuditable()) {
                        LOG.log(Level.FINE, "Creating audit trail for {0}", o);
                        modifier = new VmUserJpaController(
                                getEntityManagerFactory()).findVmUser(
                                auditedObject.getModifierId());
                        if (modifier == null) {
                            //Default to admin
                            modifier = new VmUserJpaController(
                                    getEntityManagerFactory())
                                    .findVmUser(1);
                            if (modifier == null) {
                                LOG.log(Level.FINE,
                                        "Default user not available, creating...");
                                //Need to create the user
                                modifier = new VmUser("System",
                                        encrypt("system"), "", "System",
                                        "User",
                                        getDefault().toString(),
                                        new Date(),
                                        new UserStatusJpaController(
                                        getEntityManagerFactory())
                                        .findUserStatus(1), 0);
                                modifier.setAuditable(false);
                                new VmUserJpaController(
                                        getEntityManagerFactory())
                                        .create(modifier);
                                modifier.setAuditable(true);
                                LOG.log(Level.FINE, "Done!");
                            }
                        }
                        //Get an id from database
                        boolean used = true;
                        HashMap<String, Object> parameters = new HashMap<String, Object>();
                        int record_ID;
                        while (used) {
                            record_ID = getNextId("user_modified_record");
                            parameters.clear();
                            parameters.put("recordId", record_ID);
                            used = !namedQuery(
                                    "UserModifiedRecord.findByRecordId",
                                    parameters).isEmpty();
                        }
                        LOG.log(Level.FINE, "Creating UserModifiedRecord...");
                        UserModifiedRecord mod =
                                new UserModifiedRecord(modifier.getId());
                        mod.setReason(auditedObject.getModificationReason());
                        mod.setVmUser(modifier);
                        mod.setModifiedDate(new Date());
                        new UserModifiedRecordJpaController(
                                getEntityManagerFactory()).create(mod);
                        setEnabled(true);
                        LOG.log(Level.FINE, "Done!");
                    }
                }
            } else {
                throw new Exception(o + " is not an Auditable Object and was asked for processing!");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        } catch (Error e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    /**
     * @return the enabled
     */
    public static boolean isEnabled() {
        return enabled;
    }

    /**
     * @param e the enabled to set
     */
    public static void setEnabled(boolean e) {
        enabled = e;
    }
}
