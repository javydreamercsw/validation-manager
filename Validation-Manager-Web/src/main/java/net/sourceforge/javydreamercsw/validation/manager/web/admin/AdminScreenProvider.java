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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.binder.Binder;
import com.validation.manager.core.DataBaseManager;
import net.sourceforge.javydreamercsw.validation.manager.web.core.IMainContentProvider;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import net.sourceforge.javydreamercsw.validation.manager.web.component.IssueResolutionComponent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.IssueTypeComponent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.RequirementTypeComponent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.UserComponent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.VMWindow;
import net.sourceforge.javydreamercsw.validation.manager.web.workflow.WorkflowViewer;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = IMainContentProvider.class, position = 4)
public class AdminScreenProvider extends AdminProvider {

    private static final Logger LOG
            = Logger.getLogger(IMainContentProvider.class.getSimpleName());
    private final Tabs adminSheet = new Tabs();
    private final Map<Tab, Component> tabContents = new LinkedHashMap<>();
    private final String ISSUE_TYPE = "issue.type",
            ISSUE_RESOLUTION = "issue.resolution",
            REQUIREMENT_TYPE = "requirement.type",
            DESC = "description",
            DELETE_ERROR = "delete.error",
            NAME = "name";

    @Override
    public Component getContent() {
        adminSheet.removeAll();
        tabContents.clear();
        //Build left side
        //Build setting tab
        adminSheet.add(tab(getSettingTab(), TRANSLATOR
                .translate("general.settings")));
        //Build email setting tab
        adminSheet.add(tab(getEmailSettingTab(), TRANSLATOR
                .translate("general.email.settings")));
        //Build user management tab
        adminSheet.add(tab(getUserManagementTab(), TRANSLATOR
                .translate("menu.user")));
        //Build configurable items management tab
        adminSheet.add(tab(getConfigurableTab(), TRANSLATOR
                .translate("general.configuration")));
        //Build configurable items management tab
        //TODO: Disabled for now.
//        adminSheet.add(tab(getWorkflowTab(), TRANSLATOR
//                .translate("workflow.manager")));
        VerticalLayout layout = new VerticalLayout();
        VerticalLayout contentPanel = new VerticalLayout();
        //Show the content of the selected tab only
        adminSheet.addSelectedChangeListener(event -> {
            tabContents.forEach((tab, content) -> {
                content.setVisible(tab.equals(adminSheet.getSelectedTab()));
            });
        });
        tabContents.values().forEach(content -> {
            contentPanel.add(content);
            content.setVisible(false);
        });
        adminSheet.setSelectedIndex(0);
        layout.add(adminSheet, contentPanel);
        layout.setId(getComponentCaption());
        return layout;
    }

    @Override
    public void update() {
        adminSheet.removeAll();
        adminSheet.add(tab(getSettingTab(), TRANSLATOR
                .translate("general.settings")));
        //Build email setting tab
        adminSheet.add(tab(getEmailSettingTab(), TRANSLATOR
                .translate("general.email.settings")));
        //Build user management tab
        adminSheet.add(tab(getUserManagementTab(), TRANSLATOR
                .translate("menu.user")));
        //Build configurable items management tab
        adminSheet.add(tab(getConfigurableTab(), TRANSLATOR
                .translate("general.configuration")));
        super.update();
    }

    @Override
    public String getComponentCaption() {
        return "admin.tab.name";
    }

    private Tab tab(Component content, String caption) {
        Tab t = new Tab(TRANSLATOR.translate(caption));
        tabContents.put(t, content);
        return t;
    }

    private Component displaySetting(VmSetting s) {
        return displaySetting(s, false);
    }

