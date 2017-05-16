package net.sourceforge.javydreamercsw.validation.manager.web;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroupFieldFactory;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.PropertyValueGenerator;
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
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Image;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.Tree.TreeDropCriterion;
import com.vaadin.ui.Tree.TreeTargetDetails;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.DemoBuilder;
import com.validation.manager.core.IMainContentProvider;
import com.validation.manager.core.VMException;
import com.validation.manager.core.VMUI;
import com.validation.manager.core.api.history.Versionable;
import com.validation.manager.core.api.history.Versionable.CHANGE_LEVEL;
import com.validation.manager.core.db.Baseline;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.HistoryField;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.RequirementSpecPK;
import com.validation.manager.core.db.SpecLevel;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestCaseExecution;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.VmSetting;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.BaselineJpaController;
import com.validation.manager.core.db.controller.ExecutionStepJpaController;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.db.controller.RequirementJpaController;
import com.validation.manager.core.db.controller.RequirementSpecJpaController;
import com.validation.manager.core.db.controller.RequirementSpecNodeJpaController;
import com.validation.manager.core.db.controller.SpecLevelJpaController;
import com.validation.manager.core.db.controller.StepJpaController;
import com.validation.manager.core.db.controller.TestCaseExecutionJpaController;
import com.validation.manager.core.db.controller.TestCaseJpaController;
import com.validation.manager.core.db.controller.TestPlanJpaController;
import com.validation.manager.core.db.controller.TestProjectJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.BaselineServer;
import com.validation.manager.core.server.core.ExecutionStepServer;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.RequirementServer;
import com.validation.manager.core.server.core.TestCaseExecutionServer;
import com.validation.manager.core.server.core.TestCaseServer;
import com.validation.manager.core.server.core.VMSettingServer;
import com.validation.manager.core.server.core.VMUserServer;
import com.validation.manager.core.tool.Tool;
import com.validation.manager.core.tool.requirement.importer.RequirementImportException;
import com.validation.manager.core.tool.requirement.importer.RequirementImporter;
import com.validation.manager.core.tool.step.importer.StepImporter;
import com.validation.manager.core.tool.step.importer.TestCaseImportException;
import de.steinwedel.messagebox.ButtonOption;
import de.steinwedel.messagebox.ButtonType;
import de.steinwedel.messagebox.MessageBox;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import net.sourceforge.javydreamercsw.validation.manager.web.dashboard.ExecutionDashboard;
import net.sourceforge.javydreamercsw.validation.manager.web.importer.FileUploader;
import net.sourceforge.javydreamercsw.validation.manager.web.provider.DesignerScreenProvider;
import net.sourceforge.javydreamercsw.validation.manager.web.traceability.TraceMatrix;
import net.sourceforge.javydreamercsw.validation.manager.web.wizard.assign.AssignUserStep;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.vaadin.peter.contextmenu.ContextMenu;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedListener;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedOnTreeItemEvent;
import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

@Theme("vmtheme")
@SuppressWarnings("serial")
@ServiceProvider(service = VMUI.class)
public class ValidationManagerUI extends UI implements VMUI {

    private VMUserServer user = null;
    private static final Logger LOG
            = Logger.getLogger(ValidationManagerUI.class.getSimpleName());
    private static VMDemoResetThread reset = null;
    private LoginDialog loginWindow = null;
    private final String projTreeRoot = "Available Projects";
    private Component left;
    private final TabSheet tabSheet = new TabSheet();
    private final List<Project> projects = new ArrayList<>();
    private Tree tree;
    private Tab main;
    private final List<String> roles = new ArrayList<>();
    private static final ResourceBundle RB = ResourceBundle.getBundle(
            "com.validation.manager.resources.VMMessages");
    private final String REQUIREMENT_REVIEW = "requirement.view";

    /**
     * @return the user
     */
    @Override
    public VMUserServer getUser() {
        return user;
    }

    @Override
    public Tree getTree() {
        return tree;
    }

    /**
     * @param user the user to set
     */
    protected void setUser(VMUserServer user) {
        this.user = user;
        updateScreen();
    }

