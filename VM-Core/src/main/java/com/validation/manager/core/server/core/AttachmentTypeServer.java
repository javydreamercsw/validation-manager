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
import com.validation.manager.core.db.AttachmentType;
import com.validation.manager.core.db.controller.AttachmentTypeJpaController;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class AttachmentTypeServer extends AttachmentType
        implements EntityServer<AttachmentType> {

    private final AttachmentTypeJpaController c
            = new AttachmentTypeJpaController(DataBaseManager
                    .getEntityManagerFactory());

    public AttachmentTypeServer(String type) {
        super(type);
    }

    public AttachmentTypeServer(int id) {
        setId(id);
        update();
    }

    @Override
    public int write2DB() throws Exception {
        if (getId() == null) {
            AttachmentType at = new AttachmentType();
            update(at, this);
            c.create(at);
            update(this, at);
        } else {
            AttachmentType at = getEntity();
            update(at, this);
            c.edit(at);
            update(this, at);
        }
        return getId();
    }

    @Override
    public AttachmentType getEntity() {
        return c.findAttachmentType(getId());
    }

    @Override
    public void update(AttachmentType target, AttachmentType source) {
        target.setAttachmentList(source.getAttachmentList());
        target.setDescription(source.getDescription());
        target.setType(source.getType());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    public static AttachmentType getTypeForExtension(String ext) {
        AttachmentType at = null;
        //Set attachment type
        for (AttachmentType type
                : new AttachmentTypeJpaController(DataBaseManager
                        .getEntityManagerFactory())
                        .findAttachmentTypeEntities()) {
            if (type.getType().equals(ext)) {
                at = type;
                break;
            }
        }
        return at;
    }
}
