package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.AttachmentType;
import com.validation.manager.core.db.controller.AttachmentTypeJpaController;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class AttachmentTypeServer extends AttachmentType
        implements EntityServer<AttachmentType> {

    private final AttachmentTypeJpaController c
            = new AttachmentTypeJpaController(DataBaseManager
                    .getEntityManagerFactory());

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
