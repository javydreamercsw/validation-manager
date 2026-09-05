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

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.server.StreamResource;
import static net.sourceforge.javydreamercsw.validation.manager.web.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.db.Baseline;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.ExecutionStepHasIssue;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.tool.Tool;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Trace Matrix component. Traces relationship from requirements to test case
 * steps including results and issues.
 *
 * @author Javier Ortiz Bultronjavier.ortiz.78@gmail.com
 */
public class TraceMatrix extends TreeGrid<TraceMatrix.TraceRow> {

    private static final Logger LOG
            = Logger.getLogger(TraceMatrix.class.getSimpleName());
    private final Project p;
    private final com.vaadin.flow.data.provider.hierarchy.TreeData<TraceRow> treeData
            = new com.vaadin.flow.data.provider.hierarchy.TreeData<>();
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
        setId(caption);
        this.p = p;
        init();
    }

    private void init() {
        addColumn(TraceRow::getRequirement)
                .setKey("general.requirement")
                .setHeader(TRANSLATOR.translate("general.requirement"));
        addColumn(TraceRow::getTestCase)
                .setKey("general.test.case")
                .setHeader(TRANSLATOR.translate("general.test.case"));
        addColumn(TraceRow::getResult)
                .setKey("general.result")
                .setHeader(TRANSLATOR.translate("general.result"));
        addColumn(TraceRow::getIssue)
                .setKey("general.issue")
                .setHeader(TRANSLATOR.translate("general.issue"));
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
        com.vaadin.flow.component.combobox.ComboBox<Baseline> baseline
                = new com.vaadin.flow.component.combobox.ComboBox<>(
                        TRANSLATOR.translate("baseline.filter"));
        baseline.setAllowCustomValue(false);
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
        baseline.setItemLabelGenerator(b -> b.getBaselineName());
        baseline.addValueChangeListener(event -> {
            rebuild(event.getValue());
        });
        hl.add(baseline);
        //The tableexport addon is gone; export the visible matrix as an
        //Excel file served from a temporary StreamResource (POI is already
        //a dependency).
        Button export = new Button(TRANSLATOR.translate("general.export"));
        export.addClickListener(listener -> {
            try (XSSFWorkbook wb = new XSSFWorkbook()) {
                Sheet sheet = wb.createSheet(
                        TRANSLATOR.translate("trace.matrix"));
                Row header = sheet.createRow(0);
                List<com.vaadin.flow.component.grid.Grid.Column<TraceRow>> columns
                        = getColumns();
                for (int i = 0; i < columns.size(); i++) {
                    header.createCell(i).setCellValue(columns.get(i)
                            .getHeaderText());
                }
                int rowCount = 1;
                for (TraceRow row : rows.values()) {
                    Row r = sheet.createRow(rowCount++);
                    r.createCell(0).setCellValue(row.getRequirement());
                    r.createCell(1).setCellValue(row.getTestCase());
                    r.createCell(2).setCellValue(row.getResult());
                    r.createCell(3).setCellValue(row.getIssue());
                }
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                wb.write(bos);
                StreamResource resource = new StreamResource(
                        "trace-matrix.xlsx",
                        () -> new java.io.ByteArrayInputStream(bos.toByteArray()));
                resource.setContentType(
                        "application/vnd.openxmlformats-officedocument"
                        + ".spreadsheetml.sheet");
                String url = com.vaadin.flow.component.UI.getCurrent()
                        .getSession().getResourceRegistry()
                        .registerResource(resource).getResourceUri().toString();
                Anchor download = new Anchor(url,
                        TRANSLATOR.translate("general.export"));
                download.getElement().setAttribute("download",
                        "trace-matrix.xlsx");
                hl.add(download);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        });
        hl.add(export);
        return hl;
    }
}
