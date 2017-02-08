package net.sourceforge.javydreamercsw.validation.manager.web;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Image;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.themes.ValoTheme;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import org.openide.util.Exceptions;

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

    private void displayRequirement(Requirement req) {
        displayRequirement(req, false);
    }

    private void displayRequirement(Requirement req, boolean edit) {
        Panel form = new Panel("Requirement Detail");
        FormLayout layout = new FormLayout();
        form.setContent(layout);
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(Requirement.class);
        binder.setItemDataSource(req);
        Field id = binder.buildAndBind("Requirement ID", "uniqueId");
        layout.addComponent(id);
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

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false,
            ui = ValidationManagerUI.class,
            widgetset = "net.sourceforge.javydreamercsw.validation.manager.web.AppWidgetSet")
    public static class Servlet extends VaadinServlet {

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
                rootProject.getProjectList().add(temp);
            }
            controller.edit(rootProject);
            LOG.info("Done!");
        } catch (NonexistentEntityException ex) {
            Exceptions.printStackTrace(ex);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private Tree buildProjectTree() {
        Tree tree = new Tree();
        tree.addItem(projTreeRoot);

        projects.forEach((p) -> {
            if (p.getParentProjectId() == null) {
                addProject(p, tree);
            }
        });
        tree.addValueChangeListener((Property.ValueChangeEvent event) -> {
            if (tree.getValue() instanceof Project) {
                Project p = (Project) tree.getValue();
                LOG.log(Level.INFO, "Selected: {0}", p.getName());
                displayProject(p);
            } else if (tree.getValue() instanceof Requirement) {
                Requirement req = (Requirement) tree.getValue();
                LOG.log(Level.INFO, "Selected: {0}", req.getUniqueId());
                displayRequirement(req);
            }
        });
        tree.setImmediate(true);
        tree.setSizeFull();
        return tree;
    }

    private Component getContentComponent() {
        HorizontalSplitPanel hsplit = new HorizontalSplitPanel();
        hsplit.setLocked(true);
        // Build the left component
        //left = buildProjectTree();
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
        BeanFieldGroup binder = new BeanFieldGroup(Project.class);
        binder.setItemDataSource(p);
        layout.addComponent(binder.buildAndBind("Name", "name"));
        layout.addComponent(binder.buildAndBind("Notes", "notes"));
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
        all.stream().filter((p)
                -> (p.getParentProjectId() == null)).forEachOrdered((p) -> {
            projects.add(p);
        });
        left = buildProjectTree();
        LOG.log(Level.INFO, "Found {0} root projects!", projects.size());
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
}
