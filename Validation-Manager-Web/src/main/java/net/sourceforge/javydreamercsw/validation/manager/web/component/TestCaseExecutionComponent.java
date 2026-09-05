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
package net.sourceforge.javydreamercsw.validation.manager.web.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import static net.sourceforge.javydreamercsw.validation.manager.web.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.TestCaseExecution;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.ExecutionStepServer;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.TestCaseExecutionServer;
import com.validation.manager.core.tool.Tool;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.FlowWizard;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.FlowWizardStep;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardCancelledEvent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardCompletedEvent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardProgressListener;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardStepActivationEvent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardStepSetChangedEvent;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class TestCaseExecutionComponent extends VMWindow {

    private final TestCaseExecution tce;
    private final boolean edit;
    private final ProjectServer ps;
    private static final Logger LOG
            = Logger.getLogger(TestCaseComponent.class.getSimpleName());

    public TestCaseExecutionComponent(TestCaseExecution tce, ProjectServer ps,
            boolean edit) {
        this.tce = tce;
        this.ps = ps;
        this.edit = edit;
        setHeaderTitle(TRANSLATOR.translate("test.case.execution.detail"));
        init();
    }

    public TestCaseExecutionComponent(TestCaseExecution tce, ProjectServer ps,
            boolean edit, String caption) {
        this.tce = tce;
        this.edit = edit;
        this.ps = ps;
        setHeaderTitle(caption);
        init();
    }

    private void init() {
        FormLayout layout = new FormLayout();
        TextField name = new TextField(TRANSLATOR.translate("general.name"));
        name.setRequiredIndicatorVisible(true);
        TextField scope = new TextField(TRANSLATOR.translate("general.scope"));
        //TODO: Show when finished
        TextArea conclusion = new TextArea(TRANSLATOR.translate("general.conclusion"));
        Button cancel = new Button(TRANSLATOR.translate("general.cancel"));
        cancel.addClickListener((event) -> {
            if (tce.getId() == null) {
                ((ValidationManagerUI) getUI().orElse(null))
                        .displayObject(((ValidationManagerUI) getUI().orElse(null))
                                .getSelectdValue());
            } else {
                ((ValidationManagerUI) getUI().orElse(null))
                        .displayObject(tce, false);
            }
        });
        if (edit) {
            if (tce.getId() == null) {
                TestCaseExecutionServer tces = new TestCaseExecutionServer();
                //Creating a new one
                Button save = new Button(TRANSLATOR.translate("general.save"));
                save.addClickListener((event) -> {
                    if (name.getValue() == null
                            || name.getValue().trim().isEmpty()) {
                        Notification.show(TRANSLATOR.translate("missing.name.message"));
                        return;
                    }
                    Map<Requirement, History> history = new HashMap<>();
                    if (ps != null) {
                        List<Requirement> toApprove = new ArrayList<>();
                        Tool.extractRequirements(ps).forEach((r) -> {
                            //Check each requirement and see if they have minor versions (last version is not baselined)
                            History h = r.getHistoryList().get(r.getHistoryList().size() - 1);
                            if (h.getMajorVersion() == 0
                                    || h.getMidVersion() > 0
                                    || h.getMinorVersion() > 0) {
                                if (r.getHistoryList().size() == 1) {
                                    //Nothing to choose from
                                    history.put(r, h);
                                } else {
                                    toApprove.add(r);
                                }
                            } else {
                                history.put(r, h);
                            }
                        });
                        if (!toApprove.isEmpty()) {
                            ConfirmDialog prompt = new ConfirmDialog();
                            prompt.setHeader(TRANSLATOR
                                    .translate("missing.baseline.requirement.title"));
                            prompt.setText(TRANSLATOR
                                    .translate("missing.baseline.requirement.message"));
                            prompt.setConfirmButton(TRANSLATOR.translate("general.yes"),
                                    (e) -> {
                                        //Start the wizard
                                        FlowWizard w = new FlowWizard();
                                        VMWindow sw = new VMWindow();
                                        toApprove.forEach(r -> {
                                            w.addStep(new SelectRequirementVersionStep(r));
                                        });
                                        w.addListener(new net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardProgressListener() {
                                            @Override
                                            public void activeStepChanged(
                                                    FlowWizardStepActivationEvent event) {
                                                //Do nothing
                                            }

                                            @Override
                                            public void stepSetChanged(
                                                    FlowWizardStepSetChangedEvent event) {
                                                //Do nothing
                                            }

                                            @Override
                                            public void stepCompleted(
                                                    net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardStepCompletionEvent event) {
                                                //Do nothing
                                            }

                                            @Override
                                            public void wizardCompleted(
                                                    net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardCompletedEvent event) {
                                                //Process the selections
                                                w.getSteps().forEach(s -> {
                                                    SelectRequirementVersionStep step
                                                            = (SelectRequirementVersionStep) s;
                                                    history.put(step.getRequirement(),
                                                            step.getHistory());
                                                });
                                                sw.close();
                                            }

                                            @Override
                                            public void wizardCancelled(
                                                    FlowWizardCancelledEvent event) {
                                                sw.close();
                                            }
                                        });
                                        sw.add(w);
                                        sw.open();
                                    });
                            prompt.setCancelable(true);
                            prompt.setCancelButton(TRANSLATOR.translate("general.cancel"),
                                    (e) -> {
                                        //Nothing to do
                                    });
                            prompt.open();
                        }
                    }
                    if (!history.isEmpty()) {
                        try {
                            if (conclusion.getValue() != null) {
                                tces.setConclusion(conclusion.getValue());
                            }
                            if (scope.getValue() != null) {
                                tces.setScope(scope.getValue().toString());
                            }
                            tces.setName(name.getValue().toString());
                            tces.write2DB();
                            if (ps != null) {
                                //Process the list
                                ps.getTestProjects(true).forEach(tp -> {
                                    tces.addTestProject(tp);
                                });
                                //Now look thru the ExecutionSteps and assign the right version.
                                tces.getExecutionStepList().forEach(es -> {
                                    try {
                                        ExecutionStepServer ess = new ExecutionStepServer(es);
                                        es.getStep().getRequirementList().forEach(r -> {
                                            ess.getHistoryList().add(history.get(r));
                                        });
                                        ess.write2DB();
                                    } catch (VMException ex) {
                                        LOG.log(Level.SEVERE, null, ex);
                                    }
                                });
                            }
                            tces.write2DB();
                            tces.update(tce, tces.getEntity());
                            ((ValidationManagerUI) getUI().orElse(null))
                                    .updateProjectList();
                            ((ValidationManagerUI) getUI().orElse(null))
                                    .updateScreen();
                            ((ValidationManagerUI) getUI().orElse(null))
                                    .displayObject(tce);
                        } catch (Exception ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.add(save, cancel);
                layout.add(name, scope, conclusion, hl);
            } else {
                TestCaseExecutionServer tces = new TestCaseExecutionServer(tce);
                //Editing existing one
                Button update = new Button(TRANSLATOR.translate("general.update"));
                update.addClickListener((event) -> {
                    tces.setConclusion(conclusion.getValue());
                    tces.setScope(scope.getValue().toString());
                    tces.setName(name.getValue().toString());
                    try {
                        ((ValidationManagerUI) getUI().orElse(null))
                                .handleVersioning(tces, () -> {
                                    try {
                                        tces.write2DB();
                                        tces.update(tce, tces.getEntity());
                                        ((ValidationManagerUI) getUI().orElse(null))
                                                .displayObject(tce);
                                    } catch (NonexistentEntityException ex) {
                                        LOG.log(Level.SEVERE, null, ex);
                                        Notification.show(TRANSLATOR
                                                .translate("general.error.record.update"));
                                    } catch (Exception ex) {
                                        LOG.log(Level.SEVERE, null, ex);
                                        Notification.show(TRANSLATOR
                                                .translate("general.error.record.update"));
                                    }
                                });
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.add(update, cancel);
                layout.add(name, scope, conclusion, hl);
            }
        } else {
            layout.add(name, scope, conclusion, cancel);
        }
        add(layout);
    }
}
