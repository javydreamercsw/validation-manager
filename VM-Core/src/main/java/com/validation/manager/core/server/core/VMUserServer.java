package com.validation.manager.core.server.core;

import static com.validation.manager.core.DataBaseManager.createdQuery;
import static com.validation.manager.core.DataBaseManager.getEntityManager;
import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import static com.validation.manager.core.DataBaseManager.namedQuery;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.CorrectiveAction;
import com.validation.manager.core.db.Role;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.UserAssigment;
import com.validation.manager.core.db.UserHasInvestigation;
import com.validation.manager.core.db.UserHasRootCause;
import com.validation.manager.core.db.UserModifiedRecord;
import com.validation.manager.core.db.UserTestPlanRole;
import com.validation.manager.core.db.UserTestProjectRole;
import com.validation.manager.core.db.VmException;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.CorrectiveActionJpaController;
import com.validation.manager.core.db.controller.RoleJpaController;
import com.validation.manager.core.db.controller.TestCaseJpaController;
import com.validation.manager.core.db.controller.UserAssigmentJpaController;
import com.validation.manager.core.db.controller.UserHasInvestigationJpaController;
import com.validation.manager.core.db.controller.UserHasRootCauseJpaController;
import com.validation.manager.core.db.controller.UserModifiedRecordJpaController;
import com.validation.manager.core.db.controller.UserStatusJpaController;
import com.validation.manager.core.db.controller.UserTestPlanRoleJpaController;
import com.validation.manager.core.db.controller.UserTestProjectRoleJpaController;
import com.validation.manager.core.db.controller.VmExceptionJpaController;
import com.validation.manager.core.db.controller.VmUserJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import static com.validation.manager.core.server.core.VMSettingServer.getSetting;
import static com.validation.manager.core.tool.MD5.encrypt;
import static java.lang.System.currentTimeMillis;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import static java.util.Calendar.getInstance;
import java.util.Date;
import java.util.List;
import static java.util.Locale.getDefault;
import java.util.logging.Level;
import static java.util.logging.Logger.getLogger;
import javax.persistence.EntityTransaction;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class VMUserServer extends VmUser implements EntityServer<VmUser>,
        VersionableServer<VmUser> {

    private static final long serialVersionUID = 1L;
    private boolean hashPassword = true;
    private boolean increaseAttempts = false;
    private static List<Object> result;
    private boolean change;

    public VMUserServer(VmUser vmu) {
        update(VMUserServer.this, vmu);
        //previously hashing the already hashed password
        hashPassword = false;
    }

    //create user object and login
    public VMUserServer(String attrUN, String attrUPW) throws Exception {
        try {
            result = createdQuery(
                    "SELECT u FROM VmUser uu WHERE u.username='" // NOI18N
                    + attrUN + "' AND u.password='" + encrypt(attrUPW)
                    + "' AND u.userStatusId.id <> 2");
            //throw exception if no result found
            if (result.isEmpty()) {
                parameters.clear();
                parameters.put("username", attrUN);
                result
                        = namedQuery("VmUser.findByUsername",
                                parameters);
                //The username is valid but wrong password. Increase the login attempts.
                if (result.size() > 0) {
                    increaseAttempts = true;
                    VmUser xcu = (VmUser) result.get(0);
                    setAttempts(xcu.getAttempts());
                    write2DB();
                }
                throw new Exception();
            } else {
                VmUser vmu = (VmUser) result.get(0);
                update(VMUserServer.this, vmu);
                //previously hashing the already hashed password
                hashPassword = false;
                int status = vmu.getUserStatusId().getId();
                if (status != 2) {
                    Calendar cal2 = getInstance(),
                            now = getInstance();
                    cal2.setTime(vmu.getLastModified());
                    long diffMillis = now.getTimeInMillis()
                            - cal2.getTimeInMillis();
                    long diffDays = diffMillis / (24 * 60 * 60 * 1000);
                    long age = getSetting("password.aging")
                            .getIntVal();
                    if (diffDays >= age) {
                        status = 4;
                    } else {
                        status = 1;
                    }
                    setAttempts(0);
                } else {
                    setAttempts(vmu.getAttempts());
                }
                setUserStatusId(new UserStatusJpaController(
                        getEntityManagerFactory())
                        .findUserStatus(status));
            }
        } catch (Exception e) {
            EntityTransaction transaction
                    = getEntityManager().getTransaction();
            if (transaction.isActive()) {
                transaction.rollback();
            }
            result = createdQuery("SELECT u FROM VmUser u WHERE u.username='"
                    + attrUN + "' AND u.userStatusId.id <> 2");
            //increase number of attempts
            if (!result.isEmpty()) {
                VmUser vmu = (VmUser) result.get(0);
                update(VMUserServer.this, vmu);
                //Don't rehash the pasword!
                hashPassword = false;
                //Increase attempts after a unsuccessfull login.
                setIncreaseAttempts(true);
                setLastModified(vmu.getLastModified());
                setChange(false);
                write2DB();
            }
        }
    }

//create user object for data structures
    public VMUserServer(int id) throws Exception {
        parameters.clear();
        parameters.put("id", id);
        result = namedQuery("VmUser.findById", parameters);
        //throw exception if no result found
        if (result.size() > 0) {
            VmUser vmu = (VmUser) result.get(0);
            update(VMUserServer.this, vmu);
            //previously hashing the already hashed password
            hashPassword = false;
        } else {
            throw new Exception("Unable to find user");
        }
    }

    public VMUserServer(String name, String password, String firstName,
            String lastName, String email) throws Exception {
        setId(0);
        setUsername(name);
        setPassword(password);
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setLocale(getDefault().toString());
        setUserStatusId(new UserStatusJpaController(
                getEntityManagerFactory()).findUserStatus(1));
        setAttempts(0);
        setLastModified(new java.sql.Timestamp(currentTimeMillis()));
        setHashPassword(true);
    }

//create user object
    public VMUserServer(String name, String password, String firstName,
            String lastName, String email, int userStatusId, int attempts,
            java.sql.Timestamp lastModified) throws Exception {
        setUsername(name);
        setPassword(password);
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setUserStatusId(new UserStatusJpaController(
                getEntityManagerFactory())
                .findUserStatus(userStatusId));
        setAttempts(attempts);
        setLastModified(lastModified);
        setHashPassword(true);
    }

    //write to db
    @Override
    public int write2DB() throws Exception {
        Date date;
        if (getUserStatusId().getId() == 4) {
            //Changed from aged out to password changed. Clear status
            setUserStatusId(new UserStatusJpaController(
                    getEntityManagerFactory())
                    .findUserStatus(1));
            setAttempts(0);
            setModificationReason("audit.user.account.aged");
            setChange(true);
        }
        //Increase login attempts
        if (increaseAttempts) {
            setAttempts(getAttempts() + 1);
            increaseAttempts = false;
        }
        //Lock account if needed. Can't lock main admin.
        if (getAttempts()
                > getSetting("password.attempts").getIntVal()
                && getId() > 1) {
            setUserStatusId(
                    new UserStatusJpaController(
                            getEntityManagerFactory()).findUserStatus(2));
        }
        VmUserJpaController controller
                = new VmUserJpaController(getEntityManagerFactory());
        if (getId() > 0) {
            if (isChange()) {
                setModifierId(getId());
                date = new Date();
                setLastModified(date);
                setChange(false);
            } else {
                date = getLastModified();
            }
            setLastModified(date);
            //Sometimes password got re-hashed
            String password;
            if (hashPassword) {
                password = encrypt(getPassword().replaceAll("'", "\\\\'"));
            } else {
                password = getPassword().replaceAll("'", "\\\\'");
            }
            VmUser vmu = controller.findVmUser(getId());
            vmu.setAttempts(getAttempts());
            vmu.setEmail(getEmail().replaceAll("'", "\\\\'"));
            vmu.setLastName(getLastName().replaceAll("'", "\\\\'"));
            vmu.setLastModified(getLastModified());
            vmu.setFirstName(getFirstName().replaceAll("'", "\\\\'"));
            vmu.setUserStatusId(getUserStatusId());
            vmu.setUsername(getUsername().replaceAll("'", "\\\\'"));
            vmu.setPassword(password);
            vmu.setModificationReason(getModificationReason() == null
                    ? "audit.general.modified" : getModificationReason());
            vmu.setModifierId(getModifierId());
            vmu.setModificationTime(new Timestamp(new Date().getTime()));
            vmu.setRoleList(getRoleList());
            vmu.setCorrectiveActionList(getCorrectiveActionList());
            vmu.setTestCaseList(getTestCaseList());
            vmu.setUserAssigmentList(getUserAssigmentList());
            vmu.setUserAssigmentList1(getUserAssigmentList1());
            vmu.setUserHasInvestigationList(getUserHasInvestigationList());
            vmu.setUserHasRootCauseList(getUserHasRootCauseList());
            vmu.setUserTestPlanRoleList(getUserTestPlanRoleList());
            vmu.setUserTestProjectRoleList(getUserTestProjectRoleList());
            vmu.setVmExceptionList(getVmExceptionList());
            controller.edit(vmu);
        } else {
            VmUser vmu = new VmUser(
                    getUsername().replaceAll("'", "\\\\'"), getPassword(),
                    getEmail().replaceAll("'", "\\\\'"),
                    getFirstName().replaceAll("'", "\\\\'"),
                    getLastName().replaceAll("'", "\\\\'"), getLocale(),
                    getLastModified(), new UserStatusJpaController(
                            getEntityManagerFactory())
                    .findUserStatus(1), getAttempts());
            update(vmu, this);
            controller.create(vmu);
            setId(vmu.getId());
        }
        setChange(false);
        setModificationReason("");
        return getId();
    }

//create complete list of users
    public static ArrayList<VMUserServer> getVMUsers() {
        ArrayList<VMUserServer> coreUsers = new ArrayList<VMUserServer>();
        try {
            result = createdQuery(
                    "Select x from VMUser x order by x.username");
            for (Object o : result) {
                coreUsers.add(new VMUserServer((VmUser) o));
            }
        } catch (Exception e) {
            coreUsers.clear();
        }
        return coreUsers;
    }

    public boolean isHashPassword() {
        return hashPassword;
    }

    public void setHashPassword(boolean hashPassword) {
        this.hashPassword = hashPassword;
    }

    public boolean isIncreaseAttempts() {
        return increaseAttempts;
    }

    public void setIncreaseAttempts(boolean increaseAttempts) {
        this.increaseAttempts = increaseAttempts;
    }

    public boolean isPasswordUsable(String newPass, boolean hash) {
        int id;
        boolean passwordIsUsable = true;
        try {
            //Now check if password is not the same as the current password
            result = createdQuery(
                    "Select x from VmUser x where x.id=" + getId()
                    + " and x.password='"
                    + (hash ? encrypt(newPass) : newPass) + "'");
            if (result.size() > 0) {
                passwordIsUsable = false;
            } else {
                //Here we'll catch if the password have been used in the
                //unusable period (use id in case the username was modified)
                VMUserServer user = new VMUserServer(getId());
                for (VmUser u : user.getVersions()) {
                    //Now check the aging
                    long diff = currentTimeMillis()
                            - u.getLastModified().getTime();
                    if (diff / (1000 * 60 * 60 * 24)
                            > getSetting("password.unusable_period")
                            .getIntVal()) {
                        passwordIsUsable = false;
                    }
                }
            }
            //---------------------------
        } catch (Exception ex) {
            passwordIsUsable = false;
        }
        return passwordIsUsable;
    }

    public boolean isPasswordUsable(String newPass) {
        return isPasswordUsable(newPass, true);
    }

    private void setChange(boolean c) {
        change = c;
    }

    private boolean isChange() {
        return change;
    }

    public static void deleteUser(VmUser user) {
        if (user != null) {
            parameters.clear();
            parameters.put("id", user.getId());
            user = (VmUser) namedQuery("VmUser.findById",
                    parameters).get(0);
            try {
                for (CorrectiveAction ca : user.getCorrectiveActionList()) {
                    new CorrectiveActionJpaController(
                            getEntityManagerFactory()).destroy(ca.getId());
                }
                for (Role r : user.getRoleList()) {
                    new RoleJpaController(
                            getEntityManagerFactory()).destroy(r.getId());
                }
                for (TestCase tc : user.getTestCaseList()) {
                    new TestCaseJpaController(
                            getEntityManagerFactory()).destroy(tc.getTestCasePK());
                }
                for (UserAssigment ua : user.getUserAssigmentList()) {
                    new UserAssigmentJpaController(
                            getEntityManagerFactory()).destroy(ua.getUserAssigmentPK());
                }
                for (UserAssigment ua : user.getUserAssigmentList1()) {
                    new UserAssigmentJpaController(
                            getEntityManagerFactory()).destroy(ua.getUserAssigmentPK());
                }
                for (UserHasInvestigation i : user.getUserHasInvestigationList()) {
                    new UserHasInvestigationJpaController(
                            getEntityManagerFactory()).destroy(i.getUserHasInvestigationPK());
                }
                for (UserHasRootCause rc : user.getUserHasRootCauseList()) {
                    new UserHasRootCauseJpaController(
                            getEntityManagerFactory()).destroy(rc.getUserHasRootCausePK());
                }
                for (UserModifiedRecord rc : user.getUserModifiedRecordList()) {
                    new UserModifiedRecordJpaController(
                            getEntityManagerFactory()).destroy(rc.getUserModifiedRecordPK());
                }
                for (UserTestPlanRole rc : user.getUserTestPlanRoleList()) {
                    new UserTestPlanRoleJpaController(
                            getEntityManagerFactory()).destroy(rc.getUserTestPlanRolePK());
                }
                for (UserTestProjectRole rc : user.getUserTestProjectRoleList()) {
                    new UserTestProjectRoleJpaController(
                            getEntityManagerFactory()).destroy(rc.getUserTestProjectRolePK());
                }
                for (VmException rc : user.getVmExceptionList()) {
                    new VmExceptionJpaController(
                            getEntityManagerFactory()).destroy(rc.getVmExceptionPK());
                }
                parameters.clear();
                parameters.put("id", user.getId());
                user = (VmUser) namedQuery("VmUser.findById",
                        parameters).get(0);
                new VmUserJpaController(
                        getEntityManagerFactory()).destroy(user.getId());
            } catch (NonexistentEntityException ex) {
                getLogger(VMUserServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalOrphanException ex) {
                getLogger(VMUserServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Check user credentials.
     *
     * the password is already encrypted. Usually queries from within the server
     * itself
     *
     * @param username User name
     * @param password Password
     * @param encrypt Password needs encrypting?
     * @return true if valid
     */
    public static boolean validCredentials(String username,
            String password, boolean encrypt) {
        try {
            parameters.clear();
            parameters.put("username", username);
            parameters.put("password", encrypt
                    ? encrypt(password.replaceAll("'", "\\\\'")) : password);
            return !createdQuery("SELECT x FROM VmUser x "
                    + "WHERE x.username = :username and x.password = :password",
                    parameters).isEmpty();
        } catch (VMException e) {
            getLogger(VMUserServer.class.getName()).log(Level.SEVERE, null, e);
            return false;
        } catch (Exception ex) {
            getLogger(VMUserServer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static VmUser getUser(String username,
            String password, boolean encrypt) {
        try {
            parameters.clear();
            parameters.put("username", username);
            parameters.put("password", encrypt
                    ? encrypt(password.replaceAll("'", "\\\\'")) : password);
            if (validCredentials(username, password, encrypt)) {
                return (VmUser) createdQuery("SELECT x FROM VmUser x "
                        + "WHERE x.username = :username and x.password = :password",
                        parameters).get(0);
            } else {
                return null;
            }
        } catch (Exception ex) {
            getLogger(VMUserServer.class.getName()).log(Level.SEVERE, null, ex);
            return null;

        }
    }

    public VmUser getEntity() {
        return new VmUserJpaController(
                getEntityManagerFactory())
                .findVmUser(getId());
    }

    public void update(VmUser target, VmUser source) {
        target.setPassword(source.getPassword());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setEmail(source.getEmail());
        target.setAttempts(source.getAttempts());
        target.setLastModified(source.getLastModified());
        target.setUserStatusId(source.getUserStatusId());
        target.setRoleList(source.getRoleList());
        target.setCorrectiveActionList(source.getCorrectiveActionList());
        target.setTestCaseList(source.getTestCaseList());
        target.setUserAssigmentList(source.getUserAssigmentList());
        target.setUserAssigmentList1(source.getUserAssigmentList1());
        target.setUserHasInvestigationList(source.getUserHasInvestigationList());
        target.setUserHasRootCauseList(source.getUserHasRootCauseList());
        target.setUserTestPlanRoleList(source.getUserTestPlanRoleList());
        target.setUserTestProjectRoleList(source.getUserTestProjectRoleList());
        target.setVmExceptionList(source.getVmExceptionList());
        target.setId(source.getId());
        target.setMajorVersion(source.getMajorVersion());
        target.setMidVersion(source.getMidVersion());
        target.setMinorVersion(source.getMinorVersion());
    }

    public void update() {
        update(this, getEntity());
    }

    public List<VmUser> getVersions() {
        List<VmUser> versions = new ArrayList<VmUser>();
        parameters.clear();
        parameters.put("id", getEntity().getId());
        for (Object obj : namedQuery("VmUser.findById",
                parameters)) {
            versions.add((VmUser) obj);
        }
        return versions;
    }

    public boolean isChangeVersionable() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
