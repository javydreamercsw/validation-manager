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
package net.sourceforge.javydreamercsw.validation.manager.web.core;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.server.core.VMUserServer;
import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public interface VMUI {

    /*Common Icons*/
    VaadinIcon ASSIGN_ICON = VaadinIcon.USER_CLOCK;
    VaadinIcon EDIT_ICON = VaadinIcon.EDIT;
    VaadinIcon EXECUTIONS_ICON = VaadinIcon.COGS;
    VaadinIcon EXECUTION_ICON = VaadinIcon.COG;
    VaadinIcon IMPORT_ICON = VaadinIcon.ARROW_CIRCLE_UP_O;
    VaadinIcon PLAN_ICON = VaadinIcon.BULLETS;
    VaadinIcon PROJECT_ICON = VaadinIcon.RECORDS;
    VaadinIcon REQUIREMENT_ICON = VaadinIcon.PIN;
    VaadinIcon SMALL_APP_ICON = VaadinIcon.CHECK_SQUARE;
    VaadinIcon SPEC_ICON = VaadinIcon.BOOK;
    VaadinIcon STEP_ICON = VaadinIcon.FILE_TREE_SUB;
    VaadinIcon TEST_ICON = VaadinIcon.FILE_TEXT;
    VaadinIcon TEST_PLAN_ICON = VaadinIcon.FILE_TREE_SMALL;
    VaadinIcon TEST_SUITE_ICON = VaadinIcon.FILE_TREE;
    VaadinIcon LINK_ICON = VaadinIcon.LINK;
    VaadinIcon BASELINE_ICON = VaadinIcon.INSERT;
    VaadinIcon DELETE_ICON = VaadinIcon.DEL_A;
    VaadinIcon LOGO = VaadinIcon.RECORDS;

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
     * Open a dialog in the current UI. Flow equivalent of the v8
     * {@code UI.addWindow(Window)}.
     *
     * @param dialog Dialog to open.
     */
    void openDialog(Dialog dialog);

    /**
     * Close a dialog previously opened with {@link #openDialog}. Flow
     * equivalent of the v8 {@code UI.removeWindow(Window)}.
     *
     * @param dialog Dialog to close.
     * @return true if was able to close.
     */
    boolean closeDialog(Dialog dialog);

    /**
     * Check if the dialog is currently open. Flow equivalent of the v8
     * {@code UI.getWindows().contains(window)}.
     *
     * @param dialog Dialog to check.
     * @return true if the dialog is open.
     */
    boolean isOpen(Dialog dialog);

    /**
     * Current locale.
     *
     * @return Current locale
     */
    Locale getLocale();

    /**
     * Change the locale.
     *
     * @param l New locale.
     */
    void setLocale(Locale l);

    /**
     * Show tab with provided id.
     *
     * @param id Id to search for.
     */
    void showTab(String id);

    /**
     * Check the user has a role in a project.
     *
     * @param p Project to check.
     * @param role Role to check.
     * @return true if it has the role in the project, false otherwise.
     */
    boolean checkProjectRole(Project p, String role);

    /**
     * Check the user has any of the roles in a project.
     *
     * @param p Project to check.
     * @param roles Roles to check.
     * @return true if it has any of the roles in the project, false otherwise.
     */
    boolean checkAnyProjectRole(Project p, List<String> roles);

    /**
     * Check the user has all of the roles in a project.
     *
     * @param p Project to check.
     * @param roles Roles to check.
     * @return true if it has all of the roles in the project, false otherwise.
     */
    boolean checkAllProjectRoles(Project p, List<String> roles);

    /**
     * Send a file to the client
     *
     * @param app UI to send the file.
     * @param attachment File to send.
     * @param exportedFileName Exported file name.
     * @param mimeType File's mime type.
     * @return True if sent successfully. False otherwise.
     */
    boolean sendConvertedFileToUser(final com.vaadin.flow.component.UI app,
            File attachment, String exportedFileName, String mimeType);

    /**
     * Handle versioning of an item.
     *
     * @since 0.3.5
     * @param o Object to check if versioning information needs to be provided.
     * @param r Runnable to be executed if information is provided.
     */
    void handleVersioning(Object o, Runnable r);

    /**
     * Create History Table for Requirements.
     *
     * @since 0.3.5
     * @param title Caption
     * @param historyItems History items to show in table
     * @param showVersionFields True to show version fields.
     * @return Grid with the specified history.
     */
    Component createRequirementHistoryTable(String title,
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
    Component createStepHistoryTable(String title,
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
    Component getDisplayRequirementList(String title,
            List<Requirement> requirementList);

    /**
     * Get the requirement selection component.
     *
     * @return Requirement selection component.
     */
    com.vaadin.flow.data.selection.MultiSelect<
            ? extends com.vaadin.flow.component.Component, Requirement>
            getRequirementSelectionComponent();
}
