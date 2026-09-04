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
package net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.event;

import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.FlowWizard;
import net.sourceforge.javydreamercsw.validation.manager.web.component.wizard.FlowWizardStep;

/**
 * Fired when the step set changes.
 */
public class FlowWizardStepSetChangedEvent extends FlowWizardEvent {

    private static final long serialVersionUID = 1L;

    private final java.util.List<FlowWizardStep> steps;

    public FlowWizardStepSetChangedEvent(FlowWizard wizard, java.util.List<FlowWizardStep> steps) {
        super(wizard);
        this.steps = steps;
    }

    public java.util.List<FlowWizardStep> getSteps() {
        return steps;
    }
}
