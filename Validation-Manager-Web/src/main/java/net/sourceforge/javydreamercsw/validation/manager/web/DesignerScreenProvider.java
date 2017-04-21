package net.sourceforge.javydreamercsw.validation.manager.web;

import com.vaadin.ui.Component;
import com.validation.manager.core.AbstractProvider;
import com.validation.manager.core.IMainContentProvider;
import com.validation.manager.core.db.Project;
import net.sourceforge.javydreamercsw.validation.manager.web.wizard.plan.DetailStep;
import net.sourceforge.javydreamercsw.validation.manager.web.wizard.plan.SelectTestCasesStep;
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
        w.addStep(new DetailStep(getUI()));
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
                getUI().updateScreen();
            }

            @Override
            public void wizardCancelled(WizardCancelledEvent event) {
                p = null;
                getUI().updateScreen();
            }
        });
        return w;
    }

    @Override
    public String getId() {
        return getComponentCaption();
    }

    @Override
    public String getComponentCaption() {
        return "designer.tab.name";
    }

    @Override
    public boolean shouldDisplay() {
        return getUI().getUser() != null
                && getUI().checkRight("testplan.planning") && p != null;
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
