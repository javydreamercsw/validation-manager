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

import com.vaadin.data.util.converter.Converter;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import com.validation.manager.core.db.UserStatus;
import com.validation.manager.core.db.controller.UserStatusJpaController;
import java.util.Locale;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class UserStatusConverter implements Converter<String, UserStatus> {

    private static final InternationalizationProvider TRANSLATOR
            = Lookup.getDefault().lookup(InternationalizationProvider.class);

    @Override
    public UserStatus convertToModel(String value,
            Class<? extends UserStatus> targetType,
            Locale locale) throws Converter.ConversionException {
        UserStatus status = null;
        for (UserStatus us
                : new UserStatusJpaController(DataBaseManager
                        .getEntityManagerFactory()).findUserStatusEntities()) {
            if (TRANSLATOR.translate(us.getStatus()).equals(value)) {
                status = us;
                break;
            }
        }
        return status;
    }

    @Override
    public String convertToPresentation(UserStatus value,
            Class<? extends String> targetType,
            Locale locale) throws Converter.ConversionException {
        return value == null ? "" : TRANSLATOR.translate(value.getStatus());
    }

    @Override
    public Class<UserStatus> getModelType() {
        return UserStatus.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
