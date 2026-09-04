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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.FlowWizard;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.FlowWizardStep;
import static net.sourceforge.javydreamercsw.validation.manager.web.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.ProjectType;
import com.validation.manager.core.db.Template;
import com.validation.manager.core.db.TemplateNode;
import com.validation.manager.core.db.TemplateNodePK;
import com.validation.manager.core.db.controller.ProjectTypeJpaController;
import com.validation.manager.core.db.controller.TemplateNodeJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.TemplateNodeServer;
import java.util.HashMap;
import java.util.Map;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class TemplateComponent extends VerticalLayout {

    private final Template template;
    private final boolean edit;
    private TreeGrid<Object> tree;
    //The template is the root item, template nodes are keyed by their PK.
    private final TreeData<Object> treeData = new TreeData<>();
    private final Map<TemplateNodePK, TemplateNode> nodeIndex
            = new HashMap<>();
    private TreeDataProvider<Object> dataProvider;

    public TemplateComponent(Template t, boolean edit) {
        this.template = t;
        this.edit = edit;
        init();
    }

    public TemplateComponent(Template t, boolean edit, String caption) {
        this.template = t;
        this.edit = edit;
        add(new com.vaadin.flow.component.html.Span(caption));
        init();
    }

    private void init() {
        tree = new TreeGrid<>();
        treeData.addRootItems(getTemplate());
        if (getTemplate().getTemplateNodeList() != null) {
            getTemplate().getTemplateNodeList().forEach(node -> {
                if (node.getTemplateNode() == null) {
                    //Only root folders
                    addTemplateNode(node);
                }
            });
        }
        dataProvider = new TreeDataProvider<>(treeData);
        tree.setDataProvider(dataProvider);
        tree.addComponentHierarchyColumn(this::getCaptionCell)
                .setKey("caption");
        tree.expand(getTemplate());
        //Select item on right click as well (Flow's ContextMenu opens on
        //right click automatically).
        ContextMenu menu = new ContextMenu(tree);
        menu.setTarget(tree);
        if (edit) {
            menu.addItem(TRANSLATOR.translate("general.add.child"),
                    e -> {
                        displayChildCreationWizard();
                    });
            MenuItem delete = menu.addItem(
                    TRANSLATOR.translate("general.delete"),
                    e -> {
                        displayChildDeletionWizard();
                    });
            //Don't allow to delete the root node; close the menu when the
            //right-clicked row isn't a deletable template (v8 had an
            //opened-listener for this; Flow exposes addOpenedChangeListener).
            delete.setEnabled(tree.asSingleSelect().getValue() != null
                    && treeData.getParent(tree.asSingleSelect().getValue()) != null);
            menu.addOpenedChangeListener(event -> {
                if (menu.isOpened() && tree.asSingleSelect().getValue()
                        instanceof Template) {
                    Template t = (Template) tree.asSingleSelect().getValue();
                    if (t.getId() < 1_000) {
                        menu.close();
                    }
                }
            });
            //Don't allow to delete the root node.
            delete.setEnabled(treeData.getParent(tree.asSingleSelect()
                    .getValue()) != null);
        }
        com.vaadin.flow.data.binder.Binder<Template> binder
                = new com.vaadin.flow.data.binder.Binder<>(Template.class);
        binder.setBean(getTemplate());
        TextField nameField
                = new TextField(TRANSLATOR.translate("general.name"));
        binder.forField(nameField)
                .bind("templateName");
        nameField.addValueChangeListener(listener -> {
            getTemplate().setTemplateName(nameField.getValue());
        });
        com.vaadin.flow.component.combobox.ComboBox<ProjectType> type
                = new com.vaadin.flow.component.combobox.ComboBox<>(TRANSLATOR
                        .translate("general.type"));
        type.setItems(new ProjectTypeJpaController(DataBaseManager
                .getEntityManagerFactory())
                .findProjectTypeEntities());
        type.setItemLabelGenerator(id
                -> TRANSLATOR.translate(id.getTypeName()));
        type.addValueChangeListener(listener -> {
            if (type.getValue() != null) {
                getTemplate().setProjectTypeId(type.getValue());
            }
        });
        binder.bind(type, "projectTypeId");
        VerticalLayout vl = new VerticalLayout();
        vl.add(nameField, type);
        if (template.getId() != null) {
            vl.add(tree);
        }
        binder.setReadOnly(!edit);
        add(vl);
    }

    private Component getCaptionCell(Object item) {
        return new com.vaadin.flow.component.html.Span(getCaptionFor(item));
    }

    private String getCaptionFor(Object item) {
        if (item instanceof Template) {
            return ((Template) item).getTemplateName();
        } else if (item instanceof TemplateNodePK) {
            TemplateNode node = nodeIndex.get((TemplateNodePK) item);
            return node == null ? String.valueOf(item)
                    : TRANSLATOR.translate(node.getNodeName());
        }
        return String.valueOf(item);
    }

    private VaadinIcon getIconFor(Object item) {
        if (item instanceof Template) {
            return VaadinIcon.FILE_TREE;
        } else if (item instanceof TemplateNodePK) {
            TemplateNode node = nodeIndex.get((TemplateNodePK) item);
            if (node == null || node.getTemplateNodeType() == null) {
                return VaadinIcon.FOLDER;
            }
            switch (node.getTemplateNodeType().getId()) {
                case 1://Requirement
                    return VaadinIcon.PIN;
                case 2://Test Plan
                    return VaadinIcon.BULLETS;
                case 3://Just a folder
                    return VaadinIcon.FOLDER;
                case 4://Risk Management
                    return VaadinIcon.EYE;
                default://Folder by default
                    return VaadinIcon.FOLDER;
            }
        }
        return null;
    }

    private void displayChildCreationWizard() {
        FlowWizard w = new FlowWizard();
        VMWindow cw = new VMWindow();
        TemplateNodeComponent tc
                = new TemplateNodeComponent(new TemplateNode(), true);
        w.addStep(new FlowWizardStep() {

            @Override
            public String getCaption() {
                return TRANSLATOR.translate("general.add.child");
            }

            @Override
            public Component getContent() {
                return tc;
            }

            @Override
            public boolean onAdvance() {
                return tc.isValid();
            }

            @Override
            public boolean onBack() {
                return false;
            }
        });
        w.addListener(new net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardProgressListener() {
            @Override
            public void activeStepChanged(
                    net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardStepActivationEvent event) {
                //Do nothing
            }

            @Override
            public void stepSetChanged(
                    net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardStepSetChangedEvent event) {
                //Do nothing
            }

            @Override
            public void stepCompleted(
                    net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardStepCompletionEvent event) {
                //Do nothing
            }

            @Override
            public void wizardCompleted(
                    net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardCompletedEvent event) {
                try {
                    //Add the item
                    TemplateNode tn = tc.getNode(), parent = null;
                    Template template = null;
                    if (tree.asSingleSelect().getValue() instanceof TemplateNodePK) {
                        TemplateNodeServer node
                                = new TemplateNodeServer((TemplateNodePK) tree
                                        .asSingleSelect().getValue());
                        template = node.getTemplate();
                        parent = node.getEntity();
                    } else if (tree.asSingleSelect().getValue() instanceof Template) {
                        template = (Template) tree.asSingleSelect().getValue();
                    }
                    tn.setTemplate(template);
                    if (parent != null) {
                        tn.setTemplateNode(parent);
                    }
                    new TemplateNodeJpaController(DataBaseManager
                            .getEntityManagerFactory()).create(tn);
                    addTemplateNode(tn);
                    dataProvider.refreshAll();
                    cw.close();
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

            @Override
            public void wizardCancelled(
                    net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardCancelledEvent event) {
                cw.close();
            }
        });
        cw.add(w);
        cw.open();
    }

    private void addTemplateNode(TemplateNode node) {
        Object key = node.getTemplateNodePK();
        nodeIndex.put((TemplateNodePK) key, node);
        //Parent is set on insertion; children presence implies children allowed
        treeData.addItem(node.getTemplateNode() != null
                ? node.getTemplateNode().getTemplateNodePK()
                : node.getTemplate(),
                key);
        if (node.getTemplateNodeList() != null) {
            node.getTemplateNodeList().forEach(sub -> {
                addTemplateNode(sub);
            });
        }
    }

    private void displayChildDeletionWizard() {
        ConfirmDialog prompt = new ConfirmDialog();
        prompt.setHeader(TRANSLATOR.translate("general.delete.child"));
        prompt.setText(TRANSLATOR.translate("template.delete.message"));
        prompt.setConfirmButton(TRANSLATOR.translate("general.yes"), (e) -> {
            try {
                TemplateNodeServer node
                        = new TemplateNodeServer((TemplateNodePK) tree
                                .asSingleSelect().getValue());
                TemplateNodeServer.delete(node.getEntity());
                //Removes the node and its children
                treeData.removeItem(node.getTemplateNodePK());
                dataProvider.refreshAll();
            } catch (NonexistentEntityException ex) {
                Exceptions.printStackTrace(ex);
            }
        });
        prompt.setCancelable(true);
        prompt.setCancelButton(TRANSLATOR.translate("general.no"), (e) -> {
            //Nothing to do
        });
        prompt.open();
    }

    /**
     * @return the template
     */
    public Template getTemplate() {
        return template;
    }
}
