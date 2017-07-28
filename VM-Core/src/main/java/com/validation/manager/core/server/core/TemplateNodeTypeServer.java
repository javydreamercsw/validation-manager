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
import com.validation.manager.core.db.TemplateNodeType;
import com.validation.manager.core.db.controller.TemplateNodeTypeJpaController;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class TemplateNodeTypeServer extends TemplateNodeType
        implements EntityServer<TemplateNodeType> {

    @Override
    public int write2DB() throws Exception {
        TemplateNodeTypeJpaController c
                = new TemplateNodeTypeJpaController(DataBaseManager
                        .getEntityManagerFactory());
        if (getId() == null) {
            TemplateNodeType t = new TemplateNodeType();
            update(t, this);
            c.create(t);
            setId(t.getId());
        } else {
            TemplateNodeType t = getEntity();
            update(t, this);
            c.edit(t);
        }
        update();
        return getId();
    }

    @Override
    public TemplateNodeType getEntity() {
        return new TemplateNodeTypeJpaController(DataBaseManager
                .getEntityManagerFactory()).findTemplateNodeType(getId());
    }

    @Override
    public void update(TemplateNodeType target, TemplateNodeType source) {
        target.setId(source.getId());
        target.setTemplateNodeList(source.getTemplateNodeList());
        target.setTypeName(source.getTypeName());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    public static TemplateNodeType getType(String type) {
        PARAMETERS.clear();
        PARAMETERS.put("typeName", type);
        List<Object> result = DataBaseManager
                .namedQuery("TemplateNodeType.findByTypeName", PARAMETERS);
        if (result.isEmpty()) {
            return null;
        } else {
            return (TemplateNodeType) result.get(0);
        }
    }
}
