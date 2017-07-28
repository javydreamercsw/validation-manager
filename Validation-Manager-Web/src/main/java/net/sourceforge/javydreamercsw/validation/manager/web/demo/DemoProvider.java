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
package net.sourceforge.javydreamercsw.validation.manager.web.demo;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.IMainContentProvider;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.controller.VmUserJpaController;
import com.validation.manager.core.tool.MD5;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.provider.AbstractProvider;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = IMainContentProvider.class)
public class DemoProvider extends AbstractProvider {

    private static final Logger LOG
            = Logger.getLogger(DemoProvider.class.getSimpleName());
    private VerticalLayout layout;

    @Override
    public Component getContent() {
        layout = new VerticalLayout();
        update();
        layout.setId(getComponentCaption());
        return layout;
    }

    @Override
    public void update() {
        layout.removeAllComponents();
        VmUserJpaController controller
                = new VmUserJpaController(DataBaseManager
                        .getEntityManagerFactory());
        layout.addComponent(new Label("<h1>"
                + TRANSLATOR.translate("demo.tab.title") + "</h1>",
                ContentMode.HTML));
        Label l = new Label();
        l.setId("demo.tab.message");
        layout.addComponent(l);
        StringBuilder sb = new StringBuilder("<ul>");
        controller.findVmUserEntities().stream().filter((u)
                -> (u.getId() < 1_000)).forEachOrdered((u) -> {
            try {
                //Default accounts
                if (u.getPassword() != null
                        && u.getPassword().equals(MD5
                                .encrypt(u.getUsername()))) {
                    sb.append("<li><b>")
                            .append(TRANSLATOR.translate("general.username"))
                            .append(":</b> ")
                            .append(u.getUsername())
                            .append(", <b>")
                            .append(TRANSLATOR.translate("general.password"))
                            .append(":</b> ")
                            .append(u.getUsername())
                            .append(" <b>")
                            .append(TRANSLATOR.translate("general.role"))
                            .append(":</b> ")
                            .append(TRANSLATOR
                                    .translate(u.getRoleList().get(0)
                                            .getDescription()))
                            .append("</li>");
                }
            } catch (VMException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        });
        sb.append("</ul>");
        layout.addComponent(new Label(sb.toString(),
                ContentMode.HTML));
        layout.setId(getComponentCaption());
    }

    @Override
    public String getComponentCaption() {
        return "demo.tab.name";
    }

    @Override
    public boolean shouldDisplay() {
        return DataBaseManager.isDemo();
    }
}
