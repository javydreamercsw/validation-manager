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
            c.create(r);
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
