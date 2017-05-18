package net.sourceforge.javydreamercsw.validation.manager.web.provider;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.validation.manager.core.AbstractProvider;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.IMainContentProvider;
import com.validation.manager.core.db.controller.VmUserJpaController;
import com.validation.manager.core.tool.MD5;
import java.util.logging.Level;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = IMainContentProvider.class)
public class DemoProvider extends AbstractProvider {

    @Override
    public Component getContent() {
        VerticalLayout layout = new VerticalLayout();
        VmUserJpaController controller
                = new VmUserJpaController(DataBaseManager
                        .getEntityManagerFactory());
        layout.addComponent(new Label("<h1>" + ValidationManagerUI.RB
                .getString("demo.tab.title") + "</h1>",
                ContentMode.HTML));
        layout.addComponent(new Label(ValidationManagerUI.RB
                .getString("demo.tab.message")));
        StringBuilder sb = new StringBuilder("<ul>");
        controller.findVmUserEntities().stream().filter((u)
                -> (u.getId() < 1000)).forEachOrdered((u) -> {
            try {
                //Default accounts
                if (u.getPassword() != null
                        && u.getPassword().equals(MD5
                                .encrypt(u.getUsername()))) {
                    sb.append("<li><b>").append(ValidationManagerUI.RB
                            .getString("general.username")).append(":</b> ")
                            .append(u.getUsername()).append(", <b>")
                            .append(ValidationManagerUI.RB
                                    .getString("general.password"))
                            .append(":</b> ")
                            .append(u.getUsername())
                            .append(" <b>")
                            .append(ValidationManagerUI.RB
                                    .getString("general.role"))
                            .append(":</b> ")
                            .append(ValidationManagerUI.getInstance()
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
