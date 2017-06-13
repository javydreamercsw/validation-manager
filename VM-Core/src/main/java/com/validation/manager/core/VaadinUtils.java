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

import com.vaadin.data.Property;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.UI;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@code VaadinUtils} offers some utility functions for the
 * <a href="https://vaadin.com">Vaadin UI Framework</a>.
 *
 * @author <a href="mailto:javier.ortiz.78@gmail.com">Javier Ortiz Bultron</a>
 * Based on code from: <a href="mailto:rmuller@xiam.nl">Ronald K. Muller</a>
 * https://gist.github.com/rmuller/10980706
 */
public final class VaadinUtils {

    private static final Logger LOG
            = Logger.getLogger(VaadinUtils.class.getSimpleName());

    // Utility class
    private VaadinUtils() {
    }

    /**
     * Change all {@code Locale} dependant properties of the
     * {@code com.vaadin.ui.Component}s within of the given component container
     * (typically an {@link UI} or other top level layout component). If the
     * specified {@code Locale} is the same as the current {@code Locale} of the
     * component container, this method does nothing. Otherwise it'll go thru
     * the components searching for it's component id. If it is in the resource
     * bundle, it'll set it's caption to the right translated string.
     *
     * <p>
     * To use this method, do something like:
     * <pre>
     * public class MyUI extends UI {
     *
     *     {@literal @}Override
     *     public void init(final VaadinRequest request) {
     *         // ... usual code
     *         // somewhere in the UI the user can change the "Form Locale". This code must
     *         // call myUI#setLocale(newLocale);
     *     }
     *
     *     // ...
     *
     * }
     *
     *      String key = "demo.tab.message";
     *      VerticalLayout vl = new VerticalLayout();
     *      Button b = new Button(key);
     *      vl.addComponent(b);
     *      ResourceBundle rb = ResourceBundle.getBundle(
     *              "resource.bundle",
     *              new Locale("es"));
     *      VaadinUtils.updateLocale(vl, new Locale("es"), rb);
     *
     *      It also works with components implementing Property:
     *
     *      VerticalLayout vl = new VerticalLayout();
     *      Label l = new Label(key);
     *      vl.addComponent(l);
     *      ResourceBundle rb = ResourceBundle.getBundle(
     *              "resource.bundle",
     *              new Locale("es"));
     *      VaadinUtils.updateLocale(vl, new Locale("es"), rb);
     * </pre>
     *
     * @param ui The component container for which the {@code Locale} dependent
     * component properties must be changed, never {@code null}
     * @param locale The new {@code Locale}, never {@code null}
     * @param rb The {@code ResourceBundle} for the specified {@code Locale},
     * never {@code null}
     */
    public static void updateLocale(final HasComponents ui, final Locale locale,
            final ResourceBundle rb) {

        // locale may not be null, however the current UI Locale may be null!
        if (locale.equals(ui.getLocale())) {
            return;
        }
        final long time = System.currentTimeMillis();
        walkComponentTree(ui, (Component c) -> {
            String id = c.getId();
            String caption;
            if (c instanceof Property
                    && ((Property) c).getValue() instanceof String) {
                caption = (String) ((Property) c).getValue();
            } else {
                caption = c.getCaption();
            }
            if (id != null && !id.trim().isEmpty()) {
                if (rb.containsKey(id)) {
                    try {
                        c.setCaption(new String(rb.getString(id)
                                .getBytes("ISO-8859-1"), "UTF-8"));
                    }
                    catch (UnsupportedEncodingException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }
            } else if (caption != null && !caption.trim().isEmpty()) {
                /**
                 * This is a more complex scenario where the caption uses more
                 * than one key for translation. Sort the keys in reverse so
                 * substitutions are correct.
                 */
                final SortedSet<String> ss = new TreeSet<>(Collections.reverseOrder());
                for (Enumeration<String> e = rb.getKeys(); e.hasMoreElements();) {
                    try {
                        String key = e.nextElement();
                        ss.add(key);
                        caption = caption.replaceAll(key, new String(rb.getString(key)
                                .getBytes("ISO-8859-1"), "UTF-8"));
                    }
                    catch (UnsupportedEncodingException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }
                if (c instanceof Property) {
                    ((Property) c).setValue(caption);
                } else {
                    c.setCaption(caption);
                }
            }
        });
        LOG.log(Level.FINE, "Locale updated: {0} -> {1} in {2} ms.",
                new Object[]{ui.getLocale(), locale,
                    System.currentTimeMillis() - time});
    }

    // private
    // if switching to Java 8, remove this and replace by java.util.funtion.Consumer
    // (and make to code more functional)
    interface Consumer<T> {

        void accept(T t);
    }

    // recursively walk the Component true
    private static void walkComponentTree(Component c, Consumer<Component> visitor) {
        visitor.accept(c);
        if (c instanceof HasComponents) {
            for (Component child : ((HasComponents) c)) {
                walkComponentTree(child, visitor);
            }
        }
    }
}
