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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.validation.manager.core.DataBaseManager;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
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
public final class RequirementTypeComponent extends VerticalLayout {

    private final RequirementType rt;
    private boolean edit = false;
    private static final InternationalizationProvider TRANSLATOR
            = Lookup.getDefault().lookup(InternationalizationProvider.class);
    private static final Logger LOG
            = Logger.getLogger(RequirementTypeComponent.class.getSimpleName());

    public RequirementTypeComponent(RequirementType rt, boolean edit) {
        add(new Span(TRANSLATOR.translate("issue.resolution")));
        this.rt = rt;
        this.edit = edit;
        init();
    }

    public RequirementTypeComponent(RequirementType rt, Component content,
            boolean edit) {
        add(content);
        add(new Span(TRANSLATOR.translate("issue.resolution")));
        this.rt = rt;
        this.edit = edit;
        init();
    }

    public RequirementTypeComponent(RequirementType rt, String caption,
            boolean edit) {
        add(new Span(caption));
        this.rt = rt;
        this.edit = edit;
        init();
    }

    public RequirementTypeComponent(RequirementType rt, String caption,
            Component content, boolean edit) {
        add(new Span(caption));
        add(content);
        this.rt = rt;
        this.edit = edit;
        init();
    }

    private void init() {
        FormLayout layout = new FormLayout();
        add(layout);
        Binder<RequirementType> binder = new Binder<>(RequirementType.class);
        binder.setBean(rt);
        TextField name = new TextField(TRANSLATOR
                .translate("general.name"));
        binder.bind(name, "name");
        layout.add(name);
        TextField desc = new TextField(TRANSLATOR
                .translate("general.description"));
        binder.bind(desc, "description");
        layout.add(desc);
        if (edit) {
            Button update = new Button(rt.getId() == null
                    ? TRANSLATOR.
                            translate("general.create")
                    : TRANSLATOR.
                            translate("general.update"));
            update.addClickListener((event) -> {
                RequirementTypeJpaController c
                        = new RequirementTypeJpaController(DataBaseManager.
                                getEntityManagerFactory());
                if (rt.getId() == null) {
                    rt.setName((String) name.getValue());
                    rt.setDescription((String) desc.getValue());
                    c.create(rt);
                } else {
                    try {
                        binder.writeBean(rt);
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }
            });
            Button cancel = new Button(Lookup.getDefault()
                    .lookup(InternationalizationProvider.class).
                    translate("general.cancel"));
            cancel.addClickListener((event) -> {
                ((ValidationManagerUI) UI.getCurrent()).updateScreen();
            });
            binder.setReadOnly(!edit);
            HorizontalLayout hl = new HorizontalLayout();
            hl.add(update);
            hl.add(cancel);
            layout.add(hl);
        }
    }
}
