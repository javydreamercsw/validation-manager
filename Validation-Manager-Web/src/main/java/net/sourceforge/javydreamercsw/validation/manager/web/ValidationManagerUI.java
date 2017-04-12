package net.sourceforge.javydreamercsw.validation.manager.web;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroupFieldFactory;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.label.ContentMode;
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
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.Tree.TreeDropCriterion;
import com.vaadin.ui.Tree.TreeTargetDetails;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.DemoBuilder;
import com.validation.manager.core.db.ExecutionResult;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.ExecutionStepPK;
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
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.db.controller.RequirementJpaController;
import com.validation.manager.core.db.controller.RequirementSpecJpaController;
import com.validation.manager.core.db.controller.RequirementSpecNodeJpaController;
import com.validation.manager.core.db.controller.SpecLevelJpaController;
import com.validation.manager.core.db.controller.StepJpaController;
import com.validation.manager.core.db.controller.TestCaseJpaController;
import com.validation.manager.core.db.controller.TestPlanJpaController;
import com.validation.manager.core.db.controller.TestProjectJpaController;
import com.validation.manager.core.db.controller.VmUserJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.ExecutionResultServer;
import com.validation.manager.core.server.core.ExecutionStepServer;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.TestCaseExecutionServer;
import com.validation.manager.core.server.core.TestCaseServer;
import com.validation.manager.core.server.core.VMSettingServer;
import com.validation.manager.core.server.core.VMUserServer;
import com.validation.manager.core.tool.MD5;
import com.validation.manager.core.tool.requirement.importer.RequirementImportException;
import com.validation.manager.core.tool.requirement.importer.RequirementImporter;
import com.validation.manager.core.tool.step.importer.StepImporter;
import com.validation.manager.core.tool.step.importer.TestCaseImportException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import net.sourceforge.javydreamercsw.validation.manager.web.execution.ExecutionWindow;
import net.sourceforge.javydreamercsw.validation.manager.web.importer.FileUploader;
import net.sourceforge.javydreamercsw.validation.manager.web.wizard.assign.AssignUserStep;
import net.sourceforge.javydreamercsw.validation.manager.web.wizard.plan.DetailStep;
import net.sourceforge.javydreamercsw.validation.manager.web.wizard.plan.SelectTestCasesStep;
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
public class ValidationManagerUI extends UI {

    private static final ThreadLocal<ValidationManagerUI> THREAD_LOCAL
            = new ThreadLocal<>();
    private final ThemeResource logo = new ThemeResource("vm_logo.png");
    private VMUserServer user = null;
    private static final Logger LOG
            = Logger.getLogger(ValidationManagerUI.class.getSimpleName());
    private static VMDemoResetThread reset = null;
    private LoginDialog loginWindow = null;
    private ExecutionWindow executionWindow = null;
    private final String projTreeRoot = "Available Projects";
    private Component left;
    private final TabSheet tabSheet = new TabSheet();
    private final List<Project> projects = new ArrayList<>();
    public static final VaadinIcons PROJECT_ICON = VaadinIcons.RECORDS;
    public static final VaadinIcons SPEC_ICON = VaadinIcons.BOOK;
    public static final VaadinIcons REQUIREMENT_ICON = VaadinIcons.PIN;
    public static final VaadinIcons TEST_SUITE_ICON = VaadinIcons.FILE_TREE;
    public static final VaadinIcons TEST_PLAN_ICON = VaadinIcons.FILE_TREE_SMALL;
    public static final VaadinIcons TEST_ICON = VaadinIcons.FILE_TEXT;
    public static final VaadinIcons STEP_ICON = VaadinIcons.FILE_TREE_SUB;
    public static final VaadinIcons IMPORT_ICON = VaadinIcons.ARROW_CIRCLE_UP_O;
    public static final VaadinIcons PLAN_ICON = VaadinIcons.BULLETS;
    public static final VaadinIcons EDIT_ICON = VaadinIcons.EDIT;
    public static final VaadinIcons EXECUTIONS_ICON = VaadinIcons.COGS;
    public static final VaadinIcons EXECUTION_ICON = VaadinIcons.COG;
    public static final VaadinIcons ASSIGN_ICON = VaadinIcons.USER_CLOCK;
    private Tree tree;
    private Tab main, tester, designer, demo, admin;
    private final List<String> roles = new ArrayList<>();
    public static final ResourceBundle RB = ResourceBundle.getBundle(
            "com.validation.manager.resources.VMMessages");

