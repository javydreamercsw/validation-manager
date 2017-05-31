package com.validation.manager.core;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Window;
import com.validation.manager.core.server.core.VMUserServer;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
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

    /**
     * Create and populate the project tree.
     */
    void buildProjectTree();

    /**
     * Create and populate the project tree. Select the provided object in the
     * tree.
     *
     * @param item Item to select.
     */
    void buildProjectTree(Object item);

    /**
     * Display the specified object.
     *
     * @param item Item to display.
     * @param edit True if it should be displayed in edit mode.
     */
    void displayObject(Object item, boolean edit);

    /**
     * Get the selected object from the tree.
     *
     * @return Selected object or null if none selected.
     */
    Object getSelectdValue();

    /**
     * @return the user
     */
    VMUserServer getUser();

    /**
     * Refresh the project list. Useful to show changes.
     */
    void updateProjectList();

    /**
     * Update the screen to show changes.
     */
    void updateScreen();

    /**
     * Check if the current user has the specified right.
     *
     * @param right Right to check
     * @return true if it has the right, false otherwise.
     */
    boolean checkRight(String right);

    /**
     * Check the provided rights against the current user.
     *
     * @param rights Rights to check
     * @return true if it has all the right, false otherwise.
     */
    boolean checkAllRights(List<String> rights);

    /**
     * Add a window to the UI
     *
     * @param window Window to add.
     */
    public void addWindow(Window window);

    /**
     * Get windows.
     *
     * @return available windows.
     */
    public Collection<Window> getWindows();

    /**
     * Remove a window from UI.
     *
     * @param window Window to remove.
     * @return true if was able to remove.
     */
    public boolean removeWindow(Window window);

    /**
     * Current locale.
     *
     * @return Current locale
     */
    public Locale getLocale();

    /**
     * Change the locale.
     *
     * @param l New locale.
     */
    public void setLocale(Locale l);

    /**
     * Show tab with provided id.
     *
     * @param id Id to search for.
     */
    public void showTab(String id);
}