    private Component displaySetting(VmSetting s, boolean edit) {
        Span title = new Span(TRANSLATOR.translate("setting.detail"));
        FormLayout layout = new FormLayout();
        Binder<VmSetting> binder = new Binder<>(VmSetting.class);
        binder.setBean(s);
        TextField id = new TextField(TRANSLATOR
                .translate("general.setting"));
        binder.forField(id).withNullRepresentation("").bind("setting");
        layout.add(id);
        com.vaadin.flow.component.checkbox.Checkbox bool
                = new com.vaadin.flow.component.checkbox.Checkbox(TRANSLATOR
                        .translate("bool.value"));
        binder.bind(bool, "boolVal");
        layout.add(bool);
        TextField integerVal = new TextField(TRANSLATOR
                .translate("int.value"));
        binder.forField(integerVal).withNullRepresentation("")
                .withConverter(Integer::valueOf, String::valueOf)
                .bind("intVal");
        integerVal.setSizeFull();
        layout.add(integerVal);
        TextField longVal = new TextField(TRANSLATOR
                .translate("long.val"));
        binder.bind(longVal, "longVal");
        longVal.setSizeFull();
        layout.add(longVal);
        TextArea stringVal = new TextArea(TRANSLATOR
                .translate("string.val"));
        binder.bind(stringVal, "stringVal");
        stringVal.setSizeFull();
        layout.add(stringVal);
        Button cancel = new Button(TRANSLATOR
                .translate("general.cancel"));
        cancel.addClickListener(event -> {
            binder.readBean(s);
        });
        //Editing existing one
        Button update = new Button(TRANSLATOR
                .translate("general.update"));
        update.addClickListener(event -> {
            try {
                binder.writeBean(s);
                displaySetting(s);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                com.vaadin.flow.component.notification.Notification.show(
                        TRANSLATOR.translate("general.error.record.update")
                        + " " + ex.getLocalizedMessage());
            }
        });
        boolean blocked = !s.getSetting().startsWith("version.");
        if (blocked) {
            HorizontalLayout hl = new HorizontalLayout();
            hl.add(update);
            hl.add(cancel);
            layout.add(hl);
        }
        binder.setReadOnly(edit);
        //The version settings are not modifiable from the GUI
        if (!blocked) {
            //Read-only display for version settings
            bool.setEnabled(false);
            integerVal.setEnabled(false);
            longVal.setEnabled(false);
            stringVal.setEnabled(false);
        }
        //Id is always blocked.
        id.setEnabled(false);
        layout.setSizeFull();
        return layout;
    }

