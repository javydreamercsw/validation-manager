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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import net.sourceforge.javydreamercsw.validation.manager.web.core.ContentProvider;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Template;
import com.validation.manager.core.db.controller.TemplateJpaController;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.FlowWizard;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.FlowWizardStep;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
class ProjectTemplateStep implements FlowWizardStep {

    private ComboBox<Template> templates;
    private final ProjectCreationWizard wizard;

    public ProjectTemplateStep(ProjectCreationWizard wizard) {
        this.wizard = wizard;
        this.templates
                = new ComboBox<>(ContentProvider.TRANSLATOR
                        .translate("template.tab.list.name"));
        this.templates.setRequiredIndicatorVisible(true);
    }

    @Override
    public String getCaption() {
        return ContentProvider.TRANSLATOR.translate("template.select");
    }

    @Override
    public Component getContent() {
        VerticalLayout vl = new VerticalLayout();
        getTemplates().setItems(new ArrayList<>());
        List<Template> templateList = new ArrayList<>();
        new TemplateJpaController(DataBaseManager
                .getEntityManagerFactory())
                .findTemplateEntities().forEach(t -> {
                    if (t.getProjectTypeId().getTypeName().equals(wizard.getType())
                            || t.getProjectTypeId().getTypeName().equals("general.mixed")) {
                        templateList.add(t);
                    }
                });
        getTemplates().setItems(templateList);
        getTemplates().setItemLabelGenerator(t -> ContentProvider.TRANSLATOR
                .translate(t.getTemplateName()));
        getTemplates().addValueChangeListener(event -> {
            Template t = getTemplates().getValue();
            if (t != null) {
                switch (t.getId()) {
                    case 1:
                        //GAMP 5
                        wizard.getWizard().addStep(new GAMPStep(wizard));
                        break;
                    default:
                    //Do nothing
                }
                wizard.getWizard().addStep(new ProjectDetailsStep(wizard));
            } else {
                //Remove the steps added on the earlier selection (GAMP and
                //project details). The active step (this one) and anything
                //before it can't be removed, so only drop what follows it.
                FlowWizard fw = this.wizard.getWizard();
                List<FlowWizardStep> steps = fw.getSteps();
                for (int i = steps.size() - 1; i > 1; i--) {
                    fw.removeStep(steps.get(i));
                }
            }
        });
        vl.add(getTemplates());
        return vl;
    }

    @Override
    public boolean onAdvance() {
        Template t = getTemplates().getValue();
        if (t != null) {
            wizard.setTemplate(t);
        }
        return t != null;
    }

    @Override
    public boolean onBack() {
        return true;
    }

    /**
     * @return the templates
     */
    public ComboBox<Template> getTemplates() {
        return templates;
    }

    /**
     * @param templates the templates to set
     */
    public void setTemplates(ComboBox<Template> templates) {
        this.templates = templates;
    }
}
