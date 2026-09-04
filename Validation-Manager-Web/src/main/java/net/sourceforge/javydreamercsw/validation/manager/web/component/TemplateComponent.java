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
import com.vaadin.data.Binder;
import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Resource;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.SingleSelect;
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
import java.util.HashMap;
import java.util.Map;
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
    private Tree<Object> tree;
    //The template is the root item, template nodes are keyed by their PK.
    private final TreeData<Object> treeData = new TreeData<>();
    private final Map<TemplateNodePK, TemplateNode> nodeIndex
            = new HashMap<>();
    private TreeDataProvider<Object> dataProvider;
    private SingleSelect<Object> selection;

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
        tree = new Tree<>();
        selection = tree.asSingleSelect();
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
        tree.setItemCaptionGenerator(this::getCaptionFor);
        tree.setItemIconGenerator(this::getIconFor);
        //Select item on right click as well
        tree.addItemClickListener((Tree.ItemClick<Object> event) -> {
            if (event.getSource() == tree
                    && event.getMouseEventDetails().getButton()
                    == MouseEventDetails.MouseButton.RIGHT) {
                if (event.getItem() != null) {
                    tree.select(event.getItem());
                }
            }
        });
        //Add context menu
        ContextMenu menu = new ContextMenu(tree, true);
        if (edit) {
            tree.addContextClickListener((event) -> {
                if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
                    menu.removeItems();
                    if (selection.getValue() != null) {
                        if (selection.getValue() instanceof Template) {
                            Template t = (Template) selection.getValue();
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
                        delete.setEnabled(treeData
                                .getParent(selection.getValue()) != null);
                    }
                }
            });
        }
        Binder<Template> binder = new Binder<>(Template.class);
        binder.setBean(getTemplate());
        TextField nameField
                = new TextField(TRANSLATOR.translate("general.name"));
        binder.forField(nameField)
                .withNullRepresentation("")
                .bind("templateName");
        nameField.addValueChangeListener(listener -> {
            getTemplate().setTemplateName(nameField.getValue());
        });
        ComboBox<ProjectType> type
                = new ComboBox<>(TRANSLATOR.translate("general.type"));
        type.setItems(new ProjectTypeJpaController(DataBaseManager
                .getEntityManagerFactory())
                .findProjectTypeEntities());
        type.setItemCaptionGenerator(id
                -> TRANSLATOR.translate(id.getTypeName()));
        type.addValueChangeListener(listener -> {
            if (type.getValue() != null) {
                getTemplate().setProjectTypeId(type.getValue());
            }
        });
        binder.bind(type, "projectTypeId");
        VerticalLayout vl = new VerticalLayout();
        vl.addComponent(nameField);
        vl.addComponent(type);
        if (template.getId() != null) {
            vl.addComponent(tree);
        }
        binder.setReadOnly(!edit);
        setContent(vl);
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

    private Resource getIconFor(Object item) {
        if (item instanceof Template) {
            return VaadinIcons.FILE_TREE;
        } else if (item instanceof TemplateNodePK) {
            TemplateNode node = nodeIndex.get((TemplateNodePK) item);
            if (node == null || node.getTemplateNodeType() == null) {
                return VaadinIcons.FOLDER;
            }
            switch (node.getTemplateNodeType().getId()) {
                case 1://Requirement
                    return VMUI.REQUIREMENT_ICON;
                case 2://Test Plan
                    return VMUI.PLAN_ICON;
                case 3://Just a folder
                    return VaadinIcons.FOLDER;
                case 4://Risk Management
                    return VaadinIcons.EYE;
                default://Folder by default
                    return VaadinIcons.FOLDER;
            }
        }
        return null;
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
                    if (selection.getValue() instanceof TemplateNodePK) {
                        TemplateNodeServer node
                                = new TemplateNodeServer((TemplateNodePK) selection.getValue());
                        template = node.getTemplate();
                        parent = node.getEntity();
                    } else if (selection.getValue() instanceof Template) {
                        template = (Template) selection.getValue();
                    }
                    tn.setTemplate(template);
                    if (parent != null) {
                        tn.setTemplateNode(parent);
                    }
                    new TemplateNodeJpaController(DataBaseManager
                            .getEntityManagerFactory()).create(tn);
                    addTemplateNode(tn);
                    dataProvider.refreshAll();
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
        MessageBox prompt = MessageBox.createQuestion()
                .withCaption(TRANSLATOR.translate("general.delete.child"))
                .withMessage(TRANSLATOR.translate("template.delete.message"))
                .withYesButton(() -> {
                    try {
                        TemplateNodeServer node
                                = new TemplateNodeServer((TemplateNodePK) selection.getValue());
                        TemplateNodeServer.delete(node.getEntity());
                        //Removes the node and its children
                        treeData.removeItem(node.getTemplateNodePK());
                        dataProvider.refreshAll();
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
