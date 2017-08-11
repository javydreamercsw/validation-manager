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

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.validation.manager.core.ContentProvider;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Template;
import com.validation.manager.core.db.controller.TemplateJpaController;
import java.util.ArrayList;
import java.util.List;
import org.vaadin.teemu.wizards.WizardStep;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
class ProjectTemplateStep implements WizardStep {

    private ComboBox templates;
    private final ProjectCreationWizard wizard;

    public ProjectTemplateStep(ProjectCreationWizard wizard) {
        this.wizard = wizard;
        this.templates
                = new ComboBox(ContentProvider.TRANSLATOR
                        .translate("template.tab.list.name"));
        this.templates.setRequired(true);
    }

    @Override
    public String getCaption() {
        return ContentProvider.TRANSLATOR.translate("template.select");
    }

    @Override
    public Component getContent() {
        VerticalLayout vl = new VerticalLayout();
        getTemplates().removeAllItems();
        List<Template> templateList = new ArrayList<>();
        new TemplateJpaController(DataBaseManager
                .getEntityManagerFactory())
                .findTemplateEntities().forEach(t -> {
                    if (t.getProjectTypeId().getTypeName().equals(wizard.getType())
                            || t.getProjectTypeId().getTypeName().equals("general.mixed")) {
                        templateList.add(t);
                    }
                });
        BeanItemContainer<Template> container
                = new BeanItemContainer<>(Template.class, templateList);
        getTemplates().setContainerDataSource(container);
        getTemplates().getItemIds().forEach(id -> {
            Template temp = (Template) id;
            getTemplates().setItemCaption(id, ContentProvider.TRANSLATOR
                    .translate(temp.getTemplateName()));
        });
        getTemplates().addValueChangeListener(event -> {
            Template t = (Template) getTemplates().getValue();
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
                //Remove added steps
                List<WizardStep> steps = wizard.getWizard().getSteps();
                for (int i = 1; i < steps.size(); i++) {
                    wizard.getWizard().removeStep(steps.get(i));
                }
            }
        });
        vl.addComponent(getTemplates());
        return vl;
    }

    @Override
    public boolean onAdvance() {
        Template t = (Template) getTemplates().getValue();
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
    public ComboBox getTemplates() {
        return templates;
    }

    /**
     * @param templates the templates to set
     */
    public void setTemplates(ComboBox templates) {
        this.templates = templates;
    }
}
