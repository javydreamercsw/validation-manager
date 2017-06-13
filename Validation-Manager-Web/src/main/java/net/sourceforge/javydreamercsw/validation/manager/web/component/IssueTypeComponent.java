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
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMUI;
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
public final class IssueTypeComponent extends Panel {

    private final IssueType it;
    private boolean edit = false;
    private static final InternationalizationProvider TRANSLATOR
            = Lookup.getDefault().lookup(InternationalizationProvider.class);
    private static final Logger LOG
            = Logger.getLogger(IssueTypeComponent.class.getSimpleName());

    public IssueTypeComponent(IssueType it, boolean edit) {
        setCaption(TRANSLATOR.translate("issue.type"));
        this.it = it;
        this.edit = edit;
        init();
    }

    public IssueTypeComponent(IssueType it, String caption, boolean edit) {
        super(caption);
        this.it = it;
        this.edit = edit;
        init();
    }

    public IssueTypeComponent(IssueType it) {
        setCaption(TRANSLATOR.translate("issue.type"));
        this.it = it;
        init();
    }

    public IssueTypeComponent(IssueType it, String caption) {
        super(caption);
        this.it = it;
        init();
    }

    private void init() {
        FormLayout layout = new FormLayout();
        setContent(layout);
        addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(it.getClass());
        binder.setItemDataSource(it);
        Field<?> name = binder.buildAndBind(TRANSLATOR
                .translate("general.name"), "typeName");
        layout.addComponent(name);
        Field<?> desc = binder.buildAndBind(TRANSLATOR
                .translate("general.description"), "description");
        layout.addComponent(desc);
        if (edit) {
            Button update = new Button(it.getId() == null
                    ? TRANSLATOR.
                            translate("general.create")
                    : TRANSLATOR.
                            translate("general.update"));
            update.addClickListener((Button.ClickEvent event) -> {
                IssueTypeJpaController c
                        = new IssueTypeJpaController(DataBaseManager.
                                getEntityManagerFactory());
                if (it.getId() == null) {
                    it.setDescription((String) desc.getValue());
                    it.setTypeName((String) name.getValue());
                    c.create(it);
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
