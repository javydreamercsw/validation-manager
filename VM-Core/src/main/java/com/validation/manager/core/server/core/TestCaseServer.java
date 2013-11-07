package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RiskControl;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestCasePK;
import com.validation.manager.core.db.controller.TestCaseJpaController;
import com.validation.manager.core.db.controller.TestJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import com.validation.manager.core.server.fmea.RiskControlServer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class TestCaseServer extends TestCase implements EntityServer<TestCase> {

    public TestCaseServer(TestCasePK pk) {
        super(pk);
        TestCaseJpaController controller
                = new TestCaseJpaController(DataBaseManager.getEntityManagerFactory());
        TestCase temp = controller.findTestCase(getTestCasePK());
        update((TestCaseServer) this, temp);
    }

    public TestCaseServer(TestCase tc) {
        update((TestCaseServer) this, tc);
    }

    public TestCaseServer(String name, int testCaseId, short version, Date creationDate) {
        super(name, new TestCasePK(testCaseId), version, creationDate);
        setTest(new TestJpaController(DataBaseManager.getEntityManagerFactory()).findTest(testCaseId));
    }

    @Override
    public int write2DB() throws IllegalOrphanException,
            NonexistentEntityException, Exception {
        TestCaseJpaController controller
                = new TestCaseJpaController(DataBaseManager.getEntityManagerFactory());
        if (controller.findTestCase(getTestCasePK()) != null || getTestCasePK().getId() > 0) {
            TestCase temp = controller.findTestCase(getTestCasePK());
            update(temp, this);
            controller.edit(temp);
        } else {
            TestCase temp = new TestCase(getName(), getTestCasePK(),
                    getVersion(), getCreationDate());
            update(temp, this);
            controller.create(temp);
        }
        return getTestCasePK().getId();
    }

    public static void deleteTestCase(TestCase tc) throws NonexistentEntityException, IllegalOrphanException, Exception {
        for (Iterator<Step> it = tc.getStepList().iterator(); it.hasNext();) {
            StepServer.deleteStep(it.next());
        }
        tc.getStepList().clear();
        for (Iterator<RiskControl> it = tc.getRiskControlList().iterator(); it.hasNext();) {
            RiskControlServer.deleteRiskControl(it.next());
        }
        tc.getRiskControlList().clear();
        new TestCaseJpaController(DataBaseManager.getEntityManagerFactory()).edit(tc);
        new TestCaseJpaController(DataBaseManager.getEntityManagerFactory()).destroy(tc.getTestCasePK());
    }

    public TestCase getEntity() {
        return new TestCaseJpaController(
                DataBaseManager.getEntityManagerFactory())
                .findTestCase(getTestCasePK());
    }

    public void update(TestCase target, TestCase source) {
        target.setActive(source.getActive());
        target.setCreationDate(source.getCreationDate());
        target.setAuthorId(source.getAuthorId());
        target.setExpectedResults(source.getExpectedResults());
        target.setRiskControlList(source.getRiskControlList() == null
                ? new ArrayList<RiskControl>() : source.getRiskControlList());
        target.setStepList(source.getStepList() == null
                ? new ArrayList<Step>() : source.getStepList());
        target.setIsOpen(source.getIsOpen());
        target.setSummary(source.getSummary());
        target.setTest(source.getTest());
        target.setVersion(source.getVersion());
        target.setTestCasePK(source.getTestCasePK());
        target.setName(source.getName());
    }

    public void update() {
        update(this, getEntity());
    }

    public void addStep(int sequence, String text, String note, String criteria,
            List<Requirement> requirements)
            throws PreexistingEntityException, Exception {
        StepServer s = new StepServer(getEntity(), sequence, text);
        int amount = getStepList().size();
        s.setNotes(note);
        s.setTestCase(getEntity());
        s.setExpectedResult(criteria.getBytes("UTF-8"));
        if (s.getRequirementList() == null) {
            s.setRequirementList(new ArrayList<Requirement>());
        }
        for (Requirement req : requirements) {
            s.getRequirementList().add(req);
        }
        s.write2DB();
        update(this, getEntity());
        assert getStepList().size() > amount;
    }
}
