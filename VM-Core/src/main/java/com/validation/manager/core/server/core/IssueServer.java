package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Issue;
import com.validation.manager.core.db.controller.IssueJpaController;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class IssueServer extends Issue
        implements EntityServer<Issue> {

    private final IssueJpaController c
            = new IssueJpaController(DataBaseManager.getEntityManagerFactory());

    public IssueServer() {
        super();
    }

    public IssueServer(Issue issue) {
        super(issue.getIssuePK());
        update();
    }

    @Override
    public int write2DB() throws Exception {
        if (getIssuePK() == null) {
            Issue i = new Issue();
            update(i, this);
            c.create(i);
            update(this, i);
        } else {
            Issue i = getEntity();
            update(i, this);
            c.edit(i);
            update(this, i);
        }
        return getIssuePK().getId();
    }

    @Override
    public Issue getEntity() {
        return c.findIssue(getIssuePK());
    }

    @Override
    public void update(Issue target, Issue source) {
        target.setCreationTime(source.getCreationTime());
        target.setDescription(source.getDescription());
        target.setExecutionStepHasIssueList(source.getExecutionStepHasIssueList());
        target.setIssueResolutionId(source.getIssueResolutionId());
        target.setIssueType(source.getIssueType());
        target.setTitle(source.getTitle());
        target.setIssuePK(source.getIssuePK());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