    private void displayRequirementSpecNode(RequirementSpecNode rsn,
            boolean edit) {
        Panel form = new Panel("Requirement Specification Node Detail");
        FormLayout layout = new FormLayout();
        form.setContent(layout);
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(rsn.getClass());
        binder.setItemDataSource(rsn);
        Field<?> name = binder.buildAndBind("Name", "name");
        layout.addComponent(name);
        Field desc = binder.buildAndBind("Description", "description",
                TextArea.class);
        desc.setSizeFull();
        layout.addComponent(desc);
        Field<?> scope = binder.buildAndBind("Scope", "scope");
        layout.addComponent(scope);
        Button cancel = new Button("Cancel");
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.discard();
            if (rsn.getRequirementSpecNodePK() == null) {
                displayObject(rsn.getRequirementSpec());
            } else {
                displayObject(rsn, false);
            }
        });
        if (edit) {
            if (rsn.getRequirementSpecNodePK() == null) {
                //Creating a new one
                Button save = new Button("Save");
                save.addClickListener((Button.ClickEvent event) -> {
                    try {
                        rsn.setName(name.getValue().toString());
                        rsn.setDescription(desc.getValue().toString());
                        rsn.setScope(scope.getValue().toString());
                        rsn.setRequirementSpec((RequirementSpec) tree.getValue());
                        new RequirementSpecNodeJpaController(DataBaseManager
                                .getEntityManagerFactory()).create(rsn);
                        form.setVisible(false);
                        //Recreate the tree to show the addition
                        updateProjectList();
                        buildProjectTree(rsn);
                        displayObject(rsn, true);
                        updateScreen();
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show("Error creating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(save);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            } else {
                //Editing existing one
                Button update = new Button("Update");
                update.addClickListener((Button.ClickEvent event) -> {
                    rsn.setName(name.getValue().toString());
                    rsn.setDescription(desc.getValue().toString());
                    rsn.setScope(scope.getValue().toString());
                    handleVerioning(rsn, () -> {
                        try {
                            new RequirementSpecNodeJpaController(DataBaseManager
                                    .getEntityManagerFactory()).edit(rsn);
                            displayRequirementSpecNode(rsn, true);
                        } catch (NonexistentEntityException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                            Notification.show("Error updating record!",
                                    ex.getLocalizedMessage(),
                                    Notification.Type.ERROR_MESSAGE);
                        } catch (Exception ex) {
                            LOG.log(Level.SEVERE, null, ex);
                            Notification.show("Error updating record!",
                                    ex.getLocalizedMessage(),
                                    Notification.Type.ERROR_MESSAGE);
                        }
                    });
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(update);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            }
        }
        binder.setReadOnly(!edit);
        binder.bindMemberFields(form);
        layout.setSizeFull();
        form.setSizeFull();
        setTabContent(main, form, REQUIREMENT_REVIEW);
    }

    private void setTabContent(Tab target, Component content,
            String permission) {
        Layout l = (Layout) target.getComponent();
        if (l != null) {
            l.removeAllComponents();
            if (content != null) {
                l.addComponent(content);
            }
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
        Panel form = new Panel("Test Case Execution Detail");
        FormLayout layout = new FormLayout();
        form.setContent(layout);
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(tce.getClass());
        binder.setItemDataSource(tce);
        Field<?> name = binder.buildAndBind("Name", "name");
        name.setRequired(true);
        name.setRequiredError("Please provide a name.");
        layout.addComponent(name);
        Field<?> scope = binder.buildAndBind("Scope", "scope");
        layout.addComponent(scope);
        //TODO: Show when finished
        TextArea conclusion = new TextArea("Conclusion");
        binder.bind(conclusion, "conclusion");
        layout.addComponent(conclusion);
        Button cancel = new Button("Cancel");
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.discard();
            if (tce.getId() == null) {
                displayObject(tree.getValue());
            } else {
                displayObject(tce, false);
            }
        });
        if (edit) {
            if (tce.getId() == null) {
                TestCaseExecutionServer tces = new TestCaseExecutionServer();
                //Creating a new one
                Button save = new Button("Save");
                save.addClickListener((Button.ClickEvent event) -> {
                    if (name.getValue() == null) {
                        Notification.show(name.getRequiredError(),
                                Notification.Type.ERROR_MESSAGE);
                        return;
                    }
                    Map<Requirement, History> history = new HashMap<>();
                    if (ps != null) {
                        List<Requirement> toApprove = new ArrayList<>();
                        for (Requirement r : Tool.extractRequirements(ps)) {
                            //Check each requirement and see if they have minor versions (last version is not baselined)
                            History h = r.getHistoryList().get(r.getHistoryList().size() - 1);
                            if (h.getMajorVersion() == 0
                                    || h.getMidVersion() > 0
                                    || h.getMinorVersion() > 0) {
                                if (r.getHistoryList().size() == 1) {
                                    //Nothing to choose from
                                    history.put(r, h);
                                } else {
                                    toApprove.add(r);
                                }
                            } else {
                                history.put(r, h);
                            }
                        }
                        if (!toApprove.isEmpty()) {
                            MessageBox mb = MessageBox.create();
                            mb.asModal(true)
                                    .withCaption("Non Baselined requirements detected!")
                                    .withMessage("Some requirements are not baselined, "
                                            + "please choose the versions to be tested "
                                            + "under this execution.")
                                    .withButtonAlignment(Alignment.MIDDLE_CENTER)
                                    .withOkButton(() -> {
                                        //Start the wizard
                                        Wizard w = new Wizard();
                                        Window sw = new VMWindow();
                                        w.setDisplayedMaxTitles(3);
                                        toApprove.forEach(r -> {
                                            w.addStep(new SelectRequirementVersionStep(r));
                                        });
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
                                                //Process the selections
                                                w.getSteps().forEach(s -> {
                                                    SelectRequirementVersionStep step
                                                            = (SelectRequirementVersionStep) s;
                                                    history.put(step.getRequirement(),
                                                            step.getHistory());
                                                });
                                                removeWindow(sw);
                                            }

                                            @Override
                                            public void wizardCancelled(WizardCancelledEvent event) {
                                                removeWindow(sw);
                                            }
                                        });
                                        sw.setContent(w);
                                        sw.center();
                                        sw.setModal(true);
                                        sw.setSizeFull();
                                        addWindow(sw);
                                    }, ButtonOption.focus(),
                                            ButtonOption.icon(VaadinIcons.CHECK))
                                    .withCancelButton(
                                            ButtonOption.icon(VaadinIcons.CLOSE)
                                    ).getWindow().setIcon(ValidationManagerUI.SMALL_APP_ICON);
                            mb.open();
                        }
                    }
                    if (!history.isEmpty()) {
                        try {
                            if (conclusion.getValue() != null) {
                                tces.setConclusion(conclusion.getValue());
                            }
                            if (scope.getValue() != null) {
                                tces.setScope(scope.getValue().toString());
                            }
                            tces.setName(name.getValue().toString());
                            tces.write2DB();
                            if (ps != null) {
                                //Process the list
                                ps.getTestProjects(true).forEach(tp -> {
                                    tces.addTestProject(tp);
                                });
                                //Now look thru the ExecutionSteps and assign the right version.
                                tces.getExecutionStepList().forEach(es -> {
                                    try {
                                        ExecutionStepServer ess = new ExecutionStepServer(es);
                                        es.getStep().getRequirementList().forEach(r -> {
                                            ess.getHistoryList().add(history.get(r));
                                        });
                                        ess.write2DB();
                                    } catch (Exception ex) {
                                        LOG.log(Level.SEVERE, null, ex);
                                    }
                                });
                            }
                            tces.write2DB();
                            tces.update(tce, tces.getEntity());
                            updateProjectList();
                            updateScreen();
                            displayObject(tce);
                        } catch (Exception ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(save);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            } else {
                TestCaseExecutionServer tces = new TestCaseExecutionServer(tce);
                //Editing existing one
                Button update = new Button("Update");
                update.addClickListener((Button.ClickEvent event) -> {
                    tces.setConclusion(conclusion.getValue());
                    tces.setScope(scope.getValue().toString());
                    tces.setName(name.getValue().toString());
                    try {
                        handleVerioning(tces, () -> {
                            try {
                                tces.write2DB();
                                tces.update(tce, tces.getEntity());
                                displayObject(tce);
                            } catch (NonexistentEntityException ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show("Error updating record!",
                                        ex.getLocalizedMessage(),
                                        Notification.Type.ERROR_MESSAGE);
                            } catch (Exception ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show("Error updating record!",
                                        ex.getLocalizedMessage(),
                                        Notification.Type.ERROR_MESSAGE);
                            }
                        });
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(update);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            }
        }
        binder.setReadOnly(!edit);
        binder.bindMemberFields(form);
        layout.setSizeFull();
        form.setSizeFull();
        setTabContent(main, form, "testcase.view");
    }

    private void displayExecutionStep(ExecutionStep es) {
        Panel form = new Panel("Execution Step Detail");
        FormLayout layout = new FormLayout();
        form.setContent(layout);
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(es.getClass());
        binder.setItemDataSource(es);
        FieldGroupFieldFactory defaultFactory = binder.getFieldFactory();
        binder.setFieldFactory(new FieldGroupFieldFactory() {

            @Override
            public <T extends Field> T createField(Class<?> dataType, Class<T> fieldType) {
                if (dataType.isAssignableFrom(VmUser.class)) {
                    BeanItemContainer<VmUser> userEntityContainer
                            = new BeanItemContainer<>(VmUser.class);
                    userEntityContainer.addBean(es.getAssignee());
                    Field field = new TextField(es.getAssignee() == null ? "N/A"
                            : es.getAssignee().getFirstName() + " "
                            + es.getAssignee().getLastName());
                    return fieldType.cast(field);
                }

                return defaultFactory.createField(dataType, fieldType);
            }
        });
        layout.addComponent(createStepHistoryTable("Step Details",
                Arrays.asList(es.getStepHistory()), false));
        if (es.getResultId() != null) {
            Field<?> result = binder.buildAndBind("Result",
                    "resultId.resultName");
            layout.addComponent(result);
        }
        if (es.getComment() != null) {
            TextArea comment = new TextArea("Comment");
            binder.bind(comment, "comment");
            layout.addComponent(comment);
        }
        if (es.getAssignee() != null) {
            TextField assignee = new TextField("Assignee");
            VmUser u = es.getAssignee();
            assignee.setValue(u.getFirstName() + " " + u.getLastName());
            assignee.setReadOnly(true);
            layout.addComponent(assignee);
        }
        if (es.getExecutionStart() != null) {
            Field<?> start = binder.buildAndBind("Execution Start",
                    "executionStart");
            layout.addComponent(start);
        }
        if (es.getExecutionEnd() != null) {
            Field<?> end = binder.buildAndBind("Execution End",
                    "executionEnd");
            layout.addComponent(end);
        }
        if (es.getExecutionTime() != null && es.getExecutionTime() > 0) {
            Field<?> time = binder.buildAndBind("Execution Time",
                    "executionTime");
            layout.addComponent(time);
        }
        if (!es.getHistoryList().isEmpty()) {
            layout.addComponent(createRequirementHistoryTable("Related Requirements",
                    es.getHistoryList()));
        }
        Button cancel = new Button("Cancel");
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.discard();
            if (es.getExecutionStepPK() == null) {
                displayObject(tree.getValue());
            } else {
                displayObject(es, false);
            }
        });
        binder.setReadOnly(true);
        binder.bindMemberFields(form);
        layout.setSizeFull();
        form.setSizeFull();
        setTabContent(main, form, "testcase.view");
    }

    private void displayStep(Step s, boolean edit) {
        Panel form = new Panel("Step Detail");
        FormLayout layout = new FormLayout();
        form.setContent(layout);
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(s.getClass());
        binder.setItemDataSource(s);
        Field<?> sequence = binder.buildAndBind("Sequence", "stepSequence");
        layout.addComponent(sequence);
        TextArea text = new TextArea("Text");
        text.setConverter(new ByteToStringConverter());
        binder.bind(text, "text");
        layout.addComponent(text);
        TextArea result = new TextArea("Expected Result");
        result.setConverter(new ByteToStringConverter());
        binder.bind(result, "expectedResult");
        layout.addComponent(result);
        Field notes = binder.buildAndBind("Notes", "notes",
                TextArea.class);
        notes.setSizeFull();
        layout.addComponent(notes);
        tree.select(s);
        if (!s.getRequirementList().isEmpty() && !edit) {
            layout.addComponent(getDisplayRequirementList("Related Requirements",
                    s.getRequirementList()));
        } else {
            AbstractSelect requirements = getRequirementSelectionComponent();
            //Select the exisitng ones.
            if (s.getRequirementList() != null) {
                s.getRequirementList().forEach((r) -> {
                    requirements.select(r);
                });
            }
            requirements.addValueChangeListener(event -> {
                Set<Requirement> selected
                        = (Set<Requirement>) event.getProperty().getValue();
                s.getRequirementList().clear();
                selected.forEach(r -> {
                    s.getRequirementList().add(r);
                });
            });
            layout.addComponent(requirements);
        }
        Button cancel = new Button("Cancel");
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.discard();
            if (s.getStepPK() == null) {
                displayObject(tree.getValue());
            } else {
                displayObject(s, false);
            }
        });
        if (edit) {
            if (s.getStepPK() == null) {
                //Creating a new one
                Button save = new Button("Save");
                save.addClickListener((Button.ClickEvent event) -> {
                    try {
                        s.setExpectedResult(((TextArea) result).getValue()
                                .getBytes("UTF-8"));
                        s.setNotes(notes.getValue() == null ? "null"
                                : notes.getValue().toString());
                        s.setStepSequence(Integer.parseInt(sequence
                                .getValue().toString()));
                        s.setTestCase((TestCase) tree.getValue());
                        s.setText(text.getValue().getBytes("UTF-8"));
                        if (s.getRequirementList() == null) {
                            s.setRequirementList(new ArrayList<>());
                        }
                        new StepJpaController(DataBaseManager
                                .getEntityManagerFactory()).create(s);
                        form.setVisible(false);
                        //Recreate the tree to show the addition
                        updateProjectList();
                        displayObject(s);
                        buildProjectTree(s);
                        updateScreen();
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show("Error creating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(save);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            } else {
                //Editing existing one
                Button update = new Button("Update");
                update.addClickListener((Button.ClickEvent event) -> {
                    try {
                        s.setExpectedResult(((TextArea) result).getValue()
                                .getBytes("UTF-8"));
                        s.setNotes(notes.getValue().toString());
                        s.setStepSequence(Integer.parseInt(sequence.getValue().toString()));
                        s.setText(text.getValue().getBytes("UTF-8"));
                        if (s.getRequirementList() == null) {
                            s.setRequirementList(new ArrayList<>());
                        }
                        handleVerioning(s, () -> {
                            try {
                                new StepJpaController(DataBaseManager
                                        .getEntityManagerFactory()).edit(s);
                                displayStep(s, true);
                            } catch (NonexistentEntityException ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show("Error updating record!",
                                        ex.getLocalizedMessage(),
                                        Notification.Type.ERROR_MESSAGE);
                            } catch (Exception ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show("Error updating record!",
                                        ex.getLocalizedMessage(),
                                        Notification.Type.ERROR_MESSAGE);
                            }
                        });
                    } catch (UnsupportedEncodingException | NumberFormatException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(update);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            }
        }
        binder.setReadOnly(!edit);
        binder.bindMemberFields(form);
        layout.setSizeFull();
        form.setSizeFull();
        setTabContent(main, form, "testcase.view");
    }

    private void displayTestCase(TestCase t, boolean edit) {
        Panel form = new Panel("Test Detail");
        FormLayout layout = new FormLayout();
        form.setContent(layout);
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(t.getClass());
        binder.setItemDataSource(t);
        Field<?> name = binder.buildAndBind("Name", "name");
        layout.addComponent(name);
        TextArea summary = new TextArea("Summary");
        summary.setConverter(new ByteToStringConverter());
        binder.bind(summary, "summary");
        layout.addComponent(summary);
        PopupDateField creation = new PopupDateField("Created on");
        creation.setResolution(Resolution.SECOND);
        creation.setDateFormat("MM-dd-yyyy hh:hh:ss");
        binder.bind(creation, "creationDate");
        layout.addComponent(creation);
        Field<?> active = binder.buildAndBind("Active", "active");
        layout.addComponent(active);
        Field<?> open = binder.buildAndBind("Open", "isOpen");
        layout.addComponent(open);
        Button cancel = new Button("Cancel");
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.discard();
            if (t.getId() == null) {
                displayObject(tree.getValue());
            } else {
                displayObject(t, false);
            }
        });
        if (edit) {
            if (t.getId() == null) {
                //Creating a new one
                Button save = new Button("Save");
                save.addClickListener((Button.ClickEvent event) -> {
                    try {
                        t.setName(name.getValue().toString());
                        t.setSummary(summary.getValue().getBytes("UTF-8"));
                        t.setCreationDate((Date) creation.getValue());
                        t.setActive((Boolean) active.getValue());
                        t.setIsOpen((Boolean) open.getValue());
                        t.getTestPlanList().add((TestPlan) tree.getValue());
                        new TestCaseJpaController(DataBaseManager
                                .getEntityManagerFactory()).create(t);
                        form.setVisible(false);
                        //Recreate the tree to show the addition
                        updateProjectList();
                        buildProjectTree(t);
                        displayTestCase(t, false);
                        updateScreen();
                    } catch (UnsupportedEncodingException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show("Error creating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(save);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            } else {
                //Editing existing one
                Button update = new Button("Update");
                update.addClickListener((Button.ClickEvent event) -> {
                    try {
                        t.setName(name.getValue().toString());
                        t.setSummary(summary.getValue().getBytes("UTF-8"));
                        t.setCreationDate((Date) creation.getValue());
                        t.setActive((Boolean) active.getValue());
                        t.setIsOpen((Boolean) open.getValue());
                        handleVerioning(t, () -> {
                            try {
                                new TestCaseJpaController(DataBaseManager
                                        .getEntityManagerFactory()).edit(t);
                                displayTestCase(t, true);
                            } catch (NonexistentEntityException ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show("Error updating record!",
                                        ex.getLocalizedMessage(),
                                        Notification.Type.ERROR_MESSAGE);
                            } catch (Exception ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show("Error updating record!",
                                        ex.getLocalizedMessage(),
                                        Notification.Type.ERROR_MESSAGE);
                            }
                        });
                    } catch (UnsupportedEncodingException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(update);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            }
        }
        binder.setReadOnly(!edit);
        creation.setEnabled(false);
        binder.bindMemberFields(form);
        layout.setSizeFull();
        form.setSizeFull();
        setTabContent(main, form, "testcase.view");
    }

    private void displayTestPlan(TestPlan tp, boolean edit) {
        Panel form = new Panel("Test Plan Detail");
        FormLayout layout = new FormLayout();
        form.setContent(layout);
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(tp.getClass());
        binder.setItemDataSource(tp);
        Field<?> name = binder.buildAndBind("Name", "name");
        layout.addComponent(name);
        Field notes = binder.buildAndBind("Notes", "notes",
                TextArea.class);
        notes.setSizeFull();
        layout.addComponent(notes);
        Field<?> active = binder.buildAndBind("Active", "active");
        layout.addComponent(active);
        Field<?> open = binder.buildAndBind("Open", "isOpen");
        layout.addComponent(open);
        Button cancel = new Button("Cancel");
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.discard();
            if (tp.getTestPlanPK() == null) {
                displayObject(tree.getValue());
            } else {
                displayObject(tp, false);
            }
        });
        if (edit) {
            if (tp.getTestPlanPK() == null) {
                //Creating a new one
                Button save = new Button("Save");
                save.addClickListener((Button.ClickEvent event) -> {
                    try {
                        tp.setName(name.getValue().toString());
                        tp.setNotes(notes.getValue().toString());
                        tp.setActive((Boolean) active.getValue());
                        tp.setIsOpen((Boolean) open.getValue());
                        new TestPlanJpaController(DataBaseManager
                                .getEntityManagerFactory()).create(tp);
                        form.setVisible(false);
                        //Recreate the tree to show the addition
                        updateProjectList();
                        buildProjectTree(tp);
                        displayTestPlan(tp, false);
                        updateScreen();
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show("Error creating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(save);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            } else {
                //Editing existing one
                Button update = new Button("Update");
                update.addClickListener((Button.ClickEvent event) -> {
                    try {
                        tp.setName(name.getValue().toString());
                        tp.setNotes(notes.getValue().toString());
                        tp.setActive((Boolean) open.getValue());
                        tp.setIsOpen((Boolean) open.getValue());
                        handleVerioning(tp, () -> {
                            try {
                                new TestPlanJpaController(DataBaseManager
                                        .getEntityManagerFactory()).edit(tp);
                                displayTestPlan(tp, true);
                            } catch (NonexistentEntityException ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show("Error updating record!",
                                        ex.getLocalizedMessage(),
                                        Notification.Type.ERROR_MESSAGE);
                            } catch (Exception ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show("Error updating record!",
                                        ex.getLocalizedMessage(),
                                        Notification.Type.ERROR_MESSAGE);
                            }
                        });
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(update);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            }
        }
        binder.setReadOnly(!edit);
        binder.bindMemberFields(form);
        layout.setSizeFull();
        form.setSizeFull();
        setTabContent(main, form, "testcase.view");
    }

    private void displayTestProject(TestProject tp, boolean edit) {
        Panel form = new Panel("Test Project Detail");
        FormLayout layout = new FormLayout();
        form.setContent(layout);
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(tp.getClass());
        binder.setItemDataSource(tp);
        Field<?> name = binder.buildAndBind("Name", "name");
        layout.addComponent(name);
        Field notes = binder.buildAndBind("Notes", "notes",
                TextArea.class);
        notes.setSizeFull();
        layout.addComponent(notes);
        Field<?> active = binder.buildAndBind("Active", "active");
        layout.addComponent(active);
        Button cancel = new Button("Cancel");
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.discard();
            if (tp.getId() == null) {
                displayObject(tree.getValue());
            } else {
                displayObject(tp, false);
            }
        });
        if (edit) {
            if (tp.getId() == null) {
                //Creating a new one
                Button save = new Button("Save");
                save.addClickListener((Button.ClickEvent event) -> {
                    try {
                        tp.setName(name.getValue().toString());
                        tp.setNotes(notes.getValue().toString());
                        tp.setActive((Boolean) active.getValue());
                        new TestProjectJpaController(DataBaseManager
                                .getEntityManagerFactory()).create(tp);
                        form.setVisible(false);
                        //Recreate the tree to show the addition
                        updateProjectList();
                        buildProjectTree(tp);
                        displayTestProject(tp, false);
                        updateScreen();
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show("Error creating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(save);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            } else {
                //Editing existing one
                Button update = new Button("Update");
                update.addClickListener((Button.ClickEvent event) -> {
                    try {
                        tp.setName(name.getValue().toString());
                        tp.setNotes(notes.getValue().toString());
                        tp.setActive((Boolean) active.getValue());
                        handleVerioning(tp, () -> {
                            try {
                                new TestProjectJpaController(DataBaseManager
                                        .getEntityManagerFactory()).edit(tp);
                                displayTestProject(tp, true);
                            } catch (NonexistentEntityException ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show("Error updating record!",
                                        ex.getLocalizedMessage(),
                                        Notification.Type.ERROR_MESSAGE);
                            } catch (Exception ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show("Error updating record!",
                                        ex.getLocalizedMessage(),
                                        Notification.Type.ERROR_MESSAGE);
                            }
                        });
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(update);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            }
        }
        binder.setReadOnly(!edit);
        binder.bindMemberFields(form);
        layout.setSizeFull();
        form.setSizeFull();
        setTabContent(main, form, "testcase.view");
    }

    private void displayRequirementSpec(RequirementSpec rs, boolean edit) {
        Panel form = new Panel("Requirement Specification Detail");
        FormLayout layout = new FormLayout();
        form.setContent(layout);
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(rs.getClass());
        binder.setItemDataSource(rs);
        Field<?> name = binder.buildAndBind("Name", "name");
        layout.addComponent(name);
        Field desc = binder.buildAndBind("Description", "description",
                TextArea.class);
        desc.setSizeFull();
        layout.addComponent(desc);
        Field<?> date = binder.buildAndBind("Modification Date",
                "modificationDate");
        layout.addComponent(date);
        date.setEnabled(false);
        SpecLevelJpaController controller
                = new SpecLevelJpaController(DataBaseManager
                        .getEntityManagerFactory());
        List<SpecLevel> levels = controller.findSpecLevelEntities();
        BeanItemContainer<SpecLevel> specLevelContainer
                = new BeanItemContainer<>(SpecLevel.class, levels);
        ComboBox level = new ComboBox("Spec Level");
        level.setContainerDataSource(specLevelContainer);
        level.getItemIds().forEach(id -> {
            level.setItemCaption(id, translate(((SpecLevel) id).getName()));
        });
        binder.bind(level, "specLevel");
        layout.addComponent(level);
        Button cancel = new Button("Cancel");
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.discard();
            if (rs.getRequirementSpecPK() == null) {
                displayObject(rs.getProject());
            } else {
                displayObject(rs, false);
            }
        });
        if (edit) {
            if (rs.getRequirementSpecPK() == null) {
                //Creating a new one
                Button save = new Button("Save");
                save.addClickListener((Button.ClickEvent event) -> {
                    try {
                        rs.setName(name.getValue().toString());
                        rs.setModificationDate(new Date());
                        rs.setSpecLevel((SpecLevel) level.getValue());
                        rs.setProject(((Project) tree.getValue()));
                        rs.setRequirementSpecPK(new RequirementSpecPK(
                                rs.getProject().getId(),
                                rs.getSpecLevel().getId()));
                        new RequirementSpecJpaController(DataBaseManager
                                .getEntityManagerFactory()).create(rs);
                        form.setVisible(false);
                        //Recreate the tree to show the addition
                        updateProjectList();
                        buildProjectTree(rs);
                        displayObject(rs, true);
                        updateScreen();
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show("Error creating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(save);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            } else {
                //Editing existing one
                Button update = new Button("Update");
                update.addClickListener((Button.ClickEvent event) -> {
                    try {
                        rs.setName(name.getValue().toString());
                        rs.setModificationDate(new Date());
                        rs.setSpecLevel((SpecLevel) level.getValue());
                        handleVerioning(rs, () -> {
                            try {
                                new RequirementSpecJpaController(DataBaseManager
                                        .getEntityManagerFactory()).edit(rs);
                                displayRequirementSpec(rs, true);
                            } catch (NonexistentEntityException ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show("Error updating record!",
                                        ex.getLocalizedMessage(),
                                        Notification.Type.ERROR_MESSAGE);
                            } catch (Exception ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show("Error updating record!",
                                        ex.getLocalizedMessage(),
                                        Notification.Type.ERROR_MESSAGE);
                            }
                        });
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(update);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            }
        }
        binder.setReadOnly(!edit);
        binder.bindMemberFields(form);
        layout.setSizeFull();
        form.setSizeFull();
        setTabContent(main, form, REQUIREMENT_REVIEW);
    }

    private void displayObject(Object item) {
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
        Panel form = new Panel("Requirement Detail");
        FormLayout layout = new FormLayout();
        form.setContent(layout);
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(req.getClass());
        binder.setItemDataSource(req);
        Field<?> id = binder.buildAndBind("Requirement ID", "uniqueId");
        layout.addComponent(id);
        Field desc = binder.buildAndBind("Description", "description",
                TextArea.class);
        desc.setSizeFull();
        layout.addComponent(desc);
        Field notes = binder.buildAndBind("Notes", "notes",
                TextArea.class);
        notes.setSizeFull();
        layout.addComponent(notes);
        if (req.getParentRequirementId() != null) {
            TextField tf = new TextField("Parent");
            tf.setValue(req.getParentRequirementId().getUniqueId());
            tf.setReadOnly(true);
            layout.addComponent(tf);
        }
        if (!req.getRequirementList().isEmpty() && !edit) {
            layout.addComponent(getDisplayRequirementList("Related Requirements",
                    req.getRequirementList()));
        } else if (edit) {
            //Allow user to add children
            AbstractSelect as = getRequirementSelectionComponent();
            req.getRequirementList().forEach(sub -> {
                as.select(sub);
            });
            as.addValueChangeListener(event -> {
                Set<Requirement> selected
                        = (Set<Requirement>) event.getProperty().getValue();
                req.getRequirementList().clear();
                selected.forEach(r -> {
                    req.getRequirementList().add(r);
                });
            });
            layout.addComponent(as);
        }
        Button cancel = new Button("Cancel");
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.discard();
            if (req.getId() == null) {
                displayObject(req.getRequirementSpecNode());
            } else {
                displayRequirement(req, false);
            }
        });
        if (edit) {
            if (req.getId() == null) {
                //Creating a new one
                Button save = new Button("Save");
                save.addClickListener((Button.ClickEvent event) -> {
                    req.setUniqueId(id.getValue().toString());
                    req.setNotes(notes.getValue().toString());
                    req.setDescription(desc.getValue().toString());
                    req.setRequirementSpecNode((RequirementSpecNode) tree
                            .getValue());
                    new RequirementJpaController(DataBaseManager
                            .getEntityManagerFactory()).create(req);
                    form.setVisible(false);
                    //Recreate the tree to show the addition
                    buildProjectTree(req);
                    displayObject(req, true);
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(save);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            } else {
                //Editing existing one
                Button update = new Button("Update");
                update.addClickListener((Button.ClickEvent event) -> {
                    try {
                        RequirementServer rs = new RequirementServer(req);
                        rs.setDescription(((TextArea) desc).getValue());
                        rs.setNotes(((TextArea) notes).getValue());
                        rs.setUniqueId(((TextField) id).getValue());
                        handleVerioning(rs, () -> {
                            try {
                                rs.write2DB();
                                //Recreate the tree to show the addition
                                buildProjectTree(rs.getEntity());
                                displayRequirement(rs.getEntity(), false);
                            } catch (NonexistentEntityException ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show("Error updating record!",
                                        ex.getLocalizedMessage(),
                                        Notification.Type.ERROR_MESSAGE);
                            } catch (Exception ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show("Error updating record!",
                                        ex.getLocalizedMessage(),
                                        Notification.Type.ERROR_MESSAGE);
                            }
                        });
                        form.setVisible(false);
                    } catch (VMException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(update);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            }
        }
        try {
            //Add a history section
            List<History> versions = new RequirementServer(req).getHistoryList();
            if (!versions.isEmpty()) {
                layout.addComponent(createRequirementHistoryTable(
                        "History", versions));
            }
        } catch (VMException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        binder.setBuffered(true);
        binder.setReadOnly(!edit);
        binder.bindMemberFields(form);
        form.setSizeFull();
        setTabContent(main, form, REQUIREMENT_REVIEW);
    }

    // @return the current application instance
    public static ValidationManagerUI getInstance() {
        return (ValidationManagerUI) ValidationManagerUI
                .getCurrent();
    }

    private static void buildDemoTree() {
        try {
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
        tree.removeAllItems();
        tree.addItem(projTreeRoot);
        projects.forEach((p) -> {
            if (p.getParentProjectId() == null) {
                //TODO: Check if you have permissions on project to see it.
                addProject(p, tree);
            }
        });
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
        ContextMenu.ContextMenuItem create
                = menu.addItem("Assign Test Case Execution", ASSIGN_ICON);
        create.setEnabled(checkRight("testplan.planning"));
        create.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    Wizard w = new Wizard();
                    Window sw = new VMWindow();
                    w.addStep(new AssignUserStep(this, tree.getValue()));
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
                    sw.center();
                    sw.setModal(true);
                    sw.setSizeFull();
                    addWindow(sw);
                });
    }

    public TCEExtraction extractTCE(Object key) {
        TestCaseExecutionServer tce = null;
        TestCaseServer tcs = null;
        if (key instanceof String) {
            String item = (String) key;
            String tceIdS = item.substring(item.indexOf("-") + 1,
                    item.lastIndexOf("-"));
            try {
                int tceId = Integer.parseInt(tceIdS);
                LOG.log(Level.FINE, "{0}", tceId);
                tce = new TestCaseExecutionServer(tceId);
            } catch (NumberFormatException nfe) {
                LOG.log(Level.WARNING, "Unable to find TCE: " + tceIdS, nfe);
            }
            try {
                int tcId = Integer.parseInt(item.substring(item
                        .lastIndexOf("-") + 1));
                LOG.log(Level.FINE, "{0}", tcId);
                tcs = new TestCaseServer(tcId);
            } catch (NumberFormatException nfe) {
                LOG.log(Level.WARNING, "Unable to find TCE: " + tceIdS, nfe);
            }
        } else if (key instanceof TestCaseExecution) {
            //It is a TestCaseExecution
            tce = new TestCaseExecutionServer((TestCaseExecution) key);
        } else {
            LOG.log(Level.SEVERE, "Unexpected key: {0}", key);
            tce = null;
        }
        return new TCEExtraction(tce, tcs);
    }

    private void createTestExecutionMenu(ContextMenu menu) {
        addDeleteExecution(menu);
        addTestCaseAssignment(menu);
        addExecutionDashboard(menu);
    }

    private void createRootMenu(ContextMenu menu) {
        ContextMenu.ContextMenuItem create
                = menu.addItem("Create Project", PROJECT_ICON);
        create.setEnabled(checkRight("product.modify"));
        create.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    displayProject(new Project(), true);
                });
    }

    private void createExecutionsMenu(ContextMenu menu) {
        ContextMenu.ContextMenuItem create
                = menu.addItem("Create Execution", EXECUTIONS_ICON);
        create.setEnabled(checkRight("testplan.planning"));
        create.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    int projectId = Integer.parseInt(((String) tree.getValue())
                            .substring("executions".length()));
                    displayTestCaseExecution(new TestCaseExecution(),
                            new ProjectServer(projectId), true);
                });
    }

    private void createTestCaseExecutionPlanMenu(ContextMenu menu) {
        ContextMenu.ContextMenuItem create
                = menu.addItem("Create Execution Step", SPEC_ICON);
        create.setEnabled(checkRight("testplan.planning"));
        ContextMenu.ContextMenuItem edit
                = menu.addItem("Edit Execution", EXECUTION_ICON);
        edit.setEnabled(checkRight("testplan.planning"));
        edit.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    displayTestCaseExecution((TestCaseExecution) tree
                            .getValue(), true);
                });
        addDeleteExecution(menu);
        addTestCaseAssignment(menu);
        addExecutionDashboard(menu);
    }

    private void createTestPlanMenu(ContextMenu menu) {
        ContextMenu.ContextMenuItem create
                = menu.addItem("Create Test Case", SPEC_ICON);
        create.setEnabled(checkRight("testplan.planning"));
        ContextMenu.ContextMenuItem edit
                = menu.addItem("Edit Test Plan", SPEC_ICON);
        edit.setEnabled(checkRight("testplan.planning"));
        edit.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    displayTestPlan((TestPlan) tree.getValue(),
                            true);
                });
        create.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    TestCase tc = new TestCase();
                    tc.setTestPlanList(new ArrayList<>());
                    tc.getTestPlanList().add((TestPlan) tree.getValue());
                    tc.setCreationDate(new Date());
                    displayTestCase(tc, true);
                });
    }

    private void createProjectMenu(ContextMenu menu) {
        ContextMenu.ContextMenuItem create
                = menu.addItem("Create Sub Project", PROJECT_ICON);
        create.setEnabled(checkRight("requirement.modify"));
        create.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    Project project = new Project();
                    project.setParentProjectId((Project) tree.getValue());
                    displayProject(project, true);
                });
        ContextMenu.ContextMenuItem createSpec
                = menu.addItem("Create Requirement Spec", SPEC_ICON);
        createSpec.setEnabled(checkRight("requirement.modify"));
        createSpec.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    RequirementSpec rs = new RequirementSpec();
                    rs.setProject((Project) tree.getValue());
                    displayRequirementSpec(rs, true);
                });
        ContextMenu.ContextMenuItem createTest
                = menu.addItem("Create Test Suite", TEST_SUITE_ICON);
        createTest.setEnabled(checkRight("requirement.modify"));
        ContextMenu.ContextMenuItem edit
                = menu.addItem("Edit Project", EDIT_ICON);
        edit.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    displayProject((Project) tree.getValue(), true);
                });
        edit.setEnabled(checkRight("product.modify"));
        createTest.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    TestProject tp = new TestProject();
                    tp.setProjectList(new ArrayList<>());
                    tp.getProjectList().add((Project) tree.getValue());
                    displayTestProject(tp, true);
                });
        ContextMenu.ContextMenuItem plan
                = menu.addItem("Plan Testing", PLAN_ICON);
        plan.setEnabled(checkRight("testplan.planning"));
        plan.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    displayTestPlanning((Project) tree.getValue());
                });
        ContextMenu.ContextMenuItem trace
                = menu.addItem("Trace Matrix", VaadinIcons.SPLIT);
        trace.setEnabled(checkRight("testplan.planning"));
        trace.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    displayTraceMatrix((Project) tree.getValue());
                });
    }

    private void createRequirementMenu(ContextMenu menu) {
        ContextMenu.ContextMenuItem edit
                = menu.addItem("Edit Requirement", EDIT_ICON);
        edit.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    displayRequirement((Requirement) tree.getValue(),
                            true);
                });
        edit.setEnabled(checkRight("requirement.modify"));
    }

    private void createRequirementSpecMenu(ContextMenu menu) {
        ContextMenu.ContextMenuItem create
                = menu.addItem("Create Requirement Spec Node", SPEC_ICON);
        create.setEnabled(checkRight("requirement.modify"));
        ContextMenu.ContextMenuItem edit
                = menu.addItem("Edit Requirement Spec", SPEC_ICON);
        edit.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    displayRequirementSpec((RequirementSpec) tree.getValue(),
                            true);
                });
        edit.setEnabled(checkRight("requirement.modify"));
        create.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    RequirementSpecNode rs = new RequirementSpecNode();
                    rs.setRequirementSpec((RequirementSpec) tree.getValue());
                    displayRequirementSpecNode(rs, true);
                });
        ContextMenu.ContextMenuItem baseline
                = menu.addItem("Baseline Specification", BASELINE_ICON);
        baseline.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    displayBaseline(new Baseline(), true,
                            (RequirementSpec) tree.getValue());
                });
        baseline.setEnabled(checkRight("testcase.modify"));
    }

    private void createRequirementSpecNodeMenu(ContextMenu menu) {
        ContextMenu.ContextMenuItem create
                = menu.addItem("Create Requirement", VaadinIcons.PLUS);
        create.setEnabled(checkRight("requirement.modify"));
        create.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    Requirement r = new Requirement();
                    r.setRequirementSpecNode((RequirementSpecNode) tree
                            .getValue());
                    displayRequirement(r, true);
                });
        ContextMenu.ContextMenuItem edit
                = menu.addItem("Edit Requirement Spec Node", EDIT_ICON);
        edit.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    displayRequirementSpecNode((RequirementSpecNode) tree
                            .getValue(),
                            true);
                });
        edit.setEnabled(checkRight("requirement.modify"));
        ContextMenu.ContextMenuItem importRequirement
                = menu.addItem("Import Requirements", IMPORT_ICON);
        importRequirement.setEnabled(checkRight("requirement.modify"));
        importRequirement.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    // Create a sub-window and set the content
                    Window subWindow = new VMWindow("Import Requirements");
                    VerticalLayout subContent = new VerticalLayout();
                    subWindow.setContent(subContent);

                    //Add a checkbox to know if file has headers or not
                    CheckBox cb = new CheckBox("File has header row?");

                    FileUploader receiver = new FileUploader();
                    Upload upload
                    = new Upload("Upload Excel Spreadsheet here", receiver);
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
                            LOG.log(Level.SEVERE, "Error processing import!",
                                    ex);
                            Notification.show("Importing unsuccessful!",
                                    Notification.Type.ERROR_MESSAGE);
                        } catch (VMException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                    });
                    upload.addFailedListener((Upload.FailedEvent event1) -> {
                        LOG.log(Level.SEVERE, "Upload unsuccessful!\n{0}",
                                event1.getReason());
                        Notification.show("Upload unsuccessful!",
                                Notification.Type.ERROR_MESSAGE);
                        subWindow.close();
                    });
                    subContent.addComponent(cb);
                    subContent.addComponent(upload);

                    // Center it in the browser window
                    subWindow.center();

                    // Open it in the UI
                    addWindow(subWindow);
                });
    }

    private void createTestCaseMenu(ContextMenu menu) {
        ContextMenu.ContextMenuItem create
                = menu.addItem("Create Step", VaadinIcons.PLUS);
        create.setEnabled(checkRight("requirement.modify"));
        ContextMenu.ContextMenuItem edit
                = menu.addItem("Edit Test Case", EDIT_ICON);
        edit.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    displayTestCase((TestCase) tree.getValue(), true);
                });
        edit.setEnabled(checkRight("testcase.modify"));
        create.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    TestCase tc = (TestCase) tree.getValue();
                    Step s = new Step();
                    s.setStepSequence(tc.getStepList().size() + 1);
                    s.setTestCase(tc);
                    displayStep(s, true);
                });
        ContextMenu.ContextMenuItem importSteps
                = menu.addItem("Import Steps", IMPORT_ICON);
        importSteps.setEnabled(checkRight("requirement.modify"));
        importSteps.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    // Create a sub-window and set the content
                    Window subWindow = new VMWindow("Import Test Case Steps");
                    VerticalLayout subContent = new VerticalLayout();
                    subWindow.setContent(subContent);

                    //Add a checkbox to know if file has headers or not
                    CheckBox cb = new CheckBox("File has header row?");

                    FileUploader receiver = new FileUploader();
                    Upload upload
                    = new Upload("Upload Excel Spreadsheet here", receiver);
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
                            buildProjectTree(new TestCaseServer(tc.getId())
                                    .getEntity());
                            updateScreen();
                        } catch (TestCaseImportException ex) {
                            LOG.log(Level.SEVERE, "Error processing import!",
                                    ex);
                            Notification.show("Importing unsuccessful!",
                                    Notification.Type.ERROR_MESSAGE);
                        }
                    });
                    upload.addFailedListener((Upload.FailedEvent event1) -> {
                        LOG.log(Level.SEVERE, "Upload unsuccessful!\n{0}",
                                event1.getReason());
                        Notification.show("Upload unsuccessful!",
                                Notification.Type.ERROR_MESSAGE);
                        subWindow.close();
                    });
                    subContent.addComponent(cb);
                    subContent.addComponent(upload);

                    // Center it in the browser window
                    subWindow.center();

                    // Open it in the UI
                    addWindow(subWindow);
                });
        addExecutionDashboard(menu);
    }

    private void createStepMenu(ContextMenu menu) {
        ContextMenu.ContextMenuItem edit
                = menu.addItem("Edit Step", EDIT_ICON);
        edit.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    displayStep((Step) tree.getValue(), true);
                });
        edit.setEnabled(checkRight("testcase.modify"));
    }

    private void createTestProjectMenu(ContextMenu menu) {
        ContextMenu.ContextMenuItem create
                = menu.addItem("Create Test Plan", VaadinIcons.PLUS);
        create.setEnabled(checkRight("testplan.planning"));
        ContextMenu.ContextMenuItem edit
                = menu.addItem("Edit Test Project", EDIT_ICON);
        edit.setEnabled(checkRight("testplan.planning"));
        edit.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    displayTestProject((TestProject) tree.getValue(), true);
                });
        create.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    TestPlan tp = new TestPlan();
                    tp.setTestProject((TestProject) tree.getValue());
                    displayTestPlan(tp, true);
                });
    }

    @Override
    public String translate(String mess) {
        return RB.containsKey(mess) ? RB.getString(mess) : mess;
    }

    private Component findMainProvider(String id) {
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
        //Build the right component
        if (hsplit.getSecondComponent() == null) {
            if (main == null) {
                main = tabSheet.addTab(new VerticalLayout(), "Main");
            }
            Lookup.getDefault().lookupAll(IMainContentProvider.class)
                    .forEach((provider) -> {
                        Iterator<Component> it = tabSheet.iterator();
                        Component me = findMainProvider(provider
                                .getComponentCaption());
                        if (me == null) {
                            if (provider.shouldDisplay()) {
                                tabSheet.addTab(provider
                                        .getContent(),
                                        translate(provider
                                                .getComponentCaption()));
                            }
                        } else {
                            provider.update();
                        }
                        //Hide if needed
                        if (!provider.shouldDisplay()) {
                            tabSheet.removeComponent(me);
                        }
                    });
            hsplit.setSecondComponent(tabSheet);
        }
        //This is a tabbed pane. Enable/Disable the panes based on role
        if (getUser() != null) {
            roles.clear();
            user.update();//Get any recent changes
            user.getRoleList().forEach((r) -> {
                roles.add(r.getRoleName());
            });
        }
        if (main != null) {
            main.setVisible(user != null);
        }
        hsplit.setSplitPosition(25, Unit.PERCENTAGE);
        return hsplit;
    }

    private Component getMenu() {
        GridLayout gl = new GridLayout(3, 3);
        gl.addComponent(new Image("", LOGO), 0, 0);
        if (getUser() != null) {
            //Logout button
            Button logout = new Button("Log out");
            logout.addClickListener((Button.ClickEvent event) -> {
                try {
                    user.write2DB();
                    user = null;
                    main = null;
                    updateScreen();
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            });
            gl.addComponent(logout, 2, 0);
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
        setContent(vs);
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
            Lookup.getDefault().lookupAll(IMainContentProvider.class)
                    .forEach(p -> {
                        p.processNotification();
                    });
        }
        //Add the content
        vs.setSecondComponent(getContentComponent());
    }

    private void displayProject(Project p, boolean edit) {
        // Bind it to a component
        Panel form = new Panel("Project Detail");
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        FormLayout layout = new FormLayout();
        form.setContent(layout);
        BeanFieldGroup binder = new BeanFieldGroup(p.getClass());
        binder.setItemDataSource(p);
        Field<?> name = binder.buildAndBind("Name", "name");
        Field notes = binder.buildAndBind("Notes", "notes",
                TextArea.class);
        notes.setSizeFull();
        name.setRequired(true);
        name.setRequiredError("Please provide a name.");
        layout.addComponent(name);
        layout.addComponent(notes);
        Button cancel = new Button("Cancel");
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.discard();
            if (p.getId() == null) {
                displayObject(tree.getValue());
            } else {
                displayObject(p, false);
            }
        });
        if (edit) {
            if (p.getId() == null) {
                //Creating a new one
                Button save = new Button("Save");
                save.addClickListener((Button.ClickEvent event) -> {
                    if (name.getValue() == null) {
                        Notification.show(name.getRequiredError(),
                                Notification.Type.ERROR_MESSAGE);
                        return;
                    }
                    p.setName(name.getValue().toString());
                    if (notes.getValue() != null) {
                        p.setNotes(notes.getValue().toString());
                    }
                    new ProjectJpaController(DataBaseManager
                            .getEntityManagerFactory()).create(p);
                    form.setVisible(false);
                    //Recreate the tree to show the addition
                    updateProjectList();
                    buildProjectTree(p);
                    displayProject(p, false);
                    updateScreen();
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(save);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            } else {
                //Editing existing one
                Button update = new Button("Update");
                update.addClickListener((Button.ClickEvent event) -> {
                    handleVerioning(p, () -> {
                        try {
                            p.setName(name.getValue().toString());
                            if (notes.getValue() != null) {
                                p.setNotes(notes.getValue().toString());
                            }
                            new ProjectJpaController(DataBaseManager
                                    .getEntityManagerFactory()).edit(p);
                        } catch (NonexistentEntityException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                            Notification.show("Error updating record!",
                                    ex.getLocalizedMessage(),
                                    Notification.Type.ERROR_MESSAGE);
                        } catch (Exception ex) {
                            LOG.log(Level.SEVERE, null, ex);
                            Notification.show("Error updating record!",
                                    ex.getLocalizedMessage(),
                                    Notification.Type.ERROR_MESSAGE);
                        }
                    });
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(update);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            }
        }

        binder.setBuffered(
                true);
        binder.setReadOnly(
                !edit);
        binder.bindMemberFields(form);

        form.setSizeFull();

        setTabContent(main, form,
                "project.viewer");
    }

    private void addRequirementSpec(RequirementSpec rs, Tree tree) {
        // Add the item as a regular item.
        tree.addItem(rs);
        tree.setItemCaption(rs, rs.getName());
        tree.setItemIcon(rs, SPEC_ICON);
        // Set it to be a child.
        tree.setParent(rs, rs.getProject());
        if (rs.getRequirementSpecNodeList().isEmpty()
                && rs.getBaselineList().isEmpty()) {
            //No children
            tree.setChildrenAllowed(rs, false);
        } else {
            rs.getRequirementSpecNodeList().forEach((rsn) -> {
                addRequirementSpecsNode(rsn, tree);
            });
            //Add the baseline to the spec
            rs.getBaselineList().forEach(bl -> {
                addBaseline(bl, tree);
            });
        }
    }

    private void addRequirementSpecsNode(RequirementSpecNode rsn, Tree tree) {
        // Add the item as a regular item.
        tree.addItem(rsn);
        tree.setItemCaption(rsn, rsn.getName());
        tree.setItemIcon(rsn, SPEC_ICON);
        // Set it to be a child.
        tree.setParent(rsn, rsn.getRequirementSpec());
        if (rsn.getRequirementList().isEmpty()) {
            //No children
            tree.setChildrenAllowed(rsn, false);
        } else {
            ArrayList<Requirement> list
                    = new ArrayList<>(rsn.getRequirementList());
            Collections.sort(list,
                    (Requirement o1, Requirement o2)
                    -> o1.getUniqueId().compareTo(o2.getUniqueId()));
            list.forEach((req) -> {
                addRequirement(req, tree);
            });
        }
    }

    private void addTestProject(TestProject tp, Tree tree) {
        tree.addItem(tp);
        tree.setItemCaption(tp, tp.getName());
        tree.setItemIcon(tp, TEST_SUITE_ICON);
        tree.setParent(tp, tp.getProjectList().get(0));
        boolean children = false;
        if (!tp.getTestPlanList().isEmpty()) {
            tp.getTestPlanList().forEach((plan) -> {
                addTestPlan(plan, tree);
            });
            children = true;
        }
        tree.setChildrenAllowed(tp, children);
    }

    private void addTestPlan(TestPlan tp, Tree tree) {
        tree.addItem(tp);
        tree.setItemCaption(tp, tp.getName());
        tree.setItemIcon(tp, TEST_PLAN_ICON);
        tree.setParent(tp, tp.getTestProject());
        if (!tp.getTestCaseList().isEmpty()) {
            tp.getTestCaseList().forEach((tc) -> {
                addTestCase(tc, tp, tree);
            });
        }
    }

    private void addTestCase(TestCase t, TestPlan plan, Tree tree) {
        tree.addItem(t);
        tree.setItemCaption(t, t.getName());
        tree.setItemIcon(t, TEST_ICON);
        tree.setParent(t, plan);
        List<Step> stepList = t.getStepList();
        Collections.sort(stepList, (Step o1, Step o2)
                -> o1.getStepSequence() - o2.getStepSequence());
        stepList.forEach((s) -> {
            addStep(s, tree);
        });
    }

    private void addStep(Step s, Tree tree) {
        addStep(s, tree, null);
    }

    private void addStep(Step s, Tree tree, String parent) {
        tree.addItem(s);
        tree.setItemCaption(s, "Step # " + s.getStepSequence());
        tree.setItemIcon(s, STEP_ICON);
        Object parentId = s.getTestCase();
        if (parent != null) {
            parentId = parent;
        }
        tree.setParent(s, parentId);
        tree.setChildrenAllowed(s, false);
    }

    private void addRequirement(Requirement req, Tree tree) {
        // Add the item as a regular item.
        tree.addItem(req);
        tree.setItemCaption(req, req.getUniqueId());
        tree.setItemIcon(req, REQUIREMENT_ICON);
        tree.setParent(req, req.getRequirementSpecNode());
        //No children
        tree.setChildrenAllowed(req, false);
    }

    private void addTestCaseExecutions(String parent, TestCaseExecution tce,
            Tree tree) {
        tree.addItem(tce);
        tree.setItemCaption(tce, tce.getName());
        tree.setItemIcon(tce, EXECUTION_ICON);
        tree.setParent(tce, parent);
        for (ExecutionStep es : tce.getExecutionStepList()) {
            //Group under the Test Case
            TestCase tc = es.getStep().getTestCase();
            Collection<?> children = tree.getChildren(tce);
            String node = "tce-" + tce.getId() + "-" + tc.getId();
            boolean add = true;
            if (children != null) {
                //Check if already added as children
                for (Object o : children) {
                    if (o.equals(node)) {
                        add = false;
                        break;
                    }
                }
            }
            if (add) {
                //Add Test Case if not there
                tree.addItem(node);
                tree.setItemCaption(node, tc.getName());
                tree.setItemIcon(node, TEST_ICON);
                tree.setParent(node, tce);
            }
            tree.addItem(es);
            tree.setItemCaption(es, "Step #" + es.getStep().getStepSequence());
            //Use icon based on result of step
            tree.setItemIcon(es, STEP_ICON);
            tree.setParent(es, node);
            tree.setChildrenAllowed(es, false);
        }
    }

    @Override
    public void addProject(Project p, Tree tree) {
        tree.addItem(p);
        tree.setItemCaption(p, p.getName());
        tree.setParent(p, p.getParentProjectId() == null
                ? projTreeRoot : p.getParentProjectId());
        tree.setItemIcon(p, PROJECT_ICON);
        boolean children = false;
        if (!p.getProjectList().isEmpty()) {
            p.getProjectList().forEach((sp) -> {
                addProject(sp, tree);
            });
            children = true;
        }
        if (!p.getRequirementSpecList().isEmpty()) {
            p.getRequirementSpecList().forEach((rs) -> {
                addRequirementSpec(rs, tree);
            });
            children = true;
        }
        if (!p.getTestProjectList().isEmpty()) {
            p.getTestProjectList().forEach((tp) -> {
                addTestProject(tp, tree);
            });
            children = true;
        }
        List<TestCaseExecution> executions = TestCaseExecutionServer
                .getExecutions(p);
        String id = "executions" + p.getId();
        tree.addItem(id);
        tree.setItemCaption(id, "Executions");
        tree.setItemIcon(id, EXECUTIONS_ICON);
        tree.setParent(id, p);
        executions.forEach((tce) -> {
            addTestCaseExecutions(id, tce, tree);
        });
        if (!children) {
            // No subprojects
            tree.setChildrenAllowed(p, false);
        }
    }

    @Override
    protected void init(VaadinRequest request) {
        ProjectJpaController controller
                = new ProjectJpaController(DataBaseManager
                        .getEntityManagerFactory());

        if (DataBaseManager.isDemo()
                && controller.findProjectEntities().isEmpty()) {
            buildDemoTree();
        }
        tree = new Tree();
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
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setAsContextMenuOf(tree);
        ContextMenuOpenedListener.TreeListener treeItemListener
                = (ContextMenuOpenedOnTreeItemEvent event) -> {
                    contextMenu.removeAllItems();
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
                };
        contextMenu.addContextMenuTreeListener(treeItemListener);
        tree.setImmediate(true);
        tree.expandItem(projTreeRoot);
        tree.setSizeFull();
        updateProjectList();
        updateScreen();
        Page.getCurrent().setTitle("Validation Manager");
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
        if (DataBaseManager.isDemo()
                && controller.findProjectEntities().isEmpty()) {
            buildDemoTree();
        }
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

    private boolean checkAnyRights(List<String> rights) {
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

    private void displayTestPlanning(Project p) {
        DesignerScreenProvider provider = Lookup.getDefault()
                .lookup(DesignerScreenProvider.class
                );
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

    private void displayBaseline(Baseline baseline, boolean edit) {
        displayBaseline(baseline, edit, null);
    }

    private void displayBaseline(Baseline baseline,
            boolean edit, RequirementSpec rs) {
        Panel form = new Panel("Baseline Detail");
        FormLayout layout = new FormLayout();
        form.setContent(layout);
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(baseline.getClass());
        binder.setItemDataSource(baseline);
        Field<?> id = binder.buildAndBind("Name", "baselineName");
        layout.addComponent(id);
        Field desc = binder.buildAndBind("Description", "description",
                TextArea.class
        );
        desc.setSizeFull();
        layout.addComponent(desc);
        Button cancel = new Button("Cancel");
        if (rs != null) {
            List<History> potential = new ArrayList<>();
            for (Requirement r : Tool.extractRequirements(rs)) {
                potential.add(r.getHistoryList().get(r.getHistoryList().size() - 1));
            }
            layout.addComponent(createRequirementHistoryTable("Included Requirements",
                    potential));
        } else {
            layout.addComponent(createRequirementHistoryTable("Included Requirements",
                    baseline.getHistoryList()));
        }
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.discard();
            displayObject(tree.getValue());
        });
        if (edit) {
            if (baseline.getId() == null) {
                //Creating a new one
                Button save = new Button("Save");
                save.addClickListener((Button.ClickEvent event) -> {
                    try {
                        binder.commit();
                        if (rs != null) {
                            MessageBox prompt = MessageBox.createQuestion()
                                    .withCaption("Do you want to create the baseline?")
                                    .withMessage("This is not reversible as "
                                            + "requirements will be released to a new major version")
                                    .withYesButton(() -> {
                                        Baseline entity = BaselineServer
                                                .createBaseline(
                                                        baseline.getBaselineName(),
                                                        baseline.getDescription(),
                                                        rs)
                                                .getEntity();
                                        updateProjectList();
                                        buildProjectTree(entity);
                                        displayObject(entity, false);
                                        updateScreen();
                                    },
                                            ButtonOption.focus(),
                                            ButtonOption
                                                    .icon(VaadinIcons.CHECK))
                                    .withNoButton(() -> {
                                        displayObject(tree.getValue());
                                    },
                                            ButtonOption
                                                    .icon(VaadinIcons.CLOSE));
                            prompt.getWindow().setIcon(ValidationManagerUI.SMALL_APP_ICON);
                            prompt.open();
                        } else {
                            //Recreate the tree to show the addition
                            displayObject(baseline, true);
                        }
                        updateProjectList();
                        updateScreen();
                    } catch (FieldGroup.CommitException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(save);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            } else {
                //Editing existing one
                Button update = new Button("Update");
                update.addClickListener((Button.ClickEvent event) -> {
                    try {
                        handleVerioning(baseline, () -> {
                            try {
                                new BaselineJpaController(DataBaseManager
                                        .getEntityManagerFactory()).edit(baseline);
                                //Recreate the tree to show the addition
                                buildProjectTree(baseline);
                                displayBaseline(baseline, false);
                            } catch (NonexistentEntityException ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show("Error updating record!",
                                        ex.getLocalizedMessage(),
                                        Notification.Type.ERROR_MESSAGE);
                            } catch (Exception ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show("Error updating record!",
                                        ex.getLocalizedMessage(),
                                        Notification.Type.ERROR_MESSAGE);
                            }
                        });
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(update);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            }
        }
        binder.setBuffered(true);
        binder.setReadOnly(!edit);
        binder.bindMemberFields(form);
        form.setSizeFull();
        setTabContent(main, form, REQUIREMENT_REVIEW);
    }

    private Grid getHistoryTable(String title,
            List<History> historyItems, String sortByField,
            boolean showVersionFields,
            String... fields) {
        Grid grid = new Grid(title);
        BeanItemContainer<History> histories
                = new BeanItemContainer<>(History.class
                );
        GeneratedPropertyContainer wrapperCont
                = new GeneratedPropertyContainer(histories);
        histories.addAll(historyItems);
        grid.setContainerDataSource(wrapperCont);
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(wrapperCont.size() > 5 ? 5 : wrapperCont.size());
        for (String field : fields) {
            wrapperCont.addGeneratedProperty(field,
                    new PropertyValueGenerator<String>() {

                @Override
                public String getValue(Item item, Object itemId, Object propertyId) {
                    History v = (History) itemId;
                    String result = "";
                    for (HistoryField hf : v.getHistoryFieldList()) {
                        if (hf.getFieldName().equals(field)) {
                            result = hf.getFieldValue();
                            break;
                        }
                    }
                    return result;
                }

                @Override
                public Class<String> getType() {
                    return String.class;
                }
            });
        }
        if (showVersionFields) {
            wrapperCont.addGeneratedProperty("version",
                    new PropertyValueGenerator<String>() {

                @Override
                public String getValue(Item item, Object itemId, Object propertyId) {
                    History v = (History) itemId;
                    return v.getMajorVersion() + "." + v.getMidVersion()
                            + "." + v.getMinorVersion();
                }

                @Override
                public Class<String> getType() {
                    return String.class;
                }
            });
            wrapperCont.addGeneratedProperty("modifier",
                    new PropertyValueGenerator<String>() {

                @Override
                public String getValue(Item item, Object itemId, Object propertyId) {
                    History v = (History) itemId;
                    return v.getModifierId().getFirstName() + " "
                            + v.getModifierId().getLastName();
                }

                @Override
                public Class<String> getType() {
                    return String.class;
                }
            });
            wrapperCont.addGeneratedProperty("modificationDate",
                    new PropertyValueGenerator<String>() {

                @Override
                public String getValue(Item item, Object itemId, Object propertyId) {
                    History v = (History) itemId;
                    return v.getModificationTime().toString();
                }

                @Override
                public Class<String> getType() {
                    return String.class;
                }
            });
            wrapperCont.addGeneratedProperty("modificationReason",
                    new PropertyValueGenerator<String>() {

                @Override
                public String getValue(Item item, Object itemId, Object propertyId) {
                    History v = (History) itemId;
                    return v.getReason() == null ? "null" : translate(v.getReason());
                }

                @Override
                public Class<String> getType() {
                    return String.class;
                }
            });
        }
        List<String> fieldList = new ArrayList<>();
        //Add specified fields
        fieldList.addAll(Arrays.asList(fields));
        if (showVersionFields) {
            //Add default fields
            fieldList.add("version");
            fieldList.add("modifier");
            fieldList.add("modificationDate");
            fieldList.add("modificationReason");
        }
        grid.setColumns(fieldList.toArray());
        if (showVersionFields) {
            Grid.Column version = grid.getColumn("version");
            version.setHeaderCaption("Version");
            Grid.Column mod = grid.getColumn("modifier");
            mod.setHeaderCaption("Modifier");
            Grid.Column modDate = grid.getColumn("modificationDate");
            modDate.setHeaderCaption("Modification Date");
            Grid.Column modReason = grid.getColumn("modificationReason");
            modReason.setHeaderCaption("Modification Reason");
        }
        if (sortByField != null && !sortByField.trim().isEmpty()) {
            wrapperCont.sort(new Object[]{sortByField}, new boolean[]{true});
        }
        grid.setSizeFull();
        return grid;
    }

    private Component createStepHistoryTable(String title,
            List<History> historyItems, boolean showVersionFields) {
        Grid grid = getHistoryTable(title, historyItems, null,
                showVersionFields,
                "text", "expectedResult", "notes");
        Grid.Column text = grid.getColumn("text");
        text.setHeaderCaption("Step Text");
        Grid.Column result = grid.getColumn("expectedResult");
        result.setHeaderCaption("Expected Result");
        Grid.Column notes = grid.getColumn("notes");
        notes.setHeaderCaption("Notes");
        return grid;
    }

    private Component createRequirementHistoryTable(String title,
            List<History> historyItems) {
        Grid grid = getHistoryTable(title, historyItems, "uniqueId", true,
                "uniqueId", "description", "notes");
        Grid.Column uniqueId = grid.getColumn("uniqueId");
        uniqueId.setHeaderCaption("ID");
        Grid.Column description = grid.getColumn("description");
        description.setHeaderCaption("Description");
        return grid;
    }

    private void addBaseline(Baseline bl, Tree tree) {
        if (!tree.containsId(bl)) {
            tree.addItem(bl);
            tree.setItemCaption(bl, bl.getBaselineName());
            tree.setItemIcon(bl, BASELINE_ICON);
            tree.setParent(bl, bl.getRequirementSpec());
            //No children
            tree.setChildrenAllowed(bl, false);
        }
    }

    private void displayTraceMatrix(Project project) {
        VMWindow tm = new VMWindow("Trace Matrix");
        Panel content = new Panel(new TraceMatrix(project));
        content.setSizeFull();
        tm.setContent(content);
        tm.center();
        tm.setSizeFull();
        addWindow(tm);
    }

//    private void createBaselineMenu(ContextMenu menu) {
//        //TODO:
//    }
    private AbstractSelect getRequirementSelectionComponent() {
        Project p = getParentProject();
        List<Requirement> reqs = Tool.extractRequirements(p);
        Collections.sort(reqs, (Requirement o1, Requirement o2)
                -> o1.getUniqueId().compareTo(o2.getUniqueId()));
        BeanItemContainer<Requirement> requirementContainer
                = new BeanItemContainer<>(Requirement.class,
                        reqs);
        TwinColSelect requirements
                = new TwinColSelect("Linked Requirements");
        requirements.setItemCaptionPropertyId("uniqueId");
        requirements.setContainerDataSource(requirementContainer);
        requirements.setRows(5);
        requirements.setLeftColumnCaption("Available Requirements");
        requirements.setRightColumnCaption("Linked Requirements");
        return requirements;
    }

    private Component getDisplayRequirementList(String title,
            List<Requirement> requirementList) {
        Grid grid = new Grid(title);
        BeanItemContainer<Requirement> children
                = new BeanItemContainer<>(Requirement.class
                );
        children.addAll(requirementList);
        grid.setContainerDataSource(children);
        grid.setColumns("uniqueId");
        Grid.Column uniqueId = grid.getColumn("uniqueId");
        uniqueId.setHeaderCaption("ID");
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(children.size() > 5 ? 5 : children.size());
        grid.setSizeFull();
        children.sort(new Object[]{"uniqueId"}, new boolean[]{true});
        return grid;
    }

    private void addDeleteExecution(ContextMenu menu) {
        ContextMenu.ContextMenuItem create
                = menu.addItem("Delete Execution", DELETE_ICON);
        create.setEnabled(checkRight("testplan.planning"));
        create.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    //Delete only if no execution has been started yet.
                    TCEExtraction tcee = extractTCE(tree.getValue());
                    TestCaseExecution tce = tcee.getTestCaseExecution();
                    if (tce == null) {
                        LOG.info("Invalid");
                        Notification.show("Unable to delete!",
                                "Error extracting information",
                                Notification.Type.ERROR_MESSAGE);
                    } else {
                        TestCase tc = tcee.getTestCase();
                        TestCaseExecutionServer tces
                        = new TestCaseExecutionServer(tce);
                        //Check that it's not being executed yet
                        boolean canDelete = true;
                        for (ExecutionStep es : tces.getExecutionStepList()) {
                            if (tc == null || Objects.equals(es.getStep().getTestCase()
                                    .getId(), tc.getId())) {
                                if (es.getResultId() != null
                                && es.getResultId().getResultName()
                                        .equals("result.pending")) {
                                    Notification.show("Unable to delete!",
                                            "There is a result present in the execution.",
                                            Notification.Type.ERROR_MESSAGE);
                                    //It has a result other than pending.
                                    canDelete = false;
                                }
                                if (!es.getExecutionStepHasAttachmentList()
                                        .isEmpty()) {
                                    //It has a result other than pending.
                                    Notification.show("Unable to delete!",
                                            "There are attachment present in the execution.",
                                            Notification.Type.ERROR_MESSAGE);
                                    canDelete = false;
                                }
                                if (!es.getExecutionStepHasIssueList()
                                        .isEmpty()) {
                                    //It has a result other than pending.
                                    Notification.show("Unable to delete!",
                                            "There are issues present in the execution.",
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
                                    .withCaption("Want to delete even with identified issues?")
                                    .withMessage("There are attachment(s), issue(s) and/or comment(s) present in the execution.")
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
                            Notification.show("Unable to delete!",
                                    "There are issues present in the execution.",
                                    Notification.Type.ERROR_MESSAGE);
                        }
                    }
                });

    }

    public class TCEExtraction {

        private final TestCaseExecutionServer tce;
        private final TestCaseServer tcs;

        public TCEExtraction(TestCaseExecutionServer tce, TestCaseServer tcs) {
            this.tce = tce;
            this.tcs = tcs;
        }

        /**
         * @return the tce
         */
        public TestCaseExecutionServer getTestCaseExecution() {
            return tce;
        }

        /**
         * @return the tcs
         */
        public TestCaseServer getTestCase() {
            return tcs;
        }
    }

    private void addExecutionDashboard(ContextMenu menu) {
        ContextMenu.ContextMenuItem dashboard
                = menu.addItem("View Execution Dashboard",
                        VaadinIcons.DASHBOARD);
        dashboard.setEnabled(checkRight("testplan.planning"));
        dashboard.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    addWindow(new ExecutionDashboard(extractTCE(tree
                            .getValue())));
                });

    }

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false,
            ui = ValidationManagerUI.class,
            widgetset = "net.sourceforge.javydreamercsw.validation.manager.web.AppWidgetSet")
    public static class Servlet extends VaadinServlet {

        public Servlet() {
            //Connect to the database defined in context.xml
            try {
                DataBaseManager.setPersistenceUnitName("VMPUJNDI");
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            if (reset == null && DataBaseManager.isDemo()) {
                LOG.info("Running on demo mode!");
                reset = new VMDemoResetThread();
                reset.start();
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
        }
    }

    private void handleVerioning(Object o, Runnable r) {
        if (o instanceof Versionable) {
            Versionable ao = (Versionable) o;
            if (Versionable.auditable(ao)) {
                //Set user changing to the current user
                ao.setModifierId(getUser().getId());
                //Now check the level of the change
                CHANGE_LEVEL level = CHANGE_LEVEL.MINOR;
                History latest = ao.getHistoryList().get(ao.getHistoryList()
                        .size() - 1);
                if (ao.getMajorVersion() > latest.getMajorVersion()) {
                    level = CHANGE_LEVEL.MAJOR;
                } else if (ao.getMidVersion() > latest.getMidVersion()) {
                    level = CHANGE_LEVEL.MODERATE;
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
        message.setValue("Please document the reasons for the "
                + "change to this item. This will be kep in "
                + "the history of the record along with user "
                + "and date the change is done.");
        message.setReadOnly(true);
        message.setSizeFull();
        TextArea desc = new TextArea("Reason");
        desc.setSizeFull();
        layout.addComponent(message);
        layout.addComponent(desc);
        //Prompt user with reason for change
        MessageBox prompt = MessageBox.createQuestion();
        prompt.withCaption("Please provide a reason for the change.")
                .withMessage(layout)
                .withYesButton(() -> {
                    ao.setReason(desc.getValue());
                    r.run();
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
}
