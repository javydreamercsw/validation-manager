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

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.DataEntry;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.controller.TestCaseJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class TestCaseServer extends TestCase
        implements EntityServer<TestCase> {

    public TestCaseServer(int id) {
        super(id);
        update();
    }

    public TestCaseServer(TestCase tc) {
        super.setId(tc.getId());
        update();
    }

    public TestCaseServer(String name, Date creationDate) {
        super(name, creationDate);
    }

    @Override
    public int write2DB() throws IllegalOrphanException,
            NonexistentEntityException, Exception {
        TestCaseJpaController controller
                = new TestCaseJpaController(getEntityManagerFactory());
        if (getId() != null && getId() > 0) {
            TestCase temp = controller.findTestCase(getId());
            update(temp, this);
            controller.edit(temp);
        } else {
            TestCase temp = new TestCase(getName(), getCreationDate());
            temp.setCreationDate(new Date());
            controller.create(temp);
            setId(temp.getId());
        }
        update();
        return getId();
    }

    @Override
    public TestCase getEntity() {
        return new TestCaseJpaController(getEntityManagerFactory())
                .findTestCase(getId());
    }

    @Override
    public void update(TestCase target, TestCase source) {
        target.setActive(source.getActive());
        target.setCreationDate(source.getCreationDate());
        target.setRiskControlHasTestCaseList(source
                .getRiskControlHasTestCaseList());
        target.setStepList(source.getStepList() == null
                ? new ArrayList<>() : source.getStepList());
        target.setIsOpen(source.getIsOpen());
        target.setSummary(source.getSummary());
        target.setTestPlanList(new ArrayList<>());
        target.setId(source.getId());
        target.setName(source.getName());
        target.setIsOpen(source.getIsOpen());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    public Step addStep(int sequence, String text, String note, String criteria,
            List<Requirement> requirements) throws PreexistingEntityException,
            Exception {
        //Use default plain String Field
        return addStep(sequence, text, note, criteria, requirements,
                DataEntryServer.getStringField("general.result"));
    }

    public Step addStep(int sequence, String text, String note, String criteria,
            List<Requirement> requirements, DataEntry de)
            throws PreexistingEntityException, Exception {
        StepServer ss = new StepServer(getEntity(), sequence, text);
        int amount = getStepList().size();
        ss.setNotes(note);
        ss.setExpectedResult(criteria.getBytes("UTF-8"));
        if (ss.getRequirementList() == null) {
            ss.setRequirementList(new ArrayList<>());
        }
        ss.write2DB();
        if (requirements != null) {
            requirements.forEach((req) -> {
                ss.getRequirementList().add(req);
            });
        }
        ss.write2DB();
        update();
        assert getStepList().size() > amount;
        return ss.getEntity();
    }
}
