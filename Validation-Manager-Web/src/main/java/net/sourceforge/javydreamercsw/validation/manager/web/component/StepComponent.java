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

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
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
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class StepComponent extends VerticalLayout {

    private static final InternationalizationProvider TRANSLATOR
            = org.openide.util.Lookup.getDefault().lookup(InternationalizationProvider.class);
    private final Step s;
    private final boolean edit;
    private static final Logger LOG
            = Logger.getLogger(StepComponent.class.getSimpleName());
    private final String encoding = "UTF-8";

    public StepComponent(Step s, boolean edit) {
        this.s = s;
        this.edit = edit;
        init();
    }

    public StepComponent(String caption, Step s, boolean edit) {
        this.s = s;
        this.edit = edit;
        add(new com.vaadin.flow.component.html.Span(caption));
        init();
    }

    private void init() {
        FormLayout layout = new FormLayout();
        add(layout);
        com.vaadin.flow.data.binder.Binder<Step> binder
                = new com.vaadin.flow.data.binder.Binder<>(Step.class);
        binder.setBean(s);
        TextField sequence = new TextField(TRANSLATOR.translate("general.sequence"));
        binder.bind(sequence, "stepSequence");
        TextArea text = new TextArea(TRANSLATOR.translate("general.text"));
        binder.forField(text)
                .withConverter(new ByteToStringConverter())
                .bind("text");
        TextArea result = new TextArea(TRANSLATOR.translate("expected.result"));
        binder.forField(result)
                .withConverter(new ByteToStringConverter())
                .bind("expectedResult");
        TextArea notes = new TextArea(TRANSLATOR.translate("general.notes"));
        binder.bind(notes, "notes");
        notes.setSizeFull();
        if (!s.getRequirementList().isEmpty() && !edit) {
            layout.add(((ValidationManagerUI) UI.getCurrent())
                    .getDisplayRequirementList(
                            TRANSLATOR.translate("related.requirements"),
                            s.getRequirementList()));
        } else {
            HasValue.ValueChangeListener<HasValue.ValueChangeEvent<Set<Requirement>>>
                    requirementsListener = event -> {
                Set<Requirement> selected = event.getValue();
                s.getRequirementList().clear();
                selected.forEach(r -> {
                    s.getRequirementList().add(r);
                });
            };
            @SuppressWarnings("unchecked")
            com.vaadin.flow.data.selection.MultiSelect<
                    com.vaadin.flow.component.Component, Requirement> requirements
                    = (com.vaadin.flow.data.selection.MultiSelect) ((ValidationManagerUI)
                            UI.getCurrent()).getRequirementSelectionComponent();
            //Select the exisitng ones.
            if (s.getRequirementList() != null) {
                requirements.setValue(new HashSet<>(s.getRequirementList()));
            }
            requirements.addValueChangeListener(requirementsListener);
            layout.add((com.vaadin.flow.component.Component) requirements);
        }
        DataEntryComponent fields = new DataEntryComponent(edit);
        layout.add(fields);
        binder.setReadOnly(edit);
        Button cancel = new Button(TRANSLATOR.translate("general.cancel"));
        cancel.addClickListener((event) -> {
            if (s.getStepPK() == null) {
                ((ValidationManagerUI) UI.getCurrent())
                        .displayObject(((ValidationManagerUI) UI.getCurrent())
                                .getTree().asSingleSelect().getValue());
            } else {
                ((ValidationManagerUI) UI.getCurrent()).displayObject(s, false);
            }
        });
        if (edit) {
            Button add = new Button(TRANSLATOR.translate("add.field"));
            add.addClickListener(listener -> {
                VMWindow w = new VMWindow();
                FormLayout fl = new FormLayout();
                ComboBox<DataEntryType> newType = new ComboBox<>(TRANSLATOR
                        .translate("general.type"));
                newType.setRequiredIndicatorVisible(true);
                newType.setItems(new DataEntryTypeJpaController(DataBaseManager
                        .getEntityManagerFactory())
                        .findDataEntryTypeEntities());
                newType.setItemLabelGenerator(type
                        -> TRANSLATOR.translate(type.getTypeName()));
                fl.add(newType);
                TextField tf = new TextField(TRANSLATOR.translate("general.name"));
                fl.add(tf);
                HorizontalLayout hl = new HorizontalLayout();
                Button a = new Button(TRANSLATOR.translate("general.add"));
                a.addClickListener(l -> {
                    if (newType.getValue() == null) {
                        Notification.show(TRANSLATOR
                                .translate("message.required.field.missing")
                                .replaceAll("%f", TRANSLATOR.translate("general.type")));
                        return;
                    }
                    DataEntryType det = newType.getValue();
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
                    w.close();
                });
                hl.add(a);
                Button c = new Button(TRANSLATOR.translate("general.cancel"));
                c.addClickListener(l -> {
                    w.close();
                });
                hl.add(c);
                fl.add(hl);
                w.add(fl);
                ((ValidationManagerUI) UI.getCurrent()).openDialog(w);
            });
            if (s.getStepPK() == null) {
                //Creating a new one
                Button save = new Button(TRANSLATOR.translate("general.save"));
                save.addClickListener(listener -> {
                    try {
                        s.setExpectedResult(result.getValue()
                                .getBytes(encoding));
                        s.setNotes(notes.getValue() == null ? ""
                                : notes.getValue().toString());
                        s.setStepSequence(Integer.parseInt(sequence
                                .getValue().toString()));
                        s.setTestCase((TestCase) ((ValidationManagerUI) UI
                                .getCurrent()).getTree().asSingleSelect().getValue());
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
                        Notification.show(TRANSLATOR.translate("general.error.record.creation"));
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.add(save, add, cancel);
                layout.add(hl);
            } else {
                //Editing existing one
                Button update = new Button(TRANSLATOR.translate("general.update"));
                update.addClickListener((event) -> {
                    try {
                        s.setExpectedResult(result.getValue()
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
                                Notification.show(TRANSLATOR
                                        .translate("general.error.record.update"));
                            } catch (Exception ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show(TRANSLATOR
                                        .translate("general.error.record.update"));
                            }
                        });
                        ((ValidationManagerUI) UI.getCurrent()).displayObject(s);
                    } catch (UnsupportedEncodingException | NumberFormatException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show(TRANSLATOR.translate("general.error.record.creation"));
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.add(update, add, cancel);
                layout.add(hl);
            }
        } else {
            HorizontalLayout hl = new HorizontalLayout();
            hl.add(cancel);
            layout.add(hl);
        }
        layout.setSizeFull();
        setSizeFull();
    }
}
