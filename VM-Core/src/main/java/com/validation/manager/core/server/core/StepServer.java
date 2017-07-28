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
import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.StepPK;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.controller.StepJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class StepServer extends Step implements EntityServer<Step> {

    public StepServer(TestCase tc, int stepSequence, String text)
            throws VMException {
        super(new StepPK(tc.getTestCasePK().getId()), stepSequence,
                text.getBytes());
        setTestCase(tc);
        if (getTestCase() == null) {
            throw new VMException("Provided TestCase that doesn't exist in "
                    + "the database yet!");
        }
    }

    public StepServer(StepPK stepPK) {
        super(stepPK);
        update(StepServer.this,
                new StepJpaController(DataBaseManager
                        .getEntityManagerFactory())
                        .findStep(stepPK));
    }

    public StepServer(Step step) {
        super(step.getStepPK());
        update(StepServer.this, step);
    }

    @Override
    public int write2DB() throws NonexistentEntityException, Exception {
        StepJpaController controller = new StepJpaController(
                getEntityManagerFactory());
        if (getStepPK().getId() > 0) {
            Step temp = controller.findStep(getStepPK());
            update(temp, this);
            controller.edit(temp);
            update(this, temp);
        } else {
            Step temp = new Step(getStepPK(), getStepSequence(), getText());
            update(temp, this);
            controller.create(temp);
            update(this, temp);
            setStepPK(temp.getStepPK());
        }
        return getStepPK().getId();
    }

    @Override
    public Step getEntity() {
        return new StepJpaController(getEntityManagerFactory())
                .findStep(getStepPK());
    }

    @Override
    public void update(Step target, Step source) {
        target.setDataEntryList(source.getDataEntryList());
        target.setExpectedResult(source.getExpectedResult());
        target.setNotes(source.getNotes());
        target.setRequirementList(source.getRequirementList());
        target.setStepSequence(source.getStepSequence());
        target.setTestCase(source.getTestCase());
        target.setText(source.getText());
        target.setExecutionStepList(source.getExecutionStepList());
        super.update(target, source);
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    public void addRequirement(Requirement req) throws Exception {
        if (!getRequirementList().contains(req)
                && !getEntity().getRequirementList().contains(req)) {
            RequirementServer rs = new RequirementServer(req);
            int initial = rs.getStepList().size();
            if (!rs.getStepList().contains(getEntity())) {
                rs.getStepList().add(getEntity());
                rs.write2DB();
                rs.update();
            }
            assert initial < rs.getStepList().size();
            getRequirementList().add(req);
        }
    }

    public void removeRequirement(Requirement req) throws Exception {
        if (getRequirementList().contains(req)
                && getEntity().getRequirementList().contains(req)) {
            RequirementServer rs = new RequirementServer(req);
            rs.getStepList().remove(getEntity());
            rs.write2DB();
            getRequirementList().remove(req);
        }
    }
}
