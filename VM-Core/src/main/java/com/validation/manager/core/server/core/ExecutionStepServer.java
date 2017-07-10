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
    public int write2DB() throws VMException {
        try {
            if (getExecutionStepPK() == null) {
                ExecutionStep es = new ExecutionStep();
                update(es, this);
                c.create(es);
                setExecutionStepPK(es.getExecutionStepPK());
            } else {
                ExecutionStep es = getEntity();
                update(es, this);
                c.edit(es);
            }
            update();
        }
        catch (Exception ex) {
            throw new VMException(ex);
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
        target.setAssignee(source.getAssignee());
        target.setAssigner(source.getAssigner());
        target.setLocked(source.getLocked());
        target.setExecutionStepHasIssueList(source.getExecutionStepHasIssueList());
        target.setReviewed(source.getReviewed());
        target.setReviewDate(source.getReviewDate());
        target.setHistoryList(source.getHistoryList());
        target.setStepHistory(source.getStepHistory());
        target.setExecutionStepAnswerList(source.getExecutionStepAnswerList());
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
        catch (VMException ex) {
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
