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

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import java.util.Collection;
import static net.sourceforge.javydreamercsw.validation.manager.web.core.ContentProvider.TRANSLATOR;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openide.util.Exceptions;

/**
 * Test case/execution export. The tableexport add-on has no Flow port, so the
 * XLSX is generated with Apache POI and offered as a {@link StreamResource}
 * download.
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

    public static VMWindow getTestCaseExporter(List<TestCase> testCases) {
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
        return getExportWindow(summary, treeData, null, -1);
    }

    public static VMWindow getExecutionExporter(List<TestCaseExecutionServer> executions,
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
                            Icon temp = new Icon(VaadinIcon.PAPERCLIP);
                            attachments.add(temp);
                        });
                    }
                    if (!es.getExecutionStepHasIssueList().isEmpty()) {
                        Icon temp = new Icon(VaadinIcon.BUG);
                        attachments.add(temp);
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
        return getExportWindow(summary, treeData, executions, tcID);
    }

    private static void addColumns(TreeGrid<Row> summary) {
        summary.addColumn(row -> row.testCase)
                .setKey("general.test.case")
                .setHeader(TRANSLATOR.translate("general.test.case"));
        summary.addColumn(row -> row.sequence)
                .setKey("general.sequence")
                .setHeader(TRANSLATOR.translate("general.sequence"));
        summary.addColumn(row -> row.text)
                .setKey("general.text")
                .setHeader(TRANSLATOR.translate("general.text"));
        summary.addColumn(row -> row.notes)
                .setKey("general.notes")
                .setHeader(TRANSLATOR.translate("general.notes"));
        summary.addColumn(row -> row.expectedResult)
                .setKey("expected.result")
                .setHeader(TRANSLATOR.translate("expected.result"));
        summary.addColumn(row -> row.result)
                .setKey("general.result")
                .setHeader(TRANSLATOR.translate("general.result"));
        summary.addComponentColumn(row -> row.attachments == null
                ? new HorizontalLayout() : row.attachments)
                .setKey("general.attachment")
                .setHeader(TRANSLATOR.translate("general.attachment"));
        summary.addColumn(row -> row.tester)
                .setKey("tester.desc")
                .setHeader(TRANSLATOR.translate("tester.desc"));
        summary.addColumn(row -> row.startDate)
                .setKey("start.date")
                .setHeader(TRANSLATOR.translate("start.date"));
        summary.addColumn(row -> row.endDate)
                .setKey("end.date")
                .setHeader(TRANSLATOR.translate("end.date"));
        summary.addColumn(row -> row.reviewer)
                .setKey("general.reviewer")
                .setHeader(TRANSLATOR.translate("general.reviewer"));
        summary.addColumn(row -> row.reviewDate)
                .setKey("review.date")
                .setHeader(TRANSLATOR.translate("review.date"));
    }

    private static VMWindow getExportWindow(TreeGrid<Row> summary,
            TreeData<Row> treeData,
            List<TestCaseExecutionServer> executions, int tcID) {
        VMWindow w = new VMWindow(TRANSLATOR.translate("general.export"));
        VerticalLayout vl = new VerticalLayout();
        summary.setSizeFull();
        vl.add(summary);
        List<File> attachments = new ArrayList<>();
        if (executions != null) {
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
                                        String fileName = basePath()
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
                                    File temp = as.getAttachedFile(basePath());
                                    f = new File(basePath()
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
                                String fileName = basePath()
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
                        basePath() + "Attachments.zip");
                LOG.log(Level.FINE, "Offering download: {0}",
                        attachment.getAbsolutePath());
                //Zip of the attachments, offered as a download link.
                vl.add(downloadAnchor(attachment.getName(), attachment));
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Error preparing attachments!", ex);
            }
        }
        //Create the Excel file (the tableexport add-on has no Flow port; the
        //workbook is generated with POI and offered as a StreamResource).
        Anchor export = new Anchor(new StreamResource(
                TRANSLATOR.translate("general.export") + ".xlsx",
                () -> {
                    try {
                        return new ByteArrayInputStream(toXlsx(summary, treeData));
                    } catch (IOException ex) {
                        LOG.log(Level.SEVERE, "Error generating export!", ex);
                        return new ByteArrayInputStream(new byte[0]);
                    }
                }),
                TRANSLATOR.translate("general.export"));
        export.getElement().setAttribute("download", true);
        vl.add(export);
        w.add(vl);
        return w;
    }

    private static String basePath() {
        return ((jakarta.servlet.ServletContext) com.vaadin.flow.server.VaadinService
                .getCurrent().getContext()
                .getAttribute(jakarta.servlet.ServletContext.class))
                .getRealPath("")
                + File.separator
                + "VAADIN"
                + File.separator
                + "temp"
                + File.separator;
    }

    /**
     * Wrap a file as a downloadable anchor.
     */
    private static Anchor downloadAnchor(String fileName, File file) {
        byte[] data;
        try {
            data = java.nio.file.Files.readAllBytes(file.toPath());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error reading file for download!", ex);
            data = new byte[0];
        }
        final byte[] resourceData = data;
        StreamResource resource = new StreamResource(fileName,
                () -> new ByteArrayInputStream(resourceData));
        try {
            resource.setContentType(Tool.getMimeType(file));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Unable to resolve mime type!", ex);
        }
        Anchor anchor = new Anchor(resource, fileName);
        anchor.getElement().setAttribute("download", true);
        return anchor;
    }

    /**
     * Flatten the visible grid columns into an XLSX workbook, walking the
     * export tree depth first.
     */
    private static byte[] toXlsx(TreeGrid<Row> summary, TreeData<Row> treeData)
            throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook();
                ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet(TRANSLATOR.translate("general.export"));
            List<Grid.Column<Row>> cols = new ArrayList<>();
            summary.getColumns().forEach(col -> {
                if (col.isVisible()) {
                    cols.add(col);
                }
            });
            org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
            int ci = 0;
            for (Grid.Column<Row> col : cols) {
                Cell cell = header.createCell(ci);
                cell.setCellValue(col.getHeaderText() == null ? ""
                        : col.getHeaderText());
                ci++;
            }
            int[] rowIdx = {1};
            for (Row root : treeData.getRootItems()) {
                writeRow(sheet, cols, rowIdx, root);
                writeChildren(treeData, root, sheet, cols, rowIdx);
            }
            wb.write(bos);
            return bos.toByteArray();
        }
    }

    private static void writeChildren(TreeData<Row> treeData, Row parent,
            Sheet sheet, List<Grid.Column<Row>> cols, int[] rowIdx) {
        Collection<Row> children = treeData.getChildren(parent);
        if (children != null) {
            children.forEach(child -> {
                writeRow(sheet, cols, rowIdx, child);
                writeChildren(treeData, child, sheet, cols, rowIdx);
            });
        }
    }

    private static void writeRow(Sheet sheet, List<Grid.Column<Row>> cols,
            int[] rowIdx, Row row) {
        org.apache.poi.ss.usermodel.Row sheetRow = sheet.createRow(rowIdx[0]++);
        int ci = 0;
        for (Grid.Column<Row> col : cols) {
            Cell cell = sheetRow.createCell(ci);
            cell.setCellValue(cellValue(col, row));
            ci++;
        }
    }

    private static String cellValue(Grid.Column<Row> col, Row row) {
        switch (col.getKey()) {
            case "general.test.case":
                return row.testCase;
            case "general.sequence":
                return row.sequence;
            case "general.text":
                return row.text;
            case "general.notes":
                return row.notes;
            case "expected.result":
                return row.expectedResult;
            case "general.result":
                return row.result;
            case "tester.desc":
                return row.tester;
            case "start.date":
                return row.startDate;
            case "end.date":
                return row.endDate;
            case "general.reviewer":
                return row.reviewer;
            case "review.date":
                return row.reviewDate;
            default: //attachment column
                return "";
        }
    }
}
