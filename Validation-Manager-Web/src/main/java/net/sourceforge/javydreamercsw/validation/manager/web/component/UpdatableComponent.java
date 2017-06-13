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
package net.sourceforge.javydreamercsw.validation.manager.web.component;

import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public abstract class UpdatableComponent extends Panel {

    public UpdatableComponent() {
    }

    public UpdatableComponent(Component content) {
        super(content);
    }

    public UpdatableComponent(String caption) {
        super(caption);
    }

    public UpdatableComponent(String caption, Component content) {
        super(caption, content);
    }

    /**
     * Update component contents
     */
    abstract void update();
}
