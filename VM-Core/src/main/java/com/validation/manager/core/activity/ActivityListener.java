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
package com.validation.manager.core.activity;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.VmSetting;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.VmUserJpaController;
import com.validation.manager.core.history.Versionable;
import com.validation.manager.core.server.core.ActivityServer;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ActivityListener {

    private static final Logger LOG
            = Logger.getLogger(ActivityListener.class.getSimpleName());
    private final InternationalizationProvider TRANSLATOR
            = Lookup.getDefault().lookup(InternationalizationProvider.class);

    @PreUpdate
    void onPersist(Object entity) {
        if (entity instanceof Versionable) {
            if (!(entity instanceof VmUser)) {
                Versionable v = (Versionable) entity;
                try {
                    VmUser user = new VmUserJpaController(DataBaseManager
                            .getEntityManagerFactory())
                            .findVmUser(v.getModifierId() > 0
                                    ? v.getModifierId() : 1);
                    new ActivityServer(2, new Date(),
                            TRANSLATOR.translate("item.edit.desc")
                                    .replaceAll("%u", user.getFirstName()
                                            + " " + user.getLastName())
                                    .replaceAll("%i", getItemName(entity)),
                            user).write2DB();
                }
                catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    @PrePersist
    void onCreate(Object entity) {
        if (entity instanceof Versionable) {
            if (!(entity instanceof VmUser)) {
                Versionable v = (Versionable) entity;
                try {
                    VmUser user = new VmUserJpaController(DataBaseManager
                            .getEntityManagerFactory())
                            .findVmUser(v.getModifierId() > 0
                                    ? v.getModifierId() : 1);
                    new ActivityServer(1, new Date(),
                            TRANSLATOR.translate("item.creation.desc")
                                    .replaceAll("%u", user.getFirstName()
                                            + " " + user.getLastName())
                                    .replaceAll("%i", getItemName(entity)),
                            user).write2DB();
                }
                catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    private String getItemName(Object entity) {
        String value = null;
        switch (entity.getClass().getSimpleName()) {
            case "Requirement":
                Requirement r = (Requirement) entity;
                value = TRANSLATOR.translate("general.requirement")
                        + " " + r.getUniqueId();
                break;
            case "Project":
                Project p = (Project) entity;
                value = TRANSLATOR.translate("general.project")
                        + " " + p.getName();
                break;
            case "Step":
                Step s = (Step) entity;
                value = TRANSLATOR.translate("general.step")
                        + " " + s.getTestCase().getName() + " "
                        + TRANSLATOR.translate("general.step") + ": "
                        + s.getStepSequence();
                break;
            case "VmSetting":
                VmSetting se = (VmSetting) entity;
                value = TRANSLATOR.translate("general.setting")
                        + " " + se.getSetting();
                break;
            default:
                LOG.log(Level.WARNING, "Unhandled case: {0}",
                        entity.getClass().getName());
        }
        return value;
    }
}
