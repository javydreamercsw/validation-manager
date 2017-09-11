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
package net.sourceforge.javydreamercsw.validation.manager.web;

import com.vaadin.addon.contextmenu.ContextMenu;
import com.vaadin.addon.contextmenu.MenuItem;
import com.vaadin.addon.tableexport.TemporaryFileDownloadResource;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Image;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.Tree.TreeDropCriterion;
import com.vaadin.ui.Tree.TreeTargetDetails;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.DemoBuilder;
import com.validation.manager.core.IMainContentProvider;
import com.validation.manager.core.NotificationProvider;
import com.validation.manager.core.VMException;
import com.validation.manager.core.VMUI;
import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import com.validation.manager.core.api.internationalization.LocaleListener;
import com.validation.manager.core.api.notification.INotificationManager;
import com.validation.manager.core.api.notification.NotificationTypes;
import com.validation.manager.core.db.Baseline;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestCaseExecution;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.VmSetting;
import com.validation.manager.core.db.controller.ExecutionStepJpaController;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.db.controller.StepJpaController;
import com.validation.manager.core.db.controller.TemplateJpaController;
import com.validation.manager.core.db.controller.TestCaseExecutionJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.history.Versionable;
import com.validation.manager.core.history.Versionable.CHANGE_LEVEL;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.TestCaseExecutionServer;
import com.validation.manager.core.server.core.TestCaseServer;
import com.validation.manager.core.server.core.VMSettingServer;
import com.validation.manager.core.server.core.VMUserServer;
import com.validation.manager.core.tool.TCEExtraction;
import com.validation.manager.core.tool.Tool;
import com.validation.manager.core.tool.requirement.importer.RequirementImportException;
import com.validation.manager.core.tool.requirement.importer.RequirementImporter;
import com.validation.manager.core.tool.step.importer.StepImporter;
import com.validation.manager.core.tool.step.importer.TestCaseImportException;
import de.steinwedel.messagebox.ButtonOption;
import de.steinwedel.messagebox.ButtonType;
import de.steinwedel.messagebox.MessageBox;
import elemental.json.JsonArray;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import net.sourceforge.javydreamercsw.validation.manager.web.component.BaselineComponent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.ExecutionDashboard;
import net.sourceforge.javydreamercsw.validation.manager.web.component.ExecutionStepComponent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.HistoryTable;
import net.sourceforge.javydreamercsw.validation.manager.web.component.LoginDialog;
import net.sourceforge.javydreamercsw.validation.manager.web.component.ProjectComponent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.ProjectTreeComponent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.RequirementComponent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.RequirementListComponent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.RequirementSelectionComponent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.RequirementSpecComponent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.RequirementSpecNodeComponent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.RiskManagementComponent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.StepComponent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.TestCaseComponent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.TestCaseExecutionComponent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.TestCaseExporter;
import net.sourceforge.javydreamercsw.validation.manager.web.component.TestPlanComponent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.TestProjectComponent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.VMWindow;
import net.sourceforge.javydreamercsw.validation.manager.web.dashboard.DashboardProvider;
import net.sourceforge.javydreamercsw.validation.manager.web.demo.DemoProvider;
import net.sourceforge.javydreamercsw.validation.manager.web.importer.FileUploader;
import net.sourceforge.javydreamercsw.validation.manager.web.provider.DesignerScreenProvider;
import net.sourceforge.javydreamercsw.validation.manager.web.traceability.TraceMatrix;
import net.sourceforge.javydreamercsw.validation.manager.web.wizard.assign.AssignUserStep;
import net.sourceforge.javydreamercsw.validation.manager.web.wizard.project.ProjectCreationWizard;
import org.openide.util.Lookup;
import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

@Theme("vmtheme")
@SuppressWarnings("serial")
@PreserveOnRefresh
public class ValidationManagerUI extends UI implements VMUI {

    private static final InternationalizationProvider TRANSLATOR
            = Lookup.getDefault().lookup(InternationalizationProvider.class);
    private VMUserServer user = null;
    private static final Logger LOG
            = Logger.getLogger(ValidationManagerUI.class.getSimpleName());
    private LoginDialog loginWindow = null;
    private String projTreeRoot;
    private Component left;
    private final TabSheet tabSheet = new TabSheet();
    private final List<Project> projects = new ArrayList<>();
    private ProjectTreeComponent tree;
    private Tab main;
    private final List<String> roles = new ArrayList<>();
    private final String REQUIREMENT_REVIEW = "requirement.view";
    private static final ArrayList<Locale> LOCALES = new ArrayList<>();
    private static final Map<String, Integer> SESSIONS = new HashMap<>();

    static {
        ResourceBundle locale = ResourceBundle
                .getBundle("com.validation.manager.resources.Locale");
        String list = locale.getString("AvailableLocales");
        StringTokenizer st = new StringTokenizer(list, ",");
        LOCALES.add(Locale.ENGLISH);
        while (st.hasMoreTokens()) {
            Locale loc = getLocale(st.nextToken());
            LOG.log(Level.INFO, "Add Locale: {0}", loc);
            if (!LOCALES.contains(loc)) {
                LOCALES.add(loc);
            }
        }
        LOCALES.trimToSize();
    }

    /**
     * @return the user
     */
    @Override
    public VMUserServer getUser() {
        return user;
    }

    public Tree getTree() {
        return tree;
    }

    @Override
    public void setLocale(Locale locale) {
        Lookup.getDefault().lookupAll(LocaleListener.class).forEach(listener -> {
            listener.setlocale(locale);
        });
        //Initialize string that are shared in the new language
        projTreeRoot = TRANSLATOR
                .translate("project.root.label");
        super.setLocale(locale);
    }

    public List<Locale> getSupportedLocales() {
        return getAvailableLocales();
    }

