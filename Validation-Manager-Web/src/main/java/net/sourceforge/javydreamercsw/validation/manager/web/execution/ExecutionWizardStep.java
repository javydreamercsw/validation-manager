package net.sourceforge.javydreamercsw.validation.manager.web.execution;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.ExecutionResult;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.controller.ExecutionResultJpaController;
import com.validation.manager.core.server.core.AttachmentServer;
import com.validation.manager.core.server.core.ExecutionResultServer;
import com.validation.manager.core.server.core.ExecutionStepServer;
import com.validation.manager.core.server.core.VMSettingServer;
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
import net.sourceforge.javydreamercsw.validation.manager.web.file.ImageDisplay;
import net.sourceforge.javydreamercsw.validation.manager.web.file.PDFDisplay;
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

    private final ValidationManagerUI ui;

    private final ExecutionStepServer step;
    private final ComboBox result = new ComboBox("Result");
    private static final Logger LOG
            = Logger.getLogger(ExecutionWizardStep.class.getSimpleName());

    public ExecutionWizardStep(Wizard w, ValidationManagerUI ui,
            ExecutionStep step) {
        this.w = w;
        this.ui = ui;
        this.step = new ExecutionStepServer(step);
        result.setSizeFull();
        result.setReadOnly(false);
        result.setRequired(true);
        result.setRequiredError("Please provide a result!");
        ExecutionResultJpaController c
                = new ExecutionResultJpaController(DataBaseManager
                        .getEntityManagerFactory());
        c.findExecutionResultEntities().forEach(r -> {
            String item = r.getResultName();
            if (ValidationManagerUI.rb.containsKey(item)) {
                item = ValidationManagerUI.rb.getString(item);
            }
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
        return step.getStep().getTestCase().getName() + " Step:"
                + step.getStep().getStepSequence();
    }

    @Override
    public Component getContent() {
        step.update();
        Panel form = new Panel("Step Detail");
        if (step.getExecutionStart() == null) {
            //Set the start date.
            step.setExecutionStart(new Date());
        }
        FormLayout layout = new FormLayout();
        form.setContent(layout);
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(step.getStep().getClass());
        binder.setItemDataSource(step.getStep());
        TextArea text = new TextArea("Text");
        text.setConverter(new ByteToStringConverter());
        binder.bind(text, "text");
        text.setSizeFull();
        layout.addComponent(text);
        Field notes = binder.buildAndBind("Notes", "notes",
                TextArea.class);
        notes.setSizeFull();
        layout.addComponent(notes);
        if (step.getExecutionStart() != null) {
            DateField start = new DateField("Start Date");
            start.setResolution(Resolution.SECOND);
            start.setValue(step.getExecutionStart());
            layout.addComponent(start);
        }
        if (step.getExecutionEnd() != null) {
            DateField end = new DateField("End Date");
            end.setResolution(Resolution.SECOND);
            end.setValue(step.getExecutionEnd());
            layout.addComponent(end);
        }
        binder.setReadOnly(true);
        //Space to record result
        if (step.getResultId() != null) {
            result.setValue(step.getResultId().getResultName());
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
        step.getExecutionStepHasAttachmentList().forEach(attachment -> {
            Button a = new Button(attachment.getAttachment().getFileName());
            a.setIcon(VaadinIcons.PAPERCLIP);
            a.addClickListener((Button.ClickEvent event) -> {
                LOG.log(Level.INFO, "Clicked on attachment: {0}",
                        attachment.getAttachment().getFileName());
                String name = attachment.getAttachment().getFileName();
                byte[] bytes = attachment.getAttachment().getFile();
                boolean ableToDisplay = false;
                try {
                    //TODO:Remove when the service issue is fixed
                    //See: https://vaadin.com/forum/#!/thread/15663093
                    //See: http://stackoverflow.com/questions/43373082/vaadin-how-to-add-meta-inf-services-to-the-war
                    //Optionally convert all files to pdf when possible.
                    PDFDisplay pdf = new PDFDisplay();
                    if (pdf.supportFile(name)) {
                        ui.addWindow(pdf.getViewer(pdf.loadFile(name, bytes)));
                        ableToDisplay = true;
                    }
                    ImageDisplay image = new ImageDisplay();
                    if (image.supportFile(name)) {
                        ui.addWindow(image.getViewer(image.loadFile(name, bytes)));
                        ableToDisplay = true;
                    }
                    //-------------------------------------------
                    for (IFileDisplay fd : Lookup.getDefault()
                            .lookupAll(IFileDisplay.class)) {
                        if (fd.supportFile(new File(name))) {
                            ui.addWindow(fd.getViewer(fd.loadFile(name, bytes)));
                            ableToDisplay = true;
                            break;
                        }
                    }
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE,
                            "Error loading attachment file: "
                            + name, ex);
                }
                if (!ableToDisplay) {
                    LOG.log(Level.WARNING,
                            "Unable to display: {0}. No File displayer found!",
                            name);
                }
            });
            attachments.addComponent(a);
        });
        if (attachments.getComponentCount() > 0) {
            layout.addComponent(attachments);
        }
        //Add the menu
        HorizontalLayout hl = new HorizontalLayout();
        Button attach = new Button("Add Attachment");
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
                        LOG.log(Level.INFO, "Received file {1} at: {0}",
                                new Object[]{file.getAbsolutePath(), fileName});
                        //Process the file
                        //Create the attachment
                        AttachmentServer a = new AttachmentServer();
                        a.addFile(file);
                        //Overwrite the default file name set in addFile. It'll be a temporary file name
                        a.setFileName(fileName);
                        a.write2DB();
                        //Now add it to this Execution Step
                        if (step.getExecutionStepHasAttachmentList() == null) {
                            step.setExecutionStepHasAttachmentList(new ArrayList<>());
                        }
                        step.addAttachment(a);
                        step.write2DB();
                        w.updateCurrentStep();
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, "Error creating attachment!", ex);
                    }
                }
            };
            multiFileUpload.setCaption("Select file(s) to attach...");
            vl.addComponent(multiFileUpload);
            dialog.setContent(vl);
            dialog.center();
            ui.addWindow(dialog);
        });
        hl.addComponent(attach);
        Button bug = new Button("Create an Issue");
        bug.setIcon(VaadinIcons.BUG);
        bug.addClickListener((Button.ClickEvent event) -> {
            LOG.info("Clicked to add a bug!");
        });
        hl.addComponent(bug);
        Button comment = new Button("Add comment");
        comment.setIcon(VaadinIcons.BUG);
        comment.addClickListener((Button.ClickEvent event) -> {
            LOG.info("Clicked to add comment!");
        });
        hl.addComponent(comment);
        layout.addComponent(hl);
        return layout;
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
                if (step.getResultId() == null
                        || !Objects.equals(step.getResultId().getId(),
                                newResult.getId())) {
                    step.setResultId(newResult);
                    //Set end date to null to reflect update
                    step.setExecutionEnd(null);
                }
                if (step.getExecutionEnd() == null) {
                    step.setExecutionEnd(new Date());
                }
                step.write2DB();
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        return !((String) result.getValue()).trim().isEmpty();
    }

    @Override
    public boolean onBack() {
        return step.getStep().getStepSequence() > 1;
    }
}
