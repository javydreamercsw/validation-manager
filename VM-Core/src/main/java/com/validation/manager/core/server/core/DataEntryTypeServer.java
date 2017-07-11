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
import com.validation.manager.core.db.DataEntryType;
import com.validation.manager.core.db.controller.DataEntryTypeJpaController;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class DataEntryTypeServer extends DataEntryType
        implements EntityServer<DataEntryType> {

    @Override
    public int write2DB() throws Exception {
        DataEntryTypeJpaController c
                = new DataEntryTypeJpaController(DataBaseManager
                        .getEntityManagerFactory());
        if (getId() == null) {
            DataEntryType det = new DataEntryType();
            update(det, this);
            c.create(det);
            setId(det.getId());
        } else {
            DataEntryType det = getEntity();
            update(det, this);
            c.edit(det);
            setId(det.getId());
        }
        update();
        return getId();
    }

    @Override
    public DataEntryType getEntity() {
        return new DataEntryTypeJpaController(DataBaseManager
                .getEntityManagerFactory()).findDataEntryType(getId());
    }

    @Override
    public void update(DataEntryType target, DataEntryType source) {
        target.setDataEntryList(source.getDataEntryList());
        target.setId(source.getId());
        target.setTypeDescription(source.getTypeDescription());
        target.setTypeName(source.getTypeName());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    public static DataEntryType getType(String type) {
        PARAMETERS.clear();
        PARAMETERS.put("typeName", type);
        List<Object> result = DataBaseManager
                .namedQuery("DataEntryType.findByTypeName", PARAMETERS);
        return result.isEmpty() ? null : (DataEntryType) result.get(0);
    }

    public static Iterable<DataEntryType> getTypes() {
        return new DataEntryTypeJpaController(DataBaseManager
                .getEntityManagerFactory()).findDataEntryTypeEntities();
    }
}
