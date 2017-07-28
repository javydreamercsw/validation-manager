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
import com.validation.manager.core.db.AttachmentPK;
import com.validation.manager.core.db.controller.AttachmentJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class AttachmentServer extends Attachment
        implements EntityServer<Attachment> {

    public AttachmentServer(AttachmentPK attachmentPK) {
        super(attachmentPK);
        update();
    }

    public AttachmentServer() {
        super();
    }

    @Override
    public int write2DB() throws VMException {
        try {
            AttachmentJpaController c
                    = new AttachmentJpaController(DataBaseManager.getEntityManagerFactory());
            if (getAttachmentPK() == null) {
                Attachment a = new Attachment();
                update(a, this);
                c.create(a);
                setAttachmentPK(a.getAttachmentPK());
                update();
            } else {
                Attachment a = new Attachment();
                update(a, this);
                c.edit(a);
                setAttachmentPK(a.getAttachmentPK());
                update();
            }
        }
        catch (Exception ex) {
            throw new VMException(ex);
        }
        return getAttachmentPK().getId();
    }

    @Override
    public Attachment getEntity() {
        AttachmentJpaController c
                = new AttachmentJpaController(DataBaseManager.getEntityManagerFactory());
        return c.findAttachment(getAttachmentPK());
    }

    @Override
    public void update(Attachment target, Attachment source) {
        target.setAttachmentPK(source.getAttachmentPK());
        target.setAttachmentType(source.getAttachmentType());
        target.setFileName(source.getFileName());
        target.setFile(source.getFile());
        target.setStringValue(source.getStringValue());
        target.setTextValue(source.getTextValue());
        target.setExecutionStepHasAttachmentList(source
                .getExecutionStepHasAttachmentList());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    public void addFile(File f, String fileName) {
        try {
            if (f != null && f.isFile() && f.exists()) {
                byte[] array = FileUtils.readFileToByteArray(f);
                setFile(array);
                setFileName(fileName);
                String ext = FilenameUtils.getExtension(getFileName());
                if (ext != null) {
                    //Set attachment type
                    setAttachmentType(AttachmentTypeServer.getTypeForExtension(ext));
                }
                if (getAttachmentType() == null) {
                    //Set as undefined
                    setAttachmentType(AttachmentTypeServer.getTypeForExtension(""));
                }
            }
        }
        catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public File getAttachedFile(String dest) {
        File result;
        try {
            result = new File(dest + File.separator + getFileName());
            result.deleteOnExit();
            FileUtils.writeByteArrayToFile(result, getFile());
        }
        catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            result = null;
        }
        return result;
    }

    public static void delete(Attachment entity)
            throws IllegalOrphanException, NonexistentEntityException,
            Exception {
        AttachmentJpaController c
                = new AttachmentJpaController(DataBaseManager.getEntityManagerFactory());
        c.destroy(entity.getAttachmentPK());
    }
}
