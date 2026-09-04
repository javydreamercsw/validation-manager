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
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import static net.sourceforge.javydreamercsw.validation.manager.web.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.db.DataEntryProperty;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class DataEntryPropertyComponent extends
        CustomField<List<DataEntryProperty>> {

    private final boolean edit;
    private List<DataEntryProperty> value;
    private final FormLayout l = new FormLayout();

    public DataEntryPropertyComponent(boolean edit) {
        setLabel(TRANSLATOR.translate("general.properties"));
        this.edit = edit;
        add(new Scroller(l));
    }

    private void buildContent() {
        l.removeAll();
        getValue().forEach(prop -> {
            if (!prop.getPropertyName().equals("property.expected.result")) {
                HorizontalLayout hl = new HorizontalLayout();
                TextField tf = new TextField(
                        TRANSLATOR.translate(prop.getPropertyName()),
                        prop.getPropertyValue());
                hl.add(tf);
                if (edit) {
                    //Add button for deleting this property.
                    Button delete = new Button(new Icon(VaadinIcon.MINUS));
                    delete.addClickListener(listener -> {
                        getValue().remove(prop);
                        l.remove(hl);
                    });
                    hl.add(delete);
                }
                l.add(hl);
            }
        });
    }

    @Override
    public List<DataEntryProperty> getValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(List<DataEntryProperty> value) {
        this.value = value;
        buildContent();
    }

    @Override
    protected List<DataEntryProperty> generateModelValue() {
        return value;
    }
}
