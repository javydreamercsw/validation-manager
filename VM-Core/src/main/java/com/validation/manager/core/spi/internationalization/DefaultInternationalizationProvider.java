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
package com.validation.manager.core.spi.internationalization;

import com.vaadin.ui.UI;
import com.validation.manager.core.VMUI;
import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import com.validation.manager.core.server.core.VMUserServer;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = InternationalizationProvider.class, position = 1)
public class DefaultInternationalizationProvider
        implements InternationalizationProvider {

    private static final Logger LOG
            = Logger.getLogger(DefaultInternationalizationProvider.class
                    .getSimpleName());

    @Override
    public String translate(String mess) {
        VMUI ui = (VMUI) UI.getCurrent();
        VMUserServer user = ui == null ? null : ui.getUser();
        return translate(mess, user == null ? Locale.getDefault()
                : new Locale(user.getLocale()));
    }

    @Override
    public ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle(
                "com.validation.manager.resources.VMMessages");
    }

    @Override
    public String translate(String mess, Locale locale) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(
                    "com.validation.manager.resources.VMMessages", locale);
            if (bundle.containsKey(mess)) {
                return bundle.containsKey(mess)
                        ? new String(bundle.getString(mess)
                                .getBytes("ISO-8859-1"), "UTF-8") : mess;
            } else {
                //Try to search on any other provider
                for (InternationalizationProvider i
                        : Lookup.getDefault()
                                .lookupAll(InternationalizationProvider.class)) {
                    if (!(i instanceof DefaultInternationalizationProvider)
                            && i.getResourceBundle().containsKey(mess)) {
                        return i.translate(mess);
                    }
                }
            }
        }
        catch (UnsupportedEncodingException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return mess;
    }
}
