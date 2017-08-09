/*
 * Copyright 2017 Javier A. Ortiz Bultron javier.ortiz.78@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import static com.validation.manager.core.DataBaseManager.createdQuery;
import static com.validation.manager.core.DataBaseManager.getEntityManager;
import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.Notification;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestCaseExecution;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.ExecutionStepJpaController;
import com.validation.manager.core.db.controller.UserStatusJpaController;
import com.validation.manager.core.db.controller.VmUserJpaController;
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
import java.util.logging.Logger;
import javax.persistence.EntityTransaction;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class VMUserServer extends VmUser implements EntityServer<VmUser> {

    private static final long serialVersionUID = 1L;
    private boolean hashPassword = true;
    private boolean increaseAttempts = false;
    private static final Logger LOG
            = Logger.getLogger(VMUserServer.class.getName());
    private static final String USERNAME = "username";
    private static final String PWD = "password";

    public VMUserServer(VmUser vmu) {
        super.setId(vmu.getId());
        if (getId() != null) {
            update();
        }
        //previously hashing the already hashed password
        setHashPassword(false);
    }

    //create user object and login
    public VMUserServer(String attrUN, String attrUPW) throws VMException {
        super();
        try {
            PARAMETERS.clear();
            PARAMETERS.put(USERNAME, attrUN);
            PARAMETERS.put(PWD, encrypt(attrUPW));
            List<Object> result = createdQuery(
                    "SELECT u FROM VmUser u WHERE u.username= :username"// NOI18N
                    + " AND u.password= :password AND u.userStatusId.id <> 2",
                    PARAMETERS);
            //throw exception if no result found
            if (result.isEmpty()) {
                //The username is valid but wrong password. Increase the login attempts.
                throw new VMException("general.login.invalid.message");
            } else {
                VmUser vmu = (VmUser) result.get(0);
                update(VMUserServer.this, vmu);
                //previously hashing the already hashed password
                setHashPassword(false);
                int status = vmu.getUserStatusId().getId();
                if (status != 2) {
                    Calendar cal2 = getInstance(),
                            now = getInstance();
                    History last = getHistoryList().get(getHistoryList().size() - 1);
                    cal2.setTime(last.getModificationTime());
                    long diffMillis = now.getTimeInMillis()
                            - cal2.getTimeInMillis();
                    long diffDays = diffMillis / (24 * 60 * 60 * 1_000);
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
        catch (VMException e) {
            EntityTransaction transaction
                    = getEntityManager().getTransaction();
            if (transaction.isActive()) {
                transaction.rollback();
            }
            List<Object> result = createdQuery("SELECT u FROM VmUser u WHERE u.username='"
                    + attrUN + "' AND u.userStatusId.id <> 2");
            //increase number of attempts
            if (!result.isEmpty()) {
                VMUserServer vmu = new VMUserServer((VmUser) result.get(0));
                //Don't rehash the pasword!
                vmu.setHashPassword(false);
                vmu.setIncreaseAttempts(true);//Increase attempts after a unsuccessfull login.
                vmu.write2DB();
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
        setHashPassword(true);
    }

    private void prepareToPersist() {
        if (getUserStatusId() == null) {
            setUserStatusId(new UserStatusJpaController(
                    getEntityManagerFactory())
                    .findUserStatus(1));
        }
        if (getUserStatusId().getId() == 4) {
            //Changed from aged out to password changed. Clear status
            setUserStatusId(new UserStatusJpaController(
                    getEntityManagerFactory())
                    .findUserStatus(1));
            setAttempts(0);
            setReason("audit.user.account.aged");
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
                            getEntityManagerFactory()).findUserStatus(4));
        }
    }

    //write to db
    @Override
    public int write2DB() throws VMException {
        VmUserJpaController controller
                = new VmUserJpaController(getEntityManagerFactory());
        if (getId() != null && getId() > 0) {
            prepareToPersist();
            try {
                if (getModifierId() == 0) {
                    setModifierId(getEntity().getId());
                }
                //Sometimes password got re-hashed
                String password;
                if (isHashPassword()) {
                    password = encrypt(getPassword().replaceAll("'", "\\\\'"));
                    setHashPassword(false);
                } else {
                    password = getPassword().replaceAll("'", "\\\\'");
                }
                VmUser vmu = getEntity();
                update(vmu, this);
                vmu.setPassword(password);
                vmu.setReason(getReason() == null
                        ? "audit.general.modified" : getReason());
                vmu.setModificationTime(new Date());
                vmu.updateHistory();
                controller.edit(vmu);
                update();
            }
            catch (NonexistentEntityException ex) {
                throw new VMException(ex);
            }
            catch (Exception ex) {
                throw new VMException(ex);
            }
        } else {
            try {
                prepareToPersist();
                String password;
                if (isHashPassword()) {
                    password = encrypt(getPassword().replaceAll("'", "\\\\'"));
                    setHashPassword(false);
                } else {
                    password = getPassword().replaceAll("'", "\\\\'");
                }
                VmUser vmu = new VmUser(
                        getUsername().replaceAll("'", "\\\\'"), password,
                        getEmail().replaceAll("'", "\\\\'"),
                        getFirstName().replaceAll("'", "\\\\'"),
                        getLastName().replaceAll("'", "\\\\'"), getLocale(),
                        new UserStatusJpaController(
                                getEntityManagerFactory())
                                .findUserStatus(1), getAttempts());
                vmu.updateHistory();
                controller.create(vmu);
                setId(vmu.getId());
            }
            catch (Exception ex) {
                throw new VMException(ex);
            }
        }
        setReason("");
        update();
        return getId();
    }

    //Create complete list of users
    public static ArrayList<VMUserServer> getVMUsers() {
        ArrayList<VMUserServer> coreUsers = new ArrayList<>();
        try {
            List<Object> result = createdQuery(
                    "Select x from VmUser x order by x.username");
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
            PARAMETERS.clear();
            PARAMETERS.put(USERNAME, getUsername());
            PARAMETERS.put(PWD, hash
                    ? encrypt(newPass.replaceAll("'", "\\\\'")) : newPass);
            List<Object> result = createdQuery("SELECT x FROM VmUser x "
                    + "WHERE x.username = :username and x.password = :password",
                    PARAMETERS);
            if (result.size() > 0) {
                //Can't change to same password.
                passwordIsUsable = false;
            } else {
                //Here we'll catch if the password have been used in the
                //unusable period (use id in case the username was modified)
                VMUserServer user = new VMUserServer(getId());
                for (History u : user.getHistoryList()) {
                    //Now check the aging
                    long diff = currentTimeMillis()
                            - u.getModificationTime().getTime();
                    if (diff / (1_000 * 60 * 60 * 24)
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

    /**
     * Check user credentials.
     *
     * The password is already encrypted. Usually queries from within the server
     * itself
     *
     * @param username User name
     * @param password Password
     * @param encrypt Password needs encrypting?
     * @return true if valid
     */
    protected static boolean validCredentials(String username,
            String password, boolean encrypt) {
        try {
            PARAMETERS.clear();
            PARAMETERS.put(USERNAME, username);
            PARAMETERS.put(PWD, encrypt
                    ? encrypt(password.replaceAll("'", "\\\\'")) : password);
            return !createdQuery("SELECT x FROM VmUser x "
                    + "WHERE x.username = :username and x.password = :password",
                    PARAMETERS).isEmpty();
        }
        catch (VMException e) {
            LOG.log(Level.SEVERE, null, e);
            return false;
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static VmUser getUser(String username,
            String password, boolean encrypt) {
        try {
            PARAMETERS.clear();
            PARAMETERS.put(USERNAME, username);
            PARAMETERS.put(PWD, encrypt
                    ? encrypt(password.replaceAll("'", "\\\\'")) : password);
            if (validCredentials(username, password, encrypt)) {
                return (VmUser) createdQuery("SELECT x FROM VmUser x "
                        + "WHERE x.username = :username and x.password = :password",
                        PARAMETERS).get(0);
            } else {
                return null;
            }
        }
        catch (VMException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return null;

        }
    }

    @Override
    public VmUser getEntity() {
        return new VmUserJpaController(getEntityManagerFactory())
                .findVmUser(getId());
    }

    @Override
    public void update(VmUser target, VmUser source) {
        target.setPassword(source.getPassword());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setLocale(source.getLocale());
        target.setEmail(source.getEmail());
        target.setAttempts(source.getAttempts());
        target.setUserStatusId(source.getUserStatusId());
        target.setRoleList(source.getRoleList());
        target.setCorrectiveActionList(source.getCorrectiveActionList());
        target.setUserAssigmentList(source.getUserAssigmentList());
        target.setUserAssigmentList1(source.getUserAssigmentList1());
        target.setUserHasInvestigationList(source.getUserHasInvestigationList());
        target.setUsername(source.getUsername());
        target.setRootCauseList(source.getRootCauseList());
        target.setUserTestPlanRoleList(source.getUserTestPlanRoleList());
        target.setUserTestProjectRoleList(source.getUserTestProjectRoleList());
        target.setId(source.getId());
        target.setExecutionStepList(source.getExecutionStepList());
        target.setExecutionStepList1(source.getExecutionStepList1());
        target.setExecutionStepList(source.getExecutionStepList());
        target.setExecutionStepList1(source.getExecutionStepList1());
        target.setExecutionStepHasIssueList(source.getExecutionStepHasIssueList());
        target.setNotificationList(source.getNotificationList());
        target.setNotificationList1(source.getNotificationList1());
        target.setHistoryModificationList(source.getHistoryModificationList());
        target.setUserHasRoleList(source.getUserHasRoleList());
        target.setActivityList(source.getActivityList());
        target.setWorkflowInstanceHasTransitionList(source.getWorkflowInstanceHasTransitionList());
        target.setWorkflowInstanceList(source.getWorkflowInstanceList());
        super.update(target, source);
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
        catch (VMException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void setAsAssigner(ExecutionStep es) throws Exception {
        ExecutionStepServer ess = new ExecutionStepServer(es);
        ess.setAssigner(getEntity());
        ess.write2DB();
        update();
    }

    @Override
    public String toString() {
        return getFirstName() + " " + getLastName();
    }

    public List<Notification> getPendingNotifications() {
        update();
        List<Notification> pending = new ArrayList<>();
        getNotificationList().forEach(n -> {
            if (n.getAcknowledgeDate() == null) {
                pending.add(n);
            }
        });
        return pending;
    }
}
