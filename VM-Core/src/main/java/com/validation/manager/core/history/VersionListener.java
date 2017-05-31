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
package com.validation.manager.core.history;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.History;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PostLoad;

/**
 *
 * @author Javier Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class VersionListener {

    private final static Logger LOG
            = Logger.getLogger(VersionListener.class.getSimpleName());

    @PostLoad
    public synchronized void onLoad(Object entity) {
        if (entity instanceof Versionable
                && DataBaseManager.isVersioningEnabled()) {
            try {
                //Load the audit values from last time
                Versionable v = (Versionable) entity;
                if (!v.getHistoryList().isEmpty()) {
                    History h = v.getHistoryList().get(v.getHistoryList()
                            .size() - 1);
                    v.setMajorVersion(h.getMajorVersion());
                    v.setMidVersion(h.getMidVersion());
                    v.setMinorVersion(h.getMinorVersion());
                } else {
                    v.setMajorVersion(0);
                    v.setMidVersion(0);
                    v.setMinorVersion(1);
                }
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
    }
}
