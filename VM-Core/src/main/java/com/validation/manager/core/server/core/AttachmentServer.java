package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Attachment;
import com.validation.manager.core.db.AttachmentPK;
import com.validation.manager.core.db.AttachmentType;
import com.validation.manager.core.db.controller.AttachmentJpaController;
import com.validation.manager.core.db.controller.AttachmentTypeJpaController;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class AttachmentServer extends Attachment
        implements EntityServer<Attachment> {

    private final AttachmentJpaController c
            = new AttachmentJpaController(DataBaseManager.getEntityManagerFactory());

    public AttachmentServer(AttachmentPK attachmentPK) {
        super(attachmentPK);
        update();
    }

    public AttachmentServer() {
        super();
    }

    @Override
    public int write2DB() throws Exception {
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
        return getAttachmentPK().getId();
    }

    @Override
    public Attachment getEntity() {
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

    public void addFile(File f) {
        try {
            if (f != null && f.isFile() && f.exists()) {
                byte[] array = FileUtils.readFileToByteArray(f);
                setFile(array);
                setFileName(f.getName());
                String ext = FilenameUtils.getExtension(getFileName());
                if (ext != null) {
                    //Set attachment type
                    for (AttachmentType type
                            : new AttachmentTypeJpaController(DataBaseManager
                                    .getEntityManagerFactory())
                                    .findAttachmentTypeEntities()) {
                        if (type.getType().equals(ext)) {
                            setAttachmentType(type);
                            break;
                        }
                    }
                }
                if (getAttachmentType() == null) {
                    //Set as undefined
                    setAttachmentType(new AttachmentTypeJpaController(DataBaseManager
                            .getEntityManagerFactory()).findAttachmentType(9));
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public File getAttachedFile() {
        File result;
        try {
            result = File.createTempFile("temp", "vm");
            result.deleteOnExit();
            FileUtils.writeByteArrayToFile(result, getFile());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            result = null;
        }
        return result;
    }
}
