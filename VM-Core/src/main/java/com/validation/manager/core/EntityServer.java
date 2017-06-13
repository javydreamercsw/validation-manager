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

import java.util.HashMap;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 * @param <T> Entity
 */
public interface EntityServer<T> {

    static HashMap PARAMETERS = new HashMap();

    /**
     * Write Entity to database
     *
     * @return Id for entity (new if just created)
     * @throws Exception if there was an issue updating the database.
     */
    public int write2DB() throws Exception;

    /**
     * Gets the entity represented by this EntityServer. Easy method to access
     * underlying entity for use with controllers and such.
     *
     * @return The entity.
     */
    public T getEntity();

    /**
     * Update the target with the source object.
     *
     * @param target object to update.
     * @param source object to update from.
     */
    public void update(T target, T source);

    /**
     * Update the enclosed entity.
     *
     */
    public void update();
}
