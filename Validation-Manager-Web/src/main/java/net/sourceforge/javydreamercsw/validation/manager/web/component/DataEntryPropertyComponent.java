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

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Component;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import static com.validation.manager.core.ContentProvider.TRANSLATOR;
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

    public DataEntryPropertyComponent(boolean edit) {
        setCaption(TRANSLATOR.translate("general.properties"));
        this.edit = edit;
    }

    @Override
    protected Component initContent() {
        Panel p = new Panel();
        FormLayout l = new FormLayout();
        getValue().forEach(prop -> {
            if (!prop.getPropertyName().equals("property.expected.result")) {
                HorizontalLayout hl = new HorizontalLayout();
                TextField tf = new TextField(
                        TRANSLATOR.translate(prop.getPropertyName()),
                        prop.getPropertyValue());
                hl.addComponent(tf);
                if (edit) {
                    //Add button for deleting this property.
                    Button delete = new Button();
                    delete.setIcon(VaadinIcons.MINUS);
                    delete.addClickListener(listener -> {
                        getValue().remove(prop);
                        l.removeComponent(hl);
                    });
                    hl.addComponent(delete);
                }
                l.addComponent(hl);
            }
        });
        p.setContent(l);
        return p;
    }

    @Override
    public List<DataEntryProperty> getValue() {
        return value;
    }

    @Override
    protected void doSetValue(List<DataEntryProperty> value) {
        this.value = value;
    }
}
