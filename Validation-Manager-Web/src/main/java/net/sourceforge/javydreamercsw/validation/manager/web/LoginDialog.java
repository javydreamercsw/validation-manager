package net.sourceforge.javydreamercsw.validation.manager.web;

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
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.server.core.VMUserServer;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@SuppressWarnings("serial")
public class LoginDialog extends Window {

    private ValidationManagerUI menu;

    public LoginDialog(ValidationManagerUI menu) {
        // New login -window in the center of the screen
        super("Account login");
        this.menu = menu;
        init();
    }

    private final ShortcutAction enterKey = new ShortcutAction("Login",
            ShortcutAction.KeyCode.ENTER, null);

    TextField name = new TextField("Username");
    PasswordField password = new PasswordField("Password");

    Button loginButton = new Button("Log In", new Button.ClickListener() {
        @Override
        public void buttonClick(ClickEvent event) {
            tryToLogIn();
        }
    });

    Button cancelButton = new Button("Cancel", new Button.ClickListener() {
        @Override
        public void buttonClick(ClickEvent event) {
            LoginDialog.this.close();
        }
    });

    public void init() {
        //Layout
        FormLayout layout = new FormLayout();
        setContent(layout);
        Panel buttons = new Panel();
        HorizontalLayout panellayout = new HorizontalLayout();
        buttons.setContent(panellayout);
        panellayout.addComponent(loginButton);
        panellayout.addComponent(cancelButton);
        layout.addComponent(name);
        layout.addComponent(password);
        name.focus();
        StringLengthValidator nameVal = new StringLengthValidator(
                "Username must be 5 or more characters!");
        nameVal.setMinLength(5);
        name.addValidator(nameVal);
        name.setImmediate(true);
        StringLengthValidator passVal = new StringLengthValidator(
                "Password can't be empty!");
        passVal.setMinLength(3);
        password.addValidator(passVal);
        password.setImmediate(true);
        layout.addComponent(buttons);
        layout.setComponentAlignment(name, Alignment.TOP_LEFT);
        layout.setComponentAlignment(password, Alignment.MIDDLE_LEFT);
        layout.setComponentAlignment(buttons, Alignment.MIDDLE_CENTER);
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
        if (VMUserServer.validCredentials(name.getValue(),
                password.getValue(), true)) {
            VmUser user
                    = VMUserServer.getUser(name.getValue(),
                            password.getValue(), true);
            //Set password to unencrypted to avoid passwords goig out
            user.setPassword(null);
            if (menu != null) {
                menu.setUser(user);
            }
            close();
        } else {
            if (menu != null) {
                menu.setUser(null);
            }
            new Notification("Invalid credentials",
                    "\nIncorrect username/password.",
                    Notification.TYPE_WARNING_MESSAGE, true)
                    .show(Page.getCurrent());
        }
    }
}
