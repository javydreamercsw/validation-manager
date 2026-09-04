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

import com.vaadin.data.Binder;
import com.vaadin.ui.ComboBox;
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
    private final ComboBox<TemplateNodeType> type;
    private final TextField name;

    public TemplateNodeComponent(TemplateNode node, boolean edit) {
        this.node = node;
        this.edit = edit;
        type = new ComboBox<>(TRANSLATOR.translate("general.type"));
        name = new TextField(TRANSLATOR.translate("general.name"));
        init();
    }

    public TemplateNodeComponent(TemplateNode node, boolean edit, String caption) {
        super(caption);
        this.node = node;
        this.edit = edit;
        type = new ComboBox<>(TRANSLATOR.translate("general.type"));
        name = new TextField(TRANSLATOR.translate("general.name"));
        init();
    }

    private void init() {
        VerticalLayout vl = new VerticalLayout();
        Binder<TemplateNode> binder = new Binder<>(TemplateNode.class);
        binder.setBean(getNode());
        type.setItems(new TemplateNodeTypeJpaController(DataBaseManager
                .getEntityManagerFactory())
                .findTemplateNodeTypeEntities());
        type.setItemCaptionGenerator(temp
                -> TRANSLATOR.translate(temp.getTypeName()));
        type.setEmptySelectionAllowed(false);
        type.setValue(getNode().getTemplateNodeType());
        type.addValueChangeListener(listener -> {
            getNode().setTemplateNodeType(type.getValue());
        });
        binder.bind(type, "templateNodeType");
        binder.forField(name)
                .withNullRepresentation("")
                .bind("nodeName");
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
