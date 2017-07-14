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

import com.validation.manager.core.VMException;
import com.validation.manager.core.tool.MD5;
import java.util.Locale;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class UserPasswordConverterTest {

    /**
     * Test of convertToModel method, of class UserPasswordConverter.
     */
    @Test
    public void testConvertToModel() {
        System.out.println("convertToModel");
        String value = "test";
        Class<? extends String> targetType = null;
        Locale locale = null;
        UserPasswordConverter instance = new UserPasswordConverter();
        assertEquals(value, instance.convertToModel(value, targetType, locale));
    }

    /**
     * Test of convertToPresentation method, of class UserPasswordConverter.
     *
     * @throws com.validation.manager.core.VMException
     */
    @Test
    public void testConvertToPresentation() throws VMException {
        System.out.println("convertToPresentation");
        String value = "test";
        Class<? extends String> targetType = null;
        Locale locale = null;
        UserPasswordConverter instance = new UserPasswordConverter();
        String expResult = MD5.encrypt(value);
        String result = instance.convertToPresentation(value, targetType, locale);
        assertEquals(expResult, result);
    }
}
