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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.grid.dnd.GridDragStartEvent;
import com.vaadin.flow.component.grid.dnd.GridDropEvent;
import com.vaadin.flow.component.grid.dnd.GridDropLocation;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.DemoBuilder;
import net.sourceforge.javydreamercsw.validation.manager.web.core.IMainContentProvider;
import com.validation.manager.core.NotificationProvider;
import com.validation.manager.core.VMException;
import net.sourceforge.javydreamercsw.validation.manager.web.core.VMUI;
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
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.annotation.WebServlet;
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

@Theme(themeClass = Lumo.class)
@PageTitle("Validation Manager")
@PreserveOnRefresh
@SuppressWarnings("serial")
public class ValidationManagerUI extends UI implements VMUI {

    private static final InternationalizationProvider TRANSLATOR
            = Lookup.getDefault().lookup(InternationalizationProvider.class);
    private VMUserServer user = null;
    private static final Logger LOG
            = Logger.getLogger(ValidationManagerUI.class.getSimpleName());
    private LoginDialog loginWindow = null;
    private String projTreeRoot;
    private Component left;
    private final Tabs tabSheet = new Tabs();
    private final VerticalLayout tabContentPanel = new VerticalLayout();
    private final Map<Tab, Component> tabContents = new HashMap<>();
    private final List<Project> projects = new ArrayList<>();
    private ProjectTreeComponent tree;

    {
        //Toggle tab content visibility as the selection changes (the Flow
        //replacement of the v8 TabSheet's per-tab content).
        tabSheet.addSelectedChangeListener(event -> {
            Tab selected = tabSheet.getSelectedTab();
            tabContents.forEach((tab, content) -> {
                content.setVisible(tab.equals(selected));
            });
        });
    }

    /**
     * Select an item in the project tree and expand its ancestors.
     */
    private void showItemInTree(Object item) {
        if (item == null) {
            tree.expand(projTreeRoot);
        } else {
            tree.asSingleSelect().setValue(item);
            Object parent = tree.getTreeData().getParent(item);
            while (parent != null) {
                tree.expand(parent);
                parent = tree.getTreeData().getParent(parent);
            }
        }
    }
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

