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
package com.validation.manager.core;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.ResourceBundle;
import static junit.framework.TestCase.*;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class VaadinUtilsTest {

    /**
     * Test of updateLocale method, of class VaadinUtils.
     */
    @Test
    public void testUpdateLocaleId() {
        try {
            System.out.println("updateLocaleId");
            String key = "demo.tab.message";
            VerticalLayout vl = new VerticalLayout();
            Label l = new Label();
            l.setId(key);
            vl.addComponent(l);
            ResourceBundle rb = ResourceBundle.getBundle(
                    "com.validation.manager.resources.VMMessages",
                    new Locale("es"));
            VaadinUtils.updateLocale(vl, new Locale("es"), rb);
            assertEquals(new String(rb.getString(key)
                    .getBytes("ISO-8859-1"), "UTF-8"), l.getCaption());
        }
        catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
    }

    /**
     * Test of updateLocale method, of class VaadinUtils.
     */
    @Test
    public void testUpdateLocaleCaption() {
        try {
            System.out.println("updateLocaleCaption");
            ResourceBundle rb = ResourceBundle.getBundle(
                    "com.validation.manager.resources.VMMessages",
                    new Locale("es"));
            VerticalLayout vl = new VerticalLayout();
            String message = "This is a test: select.test.case and "
                    + "select.test.case.message";
            Label l = new Label(message);
            //Now test complex captions
            Button b = new Button(message);
            vl.addComponent(l);
            vl.addComponent(b);
            VaadinUtils.updateLocale(vl, new Locale("es"), rb);
            String expected = message.replaceAll("select.test.case.message",
                    new String(rb.getString("select.test.case.message")
                            .getBytes("ISO-8859-1"), "UTF-8"))
                    .replaceAll("select.test.case",
                            new String(rb.getString("select.test.case")
                                    .getBytes("ISO-8859-1"), "UTF-8"));
            assertEquals(expected, b.getCaption());
            assertEquals(expected, l.getValue());
        }
        catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
    }
}
