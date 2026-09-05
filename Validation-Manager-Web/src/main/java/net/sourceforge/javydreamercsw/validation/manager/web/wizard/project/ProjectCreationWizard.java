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

import com.vaadin.flow.component.combobox.ComboBox;
import static net.sourceforge.javydreamercsw.validation.manager.web.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.db.Template;
import com.validation.manager.core.server.core.ProjectServer;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.component.VMWindow;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.FlowWizard;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardCancelledEvent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardCompletedEvent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardProgressListener;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardStepCompletionEvent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardStepActivationEvent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardStepSetChangedEvent;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class ProjectCreationWizard extends VMWindow {

    private Template template;
    private String type;
    private String category;
    private ProjectTemplateManager process;
    private final FlowWizard wizard = new FlowWizard();
    private static final Logger LOG
            = Logger.getLogger(ProjectCreationWizard.class.getSimpleName());
    private ProjectServer ps;

    public ProjectCreationWizard(ProjectServer p) {
        this.ps = p;
        wizard.addStep(new ProjectTypeStep(this));
        wizard.addStep(new ProjectTemplateStep(this));
        wizard.addListener(new FlowWizardProgressListener() {
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
                //Create the structure from template
                process.run();
                //Flow equivalent of v8 UI.removeWindow(this)
                ProjectCreationWizard.this.close();
            }

            @Override
            public void wizardCancelled(FlowWizardCancelledEvent event) {
                //Flow equivalent of v8 UI.removeWindow(this)
                ProjectCreationWizard.this.close();
            }
        });
        add(wizard);
        setWidth("50%");
        setHeight("50%");
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
    public FlowWizard getWizard() {
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

    public void translateSelect(ComboBox<String> s) {
        s.setItemLabelGenerator(id -> (id.startsWith("template")
                ? (id.substring(id.length() - 1) + "-") : "")
                + TRANSLATOR.translate(id));
    }
}
