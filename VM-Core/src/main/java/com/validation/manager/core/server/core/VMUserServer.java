package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import static com.validation.manager.core.DataBaseManager.createdQuery;
import static com.validation.manager.core.DataBaseManager.getEntityManager;
import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import static com.validation.manager.core.DataBaseManager.namedQuery;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.CorrectiveAction;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.Role;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestCaseExecution;
import com.validation.manager.core.db.UserAssigment;
import com.validation.manager.core.db.UserHasInvestigation;
import com.validation.manager.core.db.UserModifiedRecord;
import com.validation.manager.core.db.UserTestPlanRole;
import com.validation.manager.core.db.UserTestProjectRole;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.CorrectiveActionJpaController;
import com.validation.manager.core.db.controller.ExecutionStepJpaController;
import com.validation.manager.core.db.controller.RoleJpaController;
import com.validation.manager.core.db.controller.UserAssigmentJpaController;
import com.validation.manager.core.db.controller.UserHasInvestigationJpaController;
import com.validation.manager.core.db.controller.UserModifiedRecordJpaController;
import com.validation.manager.core.db.controller.UserStatusJpaController;
import com.validation.manager.core.db.controller.UserTestPlanRoleJpaController;
import com.validation.manager.core.db.controller.UserTestProjectRoleJpaController;
import com.validation.manager.core.db.controller.VmUserJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import static com.validation.manager.core.server.core.VMSettingServer.getSetting;
import static com.validation.manager.core.tool.MD5.encrypt;
import static java.lang.System.currentTimeMillis;
import java.util.ArrayList;
import java.util.Calendar;
import static java.util.Calendar.getInstance;
import java.util.Date;
import java.util.List;
import static java.util.Locale.getDefault;
import java.util.logging.Level;
import static java.util.logging.Logger.getLogger;
import javax.persistence.EntityTransaction;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class VMUserServer extends VmUser implements EntityServer<VmUser> {

    private static final long serialVersionUID = 1L;
    private boolean hashPassword = true;
    private boolean increaseAttempts = false;
    private boolean change;

    public VMUserServer(VmUser vmu) {
        super.setId(vmu.getId());
        update();
        //previously hashing the already hashed password
        setHashPassword(false);
    }

    //create user object and login
    public VMUserServer(String attrUN, String attrUPW) throws Exception {
        super();
        try {
            List<Object> result = createdQuery(
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
                setHashPassword(false);
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
        }
        catch (Exception e) {
            EntityTransaction transaction
                    = getEntityManager().getTransaction();
            if (transaction.isActive()) {
                transaction.rollback();
            }
            List<Object> result = createdQuery("SELECT u FROM VmUser u WHERE u.username='"
                    + attrUN + "' AND u.userStatusId.id <> 2");
            //increase number of attempts
            if (!result.isEmpty()) {
                VmUser vmu = (VmUser) result.get(0);
                update(VMUserServer.this, vmu);
                //Don't rehash the pasword!
                setHashPassword(false); //Increase attempts after a unsuccessfull login.
                setIncreaseAttempts(true);
                setLastModified(vmu.getLastModified());
                setChange(false);
                write2DB();
            }
        }
    }

//create user object for data structures
    public VMUserServer(int id) throws Exception {
        super.setId(id);
        update();
        //previously hashing the already hashed password
        setHashPassword(false);
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
//            setReason("audit.user.account.aged");
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
//                setModifierId(getEntity());
                date = new Date();
                setLastModified(date);
                setChange(false);
            } else {
                date = getLastModified();
            }
            setLastModified(date);
            //Sometimes password got re-hashed
            String password;
            if (isHashPassword()) {
                password = encrypt(getPassword().replaceAll("'", "\\\\'"));
            } else {
                password = getPassword().replaceAll("'", "\\\\'");
            }
            VmUser vmu = controller.findVmUser(getId());
            update(vmu, this);
            vmu.setPassword(password);
//            vmu.setReason(getReason() == null
//                    ? "audit.general.modified" : getReason());
//            vmu.setModificationTime(new Date());
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
//        setReason("");
        return getId();
    }

//create complete list of users
    public static ArrayList<VMUserServer> getVMUsers() {
        ArrayList<VMUserServer> coreUsers = new ArrayList<>();
        try {
            List<Object> result = createdQuery(
                    "Select x from VMUser x order by x.username");
            result.forEach((o) -> {
                coreUsers.add(new VMUserServer((VmUser) o));
            });
        }
        catch (Exception e) {
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
        boolean passwordIsUsable = true;
        try {
            //Now check if password is not the same as the current password
            List<Object> result = createdQuery(
                    "Select x from VmUser x where x.id=" + getId()
                    + " and x.password='"
                    + (hash ? encrypt(newPass) : newPass) + "'");
            if (result.size() > 0) {
                passwordIsUsable = false;
            } else {
                //Here we'll catch if the password have been used in the
                //unusable period (use id in case the username was modified)
                VMUserServer user = new VMUserServer(getId());
                for (History u : user.getHistoryModificationList()) {
                    //Now check the aging
                    long diff = currentTimeMillis()
                            - u.getModificationTime().getTime();
                    if (diff / (1000 * 60 * 60 * 24)
                            > getSetting("password.unusable_period")
                                    .getIntVal()) {
                        passwordIsUsable = false;
                    }
                }
            }
            //---------------------------
        }
        catch (Exception ex) {
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
            VmUser temp = (VmUser) namedQuery("VmUser.findById",
                    parameters).get(0);
            try {
                for (CorrectiveAction ca : temp.getCorrectiveActionList()) {
                    new CorrectiveActionJpaController(
                            getEntityManagerFactory()).destroy(ca.getId());
                }
                for (Role r : temp.getRoleList()) {
                    new RoleJpaController(
                            getEntityManagerFactory()).destroy(r.getId());
                }
                for (UserAssigment ua : temp.getUserAssigmentList()) {
                    new UserAssigmentJpaController(
                            getEntityManagerFactory()).destroy(ua.getUserAssigmentPK());
                }
                for (UserAssigment ua : temp.getUserAssigmentList1()) {
                    new UserAssigmentJpaController(
                            getEntityManagerFactory()).destroy(ua.getUserAssigmentPK());
                }
                for (UserHasInvestigation i : temp.getUserHasInvestigationList()) {
                    new UserHasInvestigationJpaController(
                            getEntityManagerFactory()).destroy(i.getUserHasInvestigationPK());
                }
                for (UserModifiedRecord rc : temp.getUserModifiedRecordList()) {
                    new UserModifiedRecordJpaController(
                            getEntityManagerFactory()).destroy(rc.getUserModifiedRecordPK());
                }
                for (UserTestPlanRole rc : temp.getUserTestPlanRoleList()) {
                    new UserTestPlanRoleJpaController(
                            getEntityManagerFactory()).destroy(rc.getUserTestPlanRolePK());
                }
                for (UserTestProjectRole rc : temp.getUserTestProjectRoleList()) {
                    new UserTestProjectRoleJpaController(
                            getEntityManagerFactory()).destroy(rc.getUserTestProjectRolePK());
                }
                parameters.clear();
                parameters.put("id", temp.getId());
                temp = (VmUser) namedQuery("VmUser.findById",
                        parameters).get(0);
                new VmUserJpaController(
                        getEntityManagerFactory()).destroy(temp.getId());
            }
            catch (NonexistentEntityException | IllegalOrphanException ex) {
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
        }
        catch (VMException e) {
            getLogger(VMUserServer.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        catch (Exception ex) {
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
        }
        catch (Exception ex) {
            getLogger(VMUserServer.class.getName()).log(Level.SEVERE, null, ex);
            return null;

        }
    }

    @Override
    public VmUser getEntity() {
        return new VmUserJpaController(
                getEntityManagerFactory())
                .findVmUser(getId());
    }

    @Override
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
        target.setUserAssigmentList(source.getUserAssigmentList());
        target.setUserAssigmentList1(source.getUserAssigmentList1());
        target.setUserHasInvestigationList(source.getUserHasInvestigationList());
        target.setRootCauseList(source.getRootCauseList());
        target.setUserTestPlanRoleList(source.getUserTestPlanRoleList());
        target.setUserTestProjectRoleList(source.getUserTestProjectRoleList());
        target.setId(source.getId());
        if (target.getExecutionStepList() == null) {
            target.setExecutionStepList(new ArrayList<>());
        }
        if (source.getExecutionStepList() != null) {
            target.setExecutionStepList(source.getExecutionStepList());
        }
        if (target.getExecutionStepList1() == null) {
            target.setExecutionStepList1(new ArrayList<>());
        }
        if (source.getExecutionStepList1() != null) {
            target.setExecutionStepList1(source.getExecutionStepList1());
        }
        if (target.getExecutionStepHasIssueList() == null) {
            target.setExecutionStepHasIssueList(new ArrayList<>());
        }
        target.setExecutionStepList(source.getExecutionStepList());
        target.setExecutionStepList1(source.getExecutionStepList1());
        target.setExecutionStepHasIssueList(source.getExecutionStepHasIssueList());
//        super.update(target, source);
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    public void assignTestCase(TestCaseExecution tce, TestCase tc,
            VmUser assigner) {
        try {
            VMUserServer a = new VMUserServer(assigner);
            ExecutionStepJpaController c
                    = new ExecutionStepJpaController(DataBaseManager
                            .getEntityManagerFactory());
            tc.getStepList().forEach((s) -> {
                tce.getExecutionStepList().stream().filter((es)
                        -> (es.getStep().getStepPK().equals(s.getStepPK())))
                        .forEachOrdered((es) -> {
                            try {
                                es.setAssignedTime(new Date());
                                c.edit(es);
                                getExecutionStepList().add(es);
                                a.getExecutionStepList1().add(es);
                            }
                            catch (Exception ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        });
            });
            a.write2DB();
            write2DB();
        }
        catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void setAsAssigner(ExecutionStep es) throws Exception {
        ExecutionStepServer ess = new ExecutionStepServer(es);
        ess.setAssigner(getEntity());
        ess.write2DB();
        update();
    }
}
