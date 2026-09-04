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
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.HistoryField;
import com.vaadin.flow.component.grid.Grid;
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
        //Column order follows the requested field order (Flow Grid has no
        //setColumns(String...) for non-bean grids; columns are ordered by
        //insertion instead).
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
        setItems(historyItems);
        //Generated properties become plain columns computed from the item.
        for (String field : fieldList) {
            switch (field) {
                case "version":
                    addColumn(history -> history.getMajorVersion() + "."
                            + history.getMidVersion() + "."
                            + history.getMinorVersion())
                            .setKey("version")
                            .setHeader(TRANSLATOR.translate("general.version"));
                    break;
                case "modifier":
                    addColumn(history -> history.getModifierId().getFirstName()
                            + " " + history.getModifierId().getLastName())
                            .setKey("modifier")
                            .setHeader(TRANSLATOR.translate("general.modifier"));
                    break;
                case "modificationDate":
                    addColumn(history -> history.getModificationTime().toString())
                            .setKey("modificationDate")
                            .setHeader(TRANSLATOR
                                    .translate("modification.date"));
                    break;
                case "modificationReason":
                    addColumn(history -> history.getReason() == null ? ""
                            : TRANSLATOR.translate(history.getReason()))
                            .setKey("modificationReason")
                            .setHeader(TRANSLATOR
                                    .translate("general.reason"));
                    break;
                default:
                    addColumn((com.vaadin.flow.function.ValueProvider<History, String>) (History v)
                            -> fieldValue(v, field))
                            .setKey(field);
                    break;
            }
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
            // v8 HeightMode.ROW: cap the visible rows (Flow has no
            // setHeightByRows; emulate with a fixed height).
            // TODO: (phase-4b-2) replace with setAllRowsVisible(true) when the
            // surrounding layout can grow.
            setHeight(historyItems.size() > 5 ? "160px" : (historyItems.size() * 40)
                    + "px");
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
}
