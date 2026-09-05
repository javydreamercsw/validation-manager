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

import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.FailureModeHasCauseHasRiskCategory;
import com.validation.manager.core.db.FailureModeHasCauseHasRiskCategoryPK;
import com.validation.manager.core.db.Fmea;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.RiskCategory;
import com.validation.manager.core.db.controller.FailureModeHasCauseHasRiskCategoryJpaController;
import com.validation.manager.core.tool.Tool;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class RiskManagementComponent extends SplitLayout {

    private static final Logger LOG
            = Logger.getLogger(RiskManagementComponent.class.getSimpleName());
    private static final List<String> FIRST_COLUMNS
            = Arrays.asList("Name", "Hazard", "Failure Mode", "Cause");

    /**
     * One row of the FMEA table: a risk item or a hazard/failure/cause entry.
     */
    private static final class Row {

        private final String name;
        private final String hazard;
        private final String failureMode;
        private final String cause;
        private final String key;

        private Row(String name, String hazard, String failureMode,
                String cause, String key) {
            this.name = name;
            this.hazard = hazard;
            this.failureMode = failureMode;
            this.cause = cause;
            this.key = key;
        }

        public String getName() {
            return name == null ? "" : name;
        }

        public String getHazard() {
            return hazard == null ? "" : hazard;
        }

        public String getFailureMode() {
            return failureMode == null ? "" : failureMode;
        }

        public String getCause() {
            return cause == null ? "" : cause;
        }
    }

    public RiskManagementComponent(Project p) {
        setOrientation(SplitLayout.Orientation.HORIZONTAL);
        setSplitterPosition(25);
        //Flow has no standalone Tree: the FMEA hierarchy renders in a TreeGrid.
        TreeGrid<Object> tree = new TreeGrid<>();
        TreeData<Object> treeData = new TreeData<>();
        treeData.addRootItems(p);
        tree.setDataProvider(new TreeDataProvider<>(treeData));
        tree.addComponentHierarchyColumn(item -> new Span(item instanceof Project
                ? ((Project) item).getName()
                : item instanceof Fmea ? ((Fmea) item).getName()
                : String.valueOf(item)))
                .setKey("caption");
        tree.setSizeFull();
        tree.expand(p);
        TreeGrid<Row> ttable = new TreeGrid<>();
        ttable.addColumn(Row::getName).setKey("Name").setHeader("Name");
        ttable.addColumn(Row::getHazard).setKey("Hazard").setHeader("Hazard");
        ttable.addColumn(Row::getFailureMode).setKey("Failure Mode")
                .setHeader("Failure Mode");
        ttable.addColumn(Row::getCause).setKey("Cause").setHeader("Cause");
        ttable.setSizeFull();
        tree.addItemClickListener((ItemClickEvent<Object> event) -> {
            tree.asSingleSelect().setValue(event.getItem());
            if (tree.asSingleSelect().getValue() != null
                    && tree.asSingleSelect().getValue() instanceof Fmea) {
                Fmea fmea = (Fmea) tree.asSingleSelect().getValue();
                List<RiskCategory> categories = fmea.getRiskCategoryList();
                //Replace the generated category columns on each selection
                //to match the v7 addGeneratedColumn() semantics.
                ttable.getColumns().stream()
                        .filter(col -> !FIRST_COLUMNS.contains(col.getKey()))
                        .collect(Collectors.toList())
                        .forEach(col -> ttable.removeColumn(col));
                //One column per risk category with a computed cell
                categories.forEach(rc -> {
                    ttable.addColumn(row -> {
                        if (row.key != null && row.key.startsWith("hazard-")) {
                            //We have one of our entries
                            StringTokenizer st
                                    = new StringTokenizer(row.key
                                            .substring(row.key.indexOf("-") + 1), "-");
                            FailureModeHasCauseHasRiskCategoryJpaController c
                                    = new FailureModeHasCauseHasRiskCategoryJpaController(DataBaseManager
                                            .getEntityManagerFactory());
                            FailureModeHasCauseHasRiskCategory fmhchrc
                                    = c.findFailureModeHasCauseHasRiskCategory(
                                            new FailureModeHasCauseHasRiskCategoryPK(
                                                    Integer.parseInt(st.nextToken()),
                                                    Integer.parseInt(st.nextToken()),
                                                    Integer.parseInt(st.nextToken()),
                                                    Integer.parseInt(st.nextToken()),
                                                    Integer.parseInt(st.nextToken()),
                                                    Integer.parseInt(st.nextToken()),
                                                    rc.getId()));
                            if (fmhchrc != null) {
                                RiskCategory cat = fmhchrc.getRiskCategory();
                                if (cat.getCategoryEquation() != null
                                        && !cat.getCategoryEquation().trim().isEmpty()) {
                                    //Calculate based on equation
                                    Double result = Tool.evaluateEquation(fmhchrc);
                                    //Update record if result changed
                                    LOG.log(Level.FINE, "Result: {0}", result);
                                    if (result != fmhchrc.getCategoryValue()) {
                                        try {
                                            fmhchrc.setCategoryValue(result);
                                            c.edit(fmhchrc);
                                        } catch (Exception ex) {
                                            Exceptions.printStackTrace(ex);
                                        }
                                    }
                                }
                                //Return result as text.
                                return "" + fmhchchrcValue(fmhchrc);
                            }
                        }
                        return "";
                    }).setHeader(rc.getName());
                });
                ttable.setDataProvider(buildRows(fmea));
            }
        });
        ContextMenu contextMenu = new ContextMenu(tree);
        addToPrimary(tree);
        addToSecondary(ttable);
        setSizeFull();
    }

    private static String fmhchchrcValue(FailureModeHasCauseHasRiskCategory fmhchrc) {
        return "" + fmhchrc.getCategoryValue();
    }

    private static TreeDataProvider<Row> buildRows(
            Fmea fmea) {
        TreeData<Row> data = new TreeData<>();
        fmea.getRiskItemList().forEach(item -> {
            Row itemRow = new Row(item.getDescription(), null, null, null,
                    "item-" + item.getRiskItemPK().getId());
            data.addRootItems(itemRow);
            item.getRiskItemHasHazardList().forEach(rihh -> {
                rihh.getHazardHasFailureModeList().forEach(hhfm -> {
                    hhfm.getFailureModeHasCauseList().forEach(fmhc -> {
                        String hkey = "hazard-"
                                + item.getRiskItemPK().getId()
                                + "-"
                                + fmea.getFmeaPK().getId()
                                + "-"
                                + fmea.getFmeaPK().getProjectId()
                                + "-"
                                + rihh.getHazard().getId()
                                + "-"
                                + hhfm.getFailureMode().getId()
                                + "-"
                                + fmhc.getCause().getId();
                        Row hazardRow = new Row(null,
                                rihh.getHazard().getName(),
                                hhfm.getFailureMode().getName(),
                                fmhc.getCause().getDescription(), hkey);
                        data.addItem(itemRow, hazardRow);
                    });
                });
            });
        });
        return new TreeDataProvider<>(data);
    }
}
