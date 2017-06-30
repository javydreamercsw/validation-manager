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

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Panel;
import static com.validation.manager.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.db.DataEntryType;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class DataEntryTypeComponent extends CustomField<List<DataEntryType>> {

    private final boolean edit;

    public DataEntryTypeComponent(boolean edit) {
        this.edit = edit;
    }

    @Override
    protected Component initContent() {
        Panel p = new Panel();
        FormLayout l = new FormLayout();
        p.setContent(l);
        BeanFieldGroup binder = new BeanFieldGroup(getInternalValue().getClass());
        binder.setItemDataSource(getInternalValue());
        l.addComponent(binder.buildAndBind(TRANSLATOR.translate("general.name"),
                "typeName"));
        l.addComponent(binder.buildAndBind(TRANSLATOR.translate("general.description"),
                "typeDescription"));
        binder.setReadOnly(!edit);
        return p;
    }

    @Override
    public Class<? extends List<DataEntryType>> getType() {
        Class clazz = List.class;
        return (Class<? extends List<DataEntryType>>) clazz;
    }
}
