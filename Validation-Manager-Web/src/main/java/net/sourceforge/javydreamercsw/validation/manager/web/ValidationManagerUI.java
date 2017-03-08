package net.sourceforge.javydreamercsw.validation.manager.web;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.converter.Converter;
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
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
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
import com.vaadin.ui.TextArea;
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
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.RequirementSpecPK;
import com.validation.manager.core.db.SpecLevel;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.db.controller.RequirementSpecJpaController;
import com.validation.manager.core.db.controller.RequirementSpecNodeJpaController;
import com.validation.manager.core.db.controller.SpecLevelJpaController;
import com.validation.manager.core.db.controller.StepJpaController;
import com.validation.manager.core.db.controller.TestCaseJpaController;
import com.validation.manager.core.db.controller.TestPlanJpaController;
import com.validation.manager.core.db.controller.TestProjectJpaController;
import com.validation.manager.core.db.controller.VmUserJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.TestCaseServer;
import com.validation.manager.core.server.core.VMUserServer;
import com.validation.manager.core.tool.MD5;
import com.validation.manager.core.tool.requirement.importer.RequirementImportException;
import com.validation.manager.core.tool.requirement.importer.RequirementImporter;
import com.validation.manager.core.tool.step.importer.StepImporter;
import com.validation.manager.core.tool.step.importer.TestCaseImportException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import net.sourceforge.javydreamercsw.validation.manager.web.importer.FileUploader;
import org.openide.util.Exceptions;
import org.vaadin.peter.contextmenu.ContextMenu;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedListener;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedOnTreeItemEvent;

@Theme("vmtheme")
@SuppressWarnings("serial")
public class ValidationManagerUI extends UI {

    private static final ThreadLocal<ValidationManagerUI> THREAD_LOCAL
            = new ThreadLocal<>();
    private final ThemeResource logo = new ThemeResource("vm_logo.png"),
            small = new ThemeResource("VMSmall.png");
    private VMUserServer user = null;
    private static final Logger LOG
            = Logger.getLogger(ValidationManagerUI.class.getSimpleName());
    private static VMDemoResetThread reset = null;
    private LoginDialog subwindow = null;
    private final String projTreeRoot = "Available Projects";
    private Component left;
    private final TabSheet right = new TabSheet();
    private final List<Project> projects = new ArrayList<>();
    private final VaadinIcons projectIcon = VaadinIcons.RECORDS;
    private final VaadinIcons specIcon = VaadinIcons.BOOK;
    private final VaadinIcons requirementIcon = VaadinIcons.PIN;
    private final VaadinIcons testSuiteIcon = VaadinIcons.FILE_TREE;
    private final VaadinIcons testPlanIcon = VaadinIcons.FILE_TREE_SMALL;
    private final VaadinIcons testIcon = VaadinIcons.FILE_TEXT;
    private final VaadinIcons stepIcon = VaadinIcons.FILE_TREE_SUB;
    private final VaadinIcons importIcon = VaadinIcons.ARROW_CIRCLE_UP_O;
    private Tree tree;
    private Tab admin, tester, designer, demo;
    private final List<String> roles = new ArrayList<>();

    /**
     * @return the user
     */
    protected VMUserServer getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    protected void setUser(VMUserServer user) {
        this.user = user;
        updateScreen();
    }

