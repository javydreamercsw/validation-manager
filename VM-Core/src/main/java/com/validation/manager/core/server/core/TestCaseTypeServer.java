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
import com.validation.manager.core.db.TestCaseType;
import com.validation.manager.core.db.controller.TestCaseTypeJpaController;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class TestCaseTypeServer extends TestCaseType
        implements EntityServer<TestCaseType> {

    public TestCaseTypeServer() {
    }

    public TestCaseTypeServer(Integer id) {
        super(id);
        update();
    }

    public TestCaseTypeServer(String typeName, String typeDescription) {
        setTypeName(typeName);
        setTypeDescription(typeDescription);
    }

    @Override
    public int write2DB() throws Exception {
        TestCaseTypeJpaController c
                = new TestCaseTypeJpaController(DataBaseManager
                        .getEntityManagerFactory());
        if (getId() == null) {
            TestCaseType tct = new TestCaseType();
            update(tct, this);
            c.create(tct);
            setId(tct.getId());
        } else {
            TestCaseType tct = getEntity();
            update(tct, this);
            c.edit(tct);
        }
        update();
        return getId();
    }

    @Override
    public TestCaseType getEntity() {
        return new TestCaseTypeJpaController(DataBaseManager
                .getEntityManagerFactory()).findTestCaseType(getId());
    }

    @Override
    public void update(TestCaseType target, TestCaseType source) {
        target.setId(source.getId());
        target.setTestCaseList(source.getTestCaseList());
        target.setTypeDescription(source.getTypeDescription());
        target.setTypeName(source.getTypeName());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
