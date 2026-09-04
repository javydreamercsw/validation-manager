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
import static net.sourceforge.javydreamercsw.validation.manager.web.core.ContentProvider.TRANSLATOR;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.FlowWizardStep;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ProjectTypeStep implements FlowWizardStep {

    private final ComboBox<String> type;
    private final ProjectCreationWizard wizard;

    public ProjectTypeStep(ProjectCreationWizard wizard) {
        type = new ComboBox<>(TRANSLATOR.translate("general.type"));
        type.setItems("general.software", "general.hardware");
        type.setAllowCustomValue(false);
        type.setWidthFull();
        this.wizard = wizard;
    }

    @Override
    public String getCaption() {
        return TRANSLATOR.translate("general.type");
    }

    @Override
    public Component getContent() {
        VerticalLayout vl = new VerticalLayout();
        wizard.translateSelect(getType());
        vl.add(getType());
        return vl;
    }

    @Override
    public boolean onAdvance() {
        if (getType().getValue() != null) {
            wizard.setType(getType().getValue());
        }
        return getType().getValue() != null;
    }

    @Override
    public boolean onBack() {
        return false;
    }

    /**
     * @return the type
     */
    protected ComboBox<String> getType() {
        return type;
    }
}
