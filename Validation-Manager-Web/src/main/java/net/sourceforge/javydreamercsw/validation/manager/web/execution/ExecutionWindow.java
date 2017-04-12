/*
 * This handles all the test execution activities.
 */
package net.sourceforge.javydreamercsw.validation.manager.web.execution;

import com.vaadin.ui.HorizontalLayout;
import com.validation.manager.core.server.core.TestCaseExecutionServer;
import java.util.List;
import net.sourceforge.javydreamercsw.validation.manager.web.VMWindow;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import org.vaadin.teemu.wizards.Wizard;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class ExecutionWindow extends VMWindow {

    private final ValidationManagerUI ui;

    /**
     * Display all the executions one after another.
     *
     * @param ui ValidationManagerUI instance
     * @param executions Executions to display.
     */
    public ExecutionWindow(ValidationManagerUI ui,
            List<TestCaseExecutionServer> executions) {
        super();
        init(executions, -1);
        this.ui = ui;
    }

    /**
     * Display all the executions one after another for the specific test case.
     *
     * @param ui ValidationManagerUI instance
     * @param executions Executions to display.
     * @param tcID test case to show
     */
    public ExecutionWindow(ValidationManagerUI ui,
            List<TestCaseExecutionServer> executions, int tcID) {
        super();
        this.ui = ui;
        init(executions, tcID);
    }

    private void init(List<TestCaseExecutionServer> executions, int tcID) {
        HorizontalLayout layout = new HorizontalLayout();
        Wizard execution = new Wizard();
        executions.forEach((tce) -> {
            tce.getExecutionStepList().forEach(es -> {
                if (tcID < 0 || es.getExecutionStepPK().getStepTestCaseId() == tcID) {
                    execution.addStep(new ExecutionWizardStep(execution, ui, es));
                }
            });
        });
        execution.setDisplayedMaxTitles(3);
        layout.addComponent(execution);
        layout.setSizeFull();
        setContent(layout);
    }
}
