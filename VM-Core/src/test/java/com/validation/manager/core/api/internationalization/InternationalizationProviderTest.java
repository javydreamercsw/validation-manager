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
package com.validation.manager.core.api.internationalization;

import java.util.Locale;
import static junit.framework.TestCase.*;
import org.junit.Test;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class InternationalizationProviderTest {

    /**
     * Test of translate method, of class InternationalizationProvider.
     */
    @Test
    public void testTranslate_String() {
        System.out.println("translate");
        String mess = "general.active";
        InternationalizationProvider instance
                = Lookup.getDefault().lookup(InternationalizationProvider.class);
        assertNotNull(instance.translate(mess));
    }

    /**
     * Test of translate method, of class InternationalizationProvider.
     */
    @Test
    public void testTranslate_String_Locale() {
        System.out.println("translate");
        String mess = "general.active";
        Locale l = new Locale("es");
        InternationalizationProvider instance
                = Lookup.getDefault().lookup(InternationalizationProvider.class);
        assertNotNull(instance.translate(mess, l));
        assertNotSame(instance.translate(mess), instance.translate(mess, l));
    }

    /**
     * Test of getResourceBundle method, of class InternationalizationProvider.
     */
    @Test
    public void testGetResourceBundle() {
        System.out.println("getResourceBundle");
        InternationalizationProvider instance
                = Lookup.getDefault().lookup(InternationalizationProvider.class);
        assertNotNull(instance.getResourceBundle());
    }
}
