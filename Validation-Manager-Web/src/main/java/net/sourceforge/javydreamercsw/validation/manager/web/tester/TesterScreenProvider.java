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
package net.sourceforge.javydreamercsw.validation.manager.web.tester;

import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.validation.manager.core.IMainContentProvider;
import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import com.validation.manager.core.db.ExecutionStep;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = IMainContentProvider.class, position = 1)
public class TesterScreenProvider extends ExecutionScreen {

    @Override
    public String getComponentCaption() {
        return "tester.tab.name";
    }

    @Override
    public boolean shouldDisplay() {
        return ValidationManagerUI.getInstance().getUser() != null
                && !ValidationManagerUI.getInstance().getUser()
                        .getExecutionStepList().isEmpty()
                && ValidationManagerUI.getInstance().checkRight("testplan.execute");
    }

    @Override
    public Component getContent() {
        Component content = super.getContent();
        content.setId(getComponentCaption());
        return content;
    }

    @Override
    public void processNotification() {
        if (shouldDisplay()) {
            for (ExecutionStep es : ValidationManagerUI.getInstance().getUser()
                    .getExecutionStepList()) {
                if (es.getExecutionStart() == null) {
                    //It has been assigned but not started
                    Notification.show(Lookup.getDefault().lookup(InternationalizationProvider.class)
                            .translate("test.pending.title"),
                            Lookup.getDefault().lookup(InternationalizationProvider.class)
                                    .translate("test.pending.message"),
                            Notification.Type.TRAY_NOTIFICATION);
                    break;
                }
            }
        }
    }
}
