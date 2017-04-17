/*
 * This handles all the test execution activities.
 */
package net.sourceforge.javydreamercsw.validation.manager.web.execution;

import com.vaadin.ui.HorizontalLayout;
import com.validation.manager.core.server.core.TestCaseExecutionServer;
import java.util.List;
import java.util.TreeMap;
import net.sourceforge.javydreamercsw.validation.manager.web.VMWindow;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

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
        TreeMap<Integer, TreeMap<Integer, ExecutionWizardStep>> sorted = new TreeMap<>();
        executions.forEach((tce) -> {
            tce.getExecutionStepList().forEach(es -> {
                if (tcID < 0
                        || es.getExecutionStepPK().getStepTestCaseId() == tcID) {
                    if (!sorted.containsKey(es.getExecutionStepPK().getStepTestCaseId())) {
                        sorted.put(es.getExecutionStepPK().getStepTestCaseId(),
                                new TreeMap<>());
                    }
                    sorted.get(es.getExecutionStepPK().getStepTestCaseId())
                            .put(es.getStep().getStepSequence(),
                                    new ExecutionWizardStep(execution, ui, es));
                }
            });
        });
        sorted.values().forEach(tm -> {
            tm.values().forEach(ew -> {
                execution.addStep(ew);
            });
        });
        execution.setDisplayedMaxTitles(3);
        execution.addListener(new WizardProgressListener() {
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
                //TODO: Add confirmation prior to locking the Test Case
                //See: https://vaadin.com/directory#!addon/messagebox
                ui.removeWindow(ExecutionWindow.this);
            }

            @Override
            public void wizardCancelled(WizardCancelledEvent event) {
                ui.removeWindow(ExecutionWindow.this);
            }
        });
        layout.addComponent(execution);
        layout.setSizeFull();
        setContent(layout);
    }
}
