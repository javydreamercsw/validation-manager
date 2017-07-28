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

import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Table;
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
        BeanFieldGroup binder = new BeanFieldGroup(user.getClass());
        binder.setItemDataSource(user);
        Field<?> fn = binder.buildAndBind(TRANSLATOR.
                translate("general.first.name"),
                "firstName", TextField.class);
        layout.addComponent(fn);
        Field<?> ln = binder.buildAndBind(TRANSLATOR.
                translate("general.last.name"),
                "lastName", TextField.class);
        layout.addComponent(ln);
        Field<?> username = binder.buildAndBind(TRANSLATOR.
                translate("general.username"),
                "username", TextField.class);
        layout.addComponent(username);
        PasswordField pw = (PasswordField) binder.buildAndBind(
                TRANSLATOR.
                        translate("general.password"),
                "password", PasswordField.class);
        PasswordChangeListener listener = new PasswordChangeListener();
        pw.addTextChangeListener(listener);
        pw.setConverter(new UserPasswordConverter());
        layout.addComponent(pw);
        Field<?> email = binder.buildAndBind(TRANSLATOR.
                translate("general.email"),
                "email", TextField.class);
        layout.addComponent(email);
        ComboBox locale = new ComboBox(TRANSLATOR.
                translate("general.locale"));
        locale.setTextInputAllowed(false);
        ValidationManagerUI.getAvailableLocales().forEach(l -> {
            locale.addItem(l.toString());
        });
        if (user.getLocale() != null) {
            locale.setValue(user.getLocale());
        }
        binder.bind(locale, "locale");
        layout.addComponent(locale);
        //Status
        ComboBox status = new ComboBox(TRANSLATOR.
                translate("general.status"));
        new UserStatusJpaController(DataBaseManager.getEntityManagerFactory())
                .findUserStatusEntities().forEach(us -> {
                    status.addItem(us);
                    status.setItemCaption(us,
                            TRANSLATOR.translate(us.getStatus()));
                });
        binder.bind(status, "userStatusId");
        status.setTextInputAllowed(false);
        layout.addComponent(status);
        List<UserHasRole> userRoles = new ArrayList<>();
        //Project specific roles
        if (!user.getUserHasRoleList().isEmpty()) {
            Tree roles = new Tree(TRANSLATOR.translate("project.specific.role"));
            user.getUserHasRoleList().forEach(uhr -> {
                if (uhr.getProjectId() != null) {
                    Project p = uhr.getProjectId();
                    if (!roles.containsId(p)) {
                        roles.addItem(p);
                        roles.setItemCaption(p, p.getName());
                        roles.setItemIcon(p, VMUI.PROJECT_ICON);
                    }
                    roles.addItem(uhr);
                    roles.setItemCaption(uhr,
                            TRANSLATOR.translate(uhr.getRole().getRoleName()));
                    roles.setChildrenAllowed(uhr, false);
                    roles.setItemIcon(uhr, VaadinIcons.USER_CARD);
                    roles.setParent(uhr, p);
                }
            });
            if (!roles.getItemIds().isEmpty()) {
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
            BeanItemContainer<Role> roleContainer
                    = new BeanItemContainer<>(Role.class, list);
            TwinColSelect roles
                    = new TwinColSelect(TRANSLATOR.translate("general.role"));
            roles.setContainerDataSource(roleContainer);
            roles.setRows(5);
            roles.setLeftColumnCaption(TRANSLATOR.translate("available.roles"));
            roles.setRightColumnCaption(TRANSLATOR.translate("current.roles"));
            roles.setImmediate(true);
            list.forEach(r -> {
                roles.setItemCaption(r, TRANSLATOR.translate(r.getDescription()));
            });
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
                Set<Role> selected
                        = (Set<Role>) event.getProperty().getValue();
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
                Table roles = new Table(TRANSLATOR.translate("general.role"));
                user.getUserHasRoleList().forEach(role -> {
                    roles.addItem(role.getRole());
                    roles.setItemCaption(role.getRole(),
                            TRANSLATOR.translate(role.getRole().getRoleName()));
                    roles.setItemIcon(role.getRole(), VaadinIcons.USER_STAR);
                });
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
                VMUserServer us;
                String password = (String) pw.getValue();
                if (user.getId() == null) {
                    us = new VMUserServer((String) username.getValue(),
                            password,
                            (String) fn.getValue(),
                            (String) ln.getValue(),
                            (String) email.getValue());
                } else {
                    us = new VMUserServer(user);
                    us.setFirstName((String) fn.getValue());
                    us.setLastName((String) ln.getValue());
                    us.setEmail((String) email.getValue());
                    us.setUsername((String) username.getValue());
                }
                us.setLocale((String) locale.getValue());
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
            binder.discard();
            ((VMUI) UI.getCurrent()).updateScreen();
        });
        binder.setReadOnly(!edit);
        binder.setBuffered(true);
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
        TwinColSelect roles
                = new TwinColSelect(TRANSLATOR.translate("general.role"));
        tree.addValueChangeListener((Property.ValueChangeEvent event) -> {
            Project selected = (Project) tree.getValue();
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
        BeanItemContainer<Role> roleContainer
                = new BeanItemContainer<>(Role.class, list);
        roles.setContainerDataSource(roleContainer);
        roles.setRows(5);
        roles.setLeftColumnCaption(TRANSLATOR.translate("available.roles"));
        roles.setRightColumnCaption(TRANSLATOR.translate("current.roles"));
        list.forEach(r -> {
            roles.setItemCaption(r, TRANSLATOR.translate(r.getDescription()));
        });
        roles.addValueChangeListener(event -> {
            Set<Role> selected
                    = (Set<Role>) event.getProperty().getValue();
            UserHasRoleJpaController c
                    = new UserHasRoleJpaController(DataBaseManager
                            .getEntityManagerFactory());
            ProjectServer ps = new ProjectServer((Project) tree.getValue());
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

    private class PasswordChangeListener implements TextChangeListener {

        private boolean changed = false;

        @Override
        public void textChange(FieldEvents.TextChangeEvent event) {
            changed = true;
        }

        /**
         * @return the changed
         */
        public boolean isChanged() {
            return changed;
        }
    }
}
