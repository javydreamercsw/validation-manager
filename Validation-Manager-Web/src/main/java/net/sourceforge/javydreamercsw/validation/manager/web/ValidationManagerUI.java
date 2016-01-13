package net.sourceforge.javydreamercsw.validation.manager.web;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.Sizeable;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;
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
    private Tree tree;
    private final ThemeResource logo = new ThemeResource("vm_logo.png"),
            small = new ThemeResource("VMSmall.png");
    private VmUser user = null;
    private static final Logger LOG
            = Logger.getLogger(ValidationManagerUI.class.getSimpleName());
    private static VMDemoResetThread reset = null;
    private LoginDialog subwindow = null;
    private final String projTreeRoot = "Available Projects";
    private Component right;

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
                    = new ProjectJpaController(DataBaseManager.getEntityManagerFactory());
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

    private void updateScreen() {
        if (getUser() == null) {
            setContent(new Image("Logo", logo));
            showLoginDialog();
        } else {
            HorizontalSplitPanel hsplit = new HorizontalSplitPanel();
            hsplit.setLocked(true);
            if (tree == null) {
                // Build the left component
                tree = new Tree();
                tree.addItem(projTreeRoot);

                List<Project> projects = new ArrayList<>();
                ProjectJpaController controller
                        = new ProjectJpaController(DataBaseManager.getEntityManagerFactory());
                if (DataBaseManager.isDemo()
                        && controller.findProjectEntities().isEmpty()) {
                    buildDemoTree();
                }
                List<Project> all = controller.findProjectEntities();
                for (Project p : all) {
                    if (p.getParentProjectId() == null) {
                        projects.add(p);
                    }
                }
                LOG.log(Level.INFO, "Found {0} root projects!", projects.size());

                for (Project p : projects) {
                    tree.addItem(p);
                    tree.setItemCaption(p, p.getName());
                    tree.setParent(p, projTreeRoot);
                    //TODO: Need to scale down icon
                    //tree.setItemIcon(p, new ThemeResource("icons/book.svg"));
                    if (p.getProjectList().isEmpty()) {
                        // No subprojects
                        tree.setChildrenAllowed(p, false);
                    } else {
                        addChildrenProjects(p);
                    }
                }
                tree.addValueChangeListener(new ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        if (tree.getValue() instanceof Project) {
                            Project p = (Project) tree.getValue();
                            LOG.log(Level.INFO, "Selected: {0}", p.getName());
                            displayProject(p);
                        }
                    }
                });
                tree.setImmediate(true);
                tree.setSizeFull();
            }
            hsplit.setFirstComponent(tree);
            //Build the right component
            VerticalSplitPanel vsplit = new VerticalSplitPanel();
            vsplit.setFirstComponent(new Image("Logo", logo));
            if (right != null) {
                vsplit.setSecondComponent(right);
            }
            vsplit.setSplitPosition(25, Sizeable.UNITS_PERCENTAGE);
            //----------------------------------------------------------
            hsplit.setSecondComponent(vsplit);
            hsplit.setSplitPosition(25, Sizeable.UNITS_PERCENTAGE);
            setContent(hsplit);
        }
    }

    private void displayProject(Project p) {
        BeanItem<Project> item = new BeanItem<>(p,
                new String[]{"name", "notes"});
        // Bind it to a component
        Form form = new Form();
        form.setItemDataSource(item);
        right = form;
        updateScreen();
    }

    public void addChildrenProjects(Project p) {
        for (Project sub : p.getProjectList()) {
            // Add the item as a regular item.
            tree.addItem(sub);
            tree.setItemCaption(sub, sub.getName());
            // Set it to be a child.
            tree.setParent(sub, p);
            if (sub.getProjectList().isEmpty()) {
                //No children
                tree.setChildrenAllowed(sub, false);
            } else {
                addChildrenProjects(sub);
            }
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
        setStyleName("black");
        updateScreen();
    }

    private void showLoginDialog() {
        if (subwindow == null) {
            subwindow = new LoginDialog(this);
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
