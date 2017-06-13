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
import com.validation.manager.core.db.ReviewResult;
import com.validation.manager.core.db.controller.ReviewResultJpaController;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ReviewResultServer extends ReviewResult
        implements EntityServer<ReviewResult> {

    public ReviewResultServer(int id) {
        setId(id);
        update();
    }

    public ReviewResultServer(String reviewName) {
        super(reviewName);
    }

    public static ReviewResult getReview(String review) {
        PARAMETERS.clear();
        PARAMETERS.put("reviewName", review);
        List r = DataBaseManager.namedQuery("ReviewResult.findByReviewName",
                PARAMETERS);
        if (r.isEmpty()) {
            return null;
        } else {
            return (ReviewResult) r.get(0);
        }
    }

    @Override
    public int write2DB() throws Exception {
        ReviewResultJpaController c
                = new ReviewResultJpaController(DataBaseManager
                        .getEntityManagerFactory());
        if (getId() == null) {
            ReviewResult rr = new ReviewResult();
            update(rr, this);
            c.create(rr);
            update(this, rr);
        } else {
            ReviewResult rr = getEntity();
            update(rr, this);
            c.edit(rr);
            update(this, rr);
        }
        return getId();
    }

    @Override
    public ReviewResult getEntity() {
        ReviewResultJpaController c
                = new ReviewResultJpaController(DataBaseManager
                        .getEntityManagerFactory());
        return c.findReviewResult(getId());
    }

    @Override
    public void update(ReviewResult target, ReviewResult source) {
        target.setExecutionStepList(source.getExecutionStepList());
        target.setId(source.getId());
        target.setReviewName(source.getReviewName());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
