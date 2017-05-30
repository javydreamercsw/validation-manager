/*
 * This is the quality assurance screen provider.
 */
package net.sourceforge.javydreamercsw.validation.manager.web.quality;

import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.IMainContentProvider;
import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.VmSetting;
import com.validation.manager.core.db.controller.ExecutionStepJpaController;
import com.validation.manager.core.server.core.VMSettingServer;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import net.sourceforge.javydreamercsw.validation.manager.web.tester.ExecutionScreen;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = IMainContentProvider.class, position = 2)
public class QualityScreenProvider extends ExecutionScreen {

    @Override
    public String getComponentCaption() {
        return "quality.tab.name";
    }

    @Override
    public boolean shouldDisplay() {
        VmSetting setting = VMSettingServer.getSetting("quality.review");
        return ValidationManagerUI.getInstance().getUser() != null
                && ValidationManagerUI.getInstance()
                        .checkRight("quality.assurance")
                && setting != null
                && setting.getBoolVal();
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
            ExecutionStepJpaController c
                    = new ExecutionStepJpaController(DataBaseManager
                            .getEntityManagerFactory());
            for (ExecutionStep es : c.findExecutionStepEntities()) {
                if (es.getLocked() && !es.getReviewed()) {
                    //It has been assigned but not started
                    Notification.show(Lookup.getDefault().lookup(InternationalizationProvider.class)
                            .translate("quality.review.pending.title"),
                            Lookup.getDefault().lookup(InternationalizationProvider.class)
                                    .translate("quality.review.pending.message"),
                            Notification.Type.TRAY_NOTIFICATION);
                    break;
                }
            }
        }
    }
}
