package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestCasePK;
import com.validation.manager.core.db.controller.TestCaseJpaController;
import com.validation.manager.core.db.controller.TestJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.fmea.RiskControl;
import com.validation.manager.core.server.fmea.RiskControlServer;
import java.util.Date;
import java.util.Iterator;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TestCaseServer extends TestCase implements EntityServer {

    public TestCaseServer(TestCasePK pk) {
        super(pk);
        TestCaseJpaController controller = new TestCaseJpaController(DataBaseManager.getEntityManagerFactory());
        TestCase temp = controller.findTestCase(getTestCasePK());
        setActive(temp.getActive());
        setCreationDate(temp.getCreationDate());
        setExpectedResults(temp.getExpectedResults());
        setIsOpen(temp.getIsOpen());
        setRiskControlList(temp.getRiskControlList());
        setStepList(temp.getStepList());
        setSummary(temp.getSummary());
        setTest(temp.getTest());
        setVersion(temp.getVersion());
    }

    public TestCaseServer(int testCaseId, short version, Date creationDate) {
        super(new TestCasePK(testCaseId), version, creationDate);
        setVersion(version);
        setCreationDate(creationDate);
        setTest(new TestJpaController(DataBaseManager.getEntityManagerFactory()).findTest(testCaseId));
    }

    @Override
    public int write2DB() throws IllegalOrphanException, NonexistentEntityException, Exception {
        TestCaseJpaController controller = new TestCaseJpaController(DataBaseManager.getEntityManagerFactory());
        if (getTestCasePK().getId() > 0) {
            TestCase temp = controller.findTestCase(getTestCasePK());
            temp.setActive(getActive());
            temp.setCreationDate(getCreationDate());
            if (getExpectedResults() != null) {
                temp.setExpectedResults(getExpectedResults());
            }

            if (getRiskControlList() != null) {
                temp.setRiskControlList(getRiskControlList());
            }
            if (getStepList() != null) {
                temp.setStepList(getStepList());
            }
            temp.setIsOpen(getIsOpen());
            temp.setSummary(getSummary());
            temp.setTest(getTest());
            temp.setVersion(getVersion());
            controller.edit(temp);
        } else {
            TestCase temp = new TestCase(getTestCasePK(), getVersion(), getCreationDate());
            temp.setActive(getActive());
            temp.setIsOpen(getIsOpen());
            if (getExpectedResults() != null) {
                temp.setExpectedResults(getExpectedResults());
            }

            if (getRiskControlList() != null) {
                temp.setRiskControlList(getRiskControlList());
            }
            if (getStepList() != null) {
                temp.setStepList(getStepList());
            }
            temp.setSummary(getSummary());
            temp.setTest(getTest());
            controller.create(temp);
            setTestCasePK(temp.getTestCasePK());
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
}
