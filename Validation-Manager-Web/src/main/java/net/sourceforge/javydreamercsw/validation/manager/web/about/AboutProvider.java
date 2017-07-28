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

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Component;
import com.vaadin.ui.Image;
import com.vaadin.ui.Link;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.validation.manager.core.IMainContentProvider;
import static com.validation.manager.core.VMUI.LOGO;
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
        vl.addComponent(new Image("", LOGO));
        TextField version = new TextField(TRANSLATOR.translate("general.version"));
        version.setValue(((ValidationManagerUI) UI.getCurrent()).getVersion());
        version.setReadOnly(true);
        vl.addComponent(version);
        TextField build = new TextField(TRANSLATOR.translate("general.build"));
        build.setValue(((ValidationManagerUI) UI.getCurrent()).getBuild());
        build.setReadOnly(true);
        vl.addComponent(build);
        TextArea desc = new TextArea();
        desc.setValue("Validation Manager is a tool to handle all the "
                + "cumbersome paperwork of regulated environment validations. "
                + "Including Validation Plans, protocols, "
                + "executions and exceptions. Keeping everything in one "
                + "place and best of all paperless. ");
        desc.setReadOnly(true);
        desc.setWidth(100, Unit.PERCENTAGE);
        Link link = new Link("Get more information here",
                new ExternalResource("https://github.com/javydreamercsw/validation-manager"));
        vl.addComponent(desc);
        vl.addComponent(link);
        vl.setId(getComponentCaption());
        return vl;
    }
}
