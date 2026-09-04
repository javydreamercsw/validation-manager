/*
 * Copyright 2026 Javier A. Ortiz Bultron javier.ortiz.78@gmail.com.
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
package net.sourceforge.javydreamercsw.validation.manager.web.component.wizard;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardCancelledEvent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardCompletedEvent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardProgressListener;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardStepActivationEvent;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event.FlowWizardStepSetChangedEvent;

/**
 * Minimal Vaadin Flow replacement for the Teemu Wizards add-on (no Flow port
 * exists). Keeps the v8 {@code WizardStep} contract — getCaption/getContent/
 * onAdvance/onBack — plus the {@code WizardProgressListener} event model, so
 * ported step classes and listeners keep their shape.
 */
public class FlowWizard extends VerticalLayout {

    private static final long serialVersionUID = 1L;

    private final List<FlowWizardStep> steps = new ArrayList<>();
    private final HorizontalLayout buttonBar = new HorizontalLayout();
    private final Span stepHeader = new Span();
    private final VerticalLayout contentArea = new VerticalLayout();
    private int current = 0;
    private boolean completed = false;
    private final List<FlowWizardProgressListener> listeners = new ArrayList<>();
    private final Button nextButton;
    private final Button backButton;
    private final Button finishButton;
    private final Button cancelButton;

    public FlowWizard() {
        setSpacing(false);
        setPadding(false);
        add(stepHeader, contentArea, buttonBar);
        backButton = new Button("Back", e -> back());
        nextButton = new Button("Next", e -> next());
        finishButton = new Button("Finish", e -> finish());
        cancelButton = new Button("Cancel", e -> cancel());
        buttonBar.add(backButton, nextButton, finishButton, cancelButton);
        if (!steps.isEmpty()) {
            showStep(0);
        }
        updateButtons();
    }

    /**
     * Append a step.
     *
     * @param step step to append
     */
    public void addStep(FlowWizardStep step) {
        steps.add(step);
        if (steps.size() == 1) {
            showStep(0);
        }
        updateButtons();
        listeners.forEach(l -> l.stepSetChanged(
                new FlowWizardStepSetChangedEvent(this, steps)));
    }

    /**
     * Register a progress listener.
     *
     * @param listener listener to add
     * @return registration (no-op removal; wizard lifetime == dialog lifetime)
     */
    public Registration addListener(FlowWizardProgressListener listener) {
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }

    /**
     * Advance to the next step if the current one allows it. Advancing past
     * the last step completes the wizard (the Teemu add-on's next() behavior
     * on the final step).
     */
    public void next() {
        if (steps.get(current).onAdvance()) {
            if (current < steps.size() - 1) {
                showStep(current + 1);
            } else {
                finish();
            }
        }
    }

    /**
     * Go back one step if the current one allows it.
     */
    public void back() {
        if (current > 0 && steps.get(current).onBack()) {
            showStep(current - 1);
        }
    }

    /**
     * Complete the wizard (fires wizardCompleted).
     */
    public void finish() {
        if (isLastStep()) {
            completed = true;
            listeners.forEach(l -> l.wizardCompleted(
                    new FlowWizardCompletedEvent(this)));
        }
    }

    /**
     * Cancel the wizard (fires wizardCancelled).
     */
    public void cancel() {
        listeners.forEach(l -> l.wizardCancelled(
                new FlowWizardCancelledEvent(this)));
    }

    public boolean isLastStep() {
        return current == steps.size() - 1;
    }

    /**
     * @return the (unmodifiable) list of registered steps
     */
    public List<FlowWizardStep> getSteps() {
        return java.util.Collections.unmodifiableList(steps);
    }

    public FlowWizardStep getCurrentStep() {
        return steps.isEmpty() ? null : steps.get(current);
    }

    public int getStepCount() {
        return steps.size();
    }

    /**
     * @return whether finish() has completed the wizard
     */
    public boolean isCompleted() {
        return completed;
    }

    private void showStep(int index) {
        current = index;
        FlowWizardStep step = steps.get(index);
        contentArea.removeAll();
        Component content = step.getContent();
        contentArea.add(content);
        stepHeader.setText((index + 1) + "/" + steps.size() + " — "
                + step.getCaption());
        listeners.forEach(l -> l.activeStepChanged(
                new FlowWizardStepActivationEvent(this, step)));
        updateButtons();
    }

    private void updateButtons() {
        backButton.setEnabled(current > 0);
        nextButton.setEnabled(current < steps.size() - 1);
        finishButton.setEnabled(current == steps.size() - 1);
    }
}
