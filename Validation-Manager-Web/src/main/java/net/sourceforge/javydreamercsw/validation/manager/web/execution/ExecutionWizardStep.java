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

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.converter.StringToDoubleConverter;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Resource;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
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
import tm.kod.widgets.numberfield.NumberField;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ExecutionWizardStep implements WizardStep
{

  private final Wizard w;
  private final ExecutionStepServer step;
  private final ComboBox result
          = new ComboBox(TRANSLATOR.translate("general.result"));
  private final ComboBox review
          = new ComboBox(TRANSLATOR.translate("quality.review"));
  private final ComboBox issueType
          = new ComboBox(TRANSLATOR.translate("issue.type"));
  private Button attach;
  private Button bug;
  private Button comment;
  private DateField start;
  private DateField end;
  private DateField reviewDate;
  private static final Logger LOG
          = Logger.getLogger(ExecutionWizardStep.class.getSimpleName());
  private boolean reviewer = false;
  private final List<AbstractField> fields = new ArrayList<>();

  public ExecutionWizardStep(Wizard w, ExecutionStep step,
          boolean reviewer)
  {
    this.reviewer = reviewer;
    this.w = w;
    this.step = new ExecutionStepServer(step);
    issueType.setSizeFull();
    issueType.setReadOnly(false);
    issueType.setRequired(true);
    issueType.setRequiredError(TRANSLATOR.translate("missing.type"));
    IssueTypeJpaController it
            = new IssueTypeJpaController(DataBaseManager
                    .getEntityManagerFactory());
    it.findIssueTypeEntities().forEach(type ->
    {
      String item = Lookup.getDefault().lookup(InternationalizationProvider.class)
              .translate(type.getTypeName());
      issueType.addItem(type);
      issueType.setItemCaption(type, item);
      switch (type.getId())
      {
        case 1:
          issueType.setItemIcon(type, VaadinIcons.BUG);
          break;
        case 2:
          issueType.setItemIcon(type, VaadinIcons.EYE);
          break;
        case 3:
          issueType.setItemIcon(type, VaadinIcons.QUESTION);
          break;
      }
      if (type.getTypeName().equals("observation.name"))
      {
        issueType.setValue(type);
      }
    });
    result.setReadOnly(false);
    result.setRequired(true);
    result.setRequiredError(TRANSLATOR.translate("missing.result"));
    result.setTextInputAllowed(false);
    review.setReadOnly(false);
    review.setRequired(true);
    review.setRequiredError(TRANSLATOR.translate("missing.reviiew.result"));
    review.setTextInputAllowed(false);
    ReviewResultJpaController c2
            = new ReviewResultJpaController(DataBaseManager
                    .getEntityManagerFactory());
    c2.findReviewResultEntities().forEach(r ->
    {
      String item = Lookup.getDefault().lookup(InternationalizationProvider.class)
              .translate(r.getReviewName());
      review.addItem(r.getReviewName());
      review.setItemCaption(r.getReviewName(), item);
      Resource icon;
      switch (r.getId())
      {
        case 1:
          icon = VaadinIcons.CHECK;
          break;
        case 2:
          icon = VaadinIcons.CLOSE;
          break;
        default:
          icon = VaadinIcons.CLOCK;
          break;
      }
      review.setItemIcon(r.getReviewName(), icon);
    });
    ExecutionResultJpaController c
            = new ExecutionResultJpaController(DataBaseManager
                    .getEntityManagerFactory());
    c.findExecutionResultEntities().forEach(r ->
    {
      String item = Lookup.getDefault().lookup(InternationalizationProvider.class)
              .translate(r.getResultName());
      result.addItem(r.getResultName());
      result.setItemCaption(r.getResultName(), item);
      Resource icon;
      switch (r.getId())
      {
        case 1:
          icon = VaadinIcons.CHECK;
          break;
        case 2:
          icon = VaadinIcons.CLOSE;
          break;
        case 3:
          icon = VaadinIcons.PAUSE;
          break;
        default:
          icon = VaadinIcons.CLOCK;
          break;
      }
      result.setItemIcon(r.getResultName(), icon);
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
    BeanFieldGroup binder = new BeanFieldGroup(getExecutionStep().getStep().getClass());
    binder.setItemDataSource(getExecutionStep().getStep());
    TextArea text = new TextArea(TRANSLATOR.translate("general.text"));
    text.setConverter(new ByteToStringConverter());
    binder.bind(text, "text");
    text.setSizeFull();
    layout.addComponent(text);
    Field notes = binder.buildAndBind(TRANSLATOR.translate("general.notes"),
            "notes", TextArea.class);
    notes.setSizeFull();
    layout.addComponent(notes);
    if (getExecutionStep().getExecutionStart() != null)
    {
      start = new DateField(TRANSLATOR.translate("start.date"));
      start.setResolution(Resolution.SECOND);
      start.setDateFormat(VMSettingServer.getSetting("date.format")
              .getStringVal());
      start.setValue(getExecutionStep().getExecutionStart());
      start.setReadOnly(true);
      layout.addComponent(start);
    }
    if (getExecutionStep().getExecutionEnd() != null)
    {
      end = new DateField(TRANSLATOR.translate("end.date"));
      end.setDateFormat(VMSettingServer.getSetting("date.format")
              .getStringVal());
      end.setResolution(Resolution.SECOND);
      end.setValue(getExecutionStep().getExecutionEnd());
      end.setReadOnly(true);
      layout.addComponent(end);
    }
    binder.setReadOnly(true);
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
      reviewDate = new DateField(TRANSLATOR
              .translate("review.date"));
      reviewDate.setDateFormat(VMSettingServer.getSetting("date.format")
              .getStringVal());
      reviewDate.setResolution(Resolution.SECOND);
      reviewDate.setValue(getExecutionStep().getReviewDate());
      reviewDate.setReadOnly(true);
      layout.addComponent(reviewDate);
    }
    if (VMSettingServer.getSetting("show.expected.result").getBoolVal())
    {
      TextArea expectedResult
              = new TextArea(TRANSLATOR.translate("expected.result"));
      expectedResult.setConverter(new ByteToStringConverter());
      binder.bind(expectedResult, "expectedResult");
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
          tf.setRequired(true);
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
              tf.setRequiredError(error);
              tf.setRequired(DataEntryServer
                      .getProperty(de,
                              "property.required")
                      .getPropertyValue().equals("true"));
              tf.addValidator((Object val) ->
              {
                //We have an expected result and a match case requirement
                if (stringCase != null
                        && stringCase.getPropertyValue().equals("true")
                        ? !((String) val).equals(r.getPropertyValue())
                        : !((String) val).equalsIgnoreCase(r.getPropertyValue()))
                {
                  throw new InvalidValueException(error);
                }
              });
            }
          }
          fields.add(tf);
          //Set value if already recorded
          updateValue(tf);
          layout.addComponent(tf);
          break;
        case 2://Numeric
          NumberField field = new NumberField(TRANSLATOR
                  .translate(de.getEntryName()));
          field.setSigned(true);
          field.setUseGrouping(true);
          field.setGroupingSeparator(',');
          field.setDecimalSeparator('.');
          field.setConverter(new StringToDoubleConverter());
          field.setRequired(DataEntryServer
                  .getProperty(de,
                          "property.required")
                  .getPropertyValue().equals("true"));
          field.setData(de.getEntryName());
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
            field.setRequiredError(error);
            field.addValidator(new DoubleRangeValidator(error,
                    min, max));
          }
          fields.add(field);
          //Set value if already recorded
          updateValue(field);
          layout.addComponent(field);
          break;
        case 3://Boolean
          CheckBox cb = new CheckBox(TRANSLATOR
                  .translate(de.getEntryName()));
          cb.setData(de.getEntryName());
          cb.setRequired(DataEntryServer
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
              cb.addValidator((Object val) ->
              {
                if (!val.toString().equals(r.getPropertyValue()))
                {
                  throw new InvalidValueException(error);
                }
              });
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
    BeanFieldGroup binder = new BeanFieldGroup(is.getClass());
    binder.setItemDataSource(is);
    Field title = binder.buildAndBind(TRANSLATOR.translate("general.summary"),
            "title",
            TextField.class);
    title.setSizeFull();
    layout.addComponent(title);
    Field desc = binder.buildAndBind(TRANSLATOR.translate("general.description"),
            "description",
            TextArea.class);
    desc.setSizeFull();
    layout.addComponent(desc);
    DateField creation = (DateField) binder
            .buildAndBind(TRANSLATOR.translate("creation.time"),
                    "creationTime",
                    DateField.class);
    creation.setReadOnly(true);
    creation.setDateFormat(VMSettingServer.getSetting("date.format")
            .getStringVal());
    creation.setResolution(Resolution.SECOND);
    layout.addComponent(creation);
    //Add the result
    layout.addComponent(issueType);
    if (is.getIssueType() != null)
    {
      issueType.setValue(is.getIssueType().getTypeName());
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
                issue.setDescription(((TextArea) desc).getValue().trim());
                issue.setIssueType((IssueType) issueType.getValue());
                issue.setCreationTime(creation.getValue());
                issue.setTitle((String) title.getValue());
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
    ((TextArea) desc).addTextChangeListener((TextChangeEvent event1) ->
    {
      //Enable if there is a description change.
      mb.getButton(ButtonType.OK)
              .setEnabled(!step.getLocked()
                      && !event1.getText().trim().isEmpty());
    });
    ((TextField) title).addTextChangeListener((TextChangeEvent event1) ->
    {
      //Enable if there is a title change.
      mb.getButton(ButtonType.OK)
              .setEnabled(!step.getLocked()
                      && !event1.getText().trim().isEmpty());
    });
    mb.open();
  }

  private void displayComment(AttachmentServer as)
  {
    Panel form = new Panel(TRANSLATOR.translate("general.comment"));
    FormLayout layout = new FormLayout();
    form.setContent(layout);
    BeanFieldGroup binder = new BeanFieldGroup(as.getClass());
    binder.setItemDataSource(as);
    Field desc = binder.buildAndBind(TRANSLATOR.translate("general.text"),
            "textValue",
            TextArea.class);
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
                a.setTextValue(((TextArea) desc).getValue().trim());
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
    ((TextArea) desc).addTextChangeListener((TextChangeEvent event1) ->
    {
      //Enable only when there is a comment.
      mb.getButton(ButtonType.OK)
              .setEnabled(!step.getLocked()
                      && !event1.getText().trim().isEmpty());
    });
    mb.open();
  }

  @Override
  public boolean onAdvance()
  {
    //Can only proceed after the current step is executed and documented.
    String answer = ((String) result.getValue());
    String answer2 = ((String) review.getValue());
    boolean pass = true;
    if (answer == null)
    {
      Notification.show(TRANSLATOR.translate("unable.to.proceed"),
              result.getRequiredError(),
              Notification.Type.WARNING_MESSAGE);
    }
    else if (reviewer && answer2 == null)
    {
      Notification.show(TRANSLATOR.translate("unable.to.proceed"),
              review.getRequiredError(),
              Notification.Type.WARNING_MESSAGE);
    }
    else
    {
      //Check all fields for answers
      for (AbstractField field : fields)
      {
        if (field.isRequired() && !(field instanceof CheckBox)
                && field.isEmpty())
        {
          Notification.show(TRANSLATOR.translate("unable.to.proceed"),
                  field.getRequiredError(),
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
          getExecutionStep().setExecutionStart(start.getValue());
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
          for (AbstractField field : fields)
          {
            //The field has the field name as data
            if (field.getData() == null)
            {
              pass = false;
              LOG.log(Level.SEVERE, "Field missing data! {0}",
                      field);
            }
            else
            {
              String fieldName = (String) field.getData();
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
              stepAnswer.setFieldAnswer(field.getValue().toString());
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

  private void updateValue(AbstractField field)
  {
    if (field.getData() != null)
    {
      //Look for the answer in the database
      getExecutionStep().getExecutionStepAnswerList().forEach(answer ->
      {
        if (answer.getFieldName().equals(field.getData()))
        {
          if (field instanceof AbstractTextField)
          {//This includes NumberField
            field.setValue(answer.getFieldAnswer());
          }
          else if (field instanceof CheckBox)
          {
            field.setValue(answer.getFieldAnswer().equals("true"));
          }
        }
      });
    }
    else
    {
      LOG.log(Level.SEVERE, "Field missing data! {0}", field);
    }
  }
}
