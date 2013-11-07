package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.VmSetting;
import com.validation.manager.core.db.controller.VmSettingJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class VMSettingServer extends VmSetting
        implements EntityServer<VmSetting> {

    private static List<Object> result;
    private static HashMap parameters = new HashMap();

    public VMSettingServer(String setting) {
        VmSetting s = getSetting(setting);
        if (s != null) {
            update(this, s);
        } else {
            throw new RuntimeException("Setting: " + setting
                    + " doesn't exist!");
        }
    }

    public VMSettingServer(String setting, boolean boolVal, int intVal,
            long longVal, String stringVal) {
        super(setting);
        setId(0);
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
     * @return
     */
    @SuppressWarnings("unchecked")
    public static VmSetting getSetting(String s) {
        parameters.clear();
        parameters.put("setting", s);
        result = DataBaseManager.namedQuery("VmSetting.findBySetting",
                parameters);
        if (result.isEmpty()) {
            return null;
        } else {
            return (VmSetting) result.get(0);
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<VmSetting> getSettings() {
        ArrayList<VmSetting> settings = new ArrayList<VmSetting>();
        result = DataBaseManager.namedQuery("VmSetting.findAll");
        for (Object o : result) {
            settings.add((VmSetting) o);
        }
        return settings;
    }

    @Override
    public int write2DB() throws NonexistentEntityException, Exception {
        if (getId() > 0) {
            VmSetting s = new VmSettingJpaController(
                    DataBaseManager.getEntityManagerFactory())
                    .findVmSetting(getId());
            update(s, this);
            new VmSettingJpaController(
                    DataBaseManager.getEntityManagerFactory()).edit(s);
        } else {
            VmSetting s = new VmSetting();
            update(s, this);
            new VmSettingJpaController(
                    DataBaseManager.getEntityManagerFactory()).create(s);
            setId(s.getId());
        }
        return getId();
    }

    public VmSetting getEntity() {
        return new VmSettingJpaController(
                DataBaseManager.getEntityManagerFactory())
                .findVmSetting(getId());
    }

    public void update(VmSetting target, VmSetting source) {
        target.setBoolVal(source.getBoolVal());
        target.setIntVal(source.getIntVal());
        target.setLongVal(source.getLongVal());
        target.setSetting(source.getSetting());
        target.setStringVal(source.getStringVal());
        target.setId(source.getId());
    }
    
    public void update() {
        update(this, getEntity());
    }
}
