package com.validation.manager.core;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Window;
import com.validation.manager.core.server.core.VMUserServer;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface VMUI {

    /*Common Icons*/
    VaadinIcons ASSIGN_ICON = VaadinIcons.USER_CLOCK;
    VaadinIcons EDIT_ICON = VaadinIcons.EDIT;
    VaadinIcons EXECUTIONS_ICON = VaadinIcons.COGS;
    VaadinIcons EXECUTION_ICON = VaadinIcons.COG;
    VaadinIcons IMPORT_ICON = VaadinIcons.ARROW_CIRCLE_UP_O;
    VaadinIcons PLAN_ICON = VaadinIcons.BULLETS;
    VaadinIcons PROJECT_ICON = VaadinIcons.RECORDS;
    VaadinIcons REQUIREMENT_ICON = VaadinIcons.PIN;
    ThemeResource SMALL_APP_ICON = new ThemeResource("VMSmall.png");
    VaadinIcons SPEC_ICON = VaadinIcons.BOOK;
    VaadinIcons STEP_ICON = VaadinIcons.FILE_TREE_SUB;
    VaadinIcons TEST_ICON = VaadinIcons.FILE_TEXT;
    VaadinIcons TEST_PLAN_ICON = VaadinIcons.FILE_TREE_SMALL;
    VaadinIcons TEST_SUITE_ICON = VaadinIcons.FILE_TREE;
    VaadinIcons LINK_ICON = VaadinIcons.LINK;
    VaadinIcons BASELINE_ICON = VaadinIcons.INSERT;
    VaadinIcons DELETE_ICON = VaadinIcons.DEL_A;
    final ThemeResource LOGO = new ThemeResource("vm_logo.png");

    void buildProjectTree();

    void buildProjectTree(Object item);

    void displayObject(Object item, boolean edit);

    Object getSelectdValue();

    /**
     * @return the user
     */
    VMUserServer getUser();

    String translate(String mess);

    void updateProjectList();

    void updateScreen();

    boolean checkRight(String right);

    boolean checkAllRights(List<String> rights);

    public void addWindow(Window window);

    public Collection<Window> getWindows();

    public boolean removeWindow(Window window);

    public Locale getLocale();

    public void setLocale(Locale l);

    public ResourceBundle getResourceBundle();
}
