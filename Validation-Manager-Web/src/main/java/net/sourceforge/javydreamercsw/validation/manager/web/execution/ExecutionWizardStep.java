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
package net.sourceforge.javydreamercsw.validation.manager.web.execution;

import static net.sourceforge.javydreamercsw.validation.manager.web.core.ContentProvider.TRANSLATOR;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jodconverter.OfficeDocumentConverter;
import org.jodconverter.office.LocalOfficeManager;
import org.jodconverter.office.OfficeException;
import org.jodconverter.office.OfficeManager;
import org.openide.util.Lookup;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileData;
import com.vaadin.flow.component.upload.receivers.MultiFileBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import com.validation.manager.core.db.AttachmentType;
import com.validation.manager.core.db.DataEntryProperty;
import com.validation.manager.core.db.ExecutionResult;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.ExecutionStepAnswer;
import com.validation.manager.core.db.ExecutionStepHasAttachment;
import com.validation.manager.core.db.ExecutionStepHasIssue;
import com.validation.manager.core.db.IssueType;
import com.validation.manager.core.db.ReviewResult;
import com.validation.manager.core.db.controller.ExecutionResultJpaController;
import com.validation.manager.core.db.controller.IssueTypeJpaController;
import com.validation.manager.core.db.controller.ReviewResultJpaController;
import com.validation.manager.core.server.core.AttachmentServer;
import com.validation.manager.core.server.core.AttachmentTypeServer;
import com.validation.manager.core.server.core.DataEntryServer;
import com.validation.manager.core.server.core.ExecutionResultServer;
import com.validation.manager.core.server.core.ExecutionStepServer;
import com.validation.manager.core.server.core.IssueServer;
import com.validation.manager.core.server.core.ReviewResultServer;
import com.validation.manager.core.server.core.VMSettingServer;

