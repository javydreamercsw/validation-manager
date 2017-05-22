package net.sourceforge.javydreamercsw.validation.manager.web.provider;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.IMainContentProvider;
import com.validation.manager.core.db.controller.VmUserJpaController;
import com.validation.manager.core.tool.MD5;
import java.util.logging.Level;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = IMainContentProvider.class)
public class DemoProvider extends AbstractProvider {

    private VerticalLayout layout;

    @Override
    public Component getContent() {
        if (layout == null) {
            layout = new VerticalLayout();
            update();
        }
        return layout;
    }

    @Override
    public void update() {
        layout.removeAllComponents();
        VmUserJpaController controller
                = new VmUserJpaController(DataBaseManager
                        .getEntityManagerFactory());
        layout.addComponent(new Label("<h1>demo.tab.title</h1>",
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
                            .append("general.username")
                            .append(":</b> ")
                            .append(u.getUsername()).append(", <b>")
                            .append("general.password")
                            .append(":</b> ")
                            .append(u.getUsername())
                            .append(" <b>")
                            .append("general.role")
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
        super.update();
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