    private Component getEmailSettingTab() {
        VerticalLayout s2 = new VerticalLayout();
        SplitLayout split2 = new SplitLayout();
        s2.add(split2);
        TreeGrid<VmSetting> sTree2 = new TreeGrid<>();
        sTree2.addColumn(VmSetting::getSetting)
                .setHeader(TRANSLATOR.translate("general.email.settings"));
        sTree2.setItems(mailSettings());
        sTree2.addItemClickListener(event -> {
            VmSetting vmSetting = event.getItem();
            if (vmSetting != null) {
                split2.addToSecondary(displaySetting(vmSetting,
                        !vmSetting.getSetting().equals("mail.enable")));
            }
        });
        split2.addToPrimary(sTree2);
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
                    vl.add(to);
                    vl.add(from);
                    vl.add(subject);
                    vl.add(mess);
                    vl.add(send);
                    vl.add(output);
                    w.add(vl);
                    w.setHeight("75%");
                    w.setWidth("75%");
                    ValidationManagerUI.getInstance().openDialog(w);
                });
        s2.add(testEmail);
        return s2;
    }

    private List<VmSetting> mailSettings() {
        List<VmSetting> mailSettings = new ArrayList<>();
        VMSettingServer.getSettings().forEach(s -> {
            if (s.getSetting().startsWith("mail")) {
                mailSettings.add(s);
            }
        });
        return mailSettings;
    }

    private Component getSettingTab() {
        VerticalLayout sl = new VerticalLayout();
        SplitLayout split1 = new SplitLayout();
        sl.add(split1);
        TreeGrid<VmSetting> sTree = new TreeGrid<>();
        sTree.addColumn(VmSetting::getSetting)
                .setHeader(TRANSLATOR.translate("general.settings"));
        split1.addToPrimary(sTree);
        sTree.addItemClickListener(event -> {
            VmSetting vmSetting = event.getItem();
            if (vmSetting != null) {
                split1.addToSecondary(
                        displaySetting(vmSetting));
            }
        });
        List<VmSetting> settings = new ArrayList<>();
        VMSettingServer.getSettings().forEach(s -> {
            if (!s.getSetting().startsWith("mail")) {
                settings.add(s);
            }
        });
        sTree.setItems(settings);
        return sl;
    }

    private Component getUserManagementTab() {
        VerticalLayout vl = new VerticalLayout();
        SplitLayout split = new SplitLayout();
        vl.add(split);
        //Create left side
        TreeGrid<VmUser> users = new TreeGrid<>();
        users.addColumn(user -> new VMUserServer(user).toString())
                .setHeader(TRANSLATOR.translate("menu.user"));
        //Menu
        VerticalLayout main = new VerticalLayout();
        main.add(users);
        HorizontalLayout hl = new HorizontalLayout();
        Button addUser = new Button(TRANSLATOR.translate("add.user"));
        addUser.addClickListener(listener -> {
            VMUserServer user = new VMUserServer(new VmUser());
            split.addToSecondary(new UserComponent(user, true));
        });
        hl.add(addUser);
        main.add(hl);
        split.addToPrimary(main);
        List<VmUser> userList = new ArrayList<>();
        VMUserServer.getVMUsers().forEach(user -> {
            if (!Objects.equals(user.getId(),
                    ValidationManagerUI.getInstance().getUser().getId())) {
                userList.add(user.getEntity());
            }
        });
        users.setItems(userList);
        users.asSingleSelect().addValueChangeListener(event -> {
            VmUser user = event.getValue();
            if (user != null) {
                split.addToSecondary(new UserComponent(new VMUserServer(user), true));
            }
        });
        vl.setSizeFull();
        return vl;
    }

    private Component getConfigurableTab() {
        VerticalLayout vl = new VerticalLayout();
        ComboBox<String> options = new ComboBox<>();
        options.setItems(ISSUE_TYPE, ISSUE_RESOLUTION, REQUIREMENT_TYPE);
        options.setItemLabelGenerator(key -> TRANSLATOR.translate(key));
        options.setAllowCustomValue(false);
        options.addValueChangeListener(event -> {
            Component nextComp = null;
            if (event.getValue() != null) {
                switch (event.getValue()) {
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
                vl.removeAll();
                vl.add(options);
                vl.add(nextComp);
            }
        });
        vl.add(options);
        vl.setSizeFull();
        return vl;
    }

    private Component displayIssueTypes() {
        VerticalLayout vl = new VerticalLayout();
        List<IssueType> typeList = new IssueTypeJpaController(DataBaseManager
                .getEntityManagerFactory())
                .findIssueTypeEntities();
        Grid<IssueType> grid = new Grid<>();
        grid.setItems(typeList);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        //Columns translate their values through the TranslationConverter.
        grid.addColumn(t -> TRANSLATOR.translate(t.getTypeName()))
                .setKey("typeName")
                .setHeader(TRANSLATOR.translate("general.name"));
        grid.addColumn(t -> TRANSLATOR.translate(t.getDescription()))
                .setKey(DESC)
                .setHeader(TRANSLATOR.translate("general.description"));
        grid.setSizeFull();
        vl.add(grid);
        //Menu
        HorizontalLayout hl = new HorizontalLayout();
        Button add = new Button(TRANSLATOR.translate("general.create"));
        add.addClickListener(listener -> {
            VMWindow w = new VMWindow();
            w.add(new IssueTypeComponent(new IssueType(), true));
            ValidationManagerUI.getInstance().openDialog(w);
            w.addOpenedChangeListener(l -> {
                if (!w.isOpened()) {
                    ValidationManagerUI.getInstance().updateScreen();
                }
            });
        });
        hl.add(add);
        Button delete = new Button(TRANSLATOR.translate("general.delete"));
        delete.setEnabled(false);
        delete.addClickListener(listener -> {
            IssueType selected = grid.asSingleSelect().getValue();
            if (selected != null && selected.getId() >= 1000) {
                try {
                    new IssueTypeJpaController(DataBaseManager
                            .getEntityManagerFactory())
                            .destroy(selected.getId());
                    ValidationManagerUI.getInstance().updateScreen();
                } catch (IllegalOrphanException | NonexistentEntityException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                    com.vaadin.flow.component.notification.Notification.show(
                            TRANSLATOR.translate(DELETE_ERROR));
                }
            }
        });
        hl.add(delete);
        vl.add(hl);
        grid.asSingleSelect().addValueChangeListener(event -> {
            IssueType selected = event.getValue();
            //Only delete custom ones.
            delete.setEnabled(selected != null && selected.getId() >= 1000);
        });
        return vl;
    }

    private Component displayIssueResolutions() {
        VerticalLayout vl = new VerticalLayout();
        List<IssueResolution> resolutionList
                = new IssueResolutionJpaController(DataBaseManager
                        .getEntityManagerFactory())
                        .findIssueResolutionEntities();
        Grid<IssueResolution> grid = new Grid<>();
        grid.setItems(resolutionList);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        //Column translates its values through the TranslationConverter.
        grid.addColumn(t -> TRANSLATOR.translate(t.getName()))
                .setKey(NAME)
                .setHeader(TRANSLATOR.translate("general.name"));
        grid.setSizeFull();
        vl.add(grid);
        //Menu
        HorizontalLayout hl = new HorizontalLayout();
        Button add = new Button(TRANSLATOR.translate("general.create"));
        add.addClickListener(listener -> {
            VMWindow w = new VMWindow();
            w.add(new IssueResolutionComponent(new IssueResolution(), true));
            ValidationManagerUI.getInstance().openDialog(w);
            w.addOpenedChangeListener(l -> {
                if (!w.isOpened()) {
                    ValidationManagerUI.getInstance().updateScreen();
                }
            });
        });
        hl.add(add);
        Button delete = new Button(TRANSLATOR.translate("general.delete"));
        delete.setEnabled(false);
        delete.addClickListener(listener -> {
            IssueResolution selected = grid.asSingleSelect().getValue();
            if (selected != null && selected.getId() >= 1000) {
                try {
                    new IssueResolutionJpaController(DataBaseManager
                            .getEntityManagerFactory())
                            .destroy(selected.getId());
                    ValidationManagerUI.getInstance().updateScreen();
                } catch (IllegalOrphanException | NonexistentEntityException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                    com.vaadin.flow.component.notification.Notification.show(
                            TRANSLATOR.translate(DELETE_ERROR));
                }
            }
        });
        hl.add(delete);
        vl.add(hl);
        grid.asSingleSelect().addValueChangeListener(event -> {
            IssueResolution selected = event.getValue();
            //Only delete custom ones.
            delete.setEnabled(selected != null && selected.getId() >= 1000);
        });
        return vl;
    }

    private Component displayRequirementTypes() {
        VerticalLayout vl = new VerticalLayout();
        List<RequirementType> typeList
                = new RequirementTypeJpaController(DataBaseManager
                        .getEntityManagerFactory())
                        .findRequirementTypeEntities();
        Grid<RequirementType> grid = new Grid<>();
        grid.setItems(typeList);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        //Columns translate their values through the TranslationConverter.
        grid.addColumn(t -> TRANSLATOR.translate(t.getName()))
                .setKey(NAME)
                .setHeader(TRANSLATOR.translate("general.name"));
        grid.addColumn(t -> TRANSLATOR.translate(t.getDescription()))
                .setKey(DESC)
                .setHeader(TRANSLATOR.translate("general.description"));
        grid.setSizeFull();
        vl.add(grid);
        //Menu
        HorizontalLayout hl = new HorizontalLayout();
        Button add = new Button(TRANSLATOR.translate("general.create"));
        add.addClickListener(listener -> {
            VMWindow w = new VMWindow();
            w.add(new RequirementTypeComponent(new RequirementType(), true));
            ValidationManagerUI.getInstance().openDialog(w);
            w.addOpenedChangeListener(l -> {
                if (!w.isOpened()) {
                    ValidationManagerUI.getInstance().updateScreen();
                }
            });
        });
        hl.add(add);
        Button delete = new Button(TRANSLATOR.translate("general.delete"));
        delete.setEnabled(false);
        delete.addClickListener(listener -> {
            RequirementType selected = grid.asSingleSelect().getValue();
            if (selected != null && selected.getId() >= 1000) {
                try {
                    new RequirementTypeJpaController(DataBaseManager
                            .getEntityManagerFactory())
                            .destroy(selected.getId());
                    ValidationManagerUI.getInstance().updateScreen();
                } catch (IllegalOrphanException | NonexistentEntityException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                    com.vaadin.flow.component.notification.Notification.show(
                            TRANSLATOR.translate(DELETE_ERROR));
                }
            }
        });
        hl.add(delete);
        vl.add(hl);
        grid.asSingleSelect().addValueChangeListener(event -> {
            RequirementType selected = event.getValue();
            //Only delete custom ones.
            delete.setEnabled(selected != null && selected.getId() >= 1000);
        });
        return vl;
    }

    private Component getWorkflowTab() {
        VerticalLayout vl = new VerticalLayout();
        Button w = new Button(TRANSLATOR.translate("workflow.manager"));
        w.addClickListener(listener -> {
            ValidationManagerUI.getInstance().openDialog(new WorkflowViewer());
        });
        vl.add(w);
        return vl;
    }
}
