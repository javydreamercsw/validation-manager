package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Attachment;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.ExecutionStepHasAttachment;
import com.validation.manager.core.db.ExecutionStepHasIssue;
import com.validation.manager.core.db.ExecutionStepPK;
import com.validation.manager.core.db.Issue;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.ExecutionStepHasAttachmentJpaController;
import com.validation.manager.core.db.controller.ExecutionStepHasIssueJpaController;
import com.validation.manager.core.db.controller.ExecutionStepJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.Date;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
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
        if (target.getExecutionStepHasAttachmentList() == null) {
            target.setExecutionStepHasAttachmentList(new ArrayList<>());
        }
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
        target.setAssignee(source.getAssignee());
        target.setAssigner(source.getAssigner());
        target.setLocked(source.getLocked());
        target.setExecutionStepHasIssueList(source.getExecutionStepHasIssueList());
        target.setReviewed(source.getReviewed());
        target.setReviewDate(source.getReviewDate());
        target.setHistoryList(source.getHistoryList());
        target.setStepHistory(source.getStepHistory());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    public void assignUser(VmUser assignee, VmUser assigner) {
        try {
            setAssignee(assignee);
            setAssigner(assigner);
            setAssignedTime(new Date());
            write2DB();
        }
        catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void addAttachment(AttachmentServer attachment) throws Exception {
        ExecutionStepHasAttachment esha = new ExecutionStepHasAttachment();
        ExecutionStepHasAttachmentJpaController controller
                = new ExecutionStepHasAttachmentJpaController(DataBaseManager
                        .getEntityManagerFactory());
        esha.setAttachment(attachment.getEntity());
        esha.setExecutionStep(getEntity());
        esha.setCreationTime(new Date());
        controller.create(esha);
        getExecutionStepHasAttachmentList().add(esha);
        write2DB();
        update();
    }

    public void addIssue(IssueServer issue, VMUserServer user) throws Exception {
        ExecutionStepHasIssue eshi = new ExecutionStepHasIssue();
        ExecutionStepHasIssueJpaController controller
                = new ExecutionStepHasIssueJpaController(DataBaseManager
                        .getEntityManagerFactory());
        eshi.setIssue(issue.getEntity());
        eshi.setExecutionStep(getEntity());
        eshi.setVmUserList(new ArrayList<>());
        eshi.getVmUserList().add(user.getEntity());
        controller.create(eshi);
        getExecutionStepHasIssueList().add(eshi);
        write2DB();
        update();
    }

    public void removeAttachment(Attachment attachment) throws Exception {
        ExecutionStepHasAttachment toRemove = null;
        for (ExecutionStepHasAttachment esha : getExecutionStepHasAttachmentList()) {
            if (esha.getAttachment().getAttachmentPK().getId()
                    == attachment.getAttachmentPK().getId()) {
                toRemove = esha;
                break;
            }
        }
        if (toRemove != null) {
            new ExecutionStepHasAttachmentJpaController(DataBaseManager
                    .getEntityManagerFactory()).destroy(toRemove
                    .getExecutionStepHasAttachmentPK());
            update();
        }
    }

    public void removeIssue(Issue issue) throws NonexistentEntityException {
        ExecutionStepHasIssue toRemove = null;
        for (ExecutionStepHasIssue eshi : getExecutionStepHasIssueList()) {
            if (eshi.getIssue().getIssuePK().getId()
                    == issue.getIssuePK().getId()) {
                toRemove = eshi;
                break;
            }
        }
        if (toRemove != null) {
            new ExecutionStepHasIssueJpaController(DataBaseManager
                    .getEntityManagerFactory()).destroy(toRemove
                    .getExecutionStepHasIssuePK());
            update();
        }
    }
}
