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
package net.sourceforge.javydreamercsw.validation.manager.web.traceability;

import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TreeTable;
import static com.validation.manager.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.VMUI;
import com.validation.manager.core.db.Baseline;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.ExecutionStepHasIssue;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.tool.Tool;

/**
 * Trace Matrix component. Traces relationship from requirements to test case
 * steps including results and issues.
 *
 * @author Javier Ortiz Bultronjavier.ortiz.78@gmail.com
 */
public class TraceMatrix extends TreeTable {

    private final Project p;

    public TraceMatrix(Project p) {
        this.p = p;
        init();
    }

    public TraceMatrix(Project p, String caption) {
        super(caption);
        this.p = p;
        init();
    }

    private void init() {
        addContainerProperty(TRANSLATOR.translate("general.requirement"),
                String.class, "");
        addContainerProperty(TRANSLATOR.translate("general.test.case"),
                String.class, "");
        addContainerProperty(TRANSLATOR.translate("general.result"),
                String.class, "");
        addContainerProperty(TRANSLATOR.translate("general.issue"),
                String.class, "");
        Tool.extractRequirements(p).forEach((r) -> {
            if (r.getParentRequirementId() == null) {
                addRequirement(r);
            }
        });
        setAnimationsEnabled(true);
        for (Object item : getItemIds().toArray()) {
            expandItem(item);
        }
        setChildrenAllowedAsNeeded();
        setSizeFull();
    }

    private void expandItem(Object item) {
        setCollapsed(item, false);
        if (hasChildren(item)) {
            getChildren(item).forEach(child -> {
                expandItem(child);
            });
        }
    }

    private void addRequirement(Requirement r) {
        addItem(new Object[]{r.getUniqueId(),
            "", "", ""}, r.getUniqueId());
        addTestCases(r);
        //Add children
        r.getRequirementList().forEach(child -> {
            addRequirement(child);
            setParent(child.getUniqueId(), r.getUniqueId());
        });
        setItemIcon(r.getUniqueId(), VMUI.REQUIREMENT_ICON);
    }

    private void addTestCases(Requirement r) {
        r.getHistoryList().forEach(h -> {
            h.getExecutionStepList().forEach(es -> {
                TestCase tc = es.getStep().getTestCase();
                Object rId = Tool.buildId(r);
                Object tcID = Tool.buildId(tc, rId);
                Object esId = Tool.buildId(es, rId);
                if (!containsId(tcID)) {
                    addItem(new Object[]{"",
                        tc.getName(), "", ""}, tcID);
                    setParent(tcID, rId);
                    setItemIcon(tcID, VMUI.TEST_ICON);
                }
                if (es.getResultId() != null) {
                    String result = es.getResultId().getResultName();
                    addItem(new Object[]{"",
                        TRANSLATOR.translate("general.step")
                        + " #" + es.getStep().getStepSequence(),
                        TRANSLATOR.translate(result), ""}, esId);
                    setParent(esId, tcID);
                    //Completed. Now check result
                    Resource icon;
                    switch (result) {
                        case "result.pass":
                            icon = VaadinIcons.CHECK;
                            break;
                        case "result.fail":
                            icon = VaadinIcons.CLOSE;
                            break;
                        case "result.blocked":
                            icon = VaadinIcons.PAUSE;
                            break;
                        case "result.progress":
                            icon = VaadinIcons.AUTOMATION;
                            break;
                        default:
                            icon = VaadinIcons.CLOCK;
                            break;
                    }
                    setItemIcon(esId, icon);
                }
                addIssues(es);
            });
        });
    }

    private void setChildrenAllowedAsNeeded() {
        getItemIds().forEach(item -> {
            setChildrenAllowed(item,
                    hasChildren(item));
        });
    }

    private void addIssues(ExecutionStep es) {
        for (ExecutionStepHasIssue eshi : es.getExecutionStepHasIssueList()) {
            int issueNumber = eshi.getIssue().getIssuePK().getId();
            Object esId = Tool.buildId(eshi.getExecutionStep());
            Object issueId = Tool.buildId(eshi.getIssue(), esId);
            addItem(new Object[]{"",
                "", "", TRANSLATOR.translate("general.issue") + "#"
                + issueNumber}, issueId);
            setParent(issueId, esId);
            setItemIcon(issueId, VaadinIcons.BUG);
            setChildrenAllowed(issueId, false);
        }
    }

    public Component getMenu() {
        HorizontalLayout hl = new HorizontalLayout();
        ComboBox baseline = new ComboBox(TRANSLATOR.translate("baseline.filter"));
        baseline.setTextInputAllowed(false);
        baseline.setNewItemsAllowed(false);
        Tool.extractRequirements(p).forEach(r -> {
            r.getHistoryList().forEach(h -> {
                h.getBaselineList().forEach(b -> {
                    if (!baseline.containsId(b)) {
                        baseline.addItem(b);
                        baseline.setItemCaption(b, b.getBaselineName());
                    }
                });
            });
        });
        baseline.addValueChangeListener(event -> {
            removeAllItems();
            Baseline b = (Baseline) baseline.getValue();
            if (b == null) {
                //None selected, no filtering
                Tool.extractRequirements(p).forEach((r) -> {
                    if (r.getParentRequirementId() == null) {
                        addRequirement(r);
                    }
                });
            } else {
                b.getHistoryList().forEach(h -> {
                    addRequirement(h.getRequirementId());
                });
            }
        });
        hl.addComponent(baseline);
        Button export = new Button(TRANSLATOR.translate("general.export"));
        export.addClickListener(listener -> {
            //Create the Excel file
            ExcelExport excelExport = new ExcelExport(this);
            excelExport.excludeCollapsedColumns();
            excelExport.setReportTitle(TRANSLATOR.translate("trace.matrix"));
            excelExport.setDisplayTotals(false);
            excelExport.export();
        });
        hl.addComponent(export);
        return hl;
    }
}
