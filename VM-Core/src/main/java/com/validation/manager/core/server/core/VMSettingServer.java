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
package com.validation.manager.core.server.core;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import static com.validation.manager.core.DataBaseManager.namedQuery;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.VmSetting;
import com.validation.manager.core.db.controller.VmSettingJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class VMSettingServer extends VmSetting
        implements EntityServer<VmSetting>, VersionableServer<VmSetting> {

    private static List<Object> result;

    public VMSettingServer(String setting) throws VMException {
        VmSetting s = getSetting(setting);
        if (s != null) {
            super.setId(s.getId());
            update();
        } else {
            throw new VMException("Setting: " + setting
                    + " doesn't exist!");
        }
    }

    public VMSettingServer(String setting, boolean boolVal, int intVal,
            long longVal, String stringVal) {
        super(setting);
        setBoolVal(boolVal);
        setIntVal(intVal);
        setLongVal("" + longVal);
        setSetting(setting);
        setStringVal(stringVal);
    }

    /**
     * Get setting from database
     *
     * @param s Setting name to retrieve
     * @return Setting with the specified name.
     */
    @SuppressWarnings("unchecked")
    public static VmSetting getSetting(String s) {
        PARAMETERS.clear();
        PARAMETERS.put("setting", s);
        result = namedQuery("VmSetting.findBySetting",
                PARAMETERS);
        if (result.isEmpty()) {
            return null;
        } else {
            return (VmSetting) result.get(0);
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<VmSetting> getSettings() {
        ArrayList<VmSetting> settings = new ArrayList<>();
        result = namedQuery("VmSetting.findAll");
        result.forEach((o) -> {
            settings.add((VmSetting) o);
        });
        return settings;
    }

    @Override
    public int write2DB() throws NonexistentEntityException, Exception {
        if (getId() != null) {
            VmSetting s = new VmSettingJpaController(
                    getEntityManagerFactory())
                    .findVmSetting(getId());
            update(s, this);
            new VmSettingJpaController(
                    getEntityManagerFactory()).edit(s);
        } else {
            VmSetting s = new VmSetting();
            update(s, this);
            new VmSettingJpaController(
                    getEntityManagerFactory()).create(s);
            setId(s.getId());
        }
        return getId();
    }

    @Override
    public VmSetting getEntity() {
        return new VmSettingJpaController(
                getEntityManagerFactory())
                .findVmSetting(getId());
    }

    @Override
    public void update(VmSetting target, VmSetting source) {
        target.setBoolVal(source.getBoolVal());
        target.setIntVal(source.getIntVal());
        target.setLongVal(source.getLongVal());
        target.setSetting(source.getSetting());
        target.setStringVal(source.getStringVal());
        target.setId(source.getId());
        target.setHistoryList(source.getHistoryList());
        target.setProjectId(source.getProjectId());
        super.update(target, source);
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
