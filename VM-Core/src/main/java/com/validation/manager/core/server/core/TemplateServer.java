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
import com.validation.manager.core.db.Template;
import com.validation.manager.core.db.TemplateNode;
import com.validation.manager.core.db.TemplateNodeType;
import com.validation.manager.core.db.controller.TemplateJpaController;
import com.validation.manager.core.db.controller.TemplateNodeJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class TemplateServer extends Template
        implements EntityServer<Template> {

    private static final Logger LOG
            = Logger.getLogger(TemplateServer.class.getSimpleName());

    public TemplateServer() {
        setTemplateNodeList(new ArrayList<>());
    }

    public TemplateServer(String templateName) {
        setTemplateName(templateName);
        setTemplateNodeList(new ArrayList<>());
    }

    public TemplateServer(int id) {
        setId(id);
        update();
    }

    public TemplateServer(Template t) {
        setId(t.getId());
        if (getId() != null) {
            update();
        } else {
            update(TemplateServer.this, t);
        }
    }

    @Override
    public int write2DB() throws Exception {
        TemplateJpaController c
                = new TemplateJpaController(DataBaseManager
                        .getEntityManagerFactory());
        if (getId() == null) {
            Template t = new Template();
            update(t, this);
            c.create(t);
            setId(t.getId());
        } else {
            Template t = getEntity();
            update(t, this);
            c.edit(t);
        }
        update();
        return getId();
    }

    @Override
    public Template getEntity() {
        return new TemplateJpaController(DataBaseManager
                .getEntityManagerFactory()).findTemplate(getId());
    }

    @Override
    public void update(Template target, Template source) {
        target.setId(source.getId());
        target.setTemplateName(source.getTemplateName());
        target.setTemplateNodeList(source.getTemplateNodeList());
        target.setProjectTypeId(source.getProjectTypeId());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    public TemplateNode addNode(String name, TemplateNodeType type)
            throws Exception {
        TemplateNode node = new TemplateNode(getId(), type.getId());
        node.setNodeName(name);
        node.setTemplate(getEntity());
        node.setTemplateNodeType(type);
        new TemplateNodeJpaController(DataBaseManager
                .getEntityManagerFactory()).create(node);
        getTemplateNodeList().add(node);
        write2DB();
        return node;
    }

    public void addChildNode(TemplateNode parent, TemplateNode child,
            boolean persist) throws Exception {
        assert parent != null : "Null parent";
        assert child != null : "Null child";
        parent.getTemplateNodeList().add(child);
        if (persist) {
            TemplateNodeJpaController c
                    = new TemplateNodeJpaController(DataBaseManager
                            .getEntityManagerFactory());
            c.edit(parent);
            c.edit(child);
        } else {
            child.setTemplateNode(parent);
        }
    }

    public void delete() throws VMException {
        try {
            TemplateNodeJpaController c
                    = new TemplateNodeJpaController(DataBaseManager
                            .getEntityManagerFactory());
            getTemplateNodeList().forEach(node -> {
                try {
                    c.destroy(node.getTemplateNodePK());
                } catch (NonexistentEntityException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            });
            new TemplateJpaController(DataBaseManager
                    .getEntityManagerFactory()).destroy(getId());
        } catch (IllegalOrphanException | NonexistentEntityException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new VMException(ex);
        }
    }
}
