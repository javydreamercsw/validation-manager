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
package net.sourceforge.javydreamercsw.validation.manager.web.tester;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import static net.sourceforge.javydreamercsw.validation.manager.web.core.ContentProvider.TRANSLATOR;
import net.sourceforge.javydreamercsw.validation.manager.web.core.VMUI;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.ExecutionStepPK;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestCaseExecution;
import com.validation.manager.core.server.core.ExecutionStepServer;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.TestCaseExecutionServer;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import net.sourceforge.javydreamercsw.validation.manager.web.component.TestCaseExporter;
import net.sourceforge.javydreamercsw.validation.manager.web.execution.ExecutionWindow;
import net.sourceforge.javydreamercsw.validation.manager.web.provider.AbstractProvider;
import net.sourceforge.javydreamercsw.validation.manager.web.quality.QualityScreenProvider;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public abstract class ExecutionScreen extends AbstractProvider {

    /**
     * A row in the test case tree. Rows carry the same synthetic String ids
     * the v7 TreeTable used ("p" + project id, "tce" + execution id, "es" +
     * execution step ids) so the status column and context menu handling keep
     * working unchanged.
     */
    public static class Row {

        private final String id;
        private String name = "";
        private String summary = "";
        private String assignmentDate = "";

        Row(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getAssignmentDate() {
            return assignmentDate;
        }

        public void setAssignmentDate(String assignmentDate) {
            this.assignmentDate = assignmentDate;
        }
    }

    private ExecutionWindow executionWindow = null;
    private final TreeGrid<Row> testCaseTree;
    //v8 Grid caption has no Flow equivalent; rendered as a Span above the grid
    private final com.vaadin.flow.component.html.Span treeCaption
            = new com.vaadin.flow.component.html.Span();
    private final Map<String, Row> rows = new HashMap<>();

    public ExecutionScreen() {
        testCaseTree = new TreeGrid<>();
        testCaseTree.addColumn(Row::getName)
                .setKey("general.name")
                .setHeader(TRANSLATOR.translate("general.name"))
                .setFlexGrow(1);
        testCaseTree.addComponentColumn(this::getStatusComponent)
                .setKey("general.status")
                .setHeader(TRANSLATOR.translate("general.status"))
                .setFlexGrow(1);
        testCaseTree.addColumn(Row::getSummary)
                .setKey("general.summary")
                .setHeader(TRANSLATOR.translate("general.summary"));
        testCaseTree.addColumn(Row::getAssignmentDate)
                .setKey("general.assignment.date")
                .setHeader(TRANSLATOR.translate("general.assignment.date"));
        testCaseTree.setHierarchyColumn("general.name");
        treeCaption.setText(TRANSLATOR.translate("available.tests"));
        //Context menu replacing the v7 Action.Handler. The native menu opens
        //on right click by itself; a right-click listener only needs to select
        //the row so the menu can be rebuilt for it.
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setTarget(testCaseTree);
        testCaseTree.addItemClickListener((com.vaadin.flow.component.grid.ItemClickEvent<Row> event) -> {
            //Select item on right click and rebuild the menu for it
            testCaseTree.asSingleSelect().setValue(event.getItem());
            contextMenu.removeAll();
            Row selected = event.getItem();
            if (selected != null) {
                addContextMenuItems(contextMenu, selected.getId());
            }
        });
    }

    private Component getStatusComponent(Row row) {
        String id = row.getId();
        String message;
        HorizontalLayout icons = new HorizontalLayout();
        Button label = new Button();
        Button label2 = new Button();
        icons.add(label2);
        icons.add(label);
        label.addThemeNames("tertiary");
        label2.addThemeNames("tertiary");
        Map<String, Integer> summary = new HashMap<>();
        boolean locked = false;
        if (id.startsWith("tce")) {
            TestCaseExecutionServer tce
            = new TestCaseExecutionServer(
                    Integer.parseInt(id.substring(3)));
            summary = getSummary(tce, -1);
            locked = isLocked(tce);
        } else if (id.startsWith("es")) {
            ExecutionStepServer es
            = new ExecutionStepServer(extractExecutionStepPK(id));
            summary = getSummary(
                    es.getTestCaseExecution(),
                    Integer.parseInt(id
                            .substring(id.lastIndexOf("-") + 1)));
            locked = es.getLocked();
        }
        if (locked) {
            label2.setIcon(new Icon(VaadinIcon.LOCK));
            label2.setTooltipText(TRANSLATOR.translate("message.locked"));
        }
        if (!summary.isEmpty()) {
            if (summary.containsKey("result.fail")) {
                //At least one failure means the test case is failing
                message = "result.fail";
            } else if (summary.containsKey("result.blocked")) {
                //It is blocked
                message = "result.blocked";
            } else if (summary.containsKey("result.pending")
            && !summary.containsKey("result.pass")) {
                //Still not done
                message = "result.pending";
            } else if (summary.containsKey("result.pending")
            && summary.containsKey("result.pass")) {
                //In progress
                message = "result.progress";
            } else {
                //All is pass
                message = "result.pass";
            }
            label.setText(TRANSLATOR
                    .translate(message));
            label.setTooltipText(TRANSLATOR
                    .translate(message));
            //Completed. Now check result
            switch (message) {
                case "result.pass":
                    label.setIcon(new Icon(VaadinIcon.CHECK));
                    break;
                case "result.fail":
                    label.setIcon(new Icon(VaadinIcon.CLOSE));
                    break;
                case "result.blocked":
                    label.setIcon(new Icon(VaadinIcon.PAUSE));
                    break;
                case "result.pending":
                    label.setIcon(new Icon(VaadinIcon.CLOCK));
                    break;
                case "result.progress":
                    label.setIcon(new Icon(VaadinIcon.AUTOMATION));
                    break;
                default:
                    label.setIcon(new Icon(VaadinIcon.CLOCK));
                    break;
            }
            return icons;
        }
        return new HorizontalLayout();
    }

    /**
     * Build the context menu entries for the given row id, mirroring the v7
     * Action.Handler behavior.
     */
    private void addContextMenuItems(ContextMenu menu, String id) {
        TestCaseExecutionServer tce;
        int tcID;
        if (id.startsWith("es")) {
            tce = new TestCaseExecutionServer(new ExecutionStepServer(
                    extractExecutionStepPK(id))
                    .getTestCaseExecution().getId());
            tcID = Integer.parseInt(id.substring(id.lastIndexOf("-") + 1));
        } else if (id.startsWith("tce")) {
            tce = new TestCaseExecutionServer(
                    Integer.parseInt(id.substring(3)));
            tcID = -1;
        } else {
            //Project rows: no execution behind them, mirrors v7 null target
            tce = null;
            tcID = -1;
        }
        if (!isLocked(tce, tcID)
                && ExecutionScreen.this instanceof TesterScreenProvider) {
            menu.addItem(TRANSLATOR
                    .translate("general.execute"),
                    (selectedItem) -> {
                        showExecutionFor(id);
                    });
        } else if (isLocked(tce, tcID)
                && ExecutionScreen.this instanceof QualityScreenProvider) {
            menu.addItem(TRANSLATOR
                    .translate("general.review"),
                    (selectedItem) -> {
                        showExecutionFor(id);
                    });
        }
        menu.addItem(TRANSLATOR
                .translate("general.export"),
                (selectedItem) -> {
                    exportFor(id);
                });
    }

    private void exportFor(String target) {
        viewExecutionScreen(getExecutionsFor(target), getTcIdFor(target));
    }

    private void showExecutionFor(String target) {
        showExecutionScreen(getExecutionsFor(target), getTcIdFor(target));
    }

    private List<TestCaseExecutionServer> getExecutionsFor(String target) {
        List<TestCaseExecutionServer> executions = new ArrayList<>();
        int tcID = getTcIdFor(target);
        if (target.startsWith("tce")) {
            executions.add(new TestCaseExecutionServer(
                    Integer.parseInt(target.substring(3))));
        } else if (target.startsWith("es")) {
            executions.add(new TestCaseExecutionServer(new ExecutionStepServer(
                    extractExecutionStepPK(target))
                    .getTestCaseExecution().getId()));
        }
        return executions;
    }

    private int getTcIdFor(String target) {
        return target.startsWith("es")
                ? Integer.parseInt(target
                        .substring(target.lastIndexOf("-") + 1))
                : -1;
    }

    private void viewExecutionScreen(List<TestCaseExecutionServer> executions,
            int tcID) {
        ValidationManagerUI.getInstance().openDialog(TestCaseExporter
                .getExecutionExporter(executions, tcID));
    }

    protected Map<String, Integer> getSummary(TestCaseExecution tce, int tcId) {
        Map<String, Integer> summary = new HashMap<>();
        tce.getExecutionStepList().forEach((ExecutionStep es) -> {
            if (tcId == -1 || es.getStep().getTestCase().getTestCasePK().getId() == tcId) {
                if (es.getExecutionStart() != null && es.getExecutionEnd() == null) {
                    //In progress
                    if (!summary.containsKey("progress")) {
                        summary.put("progress", 0);
                    }
                    summary.put("progress", summary.get("progress") + 1);
                } else if (es.getResultId() == null
                        || (es.getExecutionStart() == null
                        && es.getExecutionEnd() == null)) {
                    //Not started
                    if (!summary.containsKey("result.pending")) {
                        summary.put("result.pending", 0);
                    }
                    summary.put("result.pending", summary.get("result.pending") + 1);
                } else if (es.getExecutionStart() != null && es.getExecutionEnd() != null) {
                    if (!summary.containsKey(es.getResultId().getResultName())) {
                        summary.put(es.getResultId().getResultName(), 0);
                    }
                    summary.put(es.getResultId().getResultName(),
                            summary.get(es.getResultId().getResultName()) + 1);
                }
            }
        });
        return summary;
    }

    @Override
    public boolean shouldDisplay() {
        return ValidationManagerUI.getInstance().getUser() != null
                && ValidationManagerUI.getInstance()
                        .checkRight("system.configuration");
    }

    protected ExecutionStepPK extractExecutionStepPK(String itemId) {
        String id = itemId.substring(2); //Remove es
        int esId;
        int sId;
        int tcId;
        StringTokenizer st = new StringTokenizer(id, "-");
        esId = Integer.parseInt(st.nextToken());
        sId = Integer.parseInt(st.nextToken());
        tcId = Integer.parseInt(st.nextToken());
        return new ExecutionStepPK(esId, sId, tcId);
    }

    protected void showExecutionScreen(List<TestCaseExecutionServer> executions,
            int tcID) {
        if (executionWindow == null) {
            executionWindow = new ExecutionWindow(executions, tcID,
                    this instanceof QualityScreenProvider);
            executionWindow.setHeaderTitle(TRANSLATOR
                    .translate("test.execution"));
            executionWindow.setResizable(false);
            executionWindow.setSizeFull();
        }
        if (!ValidationManagerUI.getInstance().isOpen(executionWindow)) {
            ValidationManagerUI.getInstance().openDialog(executionWindow);
        }
    }

    @Override
    public Component getContent() {
        VerticalLayout vl = new VerticalLayout();
        update();
        vl.add(treeCaption, testCaseTree);
        vl.setId(getComponentCaption());
        return vl;
    }

    @Override
    public void update() {
        if (executionWindow != null) {
            executionWindow.setHeaderTitle(TRANSLATOR
                    .translate("test.execution"));
        }
        treeCaption.setText(TRANSLATOR
                .translate("available.tests"));
        TreeData<Row> treeData = new TreeData<>();
        rows.clear();
        testCaseTree.setDataProvider(new TreeDataProvider<>(treeData));
        if (ValidationManagerUI.getInstance().getUser() != null) {
            ProjectServer.getProjects().forEach(p -> {
                if (p.getParentProjectId() == null) {
                    Row projectRow = addRow(treeData, null,
                            "p" + p.getId(), p.getName());
                    p.getProjectList().forEach(sp -> {
                        //Add subprojects
                        Row subProjectRow = addRow(treeData, projectRow,
                                "p" + sp.getId(), sp.getName());
                        //Add applicable Executions
                        sp.getTestProjectList().forEach(test -> {
                            test.getTestPlanList().forEach(tp -> {
                                tp.getTestCaseList().forEach(testCase -> {
                                    List<Integer> tcids = new ArrayList<>();
                                    testCase.getStepList().forEach(s -> {
                                        s.getExecutionStepList().forEach(es -> {
                                            TestCaseExecution tce = es.getTestCaseExecution();
                                            Row tceRow = addRow(treeData,
                                                    subProjectRow,
                                                    "tce" + tce.getId(),
                                                    tce.getName());
                                            if (this instanceof QualityScreenProvider
                                                    && es.getLocked()
                                                    || (es.getAssignee() != null
                                                    && es.getAssignee().getId()
                                                            .equals(ValidationManagerUI.getInstance()
                                                                    .getUser().getId()))) {
                                                TestCase tc = es.getStep().getTestCase();
                                                if (!tcids.contains(tc.getTestCasePK().getId())) {
                                                    tcids.add(tc.getTestCasePK().getId());
                                                    DateTimeFormatter format
                                                            = DateTimeFormatter.ofPattern("MMM d yyyy  hh:mm a");
                                                    LocalDateTime time
                                                            = LocalDateTime.ofInstant(es.getAssignedTime()
                                                                    .toInstant(), ZoneId.systemDefault());
                                                    String key = "es" + es.getExecutionStepPK().getTestCaseExecutionId()
                                                            + "-" + es.getStep().getStepPK().getId()
                                                            + "-" + tc.getTestCasePK().getId();
                                                    Row stepRow = addRow(treeData,
                                                            tceRow, key,
                                                            tc.getName());
                                                    stepRow.setSummary(tc.getSummary() == null
                                                            ? "" : new String(tc.getSummary()));
                                                    stepRow.setAssignmentDate(format.format(time));
                                                }
                                            }
                                        });
                                    });
                                    tcids.clear();
                                });
                            });
                        });
                    });
                }
            });
        }
        //v7 TreeTable rows were expanded by default
        testCaseTree.expandRecursively(treeData.getRootItems(),
                Integer.MAX_VALUE);
        //Update column titles
        for (String h : new String[]{"general.name", "general.status",
            "general.summary", "general.assignment.date"}) {
            testCaseTree.getColumnByKey(h).setHeader(TRANSLATOR.translate(h));
        }
        testCaseTree.setSizeFull();
    }

    /**
     * Create a row (or reuse the existing one with the same id) and hook it
     * into the tree.
     */
    private Row addRow(TreeData<Row> treeData, Row parent, String id,
            String name) {
        Row row = rows.get(id);
        if (row == null) {
            row = new Row(id);
            rows.put(id, row);
            treeData.addItem(parent, row);
        }
        row.setName(name);
        return row;
    }
}
