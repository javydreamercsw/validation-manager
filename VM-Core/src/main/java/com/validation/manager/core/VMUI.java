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
package com.validation.manager.core;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.server.core.VMUserServer;
import java.io.File;
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
     * Display the specified object.
     *
     * @since 0.3.5
     * @param item Item to display.
     */
    void displayObject(Object item);

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
     * Check all the provided rights against the current user.
     *
     * @param rights Rights to check
     * @return true if it has all the rights, false otherwise.
     */
    boolean checkAllRights(List<String> rights);

    /**
     * Check any of the provided rights against the current user.
     *
     * @param rights Rights to check
     * @return true if it has at least one of the rights, false otherwise.
     */
    boolean checkAnyRights(List<String> rights);

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

    /**
     * Check the user has a role in a project.
     *
     * @param p Project to check.
     * @param role Role to check.
     * @return true if it has the role in the project, false otherwise.
     */
    public boolean checkProjectRole(Project p, String role);

    /**
     * Check the user has any of the roles in a project.
     *
     * @param p Project to check.
     * @param roles Roles to check.
     * @return true if it has any of the roles in the project, false otherwise.
     */
    public boolean checkAnyProjectRole(Project p, List<String> roles);

    /**
     * Check the user has all of the roles in a project.
     *
     * @param p Project to check.
     * @param roles Roles to check.
     * @return true if it has all of the roles in the project, false otherwise.
     */
    public boolean checkAllProjectRoles(Project p, List<String> roles);

    /**
     * Send a file to the client
     *
     * @param app UI to send the file.
     * @param attachment File to send.
     * @param exportedFileName Exported file name.
     * @param mimeType File's mime type.
     * @return True if sent successfully. False otherwise.
     */
    public boolean sendConvertedFileToUser(final UI app, File attachment,
            String exportedFileName, String mimeType);

    /**
     * Handle versioning of an item.
     *
     * @since 0.3.5
     * @param o Object to check if versioning information needs to be provided.
     * @param r Runnable to be executed if information is provided.
     */
    public void handleVersioning(Object o, Runnable r);

    /**
     * Create History Table for Requirements.
     *
     * @since 0.3.5
     * @param title Caption
     * @param historyItems History items to show in table
     * @param showVersionFields True to show version fields.
     * @return Grid with the specified history.
     */
    public Component createRequirementHistoryTable(String title,
            List<History> historyItems, boolean showVersionFields);

    /**
     * Create History Table for Step.
     *
     * @since 0.3.5
     *
     * @param title Caption
     * @param historyItems History items to show in table
     * @return Grid with the specified history.
     * @param showVersionFields True to show version fields.
     */
    public Component createStepHistoryTable(String title,
            List<History> historyItems, boolean showVersionFields);

    /**
     * Display a list of requirements.
     *
     * @since 0.3.5
     *
     * @param title Caption
     * @param requirementList List to display
     * @return Component for visualizing the list.
     */
    public Component getDisplayRequirementList(String title,
            List<Requirement> requirementList);

    /**
     * Get the requirement selection component.
     *
     * @return Requirement selection component.
     */
    public AbstractSelect getRequirementSelectionComponent();
}
