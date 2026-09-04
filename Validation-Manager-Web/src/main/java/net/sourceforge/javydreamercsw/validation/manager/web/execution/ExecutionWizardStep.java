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

import static com.validation.manager.core.ContentProvider.TRANSLATOR;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jodconverter.OfficeDocumentConverter;
import org.jodconverter.office.LocalOfficeManager;
import org.jodconverter.office.OfficeException;
import org.jodconverter.office.OfficeManager;
import org.openide.util.Lookup;
import org.vaadin.easyuploads.MultiFileUpload;
import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.WizardStep;

import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.UserError;
import com.vaadin.server.Resource;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateTimeField;
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

import de.steinwedel.messagebox.ButtonOption;
import de.steinwedel.messagebox.ButtonType;
import de.steinwedel.messagebox.MessageBox;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import net.sourceforge.javydreamercsw.validation.manager.web.component.ByteToStringConverter;
import net.sourceforge.javydreamercsw.validation.manager.web.component.VMWindow;
import net.sourceforge.javydreamercsw.validation.manager.web.file.IFileDisplay;
import net.sourceforge.javydreamercsw.validation.manager.web.file.PDFDisplay;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ExecutionWizardStep implements WizardStep
{

  private final Wizard w;
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
  private DateTimeField start;
  private DateTimeField end;
  private DateTimeField reviewDate;
  private static final Logger LOG
          = Logger.getLogger(ExecutionWizardStep.class.getSimpleName());
  private boolean reviewer = false;
  private final List<HasValue> fields = new ArrayList<>();

  public ExecutionWizardStep(Wizard w, ExecutionStep step,
          boolean reviewer)
  {
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
    it.findIssueTypeEntities().forEach(type ->
    {
      issueTypes.add(type);
      if (type.getTypeName().equals("observation.name"))
      {
        issueType.setValue(type);
      }
    });
    issueType.setItems(issueTypes);
    issueType.setItemCaptionGenerator(t -> Lookup.getDefault()
            .lookup(InternationalizationProvider.class)
            .translate(t.getTypeName()));
    issueType.setItemIconGenerator(t ->
    {
      switch (t.getId())
      {
        case 1:
          return VaadinIcons.BUG;
        case 2:
          return VaadinIcons.EYE;
        case 3:
          return VaadinIcons.QUESTION;
        default:
          return null;
      }
    });
    issueType.setItems(issueTypes);
    issueType.setItemCaptionGenerator(t -> Lookup.getDefault()
            .lookup(InternationalizationProvider.class)
            .translate(t.getTypeName()));
    result.setReadOnly(false);
    result.setRequiredIndicatorVisible(true);
    result.setTextInputAllowed(false);
    review.setReadOnly(false);
    review.setRequiredIndicatorVisible(true);
    review.setTextInputAllowed(false);
    ReviewResultJpaController c2
            = new ReviewResultJpaController(DataBaseManager
                    .getEntityManagerFactory());
    List<String> reviewResults = new ArrayList<>();
    c2.findReviewResultEntities().forEach(r ->
    {
      reviewResults.add(r.getReviewName());
    });
    review.setItems(reviewResults);
    review.setItemCaptionGenerator(item -> Lookup.getDefault()
            .lookup(InternationalizationProvider.class)
            .translate(item));
    review.setItemIconGenerator(item ->
    {
      //ReviewResult ids are stable: 1 pass, 2 fail, anything else pending
      switch (reviewResults.indexOf(item) + 1)
      {
        case 1:
          return VaadinIcons.CHECK;
        case 2:
          return VaadinIcons.CLOSE;
        default:
          return VaadinIcons.CLOCK;
      }
    });
    ExecutionResultJpaController c
            = new ExecutionResultJpaController(DataBaseManager
                    .getEntityManagerFactory());
    List<String> executionResults = new ArrayList<>();
    c.findExecutionResultEntities().forEach(r ->
    {
      executionResults.add(r.getResultName());
    });
    result.setItems(executionResults);
    result.setItemCaptionGenerator(item -> Lookup.getDefault()
            .lookup(InternationalizationProvider.class)
            .translate(item));
    result.setItemIconGenerator(item ->
    {
      //ExecutionResult ids are stable: 1 pass, 2 fail, 3 pause, else pending
      switch (executionResults.indexOf(item) + 1)
      {
        case 1:
          return VaadinIcons.CHECK;
        case 2:
          return VaadinIcons.CLOSE;
        case 3:
          return VaadinIcons.PAUSE;
        default:
          return VaadinIcons.CLOCK;
      }
    });
  }

  @Override
  public String getCaption()
  {
    return getExecutionStep().getStep().getTestCase().getName() + " "
            + TRANSLATOR.translate("general.step") + ":"
            + getExecutionStep().getStep().getStepSequence();
  }

  @Override
  public Component getContent()
  {
    Panel form = new Panel(TRANSLATOR.translate("step.detail"));
    if (getExecutionStep().getExecutionStart() == null)
    {
      //Set the start date.
      getExecutionStep().setExecutionStart(new Date());
    }
    FormLayout layout = new FormLayout();
    form.setContent(layout);
    form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
    Binder<Step> binder = new Binder<>(Step.class);
    binder.setBean(getExecutionStep().getStep());
    binder.setReadOnly(true);
    TextArea text = new TextArea(TRANSLATOR.translate("general.text"));
    binder.forField(text)
            .withConverter(new ByteToStringConverter())
            .bind("text");
    text.setSizeFull();
    layout.addComponent(text);
    TextField notes = new TextField(TRANSLATOR.translate("general.notes"));
    binder.bind(notes, "notes");
    notes.setSizeFull();
    layout.addComponent(notes);
    if (getExecutionStep().getExecutionStart() != null)
    {
      start = new DateTimeField(TRANSLATOR.translate("start.date"));
      start.setResolution(DateTimeResolution.SECOND);
      start.setDateFormat(VMSettingServer.getSetting("date.format")
              .getStringVal());
      start.setValue(toDateTime(getExecutionStep().getExecutionStart()));
      start.setReadOnly(true);
      layout.addComponent(start);
    }
    if (getExecutionStep().getExecutionEnd() != null)
    {
      end = new DateTimeField(TRANSLATOR.translate("end.date"));
      end.setDateFormat(VMSettingServer.getSetting("date.format")
              .getStringVal());
      end.setResolution(DateTimeResolution.SECOND);
      end.setValue(toDateTime(getExecutionStep().getExecutionEnd()));
      end.setReadOnly(true);
      layout.addComponent(end);
    }
    //Space to record result
    if (getExecutionStep().getResultId() != null)
    {
      result.setValue(getExecutionStep().getResultId().getResultName());
    }
    layout.addComponent(result);
    if (reviewer)
    {//Space to record review
      if (getExecutionStep().getReviewResultId() != null)
      {
        review.setValue(getExecutionStep().getReviewResultId().getReviewName());
      }
      layout.addComponent(review);
    }
    //Add Reviewer name
    if (getExecutionStep().getReviewer() != null)
    {
      TextField reviewerField = new TextField(TRANSLATOR
              .translate("general.reviewer"));
      reviewerField.setValue(getExecutionStep().getReviewer().getFirstName() + " "
              + getExecutionStep().getReviewer().getLastName());
      reviewerField.setReadOnly(true);
      layout.addComponent(reviewerField);
    }
    if (getExecutionStep().getReviewDate() != null)
    {
      reviewDate = new DateTimeField(TRANSLATOR
              .translate("review.date"));
      reviewDate.setDateFormat(VMSettingServer.getSetting("date.format")
              .getStringVal());
      reviewDate.setResolution(DateTimeResolution.SECOND);
      reviewDate.setValue(toDateTime(getExecutionStep().getReviewDate()));
      reviewDate.setReadOnly(true);
      layout.addComponent(reviewDate);
    }
    if (VMSettingServer.getSetting("show.expected.result").getBoolVal())
    {
      TextArea expectedResult
              = new TextArea(TRANSLATOR.translate("expected.result"));
      binder.forField(expectedResult)
              .withConverter(new ByteToStringConverter())
              .bind("expectedResult");
      expectedResult.setSizeFull();
      layout.addComponent(expectedResult);
    }
    //Add the fields
    fields.clear();
    getExecutionStep().getStep().getDataEntryList().forEach(de ->
    {
      switch (de.getDataEntryType().getId())
      {
        case 1://String
          TextField tf = new TextField(TRANSLATOR
                  .translate(de.getEntryName()));
          ((HasValue) tf).setRequiredIndicatorVisible(DataEntryServer
                  .getProperty(de,
                          "property.required")
                  .getPropertyValue().equals("true"));
          tf.setData(de.getEntryName());
          if (VMSettingServer.getSetting("show.expected.result")
                  .getBoolVal())
          {
            //Add expected result
            DataEntryProperty stringCase = DataEntryServer
                    .getProperty(de, "property.match.case");
            DataEntryProperty r = DataEntryServer
                    .getProperty(de, "property.expected.result");
            if (r != null
                    && !r.getPropertyValue().equals("null"))
            {
              String error = TRANSLATOR.translate("expected.result") + ": "
                      + r.getPropertyValue();
              if (stringCase != null
                      && stringCase.getPropertyValue().equals("true")
                      ? !tf.getValue().equals(r.getPropertyValue())
                      : !tf.getValue().equalsIgnoreCase(r.getPropertyValue()))
              {
                //We have an expected result and a match case requirement
                tf.setComponentError(new UserError(error));
              }
            }
          }
          fields.add(tf);
          //Set value if already recorded
          updateValue(tf);
          layout.addComponent(tf);
          break;
        case 2://Numeric
          // numberfield7 addon has no Vaadin 8 release; use a TextField
          // with a converter and range validation.
          TextField nf = new TextField(
                  TRANSLATOR.translate(de.getEntryName()));
          ((HasValue) nf).setRequiredIndicatorVisible(DataEntryServer
                  .getProperty(de,
                          "property.required")
                  .getPropertyValue().equals("true"));
          nf.setData(de.getEntryName());
          Double min = null,
           max = null;
          for (DataEntryProperty prop : de.getDataEntryPropertyList())
          {
            String value = prop.getPropertyValue();
            if (prop.getPropertyName().equals("property.max"))
            {
              try
              {
                max = Double.parseDouble(value);
              }
              catch (NumberFormatException ex)
              {
                //Leave as null
              }
            }
            else if (prop.getPropertyName().equals("property.min"))
            {
              try
              {
                min = Double.parseDouble(value);
              }
              catch (NumberFormatException ex)
              {
                //Leave as null
              }
            }
          }
          //Add expected result
          if (VMSettingServer.getSetting("show.expected.result")
                  .getBoolVal() && (min != null || max != null))
          {
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
            nf.setComponentError(new UserError(error));
          }
          fields.add(nf);
          //Set value if already recorded
          updateValue(nf);
          layout.addComponent(nf);
          break;
        case 3://Boolean
          CheckBox cb = new CheckBox(TRANSLATOR
                  .translate(de.getEntryName()));
          cb.setData(de.getEntryName());
          cb.setRequiredIndicatorVisible(DataEntryServer
                  .getProperty(de,
                          "property.required")
                  .getPropertyValue().equals("true"));
          if (VMSettingServer.getSetting("show.expected.result")
                  .getBoolVal())
          {
            DataEntryProperty r = DataEntryServer.getProperty(de,
                    "property.expected.result");
            if (r != null)
            {
              //Add expected result
              String error = TRANSLATOR.translate("expected.result") + ": "
                      + r.getPropertyValue();
              if (!cb.getValue().toString().equals(r.getPropertyValue()))
              {
                cb.setComponentError(new UserError(error));
              }
            }
          }
          fields.add(cb);
          //Set value if already recorded
          updateValue(cb);
          layout.addComponent(cb);
          break;
        case 4://Attachment
          Label l = new Label(TRANSLATOR
                  .translate(de.getEntryName()));
          layout.addComponent(l);
          break;
        default:
          LOG.log(Level.SEVERE, "Unexpected field type: {0}",
                  de.getDataEntryType().getId());
      }
    });
    //Add the Attachments
    HorizontalLayout attachments = new HorizontalLayout();
    attachments.setCaption(TRANSLATOR.translate("general.attachment"));
    HorizontalLayout comments = new HorizontalLayout();
    comments.setCaption(TRANSLATOR.translate("general.comments"));
    HorizontalLayout issues = new HorizontalLayout();
    issues.setCaption(TRANSLATOR.translate("general.issue"));
    int commentCounter = 0;
    int issueCounter = 0;
    for (ExecutionStepHasIssue ei : getExecutionStep().getExecutionStepHasIssueList())
    {
      issueCounter++;
      Button a = new Button("Issue #" + issueCounter);
      a.setIcon(VaadinIcons.BUG);
      a.addClickListener((Button.ClickEvent event) ->
      {
        displayIssue(new IssueServer(ei.getIssue()));
      });
      a.setEnabled(!step.getLocked());
      issues.addComponent(a);
    }
    for (ExecutionStepHasAttachment attachment
            : getExecutionStep().getExecutionStepHasAttachmentList())
    {
      switch (attachment.getAttachment().getAttachmentType().getType())
      {
        case "comment":
        {
          //Comments go in a different section
          commentCounter++;
          Button a = new Button("Comment #" + commentCounter);
          a.setIcon(VaadinIcons.CLIPBOARD_TEXT);
          a.addClickListener((Button.ClickEvent event) ->
          {
            if (!step.getLocked())
            {
              //Prompt if user wants this removed
              MessageBox mb = getDeletionPrompt(attachment);
              mb.open();
            }
            else
            {
              displayComment(new AttachmentServer(attachment
                      .getAttachment().getAttachmentPK()));
            }
          });
          a.setEnabled(!step.getLocked());
          comments.addComponent(a);
          break;
        }
        default:
        {
          Button a = new Button(attachment.getAttachment().getFileName());
          a.setEnabled(!step.getLocked());
          a.setIcon(VaadinIcons.PAPERCLIP);
          a.addClickListener((Button.ClickEvent event) ->
          {
            if (!step.getLocked())
            {
              //Prompt if user wants this removed
              MessageBox mb = getDeletionPrompt(attachment);
              mb.open();
            }
            else
            {
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
    if (attachments.getComponentCount() > 0)
    {
      layout.addComponent(attachments);
    }
    if (comments.getComponentCount() > 0)
    {
      layout.addComponent(comments);
    }
    if (issues.getComponentCount() > 0)
    {
      layout.addComponent(issues);
    }
    //Add the menu
    HorizontalLayout hl = new HorizontalLayout();
    attach = new Button(TRANSLATOR.translate("add.attachment"));
    attach.setIcon(VaadinIcons.PAPERCLIP);
    attach.addClickListener((Button.ClickEvent event) ->
    {
      //Show dialog to upload file.
      Window dialog = new VMWindow(TRANSLATOR.translate("attach.file"));
      VerticalLayout vl = new VerticalLayout();
      MultiFileUpload multiFileUpload = new MultiFileUpload()
      {
        @Override
        protected void handleFile(File file, String fileName,
                String mimeType, long length)
        {
          try
          {
            LOG.log(Level.FINE, "Received file {1} at: {0}",
                    new Object[]
                    {
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
            if (getExecutionStep().getExecutionStepHasAttachmentList() == null)
            {
              getExecutionStep().setExecutionStepHasAttachmentList(new ArrayList<>());
            }
            getExecutionStep().addAttachment(a);
            getExecutionStep().write2DB();
            w.updateCurrentStep();
          }
          catch (Exception ex)
          {
            LOG.log(Level.SEVERE, "Error creating attachment!", ex);
          }
        }
      };
      multiFileUpload.setCaption(TRANSLATOR.translate("select.files.attach"));
      vl.addComponent(multiFileUpload);
      dialog.setContent(vl);
      dialog.setHeight(25, Sizeable.Unit.PERCENTAGE);
      dialog.setWidth(25, Sizeable.Unit.PERCENTAGE);
      ValidationManagerUI.getInstance().addWindow(dialog);
    });
    hl.addComponent(attach);
    bug = new Button(TRANSLATOR.translate("create.issue"));
    bug.setIcon(VaadinIcons.BUG);
    bug.addClickListener((Button.ClickEvent event) ->
    {
      displayIssue(new IssueServer());
    });
    hl.addComponent(bug);
    comment = new Button(TRANSLATOR.translate("add.comment"));
    comment.setIcon(VaadinIcons.CLIPBOARD_TEXT);
    comment.addClickListener((Button.ClickEvent event) ->
    {
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

  private void displayIssue(IssueServer is)
  {
    Panel form = new Panel(TRANSLATOR.translate("general.issue"));
    FormLayout layout = new FormLayout();
    form.setContent(layout);
    if (is.getIssuePK() == null)
    {
      //Set creation date
      is.setCreationTime(new Date());
    }
    Binder<IssueServer> binder = new Binder<>(IssueServer.class);
    binder.setBean(is);
    TextField title = new TextField(TRANSLATOR.translate("general.summary"));
    binder.bind(title, "title");
    title.setSizeFull();
    layout.addComponent(title);
    TextArea desc = new TextArea(TRANSLATOR.translate("general.description"));
    binder.bind(desc, "description");
    desc.setSizeFull();
    layout.addComponent(desc);
    DateTimeField creation = new DateTimeField(TRANSLATOR.translate("creation.time"));
    binder.bind(creation, "creationTime");
    creation.setReadOnly(true);
    creation.setDateFormat(VMSettingServer.getSetting("date.format")
            .getStringVal());
    creation.setResolution(DateTimeResolution.SECOND);
    layout.addComponent(creation);
    //Add the result
    layout.addComponent(issueType);
    if (is.getIssueType() != null)
    {
      issueType.setValue(is.getIssueType());
    }
    //Lock if being created
    issueType.setReadOnly(is.getIssueType() == null);
    MessageBox mb = MessageBox.create();
    mb.setData(is);
    mb.asModal(true)
            .withMessage(layout)
            .withButtonAlignment(Alignment.MIDDLE_CENTER)
            .withOkButton(() ->
            {
              try
              {
                //Create the attachment
                IssueServer issue = (IssueServer) mb.getData();
                issue.setDescription(desc.getValue().trim());
                issue.setIssueType(issueType.getValue());
                issue.setCreationTime(creation.getValue() == null
                        ? null
                        : Date.from(creation.getValue()
                                .atZone(ZoneId.systemDefault()).toInstant()));
                issue.setTitle(title.getValue());
                boolean toAdd = issue.getIssuePK() == null;
                issue.write2DB();
                if (toAdd)
                {
                  //Now add it to this Execution Step
                  if (getExecutionStep().getExecutionStepHasIssueList() == null)
                  {
                    getExecutionStep().setExecutionStepHasIssueList(new ArrayList<>());
                  }
                  getExecutionStep().addIssue(issue, ValidationManagerUI
                          .getInstance().getUser());
                  getExecutionStep().write2DB();
                }
                w.updateCurrentStep();
              }
              catch (Exception ex)
              {
                LOG.log(Level.SEVERE, null, ex);
              }
            }, ButtonOption.focus(),
                    ButtonOption.icon(VaadinIcons.CHECK),
                    ButtonOption.disable())
            .withCancelButton(ButtonOption.icon(VaadinIcons.CLOSE));
    mb.getWindow().setCaption(TRANSLATOR.translate("issue.detail"));
    mb.getWindow().setIcon(ValidationManagerUI.SMALL_APP_ICON);
    //Enable the OK button only when both fields have content. Vaadin 8 has
    //no per-keystroke TextChangeEvent, so react to value changes instead.
    desc.setValueChangeMode(ValueChangeMode.LAZY);
    title.setValueChangeMode(ValueChangeMode.LAZY);
    desc.addValueChangeListener(event1 ->
    {
      //Enable if there is a description.
      mb.getButton(ButtonType.OK)
              .setEnabled(!step.getLocked()
                      && !desc.getValue().trim().isEmpty());
    });
    title.addValueChangeListener(event1 ->
    {
      //Enable if there is a title.
      mb.getButton(ButtonType.OK)
              .setEnabled(!step.getLocked()
                      && !title.getValue().trim().isEmpty());
    });
    mb.open();
  }

  private void displayComment(AttachmentServer as)
  {
    Panel form = new Panel(TRANSLATOR.translate("general.comment"));
    FormLayout layout = new FormLayout();
    form.setContent(layout);
    Binder<AttachmentServer> binder = new Binder<>(AttachmentServer.class);
    binder.setBean(as);
    TextArea desc = new TextArea(TRANSLATOR.translate("general.text"));
    binder.bind(desc, "textValue");
    desc.setSizeFull();
    layout.addComponent(desc);
    MessageBox mb = MessageBox.create();
    mb.setData(as);
    mb.asModal(true)
            .withMessage(desc)
            .withButtonAlignment(Alignment.MIDDLE_CENTER)
            .withOkButton(() ->
            {
              try
              {
                //Create the attachment
                AttachmentServer a = (AttachmentServer) mb.getData();
                a.setTextValue(desc.getValue().trim());
                boolean toAdd = a.getAttachmentPK() == null;
                a.write2DB();
                if (toAdd)
                {
                  //Now add it to this Execution Step
                  if (getExecutionStep().getExecutionStepHasAttachmentList() == null)
                  {
                    getExecutionStep().setExecutionStepHasAttachmentList(new ArrayList<>());
                  }
                  getExecutionStep().addAttachment(a);
                  getExecutionStep().write2DB();
                }
                w.updateCurrentStep();
              }
              catch (Exception ex)
              {
                LOG.log(Level.SEVERE, null, ex);
              }
            }, ButtonOption.focus(),
                    ButtonOption.icon(VaadinIcons.CHECK),
                    ButtonOption.disable())
            .withCancelButton(ButtonOption.icon(VaadinIcons.CLOSE));
    mb.getWindow().setCaption(TRANSLATOR.translate("enter.comment"));
    mb.getWindow().setIcon(ValidationManagerUI.SMALL_APP_ICON);
    desc.setValueChangeMode(ValueChangeMode.LAZY);
    desc.addValueChangeListener(event1 ->
    {
      //Enable only when there is a comment.
      mb.getButton(ButtonType.OK)
              .setEnabled(!step.getLocked()
                      && !desc.getValue().trim().isEmpty());
    });
    mb.open();
  }

  @Override
  public boolean onAdvance()
  {
    //Can only proceed after the current step is executed and documented.
    String answer = result.getValue();
    String answer2 = review.getValue();
    boolean pass = true;
    if (answer == null)
    {
      Notification.show(TRANSLATOR.translate("unable.to.proceed"),
              TRANSLATOR.translate("missing.result"),
              Notification.Type.WARNING_MESSAGE);
    }
    else if (reviewer && answer2 == null)
    {
      Notification.show(TRANSLATOR.translate("unable.to.proceed"),
              TRANSLATOR.translate("missing.reviiew.result"),
              Notification.Type.WARNING_MESSAGE);
    }
    else
    {
      //Check all fields for answers
      for (HasValue field : fields)
      {
        boolean required = field.isRequiredIndicatorVisible();
        boolean empty = field.getValue() == null
                || (field.getValue() instanceof String
                && ((String) field.getValue()).trim().isEmpty());
        if (required && !(field instanceof CheckBox) && empty)
        {
          Notification.show(TRANSLATOR.translate("unable.to.proceed"),
                  TRANSLATOR.translate("missing.answer"),
                  Notification.Type.WARNING_MESSAGE);
          pass = false;
        }
      }
      if (pass)
      {
        try
        {
          //Save the result
          ExecutionResult newResult = ExecutionResultServer
                  .getResult(answer);
          ReviewResult newReview = ReviewResultServer.getReview(answer2);
          getExecutionStep().setExecutionStart(toDate(start.getValue()));
          if (getExecutionStep().getResultId() == null
                  || !Objects.equals(getExecutionStep().getResultId().getId(),
                          newResult.getId()))
          {
            getExecutionStep().setResultId(newResult);
            //Set end date to null to reflect update
            getExecutionStep().setExecutionEnd(null);
          }
          if (reviewer && (getExecutionStep().getReviewResultId() == null
                  || !Objects.equals(getExecutionStep()
                          .getReviewResultId().getId(),
                          newReview.getId())))
          {
            getExecutionStep().setReviewResultId(newReview);
            getExecutionStep().setReviewer(ValidationManagerUI
                    .getInstance().getUser());
          }
          if (getExecutionStep().getExecutionEnd() == null)
          {
            getExecutionStep().setExecutionEnd(new Date());
          }
          if (reviewer && getExecutionStep().getReviewDate() == null)
          {
            getExecutionStep().setReviewDate(new Date());
          }
          if (getExecutionStep().getExecutionStepAnswerList() == null)
          {
            getExecutionStep().setExecutionStepAnswerList(new ArrayList<>());
          }
          if (getExecutionStep().getExecutionStepHasVmUserList() == null)
          {
            getExecutionStep().setExecutionStepHasVmUserList(new ArrayList<>());
          }
          getExecutionStep().getExecutionStepAnswerList().clear();
          for (HasValue field : fields)
          {
            //The field has the field name as data
            if (((AbstractComponent) field).getData() == null)
            {
              pass = false;
              LOG.log(Level.SEVERE, "Field missing data! {0}",
                      field);
            }
            else
            {
              String fieldName = (String) ((AbstractComponent) field)
                      .getData();
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
        }
        catch (Exception ex)
        {
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
  public boolean onBack()
  {
    return getExecutionStep().getStep().getStepSequence() > 1;
  }

  /**
   * @return the step
   */
  public ExecutionStepServer getExecutionStep()
  {
    return step;
  }

  public static boolean getPDFRendering(File source, File dest)
          throws IllegalStateException
  {
    OfficeManager officeManager = null;
    try
    {
      File home = new File(VMSettingServer.getSetting("openoffice.home")
              .getStringVal());
      int port = VMSettingServer
              .getSetting("openoffice.port").getIntVal();
      if (!home.isDirectory() || !home.exists())
      {
        LOG.log(Level.WARNING,
                "Unable to find OpenOffice and/or LibreOffice "
                + "installation at: {0}", home);
        Notification.show(TRANSLATOR.translate("unable.to.render.pdf.title"),
                TRANSLATOR.translate("unable.to.render.pdf.message"),
                Notification.Type.ERROR_MESSAGE);
        return false;
      }
      if (port <= 0)
      {
        LOG.log(Level.WARNING,
                "Unable to find OpenOffice and/or LibreOffice "
                + "installation at port: {0}", port);
        Notification.show(TRANSLATOR.translate("unable.to.render.pdf.title"),
                TRANSLATOR.translate("unable.to.render.pdf.port"),
                Notification.Type.ERROR_MESSAGE);
        return false;
      }
      // Connect to an OpenOffice.org instance running on available port
      try
      {
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
      }
      catch (IllegalStateException ise)
      {
        //Looks like OpenOffice or LibreOffice is not installed
        LOG.log(Level.WARNING,
                "Unable to find OpenOffice and/or LibreOffice "
                + "installation.", ise);
      }
    }
    catch (OfficeException e)
    {
      if (officeManager != null)
      {
        try
        {
          officeManager.stop();
        }
        catch (OfficeException ex)
        {
          LOG.log(Level.SEVERE, null, ex);
        }
      }
      LOG.log(Level.SEVERE, null, e);
    }
    return false;
  }

  private void displayAttachment(AttachmentServer attachment)
  {
    String name = attachment.getFileName();
    byte[] bytes = attachment.getFile();
    boolean ableToDisplay = false;
    try
    {
      for (IFileDisplay fd : Lookup.getDefault()
              .lookupAll(IFileDisplay.class))
      {
        if (fd.supportFile(new File(name)))
        {
          ValidationManagerUI.getInstance()
                  .addWindow(fd.getViewer(fd.loadFile(name,
                          bytes)));
          ableToDisplay = true;
          break;
        }
      }
      if (!ableToDisplay)
      {
        //Convert file to pfd
        PDFDisplay pdf = new PDFDisplay();
        File source = pdf.loadFile(name, bytes);
        File dest = new File(System.getProperty("java.io.tmpdir")
                + System.getProperty("file.separator")
                + name.substring(0, name.lastIndexOf("."))
                + ".pdf");
        getPDFRendering(source, dest);
        if (dest.exists())
        {
          ValidationManagerUI.getInstance().addWindow(pdf.getViewer(dest));
          ableToDisplay = true;
        }
      }
    }
    catch (IOException ex)
    {
      LOG.log(Level.SEVERE,
              "Error loading attachment file: "
              + name, ex);
    }
    if (!ableToDisplay)
    {
      Notification.show(TRANSLATOR.translate("unable.to.render.pdf.title"),
              TRANSLATOR.translate("unable.to.render.pdf.message"),
              Notification.Type.ERROR_MESSAGE);
    }
  }

  private MessageBox getDeletionPrompt(Object data)
  {
    MessageBox mb = MessageBox.createQuestion();
    mb.setData(data);
    mb.asModal(true)
            .withMessage(new Label(TRANSLATOR.translate("remove.item.title")))
            .withButtonAlignment(Alignment.MIDDLE_CENTER)
            .withYesButton(() ->
            {
              try
              {
                if (mb.getData() instanceof ExecutionStepHasAttachment)
                {
                  getExecutionStep().removeAttachment(new AttachmentServer(
                          ((ExecutionStepHasAttachment) mb.getData())
                                  .getAttachment().getAttachmentPK()));
                }
                if (mb.getData() instanceof ExecutionStepHasIssue)
                {
                  getExecutionStep().removeIssue(new IssueServer(
                          ((ExecutionStepHasIssue) mb.getData())
                                  .getIssue()));
                }
                getExecutionStep().write2DB();
                getExecutionStep().update();
                w.updateCurrentStep();
              }
              catch (Exception ex)
              {
                LOG.log(Level.SEVERE, null, ex);
              }
            }, ButtonOption.focus(),
                    ButtonOption.icon(VaadinIcons.CHECK))
            .withNoButton(() ->
            {
              if (mb.getData() instanceof ExecutionStepHasAttachment)
              {
                ExecutionStepHasAttachment esha = (ExecutionStepHasAttachment) mb.getData();
                if (esha.getAttachment().getAttachmentType().getType().equals("comment"))
                {
                  displayComment(new AttachmentServer(esha
                          .getAttachment().getAttachmentPK()));
                }
                else
                {
                  displayAttachment(new AttachmentServer(esha
                          .getAttachment().getAttachmentPK()));
                }
              }
              if (mb.getData() instanceof ExecutionStepHasIssue)
              {
                ExecutionStepHasIssue eshi = (ExecutionStepHasIssue) mb.getData();
                displayIssue(new IssueServer(eshi.getIssue()));
              }
            },
                    ButtonOption.icon(VaadinIcons.CLOSE));
    mb.getWindow().setCaption(TRANSLATOR.translate("issue.detail"));
    mb.getWindow().setIcon(ValidationManagerUI.SMALL_APP_ICON);
    return mb;
  }

  private void updateValue(HasValue field)
  {
    if (field instanceof AbstractComponent
            && ((AbstractComponent) field).getData() != null)
    {
      //Look for the answer in the database
      getExecutionStep().getExecutionStepAnswerList().forEach(answer ->
      {
        if (answer.getFieldName().equals(
                ((AbstractComponent) field).getData()))
        {
          if (field instanceof AbstractTextField)
          {//This includes NumberField
            field.setValue(answer.getFieldAnswer());
          }
          else if (field instanceof CheckBox)
          {
            field.setValue(Boolean.valueOf(answer.getFieldAnswer()));
          }
        }
      });
    }
    else
    {
      LOG.log(Level.SEVERE, "Field missing data! {0}", field);
    }
  }

  /**
   * The DB stores {@link Date} while the Vaadin 8 date fields work with
   * {@link LocalDateTime}.
   *
   * @param date date to convert, may be null
   * @return the equivalent LocalDateTime or null
   */
  private static LocalDateTime toDateTime(Date date)
  {
    return date == null ? null
            : LocalDateTime.ofInstant(date.toInstant(),
                    ZoneId.systemDefault());
  }

  /**
   * The DB stores {@link Date} while the Vaadin 8 date fields work with
   * {@link LocalDateTime}.
   *
   * @param dateTime value to convert, may be null
   * @return the equivalent Date or null
   */
  private static Date toDate(LocalDateTime dateTime)
  {
    return dateTime == null ? null
            : Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
  }
}