    public TreeGrid<Object> getTree() {
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
                com.vaadin.flow.component.notification.Notification.show(
                        TRANSLATOR.translate("message.already.logged") + " "
                        + TRANSLATOR.translate("message.already.logged.desc"));
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
        Component c = tabContents.get(target);
        if (c != null) {
            if (c instanceof VerticalLayout) {
                VerticalLayout l = (VerticalLayout) c;
                l.removeAll();
                if (content != null) {
                    l.add(content);
                }
            } else {
                LOG.log(Level.SEVERE, "Invalid target: {0}", target);
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
    public void openDialog(Dialog dialog) {
        dialog.open();
    }

    @Override
    public boolean closeDialog(Dialog dialog) {
        if (dialog.isOpened()) {
            dialog.close();
            return true;
        }
        return false;
    }

    @Override
    public boolean isOpen(Dialog dialog) {
        return dialog.isOpened();
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

    private void addTestCaseAssignment(ContextMenu menu) {
        MenuItem create
                = menu.addItem(TRANSLATOR.translate("assign.test.case.execution"),
                        e -> {
                            VMWindow sw = new VMWindow(TRANSLATOR
                                    .translate("assign.test.case.execution"));
                            net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.FlowWizard w
                            = new net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.FlowWizard();
                            w.addStep(new AssignUserStep(ValidationManagerUI.this,
                                    tree.asSingleSelect().getValue()));
                            w.addListener(new net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardProgressListener() {
                                @Override
                                public void activeStepChanged(
                                        net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardStepActivationEvent event) {
                                    //Do nothing
                                }

                                @Override
                                public void stepSetChanged(
                                        net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardStepSetChangedEvent event) {
                                    //Do nothing
                                }

                                @Override
                                public void stepCompleted(
                                        net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardStepCompletionEvent event) {
                                    //Do nothing
                                }

                                @Override
                                public void wizardCompleted(
                                        net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardCompletedEvent event) {
                                    closeDialog(sw);
                                }

                                @Override
                                public void wizardCancelled(
                                        net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardCancelledEvent event) {
                                    closeDialog(sw);
                                }
                            });
                            w.setSizeFull();
                            sw.add(w);
                            openDialog(sw);
                        });
        create.addComponentAsFirst(new Icon(ASSIGN_ICON));
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
                        e -> {
                            displayProject(new Project(), true);
                        });
        create.setEnabled(checkRight("product.modify"));
    }

    private void createExecutionsMenu(ContextMenu menu) {
        MenuItem create
                = menu.addItem(TRANSLATOR.translate("create.execution"),
                        e -> {
                            int projectId = Integer.parseInt(((String) tree.asSingleSelect().getValue())
                                    .substring(TRANSLATOR.translate("general.execution").length()));
                            displayTestCaseExecution(new TestCaseExecution(),
                                    new ProjectServer(projectId), true);
                        });
        create.setEnabled(checkRight("testplan.planning"));
    }

    private void createTestCaseExecutionPlanMenu(ContextMenu menu) {
        MenuItem create
                = menu.addItem(TRANSLATOR.translate("create.execution.step"),
                        e -> {
                            //TODO: Do something?
                        });
        create.setEnabled(checkRight("testplan.planning"));
        MenuItem edit
                = menu.addItem(TRANSLATOR.translate("edit.execution"),
                        e -> {
                            displayTestCaseExecution((TestCaseExecution) tree
                                    .asSingleSelect().getValue(), true);
                        });
        edit.setEnabled(checkRight("testplan.planning"));
        addDeleteExecution(menu);
        addTestCaseAssignment(menu);
        addExecutionDashboard(menu);
    }

    private void createTestPlanMenu(ContextMenu menu) {
        MenuItem create
                = menu.addItem(TRANSLATOR.translate("create.test.case"),
                        e -> {
                            TestCase tc = new TestCase();
                            tc.setTestPlanList(new ArrayList<>());
                            tc.getTestPlanList().add((TestPlan) tree.asSingleSelect().getValue());
                            tc.setCreationDate(new Date());
                            displayTestCase(tc, true);
                        });
        create.setEnabled(checkRight("testplan.planning"));
        MenuItem edit
                = menu.addItem(TRANSLATOR.translate("edit.test.plan"),
                        e -> {
                            displayTestPlan((TestPlan) tree.asSingleSelect().getValue(),
                                    true);
                        });
        edit.setEnabled(checkRight("testplan.planning"));
        MenuItem export
                = menu.addItem(TRANSLATOR.translate("general.export"),
                        e -> {
                            TestPlan tp = (TestPlan) tree.asSingleSelect().getValue();
                            openDialog(TestCaseExporter
                                    .getTestCaseExporter(tp.getTestCaseList()));
                        });
        export.setEnabled(checkRight("testcase.view"));
    }

    private void createProjectMenu(ContextMenu menu) {
        MenuItem create
                = menu.addItem(TRANSLATOR.translate("create.sub.project"),
                        e -> {
                            Project project = new Project();
                            project.setParentProjectId((Project) tree.asSingleSelect().getValue());
                            displayProject(project, true);
                        });
        create.setEnabled(checkRight("requirement.modify"));
        MenuItem createSpec
                = menu.addItem(TRANSLATOR.translate("create.req.spec"),
                        e -> {
                            RequirementSpec rs = new RequirementSpec();
                            rs.setProject((Project) tree.asSingleSelect().getValue());
                            displayRequirementSpec(rs, true);
                        });
        createSpec.setEnabled(checkRight("requirement.modify"));
        MenuItem createTest
                = menu.addItem(TRANSLATOR.translate("create.test.suite"),
                        e -> {
                            TestProject tp = new TestProject();
                            tp.setProjectList(new ArrayList<>());
                            tp.getProjectList().add((Project) tree.asSingleSelect().getValue());
                            displayTestProject(tp, true);
                        });
        createTest.setEnabled(checkRight("requirement.modify"));
        MenuItem edit
                = menu.addItem(TRANSLATOR.translate("edit.project"),
                        e -> {
                            displayProject((Project) tree.asSingleSelect().getValue(), true);
                        });
        edit.setEnabled(checkRight("product.modify"));
        MenuItem plan
                = menu.addItem(TRANSLATOR.translate("plan.testing"),
                        e -> {
                            displayTestPlanning((Project) tree.asSingleSelect().getValue());
                        });
        plan.setEnabled(checkRight("testplan.planning"));
        MenuItem trace
                = menu.addItem(TRANSLATOR.translate("trace.matrix"),
                        e -> {
                            displayTraceMatrix((Project) tree.asSingleSelect().getValue());
                        });
        trace.setEnabled(checkRight("testplan.planning"));
        MenuItem risk
                = menu.addItem(TRANSLATOR.translate("general.risk.management"),
                        e -> {
                            displayRiskManagement((Project) tree.asSingleSelect().getValue());
                        });
        risk.setEnabled(checkRight("risk.management.view"));
    }

    private void createRequirementMenu(ContextMenu menu) {
        MenuItem edit
                = menu.addItem(TRANSLATOR.translate("edit.req"),
                        e -> {
                            displayRequirement((Requirement) tree.asSingleSelect().getValue(),
                                    true);
                        });
        edit.setEnabled(checkRight("requirement.modify"));
    }

    private void createRequirementSpecMenu(ContextMenu menu) {
        MenuItem create
                = menu.addItem(TRANSLATOR.translate("create.req.spec.node"),
                        e -> {
                            RequirementSpecNode rs = new RequirementSpecNode();
                            rs.setRequirementSpec((RequirementSpec) tree.asSingleSelect().getValue());
                            displayRequirementSpecNode(rs, true);
                        });
        create.setEnabled(checkRight("requirement.modify"));
        MenuItem edit
                = menu.addItem(TRANSLATOR.translate("edit.req.spec"),
                        e -> {
                            displayRequirementSpec((RequirementSpec) tree.asSingleSelect().getValue(),
                                    true);
                        });
        edit.setEnabled(checkRight("requirement.modify"));
        MenuItem baseline
                = menu.addItem(TRANSLATOR.translate("baseline.spec"),
                        e -> {
                            displayBaseline(new Baseline(), true,
                                    (RequirementSpec) tree.asSingleSelect().getValue());
                        });
        baseline.setEnabled(checkRight("testcase.modify"));
    }

    private void createRequirementSpecNodeMenu(ContextMenu menu) {
        MenuItem create
                = menu.addItem(TRANSLATOR.translate("create.requiremnet"),
                        e -> {
                            Requirement r = new Requirement();
                            r.setRequirementSpecNode((RequirementSpecNode) tree
                                    .asSingleSelect().getValue());
                            displayRequirement(r, true);
                        });
        create.setEnabled(checkRight("requirement.modify"));
        MenuItem edit
                = menu.addItem(TRANSLATOR.translate("edit.req.spec.node"),
                        e -> {
                            displayRequirementSpecNode((RequirementSpecNode) tree
                                    .asSingleSelect().getValue(),
                                    true);
                        });
        edit.setEnabled(checkRight("requirement.modify"));
        MenuItem importRequirement
                = menu.addItem(TRANSLATOR.translate("import.requirement"),
                        e -> {// Create a dialog and set the content
                            VMWindow subWindow = new VMWindow(TRANSLATOR
                                    .translate("import.requirement"));
                            VerticalLayout subContent = new VerticalLayout();

                            //Add a checkbox to know if file has headers or not
                            Checkbox cb = new Checkbox(TRANSLATOR.translate("file.has.header"));

                            FileUploader receiver = new FileUploader();
                            Upload upload
                            = new Upload(receiver);
                            upload.addSucceededListener((com.vaadin.flow.component.upload.SucceededEvent event1) -> {
                                try {
                                    subWindow.close();
                                    //TODO: Display the excel file (partially), map columns and import
                                    //Process the file
                                    RequirementImporter importer
                                            = new RequirementImporter(receiver
                                                    .getFile(),
                                                    (RequirementSpecNode) tree
                                                            .asSingleSelect().getValue());

                                    importer.importFile(cb.getValue());
                                    importer.processImport();
                                    buildProjectTree(tree.asSingleSelect().getValue());
                                    updateScreen();
                                } catch (RequirementImportException ex) {
                                    LOG.log(Level.SEVERE, TRANSLATOR.translate("import.error"),
                                            ex);
                                    com.vaadin.flow.component.notification.Notification.show(
                                            TRANSLATOR.translate("import.unsuccessful"));
                                } catch (VMException ex) {
                                    LOG.log(Level.SEVERE, null, ex);
                                }
                            });
                            upload.addFailedListener((com.vaadin.flow.component.upload.FailedEvent event1) -> {
                                LOG.log(Level.SEVERE, "Upload unsuccessful!\n{0}",
                                        event1.getReason());
                                com.vaadin.flow.component.notification.Notification.show(
                                        TRANSLATOR.translate("upload.unsuccessful"));
                                subWindow.close();
                            });
                            subContentAdd(subWindow, cb, upload);
                            // Open it in the UI
                            openDialog(subWindow);
                        });
        importRequirement.setEnabled(checkRight("requirement.modify"));
    }

    private void subContentAdd(VMWindow subWindow, Checkbox cb, Upload upload) {
        VerticalLayout subContent = new VerticalLayout();
        subContent.add(cb);
        subContent.add(upload);
        subWindow.add(subContent);
    }

    private void createTestCaseMenu(ContextMenu menu) {
        MenuItem create
                = menu.addItem(TRANSLATOR.translate("create.step"),
                        e -> {
                            TestCase tc = (TestCase) tree.asSingleSelect().getValue();
                            Step s = new Step();
                            s.setStepSequence(tc.getStepList().size() + 1);
                            s.setTestCase(tc);
                            displayStep(s, true);
                        });
        create.setEnabled(checkRight("requirement.modify"));
        MenuItem edit
                = menu.addItem(TRANSLATOR.translate("edit.test.case"),
                        e -> {
                            displayTestCase((TestCase) tree.asSingleSelect().getValue(), true);
                        });
        edit.setEnabled(checkRight("testcase.modify"));
        MenuItem importSteps
                = menu.addItem(TRANSLATOR.translate("import.step"),
                        e -> { // Create a sub-dialog and set the content
                            VMWindow subWindow
                            = new VMWindow(TRANSLATOR.translate("import.test.case.step"));
                            VerticalLayout subContent = new VerticalLayout();

                            //Add a checkbox to know if file has headers or not
                            Checkbox cb = new Checkbox(TRANSLATOR.translate("file.has.header"));

                            FileUploader receiver = new FileUploader();
                            Upload upload
                            = new Upload(receiver);
                            upload.addSucceededListener((com.vaadin.flow.component.upload.SucceededEvent event1) -> {
                                try {
                                    subWindow.close();
                                    //TODO: Display the excel file (partially), map columns and import
                                    //Process the file
                                    TestCase tc = (TestCase) tree.asSingleSelect().getValue();
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
                                    com.vaadin.flow.component.notification.Notification.show(
                                            TRANSLATOR.translate("import.unsuccessful"));
                                }
                            });
                            upload.addFailedListener((com.vaadin.flow.component.upload.FailedEvent event1) -> {
                                LOG.log(Level.SEVERE, "Upload unsuccessful!\n{0}",
                                        event1.getReason());
                                com.vaadin.flow.component.notification.Notification.show(
                                        TRANSLATOR.translate("upload.unsuccessful"));
                                subWindow.close();
                            });
                            subContent.add(cb);
                            subContent.add(upload);
                            subWindow.add(subContent);
                            // Open it in the UI
                            openDialog(subWindow);
                        });
        importSteps.setEnabled(checkRight("requirement.modify"));
        MenuItem export
                = menu.addItem(TRANSLATOR.translate("general.export"),
                        e -> {
                            TestCase tc = (TestCase) tree.asSingleSelect().getValue();
                            openDialog(TestCaseExporter
                                    .getTestCaseExporter(Arrays.asList(tc)));
                        });
        export.setEnabled(checkRight("testcase.view"));
        addExecutionDashboard(menu);
    }

    private void createStepMenu(ContextMenu menu) {
        MenuItem edit
                = menu.addItem(TRANSLATOR.translate("edit.step"),
                        e -> {
                            displayStep((Step) tree.asSingleSelect().getValue(), true);
                        });
        edit.setEnabled(checkRight("testcase.modify"));
    }

    private void createTestProjectMenu(ContextMenu menu) {
        MenuItem create
                = menu.addItem(TRANSLATOR.translate("create.test.plan"),
                        e -> {
                            TestPlan tp = new TestPlan();
                            tp.setTestProject((TestProject) tree.asSingleSelect().getValue());
                            displayTestPlan(tp, true);
                        });
        create.setEnabled(checkRight("testplan.planning"));
        MenuItem edit
                = menu.addItem(TRANSLATOR.translate("edit.test.project"),
                        e -> {
                            displayTestProject((TestProject) tree.asSingleSelect().getValue(), true);
                        });
        edit.setEnabled(checkRight("testplan.planning"));
    }

    public Component findMainProvider(String id) {
        for (Entry<Tab, Component> entry : tabContents.entrySet()) {
            Component next = entry.getValue();
            if (next.getId().isPresent()
                    && next.getId().get().equals(id)) {
                return next;
            }
        }
        return null;
    }

    @Override
    public void showTab(String id) {
        Component c = findMainProvider(id);
        if (c != null) {
            for (Entry<Tab, Component> entry : tabContents.entrySet()) {
                if (entry.getValue() == c) {
                    tabSheet.setSelectedTab(entry.getKey());
                    return;
                }
            }
        }
    }

    private Component getContentComponent() {
        SplitLayout hsplit = new SplitLayout();
        hsplit.setOrientation(SplitLayout.Orientation.HORIZONTAL);
        hsplit.setSplitterPosition(25);
        if (left != null) {
            if (!(left instanceof Scroller)) {
                left = new Scroller(left);
            }
            if (user != null) {
                hsplit.addToPrimary(left);
            }
        }
        tabSheet.removeAll();
        tabContents.clear();
        tabContentPanel.removeAll();
        //Build the right component
        main = tab(new VerticalLayout(),
                TRANSLATOR.translate("general.main"));
        Lookup.getDefault().lookupAll(IMainContentProvider.class)
                .forEach((provider) -> {
                    Component me = findMainProvider(provider
                            .getComponentCaption());
                    if (me == null) {
                        if (provider.shouldDisplay()) {
                            LOG.log(Level.FINE, "Loading: {0}",
                                    TRANSLATOR.translate(provider
                                            .getComponentCaption()));
                            tab(provider.getContent(),
                                    TRANSLATOR.translate(provider
                                            .getComponentCaption()));
                        }
                    } else {
                        provider.update();
                    }
                    //Hide if needed
                    if (me != null && !provider.shouldDisplay()) {
                        removeTab(me);
                    }
                });
        hsplit.addToSecondary(tabContentPanel);
        //This is a tabbed pane. Enable/Disable the panes based on role
        if (getUser() != null) {
            roles.clear();
            user.update();//Get any recent changes
            user.getRoleList().forEach((r) -> {
                roles.add(r.getRoleName());
            });
        }
        return hsplit;
    }

    /**
     * Remove the tab showing the provided content.
     *
     * @param content Content whose tab should be removed.
     */
    private void removeTab(Component content) {
        tabContents.entrySet().removeIf(entry -> {
            if (entry.getValue() == content) {
                tabContentPanel.remove(content);
                return true;
            }
            return false;
        });
    }

    /**
     * Add a tab with the provided content. Flow replacement of the v8
     * {@code TabSheet.addTab(content, caption)}: the tab header lives in the
     * {@link Tabs} bar and the content is toggled visible on selection.
     *
     * @param content Tab content
     * @param caption Tab caption
     * @return the new tab
     */
    private Tab tab(Component content, String caption) {
        Tab t = new Tab(caption);
        tabContents.put(t, content);
        tabSheet.add(t);
        tabContentPanel.add(content);
        content.setVisible(false);
        return t;
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
        HorizontalLayout gl = new HorizontalLayout();
        gl.add(new Icon(LOGO));
        Span version = new Span(TRANSLATOR.translate("general.version")
                + ": " + getVersion());
        gl.add(version);
        if (getUser() != null) {
            getUser().update();
            //Logout button
            Button logout = new Button(TRANSLATOR.translate("general.logout"));
            logout.addClickListener((event) -> {
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
            gl.add(logout);
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
                notification.setText(""
                        + getUser().getPendingNotifications().size()); //any number, count, etc
            }
            notification.setIcon(new Icon(VaadinIcon.BELL));
            notification.addClickListener((event) -> {
                //TODO: Show notifications screen
            });
            gl.add(notification);
        }
        gl.setWidthFull();
        return gl;
    }

    @Override
    public void updateScreen() {
        //Set up a menu header on top and the content below
        SplitLayout vs = new SplitLayout();
        vs.setOrientation(SplitLayout.Orientation.VERTICAL);
        vs.setSplitterPosition(25);
        //Set up top menu panel
        vs.addToPrimary(getMenu());
        if (getUser() == null) {
            if (tabSheet != null) {
                tabSheet.removeAll();
                tabContents.clear();
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
        vs.addToSecondary(getContentComponent());
        if (getUser() != null) {
            showTab(Lookup.getDefault().lookup(DashboardProvider.class)
                    .getComponentCaption());
        } else {
            if (DataBaseManager.isDemo()) {
                showTab(Lookup.getDefault().lookup(DemoProvider.class)
                        .getComponentCaption());
            }
        }
        removeAll();
        add(vs);
    }

    private void displayProject(Project p, boolean edit) {
        if (p.getId() == null && new TemplateJpaController(DataBaseManager
                .getEntityManagerFactory()).getTemplateCount() > 0) {//Make sure there are templates defined.
            //Prompt the user to see if he wants to use a template or not.
            ConfirmDialog prompt = new ConfirmDialog();
            prompt.setHeader(TRANSLATOR.translate("use.project.wizard.title"));
            prompt.setText(TRANSLATOR.translate("use.project.wizard.message"));
            prompt.setConfirmButton(TRANSLATOR.translate("general.yes"),
                    e -> {
                        //Show creation wizard
                        showProjectWizard(p);
                        prompt.close();
                    });
            prompt.setCancelButton(TRANSLATOR.translate("general.no"),
                    e -> {
                        // Just display it.
                        setTabContent(main, new ProjectComponent(p, edit),
                                "project.viewer");
                        prompt.close();
                    });
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
        tree.setRowsDraggable(true);
        //Remember the dragged item so the drop handler can read it
        tree.addDragStartListener((GridDragStartEvent<Object> event) -> {
            List<Object> dragged = event.getDraggedItems();
            tree.getDataProvider();
            dragData = dragged.isEmpty() ? null : dragged.get(0);
        });
        tree.addDragEndListener(event -> dragData = null);
        // Allow the tree to receive drag drops and handle them
        tree.setDropMode(
                com.vaadin.flow.component.grid.dnd.GridDropMode.ON_TOP_OR_BETWEEN);
        tree.addDropListener((GridDropEvent<Object> event) -> {
            // Make sure the drag source is the same tree
            if (event.getSource() != tree) {
                return;
            }
            // Get the dragged item and the target item
            TreeData<Object> treeData = tree.getTreeData();
            Object sourceItemId = dragData;
            if (sourceItemId == null) {
                return;
            }
            Object targetItemId = event.getDropTargetItem().orElse(null);
            if (targetItemId == null) {
                return;
            }

            LOG.log(Level.INFO, "Source: {0}", sourceItemId);
            LOG.log(Level.INFO, "Target: {0}", targetItemId);

            // On which side of the target the item was dropped
            com.vaadin.flow.component.grid.dnd.GridDropLocation location
                    = event.getDropLocation();

            // Drop right on an item -> make it a child
            switch (location) {
                case ON_TOP:
                    if (!treeData.getChildren(targetItemId).isEmpty()
                            || hasPotentialChildren(targetItemId)) {
                        treeData.setParent(sourceItemId, targetItemId);
                        refreshTree();
                    }
                    break;
                case ABOVE: {
                    boolean valid = true;
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
                            valid = false;
                        }
                    }
                    if (valid) {
                        // Drop above an item -> make it previous
                        Object parentId = treeData.getParent(targetItemId);
                        treeData.setParent(sourceItemId, parentId);
                        treeData.moveAfterSibling(sourceItemId, targetItemId);
                        treeData.moveAfterSibling(targetItemId, sourceItemId);
                        refreshTree();
                        showItemInTree(sourceItemId);
                        updateScreen();
                    }
                    break;
                }
                case BELOW: {
                    // Drop below another item -> make it next
                    Object parentId = treeData.getParent(targetItemId);
                    treeData.setParent(sourceItemId, parentId);
                    treeData.moveAfterSibling(sourceItemId, targetItemId);
                    refreshTree();
                    break;
                }
                default:
                    break;
            }
        });
        tree.asSingleSelect().addValueChangeListener(event -> {
            displayObject(tree.asSingleSelect().getValue());
        });
        //Select item on right click as well
        tree.addItemClickListener((ItemClickEvent<Object> event) -> {
            if (event.getSource() == tree
                    && event.getButton() == 2) {
                tree.asSingleSelect().setValue(event.getItem());
            }
        });
        ContextMenu contextMenu = new ContextMenu(tree);
        tree.addItemClickListener((ItemClickEvent<Object> event) -> {
            if (event.getButton() == 2) {
                menu_removeItems(contextMenu);
                Object selected = event.getItem();
                if (selected instanceof Project) {
                    createProjectMenu(contextMenu);
                } else if (selected instanceof Requirement) {
                    createRequirementMenu(contextMenu);
                } else if (selected instanceof RequirementSpec) {
                    createRequirementSpecMenu(contextMenu);
                } else if (selected instanceof RequirementSpecNode) {
                    createRequirementSpecNodeMenu(contextMenu);
                } else if (selected instanceof TestProject) {
                    createTestProjectMenu(contextMenu);
                } else if (selected instanceof Step) {
                    createStepMenu(contextMenu);
                } else if (selected instanceof TestCase) {
                    createTestCaseMenu(contextMenu);
                } else if (selected instanceof String) {
                    String val = (String) selected;
                    if (val.startsWith("tce")) {
                        createTestExecutionMenu(contextMenu);
                    } else if (val.startsWith("executions")) {
                        createExecutionsMenu(contextMenu);
                    } else {
                        //We are at the root
                        createRootMenu(contextMenu);
                    }
                } else if (selected instanceof TestPlan) {
                    createTestPlanMenu(contextMenu);
                } else if (selected instanceof TestCaseExecution) {
                    createTestCaseExecutionPlanMenu(contextMenu);
                } else if (selected instanceof Baseline) {
//                        createBaselineMenu(contextMenu);
                }
            }
        });
        tree.setSizeFull();
        updateProjectList();
    }

    private transient Object dragData = null;

    private void menu_removeItems(ContextMenu menu) {
        menu.getItems().forEach(menu::remove);
    }

    /**
     * Only Steps and Requirements are valid drag sources for re-parenting.
     */
    private boolean hasPotentialChildren(Object item) {
        return item instanceof Project
                || item instanceof RequirementSpec
                || item instanceof RequirementSpecNode
                || item instanceof TestProject
                || item instanceof TestPlan
                || item instanceof TestCase
                || item instanceof TestCaseExecution
                || (item instanceof String
                && (((String) item).startsWith("tce")
                || ((String) item).startsWith("executions")));
    }

    /**
     * Refresh the tree UI after a structural change to its data.
     */
    private void refreshTree() {
        tree.getDataProvider().refreshAll();
    }

    @Override
    protected void init(VaadinRequest request) {
        LOG.log(Level.INFO, "Current working directory: {0}",
                System.getProperty("user.home"));
        updateScreen();
        //For the code below see: https://vaadin.com/forum#!/thread/1553240/8194235
        getPage().executeJs(
                "window.onbeforeunload = function (e) { var e = e || window.event; return; };");
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
            loginWindow.setCloseOnEsc(false);
            loginWindow.setCloseOnOutsideClick(false);
            loginWindow.setWidth("35%");
            loginWindow.setHeight("35%");
        } else {
            loginWindow.clear();
        }
        if (!loginWindow.isOpened()) {
            openDialog(loginWindow);
        }
    }

    private Project getParentProject() {
        Object current = tree.asSingleSelect().getValue();
        Project result = null;
        while (current != null && !(current instanceof Project)) {
            current = tree.getTreeData().getParent(current);
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
            showTab(provider.getComponentCaption());
        }
    }

    @Override
    public Object getSelectdValue() {
        return tree.asSingleSelect().getValue();
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
        Grid<History> grid = new HistoryTable(title, historyItems, null,
                showVersionFields,
                "text", "expectedResult", "notes");
        grid.getColumnByKey("text").setHeader(TRANSLATOR.translate("step.text"));
        grid.getColumnByKey("expectedResult").setHeader(TRANSLATOR
                .translate("expected.result"));
        grid.getColumnByKey("notes").setHeader(TRANSLATOR.translate("general.notes"));
        return grid;
    }

    @Override
    public Component createRequirementHistoryTable(String title,
            List<History> historyItems, boolean showVersionFields) {
        Grid<History> grid = new HistoryTable(title, historyItems, "uniqueId",
                showVersionFields,
                "uniqueId", "description", "notes");
        grid.getColumnByKey("uniqueId").setHeader(TRANSLATOR.translate("unique.id"));
        grid.getColumnByKey("description").setHeader(TRANSLATOR
                .translate("general.description"));
        grid.getColumnByKey("notes").setHeader(TRANSLATOR.translate("general.notes"));
        return grid;
    }

    private void displayTraceMatrix(Project project) {
        VMWindow w = new VMWindow(TRANSLATOR.translate("trace.matrix"));
        TraceMatrix tm = new TraceMatrix(project);
        SplitLayout vs = new SplitLayout();
        vs.setOrientation(SplitLayout.Orientation.VERTICAL);
        vs.setSplitterPosition(10);
        vs.addToPrimary(tm.getMenu());
        vs.addToSecondary(tm);
        vs.setSizeFull();
        w.add(vs);
        w.setSizeFull();
        openDialog(w);
    }

    @Override
    public com.vaadin.flow.data.selection.MultiSelect<
            ? extends com.vaadin.flow.component.Component, Requirement>
            getRequirementSelectionComponent() {
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
                        e -> {//Delete only if no execution has been started yet.
                            TCEExtraction tcee = Tool.extractTCE(tree.asSingleSelect().getValue());
                            TestCaseExecution tce = tcee.getTestCaseExecution();
                            if (tce == null) {
                                LOG.info("Invalid");
                                com.vaadin.flow.component.notification.Notification.show(
                                        TRANSLATOR.translate("delete.error") + " "
                                        + TRANSLATOR.translate("extract.error"));
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
                                            com.vaadin.flow.component.notification.Notification.show(
                                                    TRANSLATOR.translate("delete.error") + " "
                                                    + TRANSLATOR.translate("result.present"));
                                            //It has a result other than pending.
                                            canDelete = false;
                                        }
                                        if (!es.getExecutionStepHasAttachmentList()
                                                .isEmpty()) {
                                            //It has a result other than pending.
                                            com.vaadin.flow.component.notification.Notification.show(
                                                    TRANSLATOR.translate("delete.error") + " "
                                                    + TRANSLATOR.translate("attachment.present"));
                                            canDelete = false;
                                        }
                                        if (!es.getExecutionStepHasIssueList()
                                                .isEmpty()) {
                                            //It has a result other than pending.
                                            com.vaadin.flow.component.notification.Notification.show(
                                                    TRANSLATOR.translate("delete.error") + " "
                                                    + TRANSLATOR.translate("issue.present"));
                                            canDelete = false;
                                        }
                                        if (!canDelete) {
                                            break;
                                        }
                                    }
                                }
                                if (!canDelete) {
                                    ConfirmDialog prompt = new ConfirmDialog();
                                    prompt.setHeader(TRANSLATOR
                                            .translate("delete.with.issues.title"));
                                    prompt.setText(TRANSLATOR
                                            .translate("delete.with.issues.message"));
                                    prompt.setConfirmButton(
                                            TRANSLATOR.translate("general.yes"),
                                            ev -> {
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
                                                prompt.close();
                                            });
                                    prompt.setCancelButton(
                                            TRANSLATOR.translate("general.no"),
                                            ev -> {
                                                displayObject(tces.getEntity());
                                                prompt.close();
                                            });
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
                                    com.vaadin.flow.component.notification.Notification.show(
                                            TRANSLATOR.translate("delete.error!") + " "
                                            + TRANSLATOR.translate("delete.with.issues.message"));
                                }
                            }
                        });
        create.setEnabled(checkRight("testplan.planning"));
    }

