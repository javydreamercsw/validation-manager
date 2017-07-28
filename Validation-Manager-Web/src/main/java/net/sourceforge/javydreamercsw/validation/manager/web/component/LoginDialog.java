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

import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.validation.manager.core.VMException;
import com.validation.manager.core.VMUI;
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
    private final ShortcutAction enterKey
            = new ShortcutAction(Lookup.getDefault()
                    .lookup(InternationalizationProvider.class)
                    .translate("general.login"),
                    ShortcutAction.KeyCode.ENTER, null);

    private final TextField name = new TextField(Lookup.getDefault()
            .lookup(InternationalizationProvider.class)
            .translate("general.username"));
    private final PasswordField password = new PasswordField(Lookup.getDefault()
            .lookup(InternationalizationProvider.class)
            .translate("general.password"));

    private final Button loginButton = new Button(Lookup.getDefault()
            .lookup(InternationalizationProvider.class)
            .translate("general.login"),
            (ClickEvent event) -> {
                tryToLogIn();
            });

    private final Button cancelButton = new Button(Lookup.getDefault()
            .lookup(InternationalizationProvider.class)
            .translate("general.cancel"),
            (ClickEvent event) -> {
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
        setContent(layout);
        HorizontalLayout hlayout = new HorizontalLayout();
        hlayout.addComponent(loginButton);
        hlayout.addComponent(cancelButton);
        layout.addComponent(name);
        layout.addComponent(password);
        name.focus();
        name.setWidth(100, Unit.PERCENTAGE);
        StringLengthValidator nameVal = new StringLengthValidator(
                Lookup.getDefault()
                        .lookup(InternationalizationProvider.class).
                        translate("password.length.message"));
        nameVal.setMinLength(5);
        name.addValidator(nameVal);
        name.setImmediate(true);
        StringLengthValidator passVal = new StringLengthValidator(
                Lookup.getDefault()
                        .lookup(InternationalizationProvider.class).
                        translate("password.empty.message"));
        passVal.setMinLength(3);
        password.addValidator(passVal);
        password.setImmediate(true);
        password.setWidth(100, Unit.PERCENTAGE);
        layout.addComponent(hlayout);
        layout.setComponentAlignment(name, Alignment.TOP_LEFT);
        layout.setComponentAlignment(password, Alignment.MIDDLE_LEFT);
        layout.setComponentAlignment(hlayout, Alignment.BOTTOM_LEFT);
        layout.setSpacing(true);
        layout.setMargin(true);

        // Keyboard navigation - enter key is a shortcut to login
        addActionHandler(new Handler() {
            @Override
            public Action[] getActions(Object target, Object sender) {
                return new Action[]{enterKey};
            }

            @Override
            public void handleAction(Action action, Object sender,
                    Object target) {
                tryToLogIn();
            }
        });
    }

    private void tryToLogIn() {
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
                            new Notification(Lookup.getDefault()
                                    .lookup(InternationalizationProvider.class).
                                    translate("audit.user.account.lock"),
                                    Lookup.getDefault()
                                            .lookup(InternationalizationProvider.class).
                                            translate("menu.connection.error.user"),
                                    Notification.Type.ERROR_MESSAGE, true)
                                    .show(Page.getCurrent());
                            clear();
                            break;
                        case 4:
                            //Password Aged
                            new Notification(Lookup.getDefault()
                                    .lookup(InternationalizationProvider.class).
                                    translate("user.status.aged"),
                                    Lookup.getDefault()
                                            .lookup(InternationalizationProvider.class).
                                            translate("user.status.aged"),
                                    Notification.Type.WARNING_MESSAGE, true)
                                    .show(Page.getCurrent());
                            menu.setUser(user);
                            //Open the profile page
                            ((VMUI) UI.getCurrent()).showTab("message.admin.userProfile");
                            close();
                            break;
                        default:
                            LOG.log(Level.SEVERE, "Unexpected User Status: {0}",
                                    user.getUserStatusId().getId());
                            Notification.show("Unexpected User Status",
                                    "Unexpected User Status: "
                                    + user.getUserStatusId().getId()
                                    + "\n" + TRANSLATOR.translate("message.db.error"),
                                    Notification.Type.ERROR_MESSAGE);
                            menu.setUser(null);
                            close();
                            break;
                    }
                } else {
                    new Notification(Lookup.getDefault()
                            .lookup(InternationalizationProvider.class).
                            translate("general.login.invalid.title"),
                            Lookup.getDefault()
                                    .lookup(InternationalizationProvider.class).
                                    translate("general.login.invalid.message"),
                            Notification.Type.ERROR_MESSAGE, true)
                            .show(Page.getCurrent());
                    clear();
                }
            }
        } catch (VMException ex) {
            if (menu != null) {
                menu.setUser(null);
            }
            new Notification(Lookup.getDefault()
                    .lookup(InternationalizationProvider.class).
                    translate("general.login.invalid.title"),
                    Lookup.getDefault()
                            .lookup(InternationalizationProvider.class).
                            translate("general.login.invalid.message"),
                    Notification.Type.WARNING_MESSAGE, true)
                    .show(Page.getCurrent());
            password.setValue("");
        }
    }

    public void clear() {
        name.clear();
        password.clear();
        name.focus();
    }
}
