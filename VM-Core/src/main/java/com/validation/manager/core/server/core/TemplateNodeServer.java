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
import com.validation.manager.core.db.Template;
import com.validation.manager.core.db.TemplateNode;
import com.validation.manager.core.db.TemplateNodePK;
import com.validation.manager.core.db.TemplateNodeType;
import com.validation.manager.core.db.controller.TemplateNodeJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class TemplateNodeServer extends TemplateNode
        implements EntityServer<TemplateNode> {

    public TemplateNodeServer() {
    }

    public TemplateNodeServer(TemplateNodePK templateNodePK) {
        super(templateNodePK);
        if (templateNodePK != null) {
            update();
        }
    }

    public TemplateNodeServer(TemplateNodePK templateNodePK, String nodeName) {
        super(templateNodePK, nodeName);
    }

    public TemplateNodeServer(int templateId, int templateNodeTypeId) {
        super(templateId, templateNodeTypeId);
    }

    public TemplateNodeServer(String name, TemplateNodeType type, Template t) {
        setNodeName(name);
        setTemplateNodeType(type);
        setTemplate(t);
        setTemplateNodePK(new TemplateNodePK(t.getId(), type.getId()));
    }

    @Override
    public int write2DB() throws Exception {
        TemplateNodeJpaController c
                = new TemplateNodeJpaController(DataBaseManager
                        .getEntityManagerFactory());
        if (getTemplateNodePK().getId() >= 0) {
            TemplateNode node = new TemplateNode();
            update(node, this);
            c.create(node);
            setTemplateNodePK(node.getTemplateNodePK());
        } else {
            TemplateNode node = getEntity();
            update(node, this);
            c.edit(node);
        }
        update();
        return getTemplateNodePK().getId();
    }

    @Override
    public TemplateNode getEntity() {
        return new TemplateNodeJpaController(DataBaseManager
                .getEntityManagerFactory())
                .findTemplateNode(getTemplateNodePK());
    }

    @Override
    public void update(TemplateNode target, TemplateNode source) {
        target.setNodeName(source.getNodeName());
        target.setTemplate(source.getTemplate());
        target.setTemplateNode(source.getTemplateNode());
        target.setTemplateNodeList(source.getTemplateNodeList());
        target.setTemplateNodePK(source.getTemplateNodePK());
        target.setTemplateNodeType(source.getTemplateNodeType());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    public static void delete(TemplateNode n) throws NonexistentEntityException {
        assert n != null : "Null parameter!";
        assert n instanceof TemplateNode :
                "Sent a non Entity class as parameter: " + n.getClass();
        if (n.getTemplateNodeList() != null) {
            n.getTemplateNodeList().forEach(sn -> {
                try {
                    delete(sn);
                }
                catch (NonexistentEntityException ex) {
                    Exceptions.printStackTrace(ex);
                }
            });
            n.getTemplateNodeList().clear();
        }
        new TemplateNodeJpaController(DataBaseManager
                .getEntityManagerFactory()).destroy(n.getTemplateNodePK());
    }
}
