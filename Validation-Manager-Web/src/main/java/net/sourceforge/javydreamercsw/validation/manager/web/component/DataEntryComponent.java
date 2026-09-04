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
import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
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
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class DataEntryComponent extends CustomField<List<DataEntry>> {

    private final boolean edit;
    private List<DataEntry> value;

    public DataEntryComponent(boolean edit) {
        setCaption(TRANSLATOR.translate("general.fields"));
        this.edit = edit;
    }

    @Override
    public List<DataEntry> getValue() {
        return value;
    }

    @Override
    protected void doSetValue(List<DataEntry> value) {
        this.value = value;
    }

    @Override
    protected Component initContent() {
        Panel p = new Panel();
        FormLayout layout = new FormLayout();
        p.setContent(layout);
        getValue().forEach(de -> {
            Binder<DataEntry> binder = new Binder<>(DataEntry.class);
            binder.setBean(de);
            TextField name = new TextField(TRANSLATOR.translate("general.name"));
            binder.forField(name)
                    .withConverter(new TranslationConverter())
                    .bind("entryName");
            layout.addComponent(name);
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
            layout.addComponent(properties);
            binder.setReadOnly(!edit);
            type.setReadOnly(true);
        });
        return p;
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
