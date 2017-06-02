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
import com.validation.manager.core.db.ExecutionResult;
import com.validation.manager.core.db.controller.ExecutionResultJpaController;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ExecutionResultServer extends ExecutionResult
        implements EntityServer<ExecutionResult> {

    public ExecutionResultServer(String resultName) {
        super(resultName);
    }

    public ExecutionResultServer(int id) {
        setId(id);
        update();
    }

    @Override
    public int write2DB() throws Exception {
        ExecutionResultJpaController c
                = new ExecutionResultJpaController(DataBaseManager
                        .getEntityManagerFactory());
        if (getId() == null) {
            ExecutionResult r = new ExecutionResult();
            update(r, this);
            c.create(r);
            setId(r.getId());
            update();
        } else {
            ExecutionResult r = getEntity();
            update(r, this);
            c.edit(r);
            setId(r.getId());
            update();
        }
        return getId();
    }

    @Override
    public ExecutionResult getEntity() {
        return new ExecutionResultJpaController(DataBaseManager
                .getEntityManagerFactory()).findExecutionResult(getId());
    }

    @Override
    public void update(ExecutionResult target, ExecutionResult source) {
        target.setExecutionStepList(source.getExecutionStepList());
        target.setId(source.getId());
        target.setResultName(source.getResultName());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    public static ExecutionResult getResult(String result) {
        PARAMETERS.clear();
        PARAMETERS.put("resultName", result);
        List r = DataBaseManager.namedQuery("ExecutionResult.findByResultName",
                PARAMETERS);
        if (r.isEmpty()) {
            return null;
        } else {
            return (ExecutionResult) r.get(0);
        }
    }

    public static List<ExecutionResult> getResults() {
        return new ExecutionResultJpaController(DataBaseManager
                .getEntityManagerFactory()).findExecutionResultEntities();
    }
}
