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

import static net.sourceforge.javydreamercsw.validation.manager.web.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.db.DataEntryType;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class DataEntryTypeComponent extends CustomField<List<DataEntryType>> {

    private List<DataEntryType> value;
    private final boolean edit;
    private final FormLayout l = new FormLayout();
    private Binder<DataEntryType> binder;

    public DataEntryTypeComponent(boolean edit) {
        this.edit = edit;
        add(new Scroller(l));
    }

    private void buildContent() {
        l.removeAll();
        binder = new Binder<>(DataEntryType.class);
        if (getValue() != null && !getValue().isEmpty()) {
            binder.setBean(getValue().get(0));
        }
        TextField name = new TextField(TRANSLATOR.translate("general.name"));
        binder.bind(name, "typeName");
        l.add(name);
        TextField desc = new TextField(TRANSLATOR.translate("general.description"));
        binder.bind(desc, "typeDescription");
        l.add(desc);
        binder.setReadOnly(!edit);
    }

    @Override
    public List<DataEntryType> getValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(List<DataEntryType> value) {
        this.value = value;
        buildContent();
    }

    @Override
    protected List<DataEntryType> generateModelValue() {
        return value;
    }
}
