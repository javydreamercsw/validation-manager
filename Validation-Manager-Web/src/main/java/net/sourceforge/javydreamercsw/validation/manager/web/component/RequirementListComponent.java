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
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;
import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import com.validation.manager.core.db.Requirement;
import java.util.List;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class RequirementListComponent extends Grid {

    private final List<Requirement> requirementList;
    private static final InternationalizationProvider TRANSLATOR
            = Lookup.getDefault().lookup(InternationalizationProvider.class);

    public RequirementListComponent(List<Requirement> requirementList) {
        this.requirementList = requirementList;
        setCaption(TRANSLATOR.translate("related.requirements"));
        init();
    }

    public RequirementListComponent(String caption,
            List<Requirement> requirementList) {
        super(caption);
        this.requirementList = requirementList;
        init();
    }

    private void init() {
        BeanItemContainer<Requirement> children
                = new BeanItemContainer<>(Requirement.class);
        children.addAll(requirementList);
        setContainerDataSource(children);
        setColumns("uniqueId");
        Grid.Column uniqueId = getColumn("uniqueId");
        uniqueId.setHeaderCaption(TRANSLATOR.translate("unique.id"));
        setHeightMode(HeightMode.ROW);
        setHeightByRows(children.size() > 5 ? 5 : children.size());
        setSizeFull();
        children.sort(new Object[]{"uniqueId"}, new boolean[]{true});
    }
}
