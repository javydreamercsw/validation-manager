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
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.IMainContentProvider;
import com.validation.manager.core.VMException;
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
import com.validation.manager.core.server.core.TemplateServer;
import de.steinwedel.messagebox.ButtonOption;
import de.steinwedel.messagebox.MessageBox;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private final ListSelect templates
            = new ListSelect(TRANSLATOR.translate("template.tab.list.name"));
    private TextField name;
    private static final Logger LOG
            = Logger.getLogger(TemplateScreenProvider.class.getSimpleName());

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
                if (tree.getValue() != null) {
                    if (tree.getValue() instanceof Template) {
                        Template template = (Template) tree.getValue();
                        if (template.getId() < 1_000) {
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

    @Override
    public Component getContent() {
        Panel p = new Panel();
        HorizontalSplitPanel hs = new HorizontalSplitPanel();
        hs.setSplitPosition(30, Sizeable.Unit.PERCENTAGE);
        hs.addComponent(getLeftComponent());
        hs.addComponent(getRightComponent());
        hs.setSizeFull();
        p.setContent(hs);
        p.setId(getComponentCaption());
        return p;
    }

    private Component getLeftComponent() {
        Panel p = new Panel();
        VerticalLayout layout = new VerticalLayout();
        templates.setNullSelectionAllowed(true);
        templates.setWidth(100, Sizeable.Unit.PERCENTAGE);
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
        templates.setWidth(100, Sizeable.Unit.PERCENTAGE);
        layout.addComponent(templates);
        HorizontalLayout hl = new HorizontalLayout();
        Button create = new Button(TRANSLATOR.translate("general.add"));
        create.addClickListener(listener -> {
            displayTemplateCreateWizard();
        });
        hl.addComponent(create);
        Button copy = new Button(TRANSLATOR.translate("general.copy"));
        copy.addClickListener(listener -> {
            displayTemplateCopyWizard();
        });
        hl.addComponent(copy);
        Button delete = new Button(TRANSLATOR.translate("general.delete"));
        delete.addClickListener(listener -> {
            displayTemplateDeleteWizard();
        });
        hl.addComponent(delete);
        templates.addValueChangeListener(listener -> {
            if (templates.getValue() != null) {
                Template t = (Template) templates.getValue();
                delete.setEnabled(t.getId() >= 1_000);
                copy.setEnabled(t.getTemplateNodeList().size() > 0);
            }
        });
        layout.addComponent(hl);
        layout.setSizeFull();
        p.setContent(layout);
        p.setSizeFull();
        return p;
    }

    private void displayTemplate(Template t) {
        //Clear the tree to create a new one.
        tree.removeAllItems();
        if (t != null) {
            tree.addItem(t);
            tree.setItemIcon(t, VaadinIcons.FILE_TREE);
            tree.setItemCaption(t, t.getTemplateName());
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

    private Component getRightComponent() {
        Panel p = new Panel();
        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(tree);
        p.setContent(layout);
        p.setSizeFull();
        return p;
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

    private void displayChildCreationWizard() {
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
                name.setNullRepresentation("");
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
                    TemplateNode tn = new TemplateNode(), parent = null;
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
                    tn.setTemplateNodeType((TemplateNodeType) type.getValue());
                    tn.setNodeName(name.getValue());
                    new TemplateNodeJpaController(DataBaseManager
                            .getEntityManagerFactory()).create(tn);
                    addTemplateNode(tn);
                    UI.getCurrent().removeWindow(cw);
                    ((VMUI) UI.getCurrent()).updateScreen();
                    ((VMUI) UI.getCurrent()).showTab(getComponentCaption());
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

    @Override
    public void update() {
        super.update();
        tree.removeAllItems();
    }

    private void displayTemplateCopyWizard() {
        Wizard w = new Wizard();
        Window cw = new VMWindow();
        w.addStep(new WizardStep() {
            private final TextField nameField
                    = new TextField(TRANSLATOR.translate("general.name"));

            @Override
            public String getCaption() {
                return TRANSLATOR.translate("template.copy");
            }

            @Override
            public Component getContent() {
                VerticalLayout vl = new VerticalLayout();
                Template t = new Template();
                BeanFieldGroup binder = new BeanFieldGroup(t.getClass());
                binder.setItemDataSource(t);
                binder.bind(tree, "templateName");
                vl.addComponent(nameField);
                return vl;
            }

            @Override
            public boolean onAdvance() {
                try {
                    TemplateServer t = new TemplateServer(nameField.getValue());
                    t.write2DB();
                    return t.getId() > 0;
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                    Notification.show(TRANSLATOR
                            .translate("general.error.record.creation"),
                            ex.getLocalizedMessage(),
                            Notification.Type.ERROR_MESSAGE);
                }
                return false;
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
                ((VMUI) UI.getCurrent()).updateScreen();
                ((VMUI) UI.getCurrent()).showTab(getComponentCaption());
                UI.getCurrent().removeWindow(cw);
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

    private void displayTemplateCreateWizard() {
        Wizard w = new Wizard();
        Window cw = new VMWindow();
        w.addStep(new WizardStep() {
            private final TextField nameField
                    = new TextField(TRANSLATOR.translate("general.name"));

            @Override
            public String getCaption() {
                return TRANSLATOR.translate("add.template");
            }

            @Override
            public Component getContent() {
                VerticalLayout vl = new VerticalLayout();
                Template t = new Template();
                BeanFieldGroup binder = new BeanFieldGroup(t.getClass());
                binder.setItemDataSource(t);
                binder.bind(tree, "templateName");
                vl.addComponent(nameField);
                return vl;
            }

            @Override
            public boolean onAdvance() {
                try {
                    TemplateServer t = new TemplateServer(nameField.getValue());
                    t.write2DB();
                    return t.getId() > 0;
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                    Notification.show(TRANSLATOR
                            .translate("general.error.record.creation"),
                            ex.getLocalizedMessage(),
                            Notification.Type.ERROR_MESSAGE);
                }
                return false;
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
                ((VMUI) UI.getCurrent()).updateScreen();
                ((VMUI) UI.getCurrent()).showTab(getComponentCaption());
                UI.getCurrent().removeWindow(cw);
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

    private void displayTemplateDeleteWizard() {
        MessageBox prompt = MessageBox.createQuestion()
                .withCaption(TRANSLATOR.translate("template.delete.title"))
                .withMessage(TRANSLATOR.translate("template.delete.message"))
                .withYesButton(() -> {
                    if (templates.getValue() != null) {
                        try {
                            //Delete nodes
                            TemplateServer t
                                    = new TemplateServer(((Template) templates
                                            .getValue()));
                            t.delete();
                            ((VMUI) UI.getCurrent()).updateScreen();
                            ((VMUI) UI.getCurrent()).showTab(getComponentCaption());
                        } catch (VMException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
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
}
