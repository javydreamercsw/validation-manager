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
package net.sourceforge.javydreamercsw.validation.manager.web.dashboard;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.ImageRenderer;
import com.validation.manager.core.IMainContentProvider;
import com.validation.manager.core.api.image.AvatarProvider;
import com.validation.manager.core.db.Activity;
import com.validation.manager.core.server.core.ActivityServer;
import com.validation.manager.core.server.core.VMUserServer;
import java.util.List;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import net.sourceforge.javydreamercsw.validation.manager.web.provider.AbstractProvider;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.vaadin.addon.borderlayout.BorderLayout;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = IMainContentProvider.class)
public class DashboardProvider extends AbstractProvider {

    @Override
    public boolean shouldDisplay() {
        return ValidationManagerUI.getInstance().getUser() != null;
    }

    @Override
    public String getComponentCaption() {
        return "general.dashboard";
    }

    @Override
    public Component getContent() {
        try {
            BorderLayout bl = new BorderLayout();
            //Add activity stream
            List<Activity> activities = ActivityServer.getActivities();
            Grid<Activity> grid = new Grid<>(
                    TRANSLATOR.translate("general.activity.stream"));
            grid.setItems(activities);
            grid.addColumn(activity -> {
                Resource image = new ThemeResource("VMSmall.png");
                AvatarProvider ap = Lookup.getDefault()
                        .lookup(AvatarProvider.class);
                Resource icon = ap == null ? null
                        : ap.getAvatar(activity.getSourceUser(), 30);
                if (icon != null) {
                    image = icon;
                }
                return image;
            })
                    .setId("avatar")
                    .setCaption("")
                    .setRenderer(new ImageRenderer<>());
            grid.addColumn(a -> {
                try {
                    return new VMUserServer(a.getSourceUser().getId())
                            .toString();
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
                return "";
            })
                    .setId("sourceUser")
                    .setCaption(TRANSLATOR.translate("general.user"));
            grid.addColumn(a -> a.getActivityType() == null ? ""
                    : TRANSLATOR.translate(a.getActivityType().getTypeName()))
                    .setId("activityType")
                    .setCaption(TRANSLATOR.translate("general.type"));
            grid.addColumn(Activity::getDescription)
                    .setId("description")
                    .setCaption(TRANSLATOR.translate("general.description"));
            grid.addColumn(Activity::getActivityTime)
                    .setId("activityTime")
                    .setCaption(TRANSLATOR.translate("general.time"));
            grid.sort("activityTime", SortDirection.DESCENDING);
            bl.addComponent(grid, BorderLayout.Constraint.CENTER);
            bl.setId(getComponentCaption());
            return bl;
        } catch (IllegalArgumentException | IllegalStateException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
}
