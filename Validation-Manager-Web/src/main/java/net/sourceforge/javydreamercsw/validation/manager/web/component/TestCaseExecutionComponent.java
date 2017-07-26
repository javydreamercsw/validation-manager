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

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import static com.validation.manager.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.VMException;
import com.validation.manager.core.VMUI;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.TestCaseExecution;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.ExecutionStepServer;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.TestCaseExecutionServer;
import com.validation.manager.core.tool.Tool;
import de.steinwedel.messagebox.ButtonOption;
import de.steinwedel.messagebox.MessageBox;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class TestCaseExecutionComponent extends Panel {

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
        setCaption(TRANSLATOR.translate("test.case.execution.detail"));
        init();
    }

    public TestCaseExecutionComponent(TestCaseExecution tce, ProjectServer ps,
            boolean edit, String caption) {
        super(caption);
        this.tce = tce;
        this.edit = edit;
        this.ps = ps;
        init();
    }

    private void init() {
        FormLayout layout = new FormLayout();
        setContent(layout);
        addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(tce.getClass());
        binder.setItemDataSource(tce);
        Field<?> name = binder.buildAndBind(TRANSLATOR.translate("general.name"),
                "name");
        name.setRequired(true);
        name.setRequiredError(TRANSLATOR.translate("missing.name.message"));
        layout.addComponent(name);
        Field<?> scope = binder.buildAndBind(TRANSLATOR.translate("general.scope"),
                "scope");
        layout.addComponent(scope);
        //TODO: Show when finished
        TextArea conclusion = new TextArea(TRANSLATOR.translate("general.conclusion"));
        binder.bind(conclusion, "conclusion");
        layout.addComponent(conclusion);
        Button cancel = new Button(TRANSLATOR.translate("general.cancel"));
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.discard();
            if (tce.getId() == null) {
                ((VMUI) UI.getCurrent()).displayObject(((VMUI) UI.getCurrent())
                        .getSelectdValue());
            } else {
                ((VMUI) UI.getCurrent()).displayObject(tce, false);
            }
        });
        if (edit) {
            if (tce.getId() == null) {
                TestCaseExecutionServer tces = new TestCaseExecutionServer();
                //Creating a new one
                Button save = new Button(TRANSLATOR.translate("general.save"));
                save.addClickListener((Button.ClickEvent event) -> {
                    if (name.getValue() == null) {
                        Notification.show(name.getRequiredError(),
                                Notification.Type.ERROR_MESSAGE);
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
                            MessageBox mb = MessageBox.create();
                            mb.asModal(true)
                                    .withCaption(TRANSLATOR.translate("missing.baseline.requirement.title"))
                                    .withMessage(TRANSLATOR.translate("missing.baseline.requirement.message"))
                                    .withButtonAlignment(Alignment.MIDDLE_CENTER)
                                    .withOkButton(() -> {
                                        //Start the wizard
                                        Wizard w = new Wizard();
                                        VMWindow sw = new VMWindow();
                                        w.setDisplayedMaxTitles(3);
                                        toApprove.forEach(r -> {
                                            w.addStep(new SelectRequirementVersionStep(r));
                                        });
                                        w.addListener(new WizardProgressListener() {
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
                                                //Process the selections
                                                w.getSteps().forEach(s -> {
                                                    SelectRequirementVersionStep step
                                                            = (SelectRequirementVersionStep) s;
                                                    history.put(step.getRequirement(),
                                                            step.getHistory());
                                                });
                                                UI.getCurrent().removeWindow(sw);
                                            }

                                            @Override
                                            public void wizardCancelled(WizardCancelledEvent event) {
                                                UI.getCurrent().removeWindow(sw);
                                            }
                                        });
                                        sw.setContent(w);
                                        sw.setSizeFull();
                                        UI.getCurrent().addWindow(sw);
                                    }, ButtonOption.focus(),
                                            ButtonOption.icon(VaadinIcons.CHECK))
                                    .withCancelButton(
                                            ButtonOption.icon(VaadinIcons.CLOSE)
                                    ).getWindow().setIcon(ValidationManagerUI.SMALL_APP_ICON);
                            mb.open();
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
                            ((VMUI) UI.getCurrent()).updateProjectList();
                            ((VMUI) UI.getCurrent()).updateScreen();
                            ((VMUI) UI.getCurrent()).displayObject(tce);
                        } catch (Exception ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(save);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            } else {
                TestCaseExecutionServer tces = new TestCaseExecutionServer(tce);
                //Editing existing one
                Button update = new Button(TRANSLATOR.translate("general.update"));
                update.addClickListener((Button.ClickEvent event) -> {
                    tces.setConclusion(conclusion.getValue());
                    tces.setScope(scope.getValue().toString());
                    tces.setName(name.getValue().toString());
                    try {
                        ((VMUI) UI.getCurrent()).handleVersioning(tces, () -> {
                            try {
                                tces.write2DB();
                                tces.update(tce, tces.getEntity());
                                ((VMUI) UI.getCurrent()).displayObject(tce);
                            } catch (NonexistentEntityException ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show(TRANSLATOR.translate("general.error.record.update"),
                                        ex.getLocalizedMessage(),
                                        Notification.Type.ERROR_MESSAGE);
                            } catch (Exception ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show(TRANSLATOR.translate("general.error.record.update"),
                                        ex.getLocalizedMessage(),
                                        Notification.Type.ERROR_MESSAGE);
                            }
                        });
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(update);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            }
        }
        binder.setReadOnly(!edit);
        binder.bindMemberFields(this);
        layout.setSizeFull();
        setSizeFull();
    }
}
