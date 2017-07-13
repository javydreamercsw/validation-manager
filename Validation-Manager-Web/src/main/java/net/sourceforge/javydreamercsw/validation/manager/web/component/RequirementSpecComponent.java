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
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
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
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecPK;
import com.validation.manager.core.db.SpecLevel;
import com.validation.manager.core.db.controller.RequirementSpecJpaController;
import com.validation.manager.core.db.controller.SpecLevelJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class RequirementSpecComponent extends Panel {

    private final RequirementSpec rs;
    private final boolean edit;
    private static final Logger LOG
            = Logger.getLogger(RequirementSpecComponent.class.getSimpleName());

    public RequirementSpecComponent(RequirementSpec rs, boolean edit) {
        this.rs = rs;
        this.edit = edit;
        setCaption(TRANSLATOR.translate("requirement.spec.detail"));
        init();
    }

    public RequirementSpecComponent(RequirementSpec rs, boolean edit, String caption) {
        super(caption);
        this.rs = rs;
        this.edit = edit;
        init();
    }

    private void init() {
        FormLayout layout = new FormLayout();
        setContent(layout);
        addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(rs.getClass());
        binder.setItemDataSource(rs);
        Field<?> name = binder.buildAndBind(TRANSLATOR.translate("general.name"), "name");
        layout.addComponent(name);
        Field desc = binder.buildAndBind(TRANSLATOR.translate("general.description"), "description",
                TextArea.class);
        desc.setSizeFull();
        layout.addComponent(desc);
        Field<?> date = binder.buildAndBind(TRANSLATOR.translate("general.modification.data"),
                "modificationDate");
        layout.addComponent(date);
        date.setEnabled(false);
        SpecLevelJpaController controller
                = new SpecLevelJpaController(DataBaseManager
                        .getEntityManagerFactory());
        List<SpecLevel> levels = controller.findSpecLevelEntities();
        BeanItemContainer<SpecLevel> specLevelContainer
                = new BeanItemContainer<>(SpecLevel.class, levels);
        ComboBox level = new ComboBox(TRANSLATOR.translate("spec.level"));
        level.setContainerDataSource(specLevelContainer);
        level.getItemIds().forEach(id -> {
            level.setItemCaption(id, TRANSLATOR.translate(((SpecLevel) id).getName()));
        });
        binder.bind(level, "specLevel");
        layout.addComponent(level);
        Button cancel = new Button(TRANSLATOR.translate("general.cancel"));
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.discard();
            if (rs.getRequirementSpecPK() == null) {
                ((VMUI) UI.getCurrent()).displayObject(rs.getProject());
            } else {
                ((VMUI) UI.getCurrent()).displayObject(rs, false);
            }
        });
        if (edit) {
            if (rs.getRequirementSpecPK() == null) {
                //Creating a new one
                Button save = new Button(TRANSLATOR.translate("general.save"));
                save.addClickListener((Button.ClickEvent event) -> {
                    try {
                        rs.setName(name.getValue().toString());
                        rs.setModificationDate(new Date());
                        rs.setSpecLevel((SpecLevel) level.getValue());
                        rs.setProject(((Project) ((VMUI) UI.getCurrent())
                                .getSelectdValue()));
                        rs.setRequirementSpecPK(new RequirementSpecPK(
                                rs.getProject().getId(),
                                rs.getSpecLevel().getId()));
                        new RequirementSpecJpaController(DataBaseManager
                                .getEntityManagerFactory()).create(rs);
                        setVisible(false);
                        //Recreate the tree to show the addition
                        ((VMUI) UI.getCurrent()).updateProjectList();
                        ((VMUI) UI.getCurrent()).buildProjectTree(rs);
                        ((VMUI) UI.getCurrent()).displayObject(rs, true);
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
                        rs.setName(name.getValue().toString());
                        rs.setModificationDate(new Date());
                        rs.setSpecLevel((SpecLevel) level.getValue());
                        ((VMUI) UI.getCurrent()).handleVersioning(rs, () -> {
                            try {
                                new RequirementSpecJpaController(DataBaseManager
                                        .getEntityManagerFactory()).edit(rs);
                                ((VMUI) UI.getCurrent()).displayObject(rs, true);
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
                        Notification.show(TRANSLATOR.translate("general.error.record.update"),
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
