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
import com.validation.manager.core.db.HistoryField;
import com.validation.manager.core.db.HistoryFieldPK;
import com.validation.manager.core.db.controller.HistoryFieldJpaController;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class HistoryFieldServer extends HistoryField
        implements EntityServer<HistoryField> {

    public HistoryFieldServer(HistoryFieldPK historyFieldPK) {
        super(historyFieldPK);
    }

    public HistoryFieldServer(HistoryFieldPK historyFieldPK, String fieldName,
            String fieldValue) {
        super(historyFieldPK, fieldName, fieldValue);
    }

    public HistoryFieldServer(int fieldTypeId, int historyId) {
        super(fieldTypeId, historyId);
    }

    @Override
    public int write2DB() throws Exception {
        HistoryFieldJpaController c = new HistoryFieldJpaController(DataBaseManager
                .getEntityManagerFactory());
        if (getHistoryFieldPK().getId() == 0) {
            HistoryField hf = new HistoryField();
            update(hf, this);
            c.create(hf);
            setHistoryFieldPK(hf.getHistoryFieldPK());
        } else {
            HistoryField hf = getEntity();
            update(hf, this);
            c.edit(hf);
            setHistoryFieldPK(hf.getHistoryFieldPK());
        }
        update();
        return getHistoryFieldPK().getId();
    }

    @Override
    public HistoryField getEntity() {
        return new HistoryFieldJpaController(DataBaseManager
                .getEntityManagerFactory()).findHistoryField(getHistoryFieldPK());
    }

    @Override
    public void update(HistoryField target, HistoryField source) {
        target.setFieldName(source.getFieldName());
        target.setFieldType(source.getFieldType());
        target.setFieldValue(source.getFieldValue());
        target.setHistory(source.getHistory());
        target.setHistoryFieldPK(source.getHistoryFieldPK());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
