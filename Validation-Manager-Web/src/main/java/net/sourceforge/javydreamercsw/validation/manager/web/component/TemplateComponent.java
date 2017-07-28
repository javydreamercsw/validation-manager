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

import com.vaadin.addon.contextmenu.ContextMenu;
import com.vaadin.addon.contextmenu.MenuItem;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Resource;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import static com.validation.manager.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMUI;
import static com.validation.manager.core.VMUI.PROJECT_ICON;
import com.validation.manager.core.db.ProjectType;
import com.validation.manager.core.db.Template;
import com.validation.manager.core.db.TemplateNode;
import com.validation.manager.core.db.TemplateNodePK;
import com.validation.manager.core.db.controller.ProjectTypeJpaController;
import com.validation.manager.core.db.controller.TemplateNodeJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.TemplateNodeServer;
import de.steinwedel.messagebox.ButtonOption;
import de.steinwedel.messagebox.MessageBox;
import org.openide.util.Exceptions;
import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.WizardStep;
import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class TemplateComponent extends Panel {

    private final Template template;
    private final boolean edit;
    private Tree tree;

    public TemplateComponent(Template t, boolean edit) {
        super(TRANSLATOR.translate("general.template"));
        this.template = t;
        this.edit = edit;
        init();
    }

    public TemplateComponent(Template t, boolean edit, String caption) {
        super(caption);
        this.template = t;
        this.edit = edit;
        init();
    }

    private void init() {
        tree = new Tree();
        tree.addItem(getTemplate());
        tree.setItemIcon(getTemplate(), VaadinIcons.FILE_TREE);
        tree.setItemCaption(getTemplate(), getTemplate().getTemplateName());
        if (getTemplate().getTemplateNodeList() != null) {
            getTemplate().getTemplateNodeList().forEach(node -> {
                if (node.getTemplateNode() == null) {
                    //Only root folders
                    addTemplateNode(node);
                }
            });
        }
        //Select item on right click as well
        tree.addItemClickListener((ItemClickEvent event) -> {
            if (event.getSource() == tree
                    && event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
                if (event.getItem() != null) {
                    Item clicked = event.getItem();
                    tree.select(event.getItemId());
                }
            }
        });
        //Add context menu
        ContextMenu menu = new ContextMenu(tree, true);
        if (edit) {
            tree.addItemClickListener((ItemClickEvent event) -> {
                if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
                    menu.removeItems();
                    if (tree.getValue() != null) {
                        if (tree.getValue() instanceof Template) {
                            Template t = (Template) tree.getValue();
                            if (t.getId() < 1_000) {
                                return;
                            }
                        }
                        MenuItem create
                                = menu.addItem(TRANSLATOR.translate("general.add.child"),
                                        PROJECT_ICON, (MenuItem selectedItem) -> {
                                            displayChildCreationWizard();
                                        });
                        MenuItem delete
                                = menu.addItem(TRANSLATOR.translate("general.delete"),
                                        PROJECT_ICON, (MenuItem selectedItem) -> {
                                            displayChildDeletionWizard();
                                        });
                        //Don't allow to delete the root node.
                        delete.setEnabled(!tree.isRoot(tree.getValue()));
                    }
                }
            });
        }
        TextField nameField
                = new TextField(TRANSLATOR.translate("general.name"));
        VerticalLayout vl = new VerticalLayout();
        BeanFieldGroup binder = new BeanFieldGroup(getTemplate().getClass());
        binder.setItemDataSource(getTemplate());
        binder.bind(nameField, "templateName");
        nameField.addValueChangeListener(listener -> {
            getTemplate().setTemplateName(nameField.getValue());
        });
        nameField.setNullRepresentation("");
        ComboBox type = new ComboBox(TRANSLATOR.translate("general.type"));
        BeanItemContainer<ProjectType> container
                = new BeanItemContainer<>(ProjectType.class,
                        new ProjectTypeJpaController(DataBaseManager
                                .getEntityManagerFactory())
                                .findProjectTypeEntities());
        type.setContainerDataSource(container);
        for (Object o : type.getItemIds()) {
            ProjectType id = ((ProjectType) o);
            type.setItemCaption(id, TRANSLATOR.translate(id.getTypeName()));
        }
        type.addValueChangeListener(listener -> {
            if (type.getValue() != null) {
                getTemplate().setProjectTypeId((ProjectType) type.getValue());
            }
        });
        binder.bind(type, "projectTypeId");
        vl.addComponent(nameField);
        vl.addComponent(type);
        if (template.getId() != null) {
            vl.addComponent(tree);
        }
        binder.setReadOnly(!edit);
        setContent(vl);
    }

    private void displayChildCreationWizard() {
        Wizard w = new Wizard();
        Window cw = new VMWindow();
        TemplateNodeComponent tc
                = new TemplateNodeComponent(new TemplateNode(), true);
        w.addStep(new WizardStep() {

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
        w.addListener(new WizardProgressListener() {
            @Override
            public void activeStepChanged(WizardStepActivationEvent event) {
                //Do nothing
            }

            @Override
            public void stepSetChanged(WizardStepSetChangedEvent event) {
                //Do nothing
            }

            @Override
            public void wizardCompleted(WizardCompletedEvent event) {
                try {
                    //Add the item
                    TemplateNode tn = tc.getNode(), parent = null;
                    Template template = null;
                    if (tree.getValue() instanceof TemplateNodePK) {
                        TemplateNodeServer node
                                = new TemplateNodeServer((TemplateNodePK) tree.getValue());
                        template = node.getTemplate();
                        parent = node.getEntity();
                    } else if (tree.getValue() instanceof Template) {
                        template = (Template) tree.getValue();
                    }
                    tn.setTemplate(template);
                    if (parent != null) {
                        tn.setTemplateNode(parent);
                    }
                    new TemplateNodeJpaController(DataBaseManager
                            .getEntityManagerFactory()).create(tn);
                    addTemplateNode(tn);
                    UI.getCurrent().removeWindow(cw);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

            @Override
            public void wizardCancelled(WizardCancelledEvent event) {
                UI.getCurrent().removeWindow(cw);
            }
        });
        cw.setContent(w);
        cw.setSizeFull();
        UI.getCurrent().addWindow(cw);
    }

    private void addTemplateNode(TemplateNode node) {
        Object key = node.getTemplateNodePK();
        tree.addItem(key);
        tree.setItemCaption(key,
                TRANSLATOR.translate(node.getNodeName()));
        tree.setChildrenAllowed(key,
                !node.getTemplateNodeList().isEmpty());
        if (node.getTemplateNode() != null) {
            tree.setParent(key,
                    node.getTemplateNode().getTemplateNodePK());
        } else {
            tree.setParent(key,
                    node.getTemplate());
        }
        Resource icon;
        switch (node.getTemplateNodeType().getId()) {
            case 1://Requirement
                icon = VMUI.REQUIREMENT_ICON;
                break;
            case 2://Test Plan
                icon = VMUI.PLAN_ICON;
                break;
            case 3://Just a folder
                icon = VaadinIcons.FOLDER;
                break;
            case 4://Risk Management
                icon = VaadinIcons.EYE;
                break;
            default://Folder by default
                icon = VaadinIcons.FOLDER;
        }
        tree.setItemIcon(key, icon);
        node.getTemplateNodeList().forEach(sub -> {
            addTemplateNode(sub);
        });
    }

    private void displayChildDeletionWizard() {
        MessageBox prompt = MessageBox.createQuestion()
                .withCaption(TRANSLATOR.translate("general.delete.child"))
                .withMessage(TRANSLATOR.translate("template.delete.message"))
                .withYesButton(() -> {
                    try {
                        TemplateNodeServer node
                                = new TemplateNodeServer((TemplateNodePK) tree.getValue());
                        TemplateNodeServer.delete(node.getEntity());
                        tree.getChildren(tree.getValue()).forEach(child -> {
                            tree.removeItem(child);
                        });
                        tree.removeItem(node.getTemplateNodePK());
                    } catch (NonexistentEntityException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                },
                        ButtonOption.focus(),
                        ButtonOption
                                .icon(VaadinIcons.CHECK))
                .withNoButton(ButtonOption
                        .icon(VaadinIcons.CLOSE));
        prompt.getWindow().setIcon(VMUI.SMALL_APP_ICON);
        prompt.open();
    }

    /**
     * @return the template
     */
    public Template getTemplate() {
        return template;
    }
}
