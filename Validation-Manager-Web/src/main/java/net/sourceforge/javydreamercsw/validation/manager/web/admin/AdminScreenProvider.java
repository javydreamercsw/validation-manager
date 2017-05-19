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
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.validation.manager.core.AbstractProvider;
import com.validation.manager.core.IMainContentProvider;
import com.validation.manager.core.VMUI;
import com.validation.manager.core.db.VmSetting;
import com.validation.manager.core.server.core.VMSettingServer;
import java.util.logging.Level;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = IMainContentProvider.class)
public class AdminScreenProvider extends AbstractProvider {

    @Override
    public Component getContent() {
        TabSheet adminSheet = new TabSheet();
        VerticalLayout layout = new VerticalLayout();
        //Build setting tab
        VerticalLayout sl = new VerticalLayout();
        HorizontalSplitPanel split = new HorizontalSplitPanel();
        sl.addComponent(split);
        //Build left side
        Tree sTree = new Tree(Lookup.getDefault().lookup(VMUI.class)
                .translate("general.settings"));
        adminSheet.addTab(sl, Lookup.getDefault().lookup(VMUI.class)
                .translate("general.settings"));
        VMSettingServer.getSettings().stream().map((s) -> {
            sTree.addItem(s);
            sTree.setChildrenAllowed(s, false);
            return s;
        }).forEachOrdered((s) -> {
            sTree.setItemCaption(s, Lookup.getDefault().lookup(VMUI.class)
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
        return Lookup.getDefault().lookup(VMUI.class)
                .translate("admin.tab.name");
    }

    @Override
    public boolean shouldDisplay() {
        return Lookup.getDefault().lookup(VMUI.class).getUser() != null
                && Lookup.getDefault().lookup(VMUI.class)
                        .checkRight("system.configuration");
    }

    private Component displaySetting(VmSetting s) {
        Panel form = new Panel(Lookup.getDefault().lookup(VMUI.class)
                .translate("setting.detail"));
        FormLayout layout = new FormLayout();
        form.setContent(layout);
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(s.getClass());
        binder.setItemDataSource(s);
        Field<?> id = binder.buildAndBind(Lookup.getDefault().lookup(VMUI.class)
                .translate("general.setting"), "setting");
        layout.addComponent(id);
        Field bool = binder.buildAndBind(Lookup.getDefault().lookup(VMUI.class)
                .translate("bool.value"), "boolVal");
        bool.setSizeFull();
        layout.addComponent(bool);
        Field integerVal = binder.buildAndBind(Lookup.getDefault().lookup(VMUI.class)
                .translate("int.value"), "intVal");
        integerVal.setSizeFull();
        layout.addComponent(integerVal);
        Field longVal = binder.buildAndBind(Lookup.getDefault().lookup(VMUI.class)
                .translate("long.val"), "longVal");
        longVal.setSizeFull();
        layout.addComponent(longVal);
        Field stringVal = binder.buildAndBind(Lookup.getDefault().lookup(VMUI.class)
                .translate("string.val"), "stringVal",
                TextArea.class);
        stringVal.setStyleName(ValoTheme.TEXTAREA_LARGE);
        stringVal.setSizeFull();
        layout.addComponent(stringVal);
        Button cancel = new Button(Lookup.getDefault().lookup(VMUI.class)
                .translate("general.cancel"));
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.discard();
        });
        //Editing existing one
        Button update = new Button(Lookup.getDefault().lookup(VMUI.class)
                .translate("general.update"));
        update.addClickListener((Button.ClickEvent event) -> {
            try {
                binder.commit();
                displaySetting(s);
            } catch (FieldGroup.CommitException ex) {
                LOG.log(Level.SEVERE, null, ex);
                Notification.show(Lookup.getDefault().lookup(VMUI.class)
                        .translate("general.error.record.creation"),
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
