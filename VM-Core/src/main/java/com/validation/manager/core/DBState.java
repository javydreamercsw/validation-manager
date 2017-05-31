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

import java.util.ResourceBundle;

/**
 * Enumeration to describe the database state
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public enum DBState {

    /*
     * Database is up to date
     */
    VALID("message.db.valid"),
    /*
     * Database needs a manual update
     */
    NEED_MANUAL_UPDATE("error.old.db"),
    /*
     * Database just updated
     */
    UPDATED("message.updated.db"),
    /*
     * Database needs to be updated
     */
    NEED_UPDATE("message.update.db"),
    /*
     * Database needs initialization
     */
    NEED_INIT("message.init.db"),
    /*
     * Error detected
     */
    ERROR("message.db.error"),
    /*
     * Start up
     */
    START_UP("message.db.startup"),
    /*
     * Updating
     */
    UPDATING("message.update.db");
    private final String mess;
    private static final ResourceBundle lrb = ResourceBundle.getBundle(
            "com.validation.manager.resources.VMMessages");

    DBState(String mess) {
        this.mess = mess;
    }

    /**
     * @return the mess
     */
    public String getMessage() {
        if (lrb.containsKey(mess)) {
            return lrb.getString(mess);
        } else {
            return mess;
        }
    }
}
