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

    public ExecutionWindow(ValidationManagerUI ui,
            List<TestCaseExecutionServer> executions) {
        super();
        HorizontalLayout layout = new HorizontalLayout();
        Wizard execution = new Wizard();
        executions.forEach((tce) -> {
            tce.getExecutionStepList().forEach(es -> {
                execution.addStep(new ExecutionWizardStep(execution, ui, es));
            });
        });
        execution.setDisplayedMaxTitles(3);
        layout.addComponent(execution);
        layout.setSizeFull();
        setContent(layout);
    }
}
