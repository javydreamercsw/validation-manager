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
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@WebService(serviceName = "VMWebService")
public class VMWebService {

    private Map<String, Object> parameters = new HashMap<>();

    /**
     * Web service operation
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
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
        return user;
    }

    /**
     * Web service operation
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
     */
    @WebMethod(operationName = "getRootProjects")
    public List<Project> getRootProjects(@WebParam(name = "user") final VmUser user)
            throws VMException {
        List<Project> projects = new ArrayList<>();
        if (isValidUser(user.getUsername(), user.getPassword()) != null) {
            ProjectJpaController controller
                    = new ProjectJpaController(DataBaseManager.getEntityManagerFactory());
            for (Project p : controller.findProjectEntities()) {
                if (p.getParentProjectId() == null) {
                    projects.add(p);
                }
            }
        }
        return projects;
    }

    /**
     * Web service operation
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
