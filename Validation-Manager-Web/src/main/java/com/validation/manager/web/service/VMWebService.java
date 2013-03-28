package com.validation.manager.web.service;

import com.validation.manager.core.server.core.VMUserServer;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@WebService(serviceName = "VMWebService")
public class VMWebService {

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "login")
    public boolean hello(@WebParam(name = "username") String user,
            @WebParam(name = "pass") String pass) {
        return VMUserServer.validCredentials(user, pass, true);
    }
}
