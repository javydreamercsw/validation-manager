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

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import static com.validation.manager.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMException;
import com.validation.manager.core.VMUI;
import com.validation.manager.core.api.notification.NotificationTypes;
import com.validation.manager.core.db.ExecutionStepHasVmUser;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.ExecutionStepAnswerJpaController;
import com.validation.manager.core.db.controller.ExecutionStepHasVmUserJpaController;
import com.validation.manager.core.server.core.ActivityServer;
import com.validation.manager.core.server.core.ExecutionStepServer;
import com.validation.manager.core.server.core.RoleServer;
import com.validation.manager.core.server.core.TestCaseExecutionServer;
import de.steinwedel.messagebox.ButtonOption;
import de.steinwedel.messagebox.MessageBox;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import net.sourceforge.javydreamercsw.validation.manager.web.component.VMWindow;
import net.sourceforge.javydreamercsw.validation.manager.web.notification.NotificationManager;
import org.openide.util.Lookup;
import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.WizardStep;
import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

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
        Wizard execution = new Wizard();
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
                if (reviewer) {
                    MessageBox prompt = MessageBox.createQuestion()
                            .withCaption(TRANSLATOR.translate("release.test.case.title"))
                            .withMessage(TRANSLATOR.translate("release.test.case.message"))
                            .withYesButton(() -> {
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
                            }, ButtonOption.focus(),
                                    ButtonOption.icon(VaadinIcons.CHECK))
                            .withNoButton(ButtonOption.icon(VaadinIcons.CLOSE));
                    prompt.getWindow().setIcon(ValidationManagerUI.SMALL_APP_ICON);
                    prompt.open();
                } else {
                    MessageBox prompt = MessageBox.createQuestion()
                            .withCaption(TRANSLATOR.translate("lock.test.case.title"))
                            .withMessage(TRANSLATOR.translate("lock.test.case.message"))
                            .withYesButton(() -> {
                                for (WizardStep step : execution.getSteps()) {
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
                            }, ButtonOption.focus(),
                                    ButtonOption.icon(VaadinIcons.CHECK))
                            .withNoButton(ButtonOption.icon(VaadinIcons.CLOSE));
                    prompt.getWindow().setIcon(ValidationManagerUI.SMALL_APP_ICON);
                    prompt.open();
                }
                ValidationManagerUI.getInstance().removeWindow(ExecutionWindow.this);
            }

            @Override
            public void wizardCancelled(WizardCancelledEvent event) {
                ValidationManagerUI.getInstance().removeWindow(ExecutionWindow.this);
            }
        });
        layout.addComponent(execution);
        layout.setSizeFull();
        setContent(layout);
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
