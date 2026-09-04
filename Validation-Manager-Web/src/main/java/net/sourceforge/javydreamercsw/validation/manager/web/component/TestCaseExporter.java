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

import com.vaadin.addon.tableexport.DefaultGridHolder;
import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import static com.validation.manager.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.VMUI;
import com.validation.manager.core.db.DataEntry;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.ExecutionStepHasVmUser;
import com.validation.manager.core.db.HistoryField;
import com.validation.manager.core.db.Issue;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.server.core.AttachmentServer;
import com.validation.manager.core.server.core.TestCaseExecutionServer;
import com.validation.manager.core.server.core.VMSettingServer;
import com.validation.manager.core.tool.Tool;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class TestCaseExporter {

    private static final Logger LOG
            = Logger.getLogger(TestCaseExporter.class.getSimpleName());

    /**
     * One row of the export tree: a test case, a step or a data entry field.
     */
    private static final class Row {

        private String testCase = "";
        private String sequence = "";
        private String text = "";
        private String notes = "";
        private String expectedResult = "";
        private String result = "";
        private HorizontalLayout attachments;
        private String tester = "";
        private String startDate = "";
        private String endDate = "";
        private String reviewer = "";
        private String reviewDate = "";

        private static Row ofTestCase(String name) {
            Row r = new Row();
            r.testCase = name;
            return r;
        }

        private static Row ofStep(String sequence, String text, String notes,
                String expectedResult, String result,
                HorizontalLayout attachments, String tester, String startDate,
                String endDate, String reviewer, String reviewDate) {
            Row r = new Row();
            r.sequence = sequence;
            r.text = text;
            r.notes = notes;
            r.expectedResult = expectedResult;
            r.result = result;
            r.attachments = attachments;
            r.tester = tester;
            r.startDate = startDate;
            r.endDate = endDate;
            r.reviewer = reviewer;
            r.reviewDate = reviewDate;
            return r;
        }

        private static Row ofField(String name) {
            Row r = new Row();
            r.result = name;
            return r;
        }
    }

    public static Window getTestCaseExporter(List<TestCase> testCases) {
        TreeGrid<Row> summary = new TreeGrid<>();
        TreeData<Row> treeData = new TreeData<>();
        addColumns(summary);
        Map<Object, Row> caseRows = new HashMap<>();
        testCases.forEach(tc -> {
            for (Step step : tc.getStepList()) {
                //Add test case if not there already
                if (!caseRows.containsKey(tc.getTestCasePK())) {
                    Row caseRow = Row.ofTestCase(tc.getName());
                    treeData.addRootItems(caseRow);
                    caseRows.put(tc.getTestCasePK(), caseRow);
                }
                //Add the step
                String text = new String(step.getText(), StandardCharsets.UTF_8);
                Row stepRow = Row.ofStep("" + step.getStepSequence(), text,
                        step.getNotes(),
                        new String(step.getExpectedResult(),
                                StandardCharsets.UTF_8),
                        "", null, "", "", "", "", "");
                treeData.addItem(caseRows.get(tc.getTestCasePK()), stepRow);
                //Add the fields of the test case
                for (DataEntry de : step.getDataEntryList()) {
                    Row fieldRow = Row.ofField(de.getEntryName());
                    treeData.addItem(stepRow, fieldRow);
                }
            }
        });
        summary.setDataProvider(new TreeDataProvider<>(treeData));
        summary.expand(treeData.getRootItems());
        summary.setSizeFull();
        return getExportWindow(summary, null, -1);
    }

    public static Window getExecutionExporter(List<TestCaseExecutionServer> executions,
            int tcID) {
        TreeGrid<Row> summary = new TreeGrid<>();
        TreeData<Row> treeData = new TreeData<>();
        addColumns(summary);
        SimpleDateFormat format = new SimpleDateFormat(
                VMSettingServer.getSetting("date.format")
                        .getStringVal());
        Map<Object, Row> caseRows = new HashMap<>();
        for (TestCaseExecutionServer execution : executions) {
            for (ExecutionStep es : execution.getExecutionStepList()) {
                if (tcID < 0
                        || es.getExecutionStepPK().getStepTestCaseId() == tcID) {
                    //Add test case if not there already
                    if (!caseRows.containsKey(es.getStep().getTestCase()
                            .getTestCasePK())) {
                        Row caseRow = Row.ofTestCase(es.getStep().getTestCase()
                                .getName());
                        treeData.addRootItems(caseRow);
                        caseRows.put(es.getStep().getTestCase().getTestCasePK(),
                                caseRow);
                    }
                    //First calculate the sequence number
                    List<Row> siblings = treeData.getChildren(caseRows.get(es
                            .getStep().getTestCase().getTestCasePK()));
                    int i = siblings == null ? 1 : siblings.size() + 1;
                    //Calculate the fields from History
                    String text = "";
                    String notes = "";
                    String expected = "";
                    String tester = "";
                    String reviewer = "";
                    //Search roles for tester and reviewer
                    for (ExecutionStepHasVmUser eshu : es.getExecutionStepHasVmUserList()) {
                        if (eshu.getRole().getRoleName().equals("tester")) {
                            tester = eshu.getVmUser().getFirstName()
                                    + " " + eshu.getVmUser().getLastName();
                        }
                        if (eshu.getRole().getRoleName().equals("quality")) {
                            reviewer = eshu.getVmUser().getFirstName()
                                    + " " + eshu.getVmUser().getLastName();
                        }
                    }
                    for (HistoryField f : es.getStepHistory()
                            .getHistoryFieldList()) {
                        switch (f.getFieldName()) {
                            case "text":
                                text = f.getFieldValue();
                                break;
                            case "expectedResult":
                                expected = f.getFieldValue();
                                break;
                            case "notes":
                                notes = f.getFieldValue();
                                break;
                            default:
                                LOG.log(Level.SEVERE,
                                        "Unexpected field name: {0}",
                                        f.getFieldName());
                        }
                    }
                    HorizontalLayout attachments = new HorizontalLayout();
                    if (!es.getExecutionStepHasAttachmentList().isEmpty()) {
                        es.getExecutionStepHasAttachmentList().forEach(esha -> {
                            Label temp = new Label();
                            temp.setIcon(VaadinIcons.PAPERCLIP);
                            attachments.addComponent(temp);
                        });
                    }
                    if (!es.getExecutionStepHasIssueList().isEmpty()) {
                        Label temp = new Label();
                        temp.setIcon(VaadinIcons.BUG);
                        attachments.addComponent(temp);
                    }
                    Row stepRow = Row.ofStep("" + i, text, notes, expected,
                            es.getResultId() == null
                            ? TRANSLATOR.translate("result.pending")
                            : TRANSLATOR.translate(es.getResultId()
                            .getResultName()),
                            attachments, tester,
                            es.getExecutionStart() == null ? ""
                            : format.format(es.getExecutionStart()),
                            es.getExecutionEnd() == null ? ""
                            : format.format(es.getExecutionEnd()),
                            reviewer,
                            es.getReviewDate() == null ? ""
                            : format.format(es.getReviewDate()));
                    treeData.addItem(caseRows.get(es.getStep().getTestCase()
                            .getTestCasePK()), stepRow);
                }
            }
        }
        summary.setDataProvider(new TreeDataProvider<>(treeData));
        summary.expand(treeData.getRootItems());
        return getExportWindow(summary, executions, tcID);
    }

    private static void addColumns(TreeGrid<Row> summary) {
        summary.addColumn(row -> row.testCase)
                .setId("general.test.case")
                .setCaption(TRANSLATOR.translate("general.test.case"));
        summary.addColumn(row -> row.sequence)
                .setId("general.sequence")
                .setCaption(TRANSLATOR.translate("general.sequence"));
        summary.addColumn(row -> row.text)
                .setId("general.text")
                .setCaption(TRANSLATOR.translate("general.text"));
        summary.addColumn(row -> row.notes)
                .setId("general.notes")
                .setCaption(TRANSLATOR.translate("general.notes"));
        summary.addColumn(row -> row.expectedResult)
                .setId("expected.result")
                .setCaption(TRANSLATOR.translate("expected.result"));
        summary.addColumn(row -> row.result)
                .setId("general.result")
                .setCaption(TRANSLATOR.translate("general.result"));
        summary.addComponentColumn(row -> row.attachments == null
                ? new HorizontalLayout() : row.attachments)
                .setId("general.attachment")
                .setCaption(TRANSLATOR.translate("general.attachment"));
        summary.addColumn(row -> row.tester)
                .setId("tester.desc")
                .setCaption(TRANSLATOR.translate("tester.desc"));
        summary.addColumn(row -> row.startDate)
                .setId("start.date")
                .setCaption(TRANSLATOR.translate("start.date"));
        summary.addColumn(row -> row.endDate)
                .setId("end.date")
                .setCaption(TRANSLATOR.translate("end.date"));
        summary.addColumn(row -> row.reviewer)
                .setId("general.reviewer")
                .setCaption(TRANSLATOR.translate("general.reviewer"));
        summary.addColumn(row -> row.reviewDate)
                .setId("review.date")
                .setCaption(TRANSLATOR.translate("review.date"));
    }

    private static Window getExportWindow(TreeGrid<Row> summary,
            List<TestCaseExecutionServer> executions, int tcID) {
        VMWindow w = new VMWindow(TRANSLATOR.translate("general.export"));
        VerticalLayout vl = new VerticalLayout();
        summary.setSizeFull();
        vl.addComponent(summary);
        Button export = new Button(TRANSLATOR.translate("general.export"));
        List<File> attachments = new ArrayList<>();
        export.addClickListener(listener -> {
            //Hide the attachment column as it doesn't work well on the export.
            summary.getColumns().forEach(col -> {
                if (TRANSLATOR.translate("general.attachment")
                        .equals(col.getCaption())) {
                    col.setHidden(true);
                }
            });
            String basePath = VaadinService.getCurrent()
                    .getBaseDirectory().getAbsolutePath()
                    + File.separator
                    + "VAADIN"
                    + File.separator
                    + "temp"
                    + File.separator;
            if (executions != null) {
                //Also send the attachments
                executions.forEach(execution -> {
                    execution.getExecutionStepList().forEach(es -> {
                        if (tcID < 0
                                || es.getExecutionStepPK().getStepTestCaseId() == tcID) {
                            es.getExecutionStepHasAttachmentList().forEach(esha -> {
                                AttachmentServer as
                                        = new AttachmentServer(esha
                                                .getAttachment()
                                                .getAttachmentPK());
                                File f = null;
                                switch (esha.getAttachment().getAttachmentType().getType()) {
                                    case "comment": //Create a pdf version of the comment
                                        try {
                                            String fileName = basePath
                                                    + TRANSLATOR.translate("general.test.case")
                                                    + "-"
                                                    + es.getStep().getTestCase().getName()
                                                    + "-"
                                                    + TRANSLATOR.translate("general.comment")
                                                    + "-"
                                                    + TRANSLATOR.translate("general.step")
                                                    + "-"
                                                    + es.getStep().getStepSequence()
                                                    + ".pdf";
                                            f = Tool.convertToPDF(as
                                                    .getTextValue(), fileName);
                                        } catch (IOException ex) {
                                            Exceptions.printStackTrace(ex);
                                        }
                                        break;
                                    default:
                                        File temp = as.getAttachedFile(basePath);
                                        f = new File(basePath
                                                + TRANSLATOR.translate("general.test.case")
                                                + "-"
                                                + es.getStep().getTestCase().getName()
                                                + "-"
                                                + TRANSLATOR.translate("general.attachment")
                                                + "-"
                                                + temp.getName());
                                        temp.renameTo(temp);
                                }
                                if (f != null) {
                                    attachments.add(f);
                                }
                            });
                        }
                        if (es.getExecutionStepHasIssueList() != null) {
                            es.getExecutionStepHasIssueList().forEach(eshi -> {
                                try {
                                    Issue i = eshi.getIssue();
                                    String fileName = basePath
                                            + TRANSLATOR.translate("general.test.case")
                                            + "-"
                                            + es.getStep().getTestCase().getName()
                                            + "-"
                                            + TRANSLATOR.translate("general.issue")
                                            + "-"
                                            + TRANSLATOR.translate("general.step")
                                            + "-"
                                            + es.getStep().getStepSequence()
                                            + ".pdf";
                                    //Create a string version of the issue
                                    StringBuilder sb = new StringBuilder();
                                    sb.append(TRANSLATOR.translate("general.summary"))
                                            .append(i.getTitle())
                                            .append('\n')
                                            .append(TRANSLATOR.translate("issue.type"))
                                            .append(':')
                                            .append(TRANSLATOR.translate(i
                                                    .getIssueType().getTypeName()))
                                            .append('\n')
                                            .append(TRANSLATOR.translate("creation.time"))
                                            .append(':')
                                            .append(i.getCreationTime())
                                            .append('\n')
                                            .append(TRANSLATOR.translate("issue.detail"))
                                            .append(':')
                                            .append(i.getDescription())
                                            .append('\n');
                                    if (i.getIssueResolutionId() != null) {
                                        sb.append(TRANSLATOR.translate("issue.resolution"))
                                                .append(':')
                                                .append(TRANSLATOR.translate(i
                                                        .getIssueResolutionId().getName()))
                                                .append('\n');
                                    }
                                    attachments.add(Tool.convertToPDF(sb.toString(),
                                            fileName));
                                } catch (IOException ex) {
                                    Exceptions.printStackTrace(ex);
                                }
                            });
                        }
                    });
                });
            }
            if (!attachments.isEmpty()) {
                try {
                    File attachment = Tool.createZipFile(attachments,
                            basePath + "Attachments.zip");
                    LOG.log(Level.FINE, "Downloading: {0}",
                            attachment.getAbsolutePath());
                    ((VMUI) UI.getCurrent()).sendConvertedFileToUser(UI.getCurrent(),
                            attachment, attachment.getName(),
                            Tool.getMimeType(attachment));
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, "Error downloading attachments!", ex);
                }
            }
            //Create the Excel file
            ExcelExport excelExport = new ExcelExport(
                    new DefaultGridHolder(summary));
            excelExport.excludeCollapsedColumns();
            excelExport.setReportTitle(TRANSLATOR.translate("general.export"));
            excelExport.setDisplayTotals(false);
            excelExport.export();
            UI.getCurrent().removeWindow(w);
        });
        vl.addComponent(export);
        w.setContent(vl);
        w.setSizeFull();
        return w;
    }
}
