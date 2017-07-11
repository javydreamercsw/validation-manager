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

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;
import static com.validation.manager.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.HistoryField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class HistoryTable extends Grid {

    public HistoryTable(String title,
            List<History> historyItems, String sortByField,
            boolean showVersionFields,
            String... fields) {
        super(title);
        BeanItemContainer<History> histories
                = new BeanItemContainer<>(History.class);
        GeneratedPropertyContainer wrapperCont
                = new GeneratedPropertyContainer(histories);
        histories.addAll(historyItems);
        setContainerDataSource(wrapperCont);
        if (wrapperCont.size() > 0) {
            setHeightMode(HeightMode.ROW);
            setHeightByRows(wrapperCont.size() > 5 ? 5 : wrapperCont.size());
        }
        for (String field : fields) {
            wrapperCont.addGeneratedProperty(field,
                    new PropertyValueGenerator<String>() {

                @Override
                public String getValue(Item item, Object itemId, Object propertyId) {
                    History v = (History) itemId;
                    String result = "";
                    for (HistoryField hf : v.getHistoryFieldList()) {
                        if (hf.getFieldName().equals(field)) {
                            result = hf.getFieldValue();
                            break;
                        }
                    }
                    return result;
                }

                @Override
                public Class<String> getType() {
                    return String.class;
                }
            });
        }
        if (showVersionFields) {
            wrapperCont.addGeneratedProperty("version",
                    new PropertyValueGenerator<String>() {

                @Override
                public String getValue(Item item, Object itemId, Object propertyId) {
                    History v = (History) itemId;
                    return v.getMajorVersion() + "." + v.getMidVersion()
                            + "." + v.getMinorVersion();
                }

                @Override
                public Class<String> getType() {
                    return String.class;
                }
            });
            wrapperCont.addGeneratedProperty("modifier",
                    new PropertyValueGenerator<String>() {

                @Override
                public String getValue(Item item, Object itemId, Object propertyId) {
                    History v = (History) itemId;
                    return v.getModifierId().getFirstName() + " "
                            + v.getModifierId().getLastName();
                }

                @Override
                public Class<String> getType() {
                    return String.class;
                }
            });
            wrapperCont.addGeneratedProperty("modificationDate",
                    new PropertyValueGenerator<String>() {

                @Override
                public String getValue(Item item, Object itemId, Object propertyId) {
                    History v = (History) itemId;
                    return v.getModificationTime().toString();
                }

                @Override
                public Class<String> getType() {
                    return String.class;
                }
            });
            wrapperCont.addGeneratedProperty("modificationReason",
                    new PropertyValueGenerator<String>() {

                @Override
                public String getValue(Item item, Object itemId, Object propertyId) {
                    History v = (History) itemId;
                    return v.getReason() == null ? ""
                            : TRANSLATOR.translate(v.getReason());
                }

                @Override
                public Class<String> getType() {
                    return String.class;
                }
            });
        }
        List<String> fieldList = new ArrayList<>();
        //Add specified fields
        fieldList.addAll(Arrays.asList(fields));
        if (showVersionFields) {
            //Add default fields
            fieldList.add("version");
            fieldList.add("modifier");
            fieldList.add("modificationDate");
            fieldList.add("modificationReason");
        }
        setColumns(fieldList.toArray());
        if (showVersionFields) {
            Grid.Column version = getColumn("version");
            version.setHeaderCaption(TRANSLATOR.translate("general.version"));
            Grid.Column mod = getColumn("modifier");
            mod.setHeaderCaption(TRANSLATOR.translate("general.modifier"));
            Grid.Column modDate = getColumn("modificationDate");
            modDate.setHeaderCaption(TRANSLATOR.translate("modification.date"));
            Grid.Column modReason = getColumn("modificationReason");
            modReason.setHeaderCaption(TRANSLATOR.translate("general.reason"));
        }
        if (sortByField != null && !sortByField.trim().isEmpty()) {
            wrapperCont.sort(new Object[]{sortByField}, new boolean[]{true});
        }
        setSizeFull();
    }
}
