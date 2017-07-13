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

import com.vaadin.ui.Window;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class VMWindow extends Window {

    protected final ValidationManagerUI menu;

    public VMWindow() {
        super();
        menu = null;
        init();
    }

    public VMWindow(String caption) {
        super(caption);
        menu = null;
        init();
    }

    public VMWindow(ValidationManagerUI menu, String caption) {
        super(caption);
        this.menu = menu;
        init();
    }

    public VMWindow(ValidationManagerUI menu) {
        super();
        this.menu = menu;
        init();
    }

    private void init() {
        setIcon(ValidationManagerUI.SMALL_APP_ICON);
        setModal(true);
        center();
    }
}
