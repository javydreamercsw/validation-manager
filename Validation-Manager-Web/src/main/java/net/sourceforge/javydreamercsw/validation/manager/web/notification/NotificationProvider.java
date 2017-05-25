package net.sourceforge.javydreamercsw.validation.manager.web.notification;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalSplitPanel;
import com.validation.manager.core.IMainContentProvider;
import com.validation.manager.core.db.Notification;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import net.sourceforge.javydreamercsw.validation.manager.web.provider.AbstractProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = IMainContentProvider.class)
public class NotificationProvider extends AbstractProvider {

    @Override
    public boolean shouldDisplay() {
        //Show whenever an user is logged in.
        return ValidationManagerUI.getInstance().getUser() != null;
    }

    @Override
    public String getComponentCaption() {
        return "general.notifications";
    }

    @Override
    public Component getContent() {
        HorizontalSplitPanel hs = new HorizontalSplitPanel();
        hs.setSplitPosition(25, Unit.PERCENTAGE);
        //On top put a list of notifications
        BeanItemContainer<Notification> container
                = new BeanItemContainer<>(Notification.class);
        ValidationManagerUI.getInstance().getUser().getNotificationList().forEach(n -> {
            container.addBean(n);
        });
        Grid grid = new Grid(ValidationManagerUI.getInstance()
                .translate("general.notifications"), container);
        hs.setFirstComponent(grid);
        return hs;
    }
}
