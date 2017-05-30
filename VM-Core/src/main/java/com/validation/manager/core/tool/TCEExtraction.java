package com.validation.manager.core.tool;

import com.validation.manager.core.server.core.TestCaseExecutionServer;
import com.validation.manager.core.server.core.TestCaseServer;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class TCEExtraction {

    private final TestCaseExecutionServer tce;
    private final TestCaseServer tcs;

    public TCEExtraction(TestCaseExecutionServer tce, TestCaseServer tcs) {
        this.tce = tce;
        this.tcs = tcs;
    }

    /**
     * @return the tce
     */
    public TestCaseExecutionServer getTestCaseExecution() {
        return tce;
    }

    /**
     * @return the tcs
     */
    public TestCaseServer getTestCase() {
        return tcs;
    }
}
