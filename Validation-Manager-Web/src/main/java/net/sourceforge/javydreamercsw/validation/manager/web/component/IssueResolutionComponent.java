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
import com.validation.manager.core.db.IssueResolution;
import com.validation.manager.core.db.controller.IssueResolutionJpaController;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class IssueResolutionComponent extends Panel {

    private final IssueResolution ir;
    private boolean edit = false;
    private static final InternationalizationProvider TRANSLATOR
            = Lookup.getDefault().lookup(InternationalizationProvider.class);
    private static final Logger LOG
            = Logger.getLogger(IssueResolutionComponent.class.getSimpleName());

    public IssueResolutionComponent(IssueResolution ir, boolean edit) {
        setCaption(TRANSLATOR.translate("issue.resolution"));
        this.ir = ir;
        this.edit = edit;
        init();
    }

    public IssueResolutionComponent(IssueResolution ir, Component content,
            boolean edit) {
        super(content);
        setCaption(TRANSLATOR.translate("issue.resolution"));
        this.ir = ir;
        this.edit = edit;
        init();
    }

    public IssueResolutionComponent(IssueResolution ir, String caption,
            boolean edit) {
        super(caption);
        this.ir = ir;
        this.edit = edit;
        init();
    }

    public IssueResolutionComponent(IssueResolution ir, String caption,
            Component content, boolean edit) {
        super(caption, content);
        this.ir = ir;
        this.edit = edit;
        init();
    }

    private void init() {
        FormLayout layout = new FormLayout();
        setContent(layout);
        addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(ir.getClass());
        binder.setItemDataSource(ir);
        Field<?> name = binder.buildAndBind(TRANSLATOR
                .translate("general.name"), "name");
        layout.addComponent(name);
        if (edit) {
            Button update = new Button(ir.getId() == null
                    ? TRANSLATOR.
                            translate("general.create")
                    : TRANSLATOR.
                            translate("general.update"));
            update.addClickListener((Button.ClickEvent event) -> {
                IssueResolutionJpaController c
                        = new IssueResolutionJpaController(DataBaseManager.
                                getEntityManagerFactory());
                if (ir.getId() == null) {
                    ir.setName((String) name.getValue());
                    c.create(ir);
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
