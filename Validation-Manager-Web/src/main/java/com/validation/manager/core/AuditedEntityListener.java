package com.validation.manager.core;

import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestPlanT;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.TestProjectT;
import com.validation.manager.core.db.UserModifiedRecord;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.VmUserT;
import com.validation.manager.core.db.controller.TestPlanTJpaController;
import com.validation.manager.core.db.controller.TestProjectTJpaController;
import com.validation.manager.core.db.controller.UserModifiedRecordJpaController;
import com.validation.manager.core.db.controller.UserStatusJpaController;
import com.validation.manager.core.db.controller.VmUserJpaController;
import com.validation.manager.core.db.controller.VmUserTJpaController;
import com.validation.manager.core.server.core.VMIdServer;
import com.validation.manager.core.tool.MD5;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private static final Logger LOG = Logger.getLogger(AuditedEntityListener.class.getSimpleName());

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
                                DataBaseManager.getEntityManagerFactory()).findVmUser(
                                auditedObject.getModifierId());
                        boolean updated = false;
                        if (modifier == null) {
                            //Default to admin
                            modifier = new VmUserJpaController(
                                    DataBaseManager.getEntityManagerFactory())
                                    .findVmUser(1);
                            if (modifier == null) {
                                LOG.log(Level.FINE,
                                        "Default user not available, creating...");
                                //Need to create the user
                                modifier = new VmUser("System",
                                        MD5.encrypt("system"), "", "System",
                                        "User",
                                        Locale.getDefault().toString(),
                                        new Date(),
                                        new UserStatusJpaController(
                                        DataBaseManager.getEntityManagerFactory())
                                        .findUserStatus(1), 0);
                                modifier.setAuditable(false);
                                new VmUserJpaController(
                                        DataBaseManager.getEntityManagerFactory())
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
                            record_ID = VMIdServer.getNextId("user_modified_record");
                            parameters.clear();
                            parameters.put("recordId", record_ID);
                            used = !DataBaseManager.namedQuery(
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
                                DataBaseManager.getEntityManagerFactory()).create(mod);
                        setEnabled(true);
                        LOG.log(Level.FINE, "Done!");
                        if (auditedObject instanceof VmUser) {
                            VmUser user = (VmUser) auditedObject;
                            LOG.log(Level.FINE, "Updating audit trail for {0}",
                                    auditedObject.getClass().getSimpleName());
                            VmUserT newUser = new VmUserT(mod.getUserModifiedRecordPK().getRecordId(),
                                    user.getId(), user.getUsername(), user.getPassword());
                            if (user.getUserStatusId() != null) {
                                newUser.setUserStatusId(user.getUserStatusId().getId());
                            }
                            newUser.setAttempts(user.getAttempts());
                            newUser.setEmail(user.getEmail());
                            newUser.setFirst(user.getFirst());
                            newUser.setLast(user.getLast());
                            newUser.setLastModifed(user.getLastModified());
                            newUser.setLocale(user.getLocale());
                            new VmUserTJpaController(
                                    DataBaseManager.getEntityManagerFactory()).create(newUser);
                            updated = true;
                            LOG.log(Level.FINE, "Done!");
                        }
                        else if (auditedObject instanceof TestProject) {
                            TestProject project = (TestProject) auditedObject;
                            LOG.log(Level.FINE, "Updating audit trail for {0}",
                                    auditedObject.getClass().getSimpleName());
                            TestProjectT newProject = new TestProjectT(mod.getUserModifiedRecordPK().getRecordId(),
                                    project.getId(), project.getName(), project.getActive());
                            if (project.getNotes() != null) {
                                newProject.setNotes(project.getNotes());
                            }
                            new TestProjectTJpaController(
                                    DataBaseManager.getEntityManagerFactory()).create(newProject);
                            updated = true;
                            LOG.log(Level.FINE, "Done!");
                        }
                        else if (auditedObject instanceof TestPlan) {
                            TestPlan tpl = (TestPlan) auditedObject;
                            LOG.log(Level.FINE, "Updating audit trail for {0}",
                                    auditedObject.getClass().getSimpleName());
                            TestPlanT tplt = new TestPlanT(mod.getUserModifiedRecordPK().getRecordId(),
                                    tpl.getTestPlanPK().getId(), tpl.getTestPlanPK().getTestProjectId(),
                                    tpl.getIsOpen(), tpl.getIsOpen());
                            tplt.setNotes(tpl.getNotes());
                            if (tpl.getTestPlan() != null) {
                                tplt.setRegressionTestPlanId(tpl.getTestPlan().getTestPlanPK().getId());
                                tplt.setRegressionTestPlanTestProjectId(tpl.getTestPlan().getTestPlanPK().getTestProjectId());
                            }
                            new TestPlanTJpaController(
                                    DataBaseManager.getEntityManagerFactory()).create(tplt);
                            updated = true;
                            LOG.log(Level.FINE, "Done!");
                        }
                        if (!updated) {
                            throw new Exception(auditedObject
                                    + " is an Auditable Object but it's processing "
                                    + "logic is not implemented yet!");
                        }
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
