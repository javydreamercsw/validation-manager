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
import com.validation.manager.core.db.IssueType;
import com.validation.manager.core.db.controller.IssueTypeJpaController;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class IssueTypeServer extends IssueType
        implements EntityServer<IssueType> {

    public IssueTypeServer(int id) {
        setId(id);
        update();
    }

    public IssueTypeServer(String typeName) {
        super(typeName);
    }

    @Override
    public int write2DB() throws Exception {
        if (getId() == null) {
            IssueType i = new IssueType();
            update(i, this);
            new IssueTypeJpaController(DataBaseManager
                    .getEntityManagerFactory()).create(i);
            update(this, i);
        } else {
            IssueType i = getEntity();
            update(i, this);
            new IssueTypeJpaController(DataBaseManager
                    .getEntityManagerFactory()).edit(i);
            update(this, i);
        }
        return getId();
    }

    @Override
    public IssueType getEntity() {
        return new IssueTypeJpaController(DataBaseManager
                .getEntityManagerFactory()).findIssueType(getId());
    }

    @Override
    public void update(IssueType target, IssueType source) {
        target.setDescription(source.getDescription());
        target.setIssueList(source.getIssueList());
        target.setTypeName(source.getTypeName());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    /**
     * Get an Issue Type by name.
     *
     * @param typename name to search for
     * @return Issue Type for the specified name or null if not found.
     */
    public static IssueType getType(String typename) {
        IssueType result = null;
        for (IssueType type : new IssueTypeJpaController(DataBaseManager
                .getEntityManagerFactory()).findIssueTypeEntities()) {
            if (type.getTypeName().equals(typename)) {
                result = type;
                break;
            }
        }
        return result;
    }
}
