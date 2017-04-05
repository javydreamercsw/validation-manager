package net.sourceforge.javydreamercsw.validation.manager.web.wizard.plan;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.themes.ValoTheme;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.TestCaseExecution;
import com.validation.manager.core.db.controller.TestCaseExecutionJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.TestCaseExecutionServer;
import com.validation.manager.core.server.core.TestCaseServer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.ByteToStringConverter;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import org.vaadin.teemu.wizards.WizardStep;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class DetailStep implements WizardStep {

    private List<Integer> testCases;
    private final TestCaseExecution tce;
    private List<Integer> projects;
    private static final Logger LOG
            = Logger.getLogger(DetailStep.class.getSimpleName());
    private final ValidationManagerUI ui;
    private final Project p;

    public DetailStep(ValidationManagerUI ui, Project p) {
        this.tce = new TestCaseExecution();
        this.ui = ui;
        this.p = p;
    }

    @Override
    public String getCaption() {
        return "Add Details";
    }

    @Override
    public Component getContent() {
        Panel form = new Panel("Execution Detail");
        FormLayout layout = new FormLayout();
        form.setContent(layout);
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(TestCaseExecution.class);
        binder.setItemDataSource(tce);
        TextArea name = new TextArea("Name");
        name.setConverter(new ByteToStringConverter());
        binder.bind(name, "name");
        layout.addComponent(name);
        TextArea scope = new TextArea("Scope");
        scope.setConverter(new ByteToStringConverter());
        binder.bind(scope, "scope");
        layout.addComponent(scope);
        if (tce.getId() != null) {
            TextArea conclusion = new TextArea("Conclusion");
            conclusion.setConverter(new ByteToStringConverter());
            binder.bind(conclusion, "conclusion");
            layout.addComponent(conclusion);
            conclusion.setSizeFull();
            layout.addComponent(conclusion);
        }
        binder.setBuffered(false);
        binder.bindMemberFields(form);
        form.setSizeFull();
        return form;
    }

    @Override
    public boolean onAdvance() {
        try {
            //Create the record
            new TestCaseExecutionJpaController(DataBaseManager
                    .getEntityManagerFactory()).create(tce);
            TestCaseExecutionServer tces = new TestCaseExecutionServer(tce);
            //Now create the execution records
            TestCaseServer tc;
            for (Integer id : testCases) {
                //Retrieve the TestCase to get the steps
                tc = new TestCaseServer(id);
                tces.addTestCase(tc.getEntity());
            }
            projects.stream().map((pid)
                    -> new ProjectServer(pid)).forEachOrdered((ps) -> {
                LOG.log(Level.INFO, "Adding project: {0}", ps.getName());
                tces.getProjects().add(ps.getEntity());
            });
            try {
                tces.write2DB();
                ui.buildProjectTree(ui.getSelectdValue());
                ui.updateProjectList();
                ui.updateScreen();
                ui.displayObject(tces.getEntity(), false);
                return true;
            } catch (NonexistentEntityException ex) {
                LOG.log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean onBack() {
        return true;
    }

    /**
     * @return the testCases
     */
    public List<Integer> getTestCases() {
        return testCases;
    }

    /**
     * @param testCases the testCases to set
     */
    public void setTestCases(List<Integer> testCases) {
        this.testCases = testCases;
    }

    public void setProjects(List<Integer> projects) {
        this.projects = projects;
    }
}
