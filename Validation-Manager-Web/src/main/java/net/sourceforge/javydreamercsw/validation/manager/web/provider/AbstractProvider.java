package net.sourceforge.javydreamercsw.validation.manager.web.provider;

import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.validation.manager.core.IMainContentProvider;
import com.validation.manager.core.VMUI;
import com.validation.manager.core.VaadinUtils;
import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.server.core.TestCaseExecutionServer;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import org.openide.util.Lookup;

public abstract class AbstractProvider implements IMainContentProvider {

    @Override
    public void update() {
        Component c = getContent();
        if (c instanceof HasComponents) {
            VMUI instance = ValidationManagerUI.getInstance();
            VaadinUtils.updateLocale((HasComponents) c, instance.getLocale(),
                    Lookup.getDefault().lookup(InternationalizationProvider.class)
                            .getResourceBundle());
        }
    }

    @Override
    public String getId() {
        return getComponentCaption();
    }

    protected boolean isLocked(TestCaseExecutionServer tce) {
        return isLocked(tce, -1);
    }

    protected boolean isLocked(TestCaseExecutionServer tce, int tcID) {
        boolean locked = true;
        //Check to see if the execution is locked or not
        if (tce != null) {
            for (ExecutionStep e : tce.getExecutionStepList()) {
                if ((tcID > 0
                        && e.getExecutionStepPK().getStepTestCaseId() == tcID)
                        && !e.getLocked()) {
                    locked = false;
                    break;
                } else if (tcID < 0 && !e.getLocked()) {
                    locked = false;
                    break;
                }
            }
        }
        return locked;
    }

    @Override
    public void processNotification() {
        //Nothing by default
    }
}
