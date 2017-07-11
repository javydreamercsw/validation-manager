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

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import static com.validation.manager.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.db.DataEntry;
import com.validation.manager.core.db.DataEntryType;
import com.validation.manager.core.server.core.DataEntryServer;
import com.validation.manager.core.server.core.DataEntryTypeServer;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class DataEntryComponent extends CustomField<List<DataEntry>> {

    private final boolean edit;

    public DataEntryComponent(boolean edit) {
        setCaption(TRANSLATOR.translate("general.fields"));
        this.edit = edit;
    }

    @Override
    protected Component initContent() {
        Panel p = new Panel();
        FormLayout layout = new FormLayout();
        p.setContent(layout);
        getInternalValue().forEach(de -> {
            BeanFieldGroup binder = new BeanFieldGroup(de.getClass());
            binder.setItemDataSource(de);
            TextField name = new TextField(TRANSLATOR.translate("general.name"));
            binder.bind(name, "entryName");
            name.setConverter(new TranslationConverter());
            layout.addComponent(name);
            TextField type = new TextField(TRANSLATOR.translate("general.type"));
            type.setConverter(new Converter<String, DataEntryType>() {

                @Override
                public DataEntryType convertToModel(String value,
                        Class<? extends DataEntryType> targetType,
                        Locale locale) throws Converter.ConversionException {
                    for (DataEntryType det : DataEntryTypeServer.getTypes()) {
                        if (TRANSLATOR.translate(det.getTypeName()).equals(value)) {
                            return det;
                        }
                    }
                    return null;
                }

                @Override
                public String convertToPresentation(DataEntryType value,
                        Class<? extends String> targetType, Locale locale)
                        throws Converter.ConversionException {
                    return TRANSLATOR.translate(value.getTypeName());
                }

                @Override
                public Class<DataEntryType> getModelType() {
                    return DataEntryType.class;
                }

                @Override
                public Class<String> getPresentationType() {
                    return String.class;
                }
            });
            DataEntryPropertyComponent properties
                    = new DataEntryPropertyComponent(edit);
            binder.bind(type, "dataEntryType");
            layout.addComponent(type);
            binder.bind(properties, "dataEntryPropertyList");
            layout.addComponent(properties);
            binder.setReadOnly(!edit);
            type.setReadOnly(true);
        });
        return p;
    }

    @Override
    public Class<? extends List<DataEntry>> getType() {
        Class clazz = List.class;
        return (Class<? extends List<DataEntry>>) clazz;
    }

    @Override
    public void commit() throws SourceException, Validator.InvalidValueException {
        getInternalValue().forEach(de -> {
            try {
                new DataEntryServer(de).write2DB();
            } catch (Exception ex) {
                throw new Validator.InvalidValueException(ex
                        .getLocalizedMessage());
            }
        });
    }
}
