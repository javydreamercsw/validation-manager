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

import com.vaadin.data.Binder;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import static com.validation.manager.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMUI;
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
public final class TestCaseComponent extends Panel {

    private final TestCase t;
    private final boolean edit;
    private static final Logger LOG
            = Logger.getLogger(TestCaseComponent.class.getSimpleName());

    public TestCaseComponent(TestCase t, boolean edit) {
        setCaption(TRANSLATOR.translate("test.detail"));
        this.t = t;
        this.edit = edit;
        init();
    }

    public TestCaseComponent(String caption, TestCase t, boolean edit) {
        super(caption);
        this.t = t;
        this.edit = edit;
        init();
    }

    private void init() {
        FormLayout layout = new FormLayout();
        setContent(layout);
        addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        Binder<TestCase> binder = new Binder<>(TestCase.class);
        binder.setBean(t);
        TextField name = new TextField(TRANSLATOR.translate("general.name"));
        binder.bind(name, "name");
        layout.addComponent(name);
        TextArea summary = new TextArea(TRANSLATOR.translate("general.summary"));
        binder.forField(summary)
                .withConverter(new ByteToStringConverter())
                .bind("summary");
        layout.addComponent(summary);
        DateTimeField creation = new DateTimeField(TRANSLATOR
                .translate("general.creation.date"));
        creation.setResolution(DateTimeResolution.SECOND);
        creation.setDateFormat(VMSettingServer.getSetting("date.format")
                .getStringVal());
        binder.forField(creation)
                .withConverter(this::toDate, this::toDateTime)
                .bind("creationDate");
        layout.addComponent(creation);
        CheckBox active = new CheckBox(TRANSLATOR.translate("general.active"));
        binder.bind(active, "active");
        layout.addComponent(active);
        CheckBox open = new CheckBox(TRANSLATOR.translate("general.open"));
        binder.bind(open, "isOpen");
        layout.addComponent(open);
        ComboBox<TestCaseType> type = new ComboBox<>(TRANSLATOR.translate("general.test.case.type"));
        type.setTextInputAllowed(false);
        type.setRequiredIndicatorVisible(true);
        type.setItems(new TestCaseTypeJpaController(DataBaseManager
                .getEntityManagerFactory())
                .findTestCaseTypeEntities());
        type.setItemCaptionGenerator(temp
                -> TRANSLATOR.translate(temp.getTypeName()));
        if (t.getTestCaseType() == null) {
            //Pre-select Requirement
            type.setValue(new TestCaseTypeServer(5).getEntity());
        }
        binder.bind(type, "testCaseType");
        layout.addComponent(type);
        Button cancel = new Button(TRANSLATOR.translate("general.cancel"));
        cancel.addClickListener((Button.ClickEvent event) -> {
            if (t.getTestCasePK().getId() == 0) {
                ((VMUI) UI.getCurrent()).displayObject(((VMUI) UI.getCurrent())
                        .getSelectdValue());
            } else {
                ((VMUI) UI.getCurrent()).displayObject(t, false);
            }
        });
        if (edit) {
            if (t.getTestCasePK().getId() == 0) {
                //Creating a new one
                Button save = new Button(TRANSLATOR.translate("general.save"));
                save.addClickListener((Button.ClickEvent event) -> {
                    try {
                        t.setName(name.getValue().toString());
                        t.setSummary(summary.getValue().getBytes("UTF-8"));
                        t.setCreationDate(toDate(creation.getValue()));
                        t.setActive(active.getValue());
                        t.setIsOpen(open.getValue());
                        t.getTestPlanList().add((TestPlan) ((VMUI) UI.getCurrent())
                                .getSelectdValue());
                        t.setTestCaseType(type.getValue());
                        new TestCaseJpaController(DataBaseManager
                                .getEntityManagerFactory()).create(t);
                        setVisible(false);
                        //Recreate the tree to show the addition
                        ((VMUI) UI.getCurrent()).updateProjectList();
                        ((VMUI) UI.getCurrent()).buildProjectTree(t);
                        ((VMUI) UI.getCurrent()).displayObject(t, false);
                        ((VMUI) UI.getCurrent()).updateScreen();
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show(TRANSLATOR.translate("general.error.record.creation"),
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(save);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            } else {
                //Editing existing one
                Button update = new Button(TRANSLATOR.translate("general.update"));
                update.addClickListener((Button.ClickEvent event) -> {
                    try {
                        t.setName(name.getValue().toString());
                        t.setSummary(summary.getValue().getBytes("UTF-8"));
                        t.setCreationDate(toDate(creation.getValue()));
                        t.setActive(active.getValue());
                        t.setIsOpen(open.getValue());
                        ((VMUI) UI.getCurrent()).handleVersioning(t, () -> {
                            try {
                                new TestCaseJpaController(DataBaseManager
                                        .getEntityManagerFactory()).edit(t);
                                ((VMUI) UI.getCurrent()).displayObject(t, true);
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
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show(TRANSLATOR.translate("general.error.record.creation"),
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(update);
                hl.addComponent(cancel);
                layout.addComponent(hl);
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
