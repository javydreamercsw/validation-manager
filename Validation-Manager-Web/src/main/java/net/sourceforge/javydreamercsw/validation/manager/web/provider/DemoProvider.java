package net.sourceforge.javydreamercsw.validation.manager.web.provider;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.validation.manager.core.AbstractProvider;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.IMainContentProvider;
import com.validation.manager.core.VMUI;
import com.validation.manager.core.db.controller.VmUserJpaController;
import com.validation.manager.core.tool.MD5;
import java.util.logging.Level;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = IMainContentProvider.class)
public class DemoProvider extends AbstractProvider {

    private VerticalLayout layout;

    @Override
    public Component getContent() {
        if (layout == null) {
            layout = new VerticalLayout();
            VmUserJpaController controller
                    = new VmUserJpaController(DataBaseManager
                            .getEntityManagerFactory());
            layout.addComponent(new Label("<h1>"
                    + Lookup.getDefault().lookup(VMUI.class)
                            .translate("demo.tab.title") + "</h1>",
                    ContentMode.HTML));
            Label l = new Label();
            l.setId("demo.tab.message");
            layout.addComponent(l);
            StringBuilder sb = new StringBuilder("<ul>");
            controller.findVmUserEntities().stream().filter((u)
                    -> (u.getId() < 1000)).forEachOrdered((u) -> {
                try {
                    //Default accounts
                    if (u.getPassword() != null
                            && u.getPassword().equals(MD5
                                    .encrypt(u.getUsername()))) {
                        sb.append("<li><b>")
                                .append(Lookup.getDefault().lookup(VMUI.class)
                                        .translate("general.username"))
                                .append(":</b> ")
                                .append(u.getUsername()).append(", <b>")
                                .append(Lookup.getDefault().lookup(VMUI.class)
                                        .translate("general.password"))
                                .append(":</b> ")
                                .append(u.getUsername())
                                .append(" <b>")
                                .append(Lookup.getDefault().lookup(VMUI.class)
                                        .translate("general.role"))
                                .append(":</b> ")
                                .append(Lookup.getDefault().lookup(VMUI.class)
                                        .translate(u.getRoleList().get(0)
                                                .getDescription()))
                                .append("</li>");
                    }
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            });
            sb.append("</ul>");
            layout.addComponent(new Label(sb.toString(),
                    ContentMode.HTML));
            layout.setId(getComponentCaption());
        }
        return layout;
    }

    @Override
    public String getComponentCaption() {
        return "demo.tab.name";
    }

    @Override
    public boolean shouldDisplay() {
        return DataBaseManager.isDemo();
    }
}
