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
public final class HistoryTable extends Grid<History> {

    public HistoryTable(String title,
            List<History> historyItems, String sortByField,
            boolean showVersionFields,
            String... fields) {
        super(title);
        setItems(historyItems);
        //Generated properties become plain columns computed from the item.
        for (String field : fields) {
            addColumn(this.<String>fieldValue(field))
                    .setId(field);
        }
        if (showVersionFields) {
            addColumn(history -> history.getMajorVersion() + "."
                    + history.getMidVersion() + "." + history.getMinorVersion())
                    .setId("version");
            addColumn(history -> history.getModifierId().getFirstName() + " "
                    + history.getModifierId().getLastName())
                    .setId("modifier");
            addColumn(history -> history.getModificationTime().toString())
                    .setId("modificationDate");
            addColumn(history -> history.getReason() == null ? ""
                    : TRANSLATOR.translate(history.getReason()))
                    .setId("modificationReason");
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
        setColumns(fieldList.toArray(new String[0]));
        if (showVersionFields) {
            getColumn("version").setCaption(TRANSLATOR.translate("general.version"));
            getColumn("modifier").setCaption(TRANSLATOR.translate("general.modifier"));
            getColumn("modificationDate").setCaption(TRANSLATOR
                    .translate("modification.date"));
            getColumn("modificationReason").setCaption(TRANSLATOR
                    .translate("general.reason"));
        }
        if (sortByField != null && !sortByField.trim().isEmpty()) {
            //Sort on the underlying history item property
            if ("uniqueId".equals(sortByField)) {
                historyItems.sort((h1, h2) -> fieldValue(h1, "uniqueId")
                        .compareTo(fieldValue(h2, "uniqueId")));
                setItems(historyItems);
            }
        }
        if (!historyItems.isEmpty()) {
            setHeightByRows(historyItems.size() > 5 ? 5 : historyItems.size());
        }
        setSizeFull();
    }

    private String fieldValue(History v, String field) {
        String result = "";
        for (HistoryField hf : v.getHistoryFieldList()) {
            if (hf.getFieldName().equals(field)) {
                result = hf.getFieldValue();
                break;
            }
        }
        return result;
    }

    private com.vaadin.data.ValueProvider<History, String> fieldValue(
            String field) {
        return (History v) -> fieldValue(v, field);
    }
}
