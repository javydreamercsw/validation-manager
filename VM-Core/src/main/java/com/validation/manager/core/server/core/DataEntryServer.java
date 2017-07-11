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
import com.validation.manager.core.db.DataEntryType;
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
        return getStringField(name, null, false);
    }

    /**
     * Create a default string field
     *
     * @param name Field name
     * @param expected Expected result.
     * @param matchCase True if answer must match the case of the expected
     * result. False otherwise.
     * @return DataEntry. The entry is not persisted into the database yet.
     */
    public static DataEntry getStringField(String name, String expected,
            boolean matchCase) {
        DataEntry de = new DataEntry();
        DataEntryType det = DataEntryTypeServer.getType("type.string.name");
        de.setEntryName(name);
        de.setDataEntryPropertyList(getDefaultProperties(det));
        de.setDataEntryType(det);
        if (expected != null) {
            getProperty(de, "property.expected.result")
                    .setPropertyValue(expected);
            getProperty(de, "property.match.case")
                    .setPropertyValue(matchCase ? "true" : "false");
        }
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
        DataEntryType det = DataEntryTypeServer.getType("type.boolean.name");
        de.setDataEntryPropertyList(getDefaultProperties(det));
        de.setDataEntryType(det);
        return de;
    }

    /**
     * Create a boolean field
     *
     * @param name Field name
     * @param expected Expected result
     * @return DataEntry. The entry is not persisted into the database yet.
     */
    public static DataEntry getBooleanField(String name, boolean expected) {
        DataEntry de = new DataEntry();
        de.setEntryName(name);
        DataEntryType det = DataEntryTypeServer.getType("type.boolean.name");
        de.setDataEntryPropertyList(getDefaultProperties(det));
        de.setDataEntryType(det);
        getProperty(de, "property.expected.result")
                .setPropertyValue(expected ? "true" : "false");
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
        DataEntryType det = DataEntryTypeServer.getType("type.attachment.name");
        de.setDataEntryPropertyList(getDefaultProperties(det));
        de.setDataEntryType(det);
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
        DataEntryType det = DataEntryTypeServer.getType("type.numeric.name");
        de.setDataEntryPropertyList(getDefaultProperties(det));
        de.setDataEntryType(det);
        for (DataEntryProperty dep : de.getDataEntryPropertyList()) {
            if (dep.getPropertyName().equals("property.min") && min != null) {
                //Set minimum
                dep.setPropertyValue(min.toString());
            }
            if (dep.getPropertyName().equals("property.max") && max != null) {
                //Set minimum
                dep.setPropertyValue(max.toString());
            }
        }
        return de;
    }

    public static List<DataEntryProperty> getDefaultProperties(DataEntryType det) {
        List<DataEntryProperty> props = new ArrayList<>();
        //Required by default
        props.add(DataEntryPropertyServer
                .createProperty("property.required", true));
        switch (det.getId()) {
            case 2://Numeric: add value range
                props.add(DataEntryPropertyServer
                        .createProperty("property.min", null));
                props.add(DataEntryPropertyServer
                        .createProperty("property.max", null));
                break;
            case 1://Additional properties for text fields
                props.add(DataEntryPropertyServer
                        .createProperty("property.match.case", false));
            //Fall thru
            default:
                //Property for expected result.
                props.add(DataEntryPropertyServer
                        .createProperty("property.expected.result", null));
        }
        return props;
    }

    public static DataEntryProperty getProperty(DataEntry de, String name) {
        DataEntryProperty prop = null;
        for (DataEntryProperty p : de.getDataEntryPropertyList()) {
            if (p.getPropertyName().equals(name)) {
                prop = p;
                break;
            }
        }
        return prop;
    }
}
