package net.sourceforge.javydreamercsw.validation.manager.web;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Image;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalSplitPanel;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
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

        projects.stream().map((p) -> {
            tree.addItem(p);
            return p;
        }).map((p) -> {
            tree.setItemCaption(p, p.getName());
            return p;
        }).map((p) -> {
            tree.setParent(p, projTreeRoot);
            return p;
        }).forEachOrdered((p) -> {
            //TODO: Need to scale down icon
            //tree.setItemIcon(p, new ThemeResource("icons/book.svg"));
            if (p.getProjectList().isEmpty()) {
                // No subprojects
                tree.setChildrenAllowed(p, false);
            } else {
                addChildrenProjects(p, tree);
            }
        });
        tree.addValueChangeListener((Property.ValueChangeEvent event) -> {
            if (tree.getValue() instanceof Project) {
                Project p = (Project) tree.getValue();
                LOG.log(Level.INFO, "Selected: {0}", p.getName());
                displayProject(p);
            }
        });
        tree.setImmediate(true);
        tree.setSizeFull();
        return tree;
    }

    private ComboBox buildProjectDropDown() {
        // Have a bean container to put the beans in
        final BeanItemContainer<Project> container
                = new BeanItemContainer<>(Project.class);
        projects.forEach((p) -> {
            container.addItem(p);
        });
        ComboBox cb = new ComboBox("Select Project", container);
        cb.setFilteringMode(FilteringMode.CONTAINS);
        cb.setItemCaptionMode(ItemCaptionMode.PROPERTY);
        cb.setItemCaptionPropertyId("name");
        cb.addValueChangeListener((Property.ValueChangeEvent event) -> {
            Project p = (Project) cb.getValue();
            LOG.log(Level.INFO, "Selected: {0}", p.getName());
            displayProject(p);
        });
        //TODO: add icons
        //cb.setItemIconPropertyId("");
        return cb;
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
        hl.addComponent(buildProjectDropDown());
        return hl;
    }

    private void updateScreen() {
        if (getUser() == null) {
            setContent(new Image("", logo));
            showLoginDialog();
        } else {
            //Set up a menu header on top and the content below
            VerticalSplitPanel vs = new VerticalSplitPanel();
            vs.setSplitPosition(15, Unit.PERCENTAGE);
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
        BeanItem<Project> item = new BeanItem<>(p,
                new String[]{"name", "notes"});
        // Bind it to a component
        Form form = new Form();
        form.setItemDataSource(item);
        form.setEnabled(edit);
        left = form;
        updateScreen();
    }

    public void addChildrenProjects(Project p, Tree tree) {
        p.getProjectList().stream().map((sub) -> {
            // Add the item as a regular item.
            tree.addItem(sub);
            return sub;
        }).map((sub) -> {
            tree.setItemCaption(sub, sub.getName());
            return sub;
        }).map((sub) -> {
            // Set it to be a child.
            tree.setParent(sub, p);
            return sub;
        }).forEachOrdered((sub) -> {
            if (sub.getProjectList().isEmpty()) {
                //No children
                tree.setChildrenAllowed(sub, false);
            } else {
                addChildrenProjects(sub, tree);
            }
        });
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
        LOG.log(Level.INFO, "Found {0} root projects!", projects.size());
    }

    private void showLoginDialog() {
        if (subwindow == null) {
            subwindow = new LoginDialog(this, small);
            subwindow.setVisible(true);
            subwindow.setClosable(false);
            subwindow.setResizable(false);
            subwindow.center();
            addWindow(subwindow);
        } else {
            subwindow.setVisible(true);
        }
    }
}
