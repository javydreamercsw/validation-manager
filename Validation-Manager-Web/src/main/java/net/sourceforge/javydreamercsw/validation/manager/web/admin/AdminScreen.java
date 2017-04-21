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
import com.validation.manager.core.VMUI;
import com.validation.manager.core.db.VmSetting;
import com.validation.manager.core.server.core.VMSettingServer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class AdminScreen extends Panel {

    private final VMUI ui;
    private static final Logger LOG
            = Logger.getLogger(AdminScreen.class.getSimpleName());

    public AdminScreen(VMUI ui) {
        this.ui = ui;
        init();
    }

    public AdminScreen(VMUI ui, String caption) {
        super(caption);
        this.ui = ui;
        init();
    }

    private void init() {
        TabSheet adminSheet = new TabSheet();
        VerticalLayout layout = new VerticalLayout();
        //Build setting tab
        VerticalLayout sl = new VerticalLayout();
        HorizontalSplitPanel split = new HorizontalSplitPanel();
        sl.addComponent(split);
        //Build left side
        Tree sTree = new Tree("Settings");
        adminSheet.addTab(sl, "Settings");
        VMSettingServer.getSettings().stream().map((s) -> {
            sTree.addItem(s);
            sTree.setChildrenAllowed(s, false);
            return s;
        }).forEachOrdered((s) -> {
            sTree.setItemCaption(s, ui.translate(s.getSetting()));
        });
        split.setFirstComponent(sTree);
        layout.addComponent(adminSheet);
        sTree.addValueChangeListener((Property.ValueChangeEvent event) -> {
            if (sTree.getValue() instanceof VmSetting) {
                split.setSecondComponent(
                        displaySetting((VmSetting) sTree.getValue()));
            }
        });
        setContent(layout);
    }

    private Component displaySetting(VmSetting s) {
        Panel form = new Panel("Setting Detail");
        FormLayout layout = new FormLayout();
        form.setContent(layout);
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(s.getClass());
        binder.setItemDataSource(s);
        Field<?> id = binder.buildAndBind("Setting", "setting");
        layout.addComponent(id);
        Field bool = binder.buildAndBind("Boolean Value", "boolVal");
        bool.setSizeFull();
        layout.addComponent(bool);
        Field integerVal = binder.buildAndBind("Integer Value", "intVal");
        integerVal.setSizeFull();
        layout.addComponent(integerVal);
        Field longVal = binder.buildAndBind("Long Value", "longVal");
        longVal.setSizeFull();
        layout.addComponent(longVal);
        Field stringVal = binder.buildAndBind("String Value", "stringVal",
                TextArea.class);
        stringVal.setStyleName(ValoTheme.TEXTAREA_LARGE);
        stringVal.setSizeFull();
        layout.addComponent(stringVal);
        Button cancel = new Button("Cancel");
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.discard();
        });
        //Editing existing one
        Button update = new Button("Update");
        update.addClickListener((Button.ClickEvent event) -> {
            try {
                binder.commit();
                displaySetting(s);
            } catch (FieldGroup.CommitException ex) {
                LOG.log(Level.SEVERE, null, ex);
                Notification.show("Error updating record!",
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
