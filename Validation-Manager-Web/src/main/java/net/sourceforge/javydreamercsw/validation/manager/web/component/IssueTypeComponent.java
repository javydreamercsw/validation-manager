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
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.validation.manager.core.DataBaseManager;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import com.validation.manager.core.db.IssueType;
import com.validation.manager.core.db.controller.IssueTypeJpaController;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class IssueTypeComponent extends VerticalLayout {

    private final IssueType it;
    private boolean edit = false;
    private static final InternationalizationProvider TRANSLATOR
            = Lookup.getDefault().lookup(InternationalizationProvider.class);
    private static final Logger LOG
            = Logger.getLogger(IssueTypeComponent.class.getSimpleName());

    public IssueTypeComponent(IssueType it, boolean edit) {
        add(new Span(TRANSLATOR.translate("issue.type")));
        this.it = it;
        this.edit = edit;
        init();
    }

    public IssueTypeComponent(IssueType it, String caption, boolean edit) {
        add(new Span(caption));
        this.it = it;
        this.edit = edit;
        init();
    }

    public IssueTypeComponent(IssueType it) {
        add(new Span(TRANSLATOR.translate("issue.type")));
        this.it = it;
        init();
    }

    public IssueTypeComponent(IssueType it, String caption) {
        add(new Span(caption));
        this.it = it;
        init();
    }

    private void init() {
        FormLayout layout = new FormLayout();
        add(layout);
        Binder<IssueType> binder = new Binder<>(IssueType.class);
        binder.setBean(it);
        TextField name = new TextField(TRANSLATOR
                .translate("general.name"));
        binder.bind(name, "typeName");
        layout.add(name);
        TextField desc = new TextField(TRANSLATOR
                .translate("general.description"));
        binder.bind(desc, "description");
        layout.add(desc);
        if (edit) {
            Button update = new Button(it.getId() == null
                    ? TRANSLATOR.
                            translate("general.create")
                    : TRANSLATOR.
                            translate("general.update"));
            update.addClickListener((event) -> {
                IssueTypeJpaController c
                        = new IssueTypeJpaController(DataBaseManager.
                                getEntityManagerFactory());
                if (it.getId() == null) {
                    it.setDescription((String) desc.getValue());
                    it.setTypeName((String) name.getValue());
                    c.create(it);
                } else {
                    try {
                        binder.writeBean(it);
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
