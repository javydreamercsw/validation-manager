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

import com.vaadin.data.Container;
import com.vaadin.data.util.HierarchicalContainer;

public class ProjectTreeComponentBuilder {

    private String caption;
    private boolean showProject = true;
    private boolean showRequirement = true;
    private boolean showTestCase = true;
    private boolean showExecution = true;
    private Container dataSource = new HierarchicalContainer();

    public ProjectTreeComponentBuilder() {
    }

    public ProjectTreeComponentBuilder setCaption(String caption) {
        this.caption = caption;
        return this;
    }

    public ProjectTreeComponentBuilder setShowExecution(boolean showExecution) {
        this.showExecution = showExecution;
        return this;
    }

    public ProjectTreeComponentBuilder setShowProject(boolean showProject) {
        this.showProject = showProject;
        return this;
    }

    public ProjectTreeComponentBuilder setShowRequirement(boolean showRequirement) {
        this.showRequirement = showRequirement;
        return this;
    }

    public ProjectTreeComponentBuilder setShowTestCase(boolean showTestCase) {
        this.showTestCase = showTestCase;
        return this;
    }

    public ProjectTreeComponentBuilder setDataSource(Container dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public ProjectTreeComponent createProjectTreeComponent() {
        return new ProjectTreeComponent(caption, dataSource,
                showProject, showRequirement, showTestCase, showExecution);
    }
}
