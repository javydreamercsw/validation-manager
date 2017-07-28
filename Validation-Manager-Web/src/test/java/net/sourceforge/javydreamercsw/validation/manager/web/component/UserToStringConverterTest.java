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
package net.sourceforge.javydreamercsw.validation.manager.web.component;

import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.server.core.VMUserServer;
import com.validation.manager.test.AbstractVMTestCase;
import java.util.Locale;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class UserToStringConverterTest extends AbstractVMTestCase {

    /**
     * Test of convertToModel method, of class UserToStringConverter.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testConvertToModel() throws Exception {
        System.out.println("convertToModel");
        VMUserServer user = new VMUserServer(1);
        String value = user.toString();
        Class<? extends VmUser> targetType = null;
        Locale locale = null;
        UserToStringConverter instance = new UserToStringConverter();
        VmUser r = instance.convertToModel(value, targetType, locale);
        assertEquals(1, (int) r.getId());
    }

    /**
     * Test of convertToPresentation method, of class UserToStringConverter.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testConvertToPresentation() throws Exception {
        System.out.println("convertToPresentation");
        VMUserServer user = new VMUserServer(1);
        Class<? extends String> targetType = null;
        Locale locale = null;
        UserToStringConverter instance = new UserToStringConverter();
        String expResult = user.toString();
        String r = instance.convertToPresentation(user.getEntity(), targetType, locale);
        assertEquals(expResult, r);
    }
}
