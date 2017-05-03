package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.controller.HistoryJpaController;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class HistoryServer extends History
        implements EntityServer<History> {

    @Override
    public int write2DB() throws Exception {
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
        return getId();
    }

    @Override
    public History getEntity() {
        return new HistoryJpaController(DataBaseManager
                .getEntityManagerFactory()).findHistory(getId());
    }

    @Override
    public void update(History target, History source) {
        target.setRequirementList(source.getRequirementList());
        target.setId(source.getId());
        target.setModificationTime(source.getModificationTime());
        target.setModifierId(source.getModifierId());
        target.setReason(source.getReason());
        target.setMajorVersion(source.getMajorVersion());
        target.setMidVersion(source.getMidVersion());
        target.setMinorVersion(source.getMinorVersion());
        target.setHistoryFieldList(source.getHistoryFieldList());
        target.setVmSettingList(source.getVmSettingList());
        target.setProjectList(source.getProjectList());
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
