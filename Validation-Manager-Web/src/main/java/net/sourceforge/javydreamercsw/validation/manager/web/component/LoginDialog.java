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

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.validation.manager.core.VMException;
import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import com.validation.manager.core.server.core.VMUserServer;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@SuppressWarnings("serial")
public final class LoginDialog extends VMWindow {

    private static final InternationalizationProvider TRANSLATOR
            = Lookup.getDefault().lookup(InternationalizationProvider.class);
    private static final Logger LOG
            = Logger.getLogger(LoginDialog.class.getSimpleName());

    private final TextField name = new TextField(Lookup.getDefault()
            .lookup(InternationalizationProvider.class)
            .translate("general.username"));
    private final PasswordField password = new PasswordField(Lookup.getDefault()
            .lookup(InternationalizationProvider.class)
            .translate("general.password"));

    private final Button loginButton = new Button(Lookup.getDefault()
            .lookup(InternationalizationProvider.class)
            .translate("general.login"),
            (event) -> {
                tryToLogIn();
            });

    private final Button cancelButton = new Button(Lookup.getDefault()
            .lookup(InternationalizationProvider.class)
            .translate("general.cancel"),
            (event) -> {
                LoginDialog.this.close();
            });

    public LoginDialog(ValidationManagerUI menu) {
        super(menu, Lookup.getDefault()
                .lookup(InternationalizationProvider.class)
                .translate("general.login"));
        init();
    }

    public void init() {
        //Layout
        FormLayout layout = new FormLayout();
        add(layout);
        HorizontalLayout hlayout = new HorizontalLayout();
        hlayout.add(loginButton, cancelButton);
        layout.add(name, password, hlayout);
        name.focus();
        name.setWidthFull();
        password.setWidthFull();
        // Keyboard navigation - enter key is a shortcut to login
        Shortcuts.addShortcutListener(this, this::tryToLogIn, Key.ENTER);
    }

    private void tryToLogIn() {
        if (name.getValue() == null || name.getValue().trim().length() < 5
                || password.getValue() == null
                || password.getValue().length() < 3) {
            Notification.show(Lookup.getDefault()
                    .lookup(InternationalizationProvider.class)
                    .translate("password.length.message") + ": " + 3000, 3000, Notification.Position.MIDDLE);
            return;
        }
        try {
            //Throws exception if credentials are wrong.
            VMUserServer user = new VMUserServer(name.getValue(),
                    password.getValue());
            if (menu != null) {
                if (user.getUserStatusId() != null) {
                    switch (user.getUserStatusId().getId()) {
                        case 1:
                        //Everything OK, fall thru
                        case 2:
                            //TODO: Inactive. Right now no special behavior
                            menu.setUser(user);
                            close();
                            break;
                        case 3:
                            //Locked
                            Notification.show(Lookup.getDefault()
                                    .lookup(InternationalizationProvider.class).
                                    translate("audit.user.account.lock")
                                    + "\n" + Lookup.getDefault()
                                            .lookup(InternationalizationProvider.class).
                                    translate("menu.connection.error.user") + ": " + 3000, 3000, Notification.Position.MIDDLE);
                            clear();
                            break;
                        case 4:
                            //Password Aged
                            Notification.show(Lookup.getDefault()
                                    .lookup(InternationalizationProvider.class).
                                    translate("user.status.aged") + ": " + 3000, 3000, Notification.Position.MIDDLE);
                            menu.setUser(user);
                            //Open the profile page
                            ((ValidationManagerUI) UI.getCurrent())
                                    .showTab("message.admin.userProfile");
                            close();
                            break;
                        default:
                            LOG.log(Level.SEVERE, "Unexpected User Status: {0}",
                                    user.getUserStatusId().getId());
                            Notification.show("Unexpected User Status: "
                                    + user.getUserStatusId().getId()
                                    + "\n" + TRANSLATOR.translate("message.db.error") + ": " + 3000, 3000, Notification.Position.MIDDLE);
                            menu.setUser(null);
                            close();
                            break;
                    }
                } else {
                    Notification.show(Lookup.getDefault()
                            .lookup(InternationalizationProvider.class).
                            translate("general.login.invalid.title")
                            + "\n" + Lookup.getDefault()
                                    .lookup(InternationalizationProvider.class).
                            translate("general.login.invalid.message") + ": " + 3000, 3000, Notification.Position.MIDDLE);
                    clear();
                }
            }
        } catch (VMException ex) {
            if (menu != null) {
                menu.setUser(null);
            }
            Notification.show(Lookup.getDefault()
                    .lookup(InternationalizationProvider.class).
                    translate("general.login.invalid.title")
                    + "\n" + Lookup.getDefault()
                            .lookup(InternationalizationProvider.class).
                    translate("general.login.invalid.message") + ": " + 3000, 3000, Notification.Position.MIDDLE);
            password.setValue("");
        }
    }

    public void clear() {
        name.clear();
        password.clear();
        name.focus();
    }
}
