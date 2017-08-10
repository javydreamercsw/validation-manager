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
package net.sourceforge.javydreamercsw.validation.manager.web.component;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import static com.validation.manager.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.TemplateNode;
import com.validation.manager.core.db.TemplateNodeType;
import com.validation.manager.core.db.controller.TemplateNodeTypeJpaController;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class TemplateNodeComponent extends Panel {

    private final TemplateNode node;
    private final boolean edit;
    private ListSelect type;
    private TextField name;

    public TemplateNodeComponent(TemplateNode node, boolean edit) {
        this.node = node;
        this.edit = edit;
        init();
    }

    public TemplateNodeComponent(TemplateNode node, boolean edit, String caption) {
        super(caption);
        this.node = node;
        this.edit = edit;
        init();
    }

    private void init() {
        type = new ListSelect(TRANSLATOR.translate("general.type"));
        name = new TextField(TRANSLATOR.translate("general.name"));
        VerticalLayout vl = new VerticalLayout();
        BeanFieldGroup binder = new BeanFieldGroup(getNode().getClass());
        binder.setItemDataSource(getNode());
        binder.bind(type, "templateNodeType");
        BeanItemContainer<TemplateNodeType> container
                = new BeanItemContainer<>(TemplateNodeType.class,
                        new TemplateNodeTypeJpaController(DataBaseManager
                                .getEntityManagerFactory())
                                .findTemplateNodeTypeEntities());
        type.setContainerDataSource(container);
        type.getItemIds().forEach(id -> {
            TemplateNodeType temp = ((TemplateNodeType) id);
            type.setItemCaption(id,
                    TRANSLATOR.translate(temp.getTypeName()));
        });
        type.setNullSelectionAllowed(false);
        type.addValueChangeListener(listener -> {
            getNode().setTemplateNodeType((TemplateNodeType) type.getValue());
        });
        name = (TextField) binder.buildAndBind(TRANSLATOR
                .translate("general.name"),
                "nodeName", TextField.class);
        name.setNullRepresentation("");
        name.addValueChangeListener(listener -> {
            getNode().setNodeName(name.getValue());
        });
        vl.addComponent(name);
        vl.addComponent(type);
        binder.setReadOnly(!edit);
        setContent(vl);
    }

    public boolean isValid() {
        return type.getValue() != null
                && name.getValue() != null
                && !name.getValue().isEmpty();
    }

    /**
     * @return the node
     */
    protected TemplateNode getNode() {
        return node;
    }
}
