package com.validation.manager.core;

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
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class VaadinUtilsTest {

    /**
     * Test of updateLocale method, of class VaadinUtils.
     */
    @Test
    public void testUpdateLocale() {
        try {
            System.out.println("updateLocale");
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
}
