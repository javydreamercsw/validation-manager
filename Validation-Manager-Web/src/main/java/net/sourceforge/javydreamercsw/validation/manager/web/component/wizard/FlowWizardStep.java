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

/**
 * Flow replacement for the Teemu {@code WizardStep} contract. Method names
 * match the v8 add-on so step classes port unchanged.
 */
public interface FlowWizardStep {

    /**
     * @return the step caption
     */
    String getCaption();

    /**
     * @return the step content component
     */
    Component getContent();

    /**
     * @return whether advancing to the next step is allowed
     */
    boolean onAdvance();

    /**
     * @return whether going back is allowed
     */
    boolean onBack();
}