    /**
     * @param u the user to set
     */
    public void setUser(VMUserServer u) {
        this.user = u;
        if (user != null) {
            user.update();
            if (SESSIONS.containsValue(user.getId())) {
                Notification.show(TRANSLATOR.translate("message.already.logged"),
                        TRANSLATOR.translate("message.already.logged.desc"),
                        Notification.Type.ERROR_MESSAGE);
                this.user = null;
            } else {
                LOG.log(Level.FINE, "Adding session {1} for user: {0}",
                        new Object[]{user.toString(),
                            VaadinSession.getCurrent().getSession().getId()});
                SESSIONS.put(VaadinSession.getCurrent().getSession().getId(),
                        user.getId());
                try {
                    user.write2DB();
                } catch (VMException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
                Locale l;
                if (user.getLocale() == null) {
                    //Default locale
                    l = Locale.ENGLISH;
                    user.setLocale(l.getLanguage());
                    try {
                        user.write2DB();
                    } catch (VMException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                } else {
                    l = new Locale(user.getLocale());
                }
                setLocale(l);
            }
        }
        updateScreen();
    }

    private void displayRequirementSpecNode(RequirementSpecNode rsn,
            boolean edit) {
        setTabContent(main, new RequirementSpecNodeComponent(rsn, edit),
                REQUIREMENT_REVIEW);
    }

    public void setTabContent(Tab target, Component content,
            String permission) {
        Component c = target.getComponent();
        if (c != null && c instanceof Layout) {
            Layout l = (Layout) c;
            l.removeAllComponents();
            if (content != null) {
                l.addComponent(content);
            }
        } else {
            LOG.log(Level.SEVERE, "Invalid target: {0}", target);
        }
        if (permission != null && !permission.isEmpty()) {
            //Hide tab based on permissions
            boolean viewable = checkRight(permission);
            if (viewable != target.isVisible()) {
                target.setVisible(viewable);
            }
        }
        tabSheet.setSelectedTab(target);
    }

    private void displayTestCaseExecution(TestCaseExecution tce, boolean edit) {
        displayTestCaseExecution(tce, null, edit);
    }

    private void displayTestCaseExecution(TestCaseExecution tce,
            ProjectServer ps, boolean edit) {
        setTabContent(main, new TestCaseExecutionComponent(tce, ps,
                edit), "testcase.view");
    }

    private void displayExecutionStep(ExecutionStep es) {
        setTabContent(main, new ExecutionStepComponent(es), "testcase.view");
    }

    public void displayStep(Step s, boolean edit) {
        setTabContent(main, new StepComponent(s, edit), "testcase.view");
    }

    private void displayTestCase(TestCase t, boolean edit) {
        setTabContent(main, new TestCaseComponent(t, edit), "testcase.view");
    }

    private void displayTestPlan(TestPlan tp, boolean edit) {
        setTabContent(main, new TestPlanComponent(tp, edit), "testcase.view");
    }

    private void displayTestProject(TestProject tp, boolean edit) {
        setTabContent(main, new TestProjectComponent(tp, edit), "testcase.view");
    }

    private void displayRequirementSpec(RequirementSpec rs, boolean edit) {
        setTabContent(main, new RequirementSpecComponent(rs, edit), REQUIREMENT_REVIEW);
    }

    @Override
    public void displayObject(Object item) {
        displayObject(item, false);
    }

    @Override
    public void displayObject(Object item, boolean edit) {
        if (item instanceof Project) {
            Project p = (Project) item;
            LOG.log(Level.FINE, "Selected: {0}", p.getName());
            displayProject(p, edit);
        } else if (item instanceof Requirement) {
            Requirement req = (Requirement) item;
            LOG.log(Level.FINE, "Selected: {0}", req.getUniqueId());
            displayRequirement(req, edit);
        } else if (item instanceof RequirementSpec) {
            RequirementSpec rs = (RequirementSpec) item;
            LOG.log(Level.FINE, "Selected: {0}", rs.getName());
            displayRequirementSpec(rs, edit);
        } else if (item instanceof RequirementSpecNode) {
            RequirementSpecNode rsn = (RequirementSpecNode) item;
            LOG.log(Level.FINE, "Selected: {0}", rsn.getName());
            displayRequirementSpecNode(rsn, edit);
        } else if (item instanceof TestProject) {
            TestProject tp = (TestProject) item;
            LOG.log(Level.FINE, "Selected: {0}", tp.getName());
            displayTestProject(tp, edit);
        } else if (item instanceof TestPlan) {
            TestPlan tp = (TestPlan) item;
            LOG.log(Level.FINE, "Selected: {0}", tp.getName());
            displayTestPlan(tp, edit);
        } else if (item instanceof TestCase) {
            TestCase tc = (TestCase) item;
            LOG.log(Level.FINE, "Selected: {0}", tc.getName());
            displayTestCase(tc, edit);
        } else if (item instanceof Step) {
            Step step = (Step) item;
            LOG.log(Level.FINE, "Selected: Step #{0}",
                    step.getStepSequence());
            displayStep(step, edit);
        } else if (item instanceof TestCaseExecution) {
            TestCaseExecution tce = (TestCaseExecution) item;
            LOG.log(Level.FINE, "Selected: Test Case Execution #{0}",
                    tce.getId());
            displayTestCaseExecution(tce, edit);
        } else if (item instanceof ExecutionStep) {
            ExecutionStep es = (ExecutionStep) item;
            LOG.log(Level.FINE, "Selected: Test Case Execution #{0}",
                    es.getExecutionStepPK());
            displayExecutionStep(es);
        } else if (item instanceof Baseline) {
            Baseline es = (Baseline) item;
            LOG.log(Level.FINE, "Selected: Baseline #{0}",
                    es.getId());
            displayBaseline(es, edit);
        }
    }

    private void displayRequirement(Requirement req, boolean edit) {
        setTabContent(main, new RequirementComponent(req, edit),
                REQUIREMENT_REVIEW);
    }

    public static ValidationManagerUI getInstance() {
        return (ValidationManagerUI) ValidationManagerUI
                .getCurrent();
    }

    private static void buildDemoTree() {
        try {
            DataBaseManager.clean();
            DemoBuilder.buildDemoProject();
        } catch (NonexistentEntityException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void buildProjectTree() {
        buildProjectTree(null);
    }

    @Override
    public void buildProjectTree(Object item) {
        if (tree == null) {
            tree = new ProjectTreeComponent();
        } else {
            tree.update();
        }
        showItemInTree(item);
    }

    private void showItemInTree(Object item) {
        if (item == null) {
            tree.expandItem(projTreeRoot);
        } else {
            tree.select(item);
            Object parent = tree.getParent(item);
            while (parent != null) {
                tree.expandItem(parent);
                parent = tree.getParent(parent);
            }
        }
    }

    private void addTestCaseAssignment(ContextMenu menu) {
        MenuItem create
                = menu.addItem(TRANSLATOR.translate("assign.test.case.execution"),
                        ASSIGN_ICON, (MenuItem selectedItem) -> {
                            Wizard w = new Wizard();
                            Window sw = new VMWindow();
                            w.addStep(new AssignUserStep(ValidationManagerUI.this,
                                    tree.getValue()));
                            w.addListener(new WizardProgressListener() {
                                @Override
                                public void activeStepChanged(WizardStepActivationEvent event) {
                                    //Do nothing
                                }

                                @Override
                                public void stepSetChanged(WizardStepSetChangedEvent event) {
                                    //Do nothing
                                }

                                @Override
                                public void wizardCompleted(WizardCompletedEvent event) {
                                    removeWindow(sw);
                                }

                                @Override
                                public void wizardCancelled(WizardCancelledEvent event) {
                                    removeWindow(sw);
                                }
                            });
                            sw.setContent(w);
                            sw.setSizeFull();
                            addWindow(sw);
                        });
        create.setEnabled(checkRight("testplan.planning"));
    }

    private void createTestExecutionMenu(ContextMenu menu) {
        addDeleteExecution(menu);
        addTestCaseAssignment(menu);
        addExecutionDashboard(menu);
    }

    private void createRootMenu(ContextMenu menu) {
        MenuItem create
                = menu.addItem(TRANSLATOR.translate("create.project"),
                        PROJECT_ICON, (MenuItem selectedItem) -> {
                            displayProject(new Project(), true);
                        });
        create.setEnabled(checkRight("product.modify"));
    }

    private void createExecutionsMenu(ContextMenu menu) {
        MenuItem create
                = menu.addItem(TRANSLATOR.translate("create.execution"),
                        EXECUTIONS_ICON, (MenuItem selectedItem) -> {
                            int projectId = Integer.parseInt(((String) tree.getValue())
                                    .substring(TRANSLATOR.translate("general.execution").length()));
                            displayTestCaseExecution(new TestCaseExecution(),
                                    new ProjectServer(projectId), true);
                        });
        create.setEnabled(checkRight("testplan.planning"));
    }

    private void createTestCaseExecutionPlanMenu(ContextMenu menu) {
        MenuItem create
                = menu.addItem(TRANSLATOR.translate("create.execution.step"),
                        SPEC_ICON, (MenuItem selectedItem) -> {
                            //TODO: Do something?
                        });
        create.setEnabled(checkRight("testplan.planning"));
        MenuItem edit
                = menu.addItem(TRANSLATOR.translate("edit.execution"),
                        EXECUTION_ICON, (MenuItem selectedItem) -> {
                            displayTestCaseExecution((TestCaseExecution) tree
                                    .getValue(), true);
                        });
        edit.setEnabled(checkRight("testplan.planning"));
        addDeleteExecution(menu);
        addTestCaseAssignment(menu);
        addExecutionDashboard(menu);
    }

    private void createTestPlanMenu(ContextMenu menu) {
        MenuItem create
                = menu.addItem(TRANSLATOR.translate("create.test.case"),
                        SPEC_ICON,
                        (MenuItem selectedItem) -> {
                            TestCase tc = new TestCase();
                            tc.setTestPlanList(new ArrayList<>());
                            tc.getTestPlanList().add((TestPlan) tree.getValue());
                            tc.setCreationDate(new Date());
                            displayTestCase(tc, true);
                        });
        create.setEnabled(checkRight("testplan.planning"));
        MenuItem edit
                = menu.addItem(TRANSLATOR.translate("edit.test.plan"),
                        SPEC_ICON, (MenuItem selectedItem) -> {
                            displayTestPlan((TestPlan) tree.getValue(),
                                    true);
                        });
        edit.setEnabled(checkRight("testplan.planning"));
        MenuItem export
                = menu.addItem(TRANSLATOR.translate("general.export"),
                        VaadinIcons.DOWNLOAD,
                        (MenuItem selectedItem) -> {
                            TestPlan tp = (TestPlan) tree.getValue();
                            UI.getCurrent().addWindow(TestCaseExporter
                                    .getTestCaseExporter(tp.getTestCaseList()));
                        });
        export.setEnabled(checkRight("testcase.view"));
    }

    private void createProjectMenu(ContextMenu menu) {
        MenuItem create
                = menu.addItem(TRANSLATOR.translate("create.sub.project"),
                        PROJECT_ICON,
                        (MenuItem selectedItem) -> {
                            Project project = new Project();
                            project.setParentProjectId((Project) tree.getValue());
                            displayProject(project, true);
                        });
        create.setEnabled(checkRight("requirement.modify"));
        MenuItem createSpec
                = menu.addItem(TRANSLATOR.translate("create.req.spec"),
                        SPEC_ICON,
                        (MenuItem selectedItem) -> {
                            RequirementSpec rs = new RequirementSpec();
                            rs.setProject((Project) tree.getValue());
                            displayRequirementSpec(rs, true);
                        });
        createSpec.setEnabled(checkRight("requirement.modify"));
        MenuItem createTest
                = menu.addItem(TRANSLATOR.translate("create.test.suite"),
                        TEST_SUITE_ICON,
                        (MenuItem selectedItem) -> {
                            TestProject tp = new TestProject();
                            tp.setProjectList(new ArrayList<>());
                            tp.getProjectList().add((Project) tree.getValue());
                            displayTestProject(tp, true);
                        });
        createTest.setEnabled(checkRight("requirement.modify"));
        MenuItem edit
                = menu.addItem(TRANSLATOR.translate("edit.project"),
                        EDIT_ICON,
                        (MenuItem selectedItem) -> {
                            displayProject((Project) tree.getValue(), true);
                        });
        edit.setEnabled(checkRight("product.modify"));
        MenuItem plan
                = menu.addItem(TRANSLATOR.translate("plan.testing"),
                        PLAN_ICON, (MenuItem selectedItem) -> {
                            displayTestPlanning((Project) tree.getValue());
                        });
        plan.setEnabled(checkRight("testplan.planning"));
        MenuItem trace
                = menu.addItem(TRANSLATOR.translate("trace.matrix"),
                        VaadinIcons.SPLIT,
                        (MenuItem selectedItem) -> {
                            displayTraceMatrix((Project) tree.getValue());
                        });
        trace.setEnabled(checkRight("testplan.planning"));
        MenuItem risk
                = menu.addItem(TRANSLATOR.translate("general.risk.management"),
                        VaadinIcons.BOLT,
                        (MenuItem selectedItem) -> {
                            displayRiskManagement((Project) tree.getValue());
                        });
        risk.setEnabled(checkRight("risk.management.view"));
    }

    private void createRequirementMenu(ContextMenu menu) {
        MenuItem edit
                = menu.addItem(TRANSLATOR.translate("edit.req"),
                        EDIT_ICON,
                        (MenuItem selectedItem) -> {
                            displayRequirement((Requirement) tree.getValue(),
                                    true);
                        });
        edit.setEnabled(checkRight("requirement.modify"));
    }

    private void createRequirementSpecMenu(ContextMenu menu) {
        MenuItem create
                = menu.addItem(TRANSLATOR.translate("create.req.spec.node"),
                        SPEC_ICON, (MenuItem selectedItem) -> {
                            RequirementSpecNode rs = new RequirementSpecNode();
                            rs.setRequirementSpec((RequirementSpec) tree.getValue());
                            displayRequirementSpecNode(rs, true);
                        });
        create.setEnabled(checkRight("requirement.modify"));
        MenuItem edit
                = menu.addItem(TRANSLATOR.translate("edit.req.spec"),
                        SPEC_ICON,
                        (MenuItem selectedItem) -> {
                            displayRequirementSpec((RequirementSpec) tree.getValue(),
                                    true);
                        });
        edit.setEnabled(checkRight("requirement.modify"));
        MenuItem baseline
                = menu.addItem(TRANSLATOR.translate("baseline.spec"),
                        BASELINE_ICON,
                        (MenuItem selectedItem) -> {
                            displayBaseline(new Baseline(), true,
                                    (RequirementSpec) tree.getValue());
                        });
        baseline.setEnabled(checkRight("testcase.modify"));
    }

    private void createRequirementSpecNodeMenu(ContextMenu menu) {
        MenuItem create
                = menu.addItem(TRANSLATOR.translate("create.requiremnet"),
                        VaadinIcons.PLUS, (MenuItem selectedItem) -> {
                            Requirement r = new Requirement();
                            r.setRequirementSpecNode((RequirementSpecNode) tree
                                    .getValue());
                            displayRequirement(r, true);
                        });
        create.setEnabled(checkRight("requirement.modify"));
        MenuItem edit
                = menu.addItem(TRANSLATOR.translate("edit.req.spec.node"),
                        EDIT_ICON,
                        (MenuItem selectedItem) -> {
                            displayRequirementSpecNode((RequirementSpecNode) tree
                                    .getValue(),
                                    true);
                        });
        edit.setEnabled(checkRight("requirement.modify"));
        MenuItem importRequirement
                = menu.addItem(TRANSLATOR.translate("import.requirement"),
                        IMPORT_ICON,
                        (MenuItem selectedItem) -> {// Create a sub-window and set the content
                            Window subWindow = new VMWindow(TRANSLATOR
                                    .translate("import.requirement"));
                            VerticalLayout subContent = new VerticalLayout();
                            subWindow.setContent(subContent);

                            //Add a checkbox to know if file has headers or not
                            CheckBox cb = new CheckBox(TRANSLATOR.translate("file.has.header"));

                            FileUploader receiver = new FileUploader();
                            Upload upload
                            = new Upload(TRANSLATOR.translate("upload.excel"), receiver);
                            upload.addSucceededListener((Upload.SucceededEvent event1) -> {
                                try {
                                    subWindow.close();
                                    //TODO: Display the excel file (partially), map columns and import
                                    //Process the file
                                    RequirementImporter importer
                                            = new RequirementImporter(receiver
                                                    .getFile(),
                                                    (RequirementSpecNode) tree
                                                            .getValue());

                                    importer.importFile(cb.getValue());
                                    importer.processImport();
                                    buildProjectTree(tree.getValue());
                                    updateScreen();
                                } catch (RequirementImportException ex) {
                                    LOG.log(Level.SEVERE, TRANSLATOR.translate("import.error"),
                                            ex);
                                    Notification.show(TRANSLATOR.translate("import.unsuccessful"),
                                            Notification.Type.ERROR_MESSAGE);
                                } catch (VMException ex) {
                                    LOG.log(Level.SEVERE, null, ex);
                                }
                            });
                            upload.addFailedListener((Upload.FailedEvent event1) -> {
                                LOG.log(Level.SEVERE, "Upload unsuccessful!\n{0}",
                                        event1.getReason());
                                Notification.show(TRANSLATOR.translate("upload.unsuccessful"),
                                        Notification.Type.ERROR_MESSAGE);
                                subWindow.close();
                            });
                            subContent.addComponent(cb);
                            subContent.addComponent(upload);
                            // Open it in the UI
                            addWindow(subWindow);
                        });
        importRequirement.setEnabled(checkRight("requirement.modify"));
    }

    private void createTestCaseMenu(ContextMenu menu) {
        MenuItem create
                = menu.addItem(TRANSLATOR.translate("create.step"),
                        VaadinIcons.PLUS,
                        (MenuItem selectedItem) -> {
                            TestCase tc = (TestCase) tree.getValue();
                            Step s = new Step();
                            s.setStepSequence(tc.getStepList().size() + 1);
                            s.setTestCase(tc);
                            displayStep(s, true);
                        });
        create.setEnabled(checkRight("requirement.modify"));
        MenuItem edit
                = menu.addItem(TRANSLATOR.translate("edit.test.case"),
                        EDIT_ICON,
                        (MenuItem selectedItem) -> {
                            displayTestCase((TestCase) tree.getValue(), true);
                        });
        edit.setEnabled(checkRight("testcase.modify"));
        MenuItem importSteps
                = menu.addItem(TRANSLATOR.translate("import.step"),
                        IMPORT_ICON,
                        (MenuItem selectedItem) -> { // Create a sub-window and set the content
                            Window subWindow
                            = new VMWindow(TRANSLATOR.translate("import.test.case.step"));
                            VerticalLayout subContent = new VerticalLayout();
                            subWindow.setContent(subContent);

                            //Add a checkbox to know if file has headers or not
                            CheckBox cb = new CheckBox(TRANSLATOR.translate("file.has.header"));

                            FileUploader receiver = new FileUploader();
                            Upload upload
                            = new Upload(TRANSLATOR.translate("upload.excel"), receiver);
                            upload.addSucceededListener((Upload.SucceededEvent event1) -> {
                                try {
                                    subWindow.close();
                                    //TODO: Display the excel file (partially), map columns and import
                                    //Process the file
                                    TestCase tc = (TestCase) tree.getValue();
                                    StepImporter importer
                                            = new StepImporter(receiver.getFile(), tc);
                                    importer.importFile(cb.getValue());
                                    importer.processImport();
                                    SortedMap<Integer, Step> map = new TreeMap<>();
                                    tc.getStepList().forEach((s) -> {
                                        map.put(s.getStepSequence(), s);
                                    });
                                    //Now update the sequence numbers
                                    int count = 0;
                                    for (Entry<Integer, Step> entry : map.entrySet()) {
                                        entry.getValue().setStepSequence(++count);
                                        try {
                                            new StepJpaController(DataBaseManager
                                                    .getEntityManagerFactory())
                                                    .edit(entry.getValue());
                                        } catch (Exception ex) {
                                            LOG.log(Level.SEVERE, null, ex);
                                        }
                                    }
                                    buildProjectTree(new TestCaseServer(tc.getTestCasePK())
                                            .getEntity());
                                    updateScreen();
                                } catch (TestCaseImportException ex) {
                                    LOG.log(Level.SEVERE, TRANSLATOR.translate("import.error"),
                                            ex);
                                    Notification.show(TRANSLATOR.translate("import.unsuccessful"),
                                            Notification.Type.ERROR_MESSAGE);
                                }
                            });
                            upload.addFailedListener((Upload.FailedEvent event1) -> {
                                LOG.log(Level.SEVERE, "Upload unsuccessful!\n{0}",
                                        event1.getReason());
                                Notification.show(TRANSLATOR.translate("upload.unsuccessful"),
                                        Notification.Type.ERROR_MESSAGE);
                                subWindow.close();
                            });
                            subContent.addComponent(cb);
                            subContent.addComponent(upload);
                            // Open it in the UI
                            addWindow(subWindow);
                        });
        importSteps.setEnabled(checkRight("requirement.modify"));
        MenuItem export
                = menu.addItem(TRANSLATOR.translate("general.export"),
                        VaadinIcons.DOWNLOAD,
                        (MenuItem selectedItem) -> {
                            TestCase tc = (TestCase) tree.getValue();
                            UI.getCurrent().addWindow(TestCaseExporter
                                    .getTestCaseExporter(Arrays.asList(tc)));
                        });
        export.setEnabled(checkRight("testcase.view"));
        addExecutionDashboard(menu);
    }

    private void createStepMenu(ContextMenu menu) {
        MenuItem edit
                = menu.addItem(TRANSLATOR.translate("edit.step"), EDIT_ICON,
                        (MenuItem selectedItem) -> {
                            displayStep((Step) tree.getValue(), true);
                        });
        edit.setEnabled(checkRight("testcase.modify"));
    }

    private void createTestProjectMenu(ContextMenu menu) {
        MenuItem create
                = menu.addItem(TRANSLATOR.translate("create.test.plan"),
                        VaadinIcons.PLUS,
                        (MenuItem selectedItem) -> {
                            TestPlan tp = new TestPlan();
                            tp.setTestProject((TestProject) tree.getValue());
                            displayTestPlan(tp, true);
                        });
        create.setEnabled(checkRight("testplan.planning"));
        MenuItem edit
                = menu.addItem(TRANSLATOR.translate("edit.test.project"),
                        EDIT_ICON,
                        (MenuItem selectedItem) -> {
                            displayTestProject((TestProject) tree.getValue(), true);
                        });
        edit.setEnabled(checkRight("testplan.planning"));
    }

    public Component findMainProvider(String id) {
        Iterator<Component> it = tabSheet.iterator();
        Component me = null;
        while (it.hasNext()) {
            Component next = it.next();
            if (next.getId() != null
                    && next.getId().equals(id)) {
                me = next;
                break;
            }
        }
        return me;
    }

    @Override
    public void showTab(String id) {
        Component c = findMainProvider(id);
        if (c != null) {
            tabSheet.setSelectedTab(c);
        }
    }

    private Component getContentComponent() {
        HorizontalSplitPanel hsplit = new HorizontalSplitPanel();
        hsplit.setLocked(true);
        if (left != null) {
            if (!(left instanceof Panel)) {
                left = new Panel(left);
            }
            if (user != null) {
                hsplit.setFirstComponent(left);
            }
        }
        tabSheet.removeAllComponents();
        //Build the right component
        main = tabSheet.addTab(new VerticalLayout(),
                TRANSLATOR.translate("general.main"));
        Lookup.getDefault().lookupAll(IMainContentProvider.class)
                .forEach((provider) -> {
                    Iterator<Component> it = tabSheet.iterator();
                    Component me = findMainProvider(provider
                            .getComponentCaption());
                    if (me == null) {
                        if (provider.shouldDisplay()) {
                            LOG.log(Level.FINE, "Loading: {0}",
                                    TRANSLATOR.translate(provider
                                            .getComponentCaption()));
                            tabSheet.addTab(provider.getContent(),
                                    TRANSLATOR.translate(provider
                                            .getComponentCaption()));
                        }
                    } else {
                        provider.update();
                    }
                    //Hide if needed
                    if (me != null && !provider.shouldDisplay()) {
                        tabSheet.removeComponent(me);
                    }
                });
        hsplit.setSecondComponent(tabSheet);
        //This is a tabbed pane. Enable/Disable the panes based on role
        if (getUser() != null) {
            roles.clear();
            user.update();//Get any recent changes
            user.getRoleList().forEach((r) -> {
                roles.add(r.getRoleName());
            });
        }
        hsplit.setSplitPosition(25, Unit.PERCENTAGE);
        return hsplit;
    }

    public synchronized String getBuild() {
        String build = null;
        try {
            Properties p = new Properties();
            InputStream is = getClass()
                    .getResourceAsStream("/version.properties");
            if (is != null) {
                p.load(is);
                build = p.getProperty("build.number", "");
                LOG.log(Level.FINE, "Loaded build: {0}",
                        new Object[]{build});
            }
        } catch (IOException e) {
            // ignore
        }
        return build;
    }

    public synchronized String getVersion() {
        String version = null;
        try {
            Properties p = new Properties();
            InputStream is = getClass()
                    .getResourceAsStream("/version.properties");
            if (is != null) {
                p.load(is);
                version = p.getProperty("version", "");
                LOG.log(Level.FINE, "Loaded version: {0}",
                        new Object[]{version});
            }
        } catch (IOException e) {
            // ignore
        }
        return version;
    }

    private Component getMenu() {
        GridLayout gl = new GridLayout(3, 3);
        gl.addComponent(new Image("", LOGO), 0, 0);
        Label version = new Label(TRANSLATOR.translate("general.version")
                + ": " + getVersion());
        gl.addComponent(version, 2, 2);
        if (getUser() != null) {
            getUser().update();
            //Logout button
            Button logout = new Button(TRANSLATOR.translate("general.logout"));
            logout.addClickListener((Button.ClickEvent event) -> {
                try {
                    user.update();
                    user.write2DB();
                    user = null;
                    main = null;
                    setLocale(Locale.ENGLISH);
                    updateScreen();
                    // Close the session
                    closeSession();
                } catch (VMException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            });
            gl.addComponent(logout, 1, 0);
            //Notification Button
            if (getUser().getNotificationList().isEmpty()
                    && DataBaseManager.isDemo()) {
                //For demo add a notification for users
                try {
                    Lookup.getDefault().lookup(INotificationManager.class)
                            .addNotification("Welcome to ValidationManager!",
                                    NotificationTypes.GENERAL,
                                    getUser().getEntity(),
                                    new VMUserServer(1).getEntity());
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
            Button notification = new Button();
            if (getUser().getPendingNotifications().size() > 0) {
                notification.setCaption(""
                        + getUser().getPendingNotifications().size()); //any number, count, etc
            }
            notification.setHtmlContentAllowed(true);
            notification.setIcon(VaadinIcons.BELL);
            notification.addClickListener((Button.ClickEvent event) -> {
                //TODO: Show notifications screen
            });
            gl.addComponent(notification, 2, 0);
        }
        gl.setSizeFull();
        return gl;
    }

    @Override
    public void updateScreen() {
        //Set up a menu header on top and the content below
        VerticalSplitPanel vs = new VerticalSplitPanel();
        vs.setSplitPosition(25, Unit.PERCENTAGE);
        //Set up top menu panel
        vs.setFirstComponent(getMenu());
        if (getUser() == null) {
            if (tabSheet != null) {
                tabSheet.removeAllComponents();
            }
            showLoginDialog();
        } else {
            //Process any notifications
            //Check for assigned test
            getUser().update();
            //Process notifications
            Lookup.getDefault().lookupAll(NotificationProvider.class)
                    .forEach(p -> {
                        p.processNotification();
                    });
            createTree();
        }
        //Add the content
        vs.setSecondComponent(getContentComponent());
        if (getUser() != null) {
            showTab(Lookup.getDefault().lookup(DashboardProvider.class)
                    .getComponentCaption());
        } else {
            if (DataBaseManager.isDemo()) {
                showTab(Lookup.getDefault().lookup(DemoProvider.class)
                        .getComponentCaption());
            }
        }
        setContent(vs);
    }

    private void displayProject(Project p, boolean edit) {
        if (p.getId() == null && new TemplateJpaController(DataBaseManager
                .getEntityManagerFactory()).getTemplateCount() > 0) {//Make sure there are templates defined.
            //Prompt the user to see if he wants to use a template or not.
            MessageBox prompt = MessageBox.createQuestion()
                    .withCaption(TRANSLATOR.translate("use.project.wizard.title"))
                    .withMessage(TRANSLATOR.translate("use.project.wizard.message"))
                    .withYesButton(() -> {
                        //Show creation wizard
                        showProjectWizard(p);
                    },
                            ButtonOption.focus(),
                            ButtonOption
                                    .icon(VaadinIcons.CHECK))
                    .withNoButton(() -> {
                        // Just display it.
                        setTabContent(main, new ProjectComponent(p, edit),
                                "project.viewer");
                    },
                            ButtonOption
                                    .icon(VaadinIcons.CLOSE));
            prompt.getWindow().setIcon(ValidationManagerUI.SMALL_APP_ICON);
            prompt.open();
        } else {
            // Just display it.
            setTabContent(main, new ProjectComponent(p, edit),
                    "project.viewer");
        }
    }

    private void createTree() {
        tree = new ProjectTreeComponent();
        // Set the tree in drag source mode
        tree.setDragMode(TreeDragMode.NODE);
        // Allow the tree to receive drag drops and handle them
        tree.setDropHandler(new DropHandler() {
            @Override
            public AcceptCriterion getAcceptCriterion() {
                TreeDropCriterion criterion = new TreeDropCriterion() {
                    @Override
                    protected Set<Object> getAllowedItemIds(
                            DragAndDropEvent dragEvent, Tree tree) {
                        HashSet<Object> allowed = new HashSet<>();
                        tree.getItemIds().stream().filter((itemId)
                                -> (itemId instanceof Step)
                                || (itemId instanceof Requirement))
                                .forEachOrdered((itemId) -> {
                                    allowed.add(itemId);
                                });
                        return allowed;
                    }
                };
                return criterion;
            }

            @Override
            public void drop(DragAndDropEvent event) {
                // Wrapper for the object that is dragged
                Transferable t = event.getTransferable();

                // Make sure the drag source is the same tree
                if (t.getSourceComponent() != tree) {
                    return;
                }

                TreeTargetDetails target
                        = (TreeTargetDetails) event.getTargetDetails();

                // Get ids of the dragged item and the target item
                Object sourceItemId = t.getData("itemId");
                Object targetItemId = target.getItemIdOver();

                LOG.log(Level.INFO, "Source: {0}", sourceItemId);
                LOG.log(Level.INFO, "Target: {0}", targetItemId);

                // On which side of the target the item was dropped
                VerticalDropLocation location = target.getDropLocation();

                HierarchicalContainer container
                        = (HierarchicalContainer) tree.getContainerDataSource();

                if (null != location) // Drop right on an item -> make it a child
                {
                    switch (location) {

                        case MIDDLE:
                            if (tree.areChildrenAllowed(targetItemId)) {
                                tree.setParent(sourceItemId, targetItemId);
                            }
                            break;
                        case TOP: {
                            boolean valid = false;
                            //for Steps we need to update the sequence number
                            if (sourceItemId instanceof Step
                                    && targetItemId instanceof Step) {
                                Step targetItem = (Step) targetItemId;
                                Step sourceItem = (Step) sourceItemId;
                                StepJpaController stepController
                                        = new StepJpaController(DataBaseManager
                                                .getEntityManagerFactory());
                                if (targetItem.getTestCase().equals(sourceItem
                                        .getTestCase())) {
                                    //Same Test Case, just re-arrange
                                    LOG.info("Same Test Case!");
                                    SortedMap<Integer, Step> map
                                            = new TreeMap<>();
                                    targetItem.getTestCase().getStepList()
                                            .forEach((s) -> {
                                                map.put(s.getStepSequence(), s);
                                            });
                                    //Now swap the two that switched
                                    swapValues(map, sourceItem.getStepSequence(),
                                            targetItem.getStepSequence());
                                    //Now update the sequence numbers
                                    int count = 0;
                                    for (Entry<Integer, Step> entry
                                            : map.entrySet()) {
                                        entry.getValue()
                                                .setStepSequence(++count);
                                        try {
                                            stepController.edit(entry
                                                    .getValue());
                                        } catch (Exception ex) {
                                            LOG.log(Level.SEVERE, null, ex);
                                        }
                                    }
                                    valid = true;
                                } else {
                                    //Diferent Test Case
                                    LOG.info("Different Test Case!");
//                                    //Remove from source test case
//                                    SortedMap<Integer, Step> map = new TreeMap<>();
//                                    sourceItem.getTestCase().getStepList().forEach((s) -> {
//                                        map.put(s.getStepSequence(), s);
//                                    });
//                                    //Now swap the two that switched
//                                    //First we remove the one from the source Test Case
//                                    Step removed = map.remove(sourceItem.getStepSequence() - 1);
//                                    sourceItem.getTestCase().getStepList().remove(removed);
//                                    removed.setTestCase(targetItem.getTestCase());
//                                    try {
//                                        stepController.edit(removed);
//                                        tcController.edit(sourceItem.getTestCase());
//                                    } catch (NonexistentEntityException ex) {
//                                         LOG.log(Level.SEVERE, null, ex);
//                                    } catch (Exception ex) {
//                                         LOG.log(Level.SEVERE, null, ex);
//                                    }
//                                    //Now update the sequence numbers
//                                    int count = 0;
//                                    for (Entry<Integer, Step> entry : map.entrySet()) {
//                                        entry.getValue().setStepSequence(++count);
//                                        try {
//                                            stepController.edit(entry.getValue());
//                                        } catch (Exception ex) {
//                                             LOG.log(Level.SEVERE, null, ex);
//                                        }
//                                    }
//                                    //And add it to the target test Case
//                                    SortedMap<Integer, Step> map2 = new TreeMap<>();
//                                    targetItem.getTestCase().getStepList().forEach((s) -> {
//                                        map2.put(s.getStepSequence(), s);
//                                    });
//                                    map2.put(targetItem.getStepSequence() - 1, removed);
//                                    count = 0;
//                                    for (Entry<Integer, Step> entry : map2.entrySet()) {
//                                        entry.getValue().setStepSequence(++count);
//                                        try {
//                                            stepController.edit(entry.getValue());
//                                        } catch (Exception ex) {
//                                             LOG.log(Level.SEVERE, null, ex);
//                                        }
//                                    }
//                                    //Add it to the Test Case
//                                    targetItem.getTestCase().getStepList().add(removed);
                                }
                            }
                            if (valid) {
                                // Drop at the top of a subtree -> make it previous
                                Object parentId
                                        = container.getParent(targetItemId);
                                container.setParent(sourceItemId, parentId);
                                container.moveAfterSibling(sourceItemId,
                                        targetItemId);
                                container.moveAfterSibling(targetItemId,
                                        sourceItemId);
                                buildProjectTree(targetItemId);
                                updateScreen();
                            }
                            break;
                        }
                        case BOTTOM: {
                            // Drop below another item -> make it next
                            Object parentId = container.getParent(targetItemId);
                            container.setParent(sourceItemId, parentId);
                            container.moveAfterSibling(sourceItemId,
                                    targetItemId);
                            break;
                        }
                        default:
                            break;
                    }
                }
            }
        });
        tree.addValueChangeListener((Property.ValueChangeEvent event) -> {
            displayObject(tree.getValue());
        });
        //Select item on right click as well
        tree.addItemClickListener((ItemClickEvent event) -> {
            if (event.getSource() == tree
                    && event.getButton() == MouseButton.RIGHT) {
                if (event.getItem() != null) {
                    Item clicked = event.getItem();
                    tree.select(event.getItemId());
                }
            }
        });
        ContextMenu contextMenu = new ContextMenu(tree, true);
        tree.addItemClickListener((ItemClickEvent event) -> {
            if (event.getButton() == MouseButton.RIGHT) {
                contextMenu.removeItems();
                if (tree.getValue() instanceof Project) {
                    createProjectMenu(contextMenu);
                } else if (tree.getValue() instanceof Requirement) {
                    createRequirementMenu(contextMenu);
                } else if (tree.getValue() instanceof RequirementSpec) {
                    createRequirementSpecMenu(contextMenu);
                } else if (tree.getValue() instanceof RequirementSpecNode) {
                    createRequirementSpecNodeMenu(contextMenu);
                } else if (tree.getValue() instanceof TestProject) {
                    createTestProjectMenu(contextMenu);
                } else if (tree.getValue() instanceof Step) {
                    createStepMenu(contextMenu);
                } else if (tree.getValue() instanceof TestCase) {
                    createTestCaseMenu(contextMenu);
                } else if (tree.getValue() instanceof String) {
                    String val = (String) tree.getValue();
                    if (val.startsWith("tce")) {
                        createTestExecutionMenu(contextMenu);
                    } else if (val.startsWith("executions")) {
                        createExecutionsMenu(contextMenu);
                    } else {
                        //We are at the root
                        createRootMenu(contextMenu);
                    }
                } else if (tree.getValue() instanceof TestPlan) {
                    createTestPlanMenu(contextMenu);
                } else if (tree.getValue() instanceof TestCaseExecution) {
                    createTestCaseExecutionPlanMenu(contextMenu);
                } else if (tree.getValue() instanceof Baseline) {
//                        createBaselineMenu(contextMenu);
                }
            }
        });
        tree.setImmediate(true);
        tree.expandItem(projTreeRoot);
        tree.setSizeFull();
        updateProjectList();
    }

    @Override
    protected void init(VaadinRequest request) {
        LOG.log(Level.INFO, "Current working directory: {0}",
                System.getProperty("user.home"));
        Page.getCurrent().setTitle("Validation Manager");
        updateScreen();
        //For the code below see: https://vaadin.com/forum#!/thread/1553240/8194235
        JavaScript.getCurrent().addFunction("aboutToClose",
                (JsonArray arguments) -> {
                    try {
                        if (user != null) {
                            int id = SESSIONS.get(VaadinSession.getCurrent().getSession().getId());
                            VMUserServer u = new VMUserServer(id);
                            LOG.log(Level.FINE, "Clearing session for user: {0}", u.toString());
                            SESSIONS.remove(VaadinSession.getCurrent().getSession().getId());
                        }
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                });

        Page.getCurrent().getJavaScript()
                .execute("window.onbeforeunload = function (e) { var e = e || window.event; aboutToClose(); return; };");
    }

    private static <V> void swapValues(SortedMap m, int i0, int i1) {
        Object first = m.get(i0);
        Object second = m.get(i1);
        m.put(i0, second);
        m.put(i1, first);
    }

    @Override
    public void updateProjectList() {
        ProjectJpaController controller
                = new ProjectJpaController(DataBaseManager
                        .getEntityManagerFactory());
        List<Project> all = controller.findProjectEntities();
        projects.clear();
        all.stream().filter((p)
                -> (p.getParentProjectId() == null)).forEachOrdered((p) -> {
            projects.add(p);
        });
        buildProjectTree();
        left = new HorizontalLayout(tree);
        LOG.log(Level.FINE, "Found {0} root projects!", projects.size());
    }

    private void showLoginDialog() {
        if (loginWindow == null) {
            loginWindow = new LoginDialog(this);
            loginWindow.setVisible(true);
            loginWindow.setClosable(false);
            loginWindow.setResizable(false);
            loginWindow.center();
            loginWindow.setWidth(35, Unit.PERCENTAGE);
            loginWindow.setHeight(35, Unit.PERCENTAGE);
        } else {
            loginWindow.clear();
        }
        if (!getWindows().contains(loginWindow)) {
            addWindow(loginWindow);
        }
    }

    private Project getParentProject() {
        Object current = tree.getValue();
        Project result = null;
        while (current != null && !(current instanceof Project)) {
            current = tree.getParent(current);
        }
        if (current instanceof Project) {
            result = (Project) current;
        }
        return result;
    }

    @Override
    public boolean checkAnyRights(List<String> rights) {
        boolean result = false;
        if (rights.stream().anyMatch((r) -> (checkRight(r)))) {
            return true;
        }
        return result;
    }

    @Override
    public boolean checkAllRights(List<String> rights) {
        boolean result = true;
        for (String r : rights) {
            if (!checkRight(r)) {
                result = false;
                break;
            }
        }
        return result;
    }

    @Override
    public boolean checkRight(String right) {
        if (user != null) {
            user.update();
            if (user.getRoleList().stream().anyMatch((r)
                    -> (r.getUserRightList().stream().anyMatch((ur)
                            -> (ur.getDescription().equals(right)))))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkProjectRole(Project p, String role) {
        if (user != null) {
            user.update();
            if (user.getUserHasRoleList().stream().anyMatch((uhr)
                    -> (Objects.equals(uhr.getProjectId().getId(), p.getId())
                    && uhr.getRole().getRoleName().equals(role)))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkAnyProjectRole(Project p, List<String> roles) {
        return roles.stream().anyMatch((r) -> (checkProjectRole(p, r)));
    }

    @Override
    public boolean checkAllProjectRoles(Project p, List<String> roles) {
        boolean result = true;
        for (String r : roles) {
            if (!checkProjectRole(p, r)) {
                result = false;
                break;
            }
        }
        return result;
    }

    private void displayTestPlanning(Project p) {
        DesignerScreenProvider provider = Lookup.getDefault()
                .lookup(DesignerScreenProvider.class);
        if (provider != null && p != null) {
            provider.setProject(p);
            updateScreen();
            tabSheet.setSelectedTab(findMainProvider(provider
                    .getComponentCaption()));
        }
    }

    @Override
    public Object getSelectdValue() {
        return tree.getValue();
    }

    public void displayBaseline(Baseline baseline, boolean edit) {
        displayBaseline(baseline, edit, null);
    }

    public void displayBaseline(Baseline baseline,
            boolean edit, RequirementSpec rs) {
        setTabContent(main, new BaselineComponent(baseline, edit, rs),
                REQUIREMENT_REVIEW);
    }

    @Override
    public Component createStepHistoryTable(String title,
            List<History> historyItems, boolean showVersionFields) {
        Grid grid = new HistoryTable(title, historyItems, null,
                showVersionFields,
                "text", "expectedResult", "notes");
        Grid.Column text = grid.getColumn("text");
        text.setHeaderCaption(TRANSLATOR.translate("step.text"));
        Grid.Column result = grid.getColumn("expectedResult");
        result.setHeaderCaption(TRANSLATOR.translate("expected.result"));
        Grid.Column notes = grid.getColumn("notes");
        notes.setHeaderCaption(TRANSLATOR.translate("general.notes"));
        return grid;
    }

    @Override
    public Component createRequirementHistoryTable(String title,
            List<History> historyItems, boolean showVersionFields) {
        Grid grid = new HistoryTable(title, historyItems, "uniqueId",
                showVersionFields,
                "uniqueId", "description", "notes");
        Grid.Column uniqueId = grid.getColumn("uniqueId");
        uniqueId.setHeaderCaption(TRANSLATOR.translate("unique.id"));
        Grid.Column description = grid.getColumn("description");
        description.setHeaderCaption(TRANSLATOR.translate("general.description"));
        Grid.Column notes = grid.getColumn("notes");
        notes.setHeaderCaption(TRANSLATOR.translate("general.notes"));
        return grid;
    }

    private void displayTraceMatrix(Project project) {
        VMWindow w = new VMWindow(TRANSLATOR.translate("trace.matrix"));
        TraceMatrix tm = new TraceMatrix(project);
        VerticalSplitPanel vs = new VerticalSplitPanel();
        vs.setSplitPosition(10, Unit.PERCENTAGE);
        vs.setFirstComponent(tm.getMenu());
        vs.setSecondComponent(tm);
        vs.setSizeFull();
        w.setContent(vs);
        w.setSizeFull();
        addWindow(w);
    }

    @Override
    public AbstractSelect getRequirementSelectionComponent() {
        return new RequirementSelectionComponent(getParentProject());
    }

    @Override
    public Component getDisplayRequirementList(String title,
            List<Requirement> requirementList) {
        return new RequirementListComponent(title, requirementList);
    }

    private void addDeleteExecution(ContextMenu menu) {
        MenuItem create
                = menu.addItem(TRANSLATOR.translate("delete.execution"),
                        DELETE_ICON,
                        (MenuItem selectedItem) -> {//Delete only if no execution has been started yet.
                            TCEExtraction tcee = Tool.extractTCE(tree.getValue());
                            TestCaseExecution tce = tcee.getTestCaseExecution();
                            if (tce == null) {
                                LOG.info("Invalid");
                                Notification.show(TRANSLATOR.translate("delete.error"),
                                        TRANSLATOR.translate("extract.error"),
                                        Notification.Type.ERROR_MESSAGE);
                            } else {
                                TestCase tc = tcee.getTestCase();
                                TestCaseExecutionServer tces
                                = new TestCaseExecutionServer(tce);
                                //Check that it's not being executed yet
                                boolean canDelete = true;
                                for (ExecutionStep es : tces.getExecutionStepList()) {
                                    if (tc == null || Objects.equals(es.getStep().getTestCase()
                                            .getTestCasePK(), tc.getTestCasePK())) {
                                        if (es.getResultId() != null
                                        && es.getResultId().getResultName()
                                                .equals("result.pending")) {
                                            Notification.show(TRANSLATOR.translate("delete.error"),
                                                    TRANSLATOR.translate("result.present"),
                                                    Notification.Type.ERROR_MESSAGE);
                                            //It has a result other than pending.
                                            canDelete = false;
                                        }
                                        if (!es.getExecutionStepHasAttachmentList()
                                                .isEmpty()) {
                                            //It has a result other than pending.
                                            Notification.show(TRANSLATOR.translate("delete.error"),
                                                    TRANSLATOR.translate("attachment.present"),
                                                    Notification.Type.ERROR_MESSAGE);
                                            canDelete = false;
                                        }
                                        if (!es.getExecutionStepHasIssueList()
                                                .isEmpty()) {
                                            //It has a result other than pending.
                                            Notification.show(TRANSLATOR.translate("delete.error"),
                                                    TRANSLATOR.translate("issue.present"),
                                                    Notification.Type.ERROR_MESSAGE);
                                            canDelete = false;
                                        }
                                        if (!canDelete) {
                                            break;
                                        }
                                    }
                                }
                                if (!canDelete) {
                                    MessageBox prompt = MessageBox.createQuestion()
                                            .withCaption(TRANSLATOR.translate("delete.with.issues.title"))
                                            .withMessage(TRANSLATOR.translate("delete.with.issues.message"))
                                            .withYesButton(() -> {
                                                try {
                                                    if (tc != null) {
                                                        tces.removeTestCase(tc);
                                                    } else {
                                                        List<TestCase> toDelete = new ArrayList<>();
                                                        tces.getExecutionStepList().forEach(es -> {
                                                            try {
                                                                toDelete.add(es.getStep().getTestCase());
                                                            } catch (Exception ex) {
                                                                LOG.log(Level.SEVERE, null, ex);
                                                            }
                                                        });
                                                        toDelete.forEach(t -> {
                                                            try {
                                                                tces.removeTestCase(t);
                                                            } catch (Exception ex) {
                                                                LOG.log(Level.SEVERE, null, ex);
                                                            }
                                                        });
                                                        new TestCaseExecutionJpaController(DataBaseManager
                                                                .getEntityManagerFactory())
                                                                .destroy(tce.getId());
                                                    }
                                                    updateProjectList();
                                                    updateScreen();
                                                    displayObject(tces.getEntity());
                                                } catch (Exception ex) {
                                                    LOG.log(Level.SEVERE, null, ex);
                                                }
                                            },
                                                    ButtonOption.focus(),
                                                    ButtonOption
                                                            .icon(VaadinIcons.CHECK))
                                            .withNoButton(() -> {
                                                displayObject(tces.getEntity());
                                            },
                                                    ButtonOption
                                                            .icon(VaadinIcons.CLOSE));
                                    prompt.getWindow().setIcon(ValidationManagerUI.SMALL_APP_ICON);
                                    prompt.open();
                                }
                                if (canDelete) {
                                    try {
                                        if (tc != null) {
                                            tces.removeTestCase(tc);
                                        } else {
                                            ExecutionStepJpaController c
                                            = new ExecutionStepJpaController(DataBaseManager
                                                    .getEntityManagerFactory());
                                            tces.getExecutionStepList().forEach(es -> {
                                                try {
                                                    c.destroy(es.getExecutionStepPK());
                                                } catch (IllegalOrphanException | NonexistentEntityException ex) {
                                                    LOG.log(Level.SEVERE, null, ex);
                                                }
                                            });
                                            new TestCaseExecutionJpaController(DataBaseManager
                                                    .getEntityManagerFactory())
                                                    .destroy(tce.getId());
                                        }
                                        updateProjectList();
                                        updateScreen();
                                        displayObject(tces.getEntity());
                                    } catch (Exception ex) {
                                        LOG.log(Level.SEVERE, null, ex);
                                    }
                                } else {
                                    Notification.show(TRANSLATOR.translate("delete.error!"),
                                            TRANSLATOR.translate("delete.with.issues.message"),
                                            Notification.Type.ERROR_MESSAGE);
                                }
                            }
                        });
        create.setEnabled(checkRight("testplan.planning"));
    }

    private void addExecutionDashboard(ContextMenu menu) {
        MenuItem dashboard
                = menu.addItem(TRANSLATOR.translate("view.execution.dash"),
                        VaadinIcons.DASHBOARD,
                        (MenuItem selectedItem) -> {
                            addWindow(new ExecutionDashboard(Tool.extractTCE(tree
                                    .getValue())));
                        });
        dashboard.setEnabled(checkRight("testplan.planning"));
    }

    private void closeSession() {
        getSession().close();
    }

    private void displayRiskManagement(Project project) {
        VMWindow w = new VMWindow(TRANSLATOR
                .translate("general.risk.management"));
        w.setContent(new RiskManagementComponent(project));
        w.setSizeFull();
        addWindow(w);
    }

    @WebServlet(value = "/*", asyncSupported = true)
    @WebListener
    @VaadinServletConfiguration(productionMode = true,
            ui = ValidationManagerUI.class,
            widgetset = "net.sourceforge.javydreamercsw.validation.manager.web.AppWidgetSet")
    public static class Servlet extends VaadinServlet
            implements ServletContextListener {

        private ScheduledExecutorService scheduler;

        public Servlet() {
            //Build demo tree if needed
            ProjectJpaController controller
                    = new ProjectJpaController(DataBaseManager
                            .getEntityManagerFactory());
            if (DataBaseManager.isDemo()
                    && controller.findProjectEntities().isEmpty()) {
                buildDemoTree();
            }
        }

        @Override
        public void contextInitialized(ServletContextEvent sce) {
            //Connect to the database defined in context.xml
            try {
                DataBaseManager.setPersistenceUnitName("VMPUJNDI");
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            //Check for existance of OpenOffice installation
            VmSetting home = VMSettingServer.getSetting("openoffice.home");
            VmSetting port = VMSettingServer.getSetting("openoffice.port");
            if (home != null && port != null) {
                File homeLocation = new File(home.getStringVal());
                int portNumber = port.getIntVal();
                if (homeLocation.exists() && homeLocation.isDirectory()
                        && portNumber > 0) {
                    try {
                        //Everything seems valid. Start OpenOffice
                        Process p = Runtime.getRuntime().exec(
                                new String[]{
                                    "\"" + homeLocation.getAbsolutePath()
                                    + System.getProperty("file.separator")
                                    + "program"
                                    + System.getProperty("file.separator")
                                    + "soffice\"",
                                    "-headless",
                                    "-nologo",
                                    "-norestore",
                                    "-accept=socket,host=localhost,port="
                                    + portNumber
                                    + ";urp;StarOffice.ServiceManager"});
                        BufferedReader stdInput
                                = new BufferedReader(new InputStreamReader(
                                        p.getInputStream()));

                        BufferedReader stdError
                                = new BufferedReader(new InputStreamReader(
                                        p.getErrorStream()));

                        // read the output from the command
                        StringBuilder sb = new StringBuilder();
                        sb.append("Here is the standard output of the command:")
                                .append("\n");
                        String s;
                        while ((s = stdInput.readLine()) != null) {
                            sb.append(s).append("\n");
                        }

                        // read any errors from the attempted command
                        sb.append("Here is the standard error of the "
                                + "command (if any):").append("\n");
                        while ((s = stdError.readLine()) != null) {
                            sb.append(s).append("\n");
                        }
                    } catch (IOException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                } else {
                    LOG.warning("Invalid configuration for OpenOffice");
                }
            } else {
                LOG.warning("Missing configuration for Open Office!");
            }
            if (DataBaseManager.isDemo()) {
                long reset_period = DataBaseManager.getDemoResetPeriod();
                if (reset_period > 0) {
                    LOG.info("Scheduling demo reset...");
                    scheduler = Executors.newSingleThreadScheduledExecutor();
                    scheduler.scheduleAtFixedRate(new VMDemoResetThread(), 0,
                            reset_period, TimeUnit.MILLISECONDS);
                    LOG.info("Done!");
                }
            }
        }

        @Override
        public void contextDestroyed(ServletContextEvent sce) {
            LOG.info("Context destroyed!");
            if (scheduler != null) {
                scheduler.shutdownNow();
            }
        }
    }

    @Override
    public void handleVersioning(Object o, Runnable r) {
        if (o instanceof Versionable) {
            Versionable ao = (Versionable) o;
            if (Versionable.auditable(ao)) {
                //Set user changing to the current user
                ao.setModifierId(getUser().getId());
                //Now check the level of the change
                CHANGE_LEVEL level = CHANGE_LEVEL.MINOR;
                if (!ao.getHistoryList().isEmpty()) {
                    History latest = ao.getHistoryList().get(ao.getHistoryList()
                            .size() - 1);
                    if (ao.getMajorVersion() > latest.getMajorVersion()) {
                        level = CHANGE_LEVEL.MAJOR;
                    } else if (ao.getMidVersion() > latest.getMidVersion()) {
                        level = CHANGE_LEVEL.MODERATE;
                    }
                }
                switch (level) {
                    case MAJOR:
                    //Fall thru
                    case MODERATE:
                    //Fall thru
                    default:
                        showVersioningPrompt(ao, r);
                        break;
                }
            }
        }
    }

    private void showVersioningPrompt(Versionable ao, Runnable r) {
        VerticalLayout layout = new VerticalLayout();
        TextArea message = new TextArea();
        message.setValue(TRANSLATOR.translate("missing.reason.message"));
        message.setReadOnly(true);
        message.setSizeFull();
        TextArea desc = new TextArea(TRANSLATOR.translate("general.reason"));
        desc.setSizeFull();
        layout.addComponent(message);
        layout.addComponent(desc);
        //Prompt user with reason for change
        MessageBox prompt = MessageBox.createQuestion();
        prompt.withCaption(TRANSLATOR.translate("missing.reason.title"))
                .withMessage(layout)
                .withYesButton(() -> {
                    ao.setReason(desc.getValue());
                    if (r != null) {
                        r.run();
                    }
                },
                        ButtonOption.focus(),
                        ButtonOption
                                .icon(VaadinIcons.CHECK),
                        ButtonOption.disable())
                .withNoButton(ButtonOption
                        .icon(VaadinIcons.CLOSE));
        prompt.getWindow().setIcon(ValidationManagerUI.SMALL_APP_ICON);
        desc.addTextChangeListener((TextChangeEvent event1) -> {
            //Enable if there is a description change.
            prompt.getButton(ButtonType.YES)
                    .setEnabled(!event1.getText().trim().isEmpty());
        });
        prompt.getWindow().setWidth(50, Unit.PERCENTAGE);
        prompt.getWindow().setHeight(50, Unit.PERCENTAGE);
        prompt.open();
    }

    public static Locale getLocale(String loc) {
        Locale locale = Locale.ENGLISH;
        if (loc != null) {
            String[] locales = loc.split("_");
            switch (locales.length) {
                case 1:
                    locale = new Locale(locales[0]);
                    break;
                case 2:
                    locale = new Locale(locales[0], locales[1]);
                    break;
                case 3:
                    locale = new Locale(locales[0], locales[1], locales[2]);
                    break;
                default:
                    locale = Locale.getDefault();
                    break;
            }
        }
        return locale;
    }

    /**
     * @return the LOCALES
     */
    public static List<Locale> getAvailableLocales() {
        return Collections.unmodifiableList(LOCALES);
    }

    @Override
    public boolean sendConvertedFileToUser(final UI app, final File fileToExport,
            final String exportFileName, String mimeType) {
        TemporaryFileDownloadResource resource;
        try {
            resource = new TemporaryFileDownloadResource(app, exportFileName,
                    mimeType, fileToExport);
            if (null == app) {
                UI.getCurrent().getPage().open(resource, exportFileName, false);
            } else {
                app.getPage().open(resource, exportFileName, false);
            }
        } catch (final FileNotFoundException e) {
            LOG.log(Level.WARNING,
                    "Sending file to user failed with FileNotFoundException {0}", e);
            return false;
        }
        return true;
    }

    private void showProjectWizard(Project p) {
        addWindow(new ProjectCreationWizard(new ProjectServer(p)));
    }
}
