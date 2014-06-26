package com.validation.manager.core.server.core;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class TestCaseServer extends TestCase implements EntityServer<TestCase> {

    public TestCaseServer(TestCasePK pk) {
        super(pk);
        TestCaseJpaController controller
                = new TestCaseJpaController(getEntityManagerFactory());
        TestCase temp = controller.findTestCase(getTestCasePK());
        update((TestCaseServer) this, temp);
    }

    public TestCaseServer(TestCase tc) {
        update((TestCaseServer) this, tc);
    }

    public TestCaseServer(String name, int testCaseId, short version, Date creationDate) {
        super(name, new TestCasePK(testCaseId), version, creationDate);
        setTest(new TestJpaController(getEntityManagerFactory()).findTest(testCaseId));
    }

    @Override
    public int write2DB() throws IllegalOrphanException,
            NonexistentEntityException, Exception {
        TestCaseJpaController controller
                = new TestCaseJpaController(getEntityManagerFactory());
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

    @Override
    public TestCase getEntity() {
        return new TestCaseJpaController(
                getEntityManagerFactory())
                .findTestCase(getTestCasePK());
    }

    @Override
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

    @Override
    public void update() {
        update(this, getEntity());
    }

    public Step addStep(int sequence, String text, String note, String criteria,
            List<Requirement> requirements)
            throws PreexistingEntityException, Exception {
        StepServer ss = new StepServer(getEntity(), sequence, text);
        int amount = getStepList().size();
        ss.setNotes(note);
        ss.setExpectedResult(criteria.getBytes("UTF-8"));
        if (ss.getRequirementList() == null) {
            ss.setRequirementList(new ArrayList<Requirement>());
        }
        ss.write2DB();
        List<String> processed = new ArrayList<String>();
        for (Requirement req : requirements) {
            //Make sure there are no duplicate requirements in list
            if (!processed.contains(req.getUniqueId().trim())) {
                processed.add(req.getUniqueId().trim());
                Requirement max = Collections.max(new RequirementServer(req).getVersions(), null);
                ss.getRequirementList().add(max);
                RequirementServer rs = new RequirementServer(max);
                rs.getStepList().add(ss.getEntity());
                rs.write2DB();
            }
        }
        ss.write2DB();
        update(this, getEntity());
        assert getStepList().size() > amount;
        return ss.getEntity();
    }
}
