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
import com.validation.manager.core.db.Activity;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.ActivityJpaController;
import com.validation.manager.core.db.controller.ActivityTypeJpaController;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class ActivityServer extends Activity
        implements EntityServer<Activity> {

    public ActivityServer(int activityTypeId, Date activityTime,
            String description, VmUser user) {
        super(activityTypeId);
        setActivityType(new ActivityTypeJpaController(DataBaseManager
                .getEntityManagerFactory())
                .findActivityType(activityTypeId));
        setActivityTime(activityTime);
        setDescription(description);
        setSourceUser(user);
    }

    @Override
    public int write2DB() throws Exception {
        ActivityJpaController c
                = new ActivityJpaController(DataBaseManager
                        .getEntityManagerFactory());
        if (getActivityPK().getId() == 0) {
            Activity a = new Activity();
            update(a, this);
            c.create(a);
            setActivityPK(a.getActivityPK());
        } else {
            Activity a = getEntity();
            update(a, this);
            c.edit(a);
        }
        update();
        return getActivityPK().getId();
    }

    @Override
    public Activity getEntity() {
        return new ActivityJpaController(DataBaseManager
                .getEntityManagerFactory()).findActivity(getActivityPK());
    }

    @Override
    public void update(Activity target, Activity source) {
        target.setActivityPK(source.getActivityPK());
        target.setActivityTime(source.getActivityTime());
        target.setActivityType(source.getActivityType());
        target.setDescription(source.getDescription());
        target.setSourceUser(source.getSourceUser());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    public static List<Activity> getActivities() {
        return new ActivityJpaController(DataBaseManager
                .getEntityManagerFactory()).findActivityEntities();
    }
}
