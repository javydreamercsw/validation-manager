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

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.controller.HistoryJpaController;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class HistoryServer extends History
        implements EntityServer<History> {

    public HistoryServer() {
        super();
    }

    public HistoryServer(History history) {
        super(history.getId());
        update();
    }

    @Override
    public int write2DB() throws VMException {
        try {
            HistoryJpaController c = new HistoryJpaController(DataBaseManager
                    .getEntityManagerFactory());
            if (getId() == null) {
                History h = new History();
                update(h, this);
                c.create(h);
                update(this, h);
            } else {
                History h = getEntity();
                update(h, this);
                c.edit(h);
                update(this, h);
            }
        }
        catch (Exception ex) {
            throw new VMException(ex);
        }
        return getId();
    }

    @Override
    public History getEntity() {
        return new HistoryJpaController(DataBaseManager
                .getEntityManagerFactory()).findHistory(getId());
    }

    @Override
    public void update(History target, History source) {
        target.setBaselineList(source.getBaselineList());
        target.setExecutionStepList(source.getExecutionStepList());
        target.setHistoryFieldList(source.getHistoryFieldList());
        target.setId(source.getId());
        target.setMajorVersion(source.getMajorVersion());
        target.setMidVersion(source.getMidVersion());
        target.setMinorVersion(source.getMinorVersion());
        target.setModificationTime(source.getModificationTime());
        target.setModifierId(source.getModifierId());
        target.setReason(source.getReason());
        target.setProjectId(source.getProjectId());
        target.setStep(source.getStep());
        target.setRequirementId(source.getRequirementId());
        target.setVmSettingId(source.getVmSettingId());
        target.setVmUserId(source.getVmUserId());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    /**
     * Do a major version of the item
     */
    public void increaseMajor() {
        setMajorVersion(getMajorVersion() + 1);
        setMidVersion(0);
        setMinorVersion(0);
    }

    /**
     * Do a medium version of the item
     */
    public void increaseMid() {
        setMidVersion(getMidVersion() + 1);
        setMinorVersion(0);
    }

    /**
     * Do a minor version of the item
     */
    public void increaseMinor() {
        setMinorVersion(getMinorVersion() + 1);
    }
}
