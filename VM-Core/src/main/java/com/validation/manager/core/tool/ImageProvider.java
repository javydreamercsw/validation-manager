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
package com.validation.manager.core.tool;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 * @param <T> Class
 */
public interface ImageProvider<T> {

    /**
     * Provide icons for entities.
     *
     * @param e Entity to get the icon for.
     * @param type Icon type
     * @return Icon or null if none found.
     * @throws java.io.IOException If there's an error retrieving the icon.
     */
    public BufferedImage getIcon(T e, int type) throws IOException;

    /**
     * Is this entity supported
     *
     * @param e Object to get the icon for.
     * @return true if supported
     */
    public boolean supported(Object e);
}
