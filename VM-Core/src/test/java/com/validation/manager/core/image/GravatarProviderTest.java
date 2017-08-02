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
package com.validation.manager.core.image;

import com.vaadin.server.Resource;
import static junit.framework.TestCase.*;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class GravatarProviderTest {

    /**
     * Test of getIcon method, of class GravatarProvider.
     */
    @Test
    public void testGetIcon() {
        System.out.println("getIcon");
        String email = "javier.ortiz.78@gmail.com";
        GravatarProvider instance = new GravatarProvider();
        Resource result = instance.getIcon(email, 0);
        assertNotNull(result);
        result = instance.getIcon(email, 200);
        assertNotNull(result);
    }
}
