/*
 * Copyright 2017 Javier A. Ortiz Bultron javier.ortiz.78@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sourceforge.javydreamercsw.validation.manager.web.execution;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.UI;
import static net.sourceforge.javydreamercsw.validation.manager.web.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMException;
import net.sourceforge.javydreamercsw.validation.manager.web.core.VMUI;
import com.validation.manager.core.api.notification.NotificationTypes;
import com.validation.manager.core.db.ExecutionStepHasVmUser;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.ExecutionStepAnswerJpaController;
import com.validation.manager.core.db.controller.ExecutionStepHasVmUserJpaController;
import com.validation.manager.core.server.core.ActivityServer;
import com.validation.manager.core.server.core.ExecutionStepServer;
import com.validation.manager.core.server.core.RoleServer;
import com.validation.manager.core.server.core.TestCaseExecutionServer;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import net.sourceforge.javydreamercsw.validation.manager.web.component.VMWindow;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.FlowWizard;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardCancelledEvent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardCompletedEvent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardCompletedEvent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardProgressListener;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardStepActivationEvent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardStepCompletionEvent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardStepSetChangedEvent;
import net.sourceforge.javydreamercsw.validation.manager.web.notification.NotificationManager;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class ExecutionWindow extends VMWindow {

    private final boolean reviewer;
    private static final Logger LOG
            = Logger.getLogger(ExecutionWindow.class.getSimpleName());

    /**
     * Display all the executions one after another. This view is used as well
     * for reviewing the execution
     *
     * @param executions Executions to display.
     * @param reviewer true if this is for a reviewer
     */
    public ExecutionWindow(
            List<TestCaseExecutionServer> executions, boolean reviewer) {
        super();
        this.reviewer = reviewer;
        init(executions, -1);
    }

    /**
     * Display all the executions one after another for the specific test case.
     *
     * @param executions Executions to display.
     * @param tcID test case to show
     * @param reviewer true if this is for a reviewer
     */
    public ExecutionWindow(
            List<TestCaseExecutionServer> executions, int tcID, boolean reviewer) {
        super();
        this.reviewer = reviewer;
        init(executions, tcID);
    }

    private void init(List<TestCaseExecutionServer> executions, int tcID) {
        HorizontalLayout layout = new HorizontalLayout();
        FlowWizard execution = new FlowWizard();
        TreeMap<Integer, TreeMap<Integer, ExecutionWizardStep>> sorted
                = new TreeMap<>();
        executions.forEach((tce) -> {
            tce.getExecutionStepList().forEach(es -> {
                if (tcID < 0
                        || es.getExecutionStepPK().getStepTestCaseId() == tcID) {
                    if (!sorted.containsKey(es.getExecutionStepPK()
                            .getStepTestCaseId())) {
                        sorted.put(es.getExecutionStepPK().getStepTestCaseId(),
                                new TreeMap<>());
                    }
                    sorted.get(es.getExecutionStepPK().getStepTestCaseId())
                            .put(es.getStep().getStepSequence(),
                                    new ExecutionWizardStep(execution, es,
                                            reviewer));
                }
            });
        });
        sorted.values().forEach(tm -> {
            tm.values().forEach(ew -> {
                execution.addStep(ew);
            });
        });
        //v8 wizard.setDisplayedMaxTitles has no Flow equivalent; the step
        //counter in the header serves the same purpose.
        execution.addListener(new FlowWizardProgressListener() {
            @Override
            public void activeStepChanged(FlowWizardStepActivationEvent event) {
                //Do nothing
            }

            @Override
            public void stepSetChanged(FlowWizardStepSetChangedEvent event) {
                //Do nothing
            }

            @Override
            public void stepCompleted(FlowWizardStepCompletionEvent event) {
                //Do nothing
            }

            @Override
            public void wizardCompleted(FlowWizardCompletedEvent event) {
                if (reviewer) {
                    ConfirmDialog prompt = new ConfirmDialog();
                    prompt.setHeader(TRANSLATOR.translate("release.test.case.title"));
                    prompt.setText(TRANSLATOR.translate("release.test.case.message"));
                    prompt.setConfirmButton(TRANSLATOR.translate("general.yes"),
                            (ComponentEventListener<ConfirmDialog.ConfirmEvent>) e -> {
                                execution.getSteps().stream().map((step)
                                        -> (ExecutionWizardStep) step).map((s)
                                        -> s.getExecutionStep()).filter((ess) -> (!ess.getLocked()
                                        && ess.getResultId() != null))
                                        .forEachOrdered((ess) -> {
                                            try {
                                                if (ess.getReviewResultId().getId() == 2) {
                                                    //TODO: Failed, send back to retest?
                                                    ess.setLocked(false);
                                                }
                                                ess.setReviewed(true);
                                                save(ess);
                                                new ActivityServer(4, new Date(),
                                                        TRANSLATOR.translate("test.review.desc")
                                                                .replaceAll("%u",
                                                                        ((VMUI) UI.getCurrent())
                                                                                .getUser().toString())
                                                                .replaceAll("%i",
                                                                        TRANSLATOR.translate("general.test.case")),
                                                        ((VMUI) UI.getCurrent()).getUser().getEntity())
                                                        .write2DB();
                                                Lookup.getDefault().lookup(NotificationManager.class)
                                                        .addNotification(TRANSLATOR.translate("notification.review.complete")
                                                                .replaceAll("%r",
                                                                        TRANSLATOR.translate(ess
                                                                                .getReviewResultId()
                                                                                .getReviewName()))
                                                                .replaceAll("%i",
                                                                        ess.getTestCaseExecution().getName()),
                                                                NotificationTypes.REVIEW,
                                                                ess.getAssignee(),
                                                                ((VMUI) UI.getCurrent())
                                                                        .getUser().getEntity());
                                                ValidationManagerUI.getInstance()
                                                        .updateScreen();
                                            } catch (VMException ex) {
                                                LOG.log(Level.SEVERE, null, ex);
                                            } catch (Exception ex) {
                                                LOG.log(Level.SEVERE, null, ex);
                                            }
                                        });
                            });
                    prompt.setCancelButton(TRANSLATOR.translate("general.no"),
                            (ComponentEventListener<ConfirmDialog.CancelEvent>) (e) -> {
                                //Do nothing
                            });
                    prompt.open();
                } else {
                    ConfirmDialog prompt = new ConfirmDialog();
                    prompt.setHeader(TRANSLATOR.translate("lock.test.case.title"));
                    prompt.setText(TRANSLATOR.translate("lock.test.case.message"));
                    prompt.setConfirmButton(TRANSLATOR.translate("general.yes"),
                            (ComponentEventListener<ConfirmDialog.ConfirmEvent>) (e) -> {
                                for (var step : execution.getSteps()) {
                                    ExecutionWizardStep s = (ExecutionWizardStep) step;
                                    ExecutionStepServer ess = s.getExecutionStep();
                                    if (!ess.getLocked()
                                            && ess.getResultId() != null) {
                                        try {
                                            ess.setLocked(true);
                                            save(ess);
                                            new ActivityServer(3, new Date(),
                                                    TRANSLATOR.translate("test.execution.desc")
                                                            .replaceAll("%u",
                                                                    ((VMUI) UI.getCurrent())
                                                                            .getUser().toString())
                                                            .replaceAll("%i", s.getCaption()),
                                                    ((VMUI) UI.getCurrent()).getUser().getEntity())
                                                    .write2DB();
                                            ValidationManagerUI.getInstance()
                                                    .updateScreen();
                                        } catch (VMException ex) {
                                            LOG.log(Level.SEVERE, null, ex);
                                        } catch (Exception ex) {
                                            LOG.log(Level.SEVERE, null, ex);
                                        }
                                    }
                                }
                            });
                    prompt.setCancelButton(TRANSLATOR.translate("general.no"),
                            (ComponentEventListener<ConfirmDialog.CancelEvent>) (e) -> {
                                //Do nothing
                            });
                    prompt.open();
                }
                //Flow equivalent of v8 UI.removeWindow(this)
                ValidationManagerUI.getInstance().closeDialog(ExecutionWindow.this);
            }

            @Override
            public void wizardCancelled(FlowWizardCancelledEvent event) {
                ValidationManagerUI.getInstance().closeDialog(ExecutionWindow.this);
            }
        });
        layout.add(execution);
        layout.setSizeFull();
        add(layout);
        setWidth("100%");
        setHeight("100%");
    }

    private void save(ExecutionStepServer ess) throws VMException {
        //Handle temporary values
        ExecutionStepAnswerJpaController c
                = new ExecutionStepAnswerJpaController(DataBaseManager
                        .getEntityManagerFactory());
        ess.getExecutionStepAnswerList().forEach(answer -> {
            try {
                c.create(answer);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        });
        //Set tester/reviewer
        ess.write2DB();
        boolean tester = false;
        boolean review = false;
        for (ExecutionStepHasVmUser temp : ess.getExecutionStepHasVmUserList()) {
            if (temp.getRole().getRoleName().equals("tester")) {
                tester = true;
            }
            if (temp.getRole().getRoleName().equals("quality")) {
                review = true;
            }
        }
        VmUser vmUser = ((VMUI) UI.getCurrent()).getUser().getEntity();
        ExecutionStepHasVmUserJpaController c2
                = new ExecutionStepHasVmUserJpaController(DataBaseManager
                        .getEntityManagerFactory());
        if (!tester) {
            try {
                ExecutionStepHasVmUser t = new ExecutionStepHasVmUser();
                t.setExecutionStep(ess.getEntity());
                t.setRole(RoleServer.getRole("tester"));
                t.setVmUser(vmUser);
                c2.create(t);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        if (reviewer && !review) {
            try {
                ExecutionStepHasVmUser r = new ExecutionStepHasVmUser();
                r.setExecutionStep(ess.getEntity());
                r.setRole(RoleServer.getRole("quality"));
                r.setVmUser(vmUser);
                c2.create(r);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        ess.update();
    }
}