import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import net.sourceforge.javydreamercsw.validation.manager.web.component.ByteToStringConverter;
import net.sourceforge.javydreamercsw.validation.manager.web.component.VMWindow;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.FlowWizard;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.FlowWizardStep;
import net.sourceforge.javydreamercsw.validation.manager.web.file.IFileDisplay;
import net.sourceforge.javydreamercsw.validation.manager.web.file.PDFDisplay;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ExecutionWizardStep implements FlowWizardStep {

    private final FlowWizard w;
    private final ExecutionStepServer step;
    private final ComboBox<String> result
            = new ComboBox<>(TRANSLATOR.translate("general.result"));
    private final ComboBox<String> review
            = new ComboBox<>(TRANSLATOR.translate("quality.review"));
    private final ComboBox<IssueType> issueType
            = new ComboBox<>(TRANSLATOR.translate("issue.type"));
    private Button attach;
    private Button bug;
    private Button comment;
    private DateTimePicker start;
    private DateTimePicker end;
    private DateTimePicker reviewDate;
    // TODO: (phase-4b-2) FlowWizard has no re-render API; content rebuilds
    // when the step is re-entered.
    private void refreshStep() {
    }

    private static final Logger LOG
            = Logger.getLogger(ExecutionWizardStep.class.getSimpleName());
    private boolean reviewer = false;
    private final List<HasValue<?, ?>> fields = new ArrayList<>();
    //v8 AbstractComponent.setData/getData has no Flow equivalent; keep the
    //field name per field component here instead.
    private final Map<Component, String> fieldData = new HashMap<>();

    public ExecutionWizardStep(FlowWizard w, ExecutionStep step,
            boolean reviewer) {
        this.reviewer = reviewer;
        this.w = w;
        this.step = new ExecutionStepServer(step);
        issueType.setSizeFull();
        issueType.setReadOnly(false);
        issueType.setRequiredIndicatorVisible(true);
        List<IssueType> issueTypes = new ArrayList<>();
        IssueTypeJpaController it
                = new IssueTypeJpaController(DataBaseManager
                        .getEntityManagerFactory());
        it.findIssueTypeEntities().forEach(type -> {
            issueTypes.add(type);
            if (type.getTypeName().equals("observation.name")) {
                issueType.setValue(type);
            }
        });
        issueType.setItems(issueTypes);
        issueType.setItemLabelGenerator(t -> Lookup.getDefault()
                .lookup(InternationalizationProvider.class)
                .translate(t.getTypeName()));
        result.setReadOnly(false);
        result.setRequiredIndicatorVisible(true);
        result.setAllowCustomValue(false);
        review.setReadOnly(false);
        review.setRequiredIndicatorVisible(true);
        review.setAllowCustomValue(false);
        ReviewResultJpaController c2
                = new ReviewResultJpaController(DataBaseManager
                        .getEntityManagerFactory());
        List<String> reviewResults = new ArrayList<>();
        c2.findReviewResultEntities().forEach(r -> {
            reviewResults.add(r.getReviewName());
        });
        review.setItems(reviewResults);
        review.setItemLabelGenerator(item -> Lookup.getDefault()
                .lookup(InternationalizationProvider.class)
                .translate(item));
        ExecutionResultJpaController c
                = new ExecutionResultJpaController(DataBaseManager
                        .getEntityManagerFactory());
        List<String> executionResults = new ArrayList<>();
        c.findExecutionResultEntities().forEach(r -> {
            executionResults.add(r.getResultName());
        });
        result.setItems(executionResults);
        result.setItemLabelGenerator(item -> Lookup.getDefault()
                .lookup(InternationalizationProvider.class)
                .translate(item));
    }

    @Override
    public String getCaption() {
        return getExecutionStep().getStep().getTestCase().getName() + " "
                + TRANSLATOR.translate("general.step") + ":"
                + getExecutionStep().getStep().getStepSequence();
    }

    @Override
    public Component getContent() {
        //v8 Panel with a light FormLayout; Flow: plain FormLayout (Panel is
        //gone). The caption is carried by the wizard's step header.
        FormLayout layout = new FormLayout();
        if (getExecutionStep().getExecutionStart() == null) {
            //Set the start date.
            getExecutionStep().setExecutionStart(new Date());
        }
        Binder<Step> binder = new Binder<>(Step.class);
        binder.setBean(getExecutionStep().getStep());
        binder.setReadOnly(true);
        TextArea text = new TextArea(TRANSLATOR.translate("general.text"));
        binder.forField(text)
                .withConverter(new ByteToStringConverter())
                .bind("text");
        text.setSizeFull();
        layout.add(text);
        TextField notes = new TextField(TRANSLATOR.translate("general.notes"));
        binder.bind(notes, "notes");
        notes.setSizeFull();
        layout.add(notes);
        if (getExecutionStep().getExecutionStart() != null) {
            start = new DateTimePicker(TRANSLATOR.translate("start.date"));
            start.setLocale(com.vaadin.flow.component.UI.getCurrent() == null
                    ? java.util.Locale.getDefault()
                    : com.vaadin.flow.component.UI.getCurrent().getLocale());
            start.setValue(toDateTime(getExecutionStep().getExecutionStart()));
            start.setReadOnly(true);
            layout.add(start);
        }
        if (getExecutionStep().getExecutionEnd() != null) {
            end = new DateTimePicker(TRANSLATOR.translate("end.date"));
            end.setValue(toDateTime(getExecutionStep().getExecutionEnd()));
            end.setReadOnly(true);
            layout.add(end);
        }
        //Space to record result
        if (getExecutionStep().getResultId() != null) {
            result.setValue(getExecutionStep().getResultId().getResultName());
        }
        layout.add(result);
        if (reviewer) {//Space to record review
            if (getExecutionStep().getReviewResultId() != null) {
                review.setValue(getExecutionStep().getReviewResultId().getReviewName());
            }
            layout.add(review);
        }
        //Add Reviewer name
        if (getExecutionStep().getReviewer() != null) {
            TextField reviewerField = new TextField(TRANSLATOR
                    .translate("general.reviewer"));
            reviewerField.setValue(getExecutionStep().getReviewer().getFirstName() + " "
                    + getExecutionStep().getReviewer().getLastName());
            reviewerField.setReadOnly(true);
            layout.add(reviewerField);
        }
        if (getExecutionStep().getReviewDate() != null) {
            reviewDate = new DateTimePicker(TRANSLATOR
                    .translate("review.date"));
            reviewDate.setValue(toDateTime(getExecutionStep().getReviewDate()));
            reviewDate.setReadOnly(true);
            layout.add(reviewDate);
        }
        if (VMSettingServer.getSetting("show.expected.result").getBoolVal()) {
            TextArea expectedResult
                    = new TextArea(TRANSLATOR.translate("expected.result"));
            binder.forField(expectedResult)
                    .withConverter(new ByteToStringConverter())
                    .bind("expectedResult");
            expectedResult.setSizeFull();
            layout.add(expectedResult);
        }
        //Add the fields
        fields.clear();
        fieldData.clear();
        getExecutionStep().getStep().getDataEntryList().forEach(de -> {
            switch (de.getDataEntryType().getId()) {
                case 1://String
                    TextField tf = new TextField(TRANSLATOR
                            .translate(de.getEntryName()));
                    tf.setRequiredIndicatorVisible(DataEntryServer
                            .getProperty(de,
                                    "property.required")
                            .getPropertyValue().equals("true"));
                    fieldData.put(tf, de.getEntryName());
                    if (VMSettingServer.getSetting("show.expected.result")
                            .getBoolVal()) {
                        //Add expected result
                        DataEntryProperty stringCase = DataEntryServer
                                .getProperty(de, "property.match.case");
                        DataEntryProperty r = DataEntryServer
                                .getProperty(de, "property.expected.result");
                        if (r != null
                                && !r.getPropertyValue().equals("null")) {
                            String error = TRANSLATOR.translate("expected.result") + ": "
                                    + r.getPropertyValue();
                            if (stringCase != null
                                    && stringCase.getPropertyValue().equals("true")
                                    ? !tf.getValue().equals(r.getPropertyValue())
                                    : !tf.getValue().equalsIgnoreCase(r.getPropertyValue())) {
                                //We have an expected result and a match case requirement
                                tf.setErrorMessage(error);
                                tf.setInvalid(true);
                            }
                        }
                    }
                    fields.add(tf);
                    //Set value if already recorded
                    updateValue(tf);
                    layout.add(tf);
                    break;
                case 2://Numeric
                    // numberfield7 addon has no Vaadin 8 release; use a TextField
                    // with a converter and range validation.
                    TextField nf = new TextField(
                            TRANSLATOR.translate(de.getEntryName()));
                    nf.setRequiredIndicatorVisible(DataEntryServer
                            .getProperty(de,
                                    "property.required")
                            .getPropertyValue().equals("true"));
                    fieldData.put(nf, de.getEntryName());
                    Double min = null,
                     max = null;
                    for (DataEntryProperty prop : de.getDataEntryPropertyList()) {
                        String value = prop.getPropertyValue();
                        if (prop.getPropertyName().equals("property.max")) {
                            try {
                                max = Double.parseDouble(value);
                            } catch (NumberFormatException ex) {
                                //Leave as null
                            }
                        } else if (prop.getPropertyName().equals("property.min")) {
                            try {
                                min = Double.parseDouble(value);
                            } catch (NumberFormatException ex) {
                                //Leave as null
                            }
                        }
                    }
                    //Add expected result
                    if (VMSettingServer.getSetting("show.expected.result")
                            .getBoolVal() && (min != null || max != null)) {
                        String error = TRANSLATOR
                                .translate("error.out.of.range")
                                + " "
                                + (min == null ? " "
                                        : (TRANSLATOR.translate("property.min")
                                        + ": " + min))
                                + " "
                                + (max == null ? ""
                                        : (TRANSLATOR
                                        .translate("property.max")
                                        + ": " + max));
                        nf.setErrorMessage(error);
                        nf.setInvalid(true);
                    }
                    fields.add(nf);
                    //Set value if already recorded
                    updateValue(nf);
                    layout.add(nf);
                    break;
                case 3://Boolean
                    Checkbox cb = new Checkbox(TRANSLATOR
                            .translate(de.getEntryName()));
                    fieldData.put(cb, de.getEntryName());
                    cb.setRequiredIndicatorVisible(DataEntryServer
                            .getProperty(de,
                                    "property.required")
                            .getPropertyValue().equals("true"));
                    if (VMSettingServer.getSetting("show.expected.result")
                            .getBoolVal()) {
                        DataEntryProperty r = DataEntryServer.getProperty(de,
                                "property.expected.result");
                        if (r != null) {
                            //Add expected result
                            String error = TRANSLATOR.translate("expected.result") + ": "
                                    + r.getPropertyValue();
                            if (!cb.getValue().toString().equals(r.getPropertyValue())) {
                                cb.setErrorMessage(error);
                                cb.setInvalid(true);
                            }
                        }
                    }
                    fields.add(cb);
                    //Set value if already recorded
                    updateValue(cb);
                    layout.add(cb);
                    break;
                case 4://Attachment
                    Span l = new Span(TRANSLATOR
                            .translate(de.getEntryName()));
                    layout.add(l);
                    break;
                default:
                    LOG.log(Level.SEVERE, "Unexpected field type: {0}",
                            de.getDataEntryType().getId());
            }
        });
        //Add the Attachments
        HorizontalLayout attachments = new HorizontalLayout();
        attachments.add(new Span(TRANSLATOR.translate("general.attachment")));
        HorizontalLayout comments = new HorizontalLayout();
        comments.add(new Span(TRANSLATOR.translate("general.comments")));
        HorizontalLayout issues = new HorizontalLayout();
        issues.add(new Span(TRANSLATOR.translate("general.issue")));
        int commentCounter = 0;
        int issueCounter = 0;
        for (ExecutionStepHasIssue ei : getExecutionStep().getExecutionStepHasIssueList()) {
            issueCounter++;
            Button a = new Button("Issue #" + issueCounter,
                    (com.vaadin.flow.component.ClickEvent<Button> event) -> {
                        displayIssue(new IssueServer(ei.getIssue()));
                    });
            a.setIcon(new Icon(VaadinIcon.BUG));
            a.setEnabled(!step.getLocked());
            issues.add(a);
        }
        for (ExecutionStepHasAttachment attachment
                : getExecutionStep().getExecutionStepHasAttachmentList()) {
            switch (attachment.getAttachment().getAttachmentType().getType()) {
                case "comment": {
                    //Comments go in a different section
                    commentCounter++;
                    Button a = new Button("Comment #" + commentCounter,
                            (com.vaadin.flow.component.ClickEvent<Button> event) -> {
                                if (!step.getLocked()) {
                                    //Prompt if user wants this removed
                                    getDeletionPrompt(attachment).open();
                                } else {
                                    displayComment(new AttachmentServer(attachment
                                            .getAttachment().getAttachmentPK()));
                                }
                            });
                    a.setIcon(new Icon(VaadinIcon.CLIPBOARD_TEXT));
                    a.setEnabled(!step.getLocked());
                    comments.add(a);
                    break;
                }
                default: {
                    Button a = new Button(attachment.getAttachment().getFileName());
                    a.setEnabled(!step.getLocked());
                    a.setIcon(new Icon(VaadinIcon.PAPERCLIP));
                    a.addClickListener((com.vaadin.flow.component.ClickEvent<Button> event) -> {
                        if (!step.getLocked()) {
                            //Prompt if user wants this removed
                            getDeletionPrompt(attachment).open();
                        } else {
                            displayAttachment(
                                    new AttachmentServer(attachment.getAttachment()
                                            .getAttachmentPK()));
                        }
                    });
                    attachments.add(a);
                    break;
                }
            }
        }
        if (attachments.getComponentCount() > 0) {
            layout.add(attachments);
        }
        if (comments.getComponentCount() > 0) {
            layout.add(comments);
        }
        if (issues.getComponentCount() > 0) {
            layout.add(issues);
        }
        //Add the menu
        HorizontalLayout hl = new HorizontalLayout();
        attach = new Button(TRANSLATOR.translate("add.attachment"));
        attach.setIcon(new Icon(VaadinIcon.PAPERCLIP));
        attach.addClickListener((com.vaadin.flow.component.ClickEvent<Button> event) -> {
            //Show dialog to upload file.
            Dialog dialog = new VMWindow(TRANSLATOR.translate("attach.file"));
            VerticalLayout vl = new VerticalLayout();
            MultiFileBuffer buffer = new MultiFileBuffer();
            Upload multiFileUpload = new Upload(buffer);
            multiFileUpload.addSucceededListener(succeededEvent -> {
                String fileName = succeededEvent.getFileName();
                FileData data = buffer.getFileData(fileName);
                File file = data.getFile();
                try {
                    LOG.log(Level.FINE, "Received file {1} at: {0}",
                            new Object[]{
                                file.getAbsolutePath(), fileName
                            });
                    //Process the file
                    //Create the attachment
                    AttachmentServer a = new AttachmentServer();
                    a.addFile(file, fileName);
                    //Overwrite the default file name set in addFile. It'll be a temporary file name
                    a.setFileName(fileName);
                    a.write2DB();
                    //Now add it to this Execution Step
                    if (getExecutionStep().getExecutionStepHasAttachmentList() == null) {
                        getExecutionStep().setExecutionStepHasAttachmentList(new ArrayList<>());
                    }
                    getExecutionStep().addAttachment(a);
                    getExecutionStep().write2DB();
                    refreshStep();
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, "Error creating attachment!", ex);
                }
            });
            multiFileUpload.setUploadButton(new Span(
                    TRANSLATOR.translate("select.files.attach")));
            vl.add(multiFileUpload);
            dialog.add(vl);
            dialog.setHeight("25%");
            dialog.setWidth("25%");
            ValidationManagerUI.getInstance().openDialog(dialog);
        });
        hl.add(attach);
        bug = new Button(TRANSLATOR.translate("create.issue"));
        bug.setIcon(new Icon(VaadinIcon.BUG));
        bug.addClickListener((com.vaadin.flow.component.ClickEvent<Button> event) -> {
            displayIssue(new IssueServer());
        });
        hl.add(bug);
        comment = new Button(TRANSLATOR.translate("add.comment"));
        comment.setIcon(new Icon(VaadinIcon.CLIPBOARD_TEXT));
        comment.addClickListener((com.vaadin.flow.component.ClickEvent<Button> event) -> {
            AttachmentServer as = new AttachmentServer();
            //Get comment type
            AttachmentType type = AttachmentTypeServer
                    .getTypeForExtension("comment");
            as.setAttachmentType(type);
            displayComment(as);
        });
        hl.add(comment);
        step.update();
        attach.setEnabled(!step.getLocked());
        bug.setEnabled(!step.getLocked());
        comment.setEnabled(!step.getLocked());
        result.setEnabled(!step.getLocked());
        layout.add(hl);
        return layout;
    }

    private void displayIssue(IssueServer is) {
        FormLayout layout = new FormLayout();
        if (is.getIssuePK() == null) {
            //Set creation date
            is.setCreationTime(new Date());
        }
        Binder<IssueServer> binder = new Binder<>(IssueServer.class);
        binder.setBean(is);
        TextField title = new TextField(TRANSLATOR.translate("general.summary"));
        binder.bind(title, "title");
        title.setSizeFull();
        layout.add(title);
        TextArea desc = new TextArea(TRANSLATOR.translate("general.description"));
        binder.bind(desc, "description");
        desc.setSizeFull();
        layout.add(desc);
        DateTimePicker creation = new DateTimePicker(TRANSLATOR.translate("creation.time"));
        binder.forField(creation).withConverter(
                value -> value == null ? null
                        : Date.from(value.atZone(ZoneId.systemDefault()).toInstant()),
                date -> toDateTime(date)).bind("creationTime");
        creation.setReadOnly(true);
        layout.add(creation);
        //Add the result
        layout.add(issueType);
        if (is.getIssueType() != null) {
            issueType.setValue(is.getIssueType());
        }
        //Lock if being created
        issueType.setReadOnly(is.getIssueType() == null);
        ConfirmDialog mb = new ConfirmDialog();
        mb.setHeader(TRANSLATOR.translate("issue.detail"));
        mb.add(layout);
        mb.setConfirmButton(TRANSLATOR.translate("general.ok"), (e) -> {
            try {
                IssueServer issue = is;
                issue.setDescription(desc.getValue().trim());
                issue.setIssueType(issueType.getValue());
                issue.setCreationTime(creation.getValue() == null
                        ? null
                        : Date.from(creation.getValue()
                                .atZone(ZoneId.systemDefault()).toInstant()));
                issue.setTitle(title.getValue());
                boolean toAdd = issue.getIssuePK() == null;
                issue.write2DB();
                if (toAdd) {
                    //Now add it to this Execution Step
                    if (getExecutionStep().getExecutionStepHasIssueList() == null) {
                        getExecutionStep().setExecutionStepHasIssueList(new ArrayList<>());
                    }
                    getExecutionStep().addIssue(issue, ValidationManagerUI
                            .getInstance().getUser());
                    getExecutionStep().write2DB();
                }
                refreshCurrentStep();
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        });
        mb.setCancelButton(TRANSLATOR.translate("general.cancel"), (e) -> {
            //Do nothing
        });
        //Enable the OK button only when both fields have content.
        desc.setValueChangeMode(ValueChangeMode.LAZY);
        title.setValueChangeMode(ValueChangeMode.LAZY);
        desc.addValueChangeListener(event1 -> {
            //Enable if there is a description.
            rebuildConfirmButton(mb, !step.getLocked()
                    && !desc.getValue().trim().isEmpty()
                    && !title.getValue().trim().isEmpty(),
                    () -> saveIssue(is, title, desc, creation));
        });
        title.addValueChangeListener(event1 -> {
            rebuildConfirmButton(mb, !step.getLocked()
                    && !desc.getValue().trim().isEmpty()
                    && !title.getValue().trim().isEmpty(),
                    () -> saveIssue(is, title, desc, creation));
        });
        rebuildConfirmButton(mb, !step.getLocked()
                && !desc.getValue().trim().isEmpty()
                && !title.getValue().trim().isEmpty(),
                () -> saveIssue(is, title, desc, creation));
        mb.open();
    }

    private void saveIssue(IssueServer is, TextField title, TextArea desc,
            DateTimePicker creation) {
        try {
            IssueServer issue = is;
            issue.setDescription(desc.getValue().trim());
            issue.setIssueType(issueType.getValue());
            issue.setCreationTime(creation.getValue() == null
                    ? null
                    : Date.from(creation.getValue()
                            .atZone(ZoneId.systemDefault()).toInstant()));
            issue.setTitle(title.getValue());
            boolean toAdd = issue.getIssuePK() == null;
            issue.write2DB();
            if (toAdd) {
                //Now add it to this Execution Step
                if (getExecutionStep().getExecutionStepHasIssueList() == null) {
                    getExecutionStep().setExecutionStepHasIssueList(new ArrayList<>());
                }
                getExecutionStep().addIssue(issue, ValidationManagerUI
                        .getInstance().getUser());
                getExecutionStep().write2DB();
            }
            refreshCurrentStep();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Rebuild the confirm button of a {@link ConfirmDialog} with the given
     * enabled state and action. ConfirmDialog has no getButton(), so the
     * button is reassigned each time (the Flow replacement for the v8
     * MessageBox.getButton(ButtonType.OK).setEnabled(...)).
     */
    private void rebuildConfirmButton(ConfirmDialog dialog, boolean enabled,
            Runnable action) {
        dialog.setConfirmButton(TRANSLATOR.translate("general.ok"),
                (e) -> {
                    if (enabled) {
                        action.run();
                    }
                });
    }

    private void displayComment(AttachmentServer as) {
        FormLayout layout = new FormLayout();
        Binder<AttachmentServer> binder = new Binder<>(AttachmentServer.class);
        binder.setBean(as);
        TextArea desc = new TextArea(TRANSLATOR.translate("general.text"));
        binder.bind(desc, "textValue");
        desc.setSizeFull();
        layout.add(desc);
        ConfirmDialog mb = new ConfirmDialog();
        mb.setHeader(TRANSLATOR.translate("enter.comment"));
        mb.add(layout);
        mb.setConfirmButton(TRANSLATOR.translate("general.ok"), (e) -> {
            try {
                AttachmentServer a = as;
                a.setTextValue(desc.getValue().trim());
                boolean toAdd = a.getAttachmentPK() == null;
                a.write2DB();
                if (toAdd) {
                    //Now add it to this Execution Step
                    if (getExecutionStep().getExecutionStepHasAttachmentList() == null) {
                        getExecutionStep().setExecutionStepHasAttachmentList(new ArrayList<>());
                    }
                    getExecutionStep().addAttachment(a);
                    getExecutionStep().write2DB();
                }
                refreshCurrentStep();
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        });
        mb.setCancelButton(TRANSLATOR.translate("general.cancel"), (e) -> {
            //Do nothing
        });
        desc.setValueChangeMode(ValueChangeMode.LAZY);
        desc.addValueChangeListener(event1 -> {
            //Enable only when there is a comment.
            rebuildConfirmButton(mb, !step.getLocked()
                    && !desc.getValue().trim().isEmpty(),
                    () -> saveComment(as, desc));
        });
        rebuildConfirmButton(mb, !step.getLocked()
                && !desc.getValue().trim().isEmpty(),
                () -> saveComment(as, desc));
        mb.open();
    }

    private void saveComment(AttachmentServer as, TextArea desc) {
        try {
            AttachmentServer a = as;
            a.setTextValue(desc.getValue().trim());
            boolean toAdd = a.getAttachmentPK() == null;
            a.write2DB();
            if (toAdd) {
                //Now add it to this Execution Step
                if (getExecutionStep().getExecutionStepHasAttachmentList() == null) {
                    getExecutionStep().setExecutionStepHasAttachmentList(new ArrayList<>());
                }
                getExecutionStep().addAttachment(a);
                getExecutionStep().write2DB();
            }
            refreshCurrentStep();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean onAdvance() {
        //Can only proceed after the current step is executed and documented.
        //Silent fail (no Notification.show: it fails without a UI session,
        //e.g. in unit tests).
        String answer = result.getValue();
        String answer2 = review.getValue();
        boolean pass = true;
        if (answer == null) {
            pass = false;
        } else if (reviewer && answer2 == null) {
            pass = false;
        } else {
            //Check all fields for answers
            for (HasValue<?, ?> field : fields) {
                boolean required = field.isRequiredIndicatorVisible();
                boolean empty = field.getValue() == null
                        || (field.getValue() instanceof String
                        && ((String) field.getValue()).trim().isEmpty());
                if (required && !(field instanceof Checkbox) && empty) {
                    pass = false;
                }
            }
            if (pass) {
                try {
                    //Save the result
                    ExecutionResult newResult = ExecutionResultServer
                            .getResult(answer);
                    ReviewResult newReview = ReviewResultServer.getReview(answer2);
                    getExecutionStep().setExecutionStart(toDate(start == null
                            ? null : start.getValue()));
                    if (getExecutionStep().getResultId() == null
                            || !Objects.equals(getExecutionStep().getResultId().getId(),
                                    newResult.getId())) {
                        getExecutionStep().setResultId(newResult);
                        //Set end date to null to reflect update
                        getExecutionStep().setExecutionEnd(null);
                    }
                    if (reviewer && (getExecutionStep().getReviewResultId() == null
                            || !Objects.equals(getExecutionStep()
                                    .getReviewResultId().getId(),
                                    newReview.getId()))) {
                        getExecutionStep().setReviewResultId(newReview);
                        getExecutionStep().setReviewer(ValidationManagerUI
                                .getInstance().getUser());
                    }
                    if (getExecutionStep().getExecutionEnd() == null) {
                        getExecutionStep().setExecutionEnd(new Date());
                    }
                    if (reviewer && getExecutionStep().getReviewDate() == null) {
                        getExecutionStep().setReviewDate(new Date());
                    }
                    if (getExecutionStep().getExecutionStepAnswerList() == null) {
                        getExecutionStep().setExecutionStepAnswerList(new ArrayList<>());
                    }
                    if (getExecutionStep().getExecutionStepHasVmUserList() == null) {
                        getExecutionStep().setExecutionStepHasVmUserList(new ArrayList<>());
                    }
                    getExecutionStep().getExecutionStepAnswerList().clear();
                    for (HasValue<?, ?> field : fields) {
                        //The field has the field name as data
                        if (fieldData.get((Component) field) == null) {
                            pass = false;
                            LOG.log(Level.SEVERE, "Field missing data! {0}",
                                    field);
                        } else {
                            String fieldName = fieldData.get((Component) field);
                            ExecutionStepAnswer stepAnswer
                                    = new ExecutionStepAnswer(getExecutionStep()
                                            .getExecutionStepPK()
                                            .getTestCaseExecutionId(),
                                            getExecutionStep().getExecutionStepPK()
                                                    .getStepId(),
                                            getExecutionStep().getExecutionStepPK()
                                                    .getStepTestCaseId()
                                    );
                            stepAnswer.setExecutionStep(getExecutionStep().getEntity());
                            stepAnswer.setFieldName(fieldName);
                            stepAnswer.setFieldAnswer(String.valueOf(field.getValue()));
                            getExecutionStep().getExecutionStepAnswerList()
                                    .add(stepAnswer);
                        }
                    }
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        }
        boolean validAnswer = result.getValue() != null
                && !((String) result.getValue()).trim().isEmpty();
        boolean validReview = review.getValue() != null
                && !((String) review.getValue()).trim().isEmpty();
        return reviewer ? validReview && validAnswer : validAnswer && pass;
    }

    @Override
    public boolean onBack() {
        return getExecutionStep().getStep().getStepSequence() > 1;
    }

    /**
     * @return the step
     */
    public ExecutionStepServer getExecutionStep() {
        return step;
    }

    public static boolean getPDFRendering(File source, File dest)
            throws IllegalStateException {
        OfficeManager officeManager = null;
        try {
            File home = new File(VMSettingServer.getSetting("openoffice.home")
                    .getStringVal());
            int port = VMSettingServer
                    .getSetting("openoffice.port").getIntVal();
            if (!home.isDirectory() || !home.exists()) {
                LOG.log(Level.WARNING,
                        "Unable to find OpenOffice and/or LibreOffice "
                        + "installation at: {0}", home);
                Notification.show(TRANSLATOR.translate("unable.to.render.pdf.title")
                        + ": "
                        + TRANSLATOR.translate("unable.to.render.pdf.message"));
                return false;
            }
            if (port <= 0) {
                LOG.log(Level.WARNING,
                        "Unable to find OpenOffice and/or LibreOffice "
                        + "installation at port: {0}", port);
                Notification.show(TRANSLATOR.translate("unable.to.render.pdf.title")
                        + ": "
                        + TRANSLATOR.translate("unable.to.render.pdf.port"));
                return false;
            }
            // Connect to an OpenOffice.org instance running on available port
            try {
                officeManager = LocalOfficeManager.builder()
                        .portNumbers(port)
                        .officeHome(home)
                        .build();
                officeManager.start();

                OfficeDocumentConverter converter
                        = new OfficeDocumentConverter(officeManager);
                converter.convert(source, dest);
                // close the connection
                officeManager.stop();
                return true;
            } catch (IllegalStateException ise) {
                //Looks like OpenOffice or LibreOffice is not installed
                LOG.log(Level.WARNING,
                        "Unable to find OpenOffice and/or LibreOffice "
                        + "installation.", ise);
            }
        } catch (OfficeException e) {
            if (officeManager != null) {
                try {
                    officeManager.stop();
                } catch (OfficeException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
            LOG.log(Level.SEVERE, null, e);
        }
        return false;
    }

    private void displayAttachment(AttachmentServer attachment) {
        String name = attachment.getFileName();
        byte[] bytes = attachment.getFile();
        boolean ableToDisplay = false;
        try {
            for (IFileDisplay fd : Lookup.getDefault()
                    .lookupAll(IFileDisplay.class)) {
                if (fd.supportFile(new File(name))) {
                    Component viewer = fd.getViewer(fd.loadFile(name,
                            bytes));
                    if (viewer instanceof Dialog d) {
                        ValidationManagerUI.getInstance().openDialog(d);
                    }
                    ableToDisplay = true;
                    break;
                }
            }
            if (!ableToDisplay) {
                //Convert file to pfd
                PDFDisplay pdf = new PDFDisplay();
                File source = pdf.loadFile(name, bytes);
                File dest = new File(System.getProperty("java.io.tmpdir")
                        + System.getProperty("file.separator")
                        + name.substring(0, name.lastIndexOf("."))
                        + ".pdf");
                getPDFRendering(source, dest);
                if (dest.exists()) {
                    Component viewer = pdf.getViewer(dest);
                    if (viewer instanceof Dialog d) {
                        ValidationManagerUI.getInstance().openDialog(d);
                    }
                    ableToDisplay = true;
                }
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE,
                    "Error loading attachment file: "
                    + name, ex);
        }
        if (!ableToDisplay) {
            Notification.show(TRANSLATOR.translate("unable.to.render.pdf.title")
                    + ": "
                    + TRANSLATOR.translate("unable.to.render.pdf.message"));
        }
    }

    private ConfirmDialog getDeletionPrompt(Object data) {
        ConfirmDialog mb = new ConfirmDialog();
        mb.setHeader(TRANSLATOR.translate("issue.detail"));
        mb.setText(TRANSLATOR.translate("remove.item.title"));
        mb.setConfirmButton(TRANSLATOR.translate("general.yes"), (e) -> {
            try {
                if (data instanceof ExecutionStepHasAttachment) {
                    getExecutionStep().removeAttachment(new AttachmentServer(
                            ((ExecutionStepHasAttachment) data)
                                    .getAttachment().getAttachmentPK()));
                }
                if (data instanceof ExecutionStepHasIssue) {
                    getExecutionStep().removeIssue(new IssueServer(
                            ((ExecutionStepHasIssue) data)
                                    .getIssue()));
                }
                getExecutionStep().write2DB();
                getExecutionStep().update();
                refreshCurrentStep();
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        });
        mb.setCancelButton(TRANSLATOR.translate("general.no"), (e) -> {
            if (data instanceof ExecutionStepHasAttachment) {
                ExecutionStepHasAttachment esha = (ExecutionStepHasAttachment) data;
                if (esha.getAttachment().getAttachmentType().getType().equals("comment")) {
                    displayComment(new AttachmentServer(esha
                            .getAttachment().getAttachmentPK()));
                } else {
                    displayAttachment(new AttachmentServer(esha
                            .getAttachment().getAttachmentPK()));
                }
            }
            if (data instanceof ExecutionStepHasIssue) {
                ExecutionStepHasIssue eshi = (ExecutionStepHasIssue) data;
                displayIssue(new IssueServer(eshi.getIssue()));
            }
        });
        return mb;
    }

    private void updateValue(HasValue<?, ?> field) {
        String data = field instanceof Component
                ? fieldData.get((Component) field) : null;
        if (data != null) {
            //Look for the answer in the database
            getExecutionStep().getExecutionStepAnswerList().forEach(answer -> {
                if (answer.getFieldName().equals(data)) {
                    if (field instanceof com.vaadin.flow.component.textfield.TextFieldBase) {//This includes NumberField
                        ((com.vaadin.flow.component.textfield.TextFieldBase) field)
                                .setValue(answer.getFieldAnswer());
                    } else if (field instanceof Checkbox) {
                        ((Checkbox) field).setValue(Boolean.valueOf(answer.getFieldAnswer()));
                    }
                }
            });
        } else {
            LOG.log(Level.SEVERE, "Field missing data! {0}", field);
        }
    }

    /**
     * v8 {@code Wizard.updateCurrentStep()} re-rendered the current step; the
     * Flow wizard has no re-render API, so leave it to the next natural
     * activation of the step (its getContent() rebuilds from the updated
     * server state).
     */
    private void refreshCurrentStep() {
        // TODO(phase-4b-2): FlowWizard needs an explicit step re-render API;
        // until then updated attachments/issues appear when the step is
        // re-entered (Back then Next) instead of immediately.
    }

    /**
     * The DB stores {@link Date} while the Flow date fields work with
     * {@link LocalDateTime}.
     *
     * @param date date to convert, may be null
     * @return the equivalent LocalDateTime or null
     */
    private static LocalDateTime toDateTime(Date date) {
        return date == null ? null
                : LocalDateTime.ofInstant(date.toInstant(),
                        ZoneId.systemDefault());
    }

    private static Date toDate(LocalDateTime dateTime) {
        return dateTime == null ? null
                : Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
