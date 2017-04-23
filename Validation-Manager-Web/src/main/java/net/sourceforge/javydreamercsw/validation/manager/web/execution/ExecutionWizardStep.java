package net.sourceforge.javydreamercsw.validation.manager.web.execution;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMUI;
import com.validation.manager.core.db.AttachmentType;
import com.validation.manager.core.db.ExecutionResult;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.ExecutionStepHasAttachment;
import com.validation.manager.core.db.ExecutionStepHasIssue;
import com.validation.manager.core.db.controller.ExecutionResultJpaController;
import com.validation.manager.core.db.controller.IssueTypeJpaController;
import com.validation.manager.core.server.core.AttachmentServer;
import com.validation.manager.core.server.core.AttachmentTypeServer;
import com.validation.manager.core.server.core.ExecutionResultServer;
import com.validation.manager.core.server.core.ExecutionStepServer;
import com.validation.manager.core.server.core.IssueServer;
import com.validation.manager.core.server.core.IssueTypeServer;
import com.validation.manager.core.server.core.VMSettingServer;
import de.steinwedel.messagebox.ButtonOption;
import de.steinwedel.messagebox.ButtonType;
import de.steinwedel.messagebox.MessageBox;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.ByteToStringConverter;
import net.sourceforge.javydreamercsw.validation.manager.web.VMWindow;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import net.sourceforge.javydreamercsw.validation.manager.web.file.IFileDisplay;
import net.sourceforge.javydreamercsw.validation.manager.web.file.PDFDisplay;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeException;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.openide.util.Lookup;
import org.vaadin.easyuploads.MultiFileUpload;
import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.WizardStep;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ExecutionWizardStep implements WizardStep {

    private final Wizard w;
    private final VMUI ui;
    private final ExecutionStepServer step;
    private final ComboBox result = new ComboBox("Result");
    private final ComboBox issueType = new ComboBox("Issue Type");
    private Button attach;
    private Button bug;
    private Button comment;
    private DateField start;
    private DateField end;
    private static final Logger LOG
            = Logger.getLogger(ExecutionWizardStep.class.getSimpleName());

    public ExecutionWizardStep(Wizard w, VMUI ui, ExecutionStep step) {
        this.w = w;
        this.ui = ui;
        this.step = new ExecutionStepServer(step);
        issueType.setSizeFull();
        issueType.setReadOnly(false);
        issueType.setRequired(true);
        issueType.setRequiredError("Please provide a type!");
        IssueTypeJpaController it
                = new IssueTypeJpaController(DataBaseManager
                        .getEntityManagerFactory());
        it.findIssueTypeEntities().forEach(type -> {
            String item = ui.translate(type.getTypeName());
            issueType.addItem(type.getTypeName());
            issueType.setItemCaption(type.getTypeName(), item);
            switch (type.getId()) {
                case 1:
                    issueType.setItemIcon(type.getTypeName(), VaadinIcons.BUG);
                    break;
                case 2:
                    issueType.setItemIcon(type.getTypeName(), VaadinIcons.EYE);
                    break;
                case 3:
                    issueType.setItemIcon(type.getTypeName(), VaadinIcons.QUESTION);
                    break;
            }
        });
        result.setSizeFull();
        result.setReadOnly(false);
        result.setRequired(true);
        result.setRequiredError("Please provide a result!");
        result.setTextInputAllowed(false);
        ExecutionResultJpaController c
                = new ExecutionResultJpaController(DataBaseManager
                        .getEntityManagerFactory());
        c.findExecutionResultEntities().forEach(r -> {
            String item = ui.translate(r.getResultName());
            result.addItem(r.getResultName());
            result.setItemCaption(r.getResultName(), item);
            switch (r.getId()) {
                case 1:
                    result.setItemIcon(r.getResultName(), VaadinIcons.CHECK);
                    break;
                case 2:
                    result.setItemIcon(r.getResultName(), VaadinIcons.CLOSE);
                    break;
                case 3:
                    result.setItemIcon(r.getResultName(), VaadinIcons.PAUSE);
                    break;
                default:
                    result.setItemIcon(r.getResultName(), VaadinIcons.CLOCK);
                    break;
            }
        });
    }

    @Override
    public String getCaption() {
        return getStep().getStep().getTestCase().getName() + " Step:"
                + getStep().getStep().getStepSequence();
    }

    @Override
    public Component getContent() {
        getStep().update();
        Panel form = new Panel("Step Detail");
        if (getStep().getExecutionStart() == null) {
            //Set the start date.
            getStep().setExecutionStart(new Date());
        }
        FormLayout layout = new FormLayout();
        form.setContent(layout);
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(getStep().getStep().getClass());
        binder.setItemDataSource(getStep().getStep());
        TextArea text = new TextArea("Text");
        text.setConverter(new ByteToStringConverter());
        binder.bind(text, "text");
        text.setSizeFull();
        layout.addComponent(text);
        Field notes = binder.buildAndBind("Notes", "notes",
                TextArea.class);
        notes.setSizeFull();
        layout.addComponent(notes);
        if (getStep().getExecutionStart() != null) {
            start = new DateField("Start Date");
            start.setResolution(Resolution.SECOND);
            start.setValue(getStep().getExecutionStart());
            layout.addComponent(start);
        }
        if (getStep().getExecutionEnd() != null) {
            end = new DateField("End Date");
            end.setResolution(Resolution.SECOND);
            end.setValue(getStep().getExecutionEnd());
            layout.addComponent(end);
        }
        binder.setReadOnly(true);
        //Space to record result
        if (getStep().getResultId() != null) {
            result.setValue(getStep().getResultId().getResultName());
        }
        layout.addComponent(result);
        if (VMSettingServer.getSetting("show.expected.result").getBoolVal()) {
            TextArea expectedResult = new TextArea("Expected Result");
            expectedResult.setConverter(new ByteToStringConverter());
            binder.bind(expectedResult, "expectedResult");
            expectedResult.setSizeFull();
            layout.addComponent(expectedResult);
        }
        //Add the Attachments
        HorizontalLayout attachments = new HorizontalLayout();
        attachments.setCaption("Attachments");
        HorizontalLayout comments = new HorizontalLayout();
        comments.setCaption("Comments");
        HorizontalLayout issues = new HorizontalLayout();
        issues.setCaption("Issues");
        int commentCounter = 0;
        int issueCounter = 0;
        for (ExecutionStepHasIssue ei : getStep().getExecutionStepHasIssueList()) {
            issueCounter++;
            Button a = new Button("Issue #" + issueCounter);
            a.setIcon(VaadinIcons.BUG);
            a.addClickListener((Button.ClickEvent event) -> {
                displayIssue(new IssueServer(ei.getIssue()));
            });
            a.setEnabled(!step.getLocked());
            issues.addComponent(a);
        }
        for (ExecutionStepHasAttachment attachment
                : getStep().getExecutionStepHasAttachmentList()) {
            switch (attachment.getAttachment().getAttachmentType().getType()) {
                case "comment": {
                    //Comments go in a different section
                    commentCounter++;
                    Button a = new Button("Comment #" + commentCounter);
                    a.setIcon(VaadinIcons.CLIPBOARD_TEXT);
                    a.addClickListener((Button.ClickEvent event) -> {
                        if (!step.getLocked()) {
                            //Prompt if user wants this removed
                            MessageBox mb = getDeletionPrompt(attachment);
                            mb.open();
                        } else {
                            displayComment(new AttachmentServer(attachment
                                    .getAttachment().getAttachmentPK()));
                        }
                    });
                    a.setEnabled(!step.getLocked());
                    comments.addComponent(a);
                    break;
                }
                default: {
                    Button a = new Button(attachment.getAttachment().getFileName());
                    a.setEnabled(!step.getLocked());
                    a.setIcon(VaadinIcons.PAPERCLIP);
                    a.addClickListener((Button.ClickEvent event) -> {
                        if (!step.getLocked()) {
                            //Prompt if user wants this removed
                            MessageBox mb = getDeletionPrompt(attachment);
                            mb.open();
                        } else {
                            displayAttachment(
                                    new AttachmentServer(attachment.getAttachment()
                                            .getAttachmentPK()));
                        }
                    });
                    attachments.addComponent(a);
                    break;
                }
            }
        }
        if (attachments.getComponentCount() > 0) {
            layout.addComponent(attachments);
        }
        if (comments.getComponentCount() > 0) {
            layout.addComponent(comments);
        }
        if (issues.getComponentCount() > 0) {
            layout.addComponent(issues);
        }
        //Add the menu
        HorizontalLayout hl = new HorizontalLayout();
        attach = new Button("Add Attachment");
        attach.setIcon(VaadinIcons.PAPERCLIP);
        attach.addClickListener((Button.ClickEvent event) -> {
            //Show dialog to upload file.
            Window dialog = new VMWindow("Attach File");
            VerticalLayout vl = new VerticalLayout();
            MultiFileUpload multiFileUpload = new MultiFileUpload() {
                @Override
                protected void handleFile(File file, String fileName,
                        String mimeType, long length) {
                    try {
                        LOG.log(Level.FINE, "Received file {1} at: {0}",
                                new Object[]{file.getAbsolutePath(), fileName});
                        //Process the file
                        //Create the attachment
                        AttachmentServer a = new AttachmentServer();
                        a.addFile(file, fileName);
                        //Overwrite the default file name set in addFile. It'll be a temporary file name
                        a.setFileName(fileName);
                        a.write2DB();
                        //Now add it to this Execution Step
                        if (getStep().getExecutionStepHasAttachmentList() == null) {
                            getStep().setExecutionStepHasAttachmentList(new ArrayList<>());
                        }
                        getStep().addAttachment(a);
                        getStep().write2DB();
                        w.updateCurrentStep();
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, "Error creating attachment!", ex);
                    }
                }
            };
            multiFileUpload.setCaption("Select file(s) to attach...");
            vl.addComponent(multiFileUpload);
            dialog.setContent(vl);
            dialog.setHeight(25, Sizeable.Unit.PERCENTAGE);
            dialog.setWidth(25, Sizeable.Unit.PERCENTAGE);
            dialog.center();
            ui.addWindow(dialog);
        });
        hl.addComponent(attach);
        bug = new Button("Create an Issue");
        bug.setIcon(VaadinIcons.BUG);
        bug.addClickListener((Button.ClickEvent event) -> {
            displayIssue(new IssueServer());
        });
        hl.addComponent(bug);
        comment = new Button("Add comment");
        comment.setIcon(VaadinIcons.CLIPBOARD_TEXT);
        comment.addClickListener((Button.ClickEvent event) -> {
            AttachmentServer as = new AttachmentServer();
            //Get comment type
            AttachmentType type = AttachmentTypeServer
                    .getTypeForExtension("comment");
            as.setAttachmentType(type);
            displayComment(as);
        });
        hl.addComponent(comment);
        step.update();
        attach.setEnabled(!step.getLocked());
        bug.setEnabled(!step.getLocked());
        comment.setEnabled(!step.getLocked());
        result.setEnabled(!step.getLocked());
        layout.addComponent(hl);
        return layout;
    }

    private void displayIssue(IssueServer is) {
        Panel form = new Panel("Issue");
        FormLayout layout = new FormLayout();
        form.setContent(layout);
        if (is.getIssuePK() == null) {
            //Set creation date
            is.setCreationTime(new Date());
        }
        BeanFieldGroup binder = new BeanFieldGroup(is.getClass());
        binder.setItemDataSource(is);
        Field title = binder.buildAndBind("Summary", "title",
                TextField.class);
        title.setSizeFull();
        layout.addComponent(title);
        Field desc = binder.buildAndBind("Description", "description",
                TextArea.class);
        desc.setSizeFull();
        layout.addComponent(desc);
        DateField creation = (DateField) binder.buildAndBind("Creation Time",
                "creationTime",
                DateField.class);
        creation.setReadOnly(true);
        creation.setResolution(Resolution.SECOND);
        layout.addComponent(creation);
        //Add the result
        layout.addComponent(issueType);
        if (is.getIssueType() != null) {
            issueType.setValue(is.getIssueType().getTypeName());
        } else {
            //Set it as observation
            issueType.setValue("observation.name");
        }
        //Lock if being created
        issueType.setReadOnly(is.getIssueType() == null);
        MessageBox mb = MessageBox.create();
        mb.setData(is);
        mb.asModal(true)
                .withMessage(layout)
                .withButtonAlignment(Alignment.MIDDLE_CENTER)
                .withOkButton(() -> {
                    try {
                        //Create the attachment
                        IssueServer issue = (IssueServer) mb.getData();
                        issue.setDescription(((TextArea) desc).getValue().trim());
                        issue.setIssueType(IssueTypeServer.getType((String) issueType.getValue()));
                        issue.setCreationTime(creation.getValue());
                        issue.setTitle((String) title.getValue());
                        boolean toAdd = issue.getIssuePK() == null;
                        issue.write2DB();
                        if (toAdd) {
                            //Now add it to this Execution Step
                            if (getStep().getExecutionStepHasIssueList() == null) {
                                getStep().setExecutionStepHasIssueList(new ArrayList<>());
                            }
                            getStep().addIssue(issue, ui.getUser());
                            getStep().write2DB();
                        }
                        w.updateCurrentStep();
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }, ButtonOption.focus(),
                        ButtonOption.icon(VaadinIcons.CHECK),
                        ButtonOption.disable())
                .withCancelButton(ButtonOption.icon(VaadinIcons.CLOSE));
        mb.getWindow().setCaption("Issue Details");
        mb.getWindow().setIcon(ValidationManagerUI.SMALL_APP_ICON);
        ((TextArea) desc).addTextChangeListener((TextChangeEvent event1) -> {
            //Enable if there is a description change.
            mb.getButton(ButtonType.OK)
                    .setEnabled(!step.getLocked()
                            && !event1.getText().trim().isEmpty());
        });
        ((TextField) title).addTextChangeListener((TextChangeEvent event1) -> {
            //Enable if there is a title change.
            mb.getButton(ButtonType.OK)
                    .setEnabled(!step.getLocked()
                            && !event1.getText().trim().isEmpty());
        });
        mb.open();
    }

    private void displayComment(AttachmentServer as) {
        Panel form = new Panel("Comment");
        FormLayout layout = new FormLayout();
        form.setContent(layout);
        BeanFieldGroup binder = new BeanFieldGroup(as.getClass());
        binder.setItemDataSource(as);
        Field desc = binder.buildAndBind("Text", "textValue",
                TextArea.class);
        desc.setSizeFull();
        layout.addComponent(desc);
        MessageBox mb = MessageBox.create();
        mb.setData(as);
        mb.asModal(true)
                .withMessage(desc)
                .withButtonAlignment(Alignment.MIDDLE_CENTER)
                .withOkButton(() -> {
                    try {
                        //Create the attachment
                        AttachmentServer a = (AttachmentServer) mb.getData();
                        a.setTextValue(((TextArea) desc).getValue().trim());
                        boolean toAdd = a.getAttachmentPK() == null;
                        a.write2DB();
                        if (toAdd) {
                            //Now add it to this Execution Step
                            if (getStep().getExecutionStepHasAttachmentList() == null) {
                                getStep().setExecutionStepHasAttachmentList(new ArrayList<>());
                            }
                            getStep().addAttachment(a);
                            getStep().write2DB();
                        }
                        w.updateCurrentStep();
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }, ButtonOption.focus(),
                        ButtonOption.icon(VaadinIcons.CHECK),
                        ButtonOption.disable())
                .withCancelButton(ButtonOption.icon(VaadinIcons.CLOSE));
        mb.getWindow().setCaption("Enter Comment");
        mb.getWindow().setIcon(ValidationManagerUI.SMALL_APP_ICON);
        ((TextArea) desc).addTextChangeListener((TextChangeEvent event1) -> {
            //Enable only when there is a comment.
            mb.getButton(ButtonType.OK)
                    .setEnabled(!step.getLocked()
                            && !event1.getText().trim().isEmpty());
        });
        mb.open();
    }

    @Override
    public boolean onAdvance() {
        //Can only proceed after the current step is executed and documented.
        String answer = ((String) result.getValue());
        if (answer == null) {
            Notification.show("Unable to proceed!",
                    result.getRequiredError(),
                    Notification.Type.WARNING_MESSAGE);
        } else {
            try {
                //Save the result
                ExecutionResult newResult = ExecutionResultServer.getResult(answer);
                getStep().setExecutionStart(start.getValue());
                if (getStep().getResultId() == null
                        || !Objects.equals(step.getResultId().getId(),
                                newResult.getId())) {
                    getStep().setResultId(newResult);
                    //Set end date to null to reflect update
                    getStep().setExecutionEnd(null);
                }
                if (getStep().getExecutionEnd() == null) {
                    getStep().setExecutionEnd(new Date());
                }
                getStep().write2DB();
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        return result.getValue() != null
                && !((String) result.getValue()).trim().isEmpty();
    }

    @Override
    public boolean onBack() {
        return getStep().getStep().getStepSequence() > 1;
    }

    /**
     * @return the step
     */
    public ExecutionStepServer getStep() {
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
                Notification.show("Unable to render file for viewing!",
                        "Inavlid OpenOffice home. Contact your "
                        + "system administrator.",
                        Notification.Type.ERROR_MESSAGE);
                return false;
            }
            if (port <= 0) {
                LOG.log(Level.WARNING,
                        "Unable to find OpenOffice and/or LibreOffice "
                        + "installation at port: {0}", port);
                Notification.show("Unable to render file for viewing!",
                        "Inavlid OpenOffice port. Contact your "
                        + "system administrator.",
                        Notification.Type.ERROR_MESSAGE);
                return false;
            }
            // Connect to an OpenOffice.org instance running on available port
            try {
                officeManager = new DefaultOfficeManagerConfiguration()
                        .setPortNumber(port)
                        .setOfficeHome(home)
                        .buildOfficeManager();
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
                officeManager.stop();
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
                    ui.addWindow(fd.getViewer(fd.loadFile(name,
                            bytes)));
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
                    ui.addWindow(pdf.getViewer(dest));
                    ableToDisplay = true;
                }
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE,
                    "Error loading attachment file: "
                    + name, ex);
        }
        if (!ableToDisplay) {
            Notification.show("Unable to render file",
                    "Unable to render file in pdf. "
                    + "Contact the system administrator",
                    Notification.Type.ERROR_MESSAGE);
        }
    }

    private MessageBox getDeletionPrompt(Object data) {
        MessageBox mb = MessageBox.createQuestion();
        mb.setData(data);
        mb.asModal(true)
                .withMessage(new Label("Do you want to remove this item?"))
                .withButtonAlignment(Alignment.MIDDLE_CENTER)
                .withYesButton(() -> {
                    try {
                        if (mb.getData() instanceof ExecutionStepHasAttachment) {
                            getStep().removeAttachment(new AttachmentServer(
                                    ((ExecutionStepHasAttachment) mb.getData())
                                            .getAttachment().getAttachmentPK()));
                        }
                        if (mb.getData() instanceof ExecutionStepHasIssue) {
                            getStep().removeIssue(new IssueServer(
                                    ((ExecutionStepHasIssue) mb.getData())
                                            .getIssue()));
                        }
                        getStep().write2DB();
                        getStep().update();
                        w.updateCurrentStep();
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }, ButtonOption.focus(),
                        ButtonOption.icon(VaadinIcons.CHECK))
                .withNoButton(() -> {
                    if (mb.getData() instanceof ExecutionStepHasAttachment) {
                        ExecutionStepHasAttachment esha = (ExecutionStepHasAttachment) mb.getData();
                        if (esha.getAttachment().getAttachmentType().getType().equals("comment")) {
                            displayComment(new AttachmentServer(esha
                                    .getAttachment().getAttachmentPK()));
                        } else {
                            displayAttachment(new AttachmentServer(esha
                                    .getAttachment().getAttachmentPK()));
                        }
                    }
                    if (mb.getData() instanceof ExecutionStepHasIssue) {
                        ExecutionStepHasIssue eshi = (ExecutionStepHasIssue) mb.getData();
                        displayIssue(new IssueServer(eshi.getIssue()));
                    }
                },
                        ButtonOption.icon(VaadinIcons.CLOSE));
        mb.getWindow().setCaption("Issue Details");
        mb.getWindow().setIcon(ValidationManagerUI.SMALL_APP_ICON);
        return mb;
    }
}
