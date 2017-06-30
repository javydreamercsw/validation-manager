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
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import static com.validation.manager.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.DataEntry;
import com.validation.manager.core.db.DataEntryType;
import com.validation.manager.core.db.controller.DataEntryTypeJpaController;
import com.validation.manager.core.server.core.DataEntryServer;
import java.util.List;

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
            ComboBox type = new ComboBox(TRANSLATOR.translate("general.type"));
            type.setNewItemsAllowed(false);
            type.setTextInputAllowed(false);
            type.setNewItemsAllowed(false);
            BeanItemContainer<DataEntryType> container
                    = new BeanItemContainer<>(DataEntryType.class,
                            new DataEntryTypeJpaController(DataBaseManager
                                    .getEntityManagerFactory())
                                    .findDataEntryTypeEntities());
            type.setContainerDataSource(container);
            type.getItemIds().forEach(id -> {
                DataEntryType temp = ((DataEntryType) id);
                type.setItemCaption(id,
                        TRANSLATOR.translate(temp.getTypeName()));
            });
            DataEntryPropertyComponent properties
                    = new DataEntryPropertyComponent(edit);
            if (edit) {
                type.addValueChangeListener(listener -> {
                    de.setDataEntryType((DataEntryType) type.getValue());
                    de.getDataEntryPropertyList().clear();
                    de.getDataEntryPropertyList()
                            .addAll(DataEntryServer
                                    .getDefaultProperties(de
                                            .getDataEntryType()));
                });
            }
            binder.bind(type, "dataEntryType");
            layout.addComponent(type);
            binder.bind(properties, "dataEntryPropertyList");
            layout.addComponent(properties);
            binder.setReadOnly(!edit);
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
