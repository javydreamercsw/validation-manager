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
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.icons.VaadinIcons;
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
import com.validation.manager.core.db.Baseline;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.controller.BaselineJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.BaselineServer;
import com.validation.manager.core.tool.Tool;
import de.steinwedel.messagebox.ButtonOption;
import de.steinwedel.messagebox.MessageBox;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class BaselineComponent extends Panel {

    private static final Logger LOG
            = Logger.getLogger(BaselineComponent.class.getSimpleName());

    public BaselineComponent(Baseline baseline,
            boolean edit, RequirementSpec rs) {
        super(TRANSLATOR.translate("baseline.detail"));
        FormLayout layout = new FormLayout();
        setContent(layout);
        addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(baseline.getClass());
        binder.setItemDataSource(baseline);
        Field<?> id = binder.buildAndBind(TRANSLATOR.translate("general.name"), "baselineName");
        layout.addComponent(id);
        Field desc = binder.buildAndBind(TRANSLATOR.translate("general.description"), "description",
                TextArea.class
        );
        desc.setSizeFull();
        layout.addComponent(desc);
        Button cancel = new Button(TRANSLATOR.translate("general.cancel"));
        if (rs != null) {
            List<History> potential = new ArrayList<>();
            Tool.extractRequirements(rs).forEach((r) -> {
                potential.add(r.getHistoryList().get(r.getHistoryList().size() - 1));
            });
            layout.addComponent(((ValidationManagerUI) UI.getCurrent())
                    .createRequirementHistoryTable(TRANSLATOR.translate("included.requirements"),
                            potential, true));
        } else {
            layout.addComponent(((ValidationManagerUI) UI.getCurrent())
                    .createRequirementHistoryTable(TRANSLATOR.translate("included.requirements"),
                            baseline.getHistoryList(), true));
        }
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.discard();
            ((ValidationManagerUI) UI.getCurrent())
                    .displayObject(((ValidationManagerUI) UI.getCurrent())
                            .getTree().getValue());
        });
        if (edit) {
            if (baseline.getId() == null) {
                //Creating a new one
                Button save = new Button(TRANSLATOR.translate("general.save"));
                save.addClickListener((Button.ClickEvent event) -> {
                    try {
                        binder.commit();
                        if (rs != null) {
                            MessageBox prompt = MessageBox.createQuestion()
                                    .withCaption(TRANSLATOR.translate("save.baseline.title"))
                                    .withMessage(TRANSLATOR.translate("save.baseine.message")
                                            + "requirements will be released to a new major version")
                                    .withYesButton(() -> {
                                        Baseline entity = BaselineServer
                                                .createBaseline(
                                                        baseline.getBaselineName(),
                                                        baseline.getDescription(),
                                                        rs)
                                                .getEntity();
                                        ((ValidationManagerUI) UI.getCurrent())
                                                .updateProjectList();
                                        ((ValidationManagerUI) UI.getCurrent())
                                                .buildProjectTree(entity);
                                        ((ValidationManagerUI) UI.getCurrent())
                                                .displayObject(entity, false);
                                        ((ValidationManagerUI) UI.getCurrent())
                                                .updateScreen();
                                    },
                                            ButtonOption.focus(),
                                            ButtonOption
                                                    .icon(VaadinIcons.CHECK))
                                    .withNoButton(() -> {
                                        ((ValidationManagerUI) UI.getCurrent())
                                                .displayObject(((ValidationManagerUI) UI
                                                        .getCurrent()).getTree().getValue());
                                    },
                                            ButtonOption
                                                    .icon(VaadinIcons.CLOSE));
                            prompt.getWindow().setIcon(ValidationManagerUI.SMALL_APP_ICON);
                            prompt.open();
                        } else {
                            //Recreate the tree to show the addition
                            ((ValidationManagerUI) UI.getCurrent()).displayObject(baseline, true);
                        }
                        ((ValidationManagerUI) UI.getCurrent()).updateProjectList();
                        ((ValidationManagerUI) UI.getCurrent()).updateScreen();
                    } catch (FieldGroup.CommitException ex) {
                        LOG.log(Level.SEVERE, null, ex);
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
                        ((ValidationManagerUI) UI.getCurrent()).handleVersioning(baseline, () -> {
                            try {
                                new BaselineJpaController(DataBaseManager
                                        .getEntityManagerFactory()).edit(baseline);
                                //Recreate the tree to show the addition
                                ((ValidationManagerUI) UI.getCurrent()).buildProjectTree(baseline);
                                ((ValidationManagerUI) UI.getCurrent()).displayBaseline(baseline, false);
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
        binder.setBuffered(true);
        binder.setReadOnly(!edit);
        binder.bindMemberFields(this);
        setSizeFull();
    }

}
