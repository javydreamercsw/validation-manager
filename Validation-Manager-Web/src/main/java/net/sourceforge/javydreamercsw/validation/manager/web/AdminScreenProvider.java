package net.sourceforge.javydreamercsw.validation.manager.web;

import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.validation.manager.core.AbstractProvider;
import com.validation.manager.core.IMainContentProvider;
import net.sourceforge.javydreamercsw.validation.manager.web.admin.AdminScreen;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = IMainContentProvider.class, position = 3)
public class AdminScreenProvider extends AbstractProvider {

    private AdminScreen as;

    @Override
    public Component getContent() {
        if (as == null) {
            as = new AdminScreen(getUI());
            as.setId(getComponentCaption());
        } else if (as != null) {
            return as;
        }
        return as == null ? new Panel() : as;
    }

    @Override
    public String getId() {
        return getComponentCaption();
    }

    @Override
    public String getComponentCaption() {
        return "admin.tab.name";
    }

    @Override
    public boolean shouldDisplay() {
        return getUI().getUser() != null
                && getUI().checkRight("system.configuration");
    }
}
