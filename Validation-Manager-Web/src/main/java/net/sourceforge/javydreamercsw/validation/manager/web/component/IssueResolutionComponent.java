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
import com.validation.manager.core.db.IssueResolution;
import com.validation.manager.core.db.controller.IssueResolutionJpaController;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class IssueResolutionComponent extends VerticalLayout {

    private final IssueResolution ir;
    private boolean edit = false;
    private static final InternationalizationProvider TRANSLATOR
            = Lookup.getDefault().lookup(InternationalizationProvider.class);
    private static final Logger LOG
            = Logger.getLogger(IssueResolutionComponent.class.getSimpleName());

    public IssueResolutionComponent(IssueResolution ir, boolean edit) {
        add(new Span(TRANSLATOR.translate("issue.resolution")));
        this.ir = ir;
        this.edit = edit;
        init();
    }

    public IssueResolutionComponent(IssueResolution ir, Component content,
            boolean edit) {
        add(content);
        add(new Span(TRANSLATOR.translate("issue.resolution")));
        this.ir = ir;
        this.edit = edit;
        init();
    }

    public IssueResolutionComponent(IssueResolution ir, String caption,
            boolean edit) {
        add(new Span(caption));
        this.ir = ir;
        this.edit = edit;
        init();
    }

    public IssueResolutionComponent(IssueResolution ir, String caption,
            Component content, boolean edit) {
        add(new Span(caption));
        add(content);
        this.ir = ir;
        this.edit = edit;
        init();
    }

    private void init() {
        FormLayout layout = new FormLayout();
        add(layout);
        Binder<IssueResolution> binder = new Binder<>(IssueResolution.class);
        binder.setBean(ir);
        TextField name = new TextField(TRANSLATOR
                .translate("general.name"));
        binder.bind(name, "name");
        layout.add(name);
        if (edit) {
            Button update = new Button(ir.getId() == null
                    ? TRANSLATOR.
                            translate("general.create")
                    : TRANSLATOR.
                            translate("general.update"));
            update.addClickListener((event) -> {
                IssueResolutionJpaController c
                        = new IssueResolutionJpaController(DataBaseManager.
                                getEntityManagerFactory());
                if (ir.getId() == null) {
                    ir.setName((String) name.getValue());
                    c.create(ir);
                } else {
                    try {
                        binder.writeBean(ir);
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
