package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.ExecutionStepHasAttachment;
import com.validation.manager.core.db.ExecutionStepPK;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.ExecutionStepHasAttachmentJpaController;
import com.validation.manager.core.db.controller.ExecutionStepJpaController;
import java.util.Date;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class ExecutionStepServer extends ExecutionStep
        implements EntityServer<ExecutionStep> {

    private final ExecutionStepJpaController c
            = new ExecutionStepJpaController(DataBaseManager
                    .getEntityManagerFactory());

    public ExecutionStepServer(ExecutionStep es) {
        super(es.getExecutionStepPK());
        update();
    }

    public ExecutionStepServer(ExecutionStepPK pk) {
        super(pk);
        update();
    }

    @Override
    public int write2DB() throws Exception {

        if (getExecutionStepPK() == null) {
            ExecutionStep es = new ExecutionStep();
            update(es, this);
            c.create(es);
            setExecutionStepPK(es.getExecutionStepPK());
            update();
        } else {
            ExecutionStep es = getEntity();
            update(es, this);
            c.edit(es);
            update();
        }
        return getExecutionStepPK().getTestCaseExecutionId();
    }

    @Override
    public ExecutionStep getEntity() {
        return c.findExecutionStep(getExecutionStepPK());
    }

    @Override
    public void update(ExecutionStep target, ExecutionStep source) {
        target.setAssignedTime(source.getAssignedTime());
        target.setComment(source.getComment());
        target.setExecutionEnd(source.getExecutionEnd());
        target.setExecutionStart(source.getExecutionStart());
        target.setExecutionStepHasAttachmentList(source
                .getExecutionStepHasAttachmentList());
        target.setExecutionTime(source.getExecutionTime());
        target.setResultId(source.getResultId());
        target.setStep(source.getStep());
        target.setTestCaseExecution(source.getTestCaseExecution());
        target.setVmUserId(source.getVmUserId());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    public void assignUser(VmUser assignee, VmUser assigner) {
        try {
            setVmUserId(assignee);
            setAssignedTime(new Date());
            write2DB();
            //Now set the assignee
            new VMUserServer(assigner).setAsAssigner(getEntity());
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void addAttachment(AttachmentServer attachment) throws Exception {
        ExecutionStepHasAttachment esha = new ExecutionStepHasAttachment();
        ExecutionStepHasAttachmentJpaController controller
                = new ExecutionStepHasAttachmentJpaController(DataBaseManager
                        .getEntityManagerFactory());
        //Make sure attachemtn exists in database
        attachment.write2DB();
        esha.setAttachment(attachment.getEntity());
        esha.setExecutionStep(getEntity());
        esha.setCreationTime(new Date());
        controller.create(esha);
        update();
    }
}
