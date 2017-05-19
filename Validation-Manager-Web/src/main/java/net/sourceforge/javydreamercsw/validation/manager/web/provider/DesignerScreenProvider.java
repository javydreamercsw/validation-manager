package net.sourceforge.javydreamercsw.validation.manager.web.provider;

import com.vaadin.ui.Component;
import com.validation.manager.core.AbstractProvider;
import com.validation.manager.core.IMainContentProvider;
import com.validation.manager.core.VMUI;
import com.validation.manager.core.db.Project;
import net.sourceforge.javydreamercsw.validation.manager.web.wizard.plan.DetailStep;
import net.sourceforge.javydreamercsw.validation.manager.web.wizard.plan.SelectTestCasesStep;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

@ServiceProvider(service = IMainContentProvider.class, position = 4)
public class DesignerScreenProvider extends AbstractProvider {

    private Project p;

    @Override
    public Component getContent() {
        Wizard w = new Wizard();
        w.addStep(new SelectTestCasesStep(w, p));
        w.addStep(new DetailStep());
        w.addListener(new WizardProgressListener() {
            @Override
            public void activeStepChanged(WizardStepActivationEvent event) {
                //Do nothing
            }

            @Override
            public void stepSetChanged(WizardStepSetChangedEvent event) {
                //Do nothing
            }

            @Override
            public void wizardCompleted(WizardCompletedEvent event) {
                p = null;
                Lookup.getDefault().lookup(VMUI.class).updateScreen();
            }

            @Override
            public void wizardCancelled(WizardCancelledEvent event) {
                p = null;
                Lookup.getDefault().lookup(VMUI.class).updateScreen();
            }
        });
        return w;
    }

    @Override
    public String getComponentCaption() {
        return "designer.tab.name";
    }

    @Override
    public boolean shouldDisplay() {
        return Lookup.getDefault().lookup(VMUI.class).getUser() != null
                && Lookup.getDefault().lookup(VMUI.class)
                        .checkRight("testplan.planning") && p != null;
    }

    /**
     * Set the project to display design screen
     *
     * @param p
     */
    public void setProject(Project p) {
        this.p = p;
    }
}
