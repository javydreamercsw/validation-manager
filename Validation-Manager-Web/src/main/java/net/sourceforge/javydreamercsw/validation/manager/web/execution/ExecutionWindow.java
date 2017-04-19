/*
 * This handles all the test execution activities.
 */
package net.sourceforge.javydreamercsw.validation.manager.web.execution;

import com.vaadin.ui.HorizontalLayout;
import com.validation.manager.core.server.core.TestCaseExecutionServer;
import de.steinwedel.messagebox.MessageBox;
import java.util.List;
import java.util.TreeMap;
import net.sourceforge.javydreamercsw.validation.manager.web.VMWindow;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import org.openide.util.Exceptions;
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
                MessageBox prompt = MessageBox.createQuestion()
                        .withCaption("Do you want to lock the test case?")
                        .withMessage("Locked test cases are commited and can no "
                                + "longer be modified.\nIt would be equivalent "
                                + "to documenting in paper.")
                        .withYesButton(() -> {
                    execution.getSteps().stream().map((step)
                            -> (ExecutionWizardStep) step).map((s)
                            -> s.getStep()).filter((ess) -> (!ess.isLocked()
                            && ess.getResultId() != null))
                            .forEachOrdered((ess) -> {
                                try {
                                    ess.setLocked(true);
                                    ess.write2DB();
                                } catch (Exception ex) {
                                    Exceptions.printStackTrace(ex);
                                }
                            });
                        })
                        .withNoButton(() -> {
                            System.out.println("No button was pressed.");
                        });
                prompt.getWindow().setIcon(ValidationManagerUI.SMALL_APP_ICON);
                prompt.open();
                ui.removeWindow(ExecutionWindow.this);
                ui.updateScreen();
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
