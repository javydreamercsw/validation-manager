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

import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TreeTable;
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
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class TestCaseExporter {

    private static final Logger LOG
            = Logger.getLogger(TestCaseExporter.class.getSimpleName());

    public static Window getTestCaseExporter(List<TestCase> testCases) {
        TreeTable summary = new TreeTable();
        summary.addContainerProperty(TRANSLATOR.translate("general.test.case"),
                String.class, "");
        summary.addContainerProperty(TRANSLATOR.translate("general.sequence"),
                String.class, "");
        summary.addContainerProperty(TRANSLATOR.translate("general.text"),
                String.class, "");
        summary.addContainerProperty(TRANSLATOR.translate("general.notes"),
                String.class, "");
        summary.addContainerProperty(TRANSLATOR.translate("expected.result"),
                String.class, "");
        summary.addContainerProperty(TRANSLATOR.translate("expected.result"),
                String.class, "");
        summary.addContainerProperty(TRANSLATOR.translate("general.result"),
                String.class, "");
        testCases.forEach(tc -> {
            for (Step step : tc.getStepList()) {
                //Add test case if not there already
                if (!summary.containsId(step.getTestCase().getTestCasePK())) {
                    summary.addItem(new Object[]{step
                        .getTestCase().getName(),
                        "", "", "", "", ""},
                            step.getTestCase().getTestCasePK());
                }
                //Add the step
                String stepId = step.getTestCase().getTestCasePK().getId() + "."
                        + step.getStepSequence();
                String text = new String(step.getText(), StandardCharsets.UTF_8),
                        notes = step.getNotes(),
                        expected = new String(step.getExpectedResult(),
                                StandardCharsets.UTF_8);
                summary.addItem(new Object[]{"", ""
                    + step.getStepSequence(),//Sequence
                    text, //Text
                    notes, //Notes
                    expected, //Expected Result
                    ""},
                        stepId);
                //Put step under the test case
                summary.setParent(stepId,
                        step.getTestCase().getTestCasePK());
                //Add the fields of the test case
                for (DataEntry de : step.getDataEntryList()) {
                    String fieldId = step.getTestCase().getTestCasePK() + "."
                            + step.getStepSequence()
                            + "" + (summary.getChildren(stepId) == null ? 0
                            : summary.getChildren(stepId).size());
                    summary.addItem(new Object[]{"", "",//Sequence
                        "", //Text
                        "", //Notes
                        "", //Expected Result
                        de.getEntryName()},//Field
                            fieldId);
                    summary.setParent(fieldId, stepId);
                    //Mark test case as a leaf
                    summary.setChildrenAllowed(fieldId, false);
                }
            }
        });
        summary.setSizeFull();
        return getExportWindow(summary, null, -1);
    }

    public static Window getExecutionExporter(List<TestCaseExecutionServer> executions,
            int tcID) {
        TreeTable summary = new TreeTable();
        summary.addContainerProperty(TRANSLATOR.translate("general.test.case"),
                String.class, "");
        summary.addContainerProperty(TRANSLATOR.translate("general.sequence"),
                String.class, "");
        summary.addContainerProperty(TRANSLATOR.translate("general.text"),
                String.class, "");
        summary.addContainerProperty(TRANSLATOR.translate("general.notes"),
                String.class, "");
        summary.addContainerProperty(TRANSLATOR.translate("expected.result"),
                String.class, "");
        summary.addContainerProperty(TRANSLATOR.translate("general.result"),
                String.class, "");
        summary.addContainerProperty(TRANSLATOR.translate("general.attachment"),
                HorizontalLayout.class, new HorizontalLayout());
        summary.addContainerProperty(TRANSLATOR.translate("tester.desc"),
                String.class, "");
        summary.addContainerProperty(TRANSLATOR.translate("start.date"),
                String.class, "");
        summary.addContainerProperty(TRANSLATOR.translate("end.date"),
                String.class, "");
        summary.addContainerProperty(TRANSLATOR.translate("general.reviewer"),
                String.class, "");
        summary.addContainerProperty(TRANSLATOR.translate("review.date"),
                String.class, "");
        for (TestCaseExecutionServer execution : executions) {
            for (ExecutionStep es : execution.getExecutionStepList()) {
                if (tcID < 0
                        || es.getExecutionStepPK().getStepTestCaseId() == tcID) {
                    //Add test case if not there already
                    if (!summary.containsId(es.getStep().getTestCase().getTestCasePK())) {
                        summary.addItem(new Object[]{es.getStep()
                            .getTestCase().getName(),
                            "", "", "", "", "", new HorizontalLayout(), "", "",
                            "", "", ""},
                                es.getStep().getTestCase().getTestCasePK());
                    }
                    //Add the step
                    //First calculate the sequence number
                    Collection c = summary.getChildren(es.getStep()
                            .getTestCase().getTestCasePK());
                    int i = c == null ? 1 : c.size() + 1;
                    String stepId = es.getStep().getTestCase().getTestCasePK() + "."
                            + i;
                    //Calculate the fields from History
                    SimpleDateFormat format = new SimpleDateFormat(
                            VMSettingServer.getSetting("date.format")
                                    .getStringVal());
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
                            switch (esha.getAttachment().getAttachmentType().getType()) {
                                case "comment":
                                    temp.setIcon(VaadinIcons.PAPERCLIP);
                                    attachments.addComponent(temp);
                                    break;
                                default:
                                    temp.setIcon(VaadinIcons.PAPERCLIP);
                                    attachments.addComponent(temp);
                                    break;
                            }
                        });
                    }
                    if (!es.getExecutionStepHasIssueList().isEmpty()) {
                        Label temp = new Label();
                        temp.setIcon(VaadinIcons.BUG);
                        attachments.addComponent(temp);
                    }
                    summary.addItem(new Object[]{"", "" + i,//Sequence
                        text, //Text
                        notes, //Notes
                        expected, //Expected Result
                        es.getResultId() == null
                        ? TRANSLATOR.translate("result.pending")
                        : TRANSLATOR.translate(es.getResultId()
                        .getResultName()), //Result
                        attachments,//Attachments, issues and comments
                        tester,//Tester
                        es.getExecutionStart() == null ? ""
                        : format.format(es.getExecutionStart()), //Start Date
                        es.getExecutionEnd() == null ? ""
                        : format.format(es.getExecutionEnd()),//End Date
                        reviewer,//Reviewer
                        es.getReviewDate() == null ? ""
                        : format.format(es.getReviewDate())},//Review Date
                            stepId);
                    //Put step under the test case
                    summary.setParent(stepId,
                            es.getStep().getTestCase().getTestCasePK());
                    //Mark test case as a leaf
                    summary.setChildrenAllowed(stepId, false);
                }
            }
        }
        return getExportWindow(summary, executions, tcID);
    }

    private static Window getExportWindow(TreeTable summary,
            List<TestCaseExecutionServer> executions, int tcID) {
        VMWindow w = new VMWindow(TRANSLATOR.translate("general.export"));
        VerticalLayout vl = new VerticalLayout();
        summary.setSizeFull();
        vl.addComponent(summary);
        Button export = new Button(TRANSLATOR.translate("general.export"));
        List<File> attachments = new ArrayList<>();
        export.addClickListener(listener -> {
            if (ArrayUtils.contains(summary.getColumnHeaders(),
                    TRANSLATOR.translate("general.attachment"))) {
                //Hide the attachment column as it doesn't work well on the export.
                summary.setColumnCollapsingAllowed(true);
                summary.setColumnCollapsed(TRANSLATOR
                        .translate("general.attachment"), true);
            }
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
            ExcelExport excelExport = new ExcelExport(summary);
            excelExport.excludeCollapsedColumns();
            excelExport.setReportTitle(TRANSLATOR.translate("general.export"));
            excelExport.setDisplayTotals(false);
            excelExport.export();
            UI.getCurrent().removeWindow(w);
        });
        vl.addComponent(export);
        summary.getItemIds().forEach(id -> {
            summary.setCollapsed(id, false);
            summary.getChildren(id).forEach(child -> {
                summary.setCollapsed(child, false);
            });
        });
        w.setContent(vl);
        w.setSizeFull();
        return w;
    }
}
