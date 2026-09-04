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

import com.vaadin.data.Binder;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import static com.validation.manager.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.db.DataEntryType;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class DataEntryTypeComponent extends CustomField<List<DataEntryType>> {

    private List<DataEntryType> value;
    private final boolean edit;

    public DataEntryTypeComponent(boolean edit) {
        this.edit = edit;
    }

    @Override
    protected Component initContent() {
        Panel p = new Panel();
        FormLayout l = new FormLayout();
        p.setContent(l);
        Binder<DataEntryType> binder = new Binder<>(DataEntryType.class);
        if (getValue() != null && !getValue().isEmpty()) {
            binder.setBean(getValue().get(0));
        }
        TextField name = new TextField(TRANSLATOR.translate("general.name"));
        binder.bind(name, "typeName");
        l.addComponent(name);
        TextField desc = new TextField(TRANSLATOR.translate("general.description"));
        binder.bind(desc, "typeDescription");
        l.addComponent(desc);
        binder.setReadOnly(!edit);
        return p;
    }

    @Override
    public List<DataEntryType> getValue() {
        return value;
    }

    @Override
    protected void doSetValue(List<DataEntryType> value) {
        this.value = value;
    }
}
