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
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.WorkflowInstance;
import com.validation.manager.core.db.WorkflowInstanceHasTransition;
import com.validation.manager.core.db.WorkflowInstancePK;
import com.validation.manager.core.db.WorkflowStep;
import com.validation.manager.core.db.controller.WorkflowInstanceHasTransitionJpaController;
import com.validation.manager.core.db.controller.WorkflowInstanceJpaController;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class WorkflowInstanceServer extends WorkflowInstance
        implements EntityServer<WorkflowInstance> {

    public WorkflowInstanceServer() {
    }

    public WorkflowInstanceServer(WorkflowInstancePK workflowInstancePK) {
        super(workflowInstancePK);
        update();
    }

    public WorkflowInstanceServer(int workflow) {
        super(workflow);
    }

    @Override
    public int write2DB() throws Exception {
        WorkflowInstanceJpaController c
                = new WorkflowInstanceJpaController(DataBaseManager
                        .getEntityManagerFactory());
        if (getWorkflowStep() == null) {
            setWorkflowStep(getWorkflow().getWorkflowStepList().get(0));
            write2DB();
        }
        if (getWorkflowInstancePK() == null || getWorkflowInstancePK().getId() == 0) {
            WorkflowInstance wi = new WorkflowInstance();
            update(wi, this);
            c.create(wi);
            setWorkflowInstancePK(wi.getWorkflowInstancePK());
        } else {
            WorkflowInstance wi = getEntity();
            update(wi, this);
            c.edit(wi);
        }
        update();
        return getWorkflowInstancePK().getId();
    }

    @Override
    public WorkflowInstance getEntity() {
        return new WorkflowInstanceJpaController(DataBaseManager
                .getEntityManagerFactory())
                .findWorkflowInstance(getWorkflowInstancePK());
    }

    @Override
    public void update(WorkflowInstance target, WorkflowInstance source) {
        target.setAssignedUser(source.getAssignedUser());
        target.setWorkflow(source.getWorkflow());
        target.setWorkflowInstanceHasTransitionList(source
                .getWorkflowInstanceHasTransitionList());
        target.setWorkflowInstancePK(source.getWorkflowInstancePK());
        target.setWorkflowStep(source.getWorkflowStep());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    public void transition(WorkflowStep step,
            VmUser transitioner, String source) throws VMException {
        //Check if the workflow has that step
        if (Objects.equals(step.getWorkflow().getId(), getWorkflow().getId())) {
            if (getWorkflowStep() == null) {
                throw new VMException("Invalid transition!");
            } else {
                //Now check if theres a transition from the current step to the next one.
                for (StepTransitionsToStep transition
                        : getWorkflowStep().getSourceTransitions()) {
                    if (transition.getWorkflowStepTarget()
                            .getWorkflowStepPK().getId()
                            == step.getWorkflowStepPK().getId()) {
                        try {
                            WorkflowInstanceHasTransition t
                                    = new WorkflowInstanceHasTransition();
                            t.setStepTransitionsToStep(transition);
                            t.setTransitionDate(new Date());
                            t.setTransitionSource(source);
                            t.setTransitioner(transitioner);
                            t.setWorkflowInstance(getEntity());
                            new WorkflowInstanceHasTransitionJpaController(
                                    DataBaseManager.getEntityManagerFactory())
                                    .create(t);
                            update();
                            setWorkflowStep(step);
                            write2DB();
                            return;
                        }
                        catch (Exception ex) {
                            throw new VMException(ex);
                        }
                    }
                }
            }
        }
        throw new VMException("Invalid transition!");
    }
}
