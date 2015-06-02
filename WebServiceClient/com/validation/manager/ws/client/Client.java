/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class Client {

    public static void main(String[] args) {
        try { // Call Web Service Operation
            net.sourceforge.javydreamercsw.validation.manager.web.service.VMWebService_Service service = new net.sourceforge.javydreamercsw.validation.manager.web.service.VMWebService_Service();
            net.sourceforge.javydreamercsw.validation.manager.web.service.VMWebService port = service.getVMWebServicePort();
            // TODO initialize WS operation arguments here
            java.lang.String user = "test";
            java.lang.String pass = "test";
            // TODO process result here
            net.sourceforge.javydreamercsw.validation.manager.web.service.VmUser result = port.getVMUser(user, pass);
            System.out.println("Result = " + result);
        } catch (Exception ex) {
    // TODO handle custom exceptions here
        }
    }
}
