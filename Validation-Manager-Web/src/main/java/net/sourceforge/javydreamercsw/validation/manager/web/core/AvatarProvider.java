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
package net.sourceforge.javydreamercsw.validation.manager.web.core;

import com.validation.manager.core.db.VmUser;

/**
 * Provides the avatar for an user. The avatar is handed over as a URL string
 * the UI renders with an {@link com.vaadin.flow.component.html.Image} (Flow
 * replacement for the v8 {@code com.vaadin.server.Resource}).
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public interface AvatarProvider {

    /**
     * Get avatar for user.
     *
     * @param user User to get avatar for
     * @param size Size in pixels (square)
     * @return Avatar for the user
     */
    String getAvatar(VmUser user, int size);

    /**
     * Get avatar for user.
     *
     * @param user User to get avatar for
     * @return Avatar for the user
     */
    String getAvatar(VmUser user);
}
