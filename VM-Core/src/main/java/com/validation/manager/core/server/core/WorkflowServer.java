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
import com.validation.manager.core.db.StepTransitionsToStep;
import com.validation.manager.core.db.Workflow;
import com.validation.manager.core.db.WorkflowInstance;
import com.validation.manager.core.db.WorkflowStep;
import com.validation.manager.core.db.controller.StepTransitionsToStepJpaController;
import com.validation.manager.core.db.controller.WorkflowJpaController;
import com.validation.manager.core.db.controller.WorkflowStepJpaController;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class WorkflowServer extends Workflow 
        implements EntityServer<Workflow> {

    public WorkflowServer() {
        super();
    }

    
    public WorkflowServer(int id) {
        setId(id);
        update();
    }
    
    public WorkflowServer(String workflowName) {
        super(workflowName);
    }

    @Override
    public int write2DB() throws Exception {
        WorkflowJpaController c
                = new WorkflowJpaController(DataBaseManager
                        .getEntityManagerFactory());
        if (getId() == null) {
            Workflow w = new Workflow();
            update(w, this);
            c.create(w);
            setId(w.getId());
            //Create the default first step
            addStep("Start");
        } else {
            Workflow w = getEntity();
            update(w, this);
            c.edit(w);
        }
        update();
        return getId();
    }

    @Override
    public Workflow getEntity() {
        return new WorkflowJpaController(DataBaseManager
                .getEntityManagerFactory()).findWorkflow(getId());
    }

    @Override
    public void update(Workflow target, Workflow source) {
        target.setId(source.getId());
        target.setWorkflowInstanceList(source.getWorkflowInstanceList());
        target.setWorkflowName(source.getWorkflowName());
        target.setWorkflowStepList(source.getWorkflowStepList());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    public void addStep(String name) throws VMException {
        if (getId() != null) {
            try {
                WorkflowStep ws = new WorkflowStep(getId());
                ws.setWorkflow(getEntity());
                ws.setStepName(name);
                new WorkflowStepJpaController(DataBaseManager
                        .getEntityManagerFactory()).create(ws);
                update();
            }
            catch (Exception ex) {
                throw new VMException(ex);
            }
        } else {
            throw new VMException();
        }
    }

    public void addTransition(WorkflowStep source, WorkflowStep target, String name)
            throws VMException {
        if (getId() != null) {
            try {
                StepTransitionsToStep t
                        = new StepTransitionsToStep(
                                source.getWorkflowStepPK().getId(),
                                source.getWorkflowStepPK().getWorkflow(),
                                target.getWorkflowStepPK().getId(),
                                target.getWorkflowStepPK().getWorkflow());
                t.setTransitionName(name);
                t.setWorkflowStepSource(source);
                t.setWorkflowStepTarget(target);
                new StepTransitionsToStepJpaController(DataBaseManager
                        .getEntityManagerFactory()).create(t);
                source.getSourceTransitions().add(t);
                target.getTargetTransitions().add(t);
            }
            catch (Exception ex) {
                throw new VMException(ex);
            }
            update();
        } else {
            throw new VMException();
        }
    }

    public WorkflowInstance createInstance() throws VMException {
        if (getId() != null) {
            WorkflowInstanceServer instance = new WorkflowInstanceServer(getId());
            instance.setWorkflow(getEntity());
            try {
                instance.write2DB();
            }
            catch (Exception ex) {
                throw new VMException(ex);
            }
            return instance;
        } else {
            throw new VMException();
        }
    }
}
