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

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.Requirement;
import org.openide.util.Lookup;
import org.vaadin.teemu.wizards.WizardStep;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class SelectRequirementVersionStep implements WizardStep {

    private History h;
    private final Requirement r;
    private final ComboBox history = new ComboBox(Lookup.getDefault()
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
        BeanItemContainer<History> historyContainer
                = new BeanItemContainer<>(History.class,
                        getRequirement().getHistoryList());
        history.setContainerDataSource(historyContainer);
        history.getItemIds().forEach(id -> {
            History temp = ((History) id);
            String version = temp.getMajorVersion() + "."
                    + temp.getMidVersion() + "." + temp.getMinorVersion();
            history.setItemCaption(id, version);
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
            h = (History) history.getValue();
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
