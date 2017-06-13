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
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMUI;
import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import com.validation.manager.core.db.RequirementType;
import com.validation.manager.core.db.controller.RequirementTypeJpaController;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class RequirementTypeComponent extends Panel {

    private final RequirementType rt;
    private boolean edit = false;
    private static final InternationalizationProvider TRANSLATOR
            = Lookup.getDefault().lookup(InternationalizationProvider.class);
    private static final Logger LOG
            = Logger.getLogger(RequirementTypeComponent.class.getSimpleName());

    public RequirementTypeComponent(RequirementType rt, boolean edit) {
        setCaption(TRANSLATOR.translate("issue.resolution"));
        this.rt = rt;
        this.edit = edit;
        init();
    }

    public RequirementTypeComponent(RequirementType rt, Component content,
            boolean edit) {
        super(content);
        setCaption(TRANSLATOR.translate("issue.resolution"));
        this.rt = rt;
        this.edit = edit;
        init();
    }

    public RequirementTypeComponent(RequirementType rt, String caption,
            boolean edit) {
        super(caption);
        this.rt = rt;
        this.edit = edit;
        init();
    }

    public RequirementTypeComponent(RequirementType rt, String caption,
            Component content, boolean edit) {
        super(caption, content);
        this.rt = rt;
        this.edit = edit;
        init();
    }

    private void init() {
        FormLayout layout = new FormLayout();
        setContent(layout);
        addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(rt.getClass());
        binder.setItemDataSource(rt);
        Field<?> name = binder.buildAndBind(TRANSLATOR
                .translate("general.name"), "name");
        layout.addComponent(name);
        Field<?> desc = binder.buildAndBind(TRANSLATOR
                .translate("general.description"), "description");
        layout.addComponent(desc);
        if (edit) {
            Button update = new Button(rt.getId() == null
                    ? TRANSLATOR.
                            translate("general.create")
                    : TRANSLATOR.
                            translate("general.update"));
            update.addClickListener((Button.ClickEvent event) -> {
                RequirementTypeJpaController c
                        = new RequirementTypeJpaController(DataBaseManager.
                                getEntityManagerFactory());
                if (rt.getId() == null) {
                    rt.setName((String) name.getValue());
                    rt.setDescription((String) desc.getValue());
                    c.create(rt);
                } else {
                    try {
                        binder.commit();
                    } catch (FieldGroup.CommitException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }
            });
            Button cancel = new Button(Lookup.getDefault()
                    .lookup(InternationalizationProvider.class).
                    translate("general.cancel"));
            cancel.addClickListener((Button.ClickEvent event) -> {
                binder.discard();
                ((VMUI) UI.getCurrent()).updateScreen();
            });
            binder.setReadOnly(!edit);
            binder.setBuffered(true);
            HorizontalLayout hl = new HorizontalLayout();
            hl.addComponent(update);
            hl.addComponent(cancel);
            layout.addComponent(hl);
        }
    }
}
