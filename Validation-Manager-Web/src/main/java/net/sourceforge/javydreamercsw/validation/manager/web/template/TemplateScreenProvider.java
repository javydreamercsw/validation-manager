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
package net.sourceforge.javydreamercsw.validation.manager.web.template;

import com.vaadin.addon.contextmenu.ContextMenu;
import com.vaadin.addon.contextmenu.MenuItem;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Resource;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.IMainContentProvider;
import com.validation.manager.core.VMUI;
import static com.validation.manager.core.VMUI.PROJECT_ICON;
import com.validation.manager.core.db.Template;
import com.validation.manager.core.db.TemplateNode;
import com.validation.manager.core.db.TemplateNodePK;
import com.validation.manager.core.db.TemplateNodeType;
import com.validation.manager.core.db.controller.TemplateJpaController;
import com.validation.manager.core.db.controller.TemplateNodeJpaController;
import com.validation.manager.core.db.controller.TemplateNodeTypeJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.TemplateNodeServer;
import net.sourceforge.javydreamercsw.validation.manager.web.admin.AdminProvider;
import net.sourceforge.javydreamercsw.validation.manager.web.component.VMWindow;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;
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
@ServiceProvider(service = IMainContentProvider.class, position = 6)
public class TemplateScreenProvider extends AdminProvider {

    private final Tree tree;
    private final ListSelect type
            = new ListSelect(TRANSLATOR.translate("general.type"));
    private TextField name;

    @Override
    public String getComponentCaption() {
        return "template.tab.name";
    }

    public TemplateScreenProvider() {
        tree = new Tree();
        //Select item on right click as well
        tree.addItemClickListener((ItemClickEvent event) -> {
            if (event.getSource() == tree
                    && event.getButton() == MouseButton.RIGHT) {
                if (event.getItem() != null) {
                    Item clicked = event.getItem();
                    tree.select(event.getItemId());
                }
            }
        });
        //Add context menu
        ContextMenu menu = new ContextMenu(tree, true);
        tree.addItemClickListener((ItemClickEvent event) -> {
            if (event.getButton() == MouseButton.RIGHT) {
                menu.removeItems();
                if (tree.getValue() != null && !tree.isRoot(tree.getValue())) {
                    MenuItem create
                            = menu.addItem(TRANSLATOR.translate("general.add.child"),
                                    PROJECT_ICON, (MenuItem selectedItem) -> {
                                        displayCreationWizard();
                                    });
                    MenuItem delete
                            = menu.addItem(TRANSLATOR.translate("general.delete"),
                                    PROJECT_ICON, (MenuItem selectedItem) -> {
                                        displayDeletionWizard();
                                    });
                }
            }
        });
    }

    @Override
    public Component getContent() {
        Panel p = new Panel();
        HorizontalSplitPanel hs = new HorizontalSplitPanel();
        hs.setSplitPosition(25, Sizeable.Unit.PERCENTAGE);
        hs.addComponent(getLeftComponent());
        hs.addComponent(getRightComponent());
        hs.setSizeFull();
        p.setContent(hs);
        return p;
    }

    private Component getLeftComponent() {
        Panel p = new Panel();
        VerticalLayout layout = new VerticalLayout();
        ListSelect templates
                = new ListSelect(TRANSLATOR.translate("template.tab.list.name"));
        templates.setNullSelectionAllowed(true);
        BeanItemContainer<Template> container
                = new BeanItemContainer<>(Template.class,
                        new TemplateJpaController(DataBaseManager
                                .getEntityManagerFactory())
                                .findTemplateEntities());
        templates.setContainerDataSource(container);
        templates.getItemIds().forEach(id -> {
            Template temp = ((Template) id);
            templates.setItemCaption(id,
                    TRANSLATOR.translate(temp.getTemplateName()));
        });
        templates.addValueChangeListener(event -> {
            displayTemplate((Template) event.getProperty().getValue());
        });
        templates.setNullSelectionAllowed(false);
        layout.addComponent(templates);
        layout.setSizeFull();
        p.setContent(layout);
        p.setSizeFull();
        return p;
    }

    private void displayTemplate(Template t) {
        //Clear the tree to create a new one.
        tree.removeAllItems();
        if (t != null) {
            tree.addItem(t.getTemplateName());
            tree.setItemIcon(t.getTemplateName(), VaadinIcons.FILE_TREE);
            t.getTemplateNodeList().forEach(node -> {
                if (node.getTemplateNode() == null) {
                    //Only root folders
                    addTemplateNode(node);
                }
            });
        }
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
                    node.getTemplate().getTemplateName());
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

    private Component getRightComponent() {
        Panel p = new Panel();
        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(tree);
        p.setContent(layout);
        p.setSizeFull();
        return p;
    }

    private void displayDeletionWizard() {
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
    }

    private void displayCreationWizard() {
        Wizard w = new Wizard();
        Window cw = new VMWindow();
        w.addStep(new WizardStep() {
            @Override
            public String getCaption() {
                return TRANSLATOR.translate("general.add.child");
            }

            @Override
            public Component getContent() {
                VerticalLayout vl = new VerticalLayout();
                TemplateNode t = new TemplateNode();
                BeanFieldGroup binder = new BeanFieldGroup(t.getClass());
                binder.setItemDataSource(t);
                binder.bind(type, "templateNodeType");
                type.setNullSelectionAllowed(true);
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
                name = (TextField) binder.buildAndBind(TRANSLATOR
                        .translate("general.name"),
                        "nodeName", TextField.class);
                vl.addComponent(name);
                vl.addComponent(type);
                return vl;
            }

            @Override
            public boolean onAdvance() {
                return type.getValue() != null
                        && name.getValue() != null
                        && !name.getValue().isEmpty();
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
                    TemplateNode tn = new TemplateNode();
                    TemplateNodeServer node
                            = new TemplateNodeServer((TemplateNodePK) tree.getValue());
                    tn.setTemplate(node.getTemplate());
                    tn.setTemplateNode(node.getEntity());
                    tn.setTemplateNodeType((TemplateNodeType) type.getValue());
                    tn.setNodeName(name.getValue());
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
}
