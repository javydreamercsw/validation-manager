package net.sourceforge.javydreamercsw.validation.manager.web;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Item;
import com.vaadin.server.Sizeable;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Image;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
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
    private Panel main;
    private final ThemeResource logo = new ThemeResource("vm_logo.png");
    private final ThemeResource small = new ThemeResource("VMSmall.png");
    private VmUser user = null;
    private static final Logger LOG
            = Logger.getLogger(ValidationManagerUI.class.getSimpleName());
    private static VMDemoResetThread reset;
    private LoginDialog subwindow = null;

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

    private void updateScreen() {
        if (getUser() == null) {
            showLoginDialog();
        } else {
            // Have a panel to put stuff in
            VerticalLayout vl = new VerticalLayout();

            HorizontalSplitPanel vsplit = new HorizontalSplitPanel();
            vsplit.setLocked(true);

            tree = new Tree("Available Projects");

            Item root = tree.addItem("Root");

            List<Project> projects = new ArrayList<>();
            ProjectJpaController controller
                    = new ProjectJpaController(DataBaseManager.getEntityManagerFactory());
            if (DataBaseManager.isDemo()
                    && controller.findProjectEntities().isEmpty()) {
                try {
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
            List<Project> all = controller.findProjectEntities();
            for (Project p : all) {
                if (p.getParentProjectId() == null) {
                    projects.add(p);
                }
            }
            LOG.log(Level.INFO, "Found {0} projects!", projects.size());

            for (Project p : projects) {
                Item parent = tree.addItem(p.getName());
                tree.setParent(parent, root);
                if (p.getProjectList().isEmpty()) {
                    // No subprojects
                    tree.setChildrenAllowed(p, false);
                } else {
                    // Add children (moons) under the planets.
                    for (Project sub : p.getProjectList()) {
                        // Add the item as a regular item.
                        tree.addItem(sub.getName());
                        // Set it to be a child.
                        tree.setParent(sub, parent);
                        // Make the moons look like leaves.
                        tree.setChildrenAllowed(sub, false);
                    }
                    // Expand the subtree.
                }
            }
            tree.setSizeFull();
            vsplit.setFirstComponent(tree);
            vsplit.setSecondComponent(new Image("Image from file", logo));
            vsplit.setSplitPosition(25, Sizeable.UNITS_PERCENTAGE);
            vl.addComponent(vsplit);
            setContent(vsplit);
        }
    }

    @Override
    protected void init(VaadinRequest request) {
        if (reset == null && DataBaseManager.isDemo()) {
            LOG.info("Running on demo mode!");
            reset = new VMDemoResetThread();
            reset.start();
        }
        //Connect to the database defines in context.xml
        DataBaseManager.setPersistenceUnitName("VMPUJNDI");
        setInstance(this);
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
        }else{
            subwindow.setVisible(true);
        }
    }
}
