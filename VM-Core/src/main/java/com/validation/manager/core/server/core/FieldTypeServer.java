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
import com.validation.manager.core.db.FieldType;
import com.validation.manager.core.db.controller.FieldTypeJpaController;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class FieldTypeServer extends FieldType
        implements EntityServer<FieldType> {

    public static FieldType findType(String simpleName) {
        PARAMETERS.clear();
        PARAMETERS.put("typeName", simpleName);
        List r = DataBaseManager.namedQuery("FieldType.findByTypeName", PARAMETERS);
        return r.isEmpty() ? null : (FieldType) r.get(0);
    }

    @Override
    public int write2DB() throws VMException {
        try {
            FieldTypeJpaController c = new FieldTypeJpaController(DataBaseManager
                    .getEntityManagerFactory());
            if (getId() == null) {
                FieldType ft = new FieldType();
                update(ft, this);
                c.create(ft);
                update(this, ft);
            } else {
                FieldType ft = getEntity();
                update(ft, this);
                c.edit(ft);
                update(this, ft);
            }
        }
        catch (Exception ex) {
            throw new VMException(ex);
        }
        return getId();
    }

    @Override
    public FieldType getEntity() {
        return new FieldTypeJpaController(DataBaseManager
                .getEntityManagerFactory()).findFieldType(getId());
    }

    @Override
    public void update(FieldType target, FieldType source) {
        target.setHistoryFieldList(source.getHistoryFieldList());
        target.setId(source.getId());
        target.setTypeName(source.getTypeName());
        target.setWorkflowStepFieldList(source.getWorkflowStepFieldList());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
