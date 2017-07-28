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
package net.sourceforge.javydreamercsw.validation.manager.web.wizard.project;

import com.vaadin.ui.UI;
import com.validation.manager.core.VMUI;
import com.validation.manager.core.server.core.ProjectServer;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public abstract class ProjectTemplateManager implements Runnable {

    private ProjectServer ps;

    /**
     * @return the p
     */
    public ProjectServer getProject() {
        return ps;
    }

    /**
     * @param p the p to set
     */
    public void setProject(ProjectServer p) {
        this.ps = p;
    }

    public void updateComplete() {
        if (UI.getCurrent() != null) {//For unit tests
            //Display window to set details like name, etc.
            ((VMUI) UI.getCurrent()).updateProjectList();
            ((VMUI) UI.getCurrent()).buildProjectTree(ps.getEntity());
            ((VMUI) UI.getCurrent()).displayObject(ps.getEntity(), false);
            ((VMUI) UI.getCurrent()).updateScreen();
        }
    }
}