    private void displayRequirementSpecNode(RequirementSpecNode rsn) {
        displayRequirementSpecNode(rsn, false);
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
                        displayRequirementSpecNode(rsn, true);
                        updateScreen();
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                        Notification.show("Error creating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                layout.addComponent(save);
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
                        Exceptions.printStackTrace(ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    } catch (NonexistentEntityException ex) {
                        Exceptions.printStackTrace(ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                layout.addComponent(update);
            }
        }
        binder.setReadOnly(!edit);
        binder.bindMemberFields(form);
        layout.setSizeFull();
        form.setSizeFull();
        setTabContent(admin, form);
        updateScreen();
    }

    private void setTabContent(Tab target, Component content) {
        Layout l = (Layout) right.getTab(right.getTabPosition(target))
                .getComponent();
        l.removeAllComponents();
        l.addComponent(content);
    }

    private void displayStep(Step s) {
        displayStep(s, false);
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
        Field<?> notes = binder.buildAndBind("Notes", "notes");
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
        if (edit) {
            if (s.getStepPK() == null) {
                //Creating a new one
                Button save = new Button("Save");
                save.addClickListener((Button.ClickEvent event) -> {
                    try {
                        s.setExpectedResult(result.getValue()
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
                        displayStep(s);
                        buildProjectTree(s);
                        updateScreen();
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                        Notification.show("Error creating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                layout.addComponent(save);
            } else {
                //Editing existing one
                Button update = new Button("Update");
                update.addClickListener((Button.ClickEvent event) -> {
                    try {
                        s.setExpectedResult(result.getValue()
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
                        Exceptions.printStackTrace(ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                layout.addComponent(update);
            }
        }
        binder.setReadOnly(!edit);
        binder.bindMemberFields(form);
        layout.setSizeFull();
        form.setSizeFull();
        setTabContent(admin, form);
        updateScreen();
    }

    private void displayTestCase(TestCase t) {
        displayTestCase(t, false);
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
                        new TestCaseJpaController(DataBaseManager
                                .getEntityManagerFactory()).create(t);
                        form.setVisible(false);
                        //Recreate the tree to show the addition
                        updateProjectList();
                        buildProjectTree(t);
                        displayTestCase(t, false);
                        updateScreen();
                    } catch (UnsupportedEncodingException ex) {
                        Exceptions.printStackTrace(ex);
                        Notification.show("Error creating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                layout.addComponent(save);
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
                        Exceptions.printStackTrace(ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    } catch (NonexistentEntityException ex) {
                        Exceptions.printStackTrace(ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                layout.addComponent(update);
            }
        }
        binder.setReadOnly(!edit);
        creation.setEnabled(false);
        binder.bindMemberFields(form);
        layout.setSizeFull();
        form.setSizeFull();
        setTabContent(admin, form);
        updateScreen();
    }

    private void displayTestPlan(TestPlan tp) {
        displayTestPlan(tp, false);
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
        Field<?> notes = binder.buildAndBind("Notes", "notes");
        layout.addComponent(notes);
        Field<?> active = binder.buildAndBind("Active", "active");
        layout.addComponent(active);
        Field<?> open = binder.buildAndBind("Open", "isOpen");
        layout.addComponent(open);
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
                        Exceptions.printStackTrace(ex);
                        Notification.show("Error creating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                layout.addComponent(save);
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
                        Exceptions.printStackTrace(ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    } catch (NonexistentEntityException ex) {
                        Exceptions.printStackTrace(ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                layout.addComponent(update);
            }
        }
        binder.setReadOnly(!edit);
        binder.bindMemberFields(form);
        layout.setSizeFull();
        form.setSizeFull();
        setTabContent(admin, form);
        updateScreen();
    }

    private void displayTestProject(TestProject tp) {
        displayTestProject(tp, false);
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
        Field<?> notes = binder.buildAndBind("Notes", "notes");
        layout.addComponent(notes);
        Field<?> active = binder.buildAndBind("Active", "active");
        layout.addComponent(active);
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
                        Exceptions.printStackTrace(ex);
                        Notification.show("Error creating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                layout.addComponent(save);
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
                        Exceptions.printStackTrace(ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    } catch (NonexistentEntityException ex) {
                        Exceptions.printStackTrace(ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                layout.addComponent(update);
            }
        }
        binder.setReadOnly(!edit);
        binder.bindMemberFields(form);
        layout.setSizeFull();
        form.setSizeFull();
        setTabContent(admin, form);
        updateScreen();
    }

    private void displayRequirementSpec(RequirementSpec rs) {
        displayRequirementSpec(rs, false);
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
        Field<?> version = binder.buildAndBind("Version", "version");
        layout.addComponent(version);
        version.setEnabled(false);
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
                        rs.setVersion(1);
                        rs.setRequirementSpecPK(new RequirementSpecPK(
                                rs.getProject().getId(),
                                rs.getSpecLevel().getId()));
                        new RequirementSpecJpaController(DataBaseManager
                                .getEntityManagerFactory()).create(rs);
                        form.setVisible(false);
                        //Recreate the tree to show the addition
                        updateProjectList();
                        buildProjectTree(rs);
                        displayRequirementSpec(rs, false);
                        updateScreen();
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                        Notification.show("Error creating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                layout.addComponent(save);
            } else {
                //Editing existing one
                Button update = new Button("Update");
                update.addClickListener((Button.ClickEvent event) -> {
                    try {
                        rs.setName(name.getValue().toString());
                        rs.setModificationDate(new Date());
                        rs.setSpecLevel((SpecLevel) level.getValue());
                        rs.setVersion(rs.getVersion() + 1);
                        new RequirementSpecJpaController(DataBaseManager
                                .getEntityManagerFactory()).edit(rs);
                        displayRequirementSpec(rs, true);
                    } catch (FieldGroup.CommitException ex) {
                        Exceptions.printStackTrace(ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    } catch (NonexistentEntityException ex) {
                        Exceptions.printStackTrace(ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                layout.addComponent(update);
            }
        }
        binder.setReadOnly(!edit);
        binder.bindMemberFields(form);
        layout.setSizeFull();
        form.setSizeFull();
        setTabContent(admin, form);
        updateScreen();
    }

    private void displayRequirement(Requirement req) {
        displayRequirement(req, false);
    }

    private void displayRequirement(Requirement req, boolean edit) {
        Panel form = new Panel("Requirement Detail");
        FormLayout layout = new FormLayout();
        form.setContent(layout);
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(req.getClass());
        binder.setItemDataSource(req);
        layout.addComponent(binder.buildAndBind("Requirement ID", "uniqueId"));
        Field desc = binder.buildAndBind("Description", "description",
                TextArea.class);
        desc.setStyleName(ValoTheme.TEXTAREA_LARGE);
        desc.setSizeFull();
        layout.addComponent(desc);
        layout.addComponent(binder.buildAndBind("Notes", "notes"));
        binder.setReadOnly(!edit);
        binder.bindMemberFields(form);
        layout.setSizeFull();
        form.setSizeFull();
        setTabContent(admin, form);
        updateScreen();
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
            Exceptions.printStackTrace(ex);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void buildProjectTree() {
        buildProjectTree(null);
    }

    private void buildProjectTree(Object item) {
        tree.removeAllItems();
        tree.addItem(projTreeRoot);
        projects.forEach((p) -> {
            if (p.getParentProjectId() == null) {
                //TODO: Check if you have permissions on project to see it.
                addProject(p, tree);
            }
        });
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

    private void createRootMenu(ContextMenu menu) {
        ContextMenu.ContextMenuItem create
                = menu.addItem("Create Project", projectIcon);
        create.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    displayProject(new Project(), true);
                });
    }

    private void createTestPlanMenu(ContextMenu menu) {
        ContextMenu.ContextMenuItem create
                = menu.addItem("Create Test Case", specIcon);
        ContextMenu.ContextMenuItem edit
                = menu.addItem("Edit Test Plan", specIcon);
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
                = menu.addItem("Create Sub Project", projectIcon);
        create.setEnabled(isAdmin());
        create.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    Project project = new Project();
                    project.setParentProjectId((Project) tree.getValue());
                    displayProject(project, true);
                });
        ContextMenu.ContextMenuItem createSpec
                = menu.addItem("Create Requirement Spec", specIcon);
        createSpec.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    RequirementSpec rs = new RequirementSpec();
                    rs.setProject((Project) tree.getValue());
                    displayRequirementSpec(rs, true);
                });
        ContextMenu.ContextMenuItem createTest
                = menu.addItem("Create Test Suite", testSuiteIcon);
        ContextMenu.ContextMenuItem edit
                = menu.addItem("Edit Project", VaadinIcons.EDIT);
        edit.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    displayProject((Project) tree.getValue(), true);
                });
        createTest.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    TestProject tp = new TestProject();
                    tp.setProjectList(new ArrayList<>());
                    tp.getProjectList().add((Project) tree.getValue());
                    displayTestProject(tp, true);
                });
    }

    private void createRequirementMenu(ContextMenu menu) {
        ContextMenu.ContextMenuItem edit
                = menu.addItem("Edit Requirement", VaadinIcons.EDIT);
    }

    private void createRequirementSpecMenu(ContextMenu menu) {
        ContextMenu.ContextMenuItem create
                = menu.addItem("Create Requirement Spec Node", specIcon);
        ContextMenu.ContextMenuItem edit
                = menu.addItem("Edit Requirement Spec", specIcon);
        edit.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    displayRequirementSpec((RequirementSpec) tree.getValue(),
                            true);
                });
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
        ContextMenu.ContextMenuItem edit
                = menu.addItem("Edit Requirement Spec Node", VaadinIcons.EDIT);
        edit.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    displayRequirementSpecNode((RequirementSpecNode) tree.getValue(),
                            true);
                });
        create.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    Requirement r = new Requirement();
                    r.setRequirementSpecNode((RequirementSpecNode) tree.getValue());
                    displayRequirement(r, true);
                });
        ContextMenu.ContextMenuItem importRequirement
                = menu.addItem("Import Requirements", importIcon);
        importRequirement.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    // Create a sub-window and set the content
                    Window subWindow = new Window("Import Requirements");
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
        ContextMenu.ContextMenuItem edit
                = menu.addItem("Edit Test Case", VaadinIcons.EDIT);
        edit.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    displayTestCase((TestCase) tree.getValue(), true);
                });
        create.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    TestCase tc = (TestCase) tree.getValue();
                    Step s = new Step();
                    s.setStepSequence(tc.getStepList().size() + 1);
                    s.setTestCase(tc);
                    displayStep(s, true);
                });
        ContextMenu.ContextMenuItem importRequirement
                = menu.addItem("Import Steps", importIcon);
        importRequirement.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    // Create a sub-window and set the content
                    Window subWindow = new Window("Import Test Case Steps");
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
                                    Exceptions.printStackTrace(ex);
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
                = menu.addItem("Edit Step", VaadinIcons.EDIT);
        edit.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    displayStep((Step) tree.getValue(), true);
                });
    }

    private void createTestProjectMenu(ContextMenu menu) {
        ContextMenu.ContextMenuItem create
                = menu.addItem("Create Test Plan", VaadinIcons.PLUS);
        ContextMenu.ContextMenuItem edit
                = menu.addItem("Edit Test Project", VaadinIcons.EDIT);
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
        if (right != null) {
            //This is a tabbed pane. Enable/Disable the panes based on role
            TabSheet tab = (TabSheet) right;
            if (getUser() != null) {
                roles.clear();
                user.update();//Get any recent changes
                user.getRoleList().forEach((r) -> {
                    roles.add(r.getRoleName());
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
                        if (u.getPassword().equals(MD5
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
                        Exceptions.printStackTrace(ex);
                    }
                });
                sb.append("</ul>");
                layout.addComponent(new Label(sb.toString(),
                        ContentMode.HTML));
                demo = tab.addTab(layout, "Demo");
            }
            if (admin == null) {
                admin = tab.addTab(new VerticalLayout(), "Admin");
            }
            if (tester == null) {
                tester = tab.addTab(new VerticalLayout(), "Tester");
            }
            if (designer == null) {
                designer = tab.addTab(new VerticalLayout(), "Test Designer");
            }
            admin.setVisible(isAdmin());
            tester.setVisible(isAdmin() || isTester());
            designer.setVisible(isAdmin() || isDesigner());
            hsplit.setSecondComponent(right);
        }
        hsplit.setSplitPosition(25, Unit.PERCENTAGE);
        return hsplit;
    }

    private Component getMenu() {
        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(new Image("", logo));
        return hl;
    }

    private void updateScreen() {
        //Set up a menu header on top and the content below
        VerticalSplitPanel vs = new VerticalSplitPanel();
        vs.setSplitPosition(20, Unit.PERCENTAGE);
        //Set up top menu panel
        vs.setFirstComponent(getMenu());
        //Add the content
        vs.setSecondComponent(getContentComponent());
        setContent(vs);
        if (getUser() == null) {
            showLoginDialog();
        }
    }

    private void displayProject(Project p) {
        displayProject(p, false);
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
        Field<?> notes = binder.buildAndBind("Notes", "notes");
        layout.addComponent(name);
        layout.addComponent(notes);
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
                layout.addComponent(save);
            } else {
                //Editing existing one
                Button update = new Button("Update");
                update.addClickListener((Button.ClickEvent event) -> {
                    try {
                        binder.commit();
                    } catch (FieldGroup.CommitException ex) {
                        Exceptions.printStackTrace(ex);
                        Notification.show("Error updating record!",
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                layout.addComponent(update);
            }
        }
        binder.setBuffered(true);
        binder.setReadOnly(!edit);
        binder.bindMemberFields(form);
        form.setSizeFull();
        setTabContent(admin, form);
        updateScreen();
    }

    private void addRequirementSpec(RequirementSpec rs, Tree tree) {
        // Add the item as a regular item.
        tree.addItem(rs);
        tree.setItemCaption(rs, rs.getName());
        tree.setItemIcon(rs, specIcon);
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
        tree.setItemIcon(rsn, specIcon);
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
        tree.setItemIcon(tp, testSuiteIcon);
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
        tree.setItemIcon(tp, testPlanIcon);
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
        tree.setItemIcon(t, testIcon);
        tree.setParent(t, plan);
        List<Step> stepList = t.getStepList();
        Collections.sort(stepList, (Step o1, Step o2)
                -> o1.getStepSequence() - o2.getStepSequence());
        stepList.forEach((s) -> {
            addStep(s, tree);
        });
    }

    private void addStep(Step s, Tree tree) {
        tree.addItem(s);
        tree.setItemCaption(s, "Step # " + s.getStepSequence());
        tree.setItemIcon(s, stepIcon);
        tree.setParent(s, s.getTestCase());
        tree.setChildrenAllowed(s, false);
    }

    private void addRequirement(Requirement req, Tree tree) {
        // Add the item as a regular item.
        tree.addItem(req);
        tree.setItemCaption(req, req.getUniqueId());
        tree.setItemIcon(req, requirementIcon);
        tree.setParent(req, req.getRequirementSpecNode());
        //No children
        tree.setChildrenAllowed(req, false);
    }

    public void addProject(Project p, Tree tree) {
        tree.addItem(p);
        tree.setItemCaption(p, p.getName());
        tree.setParent(p, p.getParentProjectId() == null
                ? projTreeRoot : p.getParentProjectId());
        tree.setItemIcon(p, projectIcon);
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
        if (!children) {
            // No subprojects
            tree.setChildrenAllowed(p, false);
        }
    }

    @Override
    protected void init(VaadinRequest request) {
        //Connect to the database defined in context.xml
        DataBaseManager.setPersistenceUnitName("VMPUJNDI");
        setInstance(this);
        if (reset == null && DataBaseManager.isDemo()) {
            LOG.info("Running on demo mode!");
            reset = new VMDemoResetThread();
            reset.start();
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
                                            Exceptions.printStackTrace(ex);
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
//                                        Exceptions.printStackTrace(ex);
//                                    } catch (Exception ex) {
//                                        Exceptions.printStackTrace(ex);
//                                    }
//                                    //Now update the sequence numbers
//                                    int count = 0;
//                                    for (Entry<Integer, Step> entry : map.entrySet()) {
//                                        entry.getValue().setStepSequence(++count);
//                                        try {
//                                            stepController.edit(entry.getValue());
//                                        } catch (Exception ex) {
//                                            Exceptions.printStackTrace(ex);
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
//                                            Exceptions.printStackTrace(ex);
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
            if (tree.getValue() instanceof Project) {
                Project p = (Project) tree.getValue();
                LOG.log(Level.FINE, "Selected: {0}", p.getName());
                displayProject(p);
            } else if (tree.getValue() instanceof Requirement) {
                Requirement req = (Requirement) tree.getValue();
                LOG.log(Level.FINE, "Selected: {0}", req.getUniqueId());
                displayRequirement(req);
            } else if (tree.getValue() instanceof RequirementSpec) {
                RequirementSpec rs = (RequirementSpec) tree.getValue();
                LOG.log(Level.FINE, "Selected: {0}", rs.getName());
                displayRequirementSpec(rs);
            } else if (tree.getValue() instanceof RequirementSpecNode) {
                RequirementSpecNode rsn = (RequirementSpecNode) tree.getValue();
                LOG.log(Level.FINE, "Selected: {0}", rsn.getName());
                displayRequirementSpecNode(rsn);
            } else if (tree.getValue() instanceof TestProject) {
                TestProject tp = (TestProject) tree.getValue();
                LOG.log(Level.FINE, "Selected: {0}", tp.getName());
                displayTestProject(tp);
            } else if (tree.getValue() instanceof TestPlan) {
                TestPlan tp = (TestPlan) tree.getValue();
                LOG.log(Level.FINE, "Selected: {0}", tp.getName());
                displayTestPlan(tp);
            } else if (tree.getValue() instanceof TestCase) {
                TestCase tc = (TestCase) tree.getValue();
                LOG.log(Level.FINE, "Selected: {0}", tc.getName());
                displayTestCase(tc);
            } else if (tree.getValue() instanceof Step) {
                Step step = (Step) tree.getValue();
                LOG.log(Level.FINE, "Selected: Step #{0}",
                        step.getStepSequence());
                displayStep(step);
            }
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
                        //We are at the root
                        createRootMenu(contextMenu);
                    } else if (tree.getValue() instanceof TestPlan) {
                        createTestPlanMenu(contextMenu);
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

    private void updateProjectList() {
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
        if (subwindow == null) {
            subwindow = new LoginDialog(this, small);
            subwindow.setVisible(true);
            subwindow.setClosable(false);
            subwindow.setResizable(false);
            subwindow.center();
            subwindow.setWidth(25, Unit.PERCENTAGE);
            subwindow.setHeight(25, Unit.PERCENTAGE);
            addWindow(subwindow);
        } else {
            subwindow.setVisible(true);
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

    private boolean isRequirementManager() {
        return roles.contains("requirement.manager");
    }

    private boolean isSeniorTester() {
        return roles.contains("senior.tester");
    }

    private boolean isLeader() {
        return roles.contains("leader");
    }

    private boolean isAdmin() {
        return roles.contains("admin");
    }

    private boolean isTester() {
        return roles.contains("tester");
    }

    private boolean isDesigner() {
        return roles.contains("teste.designer");
    }

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false,
            ui = ValidationManagerUI.class,
            widgetset = "net.sourceforge.javydreamercsw.validation.manager.web.AppWidgetSet")
    public static class Servlet extends VaadinServlet {

        public Servlet() {
            ProjectJpaController controller
                    = new ProjectJpaController(DataBaseManager
                            .getEntityManagerFactory());

            if (DataBaseManager.isDemo()
                    && controller.findProjectEntities().isEmpty()) {
                buildDemoTree();
            }
        }
    }

    private static class ByteToStringConverter implements Converter<String, byte[]> {

        @Override
        public byte[] convertToModel(String value,
                Class<? extends byte[]> targetType,
                Locale locale) throws ConversionException {
            try {
                if (value == null) {
                    value = "null";
                }
                return value.getBytes("UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Exceptions.printStackTrace(ex);
            }
            return null;
        }

        @Override
        public String convertToPresentation(byte[] value,
                Class<? extends String> targetType, Locale locale)
                throws ConversionException {
            return value == null ? "null" : new String(value, StandardCharsets.UTF_8);
        }

        @Override
        public Class<byte[]> getModelType() {
            return byte[].class;
        }

        @Override
        public Class<String> getPresentationType() {
            return String.class;
        }
    }
}
