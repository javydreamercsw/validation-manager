package com.validation.manager.web.ui;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.MenuBar.Command;

public class VMMenuItemBuilder {

    private int index;
    private String groupName;
    private String name;
    private ThemeResource icon;
    private Command command;
    private boolean loggedIn = false;
    private boolean selected = false;
    private boolean admin = false;

    public VMMenuItemBuilder() {
    }

    public VMMenuItemBuilder setIndex(int index) {
        this.index = index;
        return this;
    }

    public VMMenuItemBuilder setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public VMMenuItemBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public VMMenuItemBuilder setIcon(ThemeResource icon) {
        this.icon = icon;
        return this;
    }

    public VMMenuItemBuilder setCommand(Command command) {
        this.command = command;
        return this;
    }

    public VMMenuItemBuilder setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
        return this;
    }

    public VMMenuItemBuilder setSelected(boolean selected) {
        this.selected = selected;
        return this;
    }

    public VMMenuItemBuilder setAdmin(boolean admin) {
        this.admin = admin;
        return this;
    }

    public VMMenuItem createVMMenuItem() {
        return new VMMenuItem(index, groupName, name, icon, command, loggedIn, selected, admin);
    }
}
