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

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import static net.sourceforge.javydreamercsw.validation.manager.web.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.DataBaseManager;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestCaseType;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.controller.TestCaseJpaController;
import com.validation.manager.core.db.controller.TestCaseTypeJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.TestCaseTypeServer;
import com.validation.manager.core.server.core.VMSettingServer;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class TestCaseComponent extends VerticalLayout {

    private final TestCase t;
    private final boolean edit;
    private static final Logger LOG
            = Logger.getLogger(TestCaseComponent.class.getSimpleName());

    public TestCaseComponent(TestCase t, boolean edit) {
        add(new com.vaadin.flow.component.html.Span(TRANSLATOR.translate("test.detail")));
        this.t = t;
        this.edit = edit;
        init();
    }

    public TestCaseComponent(String caption, TestCase t, boolean edit) {
        add(new com.vaadin.flow.component.html.Span(caption));
        this.t = t;
        this.edit = edit;
        init();
    }

    private void init() {
        FormLayout layout = new FormLayout();
        add(layout);
        Binder<TestCase> binder = new Binder<>(TestCase.class);
        binder.setBean(t);
        TextField name = new TextField(TRANSLATOR.translate("general.name"));
        binder.bind(name, "name");
        layout.add(name);
        TextArea summary = new TextArea(TRANSLATOR.translate("general.summary"));
        binder.forField(summary)
                .withConverter(new ByteToStringConverter())
                .bind("summary");
        layout.add(summary);
        DateTimePicker creation = new DateTimePicker(TRANSLATOR
                .translate("general.creation.date"));
        //Flow DateTimePicker always shows seconds
        creation.setDatePlaceholder(
                VMSettingServer.getSetting("date.format").getStringVal());
        binder.forField(creation)
                .withConverter(this::toDate, this::toDateTime)
                .bind("creationDate");
        layout.add(creation);
        Checkbox active = new Checkbox(TRANSLATOR.translate("general.active"));
        binder.bind(active, "active");
        layout.add(active);
        Checkbox open = new Checkbox(TRANSLATOR.translate("general.open"));
        binder.bind(open, "isOpen");
        layout.add(open);
        ComboBox<TestCaseType> type = new ComboBox<>(TRANSLATOR.translate("general.test.case.type"));
        type.setAllowCustomValue(false);
        type.setRequiredIndicatorVisible(true);
        type.setItems(new TestCaseTypeJpaController(DataBaseManager
                .getEntityManagerFactory())
                .findTestCaseTypeEntities());
        type.setItemLabelGenerator(temp
                -> TRANSLATOR.translate(temp.getTypeName()));
        if (t.getTestCaseType() == null) {
            //Pre-select Requirement
            type.setValue(new TestCaseTypeServer(5).getEntity());
        }
        binder.bind(type, "testCaseType");
        layout.add(type);
        Button cancel = new Button(TRANSLATOR.translate("general.cancel"));
        cancel.addClickListener((event) -> {
            if (t.getTestCasePK().getId() == 0) {
                ((ValidationManagerUI) UI.getCurrent()).displayObject(((ValidationManagerUI) UI.getCurrent())
                        .getSelectdValue());
            } else {
                ((ValidationManagerUI) UI.getCurrent()).displayObject(t, false);
            }
        });
        if (edit) {
            if (t.getTestCasePK().getId() == 0) {
                //Creating a new one
                Button save = new Button(TRANSLATOR.translate("general.save"));
                save.addClickListener((event) -> {
                    try {
                        t.setName(name.getValue().toString());
                        t.setSummary(summary.getValue().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                        t.setCreationDate(toDate(creation.getValue()));
                        t.setActive(active.getValue());
                        t.setIsOpen(open.getValue());
                        t.getTestPlanList().add((TestPlan) ((ValidationManagerUI) UI.getCurrent())
                                .getSelectdValue());
                        t.setTestCaseType(type.getValue());
                        new TestCaseJpaController(DataBaseManager
                                .getEntityManagerFactory()).create(t);
                        setVisible(false);
                        //Recreate the tree to show the addition
                        ((ValidationManagerUI) UI.getCurrent()).updateProjectList();
                        ((ValidationManagerUI) UI.getCurrent()).buildProjectTree(t);
                        ((ValidationManagerUI) UI.getCurrent()).displayObject(t, false);
                        ((ValidationManagerUI) UI.getCurrent()).updateScreen();
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show(TRANSLATOR.translate("general.error.record.creation"));
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.add(save);
                hl.add(cancel);
                layout.add(hl);
            } else {
                //Editing existing one
                Button update = new Button(TRANSLATOR.translate("general.update"));
                update.addClickListener((event) -> {
                    try {
                        t.setName(name.getValue().toString());
                        t.setSummary(summary.getValue().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                        t.setCreationDate(toDate(creation.getValue()));
                        t.setActive(active.getValue());
                        t.setIsOpen(open.getValue());
                        ((ValidationManagerUI) UI.getCurrent()).handleVersioning(t, () -> {
                            try {
                                new TestCaseJpaController(DataBaseManager
                                        .getEntityManagerFactory()).edit(t);
                                ((ValidationManagerUI) UI.getCurrent()).displayObject(t, true);
                            } catch (NonexistentEntityException ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show(TRANSLATOR.translate("general.error.record.update"));
                            } catch (Exception ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show(TRANSLATOR.translate("general.error.record.update"));
                            }
                        });
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show(TRANSLATOR.translate("general.error.record.creation"));
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.add(update);
                hl.add(cancel);
                layout.add(hl);
            }
        }
        binder.setReadOnly(!edit);
        creation.setEnabled(false);
        layout.setSizeFull();
        setSizeFull();
    }

    private Date toDate(LocalDateTime dateTime) {
        return dateTime == null ? null
                : Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private LocalDateTime toDateTime(Date date) {
        return date == null ? null
                : LocalDateTime.ofInstant(date.toInstant(),
                        ZoneId.systemDefault());
    }
}
