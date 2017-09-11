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
package net.sourceforge.javydreamercsw.validation.manager.web.admin;

import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Grid.SingleSelectionModel;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.IMainContentProvider;
import com.validation.manager.core.VMUI;
import com.validation.manager.core.api.email.IEmailManager;
import com.validation.manager.core.db.IssueResolution;
import com.validation.manager.core.db.IssueType;
import com.validation.manager.core.db.RequirementType;
import com.validation.manager.core.db.VmSetting;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.IssueResolutionJpaController;
import com.validation.manager.core.db.controller.IssueTypeJpaController;
import com.validation.manager.core.db.controller.RequirementTypeJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.VMSettingServer;
import com.validation.manager.core.server.core.VMUserServer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import net.sourceforge.javydreamercsw.validation.manager.web.component.IssueResolutionComponent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.IssueTypeComponent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.RequirementTypeComponent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.TranslationConverter;
import net.sourceforge.javydreamercsw.validation.manager.web.component.UserComponent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.VMWindow;
import net.sourceforge.javydreamercsw.validation.manager.web.workflow.WorkflowViewer;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = IMainContentProvider.class, position = 4)
public class AdminScreenProvider extends AdminProvider {

    private static final Logger LOG
            = Logger.getLogger(IMainContentProvider.class.getSimpleName());
    private final TabSheet adminSheet;
    private final String ISSUE_TYPE = "issue.type",
            ISSUE_RESOLUTION = "issue.resolution",
            REQUIREMENT_TYPE = "requirement.type",
            DESC = "description",
            DELETE_ERROR = "delete.error",
            NAME = "name";

    public AdminScreenProvider() {
        adminSheet = new TabSheet();
    }

    @Override
    public Component getContent() {
        VerticalLayout layout = new VerticalLayout();
        adminSheet.removeAllComponents();
        //Build left side
        //Build setting tab
        adminSheet.addTab(getSettingTab(), TRANSLATOR
                .translate("general.settings"));
        //Build email setting tab
        adminSheet.addTab(getEmailSettingTab(), TRANSLATOR
                .translate("general.email.settings"));
        //Build user management tab
        adminSheet.addTab(getUserManagementTab(), TRANSLATOR
                .translate("menu.user"));
        //Build configurable items management tab
        adminSheet.addTab(getConfigurableTab(), TRANSLATOR
                .translate("general.configuration"));
        //Build configurable items management tab
        //TODO: Disabled for now.
//        adminSheet.addTab(getWorkflowTab(), TRANSLATOR
//                .translate("workflow.manager"));
        layout.addComponent(adminSheet);
        layout.setId(getComponentCaption());
        return layout;
    }

    @Override
    public void update() {
        adminSheet.removeAllComponents();
        adminSheet.addTab(getSettingTab(), TRANSLATOR
                .translate("general.settings"));
        //Build email setting tab
        adminSheet.addTab(getEmailSettingTab(), TRANSLATOR
                .translate("general.email.settings"));
        //Build user management tab
        adminSheet.addTab(getUserManagementTab(), TRANSLATOR
                .translate("menu.user"));
        //Build configurable items management tab
        adminSheet.addTab(getConfigurableTab(), TRANSLATOR
                .translate("general.configuration"));
        super.update();
    }

    @Override
    public String getComponentCaption() {
        return "admin.tab.name";
    }

    private Component displaySetting(VmSetting s) {
        return displaySetting(s, false);
    }

