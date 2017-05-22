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
import com.validation.manager.core.db.VmSetting;
import com.validation.manager.core.server.core.VMSettingServer;
import java.util.logging.Level;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import net.sourceforge.javydreamercsw.validation.manager.web.provider.AbstractProvider;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = IMainContentProvider.class, position = 4)
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
        Tree sTree = new Tree(ValidationManagerUI.getInstance()
                .translate("general.settings"));
        adminSheet.addTab(sl, ValidationManagerUI.getInstance()
                .translate("general.settings"));
        VMSettingServer.getSettings().stream().map((s) -> {
            sTree.addItem(s);
            sTree.setChildrenAllowed(s, false);
            return s;
        }).forEachOrdered((s) -> {
            sTree.setItemCaption(s, ValidationManagerUI.getInstance()
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
        Panel form = new Panel(ValidationManagerUI.getInstance()
                .translate("setting.detail"));
        FormLayout layout = new FormLayout();
        form.setContent(layout);
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(s.getClass());
        binder.setItemDataSource(s);
        Field<?> id = (TextField) binder.buildAndBind(ValidationManagerUI.getInstance()
                .translate("general.setting"), "setting");
        layout.addComponent(id);
        Field bool = binder.buildAndBind(ValidationManagerUI.getInstance()
                .translate("bool.value"), "boolVal");
        bool.setSizeFull();
        layout.addComponent(bool);
        Field integerVal = binder.buildAndBind(ValidationManagerUI.getInstance()
                .translate("int.value"), "intVal");
        integerVal.setSizeFull();
        layout.addComponent(integerVal);
        Field longVal = binder.buildAndBind(ValidationManagerUI.getInstance()
                .translate("long.val"), "longVal");
        longVal.setSizeFull();
        layout.addComponent(longVal);
        Field stringVal = binder.buildAndBind(ValidationManagerUI.getInstance()
                .translate("string.val"), "stringVal",
                TextArea.class);
        stringVal.setStyleName(ValoTheme.TEXTAREA_LARGE);
        stringVal.setSizeFull();
        layout.addComponent(stringVal);
        Button cancel = new Button(ValidationManagerUI.getInstance()
                .translate("general.cancel"));
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.discard();
        });
        //Editing existing one
        Button update = new Button(ValidationManagerUI.getInstance()
                .translate("general.update"));
        update.addClickListener((Button.ClickEvent event) -> {
            try {
                binder.commit();
                displaySetting(s);
            } catch (FieldGroup.CommitException ex) {
                LOG.log(Level.SEVERE, null, ex);
                Notification.show(ValidationManagerUI.getInstance()
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
