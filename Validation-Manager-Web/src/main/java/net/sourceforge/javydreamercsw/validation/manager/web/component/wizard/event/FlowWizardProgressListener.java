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

import java.io.Serializable;

/**
 * Flow replacement for the Teemu {@code WizardProgressListener} contract.
 */
public interface FlowWizardProgressListener extends Serializable {

    void activeStepChanged(FlowWizardStepActivationEvent event);

    void stepSetChanged(FlowWizardStepSetChangedEvent event);

    void stepCompleted(FlowWizardStepCompletionEvent event);

    void wizardCompleted(FlowWizardCompletedEvent event);

    void wizardCancelled(FlowWizardCancelledEvent event);
}
