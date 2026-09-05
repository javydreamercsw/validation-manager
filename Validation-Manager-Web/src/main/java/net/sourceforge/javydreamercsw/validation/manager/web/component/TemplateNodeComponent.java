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

import static net.sourceforge.javydreamercsw.validation.manager.web.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.TemplateNode;
import com.validation.manager.core.db.TemplateNodeType;
import com.validation.manager.core.db.controller.TemplateNodeTypeJpaController;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class TemplateNodeComponent extends VerticalLayout {

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
        this.node = node;
        this.edit = edit;
        type = new ComboBox<>(TRANSLATOR.translate("general.type"));
        name = new TextField(TRANSLATOR.translate("general.name"));
        add(new com.vaadin.flow.component.html.Span(caption));
        init();
    }

    private void init() {
        VerticalLayout vl = new VerticalLayout();
        Binder<TemplateNode> binder = new Binder<>(TemplateNode.class);
        binder.setBean(getNode());
        type.setItems(new TemplateNodeTypeJpaController(DataBaseManager
                .getEntityManagerFactory())
                .findTemplateNodeTypeEntities());
        type.setItemLabelGenerator(temp
                -> TRANSLATOR.translate(temp.getTypeName()));
        type.setAllowCustomValue(false);
        type.setValue(getNode().getTemplateNodeType());
        type.addValueChangeListener(listener -> {
            getNode().setTemplateNodeType(type.getValue());
        });
        binder.bind(type, "templateNodeType");
        binder.forField(name)
                .bind("nodeName");
        name.addValueChangeListener(listener -> {
            getNode().setNodeName(name.getValue());
        });
        vl.add(name, type);
        binder.setReadOnly(!edit);
        add(vl);
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
