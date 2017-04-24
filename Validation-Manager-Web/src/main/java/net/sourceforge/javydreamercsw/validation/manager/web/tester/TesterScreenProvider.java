package net.sourceforge.javydreamercsw.validation.manager.web.tester;

import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.validation.manager.core.IMainContentProvider;
import com.validation.manager.core.db.ExecutionStep;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = IMainContentProvider.class, position = 2)
public class TesterScreenProvider extends ExecutionScreen {

    @Override
    public String getComponentCaption() {
        return "tester.tab.name";
    }

    @Override
    public boolean shouldDisplay() {
        return ValidationManagerUI.getInstance().getUser() != null
                && !ValidationManagerUI.getInstance().getUser()
                        .getExecutionStepList().isEmpty()
                && ValidationManagerUI.getInstance().checkRight("testplan.execute");
    }

    @Override
    public Component getContent() {
        Component content = super.getContent();
        content.setId(getComponentCaption());
        return content;
    }

    @Override
    public void processNotification() {
        if (shouldDisplay()) {
            for (ExecutionStep es : ValidationManagerUI.getInstance().getUser()
                    .getExecutionStepList()) {
                if (es.getExecutionStart() == null) {
                    //It has been assigned but not started
                    Notification.show("Test Pending",
                            "You have test case(s) pending execution.",
                            Notification.Type.TRAY_NOTIFICATION);
                    break;
                }
            }
        }
    }
}
