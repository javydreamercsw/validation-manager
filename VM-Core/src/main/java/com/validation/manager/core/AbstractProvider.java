package com.validation.manager.core;

import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.server.core.TestCaseExecutionServer;
import java.util.logging.Logger;

public abstract class AbstractProvider implements IMainContentProvider {

    protected static final Logger LOG
            = Logger.getLogger(AbstractProvider.class.getSimpleName());

    @Override
    public void update() {
        //Do nothing by default
    }

    @Override
    public String getId() {
        return getComponentCaption();
    }

    protected boolean isLocked(TestCaseExecutionServer tce) {
        return isLocked(tce, -1);
    }

    protected boolean isLocked(TestCaseExecutionServer tce, int tcID) {
        boolean locked = true;
        //Check to see if the execution is locked or not
        if (tce != null) {
            for (ExecutionStep e : tce.getExecutionStepList()) {
                if ((tcID > 0
                        && e.getExecutionStepPK().getStepTestCaseId() == tcID)
                        && !e.getLocked()) {
                    locked = false;
                    break;
                } else if (tcID < 0 && !e.getLocked()) {
                    locked = false;
                    break;
                }
            }
        }
        return locked;
    }

    @Override
    public void processNotification() {
        //Nothing by default
    }
}
