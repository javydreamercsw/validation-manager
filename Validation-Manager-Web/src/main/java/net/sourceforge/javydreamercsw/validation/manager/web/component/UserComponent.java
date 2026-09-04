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
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Resource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMException;
import com.validation.manager.core.VMUI;
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
import de.steinwedel.messagebox.ButtonOption;
import de.steinwedel.messagebox.MessageBox;
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
public class UserComponent extends Panel {

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
        super(caption);
        this.user = user;
        this.edit = edit;
        init();
    }

    private void init() {
        FormLayout layout = new FormLayout();
        setContent(layout);
        addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        Binder<VMUserServer> binder = new Binder<>(VMUserServer.class);
        binder.setBean(user);
        TextField fn = new TextField(TRANSLATOR.
                translate("general.first.name"));
        binder.forField(fn).bind("firstName");
        layout.addComponent(fn);
        TextField ln = new TextField(TRANSLATOR.
                translate("general.last.name"));
        binder.forField(ln).bind("lastName");
        layout.addComponent(ln);
        TextField username = new TextField(TRANSLATOR.
                translate("general.username"));
        binder.forField(username).bind("username");
        layout.addComponent(username);
        PasswordField pw = new PasswordField(TRANSLATOR.
                translate("general.password"));
        // The stored value is the MD5 hash; only take user input into the
        // model when it changed (empty means unchanged).
        binder.forField(pw)
                .withConverter(new UserPasswordConverter())
                .bind("password");
        PasswordChangeListener listener = new PasswordChangeListener();
        pw.addValueChangeListener(e -> listener.textChanged());
        layout.addComponent(pw);
        TextField email = new TextField(TRANSLATOR.
                translate("general.email"));
        binder.forField(email).bind("email");
        layout.addComponent(email);
        ComboBox<String> locale = new ComboBox<>(TRANSLATOR.
                translate("general.locale"));
        locale.setTextInputAllowed(false);
        locale.setItems(ValidationManagerUI.getAvailableLocales().stream()
                .map(Locale::toString).collect(Collectors.toList()));
        binder.forField(locale).bind("locale");
        layout.addComponent(locale);
        //Status
        ComboBox<UserStatus> status = new ComboBox<>(TRANSLATOR.
                translate("general.status"));
        status.setItems(new UserStatusJpaController(
                DataBaseManager.getEntityManagerFactory())
                .findUserStatusEntities());
        status.setItemCaptionGenerator(us
                -> TRANSLATOR.translate(us.getStatus()));
        binder.forField(status).bind("userStatusId");
        status.setTextInputAllowed(false);
        layout.addComponent(status);
        List<UserHasRole> userRoles = new ArrayList<>();
        //Project specific roles
        if (!user.getUserHasRoleList().isEmpty()) {
            Tree<TreePair> roles
                    = new Tree<>(TRANSLATOR.translate("project.specific.role"));
            List<TreePair> treeData = new ArrayList<>();
            user.getUserHasRoleList().forEach(uhr -> {
                if (uhr.getProjectId() != null) {
                    Project p = uhr.getProjectId();
                    TreePair proj = new TreePair(p, p.getName(),
                            VMUI.PROJECT_ICON);
                    int idx = treeData.indexOf(proj);
                    if (idx < 0) {
                        treeData.add(proj);
                    } else {
                        proj = treeData.get(idx);
                    }
                    proj.getChildren().add(new TreePair(uhr, TRANSLATOR
                            .translate(uhr.getRole().getRoleName()),
                            VaadinIcons.USER_CARD));
                }
            });
            roles.setItems(treeData, TreePair::getChildren);
            roles.setItemIconGenerator(tp -> tp.getIcon());
            roles.setItemCaptionGenerator(tp -> tp.getCaption());
            if (!treeData.isEmpty()) {
                layout.addComponent(roles);
            }
        }
        //Roles
        if (edit && ((VMUI) UI.getCurrent()).checkRight("system.configuration")) {
            Button projectRole = new Button(TRANSLATOR.translate("manage.project.role"));
            projectRole.addClickListener(l -> {
                VMWindow w = new VMWindow(TRANSLATOR.translate("manage.project.role"));
                w.setContent(getProjectRoleManager());
                ((VMUI) UI.getCurrent()).addWindow(w);
            });
            layout.addComponent(projectRole);
            List<Role> list = new RoleJpaController(DataBaseManager
                    .getEntityManagerFactory())
                    .findRoleEntities();
            Collections.sort(list, (Role r1, Role r2)
                    -> TRANSLATOR.translate(r1.getRoleName())
                            .compareTo(TRANSLATOR
                                    .translate(r2.getRoleName())));
            TwinColSelect<Role> roles
                    = new TwinColSelect<>(TRANSLATOR.translate("general.role"));
            roles.setItems(list);
            roles.setRows(5);
            roles.setItemCaptionGenerator(r
                    -> TRANSLATOR.translate(r.getDescription()));
            roles.setLeftColumnCaption(TRANSLATOR.translate("available.roles"));
            roles.setRightColumnCaption(TRANSLATOR.translate("current.roles"));
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
            layout.addComponent(roles);
        } else {
            if (!user.getUserHasRoleList().isEmpty()) {
                Grid<Role> roles = new Grid<>(TRANSLATOR.translate("general.role"));
                List<Role> roleList = new ArrayList<>();
                user.getUserHasRoleList().forEach(uhr
                        -> roleList.add(uhr.getRole()));
                roles.setItems(roleList);
                roles.addColumn(r -> TRANSLATOR.translate(r.getRoleName()))
                        .setCaption(TRANSLATOR.translate("general.role"));
                roles.addColumn(r -> "")
                        .setId("icon");
                roles.setWidth("300px");
                layout.addComponent(roles);
            }
        }
        Button update = new Button(user.getId() == null
                ? TRANSLATOR.
                        translate("general.create")
                : TRANSLATOR.
                        translate("general.update"));
        update.addClickListener((Button.ClickEvent event) -> {
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
                    MessageBox mb = MessageBox.create();
                    VerticalLayout vl = new VerticalLayout();
                    Label l = new Label(TRANSLATOR.
                            translate("password.confirm.pw.message"));
                    vl.addComponent(l);
                    PasswordField np = new PasswordField(Lookup.getDefault()
                            .lookup(InternationalizationProvider.class)
                            .translate("general.password"));
                    vl.addComponent(np);
                    mb.asModal(true)
                            .withCaption(Lookup.getDefault()
                                    .lookup(InternationalizationProvider.class).
                                    translate("password.confirm.pw"))
                            .withMessage(vl)
                            .withButtonAlignment(Alignment.MIDDLE_CENTER)
                            .withOkButton(() -> {
                                try {
                                    if (password.equals(MD5.encrypt(np.getValue()))) {
                                        us.setHashPassword(true);
                                        us.setPassword(np.getValue());
                                        us.write2DB();
                                        Notification.show(TRANSLATOR.
                                                translate("audit.user.account.password.change"),
                                                Notification.Type.ASSISTIVE_NOTIFICATION);
                                        ((VMUI) UI.getCurrent()).updateScreen();
                                    } else {
                                        Notification.show(TRANSLATOR.
                                                translate("password.does.not.match"),
                                                Notification.Type.WARNING_MESSAGE);
                                    }
                                    mb.close();
                                } catch (VMException ex) {
                                    Exceptions.printStackTrace(ex);
                                }
                            }, ButtonOption.focus(),
                                    ButtonOption.closeOnClick(false),
                                    ButtonOption.icon(VaadinIcons.CHECK))
                            .withCancelButton(
                                    ButtonOption.icon(VaadinIcons.CLOSE)
                            ).getWindow().setIcon(ValidationManagerUI.SMALL_APP_ICON);
                    mb.open();
                } else {
                    us.write2DB();
                }
                ((VMUI) UI.getCurrent()).getUser().update();
                ((VMUI) UI.getCurrent()).setLocale(new Locale(us.getLocale()));
                ((VMUI) UI.getCurrent()).updateScreen();
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                Notification.show(TRANSLATOR.
                        translate("general.error.record.update"),
                        ex.getLocalizedMessage(),
                        Notification.Type.ERROR_MESSAGE);
            }
        });
        Button cancel = new Button(Lookup.getDefault()
                .lookup(InternationalizationProvider.class).
                translate("general.cancel"));
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.readBean(user);
            ((VMUI) UI.getCurrent()).updateScreen();
        });
        binder.setReadOnly(!edit);
        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(update);
        hl.addComponent(cancel);
        layout.addComponent(hl);
    }

    private Component getProjectRoleManager() {
        VerticalLayout vl = new VerticalLayout();
        ProjectTreeComponent tree = new ProjectTreeComponentBuilder()
                .setShowRequirement(false)
                .setShowTestCase(false)
                .setShowExecution(false)
                .createProjectTreeComponent();
        vl.addComponent(tree);
        TwinColSelect<Role> roles
                = new TwinColSelect<>(TRANSLATOR.translate("general.role"));
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
        roles.setRows(5);
        roles.setItemCaptionGenerator(r
                -> TRANSLATOR.translate(r.getDescription()));
        roles.setLeftColumnCaption(TRANSLATOR.translate("available.roles"));
        roles.setRightColumnCaption(TRANSLATOR.translate("current.roles"));
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
        vl.addComponent(roles);
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
        private final Resource icon;
        private final List<TreePair> children = new ArrayList<>();

        TreePair(Object id, String caption, Resource icon) {
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

        public Resource getIcon() {
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
