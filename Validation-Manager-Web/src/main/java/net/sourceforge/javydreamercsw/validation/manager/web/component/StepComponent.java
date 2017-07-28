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

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import com.validation.manager.core.db.DataEntry;
import com.validation.manager.core.db.DataEntryType;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.controller.DataEntryTypeJpaController;
import com.validation.manager.core.db.controller.StepJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.DataEntryServer;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class StepComponent extends Panel {

    private static final InternationalizationProvider TRANSLATOR
            = Lookup.getDefault().lookup(InternationalizationProvider.class);
    private final Step s;
    private final boolean edit;
    private static final Logger LOG
            = Logger.getLogger(StepComponent.class.getSimpleName());
    private final String encoding = "UTF-8";

    public StepComponent(Step s, boolean edit) {
        setCaption(TRANSLATOR.translate("step.detail"));
        this.s = s;
        this.edit = edit;
        init();
    }

    public StepComponent(String caption, Step s, boolean edit) {
        super(caption);
        this.s = s;
        this.edit = edit;
        init();
    }

    private void init() {
        FormLayout layout = new FormLayout();
        setContent(layout);
        addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(s.getClass());
        binder.setItemDataSource(s);
        Field<?> sequence = binder.buildAndBind(TRANSLATOR.translate("general.sequence"),
                "stepSequence");
        layout.addComponent(sequence);
        TextArea text = new TextArea(TRANSLATOR.translate("general.text"));
        text.setConverter(new ByteToStringConverter());
        binder.bind(text, "text");
        layout.addComponent(text);
        TextArea result = new TextArea(TRANSLATOR.translate("expected.result"));
        result.setConverter(new ByteToStringConverter());
        binder.bind(result, "expectedResult");
        layout.addComponent(result);
        Field notes = binder.buildAndBind(TRANSLATOR.translate("general.notes"),
                "notes", TextArea.class);
        notes.setSizeFull();
        layout.addComponent(notes);
        if (!s.getRequirementList().isEmpty() && !edit) {
            layout.addComponent(((ValidationManagerUI) UI.getCurrent())
                    .getDisplayRequirementList(
                            TRANSLATOR.translate("related.requirements"),
                            s.getRequirementList()));
        } else {
            AbstractSelect requirements = ((ValidationManagerUI) UI.getCurrent())
                    .getRequirementSelectionComponent();
            //Select the exisitng ones.
            if (s.getRequirementList() != null) {
                s.getRequirementList().forEach((r) -> {
                    requirements.select(r);
                });
            }
            requirements.addValueChangeListener(event -> {
                Set<Requirement> selected
                        = (Set<Requirement>) event.getProperty().getValue();
                s.getRequirementList().clear();
                selected.forEach(r -> {
                    s.getRequirementList().add(r);
                });
            });
            layout.addComponent(requirements);
        }
        DataEntryComponent fields = new DataEntryComponent(edit);
        binder.bind(fields, "dataEntryList");
        layout.addComponent(fields);
        binder.setReadOnly(edit);
        Button cancel = new Button(TRANSLATOR.translate("general.cancel"));
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.discard();
            if (s.getStepPK() == null) {
                ((ValidationManagerUI) UI.getCurrent())
                        .displayObject(((ValidationManagerUI) UI.getCurrent())
                                .getTree().getValue());
            } else {
                ((ValidationManagerUI) UI.getCurrent()).displayObject(s, false);
            }
        });
        if (edit) {
            Button add = new Button(TRANSLATOR.translate("add.field"));
            add.addClickListener(listener -> {
                VMWindow w = new VMWindow();
                FormLayout fl = new FormLayout();
                ComboBox newType = new ComboBox(TRANSLATOR
                        .translate("general.type"));
                newType.setNewItemsAllowed(false);
                newType.setTextInputAllowed(false);
                newType.addValidator(new NullValidator(TRANSLATOR
                        .translate("message.required.field.missing")
                        .replaceAll("%f",
                                TRANSLATOR.translate("general.type")),
                        false));
                BeanItemContainer<DataEntryType> container
                        = new BeanItemContainer<>(DataEntryType.class,
                                new DataEntryTypeJpaController(DataBaseManager
                                        .getEntityManagerFactory())
                                        .findDataEntryTypeEntities());
                newType.setContainerDataSource(container);
                newType.getItemIds().forEach(id -> {
                    DataEntryType temp = ((DataEntryType) id);
                    newType.setItemCaption(id,
                            TRANSLATOR.translate(temp.getTypeName()));
                });
                fl.addComponent(newType);
                TextField tf = new TextField(TRANSLATOR.translate("general.name"));
                fl.addComponent(tf);
                HorizontalLayout hl = new HorizontalLayout();
                Button a = new Button(TRANSLATOR.translate("general.add"));
                a.addClickListener(l -> {
                    DataEntryType det = (DataEntryType) newType.getValue();
                    DataEntry de = null;
                    switch (det.getId()) {
                        case 1:
                            de = DataEntryServer.getStringField(tf.getValue());
                            break;
                        case 2:
                            de = DataEntryServer.getNumericField(tf.getValue(),
                                    null, null);
                            break;
                        case 3:
                            de = DataEntryServer.getBooleanField(tf.getValue());
                            break;
                        case 4:
                            de = DataEntryServer.getAttachmentField(tf.getValue());
                            break;
                    }
                    if (de != null) {
                        s.getDataEntryList().add(de);
                        ((ValidationManagerUI) UI.getCurrent()).displayObject(s);
                    }
                    UI.getCurrent().removeWindow(w);
                });
                hl.addComponent(a);
                Button c = new Button(TRANSLATOR.translate("general.cancel"));
                c.addClickListener(l -> {
                    UI.getCurrent().removeWindow(w);
                });
                hl.addComponent(c);
                fl.addComponent(hl);
                w.setContent(fl);
                UI.getCurrent().addWindow(w);
            });
            if (s.getStepPK() == null) {
                //Creating a new one
                Button save = new Button(TRANSLATOR.translate("general.save"));
                save.addClickListener(listener -> {
                    try {
                        s.setExpectedResult(((TextArea) result).getValue()
                                .getBytes(encoding));
                        s.setNotes(notes.getValue() == null ? ""
                                : notes.getValue().toString());
                        s.setStepSequence(Integer.parseInt(sequence
                                .getValue().toString()));
                        s.setTestCase((TestCase) ((ValidationManagerUI) UI
                                .getCurrent()).getTree().getValue());
                        s.setText(text.getValue().getBytes(encoding));
                        if (s.getRequirementList() == null) {
                            s.setRequirementList(new ArrayList<>());
                        }
                        new StepJpaController(DataBaseManager
                                .getEntityManagerFactory()).create(s);
                        setVisible(false);
                        //Recreate the tree to show the addition
                        ((ValidationManagerUI) UI.getCurrent()).updateProjectList();
                        ((ValidationManagerUI) UI.getCurrent()).updateScreen();
                        ((ValidationManagerUI) UI.getCurrent()).displayObject(s);
                        ((ValidationManagerUI) UI.getCurrent()).buildProjectTree(s);
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show(TRANSLATOR.translate("general.error.record.creation"),
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(save);
                hl.addComponent(add);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            } else {
                //Editing existing one
                Button update = new Button(TRANSLATOR.translate("general.update"));
                update.addClickListener((Button.ClickEvent event) -> {
                    try {
                        s.setExpectedResult(((TextArea) result).getValue()
                                .getBytes(encoding));
                        s.setNotes(notes.getValue().toString());
                        s.setStepSequence(Integer.parseInt(sequence.getValue().toString()));
                        s.setText(text.getValue().getBytes(encoding));
                        if (s.getRequirementList() == null) {
                            s.setRequirementList(new ArrayList<>());
                        }
                        ((ValidationManagerUI) UI.getCurrent()).handleVersioning(s, () -> {
                            try {
                                new StepJpaController(DataBaseManager
                                        .getEntityManagerFactory()).edit(s);
                            } catch (NonexistentEntityException ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show(TRANSLATOR.translate("general.error.record.update"),
                                        ex.getLocalizedMessage(),
                                        Notification.Type.ERROR_MESSAGE);
                            } catch (Exception ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show(TRANSLATOR.translate("general.error.record.update"),
                                        ex.getLocalizedMessage(),
                                        Notification.Type.ERROR_MESSAGE);
                            }
                        });
                        ((ValidationManagerUI) UI.getCurrent()).displayObject(s);
                    } catch (UnsupportedEncodingException | NumberFormatException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show(TRANSLATOR.translate("general.error.record.creation"),
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(update);
                hl.addComponent(add);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            }
        }
        binder.setReadOnly(!edit);
        binder.bindMemberFields(this);
        layout.setSizeFull();
        setSizeFull();
    }
}
