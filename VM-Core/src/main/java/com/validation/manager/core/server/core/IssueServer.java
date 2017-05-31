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
