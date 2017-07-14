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
package net.sourceforge.javydreamercsw.validation.manager.web.template;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalSplitPanel;
import com.validation.manager.core.IMainContentProvider;
import net.sourceforge.javydreamercsw.validation.manager.web.admin.AdminProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = IMainContentProvider.class, position = 6)
public class TemplateScreenProvider extends AdminProvider {

    @Override
    public String getComponentCaption() {
        return "template.tab.name";
    }

    @Override
    public Component getContent() {
        VerticalSplitPanel vs = new VerticalSplitPanel();

        return vs;
    }
}
