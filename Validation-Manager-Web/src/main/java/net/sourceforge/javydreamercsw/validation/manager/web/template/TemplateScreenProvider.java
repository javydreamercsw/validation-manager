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
package net.sourceforge.javydreamercsw.validation.manager.web.template;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.UI;
import com.validation.manager.core.DataBaseManager;
import net.sourceforge.javydreamercsw.validation.manager.web.core.IMainContentProvider;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.Template;
import com.validation.manager.core.db.controller.TemplateJpaController;
import com.validation.manager.core.server.core.TemplateServer;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.admin.AdminProvider;
import net.sourceforge.javydreamercsw.validation.manager.web.component.TemplateComponent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.VMWindow;
import net.sourceforge.javydreamercsw.validation.manager.web.core.VMUI;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.FlowWizard;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.FlowWizardStep;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardCancelledEvent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardCompletedEvent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardProgressListener;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardStepActivationEvent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardStepCompletionEvent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardStepSetChangedEvent;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = IMainContentProvider.class, position = 6)
public class TemplateScreenProvider extends AdminProvider {

    private final Select<Template> templates;
    private final Button create = new Button(TRANSLATOR.translate("general.add"));
    private final Button copy = new Button(TRANSLATOR.translate("general.copy"));
    private final Button delete = new Button(TRANSLATOR.translate("general.delete"));
    private static final Logger LOG
            = Logger.getLogger(TemplateScreenProvider.class.getSimpleName());
    private final SplitLayout hs = new SplitLayout();

    @Override
    public String getComponentCaption() {
        return "template.tab.name";
    }

    public TemplateScreenProvider() {
        templates = new Select<>();
        templates.setLabel(TRANSLATOR.translate("template.tab.list.name"));
    }

    @Override
    public Component getContent() {
        Scroller p = new Scroller();
        hs.setSplitterPosition(30);
        hs.addToPrimary(getLeftComponent());
        hs.addToSecondary(getRightComponent());
        hs.setSizeFull();
        p.setContent(hs);
        p.setId(getComponentCaption());
        return p;
    }

    private Component getLeftComponent() {
        Scroller p = new Scroller();
        VerticalLayout layout = new VerticalLayout();
        templates.setWidthFull();
        templates.setItems(new TemplateJpaController(DataBaseManager
                .getEntityManagerFactory())
                .findTemplateEntities());
        templates.setItemLabelGenerator(temp
                -> TRANSLATOR.translate(temp.getTemplateName()));
        templates.addValueChangeListener(event -> {
            hs.addToSecondary(getRightComponent());
        });
        templates.addValueChangeListener(listener -> {
            if (templates.getValue() != null) {
                Template t = templates.getValue();
                delete.setEnabled(t.getId() >= 1_000);
                copy.setEnabled(t.getTemplateNodeList().size() > 0);
            }
        });
        layout.add(templates);
        HorizontalLayout hl = new HorizontalLayout();
        create.addClickListener(listener -> {
            displayTemplateCreateWizard();
        });
        hl.add(create);
        copy.addClickListener(listener -> {
            displayTemplateCopyWizard();
        });
        hl.add(copy);
        delete.addClickListener(listener -> {
            displayTemplateDeleteWizard();
        });
        hl.add(delete);
        layout.add(hl);
        layout.setSizeFull();
        p.setContent(layout);
        p.setSizeFull();
        return p;
    }

    private Component getRightComponent() {
        Template t = templates.getValue();
        return t == null ? new VerticalLayout()
                : new TemplateComponent(t, t.getId() >= 1000);
    }

    private void displayTemplateCopyWizard() {
        FlowWizard w = new FlowWizard();
        VMWindow cw = new VMWindow();
        TemplateComponent tc = new TemplateComponent(new Template(), true);
        w.addStep(new FlowWizardStep() {

            @Override
            public String getCaption() {
                return TRANSLATOR.translate("template.copy");
            }

            @Override
            public Component getContent() {
                return tc;
            }

            @Override
            public boolean onAdvance() {
                try {
                    TemplateServer t = new TemplateServer(tc.getTemplate());
                    t.write2DB();
                    return t.getId() > 0;
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                    Notification.show(TRANSLATOR
                            .translate("general.error.record.creation") + ": "
                            + ex.getLocalizedMessage());
                }
                return false;
            }

            @Override
            public boolean onBack() {
                return false;
            }
        });
        w.addListener(new FlowWizardProgressListener() {
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
                ((VMUI) UI.getCurrent()).updateScreen();
                ((VMUI) UI.getCurrent()).showTab(getComponentCaption());
                ((VMUI) UI.getCurrent()).closeDialog(cw);
            }

            @Override
            public void wizardCancelled(FlowWizardCancelledEvent event) {
                ((VMUI) UI.getCurrent()).closeDialog(cw);
            }
        });
        cw.add(w);
        ((VMUI) UI.getCurrent()).openDialog(cw);
    }

    private void displayTemplateCreateWizard() {
        FlowWizard w = new FlowWizard();
        VMWindow cw = new VMWindow();
        TemplateComponent tc = new TemplateComponent(new Template(), true);
        w.addStep(new FlowWizardStep() {
            private final TextField nameField
                    = new TextField(TRANSLATOR.translate("general.name"));

            @Override
            public String getCaption() {
                return TRANSLATOR.translate("add.template");
            }

            @Override
            public Component getContent() {
                return tc;
            }

            @Override
            public boolean onAdvance() {
                try {
                    TemplateServer t = new TemplateServer(tc.getTemplate());
                    t.write2DB();
                    return t.getId() > 0;
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                    Notification.show(TRANSLATOR
                            .translate("general.error.record.creation") + ": "
                            + ex.getLocalizedMessage());
                }
                return false;
            }

            @Override
            public boolean onBack() {
                return false;
            }
        });
        w.addListener(new FlowWizardProgressListener() {
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
                ((VMUI) UI.getCurrent()).updateScreen();
                ((VMUI) UI.getCurrent()).showTab(getComponentCaption());
                ((VMUI) UI.getCurrent()).closeDialog(cw);
            }

            @Override
            public void wizardCancelled(FlowWizardCancelledEvent event) {
                ((VMUI) UI.getCurrent()).closeDialog(cw);
            }
        });
        cw.add(w);
        ((VMUI) UI.getCurrent()).openDialog(cw);
    }

    private void displayTemplateDeleteWizard() {
        ConfirmDialog prompt = new ConfirmDialog(
                TRANSLATOR.translate("template.delete.title"),
                TRANSLATOR.translate("template.delete.message"),
                TRANSLATOR.translate("general.confirm"), confirmEvent -> {
            if (templates.getValue() != null) {
                try {
                    //Delete nodes
                    TemplateServer t
                            = new TemplateServer(templates
                                    .getValue());
                    t.delete();
                    ((VMUI) UI.getCurrent()).updateScreen();
                    ((VMUI) UI.getCurrent()).showTab(getComponentCaption());
                } catch (VMException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        });
        prompt.setConfirmButtonTheme("primary");
        prompt.setRejectable(false);
        prompt.open();
    }
}
