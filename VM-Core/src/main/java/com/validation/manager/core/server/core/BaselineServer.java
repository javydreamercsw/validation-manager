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
import com.validation.manager.core.db.Baseline;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.controller.BaselineJpaController;
import com.validation.manager.core.tool.Tool;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class BaselineServer extends Baseline
        implements EntityServer<Baseline> {

    private static final Logger LOG
            = Logger.getLogger(BaselineServer.class.getSimpleName());

    public BaselineServer() {
    }

    public BaselineServer(Date creationDate, String baselineName) {
        super(creationDate, baselineName);
    }

    public BaselineServer(Baseline b) {
        super.setId(b.getId());
        update();
    }

    public BaselineServer(int id) {
        super.setId(id);
        update();
    }

    @Override
    public int write2DB() throws Exception {
        BaselineJpaController c = new BaselineJpaController(DataBaseManager
                .getEntityManagerFactory());
        if (getId() == null) {
            Baseline b = new Baseline();
            update(b, this);
            c.create(b);
            setId(b.getId());
        } else {
            Baseline b = getEntity();
            update(b, this);
            c.edit(b);
        }
        update();
        return getId();
    }

    @Override
    public Baseline getEntity() {
        return new BaselineJpaController(DataBaseManager
                .getEntityManagerFactory()).findBaseline(getId());
    }

    @Override
    public void update(Baseline target, Baseline source) {
        target.setBaselineName(source.getBaselineName());
        target.setCreationDate(source.getCreationDate());
        target.setId(source.getId());
        target.setHistoryList(source.getHistoryList());
        target.setDescription(source.getDescription());
        target.setRequirementSpec(source.getRequirementSpec());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    public static BaselineServer createBaseline(String name, String desc,
            RequirementSpec spec) {
        BaselineServer b = new BaselineServer(new Date(), name);
        b.setDescription(desc);
        try {
            //Add requirements
            if (b.getHistoryList() == null) {
                b.setHistoryList(new ArrayList<>());
            }
            for (Requirement o : Tool.extractRequirements(spec)) {
                RequirementServer rs = new RequirementServer(o);
                if (rs.getMidVersion() > 0 || rs.getMinorVersion() > 0) {
                    rs.setReason("baseline.creation");
                    rs.increaseMajorVersion();
                }
                b.getHistoryList().add(rs.getHistoryList().get(rs
                        .getHistoryList().size() - 1));
            }
            b.setRequirementSpec(spec);
            b.write2DB();
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return b;
    }
}
