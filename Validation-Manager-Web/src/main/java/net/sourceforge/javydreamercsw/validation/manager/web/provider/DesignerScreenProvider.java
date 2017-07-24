/*
 * Copyright 2017 Javier A. Ortiz Bultron javier.ortiz.78@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sourceforge.javydreamercsw.validation.manager.web.provider;

import com.vaadin.ui.Component;
import com.validation.manager.core.IMainContentProvider;
import com.validation.manager.core.db.Project;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import net.sourceforge.javydreamercsw.validation.manager.web.wizard.plan.DetailStep;
import net.sourceforge.javydreamercsw.validation.manager.web.wizard.plan.SelectTestCasesStep;
import org.openide.util.lookup.ServiceProvider;
import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

@ServiceProvider(service = IMainContentProvider.class, position = 3)
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
                ValidationManagerUI.getInstance().updateScreen();
            }

            @Override
            public void wizardCancelled(WizardCancelledEvent event) {
                p = null;
                ValidationManagerUI.getInstance().updateScreen();
            }
        });
        w.setId(getComponentCaption());
        return w;
    }

    @Override
    public String getComponentCaption() {
        return "designer.tab.name";
    }

    @Override
    public boolean shouldDisplay() {
        return ValidationManagerUI.getInstance().getUser() != null
                && ValidationManagerUI.getInstance()
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