    /**
     * @return the user
     */
    public VMUserServer getUser() {
        return user;
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
        desc.setStyleName(ValoTheme.TEXTAREA_LARGE);
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
                    try {
                        rsn.setName(name.getValue().toString());
                        rsn.setDescription(desc.getValue().toString());
                        rsn.setScope(scope.getValue().toString());
                        new RequirementSpecNodeJpaController(DataBaseManager
                                .getEntityManagerFactory()).edit(rsn);
                        displayRequirementSpecNode(rsn, true);
                    } catch (FieldGroup.CommitException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
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
        setTabContent(main, form, "requirement.view");
    }

    private void setTabContent(Tab target, Component content,
            String permission) {
        Layout l = (Layout) target.getComponent();
        l.removeAllComponents();
        if (content != null) {
            l.addComponent(content);
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

    private void setTabContent(Tab target, Component content) {
        setTabContent(target, content, null);
    }

    private void displayTestCaseExecution(TestCaseExecution tce, boolean edit) {
        Panel form = new Panel("Test Case Execution Detail");
        FormLayout layout = new FormLayout();
        form.setContent(layout);
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(tce.getClass());
        binder.setItemDataSource(tce);
        Field<?> scope = binder.buildAndBind("Scope", "scope");
        layout.addComponent(scope);
        Field<?> name = binder.buildAndBind("Name", "name");
        layout.addComponent(name);
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
            TestCaseExecutionServer tces = new TestCaseExecutionServer(tce);
            if (tce.getId() == null) {
                //Creating a new one
                Button save = new Button("Save");
                save.addClickListener((Button.ClickEvent event) -> {
                    tces.setConclusion(conclusion.getValue());
                    tces.setScope(scope.getValue().toString());
                    tces.setName(name.getValue().toString());
                    try {
                        tces.write2DB();
                        tces.update(tce, tces.getEntity());
                        displayObject(tce);
                    } catch (Exception ex) {
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
                    tces.setConclusion(conclusion.getValue());
                    tces.setScope(scope.getValue().toString());
                    tces.setName(name.getValue().toString());
                    try {
                        tces.write2DB();
                        tces.update(tce, tces.getEntity());
                        displayObject(tce);
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

    private void displayExecutionStep(ExecutionStep es, boolean edit) {
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
        BeanItemContainer stepContainer = new BeanItemContainer<>(Step.class);
        stepContainer.addBean(es.getStep());
        Grid grid = new Grid(stepContainer);
        grid.setCaption("Step Details");
        grid.setContainerDataSource(stepContainer);
        grid.setColumns("text", "expectedResult", "notes");
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(1);
        Grid.Column textColumn = grid.getColumn("text");
        textColumn.setHeaderCaption("Text");
        textColumn.setConverter(new ByteToStringConverter());
        Grid.Column resultColumn = grid.getColumn("expectedResult");
        resultColumn.setHeaderCaption("Expected Result");
        resultColumn.setConverter(new ByteToStringConverter());
        Grid.Column notesColumn = grid.getColumn("notes");
        notesColumn.setHeaderCaption("Notes");
        grid.setSizeFull();
        layout.addComponent(grid);
        if (es.getResultId() != null) {
            Field<?> result = binder.buildAndBind("Result", "resultId.resultName");
            layout.addComponent(result);
        }
        if (es.getComment() != null) {
            TextArea comment = new TextArea("Comment");
            binder.bind(comment, "comment");
            layout.addComponent(comment);
        }
        if (es.getAssignee() != null) {
            TextField assignee = new TextField("Assignee");
            assignee.setConverter(new UserToStringConverter());
            binder.bind(assignee, "vmUserId");
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
        if (es.getExecutionTime() > 0) {
            Field<?> time = binder.buildAndBind("Execution Time",
                    "executionTime");
            layout.addComponent(time);
        }
        if (es.getStep().getRequirementList() != null
                && !es.getStep().getRequirementList().isEmpty()) {
            BeanItemContainer reqContainer
                    = new BeanItemContainer<>(Requirement.class);
            reqContainer.addAll(es.getStep().getRequirementList());
            Grid reqGrid = new Grid(reqContainer);
            reqGrid.setCaption("Related Requirements");
            reqGrid.setColumns("uniqueId");
            Grid.Column reqColumn = reqGrid.getColumn("uniqueId");
            reqColumn.setHeaderCaption("Requirement ID");
            reqColumn.setRenderer(new ButtonRenderer(e -> {
                //Show the requirement details in a window
                VerticalLayout l = new VerticalLayout();
                Window subWindow = new VMWindow(
                        ((Requirement) e.getItemId()).getUniqueId()
                        + " Details");
                BeanFieldGroup<Requirement> b
                        = new BeanFieldGroup<>(Requirement.class);
                b.setItemDataSource((Requirement) e.getItemId());
                b.setReadOnly(true);
                Field<?> id = b.buildAndBind("Requirement ID", "uniqueId");
                id.setReadOnly(true); //Read only flag set to true !!!
                l.addComponent(id);
                Field<?> desc = b.buildAndBind("Description", "description",
                        TextArea.class);
                desc.setReadOnly(true); //Read only flag set to true !!!
                l.addComponent(desc);
                Field<?> notes = b.buildAndBind("Notes", "notes",
                        TextArea.class);
                notes.setReadOnly(true); //Read only flag set to true !!!
                l.addComponent(notes);
                subWindow.setContent(l);
                subWindow.setModal(true);
                subWindow.center();
                // Open it in the UI
                addWindow(subWindow);
            }));
            layout.addComponent(reqGrid);
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
        if (edit) {
            if (es.getExecutionStepPK() == null) {
                //Creating a new one
                Button save = new Button("Save");
                save.addClickListener((Button.ClickEvent event) -> {

                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(save);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            } else {
                //Editing existing one
                Button update = new Button("Update");
                update.addClickListener((Button.ClickEvent event) -> {

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
        notes.setStyleName(ValoTheme.TEXTAREA_LARGE);
        notes.setSizeFull();
        layout.addComponent(notes);
        tree.select(s);
        Project p = getParentProject();
        List<Requirement> reqs = ProjectServer.getRequirements(p);
        Collections.sort(reqs, (Requirement o1, Requirement o2)
                -> o1.getUniqueId().compareTo(o2.getUniqueId()));
        BeanItemContainer<Requirement> requirementContainer
                = new BeanItemContainer<>(Requirement.class, reqs);
        TwinColSelect requirements
                = new TwinColSelect("Linked Requirements");
        requirements.setItemCaptionPropertyId("uniqueId");
        requirements.setContainerDataSource(requirementContainer);
        requirements.setRows(5);
        requirements.setLeftColumnCaption("Available Requirements");
        requirements.setRightColumnCaption("Linked Requirements");
        if (s.getRequirementList() != null) {
            s.getRequirementList().forEach((r) -> {
                requirements.select(r);
            });
        }
        requirements.setEnabled(edit);
        layout.addComponent(requirements);
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
                        s.getRequirementList().clear();
                        ((Set<Requirement>) requirements.getValue()).forEach((r) -> {
                            s.getRequirementList().add(r);
                        });
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
                        s.getRequirementList().clear();
                        ((Set<Requirement>) requirements.getValue()).forEach((r) -> {
                            s.getRequirementList().add(r);
                        });
                        new StepJpaController(DataBaseManager
                                .getEntityManagerFactory()).edit(s);
                        displayStep(s, true);
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
                        new TestCaseJpaController(DataBaseManager
                                .getEntityManagerFactory()).edit(t);
                        displayTestCase(t, true);
                    } catch (FieldGroup.CommitException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
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
        notes.setStyleName(ValoTheme.TEXTAREA_LARGE);
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
                        new TestPlanJpaController(DataBaseManager
                                .getEntityManagerFactory()).edit(tp);
                        displayTestPlan(tp, true);
                    } catch (FieldGroup.CommitException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
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
        notes.setStyleName(ValoTheme.TEXTAREA_LARGE);
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
                        new TestProjectJpaController(DataBaseManager
                                .getEntityManagerFactory()).edit(tp);
                        displayTestProject(tp, true);
                    } catch (FieldGroup.CommitException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
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
        level.setItemCaptionPropertyId("name");
        level.setContainerDataSource(specLevelContainer);
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
                        new RequirementSpecJpaController(DataBaseManager
                                .getEntityManagerFactory()).edit(rs);
                        displayRequirementSpec(rs, true);
                    } catch (FieldGroup.CommitException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
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
        setTabContent(main, form, "requirement.view");
    }

    private void displayObject(Object item) {
        displayObject(item, false);
    }

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
            displayExecutionStep(es, edit);
        }
    }

    private Component displaySetting(VmSetting s) {
        Panel form = new Panel("Setting Detail");
        FormLayout layout = new FormLayout();
        form.setContent(layout);
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(s.getClass());
        binder.setItemDataSource(s);
        Field<?> id = binder.buildAndBind("Setting", "setting");
        layout.addComponent(id);
        Field bool = binder.buildAndBind("Boolean Value", "boolVal");
        bool.setSizeFull();
        layout.addComponent(bool);
        Field integerVal = binder.buildAndBind("Integer Value", "intVal");
        integerVal.setSizeFull();
        layout.addComponent(integerVal);
        Field longVal = binder.buildAndBind("Long Value", "longVal");
        longVal.setSizeFull();
        layout.addComponent(longVal);
        Field stringVal = binder.buildAndBind("String Value", "stringVal",
                TextArea.class);
        stringVal.setStyleName(ValoTheme.TEXTAREA_LARGE);
        stringVal.setSizeFull();
        layout.addComponent(stringVal);
        Button cancel = new Button("Cancel");
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.discard();
        });
        //Editing existing one
        Button update = new Button("Update");
        update.addClickListener((Button.ClickEvent event) -> {
            try {
                binder.commit();
                displaySetting(s);
            } catch (FieldGroup.CommitException ex) {
                LOG.log(Level.SEVERE, null, ex);
                Notification.show("Error updating record!",
                        ex.getLocalizedMessage(),
                        Notification.Type.ERROR_MESSAGE);
            }
        });
        boolean blocked = !s.getSetting().startsWith("version.");
        if (blocked) {
            HorizontalLayout hl = new HorizontalLayout();
            hl.addComponent(update);
            hl.addComponent(cancel);
            layout.addComponent(hl);
        }
        binder.setBuffered(true);
        binder.setReadOnly(false);
        binder.bindMemberFields(form);
        //The version settigns are not modifiable from the GUI
        binder.setEnabled(blocked);
        //Id is always blocked.
        id.setEnabled(false);
        form.setSizeFull();
        return form;
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
        desc.setStyleName(ValoTheme.TEXTAREA_LARGE);
        desc.setSizeFull();
        layout.addComponent(desc);
        Field notes = binder.buildAndBind("Notes", "notes",
                TextArea.class);
        notes.setStyleName(ValoTheme.TEXTAREA_LARGE);
        notes.setSizeFull();
        layout.addComponent(notes);
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
                    req.setDescription(notes.getValue().toString());
                    req.setRequirementSpecNode((RequirementSpecNode) tree.getValue());
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
                        binder.commit();
                        //Recreate the tree to show the addition
                        buildProjectTree(req);
                        displayRequirement(req, false);
                    } catch (FieldGroup.CommitException ex) {
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
        setTabContent(main, form, "requirement.view");
    }

    // @return the current application instance
    public static ValidationManagerUI getInstance() {
        return THREAD_LOCAL.get();
    }

    // Set the current application instance
    public static void setInstance(ValidationManagerUI application) {
        THREAD_LOCAL.set(application);
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

    public void buildProjectTree() {
        buildProjectTree(null);
    }

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

    private void createTestExecutionMenu(ContextMenu menu) {
        addTestCaseAssignment(menu);
    }

    private void createRootMenu(ContextMenu menu) {
        ContextMenu.ContextMenuItem create
                = menu.addItem("Create Execution", PROJECT_ICON);
        create.setEnabled(checkRight("product.modify"));
        create.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    displayProject(new Project(), true);
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
                    displayTestCaseExecution((TestCaseExecution) tree.getValue(),
                            true);
                });
        addTestCaseAssignment(menu);
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
    }

    private void createRequirementSpecNodeMenu(ContextMenu menu) {
        ContextMenu.ContextMenuItem create
                = menu.addItem("Create Requirement Spec", VaadinIcons.PLUS);
        create.setEnabled(checkRight("requirement.modify"));
        ContextMenu.ContextMenuItem edit
                = menu.addItem("Edit Requirement Spec Node", EDIT_ICON);
        edit.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    displayRequirementSpecNode((RequirementSpecNode) tree.getValue(),
                            true);
                });
        edit.setEnabled(checkRight("requirement.modify"));
        create.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    Requirement r = new Requirement();
                    r.setRequirementSpecNode((RequirementSpecNode) tree.getValue());
                    displayRequirement(r, true);
                });
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
                                    = new RequirementImporter(receiver.getFile(),
                                            (RequirementSpecNode) tree.getValue());

                            importer.importFile(cb.getValue());
                            importer.processImport();
                            buildProjectTree(tree.getValue());
                            updateScreen();
                        } catch (RequirementImportException ex) {
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
                            buildProjectTree(new TestCaseServer(tc.getId()).getEntity());
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

    public static String translate(String mess) {
        return RB.containsKey(mess) ? RB.getString(mess) : mess;
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
            if (tester == null) {
                VerticalLayout vl = new VerticalLayout();
                if (getUser() != null
                        && !getUser().getExecutionStepList().isEmpty()) {
                    TreeTable testCaseTree = new TreeTable("Available Tests");
                    testCaseTree.addContainerProperty("Name", String.class, "");
                    testCaseTree.addGeneratedColumn("Status",
                            (Table source, Object itemId, Object columnId) -> {
                                if (columnId.equals("Status")
                                && itemId instanceof String
                                && ((String) itemId).startsWith("es")) {
                                    Button label = new Button();
                                    label.addStyleName(ValoTheme.BUTTON_BORDERLESS + " labelButton");
                                    ExecutionStepServer ess = new ExecutionStepServer(extractExecutionStepPK((String) itemId));
                                    String message;
                                    if (ess.getResultId() == null) {
                                        ExecutionResult result = ExecutionResultServer.getResult("result.pending");
                                        message = result.getResultName();
                                    } else {
                                        message = ess.getResultId().getResultName();
                                    }
                                    label.setCaption(RB.containsKey(message) ? RB.getString(message) : message);
                                    if (ess.getExecutionStart() != null
                                    && ess.getExecutionEnd() == null) {
                                        //In progress
                                        label.setIcon(VaadinIcons.AUTOMATION);
                                    } else if (ess.getExecutionStart() == null
                                    && ess.getExecutionEnd() == null) {
                                        //Not started
                                        label.setIcon(VaadinIcons.CLOCK);
                                    } else if (ess.getExecutionStart() != null
                                    && ess.getExecutionEnd() != null) {
                                        //Completed. Now check result
                                        switch (ess.getResultId().getId()) {
                                            case 1:
                                                label.setIcon(VaadinIcons.CHECK);
                                                break;
                                            case 2:
                                                label.setIcon(VaadinIcons.CLOSE);
                                                break;
                                            case 3:
                                                label.setIcon(VaadinIcons.PAUSE);
                                                break;
                                            default:
                                                label.setIcon(VaadinIcons.CLOCK);
                                                break;
                                        }
                                    }
                                    return label;
                                }
                                return new Label();
                            });
                    testCaseTree.addContainerProperty("Summary", String.class, "");
                    testCaseTree.addContainerProperty("Assignment Date",
                            String.class, "");
                    testCaseTree.setVisibleColumns(new Object[]{"Name",
                        "Status", "Summary", "Assignment Date"});
                    testCaseTree.addActionHandler(new Action.Handler() {
                        @Override
                        public Action[] getActions(Object target, Object sender) {
                            List<Action> actions = new ArrayList<>();
                            if (target instanceof String
                                    && ((String) target).startsWith("es")) {
                                actions.add(new Action("Execute"));
                            }
                            return actions.toArray(new Action[actions.size()]);
                        }

                        @Override
                        public void handleAction(Action action, Object sender, Object target) {
                            //Parse the information to get the exact Execution Step
                            List<TestCaseExecutionServer> executions = new ArrayList<>();
                            executions.add(new TestCaseExecutionServer(new ExecutionStepServer(extractExecutionStepPK((String) target)).getTestCaseExecution().getId()));
                            showExecutionScreen(executions);
                        }
                    });
                    ProjectServer.getProjects().forEach(p -> {
                        if (p.getParentProjectId() == null) {
                            testCaseTree.addItem(new Object[]{p.getName(),
                                "", "",}, "p" + p.getId());
                            testCaseTree.setItemIcon("p" + p.getId(), PROJECT_ICON);
                            p.getProjectList().forEach(sp -> {
                                //Add subprojects
                                testCaseTree.addItem(new Object[]{sp.getName(),
                                    "", "",}, "p" + sp.getId());
                                testCaseTree.setParent("p" + sp.getId(), "p" + p.getId());
                                testCaseTree.setItemIcon("p" + sp.getId(), PROJECT_ICON);
                                //Add applicable Executions
                                Map<Integer, ExecutionStep> tests = new HashMap<>();
                                sp.getTestProjectList().forEach(test -> {
                                    test.getTestPlanList().forEach(tp -> {
                                        tp.getTestCaseList().forEach(testCase -> {
                                            List<Integer> tcids = new ArrayList<>();
                                            testCase.getStepList().forEach(s -> {
                                                s.getExecutionStepList().forEach(es -> {
                                                    TestCaseExecution tce = es.getTestCaseExecution();
                                                    testCaseTree.addItem(new Object[]{tce.getName(),
                                                        "", "",}, "tce" + tce.getId());
                                                    testCaseTree.setParent("tce" + tce.getId(), "p" + sp.getId());
                                                    testCaseTree.setItemIcon("tce" + tce.getId(), EXECUTION_ICON);
                                                    if (es.getAssignee().getId().equals(getUser().getId())) {
                                                        TestCase tc = es.getStep().getTestCase();
                                                        if (!tcids.contains(tc.getId())) {
                                                            tcids.add(tc.getId());
                                                            DateTimeFormatter format
                                                                    = DateTimeFormatter.ofPattern("MMM d yyyy  hh:mm a");
                                                            LocalDateTime time
                                                                    = LocalDateTime.ofInstant(es.getAssignedTime()
                                                                            .toInstant(), ZoneId.systemDefault());
                                                            String key = "es" + es.getExecutionStepPK().getTestCaseExecutionId()
                                                                    + "-" + es.getStep().getStepPK().getId() + "-" + tc.getId();
                                                            testCaseTree.addItem(new Object[]{tc.getName(),
                                                                tc.getSummary(), format.format(time),},
                                                                    key);
                                                            testCaseTree.setParent(key, "tce" + tce.getId());
                                                            testCaseTree.setItemIcon(key, TEST_ICON);
                                                            testCaseTree.setChildrenAllowed(key, false);
                                                        }
                                                    }
                                                });
                                            });
                                            tcids.clear();
                                        });
                                    });
                                });
                                testCaseTree.setSizeFull();
                                vl.addComponent(testCaseTree);
                                tester = tabSheet.addTab(vl, "Tester");
                            });
                        }
                    });
                }
            }
            if (designer == null) {
                designer = tabSheet.addTab(new VerticalLayout(), "Test Designer");
            }
            if (admin == null) {
                TabSheet adminSheet = new TabSheet();
                VerticalLayout layout = new VerticalLayout();
                //Build setting tab
                VerticalLayout sl = new VerticalLayout();
                HorizontalSplitPanel split = new HorizontalSplitPanel();
                sl.addComponent(split);
                //Build left side
                Tree sTree = new Tree("Settings");
                adminSheet.addTab(sl, "Settings");
                VMSettingServer.getSettings().stream().map((s) -> {
                    sTree.addItem(s);
                    sTree.setChildrenAllowed(s, false);
                    return s;
                }).forEachOrdered((s) -> {
                    sTree.setItemCaption(s, translate(s.getSetting()));
                });
                split.setFirstComponent(sTree);
                layout.addComponent(adminSheet);
                admin = tabSheet.addTab(layout, "Admin");
                sTree.addValueChangeListener((Property.ValueChangeEvent event) -> {
                    if (sTree.getValue() instanceof VmSetting) {
                        split.setSecondComponent(
                                displaySetting((VmSetting) sTree.getValue()));
                    }
                });
            }
            if (demo == null && DataBaseManager.isDemo()) {
                VerticalLayout layout = new VerticalLayout();
                VmUserJpaController controller
                        = new VmUserJpaController(DataBaseManager
                                .getEntityManagerFactory());
                layout.addComponent(new Label("<h1>Welcome to "
                        + "Validation Manager Demo instance</h1>",
                        ContentMode.HTML));
                layout.addComponent(new Label("Below you can find the "
                        + "various accounts that exist in the demo so "
                        + "you can explore."));
                StringBuilder sb = new StringBuilder("<ul>");
                controller.findVmUserEntities().stream().filter((u)
                        -> (u.getId() < 1000)).forEachOrdered((u) -> {
                    try {
                        //Default accounts
                        if (u.getPassword() != null
                                && u.getPassword().equals(MD5
                                        .encrypt(u.getUsername()))) {
                            sb.append("<li><b>User name:</b> ")
                                    .append(u.getUsername())
                                    .append(", <b>Password:</b> ")
                                    .append(u.getUsername())
                                    .append(" <b>Role</b>: ")
                                    .append(u.getRoleList().get(0)
                                            .getDescription())
                                    .append("</li>");
                        }
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                });
                sb.append("</ul>");
                layout.addComponent(new Label(sb.toString(),
                        ContentMode.HTML));
                demo = tabSheet.addTab(layout, "Demo");
            }
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
        if (tester != null) {
            tester.setVisible(((Layout) tester.getComponent())
                    .getComponentCount() > 0
                    && checkRight("testplan.execute"));
        }
        if (designer != null) {
            designer.setVisible(((Layout) designer.getComponent())
                    .getComponentCount() > 0
                    && checkRight("testplan.planning"));
        }
        if (admin != null) {
            admin.setVisible(checkRight("system.configuration"));
        }
        tabSheet.setTabPosition(demo, tabSheet.getComponentCount() - 1);
        hsplit.setSplitPosition(25, Unit.PERCENTAGE);
        return hsplit;
    }

    private Component getMenu() {
        GridLayout gl = new GridLayout(3, 3);
        gl.addComponent(new Image("", logo), 0, 0);
        if (getUser() != null) {
            //Logout button
            Button logout = new Button("Log out");
            logout.addClickListener((Button.ClickEvent event) -> {
                try {
                    user.write2DB();
                    user = null;
                    main = null;
                    tester = null;
                    designer = null;
                    demo = null;
                    admin = null;
                    updateScreen();
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            });
            gl.addComponent(logout, 2, 2);
        }
        gl.setSizeFull();
        return gl;
    }

    public void updateScreen() {
        //Set up a menu header on top and the content below
        VerticalSplitPanel vs = new VerticalSplitPanel();
        vs.setSplitPosition(25, Unit.PERCENTAGE);
        //Set up top menu panel
        vs.setFirstComponent(getMenu());
        setContent(vs);
        if (getUser() == null) {
            showLoginDialog();
        } else {
            //Process any notifications
            //Check for assigned test
            getUser().update();
            for (ExecutionStep es : getUser().getExecutionStepList()) {
                if (es.getExecutionStart() == null) {
                    //It has been assigned but not started
                    Notification.show("Test Pending",
                            "You have test case(s) pending execution.",
                            Notification.Type.TRAY_NOTIFICATION);
                    break;
                }
            }
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
        notes.setStyleName(ValoTheme.TEXTAREA_LARGE);
        notes.setSizeFull();
        layout.addComponent(notes);
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
                    p.setName(name.getValue().toString());
                    p.setNotes(notes.getValue().toString());
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
                    try {
                        binder.commit();
                    } catch (FieldGroup.CommitException ex) {
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
        setTabContent(main, form, "project.viewer");
    }

    private void addRequirementSpec(RequirementSpec rs, Tree tree) {
        // Add the item as a regular item.
        tree.addItem(rs);
        tree.setItemCaption(rs, rs.getName());
        tree.setItemIcon(rs, SPEC_ICON);
        // Set it to be a child.
        tree.setParent(rs, rs.getProject());
        if (rs.getRequirementSpecNodeList().isEmpty()) {
            //No children
            tree.setChildrenAllowed(rs, false);
        } else {
            rs.getRequirementSpecNodeList().forEach((rsn) -> {
                addRequirementSpecsNode(rsn, tree);
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
        List<TestCaseExecution> executions = TestCaseExecutionServer.getExecutions(p);
        if (!executions.isEmpty()) {
            String id = "executions" + p.getId();
            tree.addItem(id);
            tree.setItemCaption(id, "Executions");
            tree.setItemIcon(id, EXECUTIONS_ICON);
            tree.setParent(id, p);
            executions.forEach((tce) -> {
                addTestCaseExecutions(id, tce, tree);
            });
            children = true;
        }
        if (!children) {
            // No subprojects
            tree.setChildrenAllowed(p, false);
        }
    }

    @Override
    protected void init(VaadinRequest request) {
        //Connect to the database defined in context.xml
        try {
            DataBaseManager.setPersistenceUnitName("VMPUJNDI");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        setInstance(this);
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
                                -> (itemId instanceof Step)).forEachOrdered((itemId) -> {
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
                            //for Steps we need to update the sequence number
                            if (sourceItemId instanceof Step && targetItemId instanceof Step) {
                                Step targetItem = (Step) targetItemId;
                                Step sourceItem = (Step) sourceItemId;
                                StepJpaController stepController
                                        = new StepJpaController(DataBaseManager.getEntityManagerFactory());
//                                TestCaseJpaController tcController
//                                        = new TestCaseJpaController(DataBaseManager.getEntityManagerFactory());
                                boolean valid = false;
                                if (targetItem.getTestCase().equals(sourceItem.getTestCase())) {
                                    //Same Test Case, just re-arrange
                                    LOG.info("Same Test Case!");
                                    SortedMap<Integer, Step> map = new TreeMap<>();
                                    targetItem.getTestCase().getStepList().forEach((s) -> {
                                        map.put(s.getStepSequence(), s);
                                    });
                                    //Now swap the two that switched
                                    swapValues(map, sourceItem.getStepSequence(),
                                            targetItem.getStepSequence());
                                    //Now update the sequence numbers
                                    int count = 0;
                                    for (Entry<Integer, Step> entry : map.entrySet()) {
                                        entry.getValue().setStepSequence(++count);
                                        try {
                                            stepController.edit(entry.getValue());
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
                                if (valid) {
                                    // Drop at the top of a subtree -> make it previous
                                    Object parentId = container.getParent(targetItemId);
                                    container.setParent(sourceItemId, parentId);
                                    container.moveAfterSibling(sourceItemId, targetItemId);
                                    container.moveAfterSibling(targetItemId, sourceItemId);
                                    buildProjectTree(targetItem);
                                    updateScreen();
                                }
                            }
                            break;
                        }
                        case BOTTOM: {
                            // Drop below another item -> make it next
                            Object parentId = container.getParent(targetItemId);
                            container.setParent(sourceItemId, parentId);
                            container.moveAfterSibling(sourceItemId, targetItemId);
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
                        } else {
                            //We are at the root
                            createRootMenu(contextMenu);
                        }
                    } else if (tree.getValue() instanceof TestPlan) {
                        createTestPlanMenu(contextMenu);
                    } else if (tree.getValue() instanceof TestCaseExecution) {
                        createTestCaseExecutionPlanMenu(contextMenu);
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

    private boolean checkAllRights(List<String> rights) {
        boolean result = true;
        for (String r : rights) {
            if (!checkRight(r)) {
                result = false;
                break;
            }
        }
        return result;
    }

    private boolean checkRight(String right) {
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
        Wizard w = new Wizard();
        w.addStep(new SelectTestCasesStep(w, p));
        w.addStep(new DetailStep(ValidationManagerUI.this, p));
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
                setTabContent(designer, null, "testplan.planning");
            }

            @Override
            public void wizardCancelled(WizardCancelledEvent event) {
                setTabContent(designer, null, "testplan.planning");
            }
        });
        setTabContent(designer, w, "testplan.planning");
    }

    public Object getSelectdValue() {
        return tree.getValue();
    }

    private ExecutionStepPK extractExecutionStepPK(String itemId) {
        String id = itemId.substring(2);//Remove es
        int esId, sId, tcId;
        StringTokenizer st = new StringTokenizer(id, "-");
        esId = Integer.parseInt(st.nextToken());
        sId = Integer.parseInt(st.nextToken());
        tcId = Integer.parseInt(st.nextToken());
        return new ExecutionStepPK(esId, sId, tcId);
    }

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false,
            ui = ValidationManagerUI.class,
            widgetset = "net.sourceforge.javydreamercsw.validation.manager.web.AppWidgetSet")
    public static class Servlet extends VaadinServlet {

        public Servlet() {
            if (reset == null && DataBaseManager.isDemo()) {
                LOG.info("Running on demo mode!");
                reset = new VMDemoResetThread();
                reset.start();
            }
        }
    }

    private void showExecutionScreen(List<TestCaseExecutionServer> executions) {
        if (executionWindow == null) {
            executionWindow = new ExecutionWindow(this, executions);
            executionWindow.setCaption("Test Execution");
            executionWindow.setVisible(true);
            executionWindow.setClosable(false);
            executionWindow.setResizable(false);
            executionWindow.center();
            executionWindow.setModal(true);
            executionWindow.setSizeFull();
        }
        if (!getWindows().contains(executionWindow)) {
            addWindow(executionWindow);
        }
    }
}
