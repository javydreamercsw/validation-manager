package net.sourceforge.javydreamercsw.validation.manager.web;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.v7.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.themes.ValoTheme;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.RequirementSpecPK;
import com.validation.manager.core.db.SpecLevel;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.TestProjectJpaController;
import com.validation.manager.core.db.controller.TestPlanJpaController;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.db.controller.RequirementSpecJpaController;
import com.validation.manager.core.db.controller.SpecLevelJpaController;
import com.validation.manager.core.db.controller.RequirementSpecNodeJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.RequirementServer;
import com.validation.manager.core.server.core.RequirementSpecNodeServer;
import com.validation.manager.core.server.core.RequirementSpecServer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
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
    private VmUser user = null;
    private static final Logger LOG
            = Logger.getLogger(ValidationManagerUI.class.getSimpleName());
    private static VMDemoResetThread reset = null;
    private LoginDialog subwindow = null;
    private final String projTreeRoot = "Available Projects";
    private Component left, right;
    private final List<Project> projects = new ArrayList<>();
    private final VaadinIcons projectIcon = VaadinIcons.RECORDS;
    private final VaadinIcons specIcon = VaadinIcons.BOOK;
    private final VaadinIcons requirementIcon = VaadinIcons.PIN;
    private final VaadinIcons testSuiteIcon = VaadinIcons.FILE_TREE;
    private final VaadinIcons testIcon = VaadinIcons.FILE_TEXT;
    private final Tree tree = new Tree();

    /**
     * @return the user
     */
    protected VmUser getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    protected void setUser(VmUser user) {
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
                        buildProjectTree();
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
        right = form;
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
                        buildProjectTree();
                        displayTestPlan(tp, false);
                        updateScreen();
                        buildProjectTree();
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
        right = form;
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
                        buildProjectTree();
                        displayTestProject(tp, false);
                        updateScreen();
                        buildProjectTree();
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
        right = form;
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
                        buildProjectTree();
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
        right = form;
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
        right = form;
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

    private void buildDemoTree() {
        try {
            ProjectJpaController controller
                    = new ProjectJpaController(DataBaseManager
                            .getEntityManagerFactory());
            LOG.info("Creating demo projects...");
            //Create some test projects
            Project rootProject = new Project("Demo");
            controller.create(rootProject);
            for (int i = 0; i < 5; i++) {
                Project temp = new Project("Sub " + (i + 1));
                controller.create(temp);
                addDemoProjectRequirements(temp);
                rootProject.getProjectList().add(temp);
            }
            addDemoProjectRequirements(rootProject);
            controller.edit(rootProject);
            LOG.info("Done!");
        } catch (NonexistentEntityException ex) {
            Exceptions.printStackTrace(ex);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void addDemoProjectRequirements(Project p) throws Exception {
        for (int i = 0; i < 5; i++) {
            //Create a spec
            RequirementSpecServer temp = new RequirementSpecServer("Spec " + i,
                    "Description " + i, p.getId(), 1);
            temp.write2DB();

            RequirementSpecNodeServer node = temp.addSpecNode("Node " + i,
                    "Description " + i, "Scope " + i);
            for (int y = 0; y < 5; y++) {
                RequirementServer req
                        = new RequirementServer("Requirement " + y,
                                "Description " + y,
                                node.getRequirementSpecNodePK(), "Notes",
                                1, 1);
                req.write2DB();
                node.getRequirementList().add(req.getEntity());
            }
            node.write2DB();
            p.getRequirementSpecList().add(temp.getEntity());
        }
        new ProjectJpaController(DataBaseManager
                .getEntityManagerFactory()).edit(p);
    }

    private Tree buildProjectTree() {
        tree.removeAllItems();
        tree.addItem(projTreeRoot);

        projects.forEach((p) -> {
            if (p.getParentProjectId() == null) {
                addProject(p, tree);
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
                    } else {
                        //We are at the root
                        createRootMenu(contextMenu);
                    }
                };
        contextMenu.addContextMenuTreeListener(treeItemListener);
        tree.setImmediate(true);
        tree.expandItem(projTreeRoot);
        tree.setSizeFull();
        return tree;
    }

    private void createRootMenu(ContextMenu menu) {
        ContextMenu.ContextMenuItem create
                = menu.addItem("Create Project", projectIcon);
        create.addItemClickListener(
                (ContextMenu.ContextMenuItemClickEvent event) -> {
                    displayProject(new Project(), true);
                });
    }

    private void createProjectMenu(ContextMenu menu) {
        ContextMenu.ContextMenuItem create
                = menu.addItem("Create Sub Project", projectIcon);
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
            hsplit.setFirstComponent(left);
        }
        //Build the right component
        if (right != null) {
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
        if (getUser() == null) {
            setContent(new Image("", logo));
            showLoginDialog();
        } else {
            //Set up a menu header on top and the content below
            VerticalSplitPanel vs = new VerticalSplitPanel();
            vs.setSplitPosition(20, Unit.PERCENTAGE);
            //Set up top menu panel
            vs.setFirstComponent(getMenu());
            //Add the content
            vs.setSecondComponent(getContentComponent());
            setContent(vs);
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
                    buildProjectTree();
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
        right = form;
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
        tree.setItemIcon(tp, testIcon);
        tree.setParent(tp, tp.getTestProject());
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
        updateProjectList();
        updateScreen();
        Page.getCurrent().setTitle("Validation Manager");
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
        left = buildProjectTree();
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

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false,
            ui = ValidationManagerUI.class,
            widgetset = "net.sourceforge.javydreamercsw.validation.manager.web.AppWidgetSet")
    public static class Servlet extends VaadinServlet {

    }
}
