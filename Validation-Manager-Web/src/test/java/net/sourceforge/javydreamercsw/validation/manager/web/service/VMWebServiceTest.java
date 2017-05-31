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

import javax.xml.ws.Endpoint;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class VMWebServiceTest {
    
    @BeforeClass
    public static void setUpClass() {
        Endpoint.publish("http://localhost:8090/valitation-manager-test",
                new VMWebService());
    }

    /**
     * Test of login method, of class VMWebService.
     */
    @Test
    public void testLogin() {
//        try {
//            System.out.println("login");
//            String password = "test";
//            String username = "test";
//            VMWebService instance = new VMWebService();
//            VmUser expResult = null;
//            VmUser result = instance.getVMUser(password, username);
//            assertEquals(expResult, result);
//            SoapUITestCaseRunner runner = new SoapUITestCaseRunner();
//            runner.setProjectFile("/Users/tsu/Dropbox/projects/tsu/blog/soapUI-junit-maven-cucumber/example/test/src/test/soapUI/CarMaintenance-soapui-project.xml");
//            String[] properties = new String[2];
//            properties[0] = "addedFuel=42";
//            properties[1] = "expectedFuel=42";
//            runner.setProjectProperties(properties);
//            runner.run();
//        } catch (Exception ex) {
//            Exceptions.printStackTrace(ex);
//        }
    }
}
