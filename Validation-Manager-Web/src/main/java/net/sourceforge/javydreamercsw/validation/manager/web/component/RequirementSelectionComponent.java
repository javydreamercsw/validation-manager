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

import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.tool.Tool;
import java.util.Collections;
import java.util.List;
import org.openide.util.Lookup;

/**
 * Requirement picker. The v8 TwinColSelect has no Flow port; a
 * {@link MultiSelectComboBox} keeps the multi-select contract.
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class RequirementSelectionComponent
        extends MultiSelectComboBox<Requirement> {

    private static final InternationalizationProvider TRANSLATOR
            = Lookup.getDefault().lookup(InternationalizationProvider.class);
    private final Project p;

    public RequirementSelectionComponent(Project p) {
        this.p = p;
        setLabel(TRANSLATOR.translate("linked.requirement"));
        init();
    }

    public RequirementSelectionComponent(String caption, Project p) {
        super(caption);
        this.p = p;
        init();
    }

    private void init() {
        List<Requirement> reqs = Tool.extractRequirements(p);
        Collections.sort(reqs, (Requirement o1, Requirement o2)
                -> o1.getUniqueId().compareTo(o2.getUniqueId()));
        setItems(reqs);
        setItemLabelGenerator(Requirement::getUniqueId);
        setLabel(TRANSLATOR.translate("linked.requirement"));
    }
}
