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
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
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
import java.io.UnsupportedEncodingException;
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
        BeanFieldGroup binder = new BeanFieldGroup(t.getClass());
        binder.setItemDataSource(t);
        Field<?> name = binder.buildAndBind(TRANSLATOR.translate("general.name"),
                "name");
        layout.addComponent(name);
        TextArea summary = new TextArea(TRANSLATOR.translate("general.summary"));
        summary.setConverter(new ByteToStringConverter());
        binder.bind(summary, "summary");
        layout.addComponent(summary);
        PopupDateField creation = new PopupDateField(TRANSLATOR
                .translate("general.creation.date"));
        creation.setResolution(Resolution.SECOND);
        creation.setDateFormat(VMSettingServer.getSetting("date.format")
                .getStringVal());
        binder.bind(creation, "creationDate");
        layout.addComponent(creation);
        Field<?> active = binder.buildAndBind(TRANSLATOR.translate("general.active"),
                "active");
        layout.addComponent(active);
        Field<?> open = binder.buildAndBind(TRANSLATOR.translate("general.open"),
                "isOpen");
        layout.addComponent(open);
        ComboBox type = new ComboBox(TRANSLATOR.translate("general.test.case.type"));
        type.setNewItemsAllowed(false);
        type.setTextInputAllowed(false);
        type.addValidator(new NullValidator(TRANSLATOR
                .translate("message.required.field.missing")
                .replaceAll("%f",
                        TRANSLATOR.translate("general.test.case.type")),
                false));
        BeanItemContainer<TestCaseType> container
                = new BeanItemContainer<>(TestCaseType.class,
                        new TestCaseTypeJpaController(DataBaseManager
                                .getEntityManagerFactory())
                                .findTestCaseTypeEntities());
        type.setContainerDataSource(container);
        type.getItemIds().forEach(id -> {
            TestCaseType temp = ((TestCaseType) id);
            type.setItemCaption(id,
                    TRANSLATOR.translate(temp.getTypeName()));
        });
        if (t.getTestCaseType() == null) {
            //Pre-select Requirement
            type.setValue(new TestCaseTypeServer(5).getEntity());
        }
        binder.bind(type, "testCaseType");
        layout.addComponent(type);
        Button cancel = new Button(TRANSLATOR.translate("general.cancel"));
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.discard();
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
                        t.setCreationDate((Date) creation.getValue());
                        t.setActive((Boolean) active.getValue());
                        t.setIsOpen((Boolean) open.getValue());
                        t.getTestPlanList().add((TestPlan) ((VMUI) UI.getCurrent())
                                .getSelectdValue());
                        t.setTestCaseType((TestCaseType) type.getValue());
                        new TestCaseJpaController(DataBaseManager
                                .getEntityManagerFactory()).create(t);
                        setVisible(false);
                        //Recreate the tree to show the addition
                        ((VMUI) UI.getCurrent()).updateProjectList();
                        ((VMUI) UI.getCurrent()).buildProjectTree(t);
                        ((VMUI) UI.getCurrent()).displayObject(t, false);
                        ((VMUI) UI.getCurrent()).updateScreen();
                    } catch (UnsupportedEncodingException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show(TRANSLATOR.translate("general.error.record.creation"),
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
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
                        t.setCreationDate((Date) creation.getValue());
                        t.setActive((Boolean) active.getValue());
                        t.setIsOpen((Boolean) open.getValue());
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
                    } catch (UnsupportedEncodingException ex) {
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
        binder.bindMemberFields(this);
        layout.setSizeFull();
        setSizeFull();
    }
}
