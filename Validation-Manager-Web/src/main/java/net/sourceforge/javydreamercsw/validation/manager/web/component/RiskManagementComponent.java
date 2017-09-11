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
import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;
import com.vaadin.ui.TreeTable;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.FailureModeHasCauseHasRiskCategory;
import com.validation.manager.core.db.FailureModeHasCauseHasRiskCategoryPK;
import com.validation.manager.core.db.Fmea;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.RiskCategory;
import com.validation.manager.core.db.controller.FailureModeHasCauseHasRiskCategoryJpaController;
import com.validation.manager.core.tool.Tool;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class RiskManagementComponent extends HorizontalSplitPanel {

    private static final Logger LOG
            = Logger.getLogger(RiskManagementComponent.class.getSimpleName());

    public RiskManagementComponent(Project p) {
        setSplitPosition(25, Unit.PERCENTAGE);
        setLocked(true);
        Tree tree = new Tree("Test");
        tree.setSizeFull();
        tree.addItem(p);
        tree.setItemCaption(p, p.getName());
        p.getFmeaList().forEach(fmea -> {
            tree.addItem(fmea);
            tree.setItemCaption(fmea, fmea.getName());
            tree.setParent(fmea, p);
        });
        TreeTable ttable = new TreeTable("FMEA");
        ttable.addContainerProperty("Name", String.class, null);
        ttable.addContainerProperty("Hazard", String.class, null);
        ttable.addContainerProperty("Failure Mode", String.class, null);
        ttable.addContainerProperty("Cause", String.class, null);
        ttable.setSizeFull();
        tree.addItemClickListener(event -> {
            if (event.getItem() != null) {
                Item clicked = event.getItem();
                tree.select(event.getItemId());
            }
            if (tree.getValue() != null && tree.getValue() instanceof Fmea) {
                Fmea fmea = (Fmea) tree.getValue();
                fmea.getRiskCategoryList().forEach(rc -> {
                    ttable.addGeneratedColumn(rc.getName(),
                            (Table source, Object itemId, Object columnId) -> {
                                if (itemId instanceof String) {
                                    String id = (String) itemId;
                                    if (id.startsWith("hazard")) {
                                        //We have one of our entries
                                        StringTokenizer st
                                                = new StringTokenizer(id
                                                        .substring(id.indexOf("-") + 1), "-");
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
                                                        return new Label("Error!");
                                                    }
                                                }
                                            }
                                            //Return result in label.
                                            return new Label("" + fmhchrc.getCategoryValue());
                                        }
                                    }
                                }
                                return new Label();
                            });
                });
                ttable.removeAllItems();
                fmea.getRiskItemList().forEach(item -> {
                    ttable.addItem(new Object[]{item.getDescription(),
                        null, null, null},
                            item.getRiskItemPK().getId());
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
                                ttable.addItem(new Object[]{null,
                                    rihh.getHazard().getName(),
                                    hhfm.getFailureMode().getName(),
                                    fmhc.getCause().getDescription()},
                                        hkey);
                                ttable.setParent(hkey, item.getRiskItemPK().getId());
                                //No children
                                ttable.setChildrenAllowed(hkey, false);
                            });
                        });
                    });
                });
            }
        });
        ContextMenu contextMenu = new ContextMenu(tree, true);
        tree.addItemClickListener((ItemClickEvent event) -> {
            if (event.getButton() == MouseButton.RIGHT) {
                contextMenu.removeItems();
            }
        });
        setFirstComponent(tree);
        setSecondComponent(ttable);
        setSizeFull();
    }
}