    private void addExecutionDashboard(ContextMenu menu) {
        MenuItem dashboard
                = menu.addItem(TRANSLATOR.translate("view.execution.dash"),
                        e -> {
                            openDialog(new ExecutionDashboard(Tool.extractTCE(tree
                                    .asSingleSelect().getValue())));
                        });
        dashboard.setEnabled(checkRight("testplan.planning"));
    }

    private void closeSession() {
        getSession().close();
    }

    private void displayRiskManagement(Project project) {
        VMWindow w = new VMWindow(TRANSLATOR
                .translate("general.risk.management"));
        w.add(new RiskManagementComponent(project));
        w.setSizeFull();
        openDialog(w);
    }

    @WebServlet(value = "/*", asyncSupported = true)
    @WebListener
    public static class Servlet extends com.vaadin.flow.server.VaadinServlet
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
        layout.add(message);
        layout.add(desc);
        //Prompt user with reason for change
        ConfirmDialog prompt = new ConfirmDialog();
        prompt.setHeader(TRANSLATOR.translate("missing.reason.title"));
        prompt.setText(layout);
        prompt.setConfirmButton(TRANSLATOR.translate("general.yes"),
                e -> {
                    ao.setReason(desc.getValue());
                    if (r != null) {
                        r.run();
                    }
                    prompt.close();
                });
        prompt.setCancelButton(TRANSLATOR.translate("general.cancel"),
                e -> prompt.close());
        prompt.setCloseOnEsc(false);
        desc.setValueChangeMode(ValueChangeMode.LAZY);
        desc.addValueChangeListener(event1 -> {
            //Enable if there is a description change.
            //Flow ConfirmDialog buttons cannot be disabled dynamically;
            //an empty reason is treated as "no change".
            if (desc.getValue().trim().isEmpty()) {
                prompt.setText(layout);
            }
        });
        prompt.setWidth("50%");
        prompt.setHeight("50%");
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
        try {
            StreamResource resource = new StreamResource(exportFileName,
                        () -> {
                        try {
                            return new java.io.FileInputStream(fileToExport);
                        } catch (FileNotFoundException ex) {
                            LOG.log(Level.WARNING,
                                    "Sending file to user failed with "
                                    + "FileNotFoundException {0}", ex);
                            return null;
                        }
                    });
            resource.setContentType(mimeType);
            UI target = app == null ? UI.getCurrent() : app;
            String url = target.getSession().getResourceRegistry()
                    .registerResource(resource).getResourceUri().toString();
            target.getPage().open(url);
            return true;
        } catch (final Exception e) {
            LOG.log(Level.WARNING,
                    "Sending file to user failed {0}", e);
            return false;
        }
    }

    private void showProjectWizard(Project p) {
        openDialog(new ProjectCreationWizard(new ProjectServer(p)));
    }
}
