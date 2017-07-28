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

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.IMainContentProvider;
import com.validation.manager.core.VMException;
import com.validation.manager.core.VMUI;
import com.validation.manager.core.db.Template;
import com.validation.manager.core.db.controller.TemplateJpaController;
import com.validation.manager.core.server.core.TemplateServer;
import de.steinwedel.messagebox.ButtonOption;
import de.steinwedel.messagebox.MessageBox;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.admin.AdminProvider;
import net.sourceforge.javydreamercsw.validation.manager.web.component.TemplateComponent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.VMWindow;
import org.openide.util.lookup.ServiceProvider;
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
@ServiceProvider(service = IMainContentProvider.class, position = 6)
public class TemplateScreenProvider extends AdminProvider {

    private final ListSelect templates;
    private static final Logger LOG
            = Logger.getLogger(TemplateScreenProvider.class.getSimpleName());
    private final HorizontalSplitPanel hs = new HorizontalSplitPanel();

    @Override
    public String getComponentCaption() {
        return "template.tab.name";
    }

    public TemplateScreenProvider() {
        templates = new ListSelect(TRANSLATOR.translate("template.tab.list.name"));
    }

    @Override
    public Component getContent() {
        Panel p = new Panel();
        hs.setSplitPosition(30, Sizeable.Unit.PERCENTAGE);
        hs.setFirstComponent(getLeftComponent());
        hs.setSecondComponent(getRightComponent());
        hs.setSizeFull();
        p.setContent(hs);
        p.setId(getComponentCaption());
        return p;
    }

    private Component getLeftComponent() {
        Panel p = new Panel();
        VerticalLayout layout = new VerticalLayout();
        templates.setNullSelectionAllowed(true);
        templates.setWidth(100, Sizeable.Unit.PERCENTAGE);
        BeanItemContainer<Template> container
                = new BeanItemContainer<>(Template.class,
                        new TemplateJpaController(DataBaseManager
                                .getEntityManagerFactory())
                                .findTemplateEntities());
        templates.setContainerDataSource(container);
        templates.getItemIds().forEach(id -> {
            Template temp = ((Template) id);
            templates.setItemCaption(id,
                    TRANSLATOR.translate(temp.getTemplateName()));
        });
        templates.addValueChangeListener(event -> {
            hs.setSecondComponent(getRightComponent());
        });
        templates.setNullSelectionAllowed(false);
        templates.setWidth(100, Sizeable.Unit.PERCENTAGE);
        layout.addComponent(templates);
        HorizontalLayout hl = new HorizontalLayout();
        Button create = new Button(TRANSLATOR.translate("general.add"));
        create.addClickListener(listener -> {
            displayTemplateCreateWizard();
        });
        hl.addComponent(create);
        Button copy = new Button(TRANSLATOR.translate("general.copy"));
        copy.addClickListener(listener -> {
            displayTemplateCopyWizard();
        });
        hl.addComponent(copy);
        Button delete = new Button(TRANSLATOR.translate("general.delete"));
        delete.addClickListener(listener -> {
            displayTemplateDeleteWizard();
        });
        hl.addComponent(delete);
        templates.addValueChangeListener(listener -> {
            if (templates.getValue() != null) {
                Template t = (Template) templates.getValue();
                delete.setEnabled(t.getId() >= 1_000);
                copy.setEnabled(t.getTemplateNodeList().size() > 0);
            }
        });
        layout.addComponent(hl);
        layout.setSizeFull();
        p.setContent(layout);
        p.setSizeFull();
        return p;
    }

    private Component getRightComponent() {
        Template t = (Template) templates.getValue();
        return t == null ? new Panel()
                : new TemplateComponent(t, t.getId() >= 1000);
    }

    private void displayTemplateCopyWizard() {
        Wizard w = new Wizard();
        Window cw = new VMWindow();
        TemplateComponent tc = new TemplateComponent(new Template(), true);
        w.addStep(new WizardStep() {

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
                            .translate("general.error.record.creation"),
                            ex.getLocalizedMessage(),
                            Notification.Type.ERROR_MESSAGE);
                }
                return false;
            }

            @Override
            public boolean onBack() {
                return false;
            }
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
                ((VMUI) UI.getCurrent()).updateScreen();
                ((VMUI) UI.getCurrent()).showTab(getComponentCaption());
                UI.getCurrent().removeWindow(cw);
            }

            @Override
            public void wizardCancelled(WizardCancelledEvent event) {
                UI.getCurrent().removeWindow(cw);
            }
        });
        cw.setContent(w);
        cw.setSizeFull();
        UI.getCurrent().addWindow(cw);
    }

    private void displayTemplateCreateWizard() {
        Wizard w = new Wizard();
        Window cw = new VMWindow();
        TemplateComponent tc = new TemplateComponent(new Template(), true);
        w.addStep(new WizardStep() {
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
                            .translate("general.error.record.creation"),
                            ex.getLocalizedMessage(),
                            Notification.Type.ERROR_MESSAGE);
                }
                return false;
            }

            @Override
            public boolean onBack() {
                return false;
            }
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
                ((VMUI) UI.getCurrent()).updateScreen();
                ((VMUI) UI.getCurrent()).showTab(getComponentCaption());
                UI.getCurrent().removeWindow(cw);
            }

            @Override
            public void wizardCancelled(WizardCancelledEvent event) {
                UI.getCurrent().removeWindow(cw);
            }
        });
        cw.setContent(w);
        cw.setSizeFull();
        UI.getCurrent().addWindow(cw);
    }

    private void displayTemplateDeleteWizard() {
        MessageBox prompt = MessageBox.createQuestion()
                .withCaption(TRANSLATOR.translate("template.delete.title"))
                .withMessage(TRANSLATOR.translate("template.delete.message"))
                .withYesButton(() -> {
                    if (templates.getValue() != null) {
                        try {
                            //Delete nodes
                            TemplateServer t
                                    = new TemplateServer(((Template) templates
                                            .getValue()));
                            t.delete();
                            ((VMUI) UI.getCurrent()).updateScreen();
                            ((VMUI) UI.getCurrent()).showTab(getComponentCaption());
                        } catch (VMException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                    }
                },
                        ButtonOption.focus(),
                        ButtonOption
                                .icon(VaadinIcons.CHECK))
                .withNoButton(ButtonOption
                        .icon(VaadinIcons.CLOSE));
        prompt.getWindow().setIcon(VMUI.SMALL_APP_ICON);
        prompt.open();
    }
}
