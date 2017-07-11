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
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import static com.validation.manager.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.db.DataEntry;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.ExecutionStepHasVmUser;
import com.validation.manager.core.db.HistoryField;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.server.core.TestCaseExecutionServer;
import com.validation.manager.core.server.core.VMSettingServer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import net.sourceforge.javydreamercsw.validation.manager.web.VMWindow;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class TestCaseExporter {

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
                if (!summary.containsId(step.getTestCase().getId())) {
                    summary.addItem(new Object[]{step
                        .getTestCase().getName(),
                        "", "", "", "", ""},
                            step.getTestCase().getId());
                }
                //Add the step
                String stepId = step.getTestCase().getId() + "."
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
                        step.getTestCase().getId());
                //Add the fields of the test case
                for (DataEntry de : step.getDataEntryList()) {
                    String fieldId = step.getTestCase().getId() + "."
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
        return getExportWindow(summary);
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
                    if (!summary.containsId(es.getStep().getTestCase().getId())) {
                        summary.addItem(new Object[]{es.getStep()
                            .getTestCase().getName(),
                            "", "", "", "", "", new HorizontalLayout(), "", "",
                            "", "", ""},
                                es.getStep().getTestCase().getId());
                    }
                    //Add the step
                    //First calculate the sequence number
                    Collection c = summary.getChildren(es.getStep()
                            .getTestCase().getId());
                    int i = c == null ? 1 : c.size() + 1;
                    String stepId = es.getStep().getTestCase().getId() + "."
                            + i;
                    //Calculate the fields from History
                    SimpleDateFormat format = new SimpleDateFormat(
                            VMSettingServer.getSetting("date.format")
                                    .getStringVal());
                    String text = "", notes = "", expected = "",
                            tester = "", reviewer = "";
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
                            es.getStep().getTestCase().getId());
                    //Mark test case as a leaf
                    summary.setChildrenAllowed(stepId, false);
                }
            }
        }
        return getExportWindow(summary);
    }

    private static Window getExportWindow(TreeTable summary) {
        VMWindow w = new VMWindow(TRANSLATOR.translate("general.export"));
        VerticalLayout vl = new VerticalLayout();
        summary.setSizeFull();
        vl.addComponent(summary);
        Button export = new Button(TRANSLATOR.translate("general.export"));
        export.addClickListener(listener -> {
            if (ArrayUtils.contains(summary.getColumnHeaders(),
                    TRANSLATOR.translate("general.attachment"))) {
                //Hide the attachment column as it doesn't work well on the export.
                summary.setColumnCollapsingAllowed(true);
                summary.setColumnCollapsed(TRANSLATOR
                        .translate("general.attachment"), true);
            }
            //Create the Excel file
            ExcelExport excelExport = new ExcelExport(summary);
            excelExport.excludeCollapsedColumns();
            excelExport.setReportTitle(TRANSLATOR.translate("general.export"));
            excelExport.setDisplayTotals(false);
            excelExport.export();
            //TODO: Also send the attachments

            //
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
