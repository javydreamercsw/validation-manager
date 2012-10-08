package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
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
import com.validation.manager.core.db.VmUserT;
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
import com.validation.manager.core.tool.MD5;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityTransaction;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class VMUserServer extends VmUser implements EntityServer{

    private static final long serialVersionUID = 1L;
    private boolean hashPassword = true;
    private boolean increaseAttempts = false;
    private static List<Object> result;
    private static HashMap<String, Object> parameters = new HashMap<String, Object>();
    private boolean change;

    public VMUserServer(VmUser vmu) {
        setId(vmu.getId());
        setUsername(vmu.getUsername());
        //previously hashing the already hashed password
        hashPassword = false;
        setPassword(vmu.getPassword());
        setFirst(vmu.getFirst());
        setLast(vmu.getLast());
        setEmail(vmu.getEmail());
        setAttempts(vmu.getAttempts());
        setLastModified(vmu.getLastModified());
        setUserStatus(vmu.getUserStatus());
        setRoleList(vmu.getRoleList());
        setCorrectiveActionList(vmu.getCorrectiveActionList());
        setTestCaseList(vmu.getTestCaseList());
        setUserAssigmentList(vmu.getUserAssigmentList());
        setUserAssigmentList1(vmu.getUserAssigmentList1());
        setUserHasInvestigationList(vmu.getUserHasInvestigationList());
        setUserHasRootCauseList(vmu.getUserHasRootCauseList());
        setUserTestPlanRoleList(vmu.getUserTestPlanRoleList());
        setUserTestProjectRoleList(vmu.getUserTestProjectRoleList());
        setVmExceptionList(vmu.getVmExceptionList());
    }

    //create user object and login
    public VMUserServer(String attrUN, String attrUPW) throws Exception {
        try {
            result = DataBaseManager.createdQuery("SELECT u FROM VmUser uu WHERE u.username='" // NOI18N
                    + attrUN + "' AND u.password='" + MD5.encrypt(attrUPW) + "' AND u.userStatusId.id <> 2");
            //throw exception if no result found
            if (result.isEmpty()) {
                parameters.clear();
                parameters.put("username", attrUN);
                result = DataBaseManager.namedQuery("XincoCoreUser.findByUsername", parameters);
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
                setId(vmu.getId());
                setUsername(vmu.getUsername());
                //previously hashing the already hashed password
                hashPassword = false;
                setPassword(attrUPW);
                setFirst(vmu.getFirst());
                setLast(vmu.getLast());
                setEmail(vmu.getEmail());
                int status = vmu.getUserStatus().getId();
                if (status != 2) {
                    Calendar cal2 = GregorianCalendar.getInstance(), now = GregorianCalendar.getInstance();
                    cal2.setTime(vmu.getLastModified());
                    long diffMillis = now.getTimeInMillis() - cal2.getTimeInMillis();
                    long diffDays = diffMillis / (24 * 60 * 60 * 1000);
                    long age = VMSettingServer.getSetting("password.aging").getIntVal();
                    if (diffDays >= age) {
                        status = 4;
                    } else {
                        status = 1;
                    }
                    setAttempts(0);
                } else {
                    setAttempts(vmu.getAttempts());
                }
                setRoleList(vmu.getRoleList());
                setCorrectiveActionList(vmu.getCorrectiveActionList());
                setTestCaseList(vmu.getTestCaseList());
                setUserAssigmentList(vmu.getUserAssigmentList());
                setUserAssigmentList1(vmu.getUserAssigmentList1());
                setUserHasInvestigationList(vmu.getUserHasInvestigationList());
                setUserHasRootCauseList(vmu.getUserHasRootCauseList());
                setUserTestPlanRoleList(vmu.getUserTestPlanRoleList());
                setUserTestProjectRoleList(vmu.getUserTestProjectRoleList());
                setVmExceptionList(vmu.getVmExceptionList());
                setUserStatus(new UserStatusJpaController(DataBaseManager.getEntityManagerFactory()).findUserStatus(status));
                setLastModified(vmu.getLastModified());
            }
        } catch (Exception e) {
            EntityTransaction transaction = DataBaseManager.getEntityManager().getTransaction();
            if (transaction.isActive()) {
                transaction.rollback();
            }
            result = DataBaseManager.createdQuery("SELECT u FROM VmUser u WHERE u.username='"
                    + attrUN + "' AND u.userStatus.id <> 2");
            //increase number of attempts
            if (!result.isEmpty()) {
                VmUser vmu = (VmUser) result.get(0);
                setId(vmu.getId());
                setUsername(vmu.getUsername());
                //Don't rehash the pasword!
                hashPassword = false;
                setPassword(vmu.getPassword());
                setFirst(vmu.getFirst());
                setLast(vmu.getLast());
                setEmail(vmu.getEmail());
                setUserStatus(vmu.getUserStatus());
                //Increase attempts after a unsuccessfull login.
                setIncreaseAttempts(true);
                setLastModified(vmu.getLastModified());
                setChange(false);
                setRoleList(vmu.getRoleList());
                setCorrectiveActionList(vmu.getCorrectiveActionList());
                setTestCaseList(vmu.getTestCaseList());
                setUserAssigmentList(vmu.getUserAssigmentList());
                setUserAssigmentList1(vmu.getUserAssigmentList1());
                setUserHasInvestigationList(vmu.getUserHasInvestigationList());
                setUserHasRootCauseList(vmu.getUserHasRootCauseList());
                setUserTestPlanRoleList(vmu.getUserTestPlanRoleList());
                setUserTestProjectRoleList(vmu.getUserTestProjectRoleList());
                setVmExceptionList(vmu.getVmExceptionList());
                write2DB();
            }
        }
    }

//create user object for data structures
    public VMUserServer(int attrID) throws Exception {
        try {
            parameters.clear();
            parameters.put("id", attrID);
            result = DataBaseManager.namedQuery("XincoCoreUser.findById", parameters);
            //throw exception if no result found
            if (result.size() > 0) {
                VmUser vmu = (VmUser) result.get(0);
                setId(vmu.getId());
                setUsername(vmu.getUsername());
                //previously hashing the already hashed password
                hashPassword = false;
                setPassword(vmu.getPassword());
                setFirst(vmu.getFirst());
                setLast(vmu.getLast());
                setEmail(vmu.getEmail());
                setUserStatus(vmu.getUserStatus());
                setAttempts(vmu.getAttempts());
                setLastModified(vmu.getLastModified());
                setRoleList(vmu.getRoleList());
                setCorrectiveActionList(vmu.getCorrectiveActionList());
                setTestCaseList(vmu.getTestCaseList());
                setUserAssigmentList(vmu.getUserAssigmentList());
                setUserAssigmentList1(vmu.getUserAssigmentList1());
                setUserHasInvestigationList(vmu.getUserHasInvestigationList());
                setUserHasRootCauseList(vmu.getUserHasRootCauseList());
                setUserTestPlanRoleList(vmu.getUserTestPlanRoleList());
                setUserTestProjectRoleList(vmu.getUserTestProjectRoleList());
                setVmExceptionList(vmu.getVmExceptionList());
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new Exception();
        }
    }

    public VMUserServer(String name, String password, String firstName,
            String lastName, String email) throws Exception {
        setId(0);
        setUsername(name);
        setPassword(password);
        setFirst(firstName);
        setLast(lastName);
        setEmail(email);
        setLocale(Locale.getDefault().toString());
        setUserStatus(new UserStatusJpaController(DataBaseManager.getEntityManagerFactory()).findUserStatus(1));
        setAttempts(0);
        setLastModified(new java.sql.Timestamp(System.currentTimeMillis()));
        setHashPassword(true);
    }

//create user object
    public VMUserServer(String name, String password, String firstName,
            String lastName, String email, int userStatusId, int attempts,
            java.sql.Timestamp lastModified) throws Exception {
        setUsername(name);
        setPassword(password);
        setFirst(firstName);
        setLast(lastName);
        setEmail(email);
        setUserStatus(new UserStatusJpaController(DataBaseManager.getEntityManagerFactory()).findUserStatus(userStatusId));
        setAttempts(attempts);
        setLastModified(lastModified);
        setHashPassword(true);
    }

    //write to db
    @Override
    public int write2DB() throws Exception {
        Date date;
        if (getUserStatus().getId() == 4) {
            //Changed from aged out to password changed. Clear status
            setUserStatus(new UserStatusJpaController(DataBaseManager.getEntityManagerFactory()).findUserStatus(1));
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
        if (getAttempts() > VMSettingServer.getSetting("password.attempts").getIntVal()
                && getId() > 1) {
            setUserStatus(new UserStatusJpaController(DataBaseManager.getEntityManagerFactory()).findUserStatus(2));
        }
        VmUserJpaController controller = new VmUserJpaController( DataBaseManager.getEntityManagerFactory());
        if (getId() > 0) {
            if (isChange()) {
                setModifierId(getId());
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
                password = MD5.encrypt(getPassword().replaceAll("'", "\\\\'"));
            } else {
                password = getPassword().replaceAll("'", "\\\\'");
            }
            VmUser vmu = controller.findVmUser(getId());
            vmu.setAttempts(getAttempts());
            vmu.setEmail(getEmail().replaceAll("'", "\\\\'"));
            vmu.setLast(getLast().replaceAll("'", "\\\\'"));
            vmu.setLastModified(getLastModified());
            vmu.setFirst(getFirst().replaceAll("'", "\\\\'"));
            vmu.setUserStatus(getUserStatus());
            vmu.setUsername(getUsername().replaceAll("'", "\\\\'"));
            vmu.setPassword(password);
            vmu.setModificationReason(getModificationReason() == null ? "audit.general.modified" : getModificationReason());
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
                    getUsername().replaceAll("'", "\\\\'"), getPassword(), getEmail().replaceAll("'", "\\\\'"),
                    getFirst().replaceAll("'", "\\\\'"), getLast().replaceAll("'", "\\\\'"), getLocale(),
                    getLastModified(), new UserStatusJpaController( DataBaseManager.getEntityManagerFactory()).findUserStatus(1), getAttempts());
            vmu.setUserStatus(getUserStatus());
            vmu.setModificationReason(getModificationReason());
            vmu.setModifierId(getModifierId());
            vmu.setModificationTime(new Timestamp(new Date().getTime()));
            vmu.setLocale(getLocale());
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
            result = DataBaseManager.createdQuery("Select x from VMUser x order by x.username");
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
            result = DataBaseManager.createdQuery("Select x from VmUser x where x.id=" + getId()
                    + " and x.password='" + (hash ? MD5.encrypt(newPass) : newPass) + "'");
            if (result.size() > 0) {
                passwordIsUsable = false;
            } else {
                //Here we'll catch if the password have been used in the unusable period (use id in case the username was modified)
                result = DataBaseManager.createdQuery("Select x from VmUser x where x.id=" + getId());
                id = ((VmUser) result.get(0)).getId();
                result = DataBaseManager.createdQuery("Select x from VmUserT x where x.id=" + id
                        + " and x.password='" + (hash ? MD5.encrypt(newPass) : newPass) + "'");
                for (Object o : result) {
                    //Now check the aging
                    VmUserT user = (VmUserT) o;
                    long diff = System.currentTimeMillis() - user.getLastModifed().getTime();
                    if (diff / (1000 * 60 * 60 * 24) > VMSettingServer.getSetting("password.unusable_period").getIntVal()) {
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
            user = (VmUser) DataBaseManager.namedQuery("VmUser.findById", parameters).get(0);
            try {
                for (CorrectiveAction ca : user.getCorrectiveActionList()) {
                    new CorrectiveActionJpaController( DataBaseManager.getEntityManagerFactory()).destroy(ca.getId());
                }
                for (Role r : user.getRoleList()) {
                    new RoleJpaController( DataBaseManager.getEntityManagerFactory()).destroy(r.getId());
                }
                for (TestCase tc : user.getTestCaseList()) {
                    new TestCaseJpaController( DataBaseManager.getEntityManagerFactory()).destroy(tc.getTestCasePK());
                }
                for (UserAssigment ua : user.getUserAssigmentList()) {
                    new UserAssigmentJpaController( DataBaseManager.getEntityManagerFactory()).destroy(ua.getUserAssigmentPK());
                }
                for (UserAssigment ua : user.getUserAssigmentList1()) {
                    new UserAssigmentJpaController( DataBaseManager.getEntityManagerFactory()).destroy(ua.getUserAssigmentPK());
                }
                for (UserHasInvestigation i : user.getUserHasInvestigationList()) {
                    new UserHasInvestigationJpaController( DataBaseManager.getEntityManagerFactory()).destroy(i.getUserHasInvestigationPK());
                }
                for (UserHasRootCause rc : user.getUserHasRootCauseList()) {
                    new UserHasRootCauseJpaController( DataBaseManager.getEntityManagerFactory()).destroy(rc.getUserHasRootCausePK());
                }
                for (UserModifiedRecord rc : user.getUserModifiedRecordList()) {
                    new UserModifiedRecordJpaController( DataBaseManager.getEntityManagerFactory()).destroy(rc.getUserModifiedRecordPK());
                }
                for (UserTestPlanRole rc : user.getUserTestPlanRoleList()) {
                    new UserTestPlanRoleJpaController( DataBaseManager.getEntityManagerFactory()).destroy(rc.getUserTestPlanRolePK());
                }
                for (UserTestProjectRole rc : user.getUserTestProjectRoleList()) {
                    new UserTestProjectRoleJpaController( DataBaseManager.getEntityManagerFactory()).destroy(rc.getUserTestProjectRolePK());
                }
                for (VmException rc : user.getVmExceptionList()) {
                    new VmExceptionJpaController( DataBaseManager.getEntityManagerFactory()).destroy(rc.getVmExceptionPK());
                }
                parameters.clear();
                parameters.put("id", user.getId());
                user = (VmUser) DataBaseManager.namedQuery("VmUser.findById", parameters).get(0);
                new VmUserJpaController( DataBaseManager.getEntityManagerFactory()).destroy(user.getId());
            } catch (NonexistentEntityException ex) {
                Logger.getLogger(VMUserServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalOrphanException ex) {
                Logger.getLogger(VMUserServer.class.getName()).log(Level.SEVERE, null, ex);
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
    public static boolean validCredentials(String username, String password, boolean encrypt) {
        try {
            parameters.clear();
            parameters.put("username", username);
            parameters.put("password", encrypt ? MD5.encrypt(password.replaceAll("'", "\\\\'")) : password);
            return !DataBaseManager.createdQuery("SELECT x FROM VmUser x "
                    + "WHERE x.username = :username and x.password = :password", parameters).isEmpty();
        } catch (VMException e) {
            Logger.getLogger(VMUserServer.class.getName()).log(Level.SEVERE, null, e);
            return false;
        } catch (Exception ex) {
            Logger.getLogger(VMUserServer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
