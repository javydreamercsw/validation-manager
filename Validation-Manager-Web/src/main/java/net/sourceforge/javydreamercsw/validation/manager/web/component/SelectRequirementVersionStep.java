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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.Requirement;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.FlowWizardStep;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class SelectRequirementVersionStep implements FlowWizardStep {

    private History h;
    private final Requirement r;
    private final ComboBox<History> history = new ComboBox<>(Lookup.getDefault()
            .lookup(InternationalizationProvider.class)
            .translate("general.history"));

    public SelectRequirementVersionStep(Requirement r) {
        this.r = r;
    }

    @Override
    public String getCaption() {
        return r.getUniqueId();
    }

    @Override
    public Component getContent() {
        history.setItems(getRequirement().getHistoryList());
        history.setItemLabelGenerator(temp -> {
            String version = temp.getMajorVersion() + "."
                    + temp.getMidVersion() + "." + temp.getMinorVersion();
            return version;
        });
        if (r.getHistoryList().size() == 1) {
            //Only one, pre-select it.
            history.setValue(r.getHistoryList().get(0));
        }
        return history;
    }

    @Override
    public boolean onAdvance() {
        if (history.getValue() != null) {
            h = history.getValue();
            return true;
        }
        return false;
    }

    @Override
    public boolean onBack() {
        return true;
    }

    /**
     * @return the h
     */
    public History getHistory() {
        return h;
    }

    /**
     * @return the r
     */
    public Requirement getRequirement() {
        return r;
    }
}
