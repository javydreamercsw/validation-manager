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

import com.vaadin.addon.tableexport.DefaultGridHolder;
import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TreeGrid;
import static com.validation.manager.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.db.Baseline;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.ExecutionStepHasIssue;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.tool.Tool;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Trace Matrix component. Traces relationship from requirements to test case
 * steps including results and issues.
 *
 * @author Javier Ortiz Bultronjavier.ortiz.78@gmail.com
 */
public class TraceMatrix extends TreeGrid<TraceMatrix.TraceRow> {

    private final Project p;
    private final TreeData<TraceRow> treeData = new TreeData<>();
    private final Map<Object, TraceRow> rows = new HashMap<>();

    /**
     * A row in the trace matrix. Rows are identified by the same synthetic ids
     * the v7 TreeTable used (built via {@link Tool#buildId}) so parent/child
     * relationships keep working unchanged. Only the column relevant to the
     * row type has a value set.
     */
    public static class TraceRow {

        private final Object id;
        private String requirement = "";
        private String testCase = "";
        private String result = "";
        private String issue = "";

        TraceRow(Object id) {
            this.id = id;
        }

        public Object getId() {
            return id;
        }

        public String getRequirement() {
            return requirement;
        }

        public void setRequirement(String requirement) {
            this.requirement = requirement;
        }

        public String getTestCase() {
            return testCase;
        }

        public void setTestCase(String testCase) {
            this.testCase = testCase;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getIssue() {
            return issue;
        }

        public void setIssue(String issue) {
            this.issue = issue;
        }
    }

    public TraceMatrix(Project p) {
        this.p = p;
        init();
    }

    public TraceMatrix(Project p, String caption) {
        setCaption(caption);
        this.p = p;
        init();
    }

    private void init() {
        addColumn(TraceRow::getRequirement)
                .setId("general.requirement")
                .setCaption(TRANSLATOR.translate("general.requirement"));
        addColumn(TraceRow::getTestCase)
                .setId("general.test.case")
                .setCaption(TRANSLATOR.translate("general.test.case"));
        addColumn(TraceRow::getResult)
                .setId("general.result")
                .setCaption(TRANSLATOR.translate("general.result"));
        addColumn(TraceRow::getIssue)
                .setId("general.issue")
                .setCaption(TRANSLATOR.translate("general.issue"));
        setHierarchyColumn("general.requirement");
        rebuild(null);
        setSizeFull();
    }

    /**
     * Rebuild the whole tree with the given filter (a baseline or none).
     */
    private void rebuild(Baseline filter) {
        treeData.clear();
        rows.clear();
        if (filter == null) {
            //None selected, no filtering
            Tool.extractRequirements(p).forEach((r) -> {
                if (r.getParentRequirementId() == null) {
                    addRequirement(r);
                }
            });
        } else {
            filter.getHistoryList().forEach(h -> {
                addRequirement(h.getRequirementId());
            });
        }
        setDataProvider(new TreeDataProvider<>(treeData));
        //v7 TreeTable rows were expanded by default
        expandRecursively(treeData.getRootItems(), Integer.MAX_VALUE);
    }

    private void addRequirement(Requirement r) {
        TraceRow row;
        if (!rows.containsKey(r.getUniqueId())) {
            row = addItem(r.getUniqueId());
            row.setRequirement(r.getUniqueId());
        } else {
            row = addRowFor(r.getUniqueId());
        }
        addTestCases(r);
        //Add children
        r.getRequirementList().forEach(child -> {
            addRequirement(child);
            treeData.setParent(addRowFor(child.getUniqueId()), row);
        });
    }

    private void addTestCases(Requirement r) {
        r.getHistoryList().forEach(h -> {
            h.getExecutionStepList().forEach(es -> {
                TestCase tc = es.getStep().getTestCase();
                Object rId = Tool.buildId(r);
                Object tcID = Tool.buildId(tc, rId);
                Object esId = Tool.buildId(es, rId);
                TraceRow tcRow;
                if (!rows.containsKey(tcID)) {
                    tcRow = addItem(tcID);
                    tcRow.setTestCase(tc.getName());
                    treeData.setParent(tcRow, addRowFor(rId));
                } else {
                    tcRow = addRowFor(tcID);
                }
                if (es.getResultId() != null) {
                    String result = es.getResultId().getResultName();
                    if (!rows.containsKey(esId)) {
                        TraceRow esRow = addItem(esId);
                        esRow.setTestCase(TRANSLATOR.translate("general.step")
                                + " #" + es.getStep().getStepSequence());
                        esRow.setResult(TRANSLATOR.translate(result));
                        treeData.setParent(esRow, tcRow);
                    }
                    //Per-node icons have no TreeGrid equivalent; the
                    //translated result text conveys the state.
                }
                addIssues(es);
            });
        });
    }

    private void addIssues(ExecutionStep es) {
        for (ExecutionStepHasIssue eshi : es.getExecutionStepHasIssueList()) {
            int issueNumber = eshi.getIssue().getIssuePK().getId();
            Object esId = Tool.buildId(eshi.getExecutionStep());
            Object issueId = Tool.buildId(eshi.getIssue(), esId);
            if (!rows.containsKey(issueId)) {
                TraceRow issueRow = addItem(issueId);
                issueRow.setIssue(TRANSLATOR.translate("general.issue") + "#"
                        + issueNumber);
                //Only attach if the execution step row exists, mirroring the
                //v7 setParent() silent failure when it didn't.
                if (rows.containsKey(esId)) {
                    treeData.setParent(issueRow, addRowFor(esId));
                }
            }
        }
    }

    /**
     * Wrap a raw id into the TraceRow registered for it, creating the row on
     * first use.
     */
    private TraceRow addRowFor(Object id) {
        TraceRow row = rows.get(id);
        if (row == null) {
            row = new TraceRow(id);
            rows.put(id, row);
        }
        return row;
    }

    /**
     * Create a row for the given id, register it and add it to the tree as a
     * root item (re-parented by the callers when needed).
     */
    private TraceRow addItem(Object id) {
        TraceRow row = new TraceRow(id);
        rows.put(id, row);
        treeData.addItem(null, row);
        return row;
    }

    public Component getMenu() {
        HorizontalLayout hl = new HorizontalLayout();
        ComboBox<Baseline> baseline
                = new ComboBox<>(TRANSLATOR.translate("baseline.filter"));
        baseline.setTextInputAllowed(false);
        List<Baseline> baselines = new ArrayList<>();
        Tool.extractRequirements(p).forEach(r -> {
            r.getHistoryList().forEach(h -> {
                h.getBaselineList().forEach(b -> {
                    if (!baselines.contains(b)) {
                        baselines.add(b);
                    }
                });
            });
        });
        baseline.setItems(baselines);
        baseline.setItemCaptionGenerator(b -> b.getBaselineName());
        baseline.addValueChangeListener(event -> {
            rebuild(event.getValue());
        });
        hl.addComponent(baseline);
        Button export = new Button(TRANSLATOR.translate("general.export"));
        export.addClickListener(listener -> {
            //Create the Excel file
            ExcelExport excelExport = new ExcelExport(
                    new DefaultGridHolder(this));
            excelExport.excludeCollapsedColumns();
            excelExport.setReportTitle(TRANSLATOR.translate("trace.matrix"));
            excelExport.setDisplayTotals(false);
            excelExport.export();
        });
        hl.addComponent(export);
        return hl;
    }
}
