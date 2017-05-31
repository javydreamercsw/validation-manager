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
package net.sourceforge.javydreamercsw.validation.manager.web.admin;

import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.validation.manager.core.IMainContentProvider;
import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import com.validation.manager.core.db.VmSetting;
import com.validation.manager.core.server.core.VMSettingServer;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import net.sourceforge.javydreamercsw.validation.manager.web.provider.AbstractProvider;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = IMainContentProvider.class, position = 4)
public class AdminScreenProvider extends AbstractProvider {

    private static final Logger LOG
            = Logger.getLogger(IMainContentProvider.class.getSimpleName());

    @Override
    public Component getContent() {
        TabSheet adminSheet = new TabSheet();
        VerticalLayout layout = new VerticalLayout();
        //Build setting tab
        VerticalLayout sl = new VerticalLayout();
        HorizontalSplitPanel split = new HorizontalSplitPanel();
        sl.addComponent(split);
        //Build left side
        Tree sTree = new Tree(Lookup.getDefault().lookup(InternationalizationProvider.class)
                .translate("general.settings"));
        adminSheet.addTab(sl, Lookup.getDefault().lookup(InternationalizationProvider.class)
                .translate("general.settings"));
        VMSettingServer.getSettings().stream().map((s) -> {
            sTree.addItem(s);
            sTree.setChildrenAllowed(s, false);
            return s;
        }).forEachOrdered((s) -> {
            sTree.setItemCaption(s, Lookup.getDefault().lookup(InternationalizationProvider.class)
                    .translate(s.getSetting()));
        });
        split.setFirstComponent(sTree);
        layout.addComponent(adminSheet);
        sTree.addValueChangeListener((Property.ValueChangeEvent event) -> {
            if (sTree.getValue() instanceof VmSetting) {
                split.setSecondComponent(
                        displaySetting((VmSetting) sTree.getValue()));
            }
        });
        layout.setId(getComponentCaption());
        return layout;
    }

    @Override
    public String getComponentCaption() {
        return "admin.tab.name";
    }

    @Override
    public boolean shouldDisplay() {
        return ValidationManagerUI.getInstance().getUser() != null
                && ValidationManagerUI.getInstance()
                        .checkRight("system.configuration");
    }

    private Component displaySetting(VmSetting s) {
        Panel form = new Panel(Lookup.getDefault().lookup(InternationalizationProvider.class)
                .translate("setting.detail"));
        FormLayout layout = new FormLayout();
        form.setContent(layout);
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(s.getClass());
        binder.setItemDataSource(s);
        Field<?> id = (TextField) binder.buildAndBind(Lookup.getDefault().lookup(InternationalizationProvider.class)
                .translate("general.setting"), "setting");
        layout.addComponent(id);
        Field bool = binder.buildAndBind(Lookup.getDefault().lookup(InternationalizationProvider.class)
                .translate("bool.value"), "boolVal");
        bool.setSizeFull();
        layout.addComponent(bool);
        Field integerVal = binder.buildAndBind(Lookup.getDefault().lookup(InternationalizationProvider.class)
                .translate("int.value"), "intVal");
        integerVal.setSizeFull();
        layout.addComponent(integerVal);
        Field longVal = binder.buildAndBind(Lookup.getDefault().lookup(InternationalizationProvider.class)
                .translate("long.val"), "longVal");
        longVal.setSizeFull();
        layout.addComponent(longVal);
        Field stringVal = binder.buildAndBind(Lookup.getDefault().lookup(InternationalizationProvider.class)
                .translate("string.val"), "stringVal",
                TextArea.class);
        stringVal.setStyleName(ValoTheme.TEXTAREA_LARGE);
        stringVal.setSizeFull();
        layout.addComponent(stringVal);
        Button cancel = new Button(Lookup.getDefault().lookup(InternationalizationProvider.class)
                .translate("general.cancel"));
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.discard();
        });
        //Editing existing one
        Button update = new Button(Lookup.getDefault().lookup(InternationalizationProvider.class)
                .translate("general.update"));
        update.addClickListener((Button.ClickEvent event) -> {
            try {
                binder.commit();
                displaySetting(s);
            } catch (FieldGroup.CommitException ex) {
                LOG.log(Level.SEVERE, null, ex);
                Notification.show(Lookup.getDefault().lookup(InternationalizationProvider.class)
                        .translate("general.error.record.update"),
                        ex.getLocalizedMessage(),
                        Notification.Type.ERROR_MESSAGE);
            }
        });
        boolean blocked = !s.getSetting().startsWith("version.");
        if (blocked) {
            HorizontalLayout hl = new HorizontalLayout();
            hl.addComponent(update);
            hl.addComponent(cancel);
            layout.addComponent(hl);
        }
        binder.setBuffered(true);
        binder.setReadOnly(false);
        binder.bindMemberFields(form);
        //The version settigns are not modifiable from the GUI
        binder.setEnabled(blocked);
        //Id is always blocked.
        id.setEnabled(false);
        form.setSizeFull();
        return form;
    }
}
