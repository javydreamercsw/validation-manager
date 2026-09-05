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

import com.vaadin.flow.component.grid.Grid;
import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import com.validation.manager.core.db.Requirement;
import java.util.List;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class RequirementListComponent extends Grid<Requirement> {

    private final List<Requirement> requirementList;
    private static final InternationalizationProvider TRANSLATOR
            = Lookup.getDefault().lookup(InternationalizationProvider.class);

    public RequirementListComponent(List<Requirement> requirementList) {
        this.requirementList = requirementList;
        setHeaderRow(TRANSLATOR.translate("related.requirements"));
        init();
    }

    public RequirementListComponent(String caption,
            List<Requirement> requirementList) {
        super();
        this.requirementList = requirementList;
        setHeaderRow(caption);
        init();
    }

    private void setHeaderRow(String caption) {
        // v8 Grid caption: render as an attached header element.
        getElement().insertChild(0,
                com.vaadin.flow.dom.ElementFactory.createDiv(caption));
    }

    private void init() {
        setItems(requirementList);
        Grid.Column<Requirement> uniqueId
                = addColumn(Requirement::getUniqueId)
                        .setKey("uniqueId")
                        .setHeader(TRANSLATOR.translate("unique.id"));
        setHeight(requirementList.size() > 5 ? "160px"
                : (requirementList.size() * 40) + "px");
        setSizeFull();
        com.vaadin.flow.component.grid.GridSortOrderBuilder<Requirement> builder
                = new com.vaadin.flow.component.grid.GridSortOrderBuilder<>();
        builder.thenAsc(uniqueId);
        sort(builder.build());
    }
}
