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
package net.sourceforge.javydreamercsw.validation.manager.web.service;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.tool.MD5;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@WebService(serviceName = "VMWebService")
public class VMWebService {

    private final Map<String, Object> parameters = new HashMap<>();
    private static final Logger LOG
            = Logger.getLogger(VMWebService.class.getName());

    /**
     * Web service operation
     *
     * @param username Username
     * @param password Password
     * @return User with provided credentials or null if invalid.
     * @throws com.validation.manager.core.VMException
     */
    @WebMethod(operationName = "getVMUser")
    public VmUser getVMUser(@WebParam(name = "user") final String username,
            @WebParam(name = "pass") final String password) throws VMException {
        VmUser user = isValidUser(username, password);
        if (user == null) {
            throw new VMException("Invalid username and/or password!");
        }
        return user;
    }

    private VmUser isValidUser(String username, String password) {
        VmUser user = null;
        parameters.clear();
        parameters.put("username", username);
        List<Object> result
                = DataBaseManager.namedQuery("VmUser.findByUsername",
                        parameters);
        if (result != null) {
            for (Object obj : result) {
                if (obj instanceof VmUser) {
                    try {
                        VmUser vmuser = (VmUser) obj;
                        if (vmuser.getPassword().equals(MD5.encrypt(password))) {
                            user = vmuser;
                        }
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return user;
    }

    /**
     * Web service operation
     *
     * @param project Project to retrieve
     * @param user User requesting the information.
     * @return Update project.
     * @throws com.validation.manager.core.VMException
     */
    @WebMethod(operationName = "getProject")
    public Project getProject(@WebParam(name = "project") final Project project,
            @WebParam(name = "user") final VmUser user) throws VMException {
        Project p = null;
        if (isValidUser(user.getUsername(), user.getPassword()) != null) {
            p = new ProjectServer(project).getEntity();
        }
        return p;
    }

    /**
     * Web service operation
     *
     * @param user User requesting the information.
     * @return List of root projects.
     * @throws com.validation.manager.core.VMException
     */
    @WebMethod(operationName = "getRootProjects")
    public List<Project> getRootProjects(@WebParam(name = "user") final VmUser user)
            throws VMException {
        List<Project> projects = new ArrayList<>();
        if (isValidUser(user.getUsername(), user.getPassword()) != null) {
            ProjectJpaController controller
                    = new ProjectJpaController(DataBaseManager
                            .getEntityManagerFactory());
            controller.findProjectEntities().stream().filter((p)
                    -> (p.getParentProjectId() == null)).forEachOrdered((p) -> {
                projects.add(p);
            });
        }
        return projects;
    }

    /**
     * Web service operation
     *
     * @param newProject Project to save.
     * @param user User requesting the information.
     * @return true if successful, false otherwise.
     * @throws com.validation.manager.core.VMException
     */
    @WebMethod(operationName = "saveProject")
    public boolean saveProject(@WebParam(name = "newProject") final Project newProject,
            @WebParam(name = "user") final VmUser user) throws VMException {
        boolean result = false;
        if (isValidUser(user.getUsername(), user.getPassword()) != null) {
            ProjectServer ps = new ProjectServer(newProject);
            ps.copy(newProject);
            result = true;
        }
        return result;
    }
}
