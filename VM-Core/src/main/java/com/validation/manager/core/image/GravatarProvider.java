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

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.validation.manager.core.VMException;
import com.validation.manager.core.api.image.AvatarProvider;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.tool.MD5;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = AvatarProvider.class)
public class GravatarProvider implements AvatarProvider {

    private static final Logger LOG
            = Logger.getLogger(GravatarProvider.class.getSimpleName());

    @Override
    public Resource getAvatar(VmUser user, int size) {
        return getIcon(user.getEmail(), size);
    }

    @Override
    public Resource getAvatar(VmUser user) {
        return getAvatar(user, 0);
    }

    protected Resource getIcon(String email, int size) {
        try {
            //Calculate Gravatar email hash
            /**
             * All URLs on Gravatar are based on the use of the hashed value of
             * an email address. Images and profiles are both accessed via the
             * hash of an email, and it is considered the primary way of
             * identifying an identity within the system. To ensure a consistent
             * and accurate hash, the following steps should be taken to create
             * a hash:
             *
             * Trim leading and trailing whitespace from an email address
             *
             * Force all characters to lower-case
             *
             * md5 hash the final string
             */
            URL url = getURL(email, size);
            if (url != null) {
                LOG.log(Level.FINE, "Retrieving icon from: {0}", url);
                return new ExternalResource(url);
            }
        }
        catch (VMException | IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private URL getURL(String email, int size) throws MalformedURLException, VMException {
        if (email == null || email.trim().isEmpty()) {
            return null;
        } else {
            return new URL("https://www.gravatar.com/avatar/"
                    + MD5.encrypt(email.trim().toLowerCase())
                    + (size > 0 ? "?s=" + size : ""));
        }
    }
}
