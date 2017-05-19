package net.sourceforge.javydreamercsw.validation.manager.web.tester;

import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.validation.manager.core.IMainContentProvider;
import com.validation.manager.core.VMUI;
import com.validation.manager.core.db.ExecutionStep;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = IMainContentProvider.class, position = 2)
public class TesterScreenProvider extends ExecutionScreen {

    @Override
    public String getComponentCaption() {
        return "tester.tab.name";
    }

    @Override
    public boolean shouldDisplay() {
        return Lookup.getDefault().lookup(VMUI.class).getUser() != null
                && !Lookup.getDefault().lookup(VMUI.class).getUser()
                        .getExecutionStepList().isEmpty()
                && Lookup.getDefault().lookup(VMUI.class).checkRight("testplan.execute");
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
            for (ExecutionStep es : Lookup.getDefault().lookup(VMUI.class).getUser()
                    .getExecutionStepList()) {
                if (es.getExecutionStart() == null) {
                    //It has been assigned but not started
                    Notification.show("test.pending.title",
                            "test.pending.message",
                            Notification.Type.TRAY_NOTIFICATION);
                    break;
                }
            }
        }
    }
}
