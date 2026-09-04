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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import net.sourceforge.javydreamercsw.validation.manager.web.core.IMainContentProvider;
import net.sourceforge.javydreamercsw.validation.manager.web.core.AvatarProvider;
import com.validation.manager.core.db.Activity;
import com.validation.manager.core.server.core.ActivityServer;
import com.validation.manager.core.server.core.VMUserServer;
import java.util.List;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import net.sourceforge.javydreamercsw.validation.manager.web.provider.AbstractProvider;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

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
            //Add activity stream
            List<Activity> activities = ActivityServer.getActivities();
            VerticalLayout layout = new VerticalLayout();
            Grid<Activity> grid = new Grid<>();
            grid.setItems(activities);
            grid.addColumn(new ComponentRenderer<>(activity -> {
                Image image = new Image("/VAADIN/themes/vmtheme/VMSmall.png",
                        "");
                AvatarProvider ap = Lookup.getDefault()
                        .lookup(AvatarProvider.class);
                String icon = ap == null ? null
                        : ap.getAvatar(activity.getSourceUser(), 30);
                if (icon != null) {
                    image = new Image(icon, "");
                }
                image.setWidth("30px");
                image.setHeight("30px");
                return image;
            }))
                    .setKey("avatar")
                    .setHeader("");
            grid.addColumn(a -> {
                try {
                    return new VMUserServer(a.getSourceUser().getId())
                            .toString();
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
                return "";
            })
                    .setKey("sourceUser")
                    .setHeader(TRANSLATOR.translate("general.user"));
            grid.addColumn(a -> a.getActivityType() == null ? ""
                    : TRANSLATOR.translate(a.getActivityType().getTypeName()))
                    .setKey("activityType")
                    .setHeader(TRANSLATOR.translate("general.type"));
            grid.addColumn(Activity::getDescription)
                    .setKey("description")
                    .setHeader(TRANSLATOR.translate("general.description"));
            grid.addColumn(Activity::getActivityTime)
                    .setKey("activityTime")
                    .setHeader(TRANSLATOR.translate("general.time"));
            grid.sort(GridSortOrder.desc(
                    grid.getColumnByKey("activityTime")).build());
            layout.add(grid);
            layout.setId(getComponentCaption());
            layout.setSizeFull();
            grid.setSizeFull();
            return layout;
        } catch (IllegalArgumentException | IllegalStateException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
}
