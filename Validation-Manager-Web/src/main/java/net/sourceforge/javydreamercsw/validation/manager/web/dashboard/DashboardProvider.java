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

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.renderers.ImageRenderer;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.IMainContentProvider;
import com.validation.manager.core.api.image.AvatarProvider;
import com.validation.manager.core.db.Activity;
import com.validation.manager.core.db.ActivityType;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.ActivityTypeJpaController;
import com.validation.manager.core.db.controller.VmUserJpaController;
import com.validation.manager.core.server.core.ActivityServer;
import com.validation.manager.core.server.core.VMUserServer;
import java.util.List;
import java.util.Locale;
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
            BeanItemContainer<Activity> container
                    = new BeanItemContainer<>(Activity.class, activities);
            GeneratedPropertyContainer gpc
                    = new GeneratedPropertyContainer(container);
            gpc.addGeneratedProperty("avatar",
                    new PropertyValueGenerator<Resource>() {

                @Override
                public Resource getValue(Item item, Object itemId,
                        Object propertyId) {
                    VmUser user = ((Activity) itemId).getSourceUser();
                    Resource image = new ThemeResource("VMSmall.png");
                    AvatarProvider ap = Lookup.getDefault()
                            .lookup(AvatarProvider.class);
                    Resource icon = ap == null ? null
                            : ap.getAvatar(user, 30);
                    if (icon != null) {
                        image = icon;
                    }
                    return image;
                }

                @Override
                public Class<Resource> getType() {
                    return Resource.class;
                }
            });
            Grid grid = new Grid(TRANSLATOR.translate("general.activity.stream"),
                    gpc);
            Column at = grid.getColumn("activityType");
            at.setHeaderCaption(TRANSLATOR.translate("activity.type"));
            at.setConverter(new Converter<String, ActivityType>() {
                int type;

                @Override
                public ActivityType convertToModel(String value,
                        Class<? extends ActivityType> targetType,
                        Locale locale) throws Converter.ConversionException {
                    return new ActivityTypeJpaController(DataBaseManager
                            .getEntityManagerFactory()).findActivityType(type);
                }

                @Override
                public String convertToPresentation(ActivityType value,
                        Class<? extends String> targetType, Locale locale)
                        throws Converter.ConversionException {
                    type = value.getId();
                    return TRANSLATOR.translate(value.getTypeName());
                }

                @Override
                public Class<ActivityType> getModelType() {
                    return ActivityType.class;
                }

                @Override
                public Class<String> getPresentationType() {
                    return String.class;
                }
            });
            Column type = grid.getColumn("activityType");
            type.setHeaderCaption(TRANSLATOR.translate("general.type"));
            Column desc = grid.getColumn("description");
            desc.setHeaderCaption(TRANSLATOR.translate("general.description"));
            Column user = grid.getColumn("sourceUser");
            user.setHeaderCaption(TRANSLATOR.translate("general.user"));
            user.setConverter(new Converter<String, VmUser>() {
                private int user;

                @Override
                public String convertToPresentation(VmUser value,
                        Class<? extends String> targetType, Locale l)
                        throws Converter.ConversionException {
                    try {
                        user = value.getId();
                        return new VMUserServer(user).toString();
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    return "";
                }

                @Override
                public Class<VmUser> getModelType() {
                    return VmUser.class;
                }

                @Override
                public Class<String> getPresentationType() {
                    return String.class;
                }

                @Override
                public VmUser convertToModel(String value,
                        Class<? extends VmUser> targetType, Locale locale)
                        throws Converter.ConversionException {
                    return new VmUserJpaController(DataBaseManager
                            .getEntityManagerFactory()).findVmUser(user);
                }
            });
            Column avatar = grid.getColumn("avatar");
            avatar.setHeaderCaption("");
            avatar.setRenderer(new ImageRenderer());
            Column time = grid.getColumn("activityTime");
            time.setHeaderCaption(TRANSLATOR.translate("general.time"));
            grid.setColumns("avatar", "sourceUser", "activityType",
                    "description", "activityTime");
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
