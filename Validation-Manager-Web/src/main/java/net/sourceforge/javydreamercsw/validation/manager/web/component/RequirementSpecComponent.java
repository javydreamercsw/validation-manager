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
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecPK;
import com.validation.manager.core.db.SpecLevel;
import com.validation.manager.core.db.controller.RequirementSpecJpaController;
import com.validation.manager.core.db.controller.SpecLevelJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class RequirementSpecComponent extends VerticalLayout {

    private final RequirementSpec rs;
    private final boolean edit;
    private static final Logger LOG
            = Logger.getLogger(RequirementSpecComponent.class.getSimpleName());

    public RequirementSpecComponent(RequirementSpec rs, boolean edit) {
        this.rs = rs;
        this.edit = edit;
        init();
    }

    public RequirementSpecComponent(RequirementSpec rs, boolean edit, String caption) {
        this.rs = rs;
        this.edit = edit;
        add(new com.vaadin.flow.component.html.Span(caption));
        init();
    }

    private void init() {
        FormLayout layout = new FormLayout();
        add(layout);
        Binder<RequirementSpec> binder = new Binder<>(RequirementSpec.class);
        binder.setBean(rs);
        TextField name = new TextField(TRANSLATOR.translate("general.name"));
        binder.bind(name, "name");
        layout.add(name);
        TextArea desc = new TextArea(TRANSLATOR.translate("general.description"));
        binder.bind(desc, "description");
        desc.setSizeFull();
        layout.add(desc);
        DateTimePicker date = new DateTimePicker(
                TRANSLATOR.translate("general.modification.data"));
        binder.forField(date)
                .withConverter(this::toDate, this::toDateTime)
                .bind("modificationDate");
        layout.add(date);
        date.setEnabled(false);
        SpecLevelJpaController controller
                = new SpecLevelJpaController(DataBaseManager
                        .getEntityManagerFactory());
        List<SpecLevel> levels = controller.findSpecLevelEntities();
        ComboBox<SpecLevel> level
                = new ComboBox<>(TRANSLATOR.translate("spec.level"));
        level.setItems(levels);
        level.setItemLabelGenerator(id -> TRANSLATOR.translate(id.getName()));
        binder.bind(level, "specLevel");
        layout.add(level);
        Button cancel = new Button(TRANSLATOR.translate("general.cancel"));
        cancel.addClickListener((event) -> {
            if (rs.getRequirementSpecPK() == null) {
                ((ValidationManagerUI) UI.getCurrent()).displayObject(rs.getProject());
            } else {
                ((ValidationManagerUI) UI.getCurrent()).displayObject(rs, false);
            }
        });
        if (edit) {
            if (rs.getRequirementSpecPK() == null) {
                //Creating a new one
                Button save = new Button(TRANSLATOR.translate("general.save"));
                save.addClickListener((event) -> {
                    try {
                        rs.setName(name.getValue().toString());
                        rs.setModificationDate(new Date());
                        rs.setSpecLevel(level.getValue());
                        rs.setProject(((Project) ((ValidationManagerUI) UI.getCurrent())
                                .getSelectdValue()));
                        rs.setRequirementSpecPK(new RequirementSpecPK(
                                rs.getProject().getId(),
                                rs.getSpecLevel().getId()));
                        new RequirementSpecJpaController(DataBaseManager
                                .getEntityManagerFactory()).create(rs);
                        setVisible(false);
                        //Recreate the tree to show the addition
                        ((ValidationManagerUI) UI.getCurrent()).updateProjectList();
                        ((ValidationManagerUI) UI.getCurrent()).buildProjectTree(rs);
                        ((ValidationManagerUI) UI.getCurrent()).displayObject(rs, true);
                        ((ValidationManagerUI) UI.getCurrent()).updateScreen();
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show(TRANSLATOR.translate("general.error.record.creation"));
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.add(save, cancel);
                layout.add(hl);
            } else {
                //Editing existing one
                Button update = new Button(TRANSLATOR.translate("general.update"));
                update.addClickListener((event) -> {
                    try {
                        rs.setName(name.getValue().toString());
                        rs.setModificationDate(new Date());
                        rs.setSpecLevel(level.getValue());
                        ((ValidationManagerUI) UI.getCurrent()).handleVersioning(rs, () -> {
                            try {
                                new RequirementSpecJpaController(DataBaseManager
                                        .getEntityManagerFactory()).edit(rs);
                                ((ValidationManagerUI) UI.getCurrent()).displayObject(rs, true);
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
                        Notification.show(TRANSLATOR.translate("general.error.record.update"));
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.add(update, cancel);
                layout.add(hl);
            }
        }
        binder.setReadOnly(!edit);
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
