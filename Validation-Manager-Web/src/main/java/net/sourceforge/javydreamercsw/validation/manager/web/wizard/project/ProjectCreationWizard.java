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
package net.sourceforge.javydreamercsw.validation.manager.web.wizard.project;

import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.UI;
import static com.validation.manager.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.db.Template;
import com.validation.manager.core.server.core.ProjectServer;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.component.VMWindow;
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
public final class ProjectCreationWizard extends VMWindow {

    private Template template;
    private String type;
    private String category;
    private ProjectTemplateManager process;
    private final Wizard wizard = new Wizard();
    private static final Logger LOG
            = Logger.getLogger(ProjectCreationWizard.class.getSimpleName());
    private ProjectServer ps;

    public ProjectCreationWizard(ProjectServer p) {
        this.ps = p;
        wizard.addStep(new ProjectTypeStep(this));
        wizard.addStep(new ProjectTemplateStep(this));
        wizard.addListener(new WizardProgressListener() {
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
                //Create the structure from template
                process.run();
                if (UI.getCurrent() != null) {//For unit tests
                    UI.getCurrent().removeWindow(ProjectCreationWizard.this);
                }
            }

            @Override
            public void wizardCancelled(WizardCancelledEvent event) {
                if (UI.getCurrent() != null) {//For unit tests
                    UI.getCurrent().removeWindow(ProjectCreationWizard.this);
                }
            }
        });
        setContent(wizard);
        setWidth(50, Unit.PERCENTAGE);
        setHeight(50, Unit.PERCENTAGE);
    }

    /**
     * @return the template
     */
    public Template getTemplate() {
        return template;
    }

    /**
     * @param template the template to set
     */
    public void setTemplate(Template template) {
        this.template = template;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return the wizard
     */
    public Wizard getWizard() {
        return wizard;
    }

    /**
     * @return the process
     */
    public ProjectTemplateManager getProcess() {
        return process;
    }

    /**
     * @param process the process to set
     */
    public void setProcess(ProjectTemplateManager process) {
        this.process = process;
    }

    /**
     * @return the p
     */
    public ProjectServer getProject() {
        return ps;
    }

    /**
     * @param ps the ps to set
     */
    public void setProject(ProjectServer ps) {
        this.ps = ps;
    }

    public void translateSelect(AbstractSelect s) {
        for (Object o : s.getItemIds()) {
            String id = ((String) o);
            s.setItemCaption(id, (id.startsWith("template")
                    ? (id.substring(id.length() - 1) + "-") : "")
                    + TRANSLATOR.translate((String) id));
        }
    }
}
