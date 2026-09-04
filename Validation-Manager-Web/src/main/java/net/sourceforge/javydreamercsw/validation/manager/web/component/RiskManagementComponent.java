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

import com.vaadin.addon.contextmenu.ContextMenu;
import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.TreeGrid;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.FailureModeHasCauseHasRiskCategory;
import com.validation.manager.core.db.FailureModeHasCauseHasRiskCategoryPK;
import com.validation.manager.core.db.Fmea;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.RiskCategory;
import com.validation.manager.core.db.controller.FailureModeHasCauseHasRiskCategoryJpaController;
import com.validation.manager.core.tool.Tool;
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
public final class RiskManagementComponent extends HorizontalSplitPanel {

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
        setSplitPosition(25, Unit.PERCENTAGE);
        setLocked(true);
        Tree<Object> tree = new Tree<>("Test");
        TreeData<Object> treeData = new TreeData<>();
        treeData.addRootItems(p);
        tree.setItemCaptionGenerator(item -> item instanceof Project
                ? ((Project) item).getName()
                : item instanceof Fmea ? ((Fmea) item).getName()
                : String.valueOf(item));
        p.getFmeaList().forEach(fmea -> {
            treeData.addItem(p, fmea);
        });
        tree.setDataProvider(new TreeDataProvider<>(treeData));
        tree.setSizeFull();
        tree.expand(p);
        TreeGrid<Row> ttable = new TreeGrid<>();
        ttable.setCaption("FMEA");
        ttable.addColumn(Row::getName).setId("Name").setCaption("Name");
        ttable.addColumn(Row::getHazard).setId("Hazard").setCaption("Hazard");
        ttable.addColumn(Row::getFailureMode).setId("Failure Mode")
                .setCaption("Failure Mode");
        ttable.addColumn(Row::getCause).setId("Cause").setCaption("Cause");
        ttable.setSizeFull();
        tree.addItemClickListener((Tree.ItemClick<Object> event) -> {
            tree.asSingleSelect().setValue(event.getItem());
            if (tree.asSingleSelect().getValue() != null
                    && tree.asSingleSelect().getValue() instanceof Fmea) {
                Fmea fmea = (Fmea) tree.asSingleSelect().getValue();
                List<RiskCategory> categories = fmea.getRiskCategoryList();
                //Replace the generated category columns on each selection
                //to match the v7 addGeneratedColumn() semantics.
                ttable.getColumns().stream()
                        .filter(col -> !FIRST_COLUMNS.contains(col.getId()))
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
                                return "" + fmhchrc.getCategoryValue();
                            }
                        }
                        return "";
                    }).setCaption(rc.getName());
                });
                ttable.setDataProvider(buildRows(fmea));
            }
        });
        ContextMenu contextMenu = new ContextMenu(tree, true);
        tree.addItemClickListener((Tree.ItemClick<Object> event) -> {
            if (event.getMouseEventDetails().getButton() == MouseButton.RIGHT) {
                contextMenu.removeItems();
            }
        });
        setFirstComponent(tree);
        setSecondComponent(ttable);
        setSizeFull();
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
