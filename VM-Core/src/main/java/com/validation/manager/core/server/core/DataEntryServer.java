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
import com.validation.manager.core.db.DataEntry;
import com.validation.manager.core.db.DataEntryProperty;
import com.validation.manager.core.db.controller.DataEntryJpaController;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class DataEntryServer extends DataEntry implements EntityServer<DataEntry> {

    public DataEntryServer(DataEntry de) {
        update(DataEntryServer.this, de);
    }

    @Override
    public int write2DB() throws Exception {
        DataEntryJpaController c = new DataEntryJpaController(DataBaseManager
                .getEntityManagerFactory());
        if (getDataEntryPK() == null) {
            DataEntry de = new DataEntry();
            update(de, this);
            List<DataEntryProperty> dest = new ArrayList<>();
            if (getDataEntryPropertyList() != null) {
                dest = new ArrayList<>(getDataEntryPropertyList());
                de.setDataEntryPropertyList(new ArrayList<>());
            }
            c.create(de);
            dest.forEach(dep -> {
                try {
                    DataEntryPropertyServer deps = new DataEntryPropertyServer(dep);
                    deps.setDataEntry(de);
                    deps.write2DB();
                    deps.update(dep, deps);
                }
                catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            });
            setDataEntryPK(de.getDataEntryPK());
        } else {
            DataEntry de = getEntity();
            update(de, this);
            c.edit(de);
        }
        update();
        return getDataEntryPK().getId();
    }

    @Override
    public DataEntry getEntity() {
        return new DataEntryJpaController(DataBaseManager
                .getEntityManagerFactory()).findDataEntry(getDataEntryPK());
    }

    @Override
    public void update(DataEntry target, DataEntry source) {
        target.setDataEntryPK(source.getDataEntryPK());
        target.setDataEntryPropertyList(source.getDataEntryPropertyList());
        target.setDataEntryType(source.getDataEntryType());
        target.setEntryName(source.getEntryName());
        target.setStep(source.getStep());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    /**
     * Create a default string field
     *
     * @param name Field name
     * @return DataEntry. The entry is not persisted into the database yet.
     */
    public static DataEntry getStringField(String name) {
        DataEntry de = new DataEntry();
        de.setEntryName(name);
        de.setDataEntryPropertyList(new ArrayList<>());
        de.setDataEntryType(DataEntryTypeServer.getType("string.type.name"));
        return de;
    }

    /**
     * Create a boolean field
     *
     * @param name Field name
     * @return DataEntry. The entry is not persisted into the database yet.
     */
    public static DataEntry getBooleanField(String name) {
        DataEntry de = new DataEntry();
        de.setEntryName(name);
        de.setDataEntryPropertyList(new ArrayList<>());
        de.setDataEntryType(DataEntryTypeServer.getType("boolean.type.name"));
        return de;
    }

    /**
     * Create a attachment field
     *
     * @param name Field name
     * @return DataEntry. The entry is not persisted into the database yet.
     */
    public static DataEntry getAttachmentField(String name) {
        DataEntry de = new DataEntry();
        de.setEntryName(name);
        de.setDataEntryPropertyList(new ArrayList<>());
        de.setDataEntryType(DataEntryTypeServer.getType("attachment.type.name"));
        de.getDataEntryPropertyList().add(DataEntryPropertyServer
                .createProperty("property.required", true));
        return de;
    }

    /**
     * Create a numeric field.
     *
     * @param name Field name
     * @param min Minimum value. Set as null for no limit.
     * @param max Maximum value. Set as null for no limit.
     * @return DataEntry. The entry is not persisted into the database yet.
     */
    public static DataEntry getNumericField(String name, Float min, Float max) {
        DataEntry de = new DataEntry();
        de.setEntryName(name);
        de.setDataEntryPropertyList(new ArrayList<>());
        de.setDataEntryType(DataEntryTypeServer.getType("numeric.type.name"));
        if (min != null) {
            //Set minimum
            de.getDataEntryPropertyList().add(DataEntryPropertyServer
                    .createProperty("property.min", min));
        }
        if (max != null) {
            //Set maximum
            de.getDataEntryPropertyList().add(DataEntryPropertyServer
                    .createProperty("property.max", max));
        }
        return de;
    }
}
