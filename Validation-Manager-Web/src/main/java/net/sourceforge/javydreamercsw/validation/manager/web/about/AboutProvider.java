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
package net.sourceforge.javydreamercsw.validation.manager.web.about;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import net.sourceforge.javydreamercsw.validation.manager.web.core.IMainContentProvider;
import static net.sourceforge.javydreamercsw.validation.manager.web.core.VMUI.LOGO;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import net.sourceforge.javydreamercsw.validation.manager.web.provider.AbstractProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = IMainContentProvider.class, position = 100)
public class AboutProvider extends AbstractProvider {

    @Override
    public boolean shouldDisplay() {
        return true;
    }

    @Override
    public String getComponentCaption() {
        return "general.about";
    }

    @Override
    public Component getContent() {
        VerticalLayout vl = new VerticalLayout();
        vl.add(new Icon(LOGO));
        TextField version = new TextField(TRANSLATOR.translate("general.version"));
        version.setValue(((ValidationManagerUI) UI.getCurrent()).getVersion());
        version.setReadOnly(true);
        vl.add(version);
        TextField build = new TextField(TRANSLATOR.translate("general.build"));
        build.setValue(((ValidationManagerUI) UI.getCurrent()).getBuild());
        build.setReadOnly(true);
        vl.add(build);
        TextArea desc = new TextArea();
        desc.setValue("Validation Manager is a tool to handle all the "
                + "cumbersome paperwork of regulated environment validations. "
                + "Including Validation Plans, protocols, "
                + "executions and exceptions. Keeping everything in one "
                + "place and best of all paperless. ");
        desc.setReadOnly(true);
        desc.setWidth("100%");
        Anchor link = new Anchor(
                "https://github.com/javydreamercsw/validation-manager",
                "Get more information here");
        vl.add(desc);
        vl.add(link);
        vl.setId(getComponentCaption());
        return vl;
    }
}
