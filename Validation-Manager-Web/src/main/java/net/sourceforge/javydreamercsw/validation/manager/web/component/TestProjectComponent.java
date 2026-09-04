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
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
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
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.controller.TestProjectJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class TestProjectComponent extends Panel {

    private final TestProject tp;
    private final boolean edit;
    private static final Logger LOG
            = Logger.getLogger(TestPlanComponent.class.getSimpleName());

    public TestProjectComponent(TestProject tp, boolean edit) {
        this.tp = tp;
        this.edit = edit;
        setCaption(TRANSLATOR.translate("test.project.detail"));
        init();
    }

    public TestProjectComponent(TestProject tp, boolean edit, String caption) {
        super(caption);
        this.tp = tp;
        this.edit = edit;
        init();
    }

    private void init() {
        FormLayout layout = new FormLayout();
        setContent(layout);
        addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        Binder<TestProject> binder = new Binder<>(TestProject.class);
        binder.setBean(tp);
        TextField name = new TextField(TRANSLATOR.translate("general.name"));
        binder.bind(name, "name");
        layout.addComponent(name);
        TextArea notes = new TextArea(TRANSLATOR.translate("general.notes"));
        binder.bind(notes, "notes");
        notes.setSizeFull();
        layout.addComponent(notes);
        CheckBox active = new CheckBox(TRANSLATOR.translate("general.active"));
        binder.bind(active, "active");
        layout.addComponent(active);
        Button cancel = new Button(TRANSLATOR.translate("general.cancel"));
        cancel.addClickListener((Button.ClickEvent event) -> {
            if (tp.getId() == null) {
                ((VMUI) UI.getCurrent()).displayObject(((VMUI) UI.getCurrent())
                        .getSelectdValue());
            } else {
                ((VMUI) UI.getCurrent()).displayObject(tp, false);
            }
        });
        if (edit) {
            if (tp.getId() == null) {
                //Creating a new one
                Button save = new Button(TRANSLATOR.translate("general.save"));
                save.addClickListener((Button.ClickEvent event) -> {
                    try {
                        tp.setName(name.getValue().toString());
                        tp.setNotes(notes.getValue().toString());
                        tp.setActive((Boolean) active.getValue());
                        new TestProjectJpaController(DataBaseManager
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
                        tp.setActive((Boolean) active.getValue());
                        ((VMUI) UI.getCurrent()).handleVersioning(tp, () -> {
                            try {
                                new TestProjectJpaController(DataBaseManager
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
        layout.setSizeFull();
        setSizeFull();
    }
}
