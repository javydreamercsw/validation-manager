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
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMException;
import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Role;
import com.validation.manager.core.db.UserHasRole;
import com.validation.manager.core.db.UserStatus;
import com.validation.manager.core.db.controller.RoleJpaController;
import com.validation.manager.core.db.controller.UserHasRoleJpaController;
import com.validation.manager.core.db.controller.UserStatusJpaController;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.VMUserServer;
import com.validation.manager.core.tool.MD5;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class UserComponent extends VerticalLayout {

    private final VMUserServer user;
    private static final Logger LOG
            = Logger.getLogger(UserComponent.class.getSimpleName());
    private static final InternationalizationProvider TRANSLATOR
            = Lookup.getDefault().lookup(InternationalizationProvider.class);
    private boolean edit = false;

    public UserComponent(VMUserServer user, boolean edit) {
        this.user = user;
        this.edit = edit;
        init();
    }

    public UserComponent(VMUserServer user, String caption, boolean edit) {
        this.user = user;
        this.edit = edit;
        // v8 Panel caption: render as a header.
        if (caption != null) {
            Span header = new Span(caption);
            add(header);
        }
        init();
    }

    private void init() {
        FormLayout layout = new FormLayout();
        com.vaadin.flow.data.binder.Binder<VMUserServer> binder
                = new com.vaadin.flow.data.binder.Binder<>(VMUserServer.class);
        binder.setBean(user);
        TextField fn = new TextField(TRANSLATOR.
                translate("general.first.name"));
        binder.forField(fn).bind("firstName");
        TextField ln = new TextField(TRANSLATOR.
                translate("general.last.name"));
        binder.forField(ln).bind("lastName");
        TextField username = new TextField(TRANSLATOR.
                translate("general.username"));
        binder.forField(username).bind("username");
        PasswordField pw = new PasswordField(TRANSLATOR.
                translate("general.password"));
        // The stored value is the MD5 hash; only take user input into the
        // model when it changed (empty means unchanged).
        binder.forField(pw)
                .withConverter(new UserPasswordConverter())
                .bind("password");
        PasswordChangeListener listener = new PasswordChangeListener();
        pw.addValueChangeListener(e -> listener.textChanged());
        TextField email = new TextField(TRANSLATOR.
                translate("general.email"));
        binder.forField(email).bind("email");
        ComboBox<String> locale = new ComboBox<>(TRANSLATOR.
                translate("general.locale"));
        locale.setItems(ValidationManagerUI.getAvailableLocales().stream()
                .map(Locale::toString).collect(Collectors.toList()));
        binder.forField(locale).bind("locale");
        //Status
        ComboBox<UserStatus> status = new ComboBox<>(TRANSLATOR.
                translate("general.status"));
        status.setItems(new UserStatusJpaController(
                DataBaseManager.getEntityManagerFactory())
                .findUserStatusEntities());
        status.setItemLabelGenerator(us
                -> TRANSLATOR.translate(us.getStatus()));
        binder.forField(status).bind("userStatusId");
        List<UserHasRole> userRoles = new ArrayList<>();
        //Project specific roles
        if (!user.getUserHasRoleList().isEmpty()) {
            TreeGrid<TreePair> roles = new TreeGrid<>();
            roles.addColumn(TreePair::getCaption)
                    .setHeader(TRANSLATOR.translate("project.specific.role"));
            roles.addComponentColumn(tp -> {
                Icon icon = new Icon(tp.getIcon());
                icon.setSize("16px");
                return icon;
            }).setHeader("");
            List<TreePair> treeData = new ArrayList<>();
            user.getUserHasRoleList().forEach(uhr -> {
                if (uhr.getProjectId() != null) {
                    Project p = uhr.getProjectId();
                    TreePair proj = new TreePair(p, p.getName(),
                            VaadinIcon.RECORDS);
                    int idx = treeData.indexOf(proj);
                    if (idx < 0) {
                        treeData.add(proj);
                    } else {
                        proj = treeData.get(idx);
                    }
                    proj.getChildren().add(new TreePair(uhr, TRANSLATOR
                            .translate(uhr.getRole().getRoleName()),
                            VaadinIcon.USER_CARD));
                }
            });
            TreeData<TreePair> tdata = new TreeData<>();
            treeData.forEach(root -> {
                tdata.addItem(null, root);
                root.getChildren().forEach(child -> {
                    tdata.addItem(root, child);
                });
            });
            roles.setDataProvider(new TreeDataProvider<>(tdata));
            if (!treeData.isEmpty()) {
                add(roles);
            }
        }
        //Roles
        if (edit && ((ValidationManagerUI) getUI()
                .orElseGet(com.vaadin.flow.component.UI::getCurrent))
                .checkRight("system.configuration")) {
            Button projectRole = new Button(TRANSLATOR.translate("manage.project.role"));
            projectRole.addClickListener(l -> {
                VMWindow w = new VMWindow(TRANSLATOR.translate("manage.project.role"));
                w.add(getProjectRoleManager());
                ((ValidationManagerUI) com.vaadin.flow.component.UI.getCurrent())
                        .openDialog(w);
            });
            add(projectRole);
            List<Role> list = new RoleJpaController(DataBaseManager
                    .getEntityManagerFactory())
                    .findRoleEntities();
            Collections.sort(list, (Role r1, Role r2)
                    -> TRANSLATOR.translate(r1.getRoleName())
                            .compareTo(TRANSLATOR
                                    .translate(r2.getRoleName())));
            MultiSelectComboBox<Role> roles
                    = new MultiSelectComboBox<>(TRANSLATOR.translate("general.role"));
            roles.setItems(list);
            roles.setItemLabelGenerator(r
                    -> TRANSLATOR.translate(r.getDescription()));
            if (user.getUserHasRoleList() != null) {
                Set<Role> rs = new HashSet<>();
                user.getUserHasRoleList().forEach(uhr -> {
                    if (uhr.getProjectId() == null) {
                        rs.add(uhr.getRole());
                    }
                });
                roles.setValue(rs);
            }
            roles.addValueChangeListener(event -> {
                Set<Role> selected = event.getValue();
                selected.forEach(r -> {
                    UserHasRole temp = new UserHasRole();
                    temp.setRole(r);
                    temp.setVmUser(user);
                    userRoles.add(temp);
                });
            });
            add(roles);
        } else {
            if (!user.getUserHasRoleList().isEmpty()) {
                Grid<Role> roles = new Grid<>();
                roles.setSelectionMode(Grid.SelectionMode.NONE);
                List<Role> roleList = new ArrayList<>();
                user.getUserHasRoleList().forEach(uhr
                        -> roleList.add(uhr.getRole()));
                roles.setItems(roleList);
                roles.addColumn(r -> TRANSLATOR.translate(r.getRoleName()))
                        .setHeader(TRANSLATOR.translate("general.role"));
                roles.setWidth("300px");
                add(roles);
            }
        }
        Button update = new Button(user.getId() == null
                ? TRANSLATOR.
                        translate("general.create")
                : TRANSLATOR.
                        translate("general.update"));
        update.addClickListener((event) -> {
            try {
                if (binder.validate().isOk()) {
                    binder.writeBean(user);
                }
                VMUserServer us;
                String password = pw.getValue() == null
                        ? user.getPassword() : pw.getValue();
                if (user.getId() == null) {
                    us = new VMUserServer(username.getValue(),
                            password,
                            fn.getValue(),
                            ln.getValue(),
                            email.getValue());
                } else {
                    us = new VMUserServer(user);
                    us.setFirstName(fn.getValue());
                    us.setLastName(ln.getValue());
                    us.setEmail(email.getValue());
                    us.setUsername(username.getValue());
                }
                us.setLocale(locale.getValue());
                if (user.getUserHasRoleList() == null) {
                    user.setUserHasRoleList(new ArrayList<>());
                }
                user.getUserHasRoleList().clear();
                userRoles.forEach(uhr -> {
                    UserHasRoleJpaController c
                            = new UserHasRoleJpaController(DataBaseManager
                                    .getEntityManagerFactory());
                    try {
                        c.create(uhr);
                        user.getUserHasRoleList().add(uhr);
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                });
                if (listener.isChanged()
                        && !password.equals(user.getPassword())) {
                    //Different password. Prompt for confirmation
                    ConfirmDialog mb = new ConfirmDialog();
                    VerticalLayout vl = new VerticalLayout();
                    Span l = new Span(TRANSLATOR.
                            translate("password.confirm.pw.message"));
                    vl.add(l);
                    PasswordField np = new PasswordField(Lookup.getDefault()
                            .lookup(InternationalizationProvider.class)
                            .translate("general.password"));
                    vl.add(np);
                    mb.setHeader(Lookup.getDefault()
                            .lookup(InternationalizationProvider.class).
                            translate("password.confirm.pw"));
                    mb.setText(vl);
                    mb.setConfirmButton(TRANSLATOR.translate("general.yes"),
                            (e) -> {
                                try {
                                    if (password.equals(MD5.encrypt(np.getValue()))) {
                                        us.setHashPassword(true);
                                        us.setPassword(np.getValue());
                                        us.write2DB();
                                        Notification.show(TRANSLATOR.
                                                translate("audit.user.account.password.change"));
                                        ((ValidationManagerUI) com.vaadin.flow.component.UI.getCurrent())
                                                .updateScreen();
                                    } else {
                                        Notification.show(TRANSLATOR.
                                                translate("password.does.not.match"));
                                    }
                                    mb.close();
                                } catch (VMException ex) {
                                    Exceptions.printStackTrace(ex);
                                }
                            });
                    mb.setCancelable(true);
                    mb.setCancelButton(TRANSLATOR.translate("general.cancel"),
                            (e) -> {
                                //Nothing to do
                            });
                    mb.open();
                } else {
                    us.write2DB();
                }
                ((ValidationManagerUI) com.vaadin.flow.component.UI.getCurrent())
                        .getUser().update();
                ((ValidationManagerUI) com.vaadin.flow.component.UI.getCurrent())
                        .setLocale(new Locale(us.getLocale()));
                ((ValidationManagerUI) com.vaadin.flow.component.UI.getCurrent())
                        .updateScreen();
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                Notification.show(TRANSLATOR.
                        translate("general.error.record.update"));
            }
        });
        Button cancel = new Button(Lookup.getDefault()
                .lookup(InternationalizationProvider.class).
                translate("general.cancel"));
        cancel.addClickListener((event) -> {
            binder.readBean(user);
            ((ValidationManagerUI) com.vaadin.flow.component.UI.getCurrent())
                    .updateScreen();
        });
        binder.setReadOnly(!edit);
        HorizontalLayout hl = new HorizontalLayout();
        hl.add(update, cancel);
        layout.add(fn, ln, username, pw, email, locale, status, hl);
        add(layout);
    }

    private Component getProjectRoleManager() {
        VerticalLayout vl = new VerticalLayout();
        ProjectTreeComponent tree = new ProjectTreeComponentBuilder()
                .setShowRequirement(false)
                .setShowTestCase(false)
                .setShowExecution(false)
                .createProjectTreeComponent();
        vl.add(tree);
        MultiSelectComboBox<Role> roles
                = new MultiSelectComboBox<>(TRANSLATOR.translate("general.role"));
        tree.asSingleSelect().addValueChangeListener(event -> {
            Project selected = event.getValue() instanceof Project
                    ? (Project) event.getValue() : null;
            if (user.getUserHasRoleList() == null) {
                user.setUserHasRoleList(new ArrayList<>());
            }
            if (selected != null) {
                HashSet<Role> values = new HashSet<>();
                user.getUserHasRoleList().forEach(uhr -> {
                    if (uhr.getProjectId() != null
                            && Objects.equals(uhr.getProjectId().getId(),
                                    selected.getId())) {
                        values.add(uhr.getRole());
                    }
                });
                roles.setValue(values);
            }
        });
        List<Role> list = new RoleJpaController(DataBaseManager
                .getEntityManagerFactory())
                .findRoleEntities();
        Collections.sort(list, (Role r1, Role r2)
                -> TRANSLATOR.translate(r1.getRoleName())
                        .compareTo(TRANSLATOR
                                .translate(r2.getRoleName())));
        roles.setItems(list);
        roles.setItemLabelGenerator(r
                -> TRANSLATOR.translate(r.getDescription()));
        roles.addValueChangeListener(event -> {
            Set<Role> selected = event.getValue();
            UserHasRoleJpaController c
                    = new UserHasRoleJpaController(DataBaseManager
                            .getEntityManagerFactory());
            ProjectServer ps = new ProjectServer((Project) tree.asSingleSelect().getValue());
            if (ps.getUserHasRoleList().isEmpty()) {
                ps.setUserHasRoleList(new ArrayList<>());
            }
            selected.forEach(r -> {
                //Look for the existing ones
                boolean found = false;
                for (UserHasRole uhr : ps.getUserHasRoleList()) {
                    if (Objects.equals(uhr.getVmUser().getId(), user.getId())
                            && Objects.equals(uhr.getRole().getId(), r.getId())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    try {
                        //Create a new one
                        UserHasRole uhr = new UserHasRole();
                        uhr.setProjectId(ps.getEntity());
                        uhr.setRole(r);
                        uhr.setVmUser(user.getEntity());
                        c.create(uhr);
                        user.update();
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }
            });
        });
        vl.add(roles);
        return vl;
    }

    private class PasswordChangeListener {

        private boolean changed = false;

        public void textChanged() {
            changed = true;
        }

        /**
         * @return the changed
         */
        public boolean isChanged() {
            return changed;
        }
    }

    /**
     * Node for the project-specific role tree: a project or a role assignment
     * under its project.
     */
    private static class TreePair {

        private final Object id;
        private final String caption;
        private final VaadinIcon icon;
        private final List<TreePair> children = new ArrayList<>();

        TreePair(Object id, String caption, VaadinIcon icon) {
            this.id = id;
            this.caption = caption;
            this.icon = icon;
        }

        public Object getId() {
            return id;
        }

        public String getCaption() {
            return caption;
        }

        public VaadinIcon getIcon() {
            return icon;
        }

        public List<TreePair> getChildren() {
            return children;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof TreePair
                    && ((TreePair) obj).id.equals(id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }
}
