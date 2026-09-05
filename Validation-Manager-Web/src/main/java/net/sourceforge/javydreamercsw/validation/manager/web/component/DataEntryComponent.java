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
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import static net.sourceforge.javydreamercsw.validation.manager.web.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.db.DataEntry;
import com.validation.manager.core.db.DataEntryType;
import com.validation.manager.core.server.core.DataEntryServer;
import com.validation.manager.core.server.core.DataEntryTypeServer;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class DataEntryComponent extends CustomField<List<DataEntry>> {

    private final boolean edit;
    private List<DataEntry> value;
    private final VerticalLayout content = new VerticalLayout();

    public DataEntryComponent(boolean edit) {
        setLabel(TRANSLATOR.translate("general.fields"));
        this.edit = edit;
        add(new Scroller(content));
    }

    @Override
    public List<DataEntry> getValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(List<DataEntry> value) {
        this.value = value;
        buildContent();
    }

    @Override
    protected List<DataEntry> generateModelValue() {
        return value;
    }

    private void buildContent() {
        content.removeAll();
        getValue().forEach(de -> {
            Binder<DataEntry> binder = new Binder<>(DataEntry.class);
            binder.setBean(de);
            TextField name = new TextField(TRANSLATOR.translate("general.name"));
            binder.forField(name)
                    .withConverter(new TranslationConverter())
                    .bind("entryName");
            content.add(name);
            TextField type = new TextField(TRANSLATOR.translate("general.type"));
            binder.forField(type)
                    .withConverter(new Converter<String, DataEntryType>() {

                @Override
                public Result<DataEntryType> convertToModel(String value,
                        ValueContext context) {
                    for (DataEntryType det : DataEntryTypeServer.getTypes()) {
                        if (TRANSLATOR.translate(det.getTypeName())
                                .equals(value)) {
                            return Result.ok(det);
                        }
                    }
                    return Result.error(TRANSLATOR.translate("general.error"));
                }

                @Override
                public String convertToPresentation(DataEntryType value,
                        ValueContext context) {
                    return TRANSLATOR.translate(value.getTypeName());
                }
            })
                    .bind("dataEntryType");
            DataEntryPropertyComponent properties
                    = new DataEntryPropertyComponent(edit);
            binder.bind(properties, "dataEntryPropertyList");
            content.add(properties);
            binder.setReadOnly(!edit);
            type.setReadOnly(true);
        });
    }

    /**
     * Persist any edits made to the entries.
     */
    public void save() {
        getValue().forEach(de -> {
            try {
                new DataEntryServer(de).write2DB();
            } catch (Exception ex) {
                org.openide.util.Exceptions.printStackTrace(ex);
            }
        });
    }
}
