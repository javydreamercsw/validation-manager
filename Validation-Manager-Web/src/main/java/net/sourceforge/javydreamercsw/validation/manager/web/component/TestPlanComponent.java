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
import com.vaadin.ui.Button;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import static com.validation.manager.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMUI;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.controller.TestPlanJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class TestPlanComponent extends Panel {

    private final TestPlan tp;
    private final boolean edit;
    private static final Logger LOG
            = Logger.getLogger(TestPlanComponent.class.getSimpleName());

    public TestPlanComponent(TestPlan tp, boolean edit) {
        setCaption(TRANSLATOR.translate("test.plan.detail"));
        this.tp = tp;
        this.edit = edit;
        init();
    }

    public TestPlanComponent(TestPlan tp, boolean edit, String caption) {
        super(caption);
        this.tp = tp;
        this.edit = edit;
        init();
    }

    private void init() {
        FormLayout layout = new FormLayout();
        setContent(layout);
        addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(tp.getClass());
        binder.setItemDataSource(tp);
        Field<?> name = binder.buildAndBind(TRANSLATOR.translate("general.name"),
                "name");
        layout.addComponent(name);
        Field notes = binder.buildAndBind(TRANSLATOR.translate("general.notes"),
                "notes",
                TextArea.class);
        notes.setSizeFull();
        layout.addComponent(notes);
        Field<?> active = binder.buildAndBind(TRANSLATOR.translate("general.active"),
                "active");
        layout.addComponent(active);
        Field<?> open = binder.buildAndBind(TRANSLATOR.translate("general.open"),
                "isOpen");
        layout.addComponent(open);
        Button cancel = new Button(TRANSLATOR.translate("general.cancel"));
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.discard();
            if (tp.getTestPlanPK() == null) {
                ((VMUI) UI.getCurrent()).displayObject(((VMUI) UI.getCurrent())
                        .getSelectdValue());
            } else {
                ((VMUI) UI.getCurrent()).displayObject(tp, false);
            }
        });
        if (edit) {
            if (tp.getTestPlanPK() == null) {
                //Creating a new one
                Button save = new Button(TRANSLATOR.translate("general.save"));
                save.addClickListener((Button.ClickEvent event) -> {
                    try {
                        tp.setName(name.getValue().toString());
                        tp.setNotes(notes.getValue().toString());
                        tp.setActive((Boolean) active.getValue());
                        tp.setIsOpen((Boolean) open.getValue());
                        new TestPlanJpaController(DataBaseManager
                                .getEntityManagerFactory()).create(tp);
                        setVisible(false);
                        //Recreate the tree to show the addition
                        ((VMUI) UI.getCurrent()).updateProjectList();
                        ((VMUI) UI.getCurrent()).buildProjectTree(tp);
                        ((VMUI) UI.getCurrent()).displayObject(tp, false);
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
                        tp.setName(name.getValue().toString());
                        tp.setNotes(notes.getValue().toString());
                        tp.setActive((Boolean) open.getValue());
                        tp.setIsOpen((Boolean) open.getValue());
                        ((VMUI) UI.getCurrent()).handleVersioning(tp, () -> {
                            try {
                                new TestPlanJpaController(DataBaseManager
                                        .getEntityManagerFactory()).edit(tp);
                                ((VMUI) UI.getCurrent()).displayObject(tp, true);
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
        binder.bindMemberFields(this);
        layout.setSizeFull();
        setSizeFull();
    }
}