    private Component displaySetting(VmSetting s, boolean edit) {
        Panel form = new Panel(TRANSLATOR
                .translate("setting.detail"));
        FormLayout layout = new FormLayout();
        form.setContent(layout);
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(s.getClass());
        binder.setItemDataSource(s);
        Field<?> id = (TextField) binder.buildAndBind(TRANSLATOR
                .translate("general.setting"), "setting");
        layout.addComponent(id);
        Field bool = binder.buildAndBind(TRANSLATOR
                .translate("bool.value"), "boolVal");
        bool.setSizeFull();
        layout.addComponent(bool);
        Field integerVal = binder.buildAndBind(TRANSLATOR
                .translate("int.value"), "intVal");
        integerVal.setSizeFull();
        layout.addComponent(integerVal);
        Field longVal = binder.buildAndBind(TRANSLATOR
                .translate("long.val"), "longVal");
        longVal.setSizeFull();
        layout.addComponent(longVal);
        Field stringVal = binder.buildAndBind(TRANSLATOR
                .translate("string.val"), "stringVal",
                TextArea.class);
        stringVal.setSizeFull();
        layout.addComponent(stringVal);
        Button cancel = new Button(TRANSLATOR
                .translate("general.cancel"));
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.discard();
        });
        //Editing existing one
        Button update = new Button(TRANSLATOR
                .translate("general.update"));
        update.addClickListener((Button.ClickEvent event) -> {
            try {
                binder.commit();
                displaySetting(s);
            } catch (FieldGroup.CommitException ex) {
                LOG.log(Level.SEVERE, null, ex);
                Notification.show(TRANSLATOR
                        .translate("general.error.record.update"),
                        ex.getLocalizedMessage(),
                        Notification.Type.ERROR_MESSAGE);
            }
        });
        boolean blocked = !s.getSetting().startsWith("version.");
        if (blocked) {
            HorizontalLayout hl = new HorizontalLayout();
            hl.addComponent(update);
            hl.addComponent(cancel);
            layout.addComponent(hl);
        }
        binder.setBuffered(true);
        binder.setReadOnly(edit);
        binder.bindMemberFields(form);
        //The version settigns are not modifiable from the GUI
        binder.setEnabled(blocked);
        //Id is always blocked.
        id.setEnabled(false);
        form.setSizeFull();
        return form;
    }

    private Component getEmailSettingTab() {
        VerticalLayout s2 = new VerticalLayout();
        HorizontalSplitPanel split2 = new HorizontalSplitPanel();
        s2.addComponent(split2);
        Tree sTree2 = new Tree(TRANSLATOR
                .translate("general.email.settings"));
        sTree2.addValueChangeListener((Property.ValueChangeEvent event) -> {
            if (sTree2.getValue() instanceof VmSetting) {
                VmSetting vmSetting = (VmSetting) sTree2.getValue();
                split2.setSecondComponent(
                        displaySetting(vmSetting,
                                !vmSetting.getSetting().equals("mail.enable")));
            }
        });
        split2.setFirstComponent(sTree2);
        VMSettingServer.getSettings().forEach(s -> {
            if (s.getSetting().startsWith("mail")) {
                sTree2.addItem(s);
                sTree2.setChildrenAllowed(s, false);
                sTree2.setItemCaption(s, TRANSLATOR
                        .translate(s.getSetting()));
            }
        });
        Button testEmail = new Button(TRANSLATOR
                .translate("general.email.settings.test"),
                listener -> {
                    //Show a window to test email settings
                    VMWindow w = new VMWindow(TRANSLATOR
                            .translate("general.email.settings.test"));
                    VerticalLayout vl = new VerticalLayout();
                    TextField to = new TextField(TRANSLATOR.translate("general.email.to"));
                    TextField from = new TextField(TRANSLATOR.translate("general.email.from"));
                    TextField subject = new TextField(TRANSLATOR.translate("general.email.subject"));
                    TextArea mess = new TextArea(TRANSLATOR.translate("general.email.message"));
                    mess.setSizeFull();
                    TextArea output = new TextArea(TRANSLATOR.translate("general.output"));
                    output.setReadOnly(true);
                    output.setSizeFull();
                    Button send = new Button(TRANSLATOR.translate("general.email.send"),
                            l -> {
                                try {
                                    Lookup.getDefault().lookup(IEmailManager.class)
                                            .sendEmail(to.getValue(), null,
                                                    from.getValue(),
                                                    subject.getValue(),
                                                    mess.getValue());
                                    output.setValue(TRANSLATOR.translate("general.email.settings.test.success"));
                                    //Successful, update the enable setting.
                                    VMSettingServer enable = new VMSettingServer("mail.enable");
                                    enable.setBoolVal(true);
                                    enable.write2DB();
                                } catch (Exception ex) {
                                    LOG.log(Level.SEVERE, null, ex);
                                    StringWriter sw = new StringWriter();
                                    ex.printStackTrace(new PrintWriter(sw));
                                    output.setReadOnly(false);
                                    output.setValue(sw.toString());
                                    output.setReadOnly(true);
                                }
                            });
                    vl.addComponent(to);
                    vl.addComponent(from);
                    vl.addComponent(subject);
                    vl.addComponent(mess);
                    vl.addComponent(send);
                    vl.addComponent(output);
                    w.setContent(vl);
                    w.setHeight(75, Sizeable.Unit.PERCENTAGE);
                    w.setWidth(75, Sizeable.Unit.PERCENTAGE);
                    ValidationManagerUI.getInstance().addWindow(w);
                });
        s2.addComponent(testEmail);
        return s2;
    }

    private Component getSettingTab() {
        VerticalLayout sl = new VerticalLayout();
        HorizontalSplitPanel split1 = new HorizontalSplitPanel();
        sl.addComponent(split1);
        Tree sTree = new Tree(TRANSLATOR
                .translate("general.settings"));
        split1.setFirstComponent(sTree);
        sTree.addValueChangeListener((Property.ValueChangeEvent event) -> {
            if (sTree.getValue() instanceof VmSetting) {
                split1.setSecondComponent(
                        displaySetting((VmSetting) sTree.getValue()));
            }
        });
        VMSettingServer.getSettings().forEach(s -> {
            if (!s.getSetting().startsWith("mail")) {
                sTree.addItem(s);
                sTree.setChildrenAllowed(s, false);
                sTree.setItemCaption(s, TRANSLATOR
                        .translate(s.getSetting()));
            }
        });
        return sl;
    }

    private Component getUserManagementTab() {
        VerticalLayout vl = new VerticalLayout();
        HorizontalSplitPanel split = new HorizontalSplitPanel();
        vl.addComponent(split);
        //Create left side
        Tree users = new Tree();
        //Menu
        VerticalLayout main = new VerticalLayout();
        main.addComponent(users);
        HorizontalLayout hl = new HorizontalLayout();
        Button addUser = new Button(TRANSLATOR.translate("add.user"));
        addUser.addClickListener(listener -> {
            VMUserServer user = new VMUserServer(new VmUser());
            split.setSecondComponent(new UserComponent(user, true));
        });
        hl.addComponent(addUser);
        main.addComponent(hl);
        split.setFirstComponent(main);
        VMUserServer.getVMUsers().forEach(user -> {
            if (!Objects.equals(user.getId(),
                    ((VMUI) UI.getCurrent()).getUser().getId())) {
                users.addItem(user.getEntity());
                users.setItemCaption(user.getEntity(), user.toString());
                users.setItemIcon(user.getEntity(), VaadinIcons.USER);
                users.setChildrenAllowed(user.getEntity(), false);
            }
        });
        users.addValueChangeListener((Property.ValueChangeEvent event) -> {
            VmUser user = (VmUser) users.getValue();
            split.setSecondComponent(new UserComponent(new VMUserServer(user), true));
        });
        vl.setSizeFull();
        return vl;
    }

    private Component getConfigurableTab() {
        VerticalLayout vl = new VerticalLayout();
        ComboBox options = new ComboBox();
        options.addItem(ISSUE_TYPE);
        options.setItemCaption(ISSUE_TYPE,
                TRANSLATOR.translate(ISSUE_TYPE));
        options.addItem(ISSUE_RESOLUTION);
        options.setItemCaption(ISSUE_RESOLUTION,
                TRANSLATOR.translate(ISSUE_RESOLUTION));
        options.addItem(REQUIREMENT_TYPE);
        options.setItemCaption(REQUIREMENT_TYPE,
                TRANSLATOR.translate(REQUIREMENT_TYPE));
        options.setTextInputAllowed(false);
        options.addValueChangeListener((Property.ValueChangeEvent event) -> {
            Component nextComp = null;
            if (options.getValue() != null) {
                switch ((String) options.getValue()) {
                    case ISSUE_TYPE:
                        nextComp = displayIssueTypes();
                        break;
                    case ISSUE_RESOLUTION:
                        nextComp = displayIssueResolutions();
                        break;
                    case REQUIREMENT_TYPE:
                        nextComp = displayRequirementTypes();
                        break;
                    default:
                    //Do nothing
                    }
            }
            if (nextComp != null) {
                vl.removeAllComponents();
                vl.addComponent(options);
                vl.addComponent(nextComp);
            }
        });
        vl.addComponent(options);
        vl.setSizeFull();
        return vl;
    }

    private Component displayIssueTypes() {
        VerticalLayout vl = new VerticalLayout();
        Grid grid = new Grid(TRANSLATOR.translate(ISSUE_TYPE));
        BeanItemContainer<IssueType> types
                = new BeanItemContainer<>(IssueType.class);
        types.addAll(new IssueTypeJpaController(DataBaseManager
                .getEntityManagerFactory())
                .findIssueTypeEntities());
        grid.setContainerDataSource(types);
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.setColumns("typeName", DESC);
        Grid.Column name = grid.getColumn("typeName");
        name.setHeaderCaption(TRANSLATOR.translate("general.name"));
        name.setConverter(new TranslationConverter());
        Grid.Column desc = grid.getColumn(DESC);
        desc.setHeaderCaption(TRANSLATOR.translate("general.description"));
        desc.setConverter(new TranslationConverter());
        grid.setSizeFull();
        vl.addComponent(grid);
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(types.size() > 5 ? 5 : types.size());
        //Menu
        HorizontalLayout hl = new HorizontalLayout();
        Button add = new Button(TRANSLATOR.translate("general.create"));
        add.addClickListener(listener -> {
            VMWindow w = new VMWindow();
            w.setContent(new IssueTypeComponent(new IssueType(), true));
            ((VMUI) UI.getCurrent()).addWindow(w);
            w.addCloseListener(l -> {
                ((VMUI) UI.getCurrent()).updateScreen();
            });
        });
        hl.addComponent(add);
        Button delete = new Button(TRANSLATOR.translate("general.delete"));
        delete.setEnabled(false);
        delete.addClickListener(listener -> {
            IssueType selected = (IssueType) ((SingleSelectionModel) grid.
                    getSelectionModel()).getSelectedRow();
            if (selected != null && selected.getId() >= 1000) {
                try {
                    new IssueTypeJpaController(DataBaseManager
                            .getEntityManagerFactory())
                            .destroy(selected.getId());
                    ((VMUI) UI.getCurrent()).updateScreen();
                } catch (IllegalOrphanException | NonexistentEntityException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                    Notification.show(TRANSLATOR.translate(DELETE_ERROR),
                            TRANSLATOR.translate(DELETE_ERROR),
                            Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        hl.addComponent(delete);
        vl.addComponent(hl);
        grid.addSelectionListener(event -> { // Java 8
            // Get selection from the selection model
            IssueType selected = (IssueType) ((SingleSelectionModel) grid.
                    getSelectionModel()).getSelectedRow();
            //Only delete custom ones.
            delete.setEnabled(selected != null && selected.getId() >= 1000);
        });
        return vl;
    }

    private Component displayIssueResolutions() {
        VerticalLayout vl = new VerticalLayout();
        Grid grid = new Grid(TRANSLATOR.translate(ISSUE_RESOLUTION));
        BeanItemContainer<IssueResolution> types
                = new BeanItemContainer<>(IssueResolution.class);
        types.addAll(new IssueResolutionJpaController(DataBaseManager
                .getEntityManagerFactory())
                .findIssueResolutionEntities());
        grid.setContainerDataSource(types);
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.setColumns(NAME);
        Grid.Column name = grid.getColumn(NAME);
        name.setHeaderCaption(TRANSLATOR.translate("general.name"));
        name.setConverter(new TranslationConverter());
        grid.setSizeFull();
        vl.addComponent(grid);
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(types.size() > 5 ? 5 : types.size());
        //Menu
        HorizontalLayout hl = new HorizontalLayout();
        Button add = new Button(TRANSLATOR.translate("general.create"));
        add.addClickListener(listener -> {
            VMWindow w = new VMWindow();
            w.setContent(new IssueResolutionComponent(new IssueResolution(), true));
            ((VMUI) UI.getCurrent()).addWindow(w);
            w.addCloseListener(l -> {
                ((VMUI) UI.getCurrent()).updateScreen();
            });
        });
        hl.addComponent(add);
        Button delete = new Button(TRANSLATOR.translate("general.delete"));
        delete.setEnabled(false);
        delete.addClickListener(listener -> {
            IssueResolution selected = (IssueResolution) ((SingleSelectionModel) grid.
                    getSelectionModel()).getSelectedRow();
            if (selected != null && selected.getId() >= 1000) {
                try {
                    new IssueResolutionJpaController(DataBaseManager
                            .getEntityManagerFactory())
                            .destroy(selected.getId());
                    ((VMUI) UI.getCurrent()).updateScreen();
                } catch (IllegalOrphanException | NonexistentEntityException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                    Notification.show(TRANSLATOR.translate(DELETE_ERROR),
                            TRANSLATOR.translate(DELETE_ERROR),
                            Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        hl.addComponent(delete);
        vl.addComponent(hl);
        grid.addSelectionListener(event -> { // Java 8
            // Get selection from the selection model
            IssueResolution selected = (IssueResolution) ((SingleSelectionModel) grid.
                    getSelectionModel()).getSelectedRow();
            //Only delete custom ones.
            delete.setEnabled(selected != null && selected.getId() >= 1000);
        });
        return vl;
    }

    private Component displayRequirementTypes() {
        VerticalLayout vl = new VerticalLayout();
        Grid grid = new Grid(TRANSLATOR.translate(REQUIREMENT_TYPE));
        BeanItemContainer<RequirementType> types
                = new BeanItemContainer<>(RequirementType.class);
        types.addAll(new RequirementTypeJpaController(DataBaseManager
                .getEntityManagerFactory())
                .findRequirementTypeEntities());
        grid.setContainerDataSource(types);
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.setColumns(NAME, DESC);
        Grid.Column name = grid.getColumn(NAME);
        name.setHeaderCaption(TRANSLATOR.translate("general.name"));
        name.setConverter(new TranslationConverter());
        Grid.Column desc = grid.getColumn(DESC);
        desc.setHeaderCaption(TRANSLATOR.translate("general.description"));
        desc.setConverter(new TranslationConverter());
        grid.setSizeFull();
        vl.addComponent(grid);
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(types.size() > 5 ? 5 : types.size());
        //Menu
        HorizontalLayout hl = new HorizontalLayout();
        Button add = new Button(TRANSLATOR.translate("general.create"));
        add.addClickListener(listener -> {
            VMWindow w = new VMWindow();
            w.setContent(new RequirementTypeComponent(new RequirementType(), true));
            ((VMUI) UI.getCurrent()).addWindow(w);
            w.addCloseListener(l -> {
                ((VMUI) UI.getCurrent()).updateScreen();
            });
        });
        hl.addComponent(add);
        Button delete = new Button(TRANSLATOR.translate("general.delete"));
        delete.setEnabled(false);
        delete.addClickListener(listener -> {
            RequirementType selected = (RequirementType) ((SingleSelectionModel) grid.
                    getSelectionModel()).getSelectedRow();
            if (selected != null && selected.getId() >= 1000) {
                try {
                    new RequirementTypeJpaController(DataBaseManager
                            .getEntityManagerFactory())
                            .destroy(selected.getId());
                    ((VMUI) UI.getCurrent()).updateScreen();
                } catch (IllegalOrphanException | NonexistentEntityException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                    Notification.show(TRANSLATOR.translate(DELETE_ERROR),
                            TRANSLATOR.translate(DELETE_ERROR),
                            Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        hl.addComponent(delete);
        vl.addComponent(hl);
        grid.addSelectionListener(event -> { // Java 8
            // Get selection from the selection model
            RequirementType selected
                    = (RequirementType) ((SingleSelectionModel) grid.
                            getSelectionModel()).getSelectedRow();
            //Only delete custom ones.
            delete.setEnabled(selected != null && selected.getId() >= 1000);
        });
        return vl;
    }

    private Component getWorkflowTab() {
        VerticalLayout vl = new VerticalLayout();
        Button w = new Button(TRANSLATOR.translate("workflow.manager"));
        w.addClickListener(listener -> {
            UI.getCurrent().addWindow(new WorkflowViewer());
        });
        vl.addComponent(w);
        return vl;
    }
}
