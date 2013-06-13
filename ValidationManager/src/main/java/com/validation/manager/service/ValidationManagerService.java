package com.validation.manager.service;

import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.VMUserServer;
import java.util.ArrayList;
import java.util.List;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@WebService(serviceName = "ValidationManagerService")
public class ValidationManagerService {

    /**
     * Login into the server.
     *
     * @param name username
     * @param password password
     * @return VmUser or null if unsuccessful
     */
    @WebMethod(operationName = "login")
    public VmUser login(@WebParam(name = "name") final String name,
            @WebParam(name = "password") final String password) {
        return VMUserServer.getUser(name, password, true);
    }

    /**
     * Get a list of projects.
     * @param user user requesting the information.
     * @param project Project to get children from (use null to get all projects)
     * @return List of projects. Empty if not a valid user or without proper permissions.
     */
    @WebMethod(operationName = "getProjects")
    public List<Project> getProjects(@WebParam(name = "user") final VmUser user,
            @WebParam(name = "project") final Project project) {
        //TODO: Restrict which projects to see based on permissions
        if (validUser(user)) {
            if (project == null) {
                return ProjectServer.getProjects();
            } else {
                return new ProjectServer(project).getChildren();
            }
        } else {
            return new ArrayList<Project>();
        }
    }

    private boolean validUser(VmUser user) {
        return VMUserServer.validCredentials(user.getUsername(), user.getPassword(), false);
    }
}
