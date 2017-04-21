package net.sourceforge.javydreamercsw.validation.manager.web;

import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.validation.manager.core.AbstractProvider;
import com.validation.manager.core.IMainContentProvider;
import net.sourceforge.javydreamercsw.validation.manager.web.tester.TesterScreen;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = IMainContentProvider.class, position = 2)
public class TesterScreenProvider extends AbstractProvider {

    private TesterScreen ts;

    @Override
    public Component getContent() {
        if (ts == null) {
            ts = new TesterScreen(getUI());
            ts.setId(getComponentCaption());
        } else if (ts != null) {
            return ts;
        }
        return ts == null ? new Panel() : ts;
    }

    @Override
    public String getId() {
        return getComponentCaption();
    }

    @Override
    public String getComponentCaption() {
        return "tester.tab.name";
    }

    @Override
    public void update() {
        if (ts != null) {
            ts.update();
        }
    }

    @Override
    public boolean shouldDisplay() {
        return getUI().getUser() != null
                && !getUI().getUser().getExecutionStepList().isEmpty()
                && getUI().checkRight("testplan.execute");
    }
}
